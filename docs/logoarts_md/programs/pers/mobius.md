# Mobius Band

A Mobius band can be made from a strip of paper. It is given a twist before its ends are glued together. A half twist results in one side and one edge. If it is cut in half (between the red and green lines) two joined loops are formed.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Mobius :Twist
 For [Angle 0 359 5] [
 Home Up :Angle
 Forward 200 Down 90 LeftRoll :Twist*:Angle
 Paddle
 RightRoll :Twist*:Angle Up 90 Back 240 Wait 4]
End
To Paddle
 # draw perpendicular red/green paddle
 Right 90 Back 60 
 SetPW 8 SetPC Red PointStart Dot Pos PointEnd
 SetPW 1
 SetPC Red PenDown LineStart Forward 60 LineEnd PenUp
 SetPC Green PenDown LineStart Forward 60 LineEnd PenUp
 SetPW 8 SetPC Green PointStart Dot Pos PointEnd
 Back 60 Left 90
End
To Go :Twist
 New Perspective CS PenUp
 HideTurtle SetSC Black
 Mobius :Twist/2
 Message [View3D?] View3D
End
```
