# Dice Cube

The challenge here was to create a simple cube with faces labelled correctly 1 to 6, so that opposite faces total 7. This was the best solution. The numerals need to be 1 pixel above the surface, otherwise they randomly disappear as the cube is turned.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Cube :Side
 For [P 0 2] [
 For [Q 0 1] [
 GlobalMake "Col 3*:Q+:P+1 # series 1 2 3 4 5 6
 Parasol :Side
 Up 180]
 Up 90 Left 90]
End
To Parasol :Side
 Forward :Side/2 Down 90
 SetPC :Col Square3D :Side
 SetPC White Text3D :Col
 Up 90 Back :Side/2
End
To Square3D :Side
 # square (side x side) drawn from centre (tp)
 Back :Side/2 Right 90 Back :Side/2 PenDown
 PolyStart Repeat 4 [Forward :Side Left 90] PolyEnd
 PenUp Forward :Side/2 Left 90 Forward :Side/2 # return to centre
End
To Text3D :Label
 # label text 1 pixel above surface (tp)
 Right 90 Back (LabelLength :Col) /2 Left 90 Back FontSize/1.8
 Up 90 Forward 1 Down 90
 TextStart Label :Col TextEnd
 Up 90 Back 1 Down 90 # 1 pixel above face
 Forward FontSize/1.8 Right 90
 Forward (LabelLength :Col) /2 Left 90 # return to centre
End
To Go
 New Perspective CS PenUp
 HideTurtle SetSC Black SetFS 280 Left 12
 Cube 300 Wait 30
 Message [View3D?] View3D
End
```
