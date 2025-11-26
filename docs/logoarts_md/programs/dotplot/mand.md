# Mandelbrot Set
![Mandelbrot Set](../../art/400x400/mandlebrot.png)

This program draws the classic Mandelbrot 
Set, drawn in shades of green. Use an image editing program such as Photoshop to best adjust the colors.

The Mandelbrot Set is defined by the equation z=z2+c, where c is the starting point in the complex plane, and z is initially zero. This function is iterated many times. If z eventually escapes a circle of radius 2 centred on the origin, then the initial point c is not in the Mandlebrot set. It is assigned a color based on the number of iterations it took for z to escape. Otherwise point c is part of the Mandlebrot set and is colored black.  

We approximate infinity with a large number. 
This assumption is not always valid, but we must make it to avoid infinite looping.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Mand :Mp :Np
 Make "M 0 Make "N 0 Make "Count 0
 Repeat 90 [
 Make "Mnew (Power :M 2) - (Power :N 2) + :Mp
 Make "N (2*:M*:N) + :Np
 Make "M :Mnew
 Make "Count :Count + 1
 If ((Power :M 2) + (Power :N 2)) > 4 [
 SetPC PenCol :Count Stop] ]
End
To PenCol :Theta
 Make "Gre 255 *Sin :Theta
 Output ( List 0 :Gre 0 )
End
To Go :Order
 New
 Make "Size Item :Order [24 16 12 8 6 4 3 2 1]
 SetPW :Size
 Make "Start (Integer :Size/2)-192
 For (List "Y 0 191 :Size) [
 For (List "X :Start 191 :Size) [
 SetPC Black Mand (:X/140)-0.7 :Y/140
 SetXY :X :Y Dot Pos
 SetXY :X Minus :Y Dot Pos ] ]
End
```
