# Flora
![Flora](../../art/400x400/flora.png)

Some random mathematical flower 
shapes. The XY position for each flower is chosen at random. The 72 
spokes are of length 1+Cos D * N, where D is a random number between 
2 and 7.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Init
 Make "X (140 - Random 280)
 Make "Y (140 - Random 280)
 Make "T (Random 360)
 Make "D Pick [2 3 4 5 6 7]
End
To Plate
 Back 156 Right 88 PenDown SetPC [128 128 128]
 Repeat 90 [Forward 11 Left 4] PenUp
End
To Flora
 SetPC AngCol :T SetXY :X :Y PenDown
 For [N 0 360 5] [
 SetXY :X :Y SetH :N+:T
 Forward 28*(1+Cos(:D*:N))]
 PenUp
End
To AngCol :T
 Make "Red Round 127*(1+Cos :T)
 Make "Green Round 127*(1+Cos(120+:T))
 Make "Blue Round 127*(1+Cos(240+:T))
 Output ( List :Red :Green :Blue )
End
To Go
 New Plate Repeat 16 [Init Flora]
End
```
