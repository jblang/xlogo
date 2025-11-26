# Spinning Squares
![Spinning Squares](../../art/400x400/squares2.png)

A square drawn many times. Each time, slightly smaller, with a 4 degree rotation and color change. The corners of the squares appear prominent as a spiral.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To SpinSquare :Side
 If :Side < 12 [Stop]
 SetPC Hue 1.4*:Side
 Square :Side
 Left 4
 SpinSquare :Side-8
End
To Hue :Theta
 # Output RGB hue list from angle :Theta
 Make "Red Round 127.5*(1+Sin :Theta)
 Make "Green Round 127.5*(1+Sin (:Theta+120))
 Make "Blue Round 127.5*(1+Sin (:Theta+240))
 Output (List :Red :Green :Blue)
End
To Square :Side
 # square (side x side) drawn from centre (tp)
 Back :Side/2 Right 90 Back :Side/2 PenDown
 Repeat 4 [
 Forward :Side Left 90]
 PenUp Forward :Side/2 Left 90 Forward :Side/2 # r2c
End
To Go
 New Right 6 SpinSquare 340
End
```
