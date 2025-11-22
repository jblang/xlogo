# Developer Notes

These instructions are for developers or advanced users who are comfortable compiling their own code. If you try it, please report any bugs you find or additional Java or OS versions you test. Pull requests are welcome and encouraged.

## Build Process

- You'll need a JDK to compile and run XLogo. Any OpenJDK 11+ build should work. I primarily use [Eclipse Temurin 11](https://adoptium.net/temurin/releases?version=11) by Adoptium for development.
- I'm using the free community edition of [IntelliJ IDEA](https://www.jetbrains.com/idea/download/) for development. There are project files in the repo with a pre-configured `Logo` run configuration.
- The project uses Maven for builds. All dependencies are managed via Maven repositories.

### Build Commands

```bash
# Build shaded (fat) JAR with all dependencies
mvn clean package

# Quick build without shading (for development)
mvn clean package -Pquick

# Run from IDE or command line
java -jar target/xlogo-1.0.0-beta5.jar
```

## Dependencies

All dependencies are managed via Maven. Key dependencies include:

- **UI Framework**: FlatLaf 2.0.1 (modern Swing look and feel)
- **Code Editor**: RSyntaxTextArea 3.1.6 with autocomplete and UI extensions
- **3D Graphics**:
  - Java3D 1.6.0-scijava-2 from SciJava (uses `org.scijava.*` packages)
  - JOGL 2.4.0-rc4 from [jzy3d repository](https://maven.jzy3d.org/releases/) (includes Apple Silicon support)
- **Audio**: JLayer 1.0.1 for MP3 support
- **Help**: JavaHelp 2.0.05

Note: JOGL 2.4.0-rc4 is sourced from the jzy3d repository because the official jogamp Maven repository doesn't have ARM64 natives for macOS.

## Code Quality Issues

- There are no unit tests so it's easy to break things. I have not yet started to remedy this.
- Bad naming conventions for variables, method names, and property keys:
  - French and English are inconsistently used throughout.
  - The Java style guide has not been followed. 
  - This is improving over time: All of the top-level classes are now consistently in English and in CamelCase. I'm renaming methods and variables as I find them in the areas of code I work on.
- Bad code smells. If you like refactoring, you have arrived in heaven!
  - Public members and [Feature Envy](https://refactoring.guru/smells/feature-envy) abound.
  - Lots of copypasta.
  - Many long methods and one insanely long [switch statement](https://github.com/jblang/xlogo/blob/main/src/xlogo/kernel/LaunchPrimitive.java#L152-L3823) (3671 lines!)
  - UI code is [Totally Gridbag](https://www.youtube.com/watch?v=UuLaxbFKAcc), but I'm [working](https://github.com/jblang/xlogo/blob/main/src/xlogo/gui/preferences/EditorPanel.java#L230-L250) [on it](https://github.com/jblang/xlogo/blob/main/src/xlogo/utils/GridBagPanel.java).

## Adding a Language
- `xlogo/Language.java`: add an enum entry for the language with the following:
  - `name` is the native name of the Language in its native script
  - `code` is the two-letter ISO code of the language
  - `country` is the two-letter ISO code of the country associated with the language
  - `key` is the property key used to look up a localized version of the language name
- Flag image: add a SVG with a flag representing the language in `resources/flags`. All current flags come from Wikimedia Commons.
- GPL licence: Create an HTML file in the `resources/gpl` directory with a translation of the GPL.
- Update language properties files generated from Help->Translate

## Adding Primitives
- In `xlogo/kernel/Primitives.java`: 
  - Add the method implementing the primitive with the signature `void primitive(Stack<String> param)`
  - Add a reference to the method to the end of the primitives list: `new Prim(key, arity, general, function)`
    - `key` is used to look up the primitive in the resource bundle
    - `arity` is the number of arguments the primitive takes
    - `general` is whether the primitive has a general form (e.g., `(sum 1 2 3 4)`)
    - `function` is the method implementing the primitive operation