# GingerBread Man
![GingerBread Man](../../art/400x400/gingerbread.png)

This is a well known mathematical 
map in the shape of a ginger bread man. In the Init procedure a spoiler 
is used to produce irrational starting points for the X co-ordinate. 
This prevents a simple 6 cycle plotting just 6 dots.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Init :N
 Make "Spoiler Pi/10
 Make "X :N +:Spoiler
 Make "Y :N
 SetPC ColAng :N # comment out for no color
End
To GBMan
 Make "Xnew 1 - :Y + Abs :X
 Make "Ynew :X
 Dot List (:Xnew*34)-84 (:Ynew*34)-84
 Make "X :Xnew
 Make "Y :Ynew
End
To ColAng :Angle
 Make "Green Integer 127.5*:Angle - 255
 Output ( List 255 :Green 0 )
End
To Go
 New
 For [N 2 4 0.05] [
 Init :N
 Repeat 1000 [GBMan]]
 Print "Done
End
```
