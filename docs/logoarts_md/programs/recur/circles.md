# Circles
![Circles](../../art/400x400/circles.png)

A collection of recursive circles. In theory, the pattern repeats endlessly. Drawn with Orders from 2 to 7.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Circ :Size
 If :Size < 5 [Stop]
 Circle :Size
 Repeat :Order [
 Forward :Size-:K*:Size
 Circ :K*:Size
 Back :Size-:K*:Size
 Left 360/:Order]
End
To Go
 New
 For [Ord 2 7] [
 GlobalMake "Order :Ord Wash
 Make "K (Sin 180/:Order) / ((Sin 180/:Order)+1)
 Circ 190 Wait 100]
End
```
