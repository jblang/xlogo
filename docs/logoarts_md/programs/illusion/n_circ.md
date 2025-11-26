# Negative Shape

The gaps in the grid appear as circlular shapes floating above the 
grid. Especially strong effect if you're a bit tired.

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
