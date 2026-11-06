package phpdoc.structure

import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.ActionPresentation
import com.intellij.ide.util.treeView.smartTree.ActionPresentationData
import com.intellij.ide.util.treeView.smartTree.Filter
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.jetbrains.php.PhpIcons

/**
 * Пункт Show в View Options. Снят — члены из PhpDoc скрыты.
 * [isReverted] включает его по умолчанию, как «Show Properties».
 */
object PhpDocFilter : Filter {
    const val NAME = "Show PhpDoc members"

    override fun isVisible(treeNode: TreeElement): Boolean {
        return !isPhpDocMember((treeNode as? StructureViewTreeElement)?.value)
    }

    override fun isReverted(): Boolean = true

    override fun getPresentation(): ActionPresentation = ActionPresentationData(NAME, null, PhpIcons.FIELD)

    override fun getName(): String = NAME
}
