# Knight's Tour

This extends a 2D array (used in [Grid Art](../grid/gridart.md) etc) into the third dimension. Each cube is drawn as a simple skeleton of 12 lines. Procedure**Tour** adds a closed knight's tour to the 4x4x4 cube array. See [Knight's Tour](../puzzle/ktour.md) for 2D version.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Init
 # global values
 GlobalMake "Order 4
 GlobalMake "CellSize (First ScreenSize) /:Order
 GlobalMake "OffSet ((First ScreenSize) -:CellSize) /2
End
To ScreenPos :CellX :CellY :CellZ
 # return X Y Z screen position
 # use global values of CellSize and Offset
 Make "PosX (:CellX*:CellSize) -:OffSet
 Make "PosY (:CellY*:CellSize) -:OffSet
 Make "PosZ (:CellZ*:CellSize) -:OffSet
 Output (List :PosX :PosY :PosZ)
End
To DrawGrid
 # draw X by Y by Z array of cubes
 For (List "CellX 0 :Order-1) [
 For (List "CellY 0 :Order-1) [
 For (List "CellZ 0 :Order-1) [
 SetPos ScreenPos :CellX :CellY :CellZ
 Cube :CellSize/4 ] ] ]
End
To Cube :Side
 # draw a skeleton cube from center, tp
 Right 45 Back :Side/1.414 Left 45 Up 90 Back :Side/2 PenDown
 Repeat 2 [Repeat 2 [
 LineStart Forward :Side Down 90 Forward :Side Down 90 Forward :Side LineEnd
 Back :Side Right 90] Left 180]
 PenUp Forward :Side/2 Down 90 Right 45 Forward :Side/1.414 Left 45
End
To Tour
 # knight's 64 position tour, xyz
 Make "Tour [003 022 030 132 120 101 113 211 203 222 230 332 320 301 313 321 300 312 333 231 210 202 223 121 100 112 133 031 010 002 023 103 122 130 111 213 232 220 201 303 322 330 311 323 331 310 302 200 212 233 221 123 131 110 102 000 012 033 021 013 001 020 032 011]
 SetPW 4 SetPC Red PenUp
 SetPos ScreenPos 0 1 1 PenDown
 LineStart Repeat 64 [
 Make "PosXYZ Item RepCount :Tour
 Make "Hor Item 1 :PosXYZ
 Make "Ver Item 2 :PosXYZ
 Make "Dep Item 3 :PosXYZ
 SetPos ScreenPos :Hor :Ver :Dep] LineEnd
End
To Go
 New Perspective CS PenUp
 HideTurtle SetSC Black SetPC Green
 Init DrawGrid TourWait 30
 Message [View3D?] View3D
End
```
