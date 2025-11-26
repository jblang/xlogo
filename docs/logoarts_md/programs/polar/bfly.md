# Butterfly Curve
![Butterfly Curve](../../art/400x400/butterfly.png)

This curve is a polar plot which looks like a butterfly. The equation gives the distance 
from the origin (R) for angle theta. This one is of the form:  

e^Cos theta -2*Cos 4*theta +Sin theta/12^5  

Where e is the value 2.718 and theta ranges from 0 to 360 degrees.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Title
 # write header title and footer equation
 SetPC White
 SetPos [-190 184] Label [Butterfly Curve]
 SetPos [-190 -190]
 Label [e^Cos theta -2*Cos 4*theta +Sin theta/12^5]
End
To DrawCurve
 # draw polar curve
 For [Theta 0 360 2] [
 SetPos PtoR (56*Curve :Theta) :Theta
 SetPC Hue 3*:Theta PenDown Wait 1]
End
To PtoR :RadDist :Theta
 # convert polar to rectangular co-ordinates
 LocalMake "X :RadDist *Sin :Theta
 LocalMake "Y :RadDist *Cos :Theta
 Output List :X :Y
End
To Hue :Theta
 # Output RGB hue list from angle :Theta
 Make "Red Round 127.5*(1+Sin :Theta)
 Make "Green Round 127.5*(1+Sin (:Theta+120))
 Make "Blue Round 127.5*(1+Sin (:Theta+240))
 Output (List :Red :Green :Blue)
End
To Curve :Theta
 # return distance from origin
 Output (Power 2.7 (Cos :Theta)) -(2*Cos(4*:Theta))
 +(Power Sin(:Theta/12) 5)
 # Output (1-Abs Minus Sin (6*:Theta)) + 2*Cos (2*:Theta)
 # Output 3*Sqrt Abs Sin 3*:Theta
End
To Go
 New SetPW 2
 Title Draw Curve
End
```
