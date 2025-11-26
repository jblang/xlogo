# Squares (Centre)
![Squares (Centre)](../../art/400x400/squares.png)

This program produces a fractal of nested squares. Squares are 
drawn from their centre, so each of the squares four corners is 
centred on a smaller square.

The reduction factor is a half. This means that each square is half 
the size. The ever smaller squares never quite touch.

For less reduction, the squares will overlap, which looks messy. 
However, with a reduction factor equal to 1.618 (the Golden Ratio), 
squares will exactly overlap. They are actually drawn precisely 
on top, giving a more tidy effect.

For a Golden ratio pattern, change the 2 to 1.62 and reduce the side to 160.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To RSquare :Order :Side
 If :Order < 0 [Stop]
 Back :Side/2 Right 90 Back :Side/2
 Repeat 4 [
 SetPW :Order+1 # comment out for no line widths
 PenDown Forward :Side PenUp Left 90 Wait 1
 RSquare :Order-1 :Side/2] # Golden Ratio replace 2 with 1.62
 Forward :Side/2 Left 90 Forward :Side/2
 FillSquare # comment out for no fills
End
To FillSquare
 SetPC DarkGreen Left 45 Forward 5 Fill
 Back 10 Fill Forward 5 Right 45 SetPC Cyan
End
To Go :Order
 New SetPC Cyan
 Make "Side 192 # Replace 192 with 160 for Golden Ratio
 RSquare :Order :Side
End
```

Type **go + order** to run.

Also see [Squares Corner](squaresv.md)
