# Lace Curve
![Lace Curve](../../art/400x400/lace.png)

This curve fills a 30 60 right angled triangle. It is repeated twice to form an equalateral triangle, which can then be filled.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Bend :Level :Parity :Dir
 If :Level < 1 [Forward :Size Right :Parity*60 Forward :Size Stop]
 Right :Dir* Minus :Parity*30
 Bend :Level-1 Minus :Parity :Dir
 Turn :Parity*(60+:Dir*30)
 Bend :Level-1 Minus :Parity Minus :Dir
 Turn :Parity*(60-:Dir*30)
 Bend :Level-1 Minus :Parity :Dir
 Right :Dir* :Parity*30
End
To Turn :Angle
 Right :Angle Forward :Size Right :Angle
End
To Go :Level
 New SetPC Yellow
 GlobalMake "Size 105/(Power 1.81 :Level) # global value
 PenUp SetXY Minus :Size/2 Minus 170 PenDown Left 30
 Bend :Level 1 1 Right 60 Forward :Size Right 60
 Bend :Level 1 Minus 1 Right 60 Forward :Size
 PenUp Home SetPC [48 48 48] Wait 30 Fill # comment out for no fill
End
```

Enter **go** and the **level** eg **go 2** to draw a second 
order Lace curve.  

Enter **go 0** for the basic path, up to **go 5** or **go 
6** for increasingly complex paths.

Add the following procedure **GoX** to overlay Lace curves of level 0 to 5.

```logo
To GoX
 New
 Make "Cols [4 1 11 9 15 3]
 For [Level 0 5] [
 GlobalMake "Size 105/(Power 1.81 :Level)
 SetPW 6-:Level SetPC Item (:Level+1) :Cols
 PenUp SetXY Minus :Size/2 Minus 160 SetH 0
 Back Item (:Level+1) [-17 0 7 9 8 5] PenDown Left 30 # fudge factor
 Bend :Level 1 1 Right 60 Forward :Size Right 60
 Bend :Level 1 Minus 1 Right 60 Forward :Size
 Wait 90]
End
```


The fudge factor allows more exact overlay of the curves.
