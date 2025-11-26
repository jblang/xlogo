# Kanisza's Triangle

Named after Professor Gaetano Kanizsa of the University of Trieste 
who first introduced them. The triangle appears to float above the background. 
Orders of 4 or 5 can also be tried.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Dart
 # Draw dart shape, TP
 Right :Angle/2 PenDown Back :Size PenUp Forward :Size
 Left :Angle PenDown Back :Size PenUp Forward :Size Right :Angle/2
End
To Arch :Angle :Radius
 # symmetrical arc drawn relative to turtle heading
 Arc :Radius Heading-:Angle/2 
 Heading+:Angle/2
End
To Go :Order
 New SetPW 2
 Make "Angle 180-(360/:Order)
 Repeat :Order [
 Forward 140 Dart :Angle 30 Arch 360-:Angle 30
 Forward 10 Fill Back 10
 SetPC Black Dart :Angle 30 SetPC Green
 Back 140 Left 180/:Order
 Forward 140 Dart :Angle Item :Order [0 0 78 56 44 
 32]
 Back 140 Left 180/:Order]
End
```

Try **Go 4** and **Go 5** too.
