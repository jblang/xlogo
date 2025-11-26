# HopAlong
![HopAlong](../../art/400x400/hop100/1.png)

This plots random Hopalong functions. Each is mapped from the equation:

Xn+1 = Yn - Sign Xn * Sqrt Abs (B*Xn-C)  

Yn+1 = A - Xn

where the new values of X and Y are determined from the previous X and Y values along with 3 constants A, B and C.

The Optimise procedure is not stricly necessary. It determines the maximum and minimum X and Y values and from this calculates the scale factor and offset values which allow the plot to be displayed so that it fills the drawing area.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Init
 Make "A 0.1*((Random 199)-99)
 Make "B 0.1*((Random 199)-99)
 Make "C (Random 19)-9
 Make "Cycle 6000
End
To Display :A :B :C :Cycle
 SetPC White
 SetPos [-190 184] Label "Hopalong
 SetXY Minus 194 Minus 196 Label (List :A :B :C)
 SetX 194-LabelLength :Cycle Label :Cycle
End
To Fit2Screen :A :B :C :Cycle
 Make "X 0 Make "Y 0
 Make "Xmax 0 Make "Xmin 0
 Make "Ymax 0 Make "Ymin 0
 Repeat :Cycle [HopAlong :A :B :C
 If :X > :Xmax [Make "Xmax :X]
 If :X < :Xmin [Make "Xmin :X]
 If :Y > :Ymax [Make "Ymax :Y]
 If :Y < :Ymin [Make "Ymin :Y] ]
 Make "Xsize :Xmax + Abs :Xmin
 Make "Scale 380/:XSize
 Make "Xcent Minus (:Xmax + :Xmin) / 2
 Make "Ycent Minus (:Ymax + :Ymin) / 2
End
To Hop :A :B :C :Cycle
 Make "X 0 Make "Y 0
 Repeat 180 [
 SetPC Hue2 RepCount
 Repeat Integer :Cycle/180 [HopAlong :A :B :C
 Dot List :Scale*(:X+:XCent) :Scale*(:Y+:YCent) ] ]
End
To HopAlong :A :B :C
 Make "Xnew :Y-((Sign :X)*Sqrt (Abs (:B*:X-:C)))
 # Make "Xnew :Y-((Sign :X)*Abs((Sine :X)*(Cos :B)+:C-:X*Sine (:A+:B+:C))) # 3-ply fractal
 Make "Y :A-:X
 Make "X :Xnew
End
To Sign :Num
 # return sign (-1 or 1) of number
 If :Num < 0 [Output Minus 1] [Output 1]
End
To Hue2 :Theta
 # Output RGB hue list from angle :Theta
 Make "Red Abs 255*Sin :Theta
 Make "Green Abs 255*Sin (:Theta+120)
 Make "Blue Abs 255*Sin (:Theta+240)
 Output (List :Red :Green :Blue)
End
To Go
 New Repeat 100 [
 Init Fit2Screen :A :B :C :Cycle
 Wash
 Hop :A :B :C :Cycle
 Display :A :B :C :Cycle Wait 300]
End
```

Comment in the second function to draw a series of 'Three Ply' orbit fractals. Best if A, B and C are all set to Random 100.

To draw just one curve use the following procedure:

```logo
To GoX :A :B :C :Cycle
 New
 Fit2Screen :A :B :C :Cycle
 Hop :A :B :C :Cycle
 Display :A :B :C :Cycle
End
```
