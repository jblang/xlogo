# Hénon Curves
![Hénon Curves](../../art/400x400/henon.png)

Each Hénon curve is generated 
from a pair of quadratic equations. One gives the new value of X, 
the other of Y. Both new values of X and Y are derived from previous 
values of X and Y, and are plotted as a single pixel dot.  

Xnew = 1 - A*X2 + Y  

Ynew = B*X

For first Hénon curve, make A=1.4 and B=0.3.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Init
 Make "X 0 Make "Y Minus 0.2
 Make "XScale 128 Make "YScale 440
 Make "Alpha 1.4 Make "Beta 0.3
End
To AngCol :Theta
 Make "Red Abs 255 *Cos (:Theta)
 Make "Gre Abs 255 *Cos (:Theta + 120)
 Make "Blu Abs 255 *Cos (:Theta + 240)
 Output (List :Red :Gre :Blu)
End
To Hénon
 Make "Xnew 1 - :Alpha*(Power :X 2) + :Y
 Make "Y :Beta * :X
 Make "X :Xnew
 Dot List (:X*:XScale) (:Y*:YScale)
End
To Go
 New Init
 Repeat 4000 [
 # SetPC AngCol Integer RepCount/10 # comment in for color
 Hénon]
End
```

Replace the Init procedure for a second type of Hénon curve.

```logo
To Init
 Make "X 0 Make "Y 0
 Make "XScale 40 Make "YScale 40
 Make "Alpha 0.2 Make "Beta 0.9991
End
```
