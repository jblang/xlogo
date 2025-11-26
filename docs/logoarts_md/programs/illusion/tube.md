# Tube

The four concentric rings of clam shapes appear to distort and move. A random color is used for the screen and clam shapes.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Ring :Angle :Dist :Head :Size
 # draw concentric rings of clam shapes
 Home SetH :Angle SetPW :Size/10
 Forward :Dist SetH :Head Clam :Size
 SetPC :FillCol Fill
End
To Clam :Size
 # optical clam shape drawn from centre (tp)
 SetPC White Back :Size/3 Arch 112 0.6*:Size
 Forward :Size/3 Left 180
 SetPC Black Back :Size/3 Arch 112 0.6*:Size
 Forward :Size/3 Left 180
End
To Arch :Angle :Radius
 # symmetrical arc drawn relative to turtle heading
 Arc :Radius Heading-:Angle/2 
 Heading+:Angle/2
End
To Go
 New SetPW 3
 SetSC Pick [ [102 204 51] [51 204 102] [204 204 0] ]
 Make "FillCol Pick [ [153 102 0] [128 0 255] [0 128 255] ]
 For [Angle 0 360 10] [
 Ring :Angle 180 90 30
 Ring :Angle 150 35 25
 Ring :Angle 122 325 20
 Ring :Angle 100 270 15]
End
```
