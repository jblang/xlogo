# Peano Curve
![Peano Curve](../../art/400x400/peano.png)

| To draw this curve, the turtle moves through a patch of ground, 
which is divided into 9 squares. Starting at the lower left, it first turns left, then right 
3 times, left 3 times and a final right turn. The turtle emerges 
upper right having moved diagonally through each of the 9 
squares. By recursion, each square can be further divided into 9 smaller 
squares. The turtle ends up passing through every point on 
the patch of ground. |  |

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Init :Level
 GlobalMake "Size (SqRt 2)*(64/(Power 3 :Level))
 PenUp SetPos [-192 -192] SetH 45 Forward :Size
 PenDown # comment out for curved corners
End
To Peano :Level
 If :Level < 0 [Stop]
 Peano :Level-1
 Left90 :Size Peano :Level-1
 Repeat 3 [Rite90 :Size Peano :Level-1]
 Repeat 3 [Left90 :Size Peano :Level-1]
 Rite90 :Size Peano :Level-1
End
To Left90 :Radius
 Forward :Radius Left 90
 Forward :Radius
End
To Rite90 :Radius
 Forward :Radius Right 90
 Forward :Radius
End
To rArc :Angle :Radius
 # clockwise arc drawn relative to turtle heading
 Arc :Radius Heading :Angle+Heading Right :Angle
End
To Go :Level
 New Init :Level Peano :Level
End
To GoX
 New For [Level 0 3] [
 SetPC :Level+1 SetPW 7-(2*:Level)
 Init :Level Peano :Level Wait 30]
End
```

Enter **go 0** for the basic path as in the diagram above.  

Enter **go 1**, **go 2** or**go 3** for increasingly complex 
paths.  

Around **go 4** or **go 5** the turtle fills the screen completely.

Add any pair of procedures for different styles of left and right 90 
degree turns, which produce quite different looking curves.   

**Comment out 
the PenDown in the Init procedure for corners using rArcs.**

```logo
To Left90 :Radius
 # curved left 90 corner
 Right 90 Back :Radius Left 90
 rArc 90 :Radius
 Left 90 Forward :Radius Left 90
End
To Rite90 :Radius
 # curved right 90 corner
 Left 90 Back :Radius
 rArc 90 :Radius
 Forward :Radius Right 90
End
To Left90 :Radius
 # octagon corner
 Forward 0.414*:Radius Left 45
 Forward 0.828*:Radius Left 45
 Forward 0.414*:Radius
End
To Rite90 :Radius
 # octagon corner
 Forward 0.414*:Radius Right 45
 Forward 0.828*:Radius Right 45
 Forward 0.414*:Radius
End
To Left90 :Radius
 # left 45 turn
 Left 45 Forward 1.414*:Radius Left 45
End
To Rite90 :Radius
 # right 45 turn
 Right 45 Forward 1.414*:Radius Right 45
End
To Left90 :Radius
 # anti-curve left 90 corner
 Forward :Radius Left 180
 rArc 90 :Radius
 Forward :Radius
End
To Rite90 :Radius
 # anti-curve right 90 corner
 Forward :Radius Right 90
 rArc 90 :Radius
 Left 90 Forward :Radius
End
```

Paste **one** pair of corners into the XLogo editor.
