# Face Cubes

Recursion is used to build a series of smaller cubes on each cube face.  

This uses a 'parasol' technique to create each cube
face polygon.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Cube :Side
 For [P 0 2] [
 For [Q 0 1] [
 Parasol :Side 1+Random 16
 Up 180]
 Up 90 Left 90]
End
To Parasol :Side :Col
 Forward :Side/2 Down 90
 If :Side > 100 [Cube :Side/2]
 SetPC :Col Square3D :Side
 Up 90 Back :Side/2
End
To Square3D :Side
 # square (side x side) drawn from centre (tp)
 Back :Side/2 Right 90 Back :Side/2 PenDown
 PolyStart Repeat 4 [Forward :Side Left 90] PolyEnd
 PenUp Forward :Side/2 Left 90 Forward :Side/2 # return to centre
End
To Go
 New Perspective CS PenUp
 HideTurtle SetSC Black Left 12
 Cube 300 Wait 30
 Message [View3D?] View3D
End
```
