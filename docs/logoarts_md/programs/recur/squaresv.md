# Squares (Corner)
![Squares (Corner)](../../art/400x400/squares.png)

This program produces a fractal of nested squares. Each square 
has a smaller square added to each of its corners.

The reduction factor is a half. This means that each square is half 
the size. The ever smaller squares never quite touch.

For less reduction, the squares will overlap, which looks messy. 
However, with a reduction factor equal to 1.618 (the Golden Ratio), 
squares will exactly overlap. They are actually drawn precisely 
on top, giving a more tidy effect.

For a Golden ratio pattern, change the 2 to 1.618034 and reduce the side to 96.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To RSquare :Order :Side
 If :Order < 0 [Stop]
 Right 180
 Repeat 4 [
 SetPW :Order+1 # comment out for no line widths
 PenDown Forward :Side PenUp Left 90 Wait 1
 RSquare :Order-1 :Side/2] # Golden Ratio replace 2 with 1.62
 FillSquare # comment out for no fills
 Left 180
End
To FillSquare
 SetPC DarkGreen Left 45 Forward 5 Fill
 Back 5 Right 45 SetPC Cyan
End
To Go :Order
 New SetPC Cyan
 Make "Side 128 # Replace 128 with 96 for Golden Ratio
 SetPos List Minus :Side/2 Minus :Side/2 Left 90
 RSquare :Order :Side
End
```

Type **Go order** to run.

Also see [Squares Center](squares.md)
