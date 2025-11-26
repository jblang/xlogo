# Five Pointed Star
![Five Pointed Star](../../art/400x400/g9_star.png)

A series of nested five pointed stars. This is not drawn by recursion 
but by a simpe repeat loop.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Star :Size
 # global values
 Forward (:Size/2)/Cos 18
 Right 162 PenDown
 Repeat 5 [Forward :Size Right 144]
 PenUp Left 162 Back (:Size/2)/Cos 18
End
To Go
 New Make "Size 700
 Repeat 4 [
 SetPC RepCount
 Star :Size Right 36
 Make "Size :Size*0.38]
End
```
