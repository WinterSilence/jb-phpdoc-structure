package phpdoc.structure

import com.intellij.ide.structureView.FileEditorPositionListener
import com.intellij.ide.structureView.ModelListener
import com.intellij.ide.structureView.StructureViewModel
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.Filter
import com.intellij.ide.util.treeView.smartTree.Grouper
import com.intellij.ide.util.treeView.smartTree.NodeProvider
import com.intellij.ide.util.treeView.smartTree.ProvidingTreeModel
import com.intellij.ide.util.treeView.smartTree.Sorter
import com.intellij.openapi.vcs.FileStatus

class ColoringStructureViewModel(
    private val delegate: StructureViewModel,
) : StructureViewModel,
    StructureViewModel.ElementInfoProvider,
    StructureViewModel.ExpandInfoProvider,
    ProvidingTreeModel {
    private val providing = delegate as? ProvidingTreeModel
    private val elementInfo = delegate as? StructureViewModel.ElementInfoProvider
    private val expandInfo = delegate as? StructureViewModel.ExpandInfoProvider

    override fun getRoot(): StructureViewTreeElement = delegate.root

    override fun getNodeProviders(): Collection<NodeProvider<*>> = providing?.nodeProviders.orEmpty()

    override fun isEnabled(provider: NodeProvider<*>): Boolean = providing?.isEnabled(provider) ?: false

    override fun getGroupers(): Array<Grouper> = delegate.groupers

    override fun getSorters(): Array<Sorter> = delegate.sorters

    override fun getFilters(): Array<Filter> = delegate.filters + PhpDocFilter

    override fun dispose() {
        delegate.dispose()
    }

    override fun getCurrentEditorElement(): Any? = delegate.currentEditorElement

    override fun shouldEnterElement(element: Any?): Boolean = delegate.shouldEnterElement(element)

    override fun addEditorPositionListener(listener: FileEditorPositionListener) {
        delegate.addEditorPositionListener(listener)
    }

    override fun removeEditorPositionListener(listener: FileEditorPositionListener) {
        delegate.removeEditorPositionListener(listener)
    }

    override fun addModelListener(listener: ModelListener) {
        delegate.addModelListener(listener)
    }

    override fun removeModelListener(listener: ModelListener) {
        delegate.removeModelListener(listener)
    }

    override fun getElementStatus(element: Any?): FileStatus = delegate.getElementStatus(element)

    override fun isAlwaysShowsPlus(element: StructureViewTreeElement): Boolean {
        return elementInfo?.isAlwaysShowsPlus(element) ?: false
    }

    override fun isAlwaysLeaf(element: StructureViewTreeElement): Boolean {
        return elementInfo?.isAlwaysLeaf(element) ?: false
    }

    override fun isAutoExpand(element: StructureViewTreeElement): Boolean {
        return expandInfo?.isAutoExpand(element) ?: false
    }

    override fun isSmartExpand(): Boolean = expandInfo?.isSmartExpand ?: false
}
