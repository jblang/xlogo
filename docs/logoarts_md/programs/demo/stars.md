# Star Shapes
![Star Shapes](../../art/400x400/g9_stars.png)

Star shaped outlines of at least 5 points. Quite a bit of trigonometry 
was needed to calculate the distance from centre, angles and side length 
of each star shape.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Star :N :Size
 # star shape drawn from centre (tp)
 Make "Dist (:Size/2) *(Sin 180/:N)/Cos 180/:N
 Forward :Size/2 Right 90+(360/:N) PenDown
 Repeat :N [
 Forward :Dist Left 360/:N Forward :Dist Right 720/:N]
 PenUp Left 90+(360/:N) Back :Size/2 # return to centre
End
To Go
 New For [N 5 9] [
 SetPC :N-4 SetPW 10-:N
 Star :N 378 Wait 40]
End
```
