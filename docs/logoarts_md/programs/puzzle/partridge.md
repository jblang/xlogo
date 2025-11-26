# Partridge Puzzle
![Partridge Puzzle](../../art/400x400/partridge.png)

A famous puzzle which asks if a square can be divided into a consecutive number of smaller squares. Here, a 36x36 square is disected into 36 squares of size one 1x1 to eight 8x8 squares.

1x1+2(2x2)+3(3x3)+4(4x4)+5(5x5)+6(6x6)+7(7x7)+8(8x8)=36x36

This program does not calculate a solution. It simply draws two possible solutions using a list of all the squares centre points.

By the way, the puzzle of using a 70x70 square is still unresolved.  

1x1+2(2x2)+3(3x3)+........................+23(23x23)+24(24x24)=70x70

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Init
 Make "Centres1 [[30.5 31.5] [22 14] [29 17]
 [14.5 9.5] [31.5 29.5] [34.5 29.5] [15 13] [19 13] [25 34] [29 34]
 [18.5 8.5] [25.5 15.5] [8.5 17.5] [13.5 17.5] [33.5 33.5]
 [19 3] [25 3] [3 11] [3 17] [33 19] [33 25] [24.5 9.5] [9.5 11.5] [19.5 18.5] [26.5 21.5] [19.5 25.5] [26.5 28.5] [19.5 32.5]
 [4 4] [12 4] [32 4] [32 12] [4 24] [12 24] [4 32] [12 32]]
 Make "Centres2 [[21.5 14.5] [23 15] [23 17]
 [22.5 9.5] [22.5 12.5] [20.5 16.5] [26 14] [26 18] [26 22] [26 26]
 [21.5 20.5] [9.5 23.5] [21.5 25.5] [9.5 28.5] [9.5 33.5]
 [27 3] [33 3] [27 9] [33 9] [10 18] [16 18]
 [3.5 11.5] [10.5 11.5] [17.5 11.5] [3.5 18.5] [15.5 24.5] [3.5 25.5] [3.5 32.5]
 [4 4] [12 4] [20 4] [32 16] [32 24] [16 32] [24 32] [32 32]]
End
To Draw :Centres
 For [Size 1 8] [
 Repeat :Size [
 SetPC Item :Size [1 2 3 8 12 13 15 16]
 Make "Centre First :Centres
 SetXY 10*((First :Centre)-18) Minus 10*((Last :Centre)-18)
 Square 10*:Size-PenWidth 
 # exact size
 SetPC Light PenColor Fill
 If :Size>1 [SetPC Black Back 5 Right 90 Back 3 Left 90 Label :Size] 
 Wait 6
 Make "Centres ButFirst :Centres]]
End
To Square :Side
 # square (side x side) drawn from centre (tp)
 Back :Side/2 Right 90 Back :Side/2 PenDown
 Repeat 4 [
 Forward :Side Left 90]
 PenUp Forward :Side/2 Left 90 Forward :Side/2 # r2c
End
To Light :Hue
 # output rgb list midway between :hue and white
 Repeat 3 [
 Make "Hue ButFirst LPut Int (255+(First :Hue))/2 :Hue]
 Output :Hue
End
To Go
 New Init
 Draw :Centres1 Wait 300 Wash
 Draw :Centres2
End
```
