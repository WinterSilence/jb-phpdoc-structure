package phpdoc.structure

import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import com.jetbrains.php.structureView.PhpStructureViewTreeElement

class ColoredPhpStructureViewTreeElement(
    private val member: PsiElement,
    inherited: Boolean,
) : PhpStructureViewTreeElement(member, inherited) {
    override fun getPresentation(): ItemPresentation {
        val base = super.getPresentation()
        if (!isPhpDocMember(member)) {
            return base
        }
        return colorPhpDocPresentation(base)
    }

    override fun getChildrenBase(): List<StructureViewTreeElement> {
        return super.getChildrenBase().map { child -> colorizeStructureNode(child) as StructureViewTreeElement }
    }
}

fun colorizeStructureNode(element: TreeElement): TreeElement {
    if (element is ColoredPhpStructureViewTreeElement) {
        return element
    }
    if (element is PhpStructureViewTreeElement) {
        val psi = element.psiElement ?: return element
        return ColoredPhpStructureViewTreeElement(psi, element.isInherited)
    }
    return element
}
