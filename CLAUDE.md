# XLogo - Claude Code Guide

This document provides essential information about the XLogo codebase for Claude Code instances and AI assistants.

## Project Overview

**XLogo** is a Logo interpreter written in Java and licensed under the GPL. It is a modern reboot of the original XLogo project by Loïc Le Coq, maintained by J.B. Langston.

### Purpose
- Implement a Logo programming language interpreter for educational purposes
- Logo is an excellent language for teaching programming basics (loops, tests, procedures, etc.)
- The user controls a "turtle" graphics object using simple commands (forward, back, right, left)
- As the turtle moves, it leaves a trail, enabling graphical art creation
- Supports advanced features: 3D graphics, networking, sound, animation, and multiple languages (9 languages supported)

### Current Version
- **Version**: 1.0.0 beta 5
- **Java Requirement**: JDK 11+ (tested with Eclipse Temurin 11)
- **Target Java Release**: Java 11
- **License**: GPL v3

### Key Statistics
- **Total Java Files**: 79
- **Total Source Files**: 179
- **Largest File**: Interpreter.java (6546 lines)
- **Build System**: Apache Ant

---

## Build System & Build Commands

### Build Tool: Apache Ant
The project uses **Apache Ant** as the build system (see `/build.xml`).

#### Key Ant Targets
| Target | Purpose | Notes |
|--------|---------|-------|
| `deploy` (default) | Complete build including jar creation | Depends on `create.jar` |
| `compile` | Compile all Java files | Uses JDK 11, UTF-8 encoding |
| `copy.static.files` | Copy non-binary resources | Images, properties files, SVG, HTML |
| `create.jar` | Create executable jars | Produces `xlogo-main.jar` and `xlogo-${version}.jar` |
| `create.deploy` | Initialize deploy directory | Cleans and creates `/deploy` folder |
| `clean-deploy` | Remove deploy directory | |
| `create.src.tarball` | Create source distribution | Creates `.tar.bz2` file (optional) |
| `sign.xlogo` | Sign jar with keystore | Optional for Java Web Start deployment |

#### Build Commands
```bash
# Full build (default target)
ant

# Compile only
ant compile

# Create jars
ant create.jar

# Create source tarball
ant create.src.tarball

# Clean build directory
ant clean-deploy
```

#### Classpath & Dependencies
- All dependencies are in `/lib` directory (not managed by Maven/Gradle)
- Build classpath references all `.jar` files in `/lib`
- Dependencies are hardcoded in manifest as `Class-Path` attribute

#### Build Output
- **Directory**: `/deploy` (created during build)
- **Main Jar**: `deploy/xlogo-main.jar` - Contains compiled classes and resources
- **Distribution Jar**: `deploy/xlogo-${version}.jar` - Self-contained with Launcher
- **Source Tarball**: `deploy/xlogo-${version}-source.tar.bz2` (optional)

### IntelliJ IDEA Configuration
- Pre-configured run configuration: `Logo`
- Project files located in `.idea/` directory
- Module definition in `logo.iml`
- Use Community Edition (free)

---

## Testing

### Current Test Status
**No automated unit tests exist** - This is a known limitation documented in DEVNOTES.md.

### Testing Notes
- Manual testing only
- All testing is cross-platform (Windows, Mac, Linux)
- High DPI display handling needs testing
- No continuous integration / automated testing framework in place
- This is an area that needs development

### Test Files
- Example Logo programs in `/examples` directory
- Can be used for manual testing
- Organized by language: `en/`, `es/`, `fr/`, `pt/`

### Running the Application for Testing
```bash
# After building
cd deploy
java -cp xlogo-main.jar:lib/* xlogo.Logo

# Or run the full launcher jar
java -jar xlogo-${version}.jar
```

---

## Project Structure & Main Packages

### Directory Layout
```
/src/
├── Launcher.java                    # Entry point for standalone jar
└── xlogo/
    ├── Logo.java                    # Main application class
    ├── Config.java                  # Configuration management
    ├── Language.java                # Language enum for localization
    ├── kernel/                      # Core interpreter engine
    ├── gui/                         # GUI components and UI
    ├── utils/                       # Utility classes
    └── resources/                   # Images, properties, flags, etc.

/lib/                               # All third-party dependencies (33 jars)
/deploy/                            # Build output (created by Ant)
/examples/                          # Example Logo programs
/docs/                              # TeX documentation sources
/bin/                               # Pre-built binaries (legacy)
```

### Main Packages

#### `xlogo` (Top-level)
| Class | Purpose |
|-------|---------|
| `Logo.java` | Main application initializer - registers syntax highlighter, loads config, launches UI |
| `Config.java` | Persistent configuration (UI preferences, turtle settings, etc.) - stored in XML format |
| `Language.java` | Enum defining supported languages with ISO codes, localization keys, and flag images |

#### `xlogo.kernel` (Core Interpreter - 25 files, ~12,500 LOC)
The heart of the Logo interpreter. Responsible for parsing and executing Logo code.

| Class | Purpose | Size |
|-------|---------|------|
| **Interpreter.java** | Main interpreter - parses and executes Logo instructions | 6546 LOC |
| **Kernel.java** | Facade between GUI and interpreter | 124 LOC |
| **Workspace.java** | Manages procedures, variables, turtle state | 508 LOC |
| **DrawPanel.java** | 2D graphics rendering (Swing) | 2527 LOC |
| **Turtle.java** | Turtle state and geometry | 321 LOC |
| **Calculator.java** | Expression evaluation and math operations | 976 LOC |
| **Primitive.java** | Primitive command interface (functional interface pattern) | 36 LOC |
| **Procedure.java** | User-defined procedures | 197 LOC |
| **AbstractProcedure.java** | Interface for primitives and procedures | Interface |
| **InstructionBuffer.java** | Buffers instructions for execution | 197 LOC |
| **Animation.java** | Animation/threading support | 94 LOC |
| **Flow.java**, **FlowReader.java**, **FlowWriter.java** | File I/O operations | - |
| **LogoException.java** | Exception hierarchy | 74 LOC |
| **SyntaxException.java** | Syntax error reporting | - |
| **SoundPlayer.java** | Audio playback | 246 LOC |
| **MP3Player.java** | MP3-specific audio | - |
| **LogoTokenMaker.java** | Syntax highlighting tokenizer | 190 LOC |
| **Loop\*.java** (5 files) | Control structures: LoopFor, LoopForEach, LoopRepeat, LoopWhile, LoopFillPolygon | - |
| **kernel.gui/** | 3D GUI components (GuiButton, GuiComponent, GuiMenu, GuiMap) | - |
| **kernel.network/** | Networking primitives (ChatFrame, NetworkServer, NetworkClientExecute, etc.) | - |
| **kernel.perspective/** | 3D graphics rendering (World3D, Viewer3D, Light, Fog, Element3D primitives) | - |

#### `xlogo.gui` (User Interface - 13 files)
Swing-based GUI components and dialogs.

| Class | Purpose |
|-------|---------|
| **Application.java** | Main application window (extends JFrame) - contains menu bar, editor, draw panel, command line |
| **Editor.java** | RSyntaxTextArea-based code editor with syntax highlighting |
| **DrawPanel.java** | Canvas for 2D turtle graphics rendering |
| **InputFrame.java** | Input dialog for primitives requiring user input |
| **HistoryPanel.java** | Command history display |
| **SearchFrame.java** | Search/Find functionality |
| **MessageTextArea.java** | Status/error message display |
| **PrinterPanel.java** | Print preview and printing support |
| **StartupFileDialog.java** | Dialog for loading startup files |
| **ProcedureEraser.java** | UI for deleting procedures |
| **CodeTranslator.java** | Translate Logo code between languages |
| **LanguageListRenderer.java** | Language selection UI |
| **LicensePane.java** | GPL license display |
| **gui.preferences/** | PreferencesDialog, EditorPanel, SoundPanel, OptionsPanel, etc. |
| **gui.translation/** | UiTranslator - handles UI localization |

#### `xlogo.utils` (Utilities - 4 files)
| Class | Purpose |
|-------|---------|
| **Utils.java** | General utility functions |
| **ImageWriter.java** | Export drawings as images (PNG, BMP, etc.) |
| **GridBagPanel.java** | GridBagLayout helper (being improved) |
| **ExtensionFilter.java** | File dialog filters |

#### `xlogo.resources` (Resources)
- **flags/** - SVG images for language selection (from Wikimedia Commons)
- **icons/** - SVG icons (from IntelliJ IDEA)
- **turtles/** - Turtle shape images
- **gpl/** - GPL license in multiple languages
- **language_*.properties** - UI localization (12 languages)
- **primitives_*.properties** - Logo command localization (12 languages)

---

## Key Architectural Patterns & Main Entry Points

### Architecture Overview

The application follows a **Model-View-Controller (MVC)** pattern with a layered architecture:

```
┌─────────────────────────────────────┐
│   GUI Layer (xlogo.gui)              │
│   Application, Editor, DrawPanel    │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│  Control Layer (Kernel)              │
│   Kernel (Facade)                   │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│  Interpreter Engine (xlogo.kernel)   │
│  - Interpreter (parser & executor)  │
│  - Workspace (state management)     │
│  - DrawPanel (rendering)            │
│  - Calculator (math evaluation)     │
└──────────────────────────────────────┘
```

### Main Entry Points

#### 1. **Application Startup**
```
Launcher.main(String[] args)
  ↓
Launcher.extractJars() & launchJVM()
  ↓
Logo.main(String[] args)
  ↓
Application() constructor
  ↓
Kernel initialization
  ↓
Interpreter initialization
```

**Files**:
- `/src/Launcher.java` - Outer launcher (extracts nested jars, manages JVM)
- `/src/xlogo/Logo.java` - Bootstrap application

#### 2. **Execution Flow**
When user clicks "Run" or enters a command:

```
Application.startAnimation(String code)
  ↓
Interpreter.execute(StringBuffer instructions)
  ↓
Parser processes tokens/symbols
  ↓
Primitives executed via Primitive.execute(Stack<String> params)
  ↓
Turtle state updated
  ↓
DrawPanel repainted
```

**Key Classes**:
- `Interpreter` - Main execution engine
- `InstructionBuffer` - Command queue
- `Workspace` - State (procedures, variables, turtles)
- `Turtle` - Turtle position/orientation
- `DrawPanel` - Rendering

#### 3. **Language & Localization**
- UI strings loaded via `UiTranslator`
- Primitive names (commands) loaded from properties files
- Multi-language support: English, French, Spanish, Arabic, Portuguese, German, Esperanto, Galician, Greek, Hungarian, Catalan

### Key Design Patterns

#### **Primitive Command Pattern**
Logo primitives (built-in commands) implemented as lambda functions in a map.

```java
// In Interpreter
class Primitive implements AbstractProcedure {
    public String key;
    public int arity;
    public boolean general;  // supports general form?
    public PrimFunc function; // lambda implementation
    
    interface PrimFunc {
        void execute(Stack<String> params);
    }
}
```

#### **Stack-Based Evaluation**
- Operand stack for expression evaluation (RPN-like)
- Procedure call stack for tracking execution context
- Local variable scopes implemented as nested hash maps

#### **Workspace Management**
The `Workspace` class maintains:
- Defined procedures (user-defined commands)
- Global and local variables
- Property getter/setter variables
- Turtle state

#### **Observer Pattern**
- Application listens to GUI events
- DrawPanel repaints on turtle state changes
- Editor syntax highlighting via RSyntaxTextArea

### Notable Architectural Issues (Documented in DEVNOTES.md)

1. **Very long methods**: Interpreter class and LaunchPrimitive contain long switch statements
   - LaunchPrimitive has a 3671-line switch statement
   - Ideal candidate for refactoring

2. **No unit tests**: Manual testing only

3. **Inconsistent naming**:
   - French and English mixed throughout codebase
   - Not following Java style guide consistently
   - Improving over time with recent refactoring

4. **Public members & Feature Envy**: Common code smells

5. **Copy-paste code**: Opportunities for abstraction

6. **UI Code**: Heavy use of GridBagLayout (being improved with GridBagPanel helper)

---

## Dependencies

### Key Third-Party Libraries (in /lib)

| Library | Version | Purpose | License |
|---------|---------|---------|---------|
| **FlatLaf** | 2.0.1 | Modern Swing look & feel | Apache 2.0 |
| **RSyntaxTextArea** | 3.1.6 | Syntax-highlighting editor | BSD |
| **RSTAUI** | 3.1.4 | Editor UI components | BSD |
| **AutoComplete** | 3.1.5 | Code completion | BSD |
| **SvgSalamander** | 1.1.3 | SVG rendering | BSD |
| **Java3D** | 1.6.2 | 3D graphics | GPL |
| **JOGL/GlueGen** | 2.3.2 | OpenGL binding | BSD/MIT/Apache |
| **JavaHelp** | 2.0_03 | Help system | GPL |
| **JLayer** | 1.0.1 | MP3 player | LGPL |
| Platform-specific JOGL/GlueGen natives | | OpenGL native libraries | Multiple |

### Dependency Management
- Dependencies not available in MavenCentral are stored in the repo
- No Maven or Gradle used (documented limitation)
- Manual jar management in `/lib`
- Someday may switch to Gradle if JOGL/Java3D become available in MavenCentral

---

## Existing Documentation

### Project Documentation Files
| File | Purpose |
|------|---------|
| **README.md** | Main project documentation, download links, feature overview |
| **DEVNOTES.md** | Developer guide: build process, dependencies, code quality issues, adding features |
| **COPYING.md** | Full GPL v3 license text |

### External Documentation
- **XLogo Website**: http://xlogo.tuxfamily.org/ (extensive manuals in 7 languages)
- **LogoArts**: http://www.cr31.co.uk/logoarts/ (examples and primitive reference)
- **Java3D Documentation**: Needed for 3D graphics development

### Help System
- Integrated JavaHelp system (configured in Application.java)
- Help files referenced but path/implementation details in help configuration

### Code Documentation
- Javadoc comments on major classes (Interpreter, Kernel, Application)
- Recently improved documentation quality
- DEVNOTES.md provides guidelines for adding languages and primitives

---

## Existing AI Assistant Rules

### Current Configuration
- **.claude/settings.local.json** exists but is empty/minimal
- **No .cursorrules or .github/copilot-instructions.md** files exist
- No formal AI assistant guidelines currently in place

### IntelliJ IDEA Configuration
- **.idea/** folder contains IDE configuration
- Pre-configured `Logo` run configuration for easy testing
- Module dependencies properly configured

---

## Important Files to Know

### Critical Entry Points
| File | Role | Key Methods |
|------|------|-------------|
| `src/Launcher.java` | Outer JAR launcher | `main()`, `extractJars()` |
| `src/xlogo/Logo.java` | App initializer | `main()`, `launchApp()` |
| `src/xlogo/gui/Application.java` | Main window | `Application()`, `startAnimation()` |
| `src/xlogo/kernel/Interpreter.java` | Execution engine | `execute()`, parser methods |
| `src/xlogo/kernel/Kernel.java` | Facade | All public execution entry points |

### Configuration Files
| File | Purpose |
|------|---------|
| `build.xml` | Ant build configuration |
| `Config.java` | Runtime configuration (stored in `~/.xlogo/`) |
| `Language.java` | Supported languages enum |

### Build Output
- `/deploy/xlogo-main.jar` - Main application JAR (contains compiled classes)
- `/deploy/xlogo-1.0.0beta5.jar` - Distribution JAR (with Launcher and dependencies)

---

## Development Quick Start

### Building
```bash
cd /Users/jblang/repos/xlogo
ant clean-deploy          # Clean previous build
ant                       # Full build (same as 'ant deploy')
```

### Running Locally
```bash
# After building
cd deploy
java -cp xlogo-main.jar:lib/* xlogo.Logo

# Or with the full jar
java -jar xlogo-1.0.0beta5.jar path/to/file.lgo
```

### Running in IDE
- Use IntelliJ IDEA with pre-configured `Logo` run configuration
- Debugging: Set breakpoints in Interpreter.java for execution flow
- Editor: Modify Editor.java for syntax highlighting changes

### Common Development Tasks

#### Adding a New Language
1. Add enum entry in `Language.java`
2. Add SVG flag in `resources/flags/`
3. Add GPL translation in `resources/gpl/`
4. Create `language_xx_YY.properties` files
5. Create `primitives_xx_YY.properties` files
6. See DEVNOTES.md for detailed instructions

#### Adding a New Primitive Command
1. Implement in `Interpreter.java` or `Workspace.java`
2. Register in `buildPrimitiveTreemap()` with `new Primitive(key, arity, general, function)`
3. Add localized name in all `primitives_*.properties` files
4. Document in Logo reference manual

#### Making UI Changes
1. Modify `Application.java` for main window layout
2. Modify `Editor.java` for editor customization
3. Use `GridBagPanel` utility for easier layout
4. Update `PreferencesDialog` for new preferences

---

## Performance & Constraints

### Memory Management
- Default JVM heap: 256 MB (configurable via `Launcher`)
- Memory limit stored in `~/.xlogo/xmx.txt`
- Command line override: `java -jar xlogo.jar -memory 512 file.lgo`
- Large recursive procedures may hit stack limits

### Graphics Performance
- Drawing quality settings: NORMAL, HIGH, LOW
- High DPI scaling support (improved in recent reboot)
- 3D rendering via Java3D (GPU-accelerated)
- Turtle graphics: Swing-based 2D rendering

### Supported Platforms
- Windows
- macOS (requires native libraries)
- Linux (requires native libraries and OpenGL support)
- Requires OpenGL for 3D graphics

---

## Code Quality & Areas for Improvement

### Known Issues (From DEVNOTES.md)
1. **No unit tests** - Critical gap
2. **Very long methods** - Especially Interpreter (6500+ LOC)
3. **Inconsistent naming** - French/English mixed
4. **Public fields** - Violates encapsulation
5. **Code duplication** - Many patterns repeated
6. **Dependency management** - Manual jar management, not using Maven/Gradle

### Refactoring Opportunities
- Break LaunchPrimitive's 3671-line switch statement
- Extract command execution into separate classes
- Add comprehensive unit tests
- Standardize naming conventions
- Reduce code duplication with polymorphism

### Recent Improvements
- Migration to GitHub
- Fixed high DPI scaling
- FlatLaf modern UI
- SVG icons and images
- RSyntaxTextArea editor integration
- Java3D and JOGL updates

---

## Useful Commands & Shortcuts

### Building
```bash
ant           # Full build (default)
ant compile   # Compile only
ant clean-deploy  # Clean build directory
```

### Testing/Running
```bash
java -cp deploy/xlogo-main.jar:deploy/lib/* xlogo.Logo
java -jar deploy/xlogo-1.0.0beta5.jar
```

### Git
```bash
git log --oneline | head -10  # See recent commits
git status   # Check modified files
```

### File Organization
- Source code: `/src/xlogo/`
- Libraries: `/lib/` (all third-party jars)
- Examples: `/examples/` (test programs in multiple languages)
- Build output: `/deploy/` (created by Ant)

---

## Links & Resources

### Official Resources
- **GitHub**: https://github.com/jblang/xlogo
- **XLogo Website**: http://xlogo.tuxfamily.org/
- **LogoArts**: http://www.cr31.co.uk/logoarts/

### Libraries & Dependencies
- **FlatLaf**: https://www.formdev.com/flatlaf/
- **RSyntaxTextArea**: https://github.com/bobbylight/RSyntaxTextArea
- **Java3D**: https://github.com/hharrison/java3d-core
- **JOGL**: https://jogamp.org/
- **JavaHelp**: https://github.com/javaee/javahelp

### IDE
- **IntelliJ IDEA**: https://www.jetbrains.com/idea/
- **JDK Downloads**: https://adoptium.net/temurin/releases?version=11

---

## Summary

XLogo is an educational Logo interpreter with a modern Swing UI, extensive language support, and advanced features like 3D graphics and networking. The codebase is transitioning from legacy code to modern Java practices. It uses Apache Ant for building, stores dependencies locally, and lacks automated testing. The interpreter's architecture is centered around a stack-based execution model with a workspace managing procedures and state. Recent work has focused on UI modernization and bug fixes. The project is an excellent opportunity for refactoring, testing, and improving code quality while maintaining its core functionality.
