package phpdoc.structure

import com.intellij.concurrency.ConcurrentCollectionFactory
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.structureView.newStructureView.StructureViewComponent
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.ide.util.treeView.NodeRenderer
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.project.Project
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.treeStructure.filtered.FilteringTreeStructure
import com.intellij.util.concurrency.AppExecutorUtil
import com.intellij.util.ui.tree.TreeUtil
import java.awt.Color
import java.util.*
import java.util.concurrent.Callable
import javax.swing.JTree
import javax.swing.SwingUtilities

class PhpDocNodeRenderer : NodeRenderer()
{
    override fun customizeCellRenderer(
        tree: JTree,
        value: Any?,
        selected: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int,
        hasFocus: Boolean,
    ) {
        super.customizeCellRenderer(tree, value, selected, expanded, leaf, row, hasFocus)
        if (!isPhpDocMemberOnUi(tree, value)) {
            return
        }
        val color = memberColor(tree)
        val fragments = iterator()
        while (fragments.hasNext()) {
            fragments.next()
            val attributes = fragments.textAttributes
            fragments.textAttributes = SimpleTextAttributes(attributes.style, color)
        }
    }

    private fun isPhpDocMemberOnUi(tree: JTree, node: Any?): Boolean
    {
        val element = structureTreeElement(node) ?: return false
        val cached = treeElementMembers[element]
        if (cached != null) {
            return cached
        }
        schedulePhpDocMemberResolve(tree, element)
        return false
    }

    private fun schedulePhpDocMemberResolve(tree: JTree, element: StructureViewTreeElement)
    {
        if (!pendingTreeElements.add(element)) {
            return
        }
        val project = structureProject(tree)
        try {
            ReadAction.nonBlocking(Callable {
                val member = PhpDocMembers.isPhpDocMember(element.value)
                treeElementMembers[element] = member
                member
            })
                .expireWhen { project?.isDisposed == true }
                .submit(AppExecutorUtil.getAppExecutorService())
                .onProcessed {
                    pendingTreeElements.remove(element)
                    SwingUtilities.invokeLater { tree.repaint() }
                }
        } catch (error: Throwable) {
            pendingTreeElements.remove(element)
            throw error
        }
    }

    private fun structureTreeElement(value: Any?): StructureViewTreeElement?
    {
        var current = TreeUtil.getUserObject(value)
        if (current is FilteringTreeStructure.FilteringNode) {
            current = current.delegate
        }
        if (current is AbstractTreeNode<*>) {
            current = current.value
        }
        return current as? StructureViewTreeElement
    }

    private fun memberColor(tree: JTree): Color
    {
        val project = structureProject(tree) ?: return DEFAULT_PHPDOC_MEMBER_COLOR
        if (project.isDisposed) {
            return DEFAULT_PHPDOC_MEMBER_COLOR
        }
        return PhpDocColorSettings.getInstance(project).color
    }

    private fun structureProject(tree: JTree): Project?
    {
        val view = SwingUtilities.getAncestorOfClass(StructureViewComponent::class.java, tree) as? StructureViewComponent
        return view?.project
    }

    companion object {
        private val treeElementMembers = ConcurrentCollectionFactory.createConcurrentWeakIdentityMap<StructureViewTreeElement, Boolean>()

        private val pendingTreeElements: MutableSet<StructureViewTreeElement> = Collections.newSetFromMap(
            ConcurrentCollectionFactory.createConcurrentWeakIdentityMap(),
        )

        internal fun rememberPhpDocTreeElement(element: StructureViewTreeElement, member: Boolean)
        {
            treeElementMembers[element] = member
        }
    }
}
