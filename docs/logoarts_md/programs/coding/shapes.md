# Shapes
![Shapes](../../art/400x400/h_shoe.png)

Many shapes are drawn using the rArc and Arch procedures. This program introduces two procedures, which will add radius and angle information arrows to the arc or arch. As they are called exactly   the same name, they can replace the original procedures directly.

To draw more shapes (eg Clover, Lens, Quad), add the name  into the Shapes list, and the procedure from the shapes library.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Title :Titles
 # label up to 4 titles
 SetPC White Repeat Count :Titles [
 Home Right (RepCount*90) -135 Forward 262 SetH 0
 If X>0 [SetFontJustify [2 1]] [SetFontJustify [0 1]]
 Label Item RepCount :Titles]
End
To Grout :Size :Order
 # draw centred square grid with :order divisions
 SetPC [64 64 64] SetPW 1
 For (List "Point 0 :Size+1 :Size/:Order) [
 SetXY :Point-:Size/2 :Size/2 SetH 180
 PenDown Forward :Size PenUp
 SetXY :Size/2 :Point-:Size/2 SetH 270
 PenDown Forward :Size PenUp Wait 2]
End
To Burst :Spokes :Radius
 # star burst drawn from centre (tp)
 Repeat :Spokes [
 PenDown Forward :Radius
 PenUp Back :Radius
 Right 360/:Spokes Wait 2]
End
To Arrow :Size
 # draw simple end-of-line arrow (tp)
 Left 15 Back :Size PenDown
 Forward :Size Right 30 
 Back :Size 
 PenUp Forward :Size Left 15
End
To Arch :Angle :Radius
 # symmetrical arc drawn relative to turtle heading
 If :Info [AddInfo :Angle :Radius] # label arc info
 Arc :Radius Heading-:Angle/2 
 Heading+:Angle/2
End
To rArc :Angle :Radius
 # clockwise arc drawn relative to turtle heading
 If :Info [Right :Angle/2 AddInfo :Angle :Radius Left :Angle/2]
 Arc :Radius Heading :Angle+Heading Right :Angle
End
To AddInfo :Angle :Radius
 # add angle and radius info labels
 Make "Hue PenColor Wait 40 
 SetPC Cyan Circle 5
 If Not :Angle=180 [Left :Angle/2 DottedLine :Radius
 Right :Angle DottedLine :Radius Left :Angle/2]
 SetPC Yellow PenDown Forward :Radius PenUp Arrow 22 
 SetPC White SetFontjustify [1 1]
 Forward 12 Label Word Round :Angle "º Back 12 
 If Or X=0 X>0 [Make "P 1] [Make "P Minus 1]
 SetFontJustify List :P+1 0
 Back 12 Left :P*90 Back 18 Label Round :Radius
 Forward 18 Right :P*90 Forward 12 Back :Radius
 SetPC :Hue Wait 20
End
To DottedLine :Length
 # draw radiating dotted line
 SetPC Gray Repeat Round :Length/18 [
 PenDown Forward 4 PenUp Forward 14]
 Back 18* Round :Length/18
End
To Egg :Size
 # egg shape drawn from centre (tp)
 Make "Size :Size/1.3
 Forward :Size/2.9 Left 45 rArc 90 0.292*:Size
 Back :Size/1.414 rArc 45 :Size
 Forward :Size/2 rArc 180 :Size/2
 Back :Size/2 rArc 45 :Size
 Forward :Size/1.414 Right 45 Back :Size/2.9 
 # r2c
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
To Oval :Size
 # oval shape drawn from centre (tp)
 Repeat 2 [
 Back :Size/4 Arch 90 :Size/1.66 Forward :Size/4 Left 90
 Forward :Size/4 Arch 90 :Size/4 Back :Size/4 Left 90]
End
To Propeller :Size
 # propeller shape drawn from centre (tp)
 Repeat 3 [
 Forward :Size/3 Arch 240 :Size/6
 Back :Size/3 Left 60
 Forward :Size/3 Left 180 Arch 120 :Size/6 Left 180
 Back :Size/3 Left 60]
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
 New Make "Size 320
 Make "Shapes [Egg Heart Horseshoe Oval Propeller]
 ForEach "Shape (Ask :Shapes) [
 Wash Title (List :Shape [] (Se "Size: :Size)) Wait 30
 Home SetPC [32 0 64] 
 Make "Info False Make "Offset 0
 Run :Shape :Size
 Forward :Offset Fill Wait 30
 PX PU Grout :Size 12 PPT PU Wait 30
 Home SetPC Green Burst 4 8
 Make "Info True Run :Shape :Size Wait 150]
End
```
