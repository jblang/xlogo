# Spirals
![Spirals](../../art/400x400/arch.png)

All these spirals are drawn using polar co-ordinates. See [Spirals](../spiral/spirals.md) for spirals drawn using calculation of curvature.  

This draws a ten turn Archimedes spiral.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To P2R :Radius :Angle
 Make "X :Radius * Cos :Angle
 Make "Y :Radius * Sin :Angle
 Output List :X :Y
End
To Spiral :Angle
 # Archimides Spiral
 Make "Radius :Angle/20
End
To Go
 New SetPC Yellow
 For [Angle 0 3600] [
 Spiral :Angle
 SetPos P2R :Radius :Angle PenDown]
End
```

For Fermats spiral, Radius is proportional to the square root of 
the Angle. Copy and paste to update the spiral procedure.

```logo
To Spiral :Angle
 # Fermats Spiral
 Make "Radius 3*Power :Angle 0.5
End
```

An equiangular spiral grows very quickly.

```logo
To Spiral :Angle
 # Equiangular Spiral
 Make "Radius Power 1.002 :Angle
End
```

This adds a small ripple to an Archimedes spiral, giving an illusion 
of petals. You will need to increase the number of steps to 8860.

```logo
To Spiral :Angle
 # Archimides Daisy
 Make "Offset Sin 7.05*:Angle
 Make "Radius :Angle/52 + (Power (:Angle/2000) 2) * :Offset
 SetPC ( List 0 (Round 127.5 * (1+:Offset)) 255 )
End
```
