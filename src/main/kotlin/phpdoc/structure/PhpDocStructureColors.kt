package phpdoc.structure

import com.intellij.openapi.editor.colors.TextAttributesKey

val FALLBACK_PHPDOC_STRUCTURE_MEMBER: TextAttributesKey = TextAttributesKey.createTextAttributesKey("FALLBACK_PHPDOC_STRUCTURE_MEMBER")
val PHPDOC_STRUCTURE_MEMBER: TextAttributesKey = TextAttributesKey.createTextAttributesKey("PHPDOC_STRUCTURE_MEMBER", FALLBACK_PHPDOC_STRUCTURE_MEMBER)

