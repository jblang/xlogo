# Cube Octahedron

By pushing out the cube faces and rotating each by 45 degrees a Cube Octagon is created. Opposite faces are the same color. This shows how to develop a simple shape into a more complex one.

The procedure Square3D draws each square. If RepCount is even, a ball is added to the vertex.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Cube :Side
 Repeat 3 [
 SetPC RepCount Repeat 2 [
 Forward :Side/1.4 Down 90
 Left 45 Square3D :Side Right 45
 Up 90 Back :Side/1.4
 Up 180]
 Up 90 Left 90]
End
To Square3D :Side
 # square (side x side) drawn from centre (tp)
 Back :Side/2 Right 90 Back :Side/2 PenDown
 Repeat 4 [
 SetPW 1 LineStart
 Forward :Side LineEnd Left 90 Wait 12
 If Even? RepCount [SetPW 24 PointStart Forward 0 PointEnd] ]
 PenUp Forward :Side/2 Left 90 Forward :Side/2 # r2c
End
To Even? :Num
 # return 'true' if Num even, else return 'false'
 If (Mod :Num 2)=0 [Output "True] [Output "False]
End
To Go
 New Perspective CS PenUp
 HideTurtle SetSC Black
 Cube 250 Wait 30
 Message [View3D?] View3D
End
```
