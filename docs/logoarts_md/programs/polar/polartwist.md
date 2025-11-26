# Twisted Rose Curves
![Twisted Rose Curves](../../art/400x400/trose2.png)

In spiral and rose curve polar 
plots, the Angle increased uniformly. By making the Angle dependent 
on Radius and Sine Angle, we can add a twist to the rose petals.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To PtoR :RadDist :Theta
 # convert polar to rectangular co-ordinates
 LocalMake "X :RadDist *Sin :Theta
 LocalMake "Y :RadDist *Cos :Theta
 Output List :X :Y
End
To Spiral :Ang
 # Twisted Rose Curve
 Make "Radius 180*Sin(6*:Ang)
 Make "Angle :Ang + (:Radius/4)
 Output PtoR :Radius :Angle
End
To Go
 New SetPC Yellow
 For [Ang 0 360] [
 SetPos Spiral :Ang PenDown]
End
```

Try out the following ...

```logo
To Spiral :Angle
 # Twisted Rose Curve2
 Make "Radius 180*Sin(4*:Angle)
 Make "Angle :Angle + :Radius
 Output PtoR :Radius :Angle
End
```

Also, see an animated sequence of 24 [twisted 
rose curves](rosecurves.md).

Or even simply

```logo
To Spiral :Angle
 # Twisted Rose Simple
 Make "Radius 160*Sin :Angle
 Make "Angle :Angle + :Radius
 Output PtoR :Radius :Angle
End
```
