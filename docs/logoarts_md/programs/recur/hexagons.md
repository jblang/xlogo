# Hexagons
![Hexagons](../../art/400x400/hexagons.png)

A simple recursion of hexagons shapes. Colors depend on distance from central [0 0] point.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Hue :Angle
 Make "Red Round 127*(1+Cos :Angle)
 Make "Green Round 127*(1+Cos(120+:Angle))
 Make "Blue Round 127*(1+Cos(240+:Angle))
 Output ( List :Red :Green :Blue )
End
To Hexagon :Side
 If :Side < 24 [Stop]
 Back :Side Right 60
 Repeat 6 [SetPC Hue 3*Distance [0 0]
 PenDown Forward :Side PenUp
 Left 60 Hexagon :Side/2]
 Left 60 Forward :Side
End
To Go
 New Right 30 Hexagon 100
End
```

For more recursions, reduce the side (shown in red) 
to 12 or 6.  

See [editing procedures](../../ipt/info/tuts.md).
