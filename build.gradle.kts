plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.intellij.platform")
}

kotlin {
    jvmToolchain(25)
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        // CDN JetBrains отвечает 451 на архив PhpStorm, поэтому сборка идёт
        // из локальной установки PS-262.10968.76. Если архив доступен, замените на phpstorm("2026.2.3").
        local("/opt/devel/PhpStorm")
        bundledPlugin("com.jetbrains.php")
        bundledModules("intellij.platform.structureView")
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "262"
            untilBuild = provider { null }
        }
    }
}
