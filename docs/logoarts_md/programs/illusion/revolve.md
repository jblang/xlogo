# Revolve

Focus your eyes on the central dot. Move your head slightly backwards and forwards, the two outer rings appear to revolve.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Rhombus
 SetPC Gray Right 30 Forward 14
 Left 60 Forward 14
 SetPC White Left 120 Forward 14
 Left 60 Forward 14 Left 150
End
To Go
 New SetSC LightGray SetPC Black
 SetPW 4 Dot Pos SetPW 1
 Repeat 36 [
 Forward 180 Right 120 PenDown
 Rhombus PenUp Left 120 Back 180 Left 10]
 Repeat 30 [
 Forward 135 Left 300 PenDown
 Rhombus PenUp Right 300 Back 135 Left 12]
End
```
