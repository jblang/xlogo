# Triangular Tree
![Triangular Tree](../../art/400x400/tritree.png)

Trees are drawn using square and right angled triangle with angles 36 and 54 degrees. When they are both 45 degrees a symmetrical tree is drawn.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Tree :Order :Size
 If :Order < 0 [Forward :Size Stop] # stop if order < 0
 Left 90 Repeat 5 [Forward :Size Right 90]
 Left 54 Tree :Order-1 :Size*Sin 36
 Right 90 Tree :Order-1 :Size*Cos 36
 Right 54 Forward :Size Left 90
End
To Go
 New
 SetPos [-68 -160] Right 90 PenDown
 Tree 5 72
End
```

Just a few changes produces a 45 degree angle tree.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Tree :Order :Size
 If :Order < 0 [Forward :Size Stop] # stop if order < 0
 Left 90 Repeat 5 [Forward :Size Right 90]
 Left 45 Tree :Order-1 :Size*Sin 45
 Right 90 Tree :Order-1 :Size*Cos 45
 Right 45 Forward :Size Left 90
End
To Go
 New
 SetPos [-40 -160] Right 90 PenDown
 Tree 5 75
End
```
