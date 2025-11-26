# Mystic Rose
![Mystic Rose](../../art/400x400/mystic_rose.png)

This program creates a set of evenly spaced points around a circle. 
It then joins every point with every other point.

Enter **GoX** for an animation from two to 16 points.  

Enter **Go :C** for a circle with**C**points.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Display :C
 SetPC White
 SetPos [-190 184] Label Sentence [Mystic Rose] :C
End
To MRose :C
 SetPC Cyan
 Make "T 0 Make "S 0
 Make "Angle 360 / :C
 Repeat (:C-1) [
 For (List "Count :S :C) [
 PenUp
 SetXY 190*Sin (:Angle * :S) 190*Cos (:Angle * :S)
 PenDown
 SetXY 190*Sin (:Angle * :Count) 190*Cos (:Angle * :Count)
 Make "T :T+1 Wait 1]
 Make "S :S+1]
 PenUp
End
To GoX
 New For [C 2 16] [
 Wash Display :C MRosewww :C Wait 100]
End
To Go :C
 New Display :C MRose :C
End
```

Type **go :C** for example **Go 5** for a circle with 5 points.
