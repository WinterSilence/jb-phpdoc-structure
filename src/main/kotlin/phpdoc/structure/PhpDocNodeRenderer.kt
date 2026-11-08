package phpdoc.structure

import com.intellij.concurrency.ConcurrentCollectionFactory
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.structureView.newStructureView.StructureViewComponent
import com.intellij.ide.util.treeView.NodeRenderer
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.ui.SimpleTextAttributes
import com.intellij.util.concurrency.AppExecutorUtil
import java.awt.Color
import java.util.Collections
import java.util.concurrent.Callable
import javax.swing.JTree
import javax.swing.SwingUtilities

class PhpDocNodeRenderer : NodeRenderer() {
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
}

private val pendingPhpDocMembers: MutableSet<PsiElement> = Collections.newSetFromMap(
    ConcurrentCollectionFactory.createConcurrentWeakIdentityMap(),
)

private fun isPhpDocMemberOnUi(tree: JTree, node: Any?): Boolean {
    val value = structureValue(node)
    if (ApplicationManager.getApplication().isReadAccessAllowed) {
        return isPhpDocMember(value)
    }
    val cached = cachedPhpDocMember(value)
    if (cached != null) {
        return cached
    }
    schedulePhpDocMemberResolve(tree, value)
    return false
}

private fun schedulePhpDocMemberResolve(tree: JTree, value: Any?) {
    val element = value as? PsiElement ?: return
    if (!pendingPhpDocMembers.add(element)) {
        return
    }
    val project = structureProject(tree)
    try {
        ReadAction.nonBlocking(Callable { isPhpDocMember(element) })
            .expireWhen { project?.isDisposed == true }
            .submit(AppExecutorUtil.getAppExecutorService())
            .onProcessed {
                pendingPhpDocMembers.remove(element)
                SwingUtilities.invokeLater { tree.repaint() }
            }
    } catch (error: Throwable) {
        pendingPhpDocMembers.remove(element)
        throw error
    }
}

private fun structureValue(value: Any?): Any? {
    val unwrapped = StructureViewComponent.unwrapValue(value)
    return (unwrapped as? StructureViewTreeElement)?.value ?: unwrapped
}

private fun memberColor(tree: JTree): Color {
    val project = structureProject(tree) ?: return DEFAULT_PHPDOC_MEMBER_COLOR
    if (project.isDisposed) {
        return DEFAULT_PHPDOC_MEMBER_COLOR
    }
    return PhpDocColorSettings.getInstance(project).color
}

private fun structureProject(tree: JTree): Project? {
    val view = SwingUtilities.getAncestorOfClass(StructureViewComponent::class.java, tree) as? StructureViewComponent
    return view?.project
}
