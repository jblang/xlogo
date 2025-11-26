# Cardioid Curves
![Cardioid Curves](../../art/400x400/cardioid.png)

A series of circles is drawn, all of which touch the central horizontal axis. Default is 48 circles.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Go :Order
 New
 Repeat Abs :Order [
 Forward 90 Circle (Absolute Last Pos)
 Back 90 Right (360/:Order) Wait 8]
End
```

A series of circles is drawn, all of which touch the central point (0 40). Default is 30 circles.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Go :Order
 New
 Forward 40 Make "Radius 70
 Repeat Abs :Order [
 Forward :Radius
 Circle (Distance List 0 40+:Radius)
 Back :Radius Right (360/:Order) Wait 8]
End
```
