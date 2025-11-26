# C_Curve
![C_Curve](../../art/400x400/c_curve.png)

This is a mathematical curve similar 
to the dragon curve.

The C_Curve calls itself recursivly. There is no need for a parity 
varible, as in the dragon curve.

A C_Curve of order 0 is a straight line.  

The limit for this curve is around order 13 (8192 segments), when 
the lines get too small (size 2).

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Init :Order
 # make Length and Angle global values
 Make "Size 8*Power (SqRt 2) (9-:Order)
 Make "Angle 90
End
To Display :Order
 # write header title and curve level
 SetPC White
 SetPos [-190 184] Label Sentence [C_Curve Order] :Order
End
To C_Curve :Order
 If :Order < 1 [Forward :Size Stop] # ie if = 0
 Right 45
 C_Curve :Order-1
 Left 90
 C_Curve :Order-1
 Right 45
End
To Go :Order
 New Init :Order Display :Order
 SetPos [-94 0] SetPC Green Right 90 PenDown
 C_Curve :Order
 # Right 180 C_Curve :Order # comment in for double C curve
End
```

For an animation of C_Curves 0-11

```logo
To GoX
 New Animation
 For [Order 0 11] [Wash # comment out Wash for overlaid curves
 Init :Order Display :Order
 SetPos [-94 0] SetPC Green Right 90 PenDown
 C_Curve :Order
 # Right 180 C_Curve :Order # comment in for double C curve
 Refresh Wait 80 PenUp Home]
End
```

- <a href="../../lgo/anim/chaos.jnlp">On-Line</a></li>
<li><a href="#">File</a></li>
[Gallery](../../pict/gall5/dragon.md)
[?](../../ipt/top/inst.md)
