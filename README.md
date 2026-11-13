# PHPDoc Structure Color

The PhpStorm plugin colors properties and methods declared with `@property`, `@property-read`, `@property-write`, and `@method` tags (and Psalm/Phpstan prefixed versions) in the "Structure" tab.

Regular fields and methods remain unchanged. Inherited members from these tags are colored the same.
Sorting and Structure groups remain from PHP. In the "View Options", under the "Show" group, there's a "Show PHPDoc members" option: unchecking this option hides these members.

## Deploy

JDK 25 is required. The build is compiled against the installed PhpStorm 2026.2.

```bash
./gradlew buildPlugin
```

Plugin archive: `build/distributions/jb-phpdoc-structure-X.Y.Z.zip`.

Verify plugin:
```bash
java -jar verifier-all.jar check-plugin jb-phpdoc-structure/build/distributions/jb-phpdoc-structure-X.Y.Z.zip /opt/devel/PhpStorm
```
(Replace "/opt/devel/PhpStorm" to your PhpStorm install directory)

Installation: `Settings` | `Plugins` | `"gear icon"` | `Install Plugin from Disk`.

## Checking in IDE

```bash
./gradlew runIde
```

Open a PHP class with a regular field, a regular method, and `@property`/`@method` tags. Color only for members from `PhpDoc`.
