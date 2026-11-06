package phpdoc.structure

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import java.awt.Color

internal val DEFAULT_PHPDOC_MEMBER_COLOR: Color = Color(0x20, 0x7D, 0x97)

@Service(Service.Level.PROJECT)
@State(name = "PhpDocStructureColorSettings", storages = [Storage("phpdocStructureColor.xml")])
class PhpDocColorSettings : PersistentStateComponent<PhpDocColorSettings.State> {
    var color: Color = DEFAULT_PHPDOC_MEMBER_COLOR

    class State {
        var color: String = DEFAULT_PHPDOC_MEMBER_COLOR.toHex()
    }

    override fun getState(): State = State().also { it.color = color.toHex() }

    override fun loadState(state: State) {
        color = parseColor(state.color) ?: DEFAULT_PHPDOC_MEMBER_COLOR
    }

    companion object {
        fun getInstance(project: Project): PhpDocColorSettings = project.service()
    }
}

internal fun parseColor(value: String?): Color? {
    val hex = value?.trim()?.removePrefix("#") ?: return null
    if (hex.length != 6) {
        return null
    }
    val rgb = hex.toIntOrNull(16) ?: return null
    return Color(rgb)
}

internal fun Color.toHex(): String = "%06X".format(rgb and 0xFFFFFF)
