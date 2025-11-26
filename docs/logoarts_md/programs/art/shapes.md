# Random Shapes
![Random Shapes](../../art/400x400/g6_stars.png)

This fills the drawing area with random shapes, either stars, yin, lunes 
or leaves. They are drawn twice so that the FillZone primitive can fill 
the entire shape with a single color.  

The shapes were adapted from [shapes procedures](../../ipt/info/proshape.md) to use a fixed size.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Init
 # global values
 GlobalMake "ShapeSize 42 # size of each shape
 GlobalMake "Total 100 # total number of shapes drawn 
 # add more shapes or hues to the following lists 
 GlobalMake "Shapes Ask [Annulus Clover Heart Horseshoe Lune Star]
 GlobalMake "Hues [Red Green Yellow Orange Pink Aqua]
End
To Annulus :Size
 # annulus shape drawn from centre (tp)
 Make "Offset :Size/3 # global offset fill
 Circle :Size/2
 Circle :Size/4
End
To Clover :Size
 # clover shape drawn from centre (tp)
 Repeat 3 [
 Forward :Size/3.74 Arch 300 0.23*:Size
 Back :Size/3.74 Left 120]
End
To Heart :Size
 # heart shape drawn from centre (tp)
 Forward :Size/4 ForEach "P [1 -1] [ # parity
 Left :P*90 Forward :Size/4 Right :P*90
 Arch 180 :Size/4 Right :P*90 Back :Size/15 Right :P*33.5
 Arch 67 49*:Size/60
 Left :P*33.5 Forward 19*:Size/60 Left :P*90]
 Back :Size/4 # return to centre
End
To Horseshoe :Size
 # horseshoe shape drawn from centre (tp)
 Make "Offset :Size/3 # global offset fill
 Arch 180 :Size/2 Arch 180 :Size/4
 ForEach "P [1 -1] [ # parity
 Left :P*90 Back :Size/2 Left :P*12.75
 Arch 25.5 :Size Arch 25.5 0.75*:Size Left :P*12.75
 Forward 0.875*:Size Left :P*90 Arch 180 :Size/8
 Right :P*90 Back 0.875*:Size Right :P*25.5
 Forward :Size/2 Right :P*90]
End
To Lune :Size
 # lune shape drawn from centre (tp)
 Make "Offset :Size/3 # global offset fill
 Arch 240 :Size/2 Back :Size/2
 Arch 120 :Size/2 Forward :Size/2
End
To Star :Size
 # 5 pointed star shape drawn from centre (tp)
 Forward :Size/2 Right 162 PenDown
 Repeat 5 [
 Forward :Size/2.76 Left 72 Forward :Size/2.76 Right 144]
 PenUp Left 162 Back :Size/2 # return to centre
End
To Aqua
 Output [0 128 255] # return aqua rgb color
End
To Arch :Angle :Radius
 # symmetrical arc drawn relative to turtle heading
 Arc :Radius Heading-:Angle/2 
 Heading+:Angle/2
End
To rArc :Angle :Radius
 # clockwise arc drawn relative to turtle heading
 Arc :Radius Heading :Angle+Heading Right :Angle
End
To Jump :Side
 # set turtle to random position within square of size side
 SetXY (Random :Side) - :Side/2 (Random :Side) - :Side/2
End
To Ask :Items
 # return list of user selected items
 LocalMake "Say List "Select: [_ all]
 Repeat Count :Items [
 LocalMake "Say LPut List RepCount (Item RepCount :Items) :Say]
 Read :Say "Input
 If :Input = " [Print "all Output ButFirst ButFirst :Say] # all if no entry
 LocalMake "Selection []
 ForEach "ItemNo :Input [
 LocalMake "Selection LPut (Item :ItemNo+2 :Say) :Selection]
 Print :Selection Output :Selection
End
To Go
 New Init
 Repeat :Total [
 Make "Shape Pick :Shapes
 Make "Offset 0 Make "Size :ShapeSize 
 If :Shape ="Star [Make "Size :Size/0.8]
 Jump 320 SetH Random 360
 SetPC [0 0 1] Run List :Shape :Size
 Forward :Offset FillZone Back :Offset
 SetPC Run Pick :Hues
 Forward :Offset Fill Back :Offset
 SetPC Black Run List :Shape :Size]
End
```

Find more shapes in the [shapes library](../../ipt/info/proshape.md).  

Silver moon [187 187 187] and gold stars [255 204 0].
