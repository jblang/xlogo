# Hexagrams
![Hexagrams](../../art/400x400/hexagram.png)

A complete set of Hexagrams drawn in an 8x8 grid. Each hexagram is made up of a 
pair of tri-grams.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Stick :W
 SetPC [255 102 51] # orange
 Right 90 PenDown Forward 7
 If (Item :W :BinList) = 0 [PenUp]
 Forward 9 PenDown Forward 7
 PenUp Back 23 Left 90 Forward 5
End
To Hex :M :N
 Make "Count (8*:M)+:N
 Make "BinList Dec2Bin :Count
 SetPC White Label :Count Forward 14
 For [W 3 8] [Stick :W]
End
To Gridd
 For [M 0 7][
 For [N 0 7][
 Make "X :N*48-182
 Make "Y :M*48-188
 SetXY :X :Y Hex :M :N]]
End
To Dec2Bin :Num
 # convert decimal number Num to an 8 item binary list
 If :Num >255 [Print [Rule is too large!] Stop]
 LocalMake "Bin []
 For [C 7 0 -1] [
 LocalMake "Bin LPut (Quotient :Num Power 2 :C) :Bin
 LocalMake "Num :Num -((Power 2 :C) *Last :Bin)]
 Output :Bin
End
To Go
 New Gridd
End
```
