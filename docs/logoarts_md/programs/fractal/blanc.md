# Blancmange Curve
![Blancmange Curve](../../art/400x400/blam.png)

This draws the Blancmange curve. An empty list is first created to hold the 256 heights. At each pass, the middle position is calculated as the average height plus an offset.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Init
 # create empty list
 Make "Y [ ]
 Repeat 257 [ Make "Y LPut 0 :Y ]
End
To Display
 # write header title
 SetPC White PenUp
 SetPos [-190 180] Label [Blancmange Curve]
End
To Blancmange
 # calculate heights
 Make "Index 256
 For [N 1 8] [
 For (List "M 1 256 :Index) [
 Make "Average ((Item :M :Y) + Item (:M + :Index ) :Y) / 2
 Make "Y SetItem :Y (:M+:Index/2) (:Index + :Average) ]
 Make "Index :Index / 2 ]
End
To Draw
 # draw curves from list
 Make "Index 256
 For [N 1 8] [
 Wash Display
 PenUp SetPos [-127 -100] PenDown SetPC Pink
 For (List "P 1 257 :Index) [SetPos List :P-128 (Item :P :Y) / 2 -100]
 Make "Index :Index / 2 Wait 80]
End
To Go
 New Init
 Blancmange Draw
End
```
