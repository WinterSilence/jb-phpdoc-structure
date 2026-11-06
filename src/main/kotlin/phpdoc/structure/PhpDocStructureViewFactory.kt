package phpdoc.structure

import com.intellij.ide.structureView.StructureViewBuilder
import com.intellij.ide.structureView.StructureViewModel
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder
import com.intellij.lang.LanguageStructureViewBuilder
import com.intellij.lang.PsiStructureViewFactory
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import com.jetbrains.php.structureView.PhpStructureViewBuilderProvider

class PhpDocStructureViewFactory : PsiStructureViewFactory {
    override fun getStructureViewBuilder(psiFile: PsiFile): StructureViewBuilder? {
        val base = delegateBuilder(psiFile) ?: return null
        if (base !is TreeBasedStructureViewBuilder) {
            return base
        }
        return object : TreeBasedStructureViewBuilder() {
            override fun createStructureViewModel(editor: Editor?): StructureViewModel {
                return ColoringStructureViewModel(base.createStructureViewModel(editor))
            }

            override fun isRootNodeShown(): Boolean = base.isRootNodeShown
        }
    }

    private fun delegateBuilder(psiFile: PsiFile): StructureViewBuilder? {
        val next = LanguageStructureViewBuilder.getInstance().allForLanguage(psiFile.language)
            .firstOrNull { it !is PhpDocStructureViewFactory }
        if (next != null) {
            return next.getStructureViewBuilder(psiFile)
        }
        return PhpStructureViewBuilderProvider().getStructureViewBuilder(psiFile)
    }
}
