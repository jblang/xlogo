# Developer Notes

These instructions are for developers or advanced users who are comfortable compiling their own code. If you try it, please report any bugs you find or additional Java or OS versions you test. Pull requests are welcome and encouraged.

## Build Process

- You'll need a JDK to compile and run XLogo. Any OpenJDK 21+ build should work. I primarily use [Temurin 21](https://adoptium.net/temurin/releases?version=21&os=any&arch=any) by Adoptium for development.
- I'm using the free community edition of [IntelliJ IDEA](https://www.jetbrains.com/idea/download/) for development.
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

- **UI Framework**: FlatLaf 3.6.2 (modern Swing look and feel)
- **Code Editor**: RSyntaxTextArea 3.6.0 with autocomplete 3.3.2 and rstaui 3.3.1
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
- Bad code smells. Public members and [Feature Envy](https://refactoring.guru/smells/feature-envy) abound.

## Modernization Opportunities (Java 21)

The following improvements could be made to leverage modern Java features:

### High Priority

- **Break up `Interpreter.java`** (~5,500 LOC): Extract primitives into domain-specific classes (MathPrimitives, GraphicsPrimitives, ControlPrimitives, IOPrimitives) using a registry pattern.
- **Encapsulate public fields**: Core classes like `Turtle`, `DrawPanel`, `Application`, and `World3D` expose internal state via public fields. These should be private with accessor methods.
- **Convert to records** (Java 16+): `Primitive` class is a good candidate—immutable data with final fields.

### Medium Priority

- **Pattern matching for instanceof** (Java 16+): Found in `Application.java`, `Interpreter.java`, `TranslationTable.java`. Replace `if (x instanceof Foo) { ((Foo) x).bar(); }` with `if (x instanceof Foo f) { f.bar(); }`.
- **Switch expressions** (Java 14+): `Fog.java`, `Light.java`, and `Logo.java` have switch statements that could use arrow syntax and yield values.
- **Remove `new String(sb)` anti-pattern**: 30+ occurrences across `Interpreter.java`, `Workspace.java`, `Procedure.java`, `Turtle.java`. Use `sb.toString()` instead.
- **Try-with-resources**: `Utils.java` and `HistoryPanel.java` manually close file streams instead of using try-with-resources.

### Lower Priority

- **Lambdas for anonymous classes**: `SoundPlayer.java`, `HistoryPanel.java` have single-method anonymous classes that could be lambdas.
- **Sealed classes** (Java 17+): `Element3D` hierarchy and Loop subclasses (`LoopRepeat`, `LoopFor`, etc.) could be sealed for exhaustive pattern matching.
- **Text blocks** (Java 15+): Multi-line strings and embedded HTML could use `"""` syntax.
- **Virtual threads** (Java 21): Network operations and animation could benefit from lightweight threads.

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
- In `xlogo/kernel/Interpreter.java`:
  - Add a method implementing the primitive with the signature `void methodName(Stack<String> params)`
  - Add a reference to the method in the `primitives` list: `new Primitive(key, arity, general, function)`
    - `key` is used to look up the primitive in the resource bundle
    - `arity` is the number of arguments the primitive takes
    - `general` is whether the primitive has a general form (e.g., `(sum 1 2 3 4)`)
    - `function` is a method reference to the primitive implementation (e.g., `this::methodName`)