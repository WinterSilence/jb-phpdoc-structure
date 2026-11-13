package phpdoc.structure

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project
import java.awt.Color

internal val DEFAULT_PHPDOC_MEMBER_COLOR: Color = Color(0x20, 0x7D, 0x97)
internal val DEFAULT_PHPDOC_MEMBER_TAGS = setOf(
    "@property",
    "@property-read",
    "@property-write",
    "@method",
    "@phpstan-property",
    "@phpstan-method",
    "@psalm-property",
    "@psalm-method",
)

@Service(Service.Level.PROJECT)
@State(name = "PhpDocStructureColorSettings", storages = [Storage("phpdocStructureColor.xml")])
class PhpDocColorSettings : PersistentStateComponent<PhpDocColorSettings.State> {
    var color: Color = DEFAULT_PHPDOC_MEMBER_COLOR
    var tags: Set<String> = DEFAULT_PHPDOC_MEMBER_TAGS

    class State {
        var color: String = DEFAULT_PHPDOC_MEMBER_COLOR.toHex()
        var tags: String = DEFAULT_PHPDOC_MEMBER_TAGS.joinToString(",")
    }

    override fun getState(): State = State().also {
        it.color = color.toHex()
        it.tags = tags.joinToString(",")
    }

    override fun loadState(state: State) {
        color = parseColor(state.color) ?: DEFAULT_PHPDOC_MEMBER_COLOR
        tags = state.tags.split(",").map { it.trim() }.toSet()
    }

    companion object {
        fun getInstance(project: Project): PhpDocColorSettings = project.service()

        private fun parseColor(value: String?): Color? {
            val hex = value?.trim()?.removePrefix("#") ?: return null
            if (hex.length != 6) {
                return null
            }

            val rgb = hex.toIntOrNull(16) ?: return null
            return Color(rgb)
        }

        private fun Color.toHex(): String = "%06X".format(rgb and 0xFFFFFF)
    }
}
