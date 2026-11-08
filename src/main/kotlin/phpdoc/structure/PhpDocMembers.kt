package phpdoc.structure

import com.intellij.concurrency.ConcurrentCollectionFactory
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocMethod
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocMethodTag
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocProperty
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocPropertyTag
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocTag
import com.jetbrains.php.lang.psi.elements.PhpNamedElement

private val phpDocMemberCache = ConcurrentCollectionFactory.createConcurrentWeakIdentityMap<PsiElement, Boolean>()

fun isPhpDocMember(value: Any?): Boolean {
    val element = value as? PsiElement ?: return false
    val member = detectPhpDocMember(element)
    phpDocMemberCache[element] = member
    return member
}

fun cachedPhpDocMember(value: Any?): Boolean? {
    val element = value as? PsiElement ?: return false
    return phpDocMemberCache[element]
}

private fun detectPhpDocMember(element: PsiElement): Boolean {
    if (element is PhpDocMethod || element is PhpDocProperty) {
        return true
    }

    val navigation = element.navigationElement
    if (
        navigation is PhpDocMethod
        || navigation is PhpDocProperty
        || navigation is PhpDocMethodTag
        || navigation is PhpDocPropertyTag
    ) {
        return true
    }

    val nameIdentifier = (element as? PhpNamedElement)?.nameIdentifier
    return sequenceOf(element, navigation, nameIdentifier).any { candidate ->
        val tag = candidate?.let { PsiTreeUtil.getParentOfType(it, PhpDocTag::class.java, false) }
        tag != null && tag.name in DEFAULT_PHPDOC_MEMBER_TAGS
    }
}
