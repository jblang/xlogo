# Circles

Both colored circles are the same size. The upper circle though appears larger than the lower circle. We are influenced by the sizes of the surrounding circles.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Go
 New
 SetPC [230 127 57] SetPW 45
 SetPos [-60 -60] Dot Pos
 SetPC [145 164 185] SetPW 59
 Repeat 6 [Forward 70 Dot Pos Back 70 Left 60]
 SetPC [230 127 57] SetPW 45
 SetPos [110 110] Dot Pos
 SetPC [145 164 185] SetPW 9
 Repeat 8 [Forward 40 Dot Pos Back 40 Left 45]
End
```
