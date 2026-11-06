# PhpDoc Structure Color

Плагин PhpStorm красит во вкладке Structure свойства и методы, объявленные тегами `@property`, `@property-read`, `@property-write` и `@method`. Цвет текста — `#207D97`.

Обычные поля и методы не меняются. Наследуемые члены из этих тегов красятся так же. Сортировка и группы Structure остаются от PHP. В View Options, в группе Show, есть пункт "Show PhpDoc members": снятый флажок скрывает эти члены.

## Сборка

Нужен JDK 25. Сборка компилируется против установленного PhpStorm 2026.2 (`/opt/devel/PhpStorm`, сборка 262.10968.76): архив с download.jetbrains.com сейчас отвечает HTTP 451. Если архив доступен, в `build.gradle.kts` замените `local(...)` на `phpstorm("2026.2.3")`.

```bash
./gradlew buildPlugin
```

Архив плагина: `build/distributions/phpstorm-phpdoc-structure-0.1.0.zip`.

Установка: Settings | Plugins | шестерёнка | Install Plugin from Disk.

## Проверка в IDE

```bash
./gradlew runIde
```

Откройте PHP-класс с обычным полем, обычным методом и тегами `@property` / `@method`. Цвет `#207D97` только у членов из PhpDoc.
