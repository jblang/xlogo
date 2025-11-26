# Segment

The five segments are identical but appear to be slightly different sizes due to their orientations.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Segment
 # Draw a single segment
 Arc 150 Heading-20 Heading+20
 Left 20 Forward 150 PenDown Forward 50 PenUp Back 200
 Right 40 Forward 150 PenDown Forward 50 PenUp Back 200 Left 20
 Forward 55 Arc 150 Heading-27 Heading+27
End
To Go
 New SetPos [-60 -260] Right 27.25
 Repeat 5 [
 SetPC Cyan Segment
 SetPC [0 128 128] Forward 115 Fill
 Make "myHeading Heading
 SetH 0 SetPC White Label RepCount
 SetH :myHeading
 Back 115 Left 7.25]
End
```
