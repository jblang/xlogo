# Spiral Arcs
![Spiral Arcs](../../art/400x400/arcwave.png)

A series of arcs drawn in a spiral formation. The wait time slow the 
drawing and allow the progress of the turtle to be seen.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Wave :Size
 If :Size <2 [Stop] # stop when size too small
 PenUp Back :Size PenDown
 rArc 10 :Size Forward :Size Wait 10
 Wave :Size/1.1
End
To rArc :Angle :Radius
 # clockwise arc drawn relative to turtle heading
 Arc :Radius Heading :Angle+Heading Right :Angle
End
To Go
 New SetPos [192 -170] SetH 270 PenDown
 Forward 384 Wave 384
End
```
