package phpdoc.structure

import com.intellij.ide.structureView.StructureView
import com.intellij.ide.structureView.StructureViewBuilder
import com.intellij.ide.structureView.StructureViewModel
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder
import com.intellij.ide.structureView.newStructureView.StructureViewComponent
import com.intellij.lang.LanguageStructureViewBuilder
import com.intellij.lang.PsiStructureViewFactory
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

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

            override fun createStructureView(fileEditor: FileEditor?, project: Project): StructureView {
                val view = super.createStructureView(fileEditor, project)
                val tree = (view as? StructureViewComponent)?.tree ?: return view
                tree.cellRenderer = PhpDocNodeRenderer()
                return view
            }
        }
    }

    private fun delegateBuilder(psiFile: PsiFile): StructureViewBuilder? {
        return LanguageStructureViewBuilder.getInstance().allForLanguage(psiFile.language)
            .firstOrNull { it !is PhpDocStructureViewFactory }
            ?.getStructureViewBuilder(psiFile)
    }
}
