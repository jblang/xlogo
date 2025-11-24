# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

XLogo is an educational Logo interpreter written in Java (GPL licensed). Users control a "turtle" that draws graphics using commands like `forward`, `right`, etc. Features include 3D graphics (Java3D), networking, sound, animation, and support for 12 UI languages.

Originally developed by Loic Le Coq until 2012, now maintained by J.B. Langston.

## Build Commands

```bash
mvn clean package    # Full build - creates shaded JAR
mvn package          # Build without cleaning
mvn compile          # Compile only
```

**Build output:** `target/xlogo-1.0.0-beta5.jar` (shaded JAR with all dependencies, ~12MB)

**Requirements:** JDK 21+ (tested with Eclipse Temurin 21)

## Running the Application

```bash
java -jar target/xlogo-1.0.0-beta5.jar
java -jar target/xlogo-1.0.0-beta5.jar file.lgo    # Open a file
java -jar target/xlogo-1.0.0-beta5.jar -memory 512  # Set heap size
```

The JAR self-relaunches with proper JVM arguments (heap size from `~/.xlogo/xmx.txt`, JOGL exports for Java 9+).

## Testing

**No automated tests exist.** Testing is manual only - this is a known gap. Example Logo programs in `/examples` can be used for manual testing.

## Architecture

### Entry Points
1. `Logo.main()` - Application entry point, handles JVM relaunch with proper args
2. `Application()` - Main JFrame with editor, draw panel, command line

### Key Packages

- **`xlogo.kernel`** - Core interpreter engine (~12,500 LOC across 25 files)
  - `Interpreter.java` (6,500+ LOC) - Parser and execution engine
  - `Kernel.java` - Facade between GUI and interpreter
  - `Workspace.java` - Manages procedures, variables, turtle state
  - `DrawPanel.java` - 2D turtle graphics rendering
  - `Primitive.java` - Functional interface for built-in commands

- **`xlogo.gui`** - Swing-based UI
  - `Application.java` - Main window
  - `Editor.java` - RSyntaxTextArea-based code editor

- **`xlogo.kernel.perspective`** - 3D graphics (Java3D)

### Execution Flow
User input -> `Application.startAnimation()` -> `Interpreter.execute()` -> primitives update turtle state -> `DrawPanel` renders

### Localization
- UI strings: `resources/language_*.properties`
- Primitive names: `resources/primitives_*.properties`
- Language enum: `Language.java`

## Dependencies

All dependencies managed via Maven (`pom.xml`):

| Library | Version | Purpose |
|---------|---------|---------|
| FlatLaf | 3.6.2 | Modern Swing look & feel |
| RSyntaxTextArea | 3.6.0 | Syntax-highlighting editor |
| JOGL/GlueGen | 2.4.0-rc4 | OpenGL binding (from jzy3d repo) |
| Java3D (SciJava) | 1.6.0-scijava-2 | 3D graphics |
| JavaHelp | 2.0.05 | Help system |
| JLayer | 1.0.1 | MP3 player |

**Note:** Java3D uses SciJava's mavenized version with renamed packages:
- `javax.vecmath` -> `org.scijava.vecmath`
- `javax.media.j3d` -> `org.scijava.java3d`
- `com.sun.j3d` -> `org.scijava.java3d`

## Adding Features

### Adding a New Primitive
1. Add method in `Interpreter.java` with signature `void methodName(Stack<String> params)`
2. Register in the `primitives` list with `new Primitive(key, arity, general, this::methodName)`
3. Add localized names to all `primitives_*.properties` files

### Adding a New Language
See DEVNOTES.md for detailed steps involving `Language.java`, flag SVGs, and properties files.

## Known Code Quality Issues

From DEVNOTES.md:
- No unit tests
- French/English naming inconsistently mixed
- Very long methods (Interpreter is 6,500+ LOC)
- Public fields and feature envy throughout
- UI uses heavy GridBagLayout (being improved with `GridBagPanel` helper)

## Key Documentation

- `README.md` - Project overview, download links, documentation links
- `DEVNOTES.md` - Build process, adding features, code quality issues
- External: http://xlogo.tuxfamily.org/ (manuals in 7 languages)
