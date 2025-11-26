# Plastic Number
![Plastic Number](../../art/400x400/plastic.png)

This is a spiral of equalateral triangles, increasing in size by approx 1.3.  

A logarithmic spiral can be drawn through their corners. Here, the spiral 
is approximated by a 60 degree arc in each triangle.

The centre of each triangle is labelled with the length of it's side.  

Also, three lists are updated in turn with triangle corner coordinates, allowing three diagonals to be automatically drawn.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Init :N
 Make "Parity 0
 Repeat :N [
 Make Word "Diag RepCount-1 List [0 0] [0 0] ]
End
To Section :Side
 # spiral of triangles
 If :Side > 400 [Stop] 
 # stop when side too long
 SetPC Green Repeat 2 [Forward :Side Right 120]
 SetPC Red rArc 60 :Side Left 60 # draw arc
 SetPC Green Repeat 2 [Forward :Side Right 120]
 AddDiag 3
 If :Side > 30 [AddInfo 3 Integer :Side]
 Left 60 Wait 20
 Section :Side*1.3247 
 # do again with larger side
End
To AddInfo :N :Side
 # label size
 Make "R (:Side/2)*(1/(Sin (180/:N)) ) # Distance to polygon center
 Right 90-(180/:N) PenUp Forward :R
 Make "myH Heading
 # Make "Blue (:N * :Side) / 6 SetPC (List 0 0 :Blue) Fill SetPC White
 SetH 0 SetFontJustify [1 1] SetPC White Label :Side
 SetH :myH Back :R Left 90-(180/:N) PenDown
End
To AddDiag :N
 # Add current turtle position to next diagonal list
 Make "Name Word "Diag :Parity
 Make :Name LPut Pos ButFirst Thing :Name
 Make "Parity Mod :Parity+1 :N
End
To Diagonals :N
 # Draw diagonals 1 to N
 SetPC Blue Repeat :N [
 PenUp SetPos First Thing Word "Diag RepCount-1
 PenDown SetPos Last Thing Word "Diag RepCount-1 ]
End
To Display :Title
 PenUp SetPC White SetH 0
 SetPos [-184 180] Label :Title
End
To rArc :Angle :Radius
 # clockwise arc drawn relative to turtle heading
 Arc :Radius Heading :Angle+Heading Right :Angle
End
To Go
 New Init 3
 Display [Plastic Number 1.3247]
 SetPos [78 -16] Left 90 PenDown
 Section 1.2 # begin with triangle of side 1.2
 Diagonals 3 Wait 20
End
```

Squares use the golden ratio of 1.618. Thier side lengths form a [Fibonacci](../../ipt/info/prorecur.md) series. Modify the Section and Go procedures by pasting in the following code.

```logo
To Section :Side
 # spiral of squares
 If :Side > 400 [Stop] # stop when side too long
 SetPC Green Repeat 3 [Forward :Side Right 90]
 SetPC Red rArc 90 :Side Left 90 # draw arc
 SetPC Green Repeat 2 [Forward :Side Right 90]
 AddDiag 2
 SetPC Green Repeat 1 [Forward :Side Right 90]
 If :Side > 30 [AddInfo 4 Integer :Side]
 Left 90 Wait 20
 Section :Side*1.618 # do again with larger side
End
To Go
 New Init 2
 Display [Plastic Number 1.618]
 SetPos [86 -20] PenDown
 Section 1.2 # begin with triangle of side 1.2
 Diagonals 2 Wait 20
End
```


Spiral of Hexagons.

```logo
To Section :Side
 # spiral of hexagons
 If :Side > 130 [Stop] # stop when side too long
 SetPC Green Repeat 4 [Forward :Side Right 60]
 Forward :Side/2 Right 40
 SetPC Red rArc 100 1.32*:Side Left 100 # draw arc
 SetPC Green Left 40 Forward :Side/2 Right 60
 Repeat 4 [Forward :Side Right 60]
 AddDiag 3
 If :Side > 14 [AddInfo 6 Integer :Side]
 Left 120 Wait 20
 Section :Side*1.2112 # do again with larger side
End
To Go
 New Init 3
 Display [Plastic Hex 1.2112]
 SetPos [54 -12] PenDown
 Section 1.2 # begin with triangle of side 1.2
 Diagonals 3 Wait 20
End
```


Spiral of Octagons.

```logo
To Section :Side
 # spiral of octagons
 If :Side > 70 [Stop] # stop when side too long
 SetPC Green Repeat 6 [Forward :Side Right 45]
 SetPC Red Right 22 rArc 90 1.84*:Side Left 112 # draw arc
 SetPC Green Repeat 6 [Forward :Side Right 45]
 AddDiag 4
 If :Side > 8 [AddInfo 8 Integer :Side]
 Left 135 Wait 20
 Section :Side*1.111 # do again with larger side
End
To Go
 New Init 4
 Display [Plastic Octal 1.111]
 SetPos [42 -8] Right 90 PenDown
 Section 1.2 # begin with triangle of side 1.2
 Diagonals 4 Wait 20
End
```
