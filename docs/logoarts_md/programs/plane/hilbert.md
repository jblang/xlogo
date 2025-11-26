# Hilbert Curve
![Hilbert Curve](../../art/400x400/hilbert.png)

This plane filling curve was invented by David Hilbert, a German 
mathematician in the early 1900's. The procedure uses a parity value 
of either 1 or -1 to turn the curve left or right and so ensure 
it joins up correctly. Each curve is one continueous path from start 
(lower left) to finish (lower right).

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Init :Level
 Make "Size 192/(Power 2 :Level) 
 # global value
 PenUp SetXY (:Size/2)-192 (:Size/2)-192
 SetH 90 PenDown
End
To Hilbert :Level :Parity
 If :Level < 0 [Stop]
 Left :Parity*90
 Hilbert :Level-1 Minus :Parity
 Forward :Size
 Right :Parity*90
 Hilbert :Level-1 :Parity
 Forward :Size
 Hilbert :Level-1 :Parity
 Right :Parity*90
 Forward :Size
 Hilbert :Level-1 Minus :Parity
 Left :Parity*90
End
To Go :Level
 New Init :Level Hilbert :Level 1
End
```

Enter **go** and the **level** eg **go 2** to draw a second 
order Hilbert curve.  

Enter **go 0** for the basic path, up to **go 4** or **go 
5** for increasingly complex paths.

Add the following procedure **GoX** to overlay Hilbert curves 
of level 1 to 4 (or you could increase it to 5).

```logo
To GoX
 New For [Level 0 4]
 [SetPC :Level+1 SetPW 5-:Level
 Init :Level Hilbert :Level 1 Wait 60]
End
```
