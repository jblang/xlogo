# Neon

A central square can be seen, tinted red, aulthough only the rings are colored.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Rings
 For [Radius 20 60 20] [
 SetPC White rArc 270 :Radius
 SetPC Red rArc 90 :Radius]
End
To rArc :Angle :Radius
 # clockwise arc drawn relative to turtle heading
 Arc :Radius Heading :Angle+Heading Right :Angle
End
To Go
 New SetPW 2
 Repeat 4 [
 Home Right 45 + RepCount *90
 Forward 120 Left 135 Rings]
End
```
