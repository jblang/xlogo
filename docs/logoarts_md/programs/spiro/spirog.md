# Spirograph
![Spirograph](../../art/400x400/spirograph.png)

Plastic wheels and leaky pens. 
This program simulates Spirograph patterns with a lot less fuss.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Spir
 For [S 10 70 5] [
 PenUp SetPos XYCurve :S 0 PenDown SetPC Hue2 :S+10*:B
 For [T 1 360] [SetPos XYCurve :S :T]]
End
To XYCurve :S :T
 Make "X 120 * (Sin :S+:A*:T) + :S * (Cos (:B*:T))
 Make "Y 120 * (Cos :S+:A*:T) + :S * (Sin (:B*:T))
 Output (List :X :Y)
End
To Rand2
 Make "A (Pick [-1 1]) * Pick [1 2]
 Make "B (Pick [-1 1]) * Pick [2 3 4 5 6]
End
To Display
 # write header title and footer values
 SetPC White
 SetPos [-190 184] Label "Spirograph
 SetPos [-190 -190] Label ( List "A\ B\ = :A :B )
End
To Hue2 :Theta
 # Output RGB hue list from angle :Theta
 Make "Red Abs 255*Sin :Theta
 Make "Green Abs 255*Sin (:Theta+120)
 Make "Blue Abs 255*Sin (:Theta+240)
 Output (List :Red :Green :Blue)
End
To Go
 Repeat 12 [New Rand2 Display Spir Wait 200]
End
```
