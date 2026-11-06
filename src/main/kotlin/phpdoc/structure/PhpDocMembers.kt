package phpdoc.structure

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.util.treeView.PresentableNodeDescriptor.ColoredFragment
import com.intellij.navigation.ColoredItemPresentation
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.ui.SimpleTextAttributes
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocMethod
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocMethodTag
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocProperty
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocPropertyTag
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocTag
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import java.awt.Color

private val PHPDOC_MEMBER_TAGS = setOf("@property", "@property-read", "@property-write", "@method", "@phpstan-property", "@phpstan-method", "@psalm-property", "@psalm-method")
private val PHPDOC_MEMBER_COLOR = Color(0x20, 0x7D, 0x97)

fun isPhpDocMember(value: Any?): Boolean {
    val element = value as? PsiElement ?: return false
    if (element is PhpDocMethod || element is PhpDocProperty) {
        return true
    }
    val navigation = element.navigationElement
    if (navigation is PhpDocMethod || navigation is PhpDocProperty || navigation is PhpDocMethodTag || navigation is PhpDocPropertyTag) {
        return true
    }
    val nameIdentifier = (element as? PhpNamedElement)?.nameIdentifier
    return sequenceOf(element, navigation, nameIdentifier).any { candidate ->
        val tag = candidate?.let { PsiTreeUtil.getParentOfType(it, PhpDocTag::class.java, false) }
        tag != null && tag.name in PHPDOC_MEMBER_TAGS
    }
}

fun colorPhpDocPresentation(base: ItemPresentation): ItemPresentation {
    val data = PresentationData()
    when (base) {
        is PresentationData -> data.copyFrom(base)
        else -> {
            data.presentableText = base.presentableText
            data.setLocationString(base.locationString)
            data.setIcon(base.getIcon(false))
            if (base is ColoredItemPresentation) {
                data.setAttributesKey(base.textAttributesKey)
            }
        }
    }
    if (data.coloredText.isNotEmpty()) {
        val fragments = data.coloredText.map { fragment ->
            ColoredFragment(
                fragment.text,
                fragment.toolTip,
                SimpleTextAttributes(fragment.attributes.style, PHPDOC_MEMBER_COLOR),
            )
        }
        data.clearText()
        fragments.forEach(data::addText)
    }
    data.setAttributesKey(PHPDOC_STRUCTURE_MEMBER)
    return data
}
