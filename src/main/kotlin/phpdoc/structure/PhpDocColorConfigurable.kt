package phpdoc.structure

import com.intellij.ide.structureView.newStructureView.StructureViewComponent
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.ui.ColorPanel
import com.intellij.ui.dsl.builder.panel
import java.awt.Component
import java.awt.Container
import java.awt.Window
import javax.swing.JComponent

class PhpDocColorConfigurable(private val project: Project) : SearchableConfigurable {
    private val settings = PhpDocColorSettings.getInstance(project)
    private var chooser: ColorPanel? = null

    override fun getId(): String = ID

    override fun getDisplayName(): String = DISPLAY_NAME

    override fun createComponent(): JComponent {
        val colorPanel = ColorPanel()
        colorPanel.selectedColor = settings.color
        chooser = colorPanel
        return panel {
            row("Member color:") {
                cell(colorPanel)
                button("Restore Default") {
                    colorPanel.selectedColor = DEFAULT_PHPDOC_MEMBER_COLOR
                }
            }
            row {
                comment("Color of @property and @method members in the Structure view.")
            }
        }
    }

    override fun isModified(): Boolean = selectedColor().rgb != settings.color.rgb

    override fun apply() {
        settings.color = selectedColor()
        repaintOpenStructureViews()
    }

    override fun reset() {
        chooser?.selectedColor = settings.color
    }

    private fun selectedColor() = chooser?.selectedColor ?: settings.color

    companion object {
        const val ID = "phpdoc.structure.color.settings"
        const val DISPLAY_NAME = "PhpDoc Structure"
    }
}

private fun repaintOpenStructureViews() {
    for (window in Window.getWindows()) {
        repaintStructureViews(window)
    }
}

private fun repaintStructureViews(component: Component) {
    if (component is StructureViewComponent) {
        component.tree.repaint()
    }
    if (component is Container) {
        for (child in component.components) {
            repaintStructureViews(child)
        }
    }
}
