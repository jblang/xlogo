# Zoom Grid

The rectangles appear to be continuously moving out of the grid.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Gridd :M :N
 # return X Y screen position
 Make "X (:N*:CellSize) -180
 Make "Y (:M*:CellSize) -180
 Output List :X :Y
End
To DrawGrid
 # draw M x N array of squares
 For (List "M 5 1 Minus 1) [
 For (List "N 5 1 Minus 1) [
 SetPos Gridd (:M*:M)/5 (:N*:N)/5 Rect 8*:M 8*:N Left 90
 SetXY Minus 1*First Pos Last Pos Rect 8*:N 8*:M Left 90
 SetXY First Pos Minus 1*Last Pos Rect 8*:M 8*:N Left 90
 SetXY Minus 1*First Pos Last Pos Rect 8*:N 8*:M Left 90 ] ]
End
To Rect :Width :Height
 # shaded rectangle (width x height) drawn from centre (tp) and filled
 Back :Height/2 Right 90 Back :Width/2 PenDown
 SetPC Black Forward :Width Left 90 Forward :Height Left 90
 SetPC White Forward :Width Left 90 Forward :Height Left 90
 PenUp Forward :Width/2 Left 90 Forward :Height/2
 SetPC [153 0 204] Fill
End
To Go
 New SetSC [51 204 102] SetPW 2
 Left 90 # change to 270 to zoom in
 Make "CellSize 30
 DrawGrid
End
```

Add the following code for rounded rectangles.

```logo
To Rect :Width :Height
 # shaded pebble shape drawn from centre (tp) 
 and filled
 LocalMake "Diff Abs :Width-:Height
 If :Width > :Height
 [LocalMake "Rad :Height/2 LocalMake "Vert 0 LocalMake "Horiz :Diff]
 [LocalMake "Rad :Width/2 LocalMake "Vert :Diff LocalMake "Horiz 0]
 PenUp Back :Height/2 Right 90 Back :Horiz/2 PenDown
 SetPC Black Forward :Horiz LeftArc 90 :Rad Forward :Vert LeftArc 45 :Rad
 SetPC White LeftArc 45 :Rad Forward :Horiz LeftArc 90 :Rad Forward :Vert LeftArc 45 :Rad
 SetPC Black LeftArc 45 :Rad
 PenUp Forward :Horiz/2 Left 90 Forward :Height/2 # return to centre
 SetPC [153 0 204] Fill
End
To LeftArc :Angle :Radius
 # arc drawn relative to turtle position, angle & radius positive
 PenUp Right 90 Back :Radius Left :Angle
 Arc :Radius Heading Heading+:Angle
 Forward :Radius Left 90 PenDown
End
```
