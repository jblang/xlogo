# Walk of Arcs
![Walk of Arcs](../../art/400x400/arcstep.png)

The turtle draws a random series of 90 degree arcs. When the edge of 
the screen is reached the next curve is drawn in a different pen color.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To OnStage?
 # check turtle within window edges
 If (Or X<-190 X>190 Y<-190 Y>190) [Output False] [Output True]
End
To RightArc :Angle :Radius
 # arc drawn relative to turtle position, angle & radius 
 positive
 PenUp Left 90 Back :Radius
 Arc :Radius Heading 
 Heading+:Angle
 Right :Angle Forward :Radius Right 90 PenDown
End
To Go
 New
 Repeat 4 [
 PenUp Home SetH RepCount*90 SetPC RepCount PenDown
 While [OnStage?] [
 Make "Parity Pick [-1 1]
 RightArc :Parity *90 12 Wait 2] ]
End
```
