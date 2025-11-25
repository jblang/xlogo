# Primitive Extraction Progress

## Completed Work (Commit 0fe009c)

Successfully extracted **173 primitives** from Interpreter.java into 10 modular classes:

| Class | Primitives | Description |
|-------|------------|-------------|
| MathPrimitives | 31 | Trigonometry, arithmetic, random numbers |
| ColorPrimitives | 17 | Color conversion and manipulation |
| Primitives3D | 22 | 3D graphics, camera, lighting |
| TimePrimitives | 6 | Date/time operations |
| SoundPrimitives | 9 | Audio playback and MIDI |
| FilePrimitives | 14 | File I/O and directory operations |
| ListPrimitives | 17 | List/word manipulation |
| WorkspacePrimitives | 25 | Procedures, variables, tracing |
| UIPrimitives | 28 | Text output, keyboard/mouse, GUI components |
| NetPrimitives | 4 | TCP client/server communication |

**Impact:** Reduced Interpreter.java from 5,480 to 3,259 lines (40% reduction)

## Remaining Work

### 1. DrawingPrimitives (~88 primitives) - PAUSED

Drawing primitives remain in Interpreter.java. These are tightly coupled to DrawPanel.

**Full list** (from analysis):
```
startAnimation, arc, drawBothAxes, getAxisColor, back, changeDirectory, circle,
clearScreenWrap, distance, dot, getDrawingQuality, eraseTurtle, fenceTurtle, fill,
fillPolygon, fillZone, findColor, getFontJustify, getLabelFont, getFontSize, forward,
drawGrid, isGridEnabled, getGridColor, heading, hideTurtle, home, label, getLabelLength,
left, loadImage, penColor, penDown, isPenDown, penErase, penPaint, penReverse,
getPenShape, penUp, getPenWidth, position, refresh, right, saveImage, screenColor,
getImageSize, setAxisColor, setDrawingQuality, setFontJustify, setLabelFont,
setFontSize, setGridColor, setHeading, setPenColor, setPenShape, setPenWidth, setPos,
setScreenColor, setScreenSize, setShape, setTurtle, setTurtlesMax, setX, setXY, setY,
setZoom, getShape, showTurtle, stopAnimation, disableAxes, disableGrid, towards,
getActiveTurtle, getTurtles, getTurtlesMax, isVisible, wash, windowTurtle, wrapTurtle,
getX, drawXAxis, isXAxisEnabled, getY, drawYAxis, isYAxisEnabled, getZ, getZoneSize,
getZoom
```

**Why paused:** Many of these methods directly access DrawPanel fields and use helper methods. Extraction would require:
- Careful dependency analysis (which methods need DrawPanel access)
- Possibly passing DrawPanel as parameter or exposing it via PrimitiveGroup
- Large extraction effort (~88 methods to move)

**Approach when resuming:**
- Could use automated extraction (Python/sed script) to speed up the mechanical work
- Focus on getting the pattern right for the first few, then automate the rest
- Test frequently during extraction

### 2. ControlPrimitives (~32 primitives) - NOT STARTED

Control flow primitives remain in Interpreter.java. These are tightly coupled to the interpreter execution loop.

**Examples:** loops (repeat, while, for, forEach), conditionals (if, ifelse), procedure calls, etc.

**Why they remain:** These primitives manipulate the instruction buffer, loop stack, and control flow directly. They're core to the interpreter's execution model.

**Recommendation:** May want to keep these in Interpreter.java permanently, as they're fundamental to the interpreter loop.

## Established Pattern

### Architecture
```java
// Base class (PrimitiveGroup.java)
public abstract class PrimitiveGroup {
    protected final Interpreter interpreter;
    protected final Application app;
    protected final Kernel kernel;

    protected PrimitiveGroup(Interpreter interpreter) {
        this.interpreter = interpreter;
        this.app = interpreter.app;
        this.kernel = interpreter.kernel;
    }

    public abstract List<Primitive> getPrimitives();
    // ... helper methods ...
}

// Concrete implementation
public class MathPrimitives extends PrimitiveGroup {
    public MathPrimitives(Interpreter interpreter) {
        super(interpreter);
    }

    @Override
    public List<Primitive> getPrimitives() {
        return List.of(
            new Primitive("math.abs", 1, false, this::abs),
            // ...
        );
    }

    void abs(Stack<String> param) {
        // implementation
    }
}

// Registration in Interpreter constructor
this.primitiveGroups = List.of(
    new MathPrimitives(this),
    new ColorPrimitives(this),
    // ...
);
```

### Helper Methods in PrimitiveGroup

As primitives were extracted, helper methods were added to PrimitiveGroup base class:
- `setReturnValue()`, `pushResult()`, `delay()`
- `getList()`, `getWord()`, `isList()`, `formatList()`
- `getFinalList()`, `numberOfElements()`, `item()`, etc.
- `getAllProcedures()`, `getAllVariables()`, etc.
- `primitive2D()`, `primitive3D()` - for mode checking

### Extraction Process

1. Create new `*Primitives.java` class extending `PrimitiveGroup`
2. Override `getPrimitives()` with `List.of(new Primitive(..., this::method), ...)`
3. Copy method implementations from Interpreter.java
4. Add new class to `primitiveGroups` list in Interpreter constructor
5. Remove primitives from inline `primitives` list in Interpreter.java (add comment tracking what was moved)
6. Remove method implementations from Interpreter.java
7. Add any needed helper methods to PrimitiveGroup base class
8. Build with `mvn compile -q` to verify

## Technical Notes

### Challenges Encountered

1. **Import path error** - UIPrimitives initially had wrong import paths:
   - Wrong: `xlogo.gui.components.GuiButton`
   - Correct: `xlogo.kernel.gui.GuiButton`
   - Fixed by using `Glob` to find actual locations

2. **savedWorkspace field** - NetPrimitives needed access to `interpreter.savedWorkspace`
   - Solution: Made field public or accessed via `interpreter.savedWorkspace`
   - Pattern: Some primitives legitimately need interpreter fields beyond app/kernel

3. **List vs control primitives** - Some list operations like `sentence` are actually control primitives
   - Careful reading of primitive keys needed during extraction
   - Not everything with "list" in the name goes in ListPrimitives

### Build Verification

All extractions verified with:
```bash
mvn compile -q
```

Warnings about `sun.misc.Unsafe` are expected (from Maven/Guice, not our code).

## Next Steps

When ready to continue:

1. **Option A: Extract DrawingPrimitives**
   - Largest remaining group
   - Would further reduce Interpreter.java significantly
   - May want to analyze DrawPanel coupling first
   - Consider automated extraction approach

2. **Option B: Leave as-is**
   - Already achieved 40% reduction
   - Remaining primitives are tightly coupled by design
   - May not be worth the extraction effort

3. **Option C: Incremental approach**
   - Extract just the drawing primitives with minimal DrawPanel coupling first
   - Leave the deeply coupled ones in Interpreter.java
   - Balance between modularity and pragmatism

## References

- Commit: `0fe009c` - "Refactor Interpreter: Extract 173 primitives into modular classes"
- Drawing primitives list: `/tmp/drawing_methods.txt` (88 methods)
- Base pattern: `src/xlogo/kernel/PrimitiveGroup.java`
- Example: `src/xlogo/kernel/MathPrimitives.java`
