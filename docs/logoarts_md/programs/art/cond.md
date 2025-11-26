# Droplet Condensation
![Droplet Condensation](../../art/400x400/condensation.png)

Circles are drawn one at a time. 
They expand until they touch another circle, or the box edge. They 
cannot start within another circle. The result is a collection of 
touching circles, resembling condensation water droplets.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Probe
 Make "Centre RanPoint
 Make "A "OK Make "Radius 0
 Repeat 40 [
 If :A = "Abort [Stop]
 Make "Num :Radius + 24
 Make "Angle 360/ :Num
 Repeat :Num [
 SetPos :Centre Right :Angle
 Forward :Radius
 #SetPC Green PenDown Forward 1 PenUp
 If Test > 64 [Make "A "Abort Stop] ]
 Make "Radius :Radius + 1]
 If :Radius > 6 [SetPos :Centre Blob :Radius-2]
End
To Blob :Radius
 SetPC [51 51 51] Circle :Radius
 SetPC Blue Fill
 SetH 315 Forward :Radius/2
 SetPC Cyan SetPW :Radius/3
 Dot Pos SetPW 1
End
To RanPoint
 Output List (Random 390)-195 (Random 390)-195
End
To Test
 Output Item 3 FindColor Pos
End
To Square :Side
 # square (side x side) drawn from centre (tp)
 Back :Side/2 Right 90 Back :Side/2 PenDown
 Repeat 4 [
 Forward :Side Left 90]
 PenUp Forward :Side/2 Left 90 Forward :Side/2 # r2c
End
To Go
 New
 SetPC Gray Square 400
 Repeat 5000 [Probe]
End
```
