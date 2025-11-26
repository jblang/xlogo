# Pebble
![Pebble](../../art/400x400/pebble.png)

A collection of 20 random pebble outline shapes, with different widths 
and heights, filled with screencolor black.  

All pebbles are formed from two end curves and two straight lines.  

Type in **Pebble :Width :Height** for individual pebble shapes.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Pebble :Wide :High
 # pebble shape drawn from centre (tp)
 LocalMake "Diff Abs :Wide-:High
 If :Wide > :High
 [LocalMake "Rad :High/2 LocalMake "Vert 0 LocalMake "Horiz :Diff]
 [LocalMake "Rad :Wide/2 LocalMake "Vert :Diff LocalMake "Horiz 0]
 PenUp Back :High/2 Right 90 Back :Horiz/2 PenDown
 Repeat 2 [
 Forward :Horiz LeftArc 90 :Rad Forward :Vert LeftArc 90 :Rad]
 PenUp Forward :Horiz/2 Left 90 Forward :High/2 # return to centre
End
To LeftArc :Angle :Radius
 # arc drawn relative to turtle position, angle & radius positive
 PenUp Right 90 Back :Radius Left :Angle
 Arc :Radius Heading Heading+:Angle
 Forward :Radius Left 90 PenDown
End
To Hue :Theta
 # Output RGB hue list from angle :Theta
 Make "Red Round 127.5*(1+Sin :Theta)
 Make "Green Round 127.5*(1+Sin (:Theta+120))
 Make "Blue Round 127.5*(1+Sin (:Theta+240))
 Output (List :Red :Green :Blue)
End
To Go
 New Repeat 20 [
 Make "Horiz 20+Random 80
 Make "Vert 20+Random 80
 SetXY (Random 300)-150 (Random 300)-150
 SetPC Gray
 Pebble :Horiz :Vert FillZone
 SetPC Black Fill
 SetPC Hue Random 360
 Pebble :Horiz :Vert]
End
```

Copy and paste the procedure below for a pebble pillow.

```logo
To Go
 # pebble pillow
 New Right 45
 For [Size 0.5 15.5] [
 Pebble 34*:Size 34*(16-:Size) ]
End
```
