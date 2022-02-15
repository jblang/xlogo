# Developer Notes

These instructions are for developers or advanced users who are comfortable compiling their own code. If you try it, please report any bugs you find or additional Java or OS versions you test. Pull requests are welcome and encouraged.

## Java Requirements

You'll need a JDK to compile and run XLogo. I have been using [Amazon Coretto 17](https://aws.amazon.com/corretto) for development. This is the only build I am regularly testing with.  Many companies offer free [OpenJDK builds](https://sdkman.io/jdks). Any OpenJDK 17 build should work, so pick one you prefer.

## Build Process

I'm using the free community edition of [IntelliJ IDEA](https://www.jetbrains.com/idea/download/) for development. There are project files in the repo. Use the pre-configured `Logo` run configuration.

There are also Eclipse project files, but beyond importing them into IntelliJ, I haven't tested them.  The project contains an Ant build script but again, I have not tested it. Dependency management is currently manual.

## Dependencies

Until the ant build system is replaced with something supporting dependency management, the dependencies 
are stored in the repo.

I downloaded the jars for dependencies I added from maven.org:
- [flatlaf-2.0.1.jar](https://search.maven.org/artifact/com.formdev/flatlaf/2.0.1/jar)
- [flatlaf-extras-2.0.1.jar](https://search.maven.org/artifact/com.formdev/flatlaf-extras/2.0.1/jar)
- [svgSalamander-1.1.3.jar](https://search.maven.org/artifact/com.formdev/svgSalamander/1.1.3/jar)

The remaining jars were already in the subversion repo when it was imported:
- jh.jar: JavaHelp
- j3dcore.jar: Java3D core
- j3dutils.jar: Java3D utilities
- vecmath.jar: Java3D vector math
- jl1.0.1.jar: JavaZOOM JLayer MP3 library

## Known Bugs
- Zooming and clipboard selection on images don't work right
- Rendering in high quality sometimes leaves artifacts
- The scroll area for the graphics display doesn't size to properly fit
- The turtle PNGs need to be replaced with SVGs so they aren't pixelated
- Save, new, stop, and play commands don't always disable when they should
- Some windows catch close events and won't close via the window controls

## Code Quality Issues
- Build system needs dependency management. Still undecided, but probably Gradle.
- Resources are unorganized and mixed in with code
- French and English are inconsistently used in class names, method names, and property keys
- The Java style guide has not been followed for class, method, and variable names
- Code organization needs to be improved
- There are no unit tests so it's easy to break things

## Adding a Language
- `Config.java`: add a `public static LANGUAGE_...` constant 
- `Logo.java`:
  - Add English name of language to `englishLanguage`
  - Add two-letter ISO abbreviation to `locales`
  - Add a new property key to `translationLanguage` in `generateLanguage()`
  - Add a case for the new language in `getLocale()`
- Flag image: add a 150x100 png with a flag representing the language in `utils`
- GPL licence: Create an HTML file in the `gpl` directory with a translation of the GPL 
- Update language properties files genrated from Help->Translate

## Adding Primitives
- `xlogo/kernel/genericPrimitive`: Add the generic name for the primitive and the number of arguments 
  - If this primitive allows a general form (example: `(sum 1 2 3 4)`), add a + at the end of the line
- `xlogo/kernel/LaunchPrimitive.java`: add the primitive instruction
- If it's a drawing primitive, use `DrawPanel.java`
- If it's a control primitive (loop, break, return ...), add it to `Primitive.java` 