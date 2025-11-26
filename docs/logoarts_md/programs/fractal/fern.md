# Fern
![Fern](../../art/400x400/fern.png)

Many plants exhibit recursive 
properties in their structures. Ferns are a good example. Each branch 
is a smaller replica of the whole fern leaf.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Hue :Angle
 # Green from 187 to 252
 Make "Green 0.18*:Angle + 187
 Output ( List 215 :Green 88 )
End
To Fern :Size
 If :Size < 5 [Stop]
 SetPC Hue Heading
 Forward :Size / 20
 Left 80 Fern :Size * 0.3
 Right 82 Forward :Size / 20
 Right 80 Fern :Size * 0.3
 Left 78 Fern :Size * 0.9
 Left 2 Back :Size / 20
 Left 2 Back :Size / 20
End
To Go
 New SetPos [-50 -170] PenDown Fern 440
End
```

For less detail, increase the size limit (shown in red). 
See [editing procedures](../../ipt/info/tuts.md).

Also see [Fractal Fern](../dotplot/fern.md) created by Chaos Game dot plotting.
