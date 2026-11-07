package phpdoc.structure

import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.structureView.newStructureView.StructureViewComponent
import com.intellij.ide.util.treeView.NodeRenderer
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.WriteIntentReadAction
import com.intellij.openapi.project.Project
import com.intellij.ui.SimpleTextAttributes
import java.awt.Color
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
        if (!isPhpDocMemberOnUi(value)) {
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

private fun isPhpDocMemberOnUi(node: Any?): Boolean {
    val app = ApplicationManager.getApplication()
    if (app.isReadAccessAllowed) {
        return isPhpDocMember(structureValue(node))
    }
    return WriteIntentReadAction.compute {
        isPhpDocMember(structureValue(node))
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
