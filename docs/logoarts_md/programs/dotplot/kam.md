# Kam Torus
![Kam Torus](../../art/400x400/kam.png)

This draws a Kam Torus. Invented 
by three people Kalmogorov, Arnold and Moser, hence KAM.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Kam
 Make "Angle 74.5
 For [Orbit 0.2 1.5 
 0.05] [
 Make "X :Orbit/3 Make "Y :Orbit/3
 Repeat 300 [
 Make "X2Y (Power :X 2)-:Y
 Make "Xnew :X * (Cos :Angle) + :X2Y * Sin :Angle
 Make "Y :X * (Sin :Angle) - :X2Y * Cos :Angle
 Make "X :Xnew
 Dot List :X*300 :Y*300 ] ]
End
To Go
 New Kam
End
```
