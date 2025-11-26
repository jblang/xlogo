# Tango
![Tango](../../art/400x400/tango.png)

Here, the turtle draws a random arc, then moves to the end point, turns 180 degrees and continues with another random arc in the opposite color. This is repeated 300 times, producing a tangled shrubbery appearance.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Roam
 Make "Angle 30 + Random 210
 rArc :Angle :Radius
 Forward :Radius Left 180
 Make "PenCol 8+Minus :PenCol SetPC :PenCol # yellow/magenta
 Wait 2
End
To rArc :Angle :Radius
 # clockwise arc drawn relative to turtle heading
 Arc :Radius Heading :Angle+Heading Right :Angle
End
To Go
 New Wrap
 Make "PenCol 2
 Make "Radius 30
 Repeat 300 [Roam]
End
```
