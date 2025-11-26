# Cycloid Curves
![Cycloid Curves](../../art/400x400/cycloid.png)

All these curves are drawn using a single procedure 'Cycloid". Each curve uses a different multiplying factor of **:**N.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Curve :Angle
 If :Angle > :MaxAngle [Stop]
 Right 1 Forward Sine :N*:Angle
 Curve :Angle+1
End
To Display :N
 # write header info
 SetPC White PenUp SetH 0
 SetPos [-190 180]
 If (:N > 1) [Label List "Hypo_Cycloid :N] [Label List "Epi_Cycloid :N]
End
To Go
 New PenDown
 Make "Curves [
 [5 180] [4 360] [3 180] [2 360] [1.5 720] [1.333 1080]
 [0.666 1080] [0.5 720] [0.333 540] [0.25 1440] [0.2 900] [0.02 4500] ]
 Repeat Count :Curves [
 Make "N Item 1 Item RepCount :Curves
 Make "MaxAngle Item 2 Item RepCount :Curves
 Wash Display :N SetPC Green Home PenDown Curve 0 Wait 60]
End
```
