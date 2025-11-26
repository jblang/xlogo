# Sphinx
![Sphinx](../../art/400x400/sphinx.png)

Series of fractal sphinx shapes. Each sphinx contains 4 similar sphinx shapes, 3 of opposite parity.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Sphinx :Order :Size :Parity
 If :Order < 1 [Stop] # ie if = 0
 SetPW :Order Forward 3*:Size Left 180
 Sphinx :Order-1 :Size/2 Minus :Parity
 SetPW :Order Left 180 Forward 3*:Size Left 180
 Sphinx :Order-1 :Size/2 Minus :Parity
 SetPW :Order Right :Parity*60 Forward :Size
 Sphinx :Order-1 :Size/2 :Parity
 SetPW :Order Forward 3*:Size
 Left :Parity*120 Forward 2*:Size
 Right :Parity*60 Forward 2*:Size Left 180
 Sphinx :Order-1 :Size/2 Minus :Parity
 SetPW :Order Right :Parity*120
 Forward 2*:Size Left :Parity*120
End
To Go
 New
 SetPos [-180 -120] Right 90 PenDown
 Sphinx 4 60 1
End
```
