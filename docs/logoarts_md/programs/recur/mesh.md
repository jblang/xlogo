# Mesh
![Mesh](../../art/400x400/mesh.png)

This program constructs a regular grid using recursive methods. Meshes of 3, 4 and 6 are triangular, square and hexagonal grids. The greater the order the more detailed the grid.  

**Go 5 3** produces a similar diagram to the [Sierpinski 
triangle](../dotplot/sier.md).

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Gridd :Order :Size :Mesh
 If :Order < 1 [PenDown Forward :Size PenUp Back :Size Stop]
 Forward :Size/2
 Repeat :Mesh [Gridd :Order-1 :Size/2 :Mesh Left 360/:Mesh]
 Back :Size/2
End
To Go :Order :Mesh
 New Back 192
 Make "Size 384
 Gridd :Order :Size :Mesh
End
```

Type **Go order mesh** (eg **go 2 4**) to run.
