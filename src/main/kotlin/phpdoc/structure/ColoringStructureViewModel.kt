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
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.openapi.vcs.FileStatus
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Modifier
import java.lang.reflect.Proxy

class ColoringStructureViewModel(
    private val delegate: StructureViewModel,
) : StructureViewModel,
    StructureViewModel.ElementInfoProvider,
    StructureViewModel.ExpandInfoProvider,
    ProvidingTreeModel {
    private val providing = delegate as? ProvidingTreeModel
    private val elementInfo = delegate as? StructureViewModel.ElementInfoProvider
    private val expandInfo = delegate as? StructureViewModel.ExpandInfoProvider

    override fun getRoot(): StructureViewTreeElement = colorizeStructureNode(delegate.root) as StructureViewTreeElement

    override fun getNodeProviders(): Collection<NodeProvider<*>> {
        return providing?.nodeProviders.orEmpty().map { wrapNodeProvider(it) }
    }

    override fun isEnabled(provider: NodeProvider<*>): Boolean {
        val original = unwrapNodeProvider(provider) ?: provider
        return providing?.isEnabled(original) ?: false
    }

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

    override fun getMinimumAutoExpandDepth(): Int {
        return expandInfo?.minimumAutoExpandDepth ?: super.getMinimumAutoExpandDepth()
    }
}

private class NodeProviderHandler(
    val delegate: NodeProvider<*>,
) : InvocationHandler {
    override fun invoke(proxy: Any, method: java.lang.reflect.Method, args: Array<out Any>?): Any? {
        val arguments = args ?: emptyArray()
        if (method.name == "provideNodes" && arguments.size == 1) {
            val provided = delegate.provideNodes(arguments[0] as TreeElement)
            return provided.map { colorizeStructureNode(it) }
        }
        if (method.name == "equals" && arguments.size == 1) {
            val other = arguments[0]
            val otherDelegate = unwrapNodeProvider(other)
            return delegate == (otherDelegate ?: other)
        }
        if (method.name == "hashCode" && arguments.isEmpty()) {
            return delegate.hashCode()
        }
        if (method.name == "toString" && arguments.isEmpty()) {
            return delegate.toString()
        }
        return try {
            method.invoke(delegate, *arguments)
        } catch (exception: InvocationTargetException) {
            throw exception.cause ?: exception
        }
    }
}

private fun wrapNodeProvider(delegate: NodeProvider<*>): NodeProvider<*> {
    val interfaces = publicInterfaces(delegate.javaClass).toTypedArray()
    val proxy = Proxy.newProxyInstance(
        ColoringStructureViewModel::class.java.classLoader,
        interfaces,
        NodeProviderHandler(delegate),
    )
    return proxy as NodeProvider<*>
}

private fun unwrapNodeProvider(provider: Any): NodeProvider<*>? {
    if (!Proxy.isProxyClass(provider.javaClass)) {
        return null
    }
    val handler = Proxy.getInvocationHandler(provider)
    return (handler as? NodeProviderHandler)?.delegate
}

private fun publicInterfaces(type: Class<*>): Set<Class<*>> {
    val result = linkedSetOf<Class<*>>()
    val queue = ArrayDeque<Class<*>>()
    queue.add(type)
    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        for (iface in current.interfaces) {
            if (Modifier.isPublic(iface.modifiers) && result.add(iface)) {
                queue.add(iface)
            }
        }
        current.superclass?.let(queue::add)
    }
    result.add(NodeProvider::class.java)
    return result
}
