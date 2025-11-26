# Spiro Additive
![Spiro Additive](../../art/400x400/spirotest.png)

In [FerrisWheels](ferris.md), the three angles are independent 
of each other. Here, they are added together, resulting in non symmetrical 
patterns.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Spir :R :Rang :S :Sang :T :Tang
 PenUp Home SetPC White PenDown Circle 2
 Left :Rang Forward :R Circle 2
 Left :Sang Forward :S Circle 2
 Left :Tang Forward :T
End
To Init
 SetTurtle 1 # follow turtle
 Home SetPC 5
 PenUp SetPos [0 190]
 SetTurtle 0 PenDown
End
To Go
 New Init PX # inverted paint pen
 Make "R 90 Make "S 60 Make "T 40
 For [Q 0 360 4] [
 Make "Rang :Q Make "Sang 3*:Q + 90 Make "Tang Minus 2*:Q -90
 Spir :R :Rang :S :Sang :T :Tang
 SetPC Red Circle 2
 Make "A Pos
 SetTurtle 1 SetPC Red1 SetPos :A PenDown SetTurtle 0
 If :Q=360 [SetPC Red Circle 1 Stop] Wait 10
 Spir :R :Rang :S :Sang :T :Tang]
End
```
