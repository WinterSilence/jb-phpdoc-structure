package phpdoc.structure

import com.intellij.ide.structureView.newStructureView.StructureViewComponent
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.ui.ColorPanel
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.panel
import java.awt.Component
import java.awt.Container
import java.awt.Window
import javax.swing.JComponent

class PhpDocColorConfigurable(project: Project) : SearchableConfigurable {
    private val settings = PhpDocColorSettings.getInstance(project)
    private var chooser: ColorPanel? = null
    private var input: JBTextField? = null

    override fun getId(): String = ID

    override fun getDisplayName(): String = DISPLAY_NAME

    override fun createComponent(): JComponent {
        val colorPanel = ColorPanel()
        val textField = JBTextField()
        colorPanel.selectedColor = settings.color
        textField.text = settings.tags.joinToString(",") { tag -> tag.trim() }

        chooser = colorPanel
        input = textField

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
            row("Marked tags:") {
                cell(textField)
                button("Restore Default") {
                    textField.text = DEFAULT_PHPDOC_MEMBER_TAGS.joinToString(",")
                }
            }
            row {
                comment("Tags with prefix @, separated by comma.")
            }
        }
    }

    override fun isModified(): Boolean = selectedColor().rgb != settings.color.rgb || selectTags() != settings.tags

    override fun apply() {
        settings.color = selectedColor()
        settings.tags = selectTags()

        repaintOpenStructureViews()
    }

    override fun reset() {
        chooser?.selectedColor = settings.color
        input?.text = settings.tags.joinToString(",")
    }

    private fun selectedColor() = chooser?.selectedColor ?: settings.color
    private fun selectTags() = input?.text?.split(",")?.toSet() ?: settings.tags

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

    companion object {
        const val ID = "phpdoc.structure.color.settings"
        const val DISPLAY_NAME = "PHPDoc Structure"
    }
}
