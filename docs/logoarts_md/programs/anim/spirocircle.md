# SpiroCircle
![SpiroCircle](../../art/400x400/spirocirc.png)

A series of animated spirographs, which morph seamlessly from one pattern to the next.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Spir
 For [R 0 180 4] [
 Make "S (180-:R) Make "Theta 0
 PenUp SetPos XYCurve :R :Theta PenDown
 For [Theta 2 360 2] [SetPos XYCurve :R :Theta]
 Refresh Wash] # put a wait in here if too fast
End
To Init
 Make "A (1 + Random 7)
 Make "B (1 + Random 5)
 Make "W Minus :W
End
To XYCurve :R :Theta
 Make "RotA :A*:Theta
 Make "RotB :B*:Theta
 Make "X :R*(Sin :RotA) + :S*(Cos :RotB)*:W
 Make "Y :R*(Cos :RotA) + :S*(Sin :RotB)
 Output (List :X :Y)
End
To Go
 New Animation Make "W 1
 Forever [Init SetPC :A Spir]
End
```
