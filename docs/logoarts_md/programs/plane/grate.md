# Grate Curve
![Grate Curve](../../art/400x400/grate.png)

Even orders produce the complete Grate curve.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Square :Angle :Width :Height
 Forward :Width Two :Angle :Width :Height-1
End
To Two :Angle :Width :Height
 If :Height < 1 [Stop] # ie if = 0
 Right :Angle Forward 1 Right :Angle Forward :Width Left :Angle
 If :Height > 0 [ Forward 1]
 Left :Angle Forward :Width
 Two :Angle :Width :Height -2
End
To Grate :Order :Angle :Width :Height
 If :Order < 1 [Square :Angle :Width :Height Stop] # ie if = 0
 Right :Angle
 Grate :Order-1 Minus :Angle :Height/4 :Width
 Forward :Height/8
 Grate :Order-1 :Angle :Height/4 :Width
 Forward :Height/8
 Grate :Order-1 Minus :Angle :Height/4 :Width
 Left :Angle
End
To Go :Order
 New
 SetPos [-192 -190] SetPC Brown PenDown
 Grate :Order 90 380 380
End
```

Type **Go order** for example **Go 5** to run.
