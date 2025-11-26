# XLogo Arts - Markdown Documentation

This directory contains Markdown versions of the XLogo Arts HTML documentation, converted for easier reading and maintenance.

## Structure

```
logoarts_md/
├── index.md              # Main entry point
├── art/                  # Image assets (1,209 files)
│   ├── 100x100/           # 100x100 thumbnails (gallery 1)
│   ├── 100x100/           # 100x100 thumbnails (gallery 2)
│   └── ...              # Other image galleries
├── programs/             # Program documentation (200+ examples)
│   ├── index.md         # Programs overview with category links
│   ├── anim/            # Animation programs
│   ├── art/             # Art programs
│   ├── fractal/         # Fractal programs
│   ├── illusion/        # Optical illusion programs
│   ├── l-system/        # L-System programs
│   └── ...              # 22 categories total
├── primitives/          # XLogo command reference (21 categories)
│   ├── index.md         # Primitives overview
│   ├── basic.md         # Basic movement commands
│   ├── drawing.md       # Drawing commands
│   ├── color.md         # Color commands
│   ├── maths.md         # Math functions
│   └── ...              # 21 primitive categories
└── resources/           # Help and reference materials
    ├── index.md         # Resources overview
    ├── glossary.md      # Logo terminology
    ├── prolib.md        # Procedure library
    └── ...              # 21 resource files

```

## Features

- **Clean Content**: All website chrome (headers, navigation, footers) removed
- **Inline Screenshots**: Each program includes its screenshot at the top of the page
- **Organized Categories**: Programs organized into 22 categories with dedicated indexes
- **Working Images**: All 1,209 image files included with correct relative paths
- **Code Examples**: Logo code in properly formatted code blocks
- **Easy Navigation**: Index files at every level for easy browsing
- **Complete Reference**: Full XLogo primitives documentation with examples

## Sections

### Programs (22 categories)
- **Animation** - Animated demonstrations and simulations
- **Art** - Artistic patterns and designs
- **Cellular Automata** - CA simulations
- **Coding** - Programming examples
- **Demo** - Simple demonstrations
- **Dot Plot** - Attractors and chaotic systems
- **Fractal** - Fractal curves and patterns
- **Grid** - Grid-based designs
- **Illusion** - Optical illusions
- **L-System** - Lindenmayer system fractals
- **Multi Turtle** - Multiple turtle programs
- **Perspective** - 3D perspective drawings
- **Plane Filling** - Space-filling curves
- **Polar** - Polar coordinate curves
- **Puzzle** - Puzzles and games
- **Recursion** - Recursive patterns
- **Spiral** - Spiral designs
- **Spirograph** - Spirograph curves
- **Tree** - Tree fractals
- **Walk** - Random walks

### Primitives (21 categories)
- **Animation** - Animation mode commands
- **Axis & Grid** - Axis and grid display
- **Basic** - Basic turtle movement
- **Booleans** - Boolean operations
- **Colors** - Color functions
- **Drawing** - Drawing commands
- **Files** - File operations
- **Font & Text** - Text formatting
- **General** - General commands
- **GUI** - GUI interface
- **Lists** - List operations
- **Loops** - Loop structures
- **Maths** - Mathematical functions
- **Multiple Turtles** - Multi-turtle commands
- **Network** - Network operations
- **Perspective** - 3D perspective mode
- **Preferences** - User preferences
- **Sound** - Sound and music
- **Symbols** - Symbol operators
- **Time & Date** - Time and date functions
- **User Input** - User interaction

### Resources (21 files)
- Glossary of Logo terms
- Procedure libraries
- Tutorials and guides
- Book references

## Usage

Start with `index.md` for the main overview, or jump directly to:
- `programs/index.md` - Browse all program categories
- `primitives/index.md` - Look up XLogo commands and syntax
- `resources/index.md` - View help resources

All image links use relative paths and work correctly when viewing from any markdown reader or static site generator.

## Original Source

Converted from HTML files in `docs/logoarts/`
- Original site: logoarts.co.uk (archived)
- Created for XLogo Java application

## Statistics

- **277 Markdown files**
  - 200+ program examples
  - 21 primitive reference pages
  - 21 resource documents
- **1,209 image files**
- **14 MB total size**
