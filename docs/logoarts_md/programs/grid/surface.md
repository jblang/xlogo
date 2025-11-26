# 3D surfaces
![3D surfaces](../../art/400x400/surface2.png)

Imagine a very elastic grid. Undeformed 
it appears as a flat lattice of squares. Apply a function and each 
vertex is deformed up or down from its original position.

The grid will show a 3D surface.
The grid has corner co-ordinate values of [1,1] [1,-1] [-1,-1] [-1,1]
You can alter the mesh size of the grid by changing the **:**Size varible shown in red in the Go procedure.

You can also comment out code to draw only horizontal or vertical 
lines.

In the function procedure, comment in just one function equation.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To GridPos :I :J
 Make "ZZ Function :I :J
 SetPC Color :I :J
 Make "XX 24*((5*:I)-(3*:J))
 Make "YY 9*((3*:I)+(5*:J)) + :ZZ -16
 Output List :XX :YY
End
To Function :X :Y
 # Output 0 # flat plane
 # Output 96*(Cos (90* :X))*(Cos (90* :Y))
 # Output 64*(Cos (270* :X))*(Cos (270* :Y))
 # Output 8*Cos 900*Sqrt ((Power :X 2)+(Power :Y 2))
 Output 96*Cos 360*Sqrt ((Power :X 2)+(Power :Y 2))
End
To DrawGrid
 For (List "L 1 Minus 1 Minus 2/:Size) [PenUp
 For (List "J 1 Minus 1 Minus 2/:Size) [
 SetPos GridPos :L :J PenDown] PenUp # vertical lines
 For (List "I Minus 1 1 2/:Size) [
 SetPos GridPos :I :L PenDown]] # horizontal lines
End
To Color :I :J
 # shade green
 Make "Green 163-46*(:I + :J)
 Output (List 0 :Green 0)
End
To Go
 New
 GlobalMake "Size 24 # increase (eg 32) for finer grid
 DrawGrid
End
```

A procedure to remove hidden lines will be added later...
