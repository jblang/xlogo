# Gasket
![Gasket](../../art/400x400/gasket.png)

Gaskets have self similar holes of ever decreasing size removed.  

Type go + Order eg Go 4 to run.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Gasket :Side :Order
 If :Order < 1 [Stop] # ie if = 0
 Repeat 4 [myShape :Side :Order Right 90]
 myFill :Side :Order
End
To myShape :Side :Order
 Gasket :Side/3 :Order-1
 Forward :Side/3
 Gasket :Side/3 :Order-1
 Forward 2*:Side/3
End
To myFill :Side :Order
 PenUp Right 45 Forward 0.7*:Side
 SetPC :Order Fill
 Back 0.7*:Side Left 45 PenDown SetPC [0 0 1]
End
To Go :Order
 New
 SetPos [-200 -200] SetPC [0 0 1] PenDown
 Gasket 400 :Order
End
```

Type **Go order** for example **Go 4** to run.
