# Flowers
![Flowers](../../art/400x400/flowers.png)

Aulthough initially this program 
looks over long, it demonstrates good use of procedures. No variables 
or arguments are used.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Arch
 # draw single 90 degree arc
 Repeat 18 [Forward 4 Right 5]
End
To Oval
 # draw single oval shape (tp)
 Left 45 Arch Right 90
 Arch Right 135 Wait 6
End
To Base
 # draw single base (tp)
 Right 90 Back 30 PenDown
 SetPC LightGray Oval
 PenUp Forward 30 Left 90
End
To Leaf
 # draw single leaf (tp)
 SetPC Green
 Forward 24 Oval Back 24
End
To Section
 # draw short stem and 2 leaves
 Right 45 Leaf Left 45 Forward 24
 Left 45 Leaf Right 45 Forward 24
End
To Stem
 # draw 3 sections
 SetPC Green Forward 48
 Repeat 3 [Section]
 PenUp Forward 64 PenDown
End
To Petal
 # draw single petal (tp)
 SetPC Red Oval
End
To Flower
 # draw flower head (tp)
 Repeat 18 [
 SetPC Yellow Forward 4
 Petal
 PenUp Back 4 Right 20 PenDown]
End
To Plant
 # draw flower (tp)
 Stem Flower
 PenUp Back 256 PenDown
End
To Bunch
 # draw 3 flowers
 Left 24 PenDown
 Repeat 3 [Plant Right 24]
End
To Go
 # CS and go to start point
 New Back 150
 Base Bunch
End
```

Note (tp) identifies a procedure as transparent, 
ie it leaves the position and heading of the turtle unchanged. It 
is much easier to use transparent procedures as building blocks.
