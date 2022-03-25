# Developer Notes

These instructions are for developers or advanced users who are comfortable compiling their own code. If you try it, please report any bugs you find or additional Java or OS versions you test. Pull requests are welcome and encouraged.

## Build Process

- You'll need a JDK to compile and run XLogo. Many companies offer free [OpenJDK builds](https://sdkman.io/jdks). Any OpenJDK 17 build should work, so pick one you prefer.  I have primarily been using [Amazon Coretto 17](https://aws.amazon.com/corretto) for development.
- I'm using the free community edition of [IntelliJ IDEA](https://www.jetbrains.com/idea/download/) for development. There are project files in the repo with a pre-configured `Logo` run configuration.
- The project contains an Ant build script, which I use to build the jars for release on GitHub. 

## Dependencies

Because not all the required libraries are available in MavenCentral, I am keeping the dependencies in the repo.  The jars were sourced from the following locations.
  - Jars imported from original SVN repo:
    - jh.jar: JavaHelp 2.0_03
    - jl1.0.1.jar: JLayer 1.0.1 MP3 library
  - Jars downloaded from maven.org:
    - [flatlaf-2.0.1.jar](https://search.maven.org/artifact/com.formdev/flatlaf/2.0.1/jar)
    - [flatlaf-extras-2.0.1.jar](https://search.maven.org/artifact/com.formdev/flatlaf-extras/2.0.1/jar)
    - [svgSalamander-1.1.3.jar](https://search.maven.org/artifact/com.formdev/svgSalamander/1.1.3/jar)
    - [rsyntaxtextarea-3.1.6.jar](https://search.maven.org/artifact/com.fifesoft/rsyntaxtextarea/3.1.6/jar)
    - [rstaui-3.1.4.jar](https://search.maven.org/artifact/com.fifesoft/rstaui/3.1.4/jar)
    - [autocomplete-3.1.5.jar](https://search.maven.org/artifact/com.fifesoft/autocomplete/3.1.5/jar)
  - Jars downloaded elsewhere:
    - [Java3D 1.6.2](https://github.com/hharrison/java3d-core/releases/tag/1.6.2) from GitHub:
      - j3dcore.jar
      - j3dutils.jar
      - vecmath.jar
    - [JOGL/GlueGen 2.3.2](https://jogamp.org/deployment/v2.3.2/jar/) from jogamp.org:
      - jogl-all*.jar
      - gluegen-rt*.jar

## Known Bugs

Rather than try to catalog them here, I will start creating [GitHub Issues](https://github.com/jblang/xlogo/issues) from now on.

## Code Quality Issues

- It would be nice to have automated dependency management, but the latest JOGL and Java3D jars don't seem to be available in MavenCentral.  If that ever gets resolved, I'll probably switch to Gradle.
- There are no unit tests so it's easy to break things.  I have not yet started to remedy this.
- Bad naming conventions for variables, method names, and property keys:
  - French and English are inconsistently used throughout.
  - The Java style guide has not been followed. 
  - This is improving over time: All of the top-level classes are now consistently in English and in CamelCase. I'm renaming methods and variables as I find them in the areas of code I work on.
- Bad code smells. If you like refactoring, you have arrived in heaven!
  - Public members and [Feature Envy](https://refactoring.guru/smells/feature-envy) abound.
  - Lots of copypasta.
  - Many long methods and one insanely long [switch statement](https://github.com/jblang/xlogo/blob/main/src/xlogo/kernel/LaunchPrimitive.java#L152-L3823) (3671 lines!)
  - UI code is [Totally Gridbag](https://www.youtube.com/watch?v=UuLaxbFKAcc), but I'm [working](https://github.com/jblang/xlogo/blob/main/src/xlogo/gui/preferences/EditorPanel.java#L230-L250) [on it](https://github.com/jblang/xlogo/blob/main/src/xlogo/utils/GridBagPanel.java).
- I take great pride in having reduced the lines of code from 19782 to 15876 while improving the feature set, but I probably have at least another 5000 lines to go.

## Adding a Language
- `Config.java`: add a `public static LANGUAGE_...` constant 
- `Logo.java`:
  - Add English name of language to `englishLanguage`
  - Add two-letter ISO abbreviation to `locales`
  - Add a new property key to `translationLanguage` in `generateLanguage()`
  - Add a case for the new language in `getLocale()`
- Flag image: add a SVG with a flag representing the language in `resources/flags`. All current flags come from Wikimedia Commons.
- GPL licence: Create an HTML file in the `resources/gpl` directory with a translation of the GPL 
- Update language properties files genrated from Help->Translate

## Adding Primitives
- `xlogo/resources/Primitives.txt`: Add the generic name for the primitive and the number of arguments 
  - If this primitive allows a general form (example: `(sum 1 2 3 4)`), add a + at the end of the line
- `xlogo/kernel/LaunchPrimitive.java`: add the primitive instruction
- If it's a drawing primitive, use `DrawPanel.java`
- If it's a control primitive (loop, break, return ...), add it to `Primitive.java` 