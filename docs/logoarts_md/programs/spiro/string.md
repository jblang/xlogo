# Filography
![Filography](../../art/400x400/loom/3190.png)

Filography is the art of stringcraft. 
Typically a brightly colored thread is wound between nails knocked 
into a piece of wood, or through holes in a piece of card.

This program uses a circular array of 90 holes. Imagine they are numbered 
1 to 90 in a clockwise direction from the top. Each string is threaded 
from a start hole to a finish hole. The start holes rise sequentially 
1 through 90. If StepA and StepB are both 2 then the finish holes rise in steps of 2. Therefore, the 
first few strings are threaded from 1 to 2, 2 to 4, 3 to 6, 4 to 8 
etc. This gives the classic pattern.

If StepA and StepB are not the same, then the string moves away from the circle. An Offset adds a set amount to each destination, resulting in yet more patterns.

The program draws random patterns. It generates 3 parameters: StepA, StepB and Offset. These are displayed at the top of the screen.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Init
 Make "StepA Pick [0 2 3 4]
 Make "StepB Pick [-3 -2 1 2 3 4]
 Make "Offset 15*Random 9
End
To Display :StepA :StepB :Offset
 SetPC White
 SetXY Minus 196 186 Label (List "Loom :StepA :StepB :Offset)
End
To Loom :StepA :StepB :Offset
 For [Angle 0 360 2] [
 SetPC AngCol :Angle PenUp
 SetXY 180*Sin :Angle 180*Cos :Angle PenDown # circle start point
 SetXY 180*Sin (:StepA*:Angle) 180*Cos (:Offset+(:StepB*:Angle)) # finish point
 Wait 2]
End
To AngCol :Ang
 # Output list of red, grn, blue
 Make "Red 127*(1+Cos :Ang)
 Make "Grn 127*(1+Cos(120+:Ang))
 Make "Blue 127*(1+Cos(240+:Ang))
 Output ( List :Red :Grn :Blue )
End
To Go
 New Forever [
 Init Display :StepA :StepB :Offset
 Loom :StepA :StepB :Offset
 Wait 300 PenUp Wash]
End
```

Add in the following procedure to draw individual looms.

```logo
To GoX :StepA :StepB :Offset
 New Display :StepA :StepB :Offset
 Loom :StepA :StepB :Offset
End
```

Type **goX StepA StepB Offset** for example **GoX 2 3 60** to run.
