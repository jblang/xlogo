# Cube
![Cube](../../art/400x400/cube.png)

A tumblng cube. The new xyz position of each corner is calculated 
from the original starting positions. X is horizontal, Y is vertical 
and Z is depth. Here, the Z position is also used to determine the 
size of the circles, larger when near, smaller for further away.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Init
 Make "AngleX 0
 Make "AngleY 0
 Make "AngleZ 0
 Make "M Count :Color
End
To Draw
 While ["True] [
 Make "N 1
 Make "AngleX :AngleX+5
 Make "AngleY :AngleY+7
 Make "AngleZ :AngleZ+3
 Repeat :M [
 Make "Xd Item :N :ShapeX
 Make "Yd Item :N :ShapeY
 Make "Zd Item :N :ShapeZ
 Make "Zx :Xd*(Cos :AngleZ) - :Yd*(Sin :AngleZ) - :Xd
 Make "Zy :Xd*(Sin :AngleZ) + :Yd*(Cos :AngleZ) - :Yd
 Make "Yx (:Xd+:Zx)*(Cos :AngleY) - :Zd*(Sin :AngleY) - (:Xd+:Zx)
 Make "Yz (:Xd+:Zx)*(Sin :AngleY) + :Zd*(Cos :AngleY) - :Zd
 Make "Xy (:Yd+:Zy)*(Cos :AngleX)-(:Zd+:Yz)*(Sin :AngleX)-(:Yd+:Zy)
 Make "Xz (:Yd+:Zy)*(Sin :AngleX)+(:Zd+:Yz)*(Cos :AngleX)-(:Zd+:Yz)
 Make "Z :Zd + (:Xz+:Yz)
 Make "X (:Xd + (:Yx+:Zx))*((:Z+300)/295)
 Make "Y (:Yd + (:Zy+:Xy))*((:Z+300)/295)
 SetPos List :X :Y
 SetPC Item :N :Color Circle 4+(:Z+200)/50 Make "N :N+1]
 Refresh Wash Wait 15]
End
To 3DShape
 # cube
 Make "ShapeX [80 -80 -80 80 80 -80 -80 80]
 Make "ShapeY [80 80 -80 -80 -80 -80 80 80]
 Make "ShapeZ [80 80 80 80 -80 -80 -80 -80]
 Make "Color [1 1 2 2 3 3 6 6]
End
To Go
 New Animation 3DShape Init Draw
End
```

Try out some of these other 3DShapes. Or write one of your own!

```logo
To 3DShape
 # tetrahedron
 Make "ShapeX [0 -80 80 0]
 Make "ShapeY [-65 -65 -65 65]
 Make "ShapeZ [80 -50 -50 0]
 Make "Color [1 2 3 6]
End
To 3DShape
 # diamond
 Make "ShapeX [80 -80 -80 80 0 0]
 Make "ShapeY [0 0 0 0 113 -113]
 Make "ShapeZ [80 80 -80 -80 0 0]
 Make "Color [2 6 2 6 1 1]
End
```
