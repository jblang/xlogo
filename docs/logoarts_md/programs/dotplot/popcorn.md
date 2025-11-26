# Popcorn
![Popcorn](../../art/400x400/popcorn.png)

This draws a strange looking 'popcorn' fractal. Similar to the Kam Torus fractal.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Popcorn
 For [Xn -190 190 10] [
 For [Y -190 190 10] [
 SetPC Hue2 :Y
 Make "X :Xn
 Repeat 30 [
 Make "Xnew :X - 5 * Sin ((2*:Y) + Tan (6*:Y))
 Make "Y :Y - 5 * Sin ((2*:X) + Tan (6*:X))
 Make "X :Xnew
 Dot List :X :Y ] ] ]
End
To Hue2 :Theta
 # Output RGB hue list from angle :Theta
 Make "Red Abs 255*Sin :Theta
 Make "Green Abs 255*Sin (:Theta+120)
 Make "Blue Abs 255*Sin (:Theta+240)
 Output (List :Red :Green :Blue)
End
To Go
 New Popcorn
End
```
