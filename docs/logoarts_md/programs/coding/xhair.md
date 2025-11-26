# X-Hair

Add this procedure to any other 
program in XLogo. Enter **GoX** to run. It will produce an on-screen X-Hair. 
Useful for measuring point to point distances.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To GoX
 New SetPC White SetPos [-190 184] Label 
 "X-Hair
 PX Make "OldX 201 Make "OldY 202
 Forever [
 Make "MouseX Item 1 MousePos
 Make "MouseY Item 2 MousePos
 While [Or (Not :MouseX = :OldX) (Not :MouseY = :OldY)] [
 PenUp SetXY :OldX Minus 200
 PenDown SetXY :OldX 200
 PenUp SetXY Minus 200 :OldY
 PenDown SetXY 200 :OldY
 PenUp SetPC ScreenColor SetPos [-190 -190] Label List :OldX :OldY
 PenUp SetPC White SetPos [-190 -190] Label List :MouseX :MouseY
 PenUp SetXY :MouseX Minus 200
 PenDown SetXY :MouseX 200
 PenUp SetXY Minus 200 :MouseY
 PenDown SetXY 200 :MouseY
 Make "OldX :MouseX
 Make "OldY :MouseY]]
End
```
