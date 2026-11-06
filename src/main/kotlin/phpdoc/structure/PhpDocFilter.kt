package phpdoc.structure

import com.intellij.ide.util.treeView.smartTree.ActionPresentation
import com.intellij.ide.util.treeView.smartTree.ActionPresentationData
import com.intellij.ide.util.treeView.smartTree.Filter
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.jetbrains.php.PhpIcons
import com.jetbrains.php.structureView.PhpStructureViewTreeElement

/**
 * Пункт Show в View Options. Снят — члены из PhpDoc скрыты.
 * [isReverted] включает его по умолчанию, как «Show Properties».
 */
object PhpDocFilter : Filter {
    const val NAME = "Show PhpDoc members"

    override fun isVisible(treeNode: TreeElement): Boolean {
        val element = treeNode as? PhpStructureViewTreeElement ?: return true
        val psi = element.psiElement ?: return true
        return !isPhpDocMember(psi)
    }

    override fun isReverted(): Boolean = true

    override fun getPresentation(): ActionPresentation = ActionPresentationData(NAME, null, PhpIcons.FIELD)

    override fun getName(): String = NAME
}
