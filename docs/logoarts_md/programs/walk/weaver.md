# Polygon Weaver
![Polygon Weaver](../../art/400x400/weave.png)

Here, two regular polygons are 'weaved' together. The turtle alternates between drawing one polygon and then the other.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Display :PolyA :PolyB
 # write header title and footer values
 PenUp SetPC White SetH 0
 SetPos [-190 182] Label "PolyWeaver
 SetPos [-190 -190] Label ( List "A\ B\ = :PolyA :PolyB )
 SetPos [134 -190] Label ( List "Rep\ = :Rep )
End
To Init
 Make "Side 52
 Make "PolyA Pick [3 4 5 6 7 8 9 10 11 12]
 Make "PolyB Pick [2 3 4 5 6 7 8]
 Make "Rep HCF :PolyA :PolyB
 Make "Rep (:PolyA/:Rep) * :PolyB
End
To HCF :I :J
 # return highest common factor of two integers
 LocalMake "Rem Modulo :I :J
 If :Rem =0 [Output :J] 
 [Output HCF :J :Rem]
End
To Weave :PolyA :PolyB :OffSet :Draw
 Make "A 90 Make "B 90
 Repeat :Rep [
 SetH :A
 If :Draw [SetPC Green] Forward :Side
 SetH :B + :OffSet + :A
 If :Draw [SetPC Yellow] Forward :Side
 Make "A :A-(360/:PolyA)
 Make "B :B-(360/:PolyB) ]
End
To Go
 New Animation
 Forever [
 Wash Init Display :PolyA :PolyB
 SetPos List Minus :Side/2 Minus 60 PenDown
 For [OffSet 0 480 5] [
 Weave :PolyA :PolyB :OffSet True
 Refresh Wait 3
 SetPC Black
 Weave :PolyA :PolyB :OffSet False]
 Wait 60]
End
```
