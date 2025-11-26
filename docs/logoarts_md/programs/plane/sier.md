# Sierpinski Curve
![Sierpinski Curve](../../art/400x400/sier.png)

The Sierpinski curve on its own fills 
a triangle shape. It is drawn four times (with a single join) to 
fill a square plane.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Init :Order
 Make "Size 112 / Power (Sqrt 2) :Order # make Size a global value
 Make "D Power 2 Integer (1+0.5*(:Order+1))
 Make "H Power 2 Integer (1+0.5*(:Order)) Make "H :H-1
 Make "Dist (:H*:Size) + (:D * (:Size / Sqrt 2))
End
To Sierpinski :Order :Parity
 If :Order < 1 [Forward :Size Stop] # ie if = 0
 Right :Parity*45 Sierpinski :Order-1 Minus :Parity Left :Parity*45 
 Forward :Size
 Left :Parity*45 Sierpinski :Order-1 Minus :Parity Right :Parity*45
End
To Go :Order
 New Init :Order
 SetPos List Minus :Dist/2 Minus (:Dist/2-(:Size / Sqrt 2)) PenDown
 Repeat 4 [Sierpinski :Order 1 Right 45 Forward :Size Right 45]
 PenUp Home SetPC DarkRed Pendown Fill # fill curve
End
```

Type **go + order** for example **Go 4** to run.

For an animation of Sierpinski Curves of orders 0-10.

```logo
To GoX
 New Animation
 For [Order 0 10] [Wash # comment out Wash for overlaid curves
 Init :Order
 SetPos List Minus :Dist/2 Minus (:Dist/2-(:Size / Sqrt 2)) PenDown
 Repeat 4 [Sierpinski :Order 1 Right 45 Forward :Size Right 45]
 PenUp Home SetPC DarkRed Pendown Fill SetPC Green 
 # comment out above line for no fills
 Refresh Wait 80 PenUp Home]
End
```
