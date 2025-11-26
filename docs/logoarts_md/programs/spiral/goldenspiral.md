# Golden Ratio Spiral
![Golden Ratio Spiral](../../art/400x400/goldenratio.png)

This is a famous diagram showing 
a rectangle of golden ratio divided into an infinite series of squares. 
A logarithmic spiral can be drawn through the squares. Here, the spiral 
is approximated by a quater circle arc in each square.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Section :Side
 If :Side < 2 [Stop] # stop when side too short
 SetPC Green Square :Side # comment out for curve only
 SetPC Red Curve :Side # comment out for squares only
 Section :Side*0.618 # do again with smaller side
End
To Square :Side
 Repeat 4 [Forward :Side Right 90]
End
To Curve :Side
 Make "Step :Side*0.0524 # heading correction pi/60
 Right 1.5
 Repeat 30 [Forward :Step Right 3 Wait 4] # 90 degree arc in 30 steps
 Left 1.5
End
To Lines
 SetPC [0 0 128] # draw crossed lines in blue
 PenUp SetPos [-185 88]
 PenDown SetPos [184 -140]
 PenUp SetPos [184 88]
 PenDown SetPos [43 -140] Wait 30
End
To Go
 New Lines SetPos [-185 -140]
 Section 228 # begin with square of side 228
End
```

Also see [Plastic Number](plastic.md).
