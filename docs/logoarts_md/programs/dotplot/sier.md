# Sierpinski Triangle
![Sierpinski Triangle](../../art/400x400/sierpinski.png)

This curve or 'gasket' emerges 
after many points are plotted. The turtle starts in the centre of 
the triangle. It chooses one of the triangle corners at random. The 
turtle then moves half way from its current position to the chosen 
triangle corner and plots a point. It then chooses the next random 
corner and moves half way towards it. The process is repeated 10000 
times. Surprisingly, a pattern emerges within the triangle.  

Can you see how each area gets its color?

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Sier
 Make "Corner 1+Random 3 
 # choose random corner
 Make "CornerPos Item :Corner [ [190 -180] [-190 -180] [0 160] ]
 SetH Towards :CornerPos # turn to face corner
 Forward (Distance :CornerPos)/2 
 # move half way towards corner
 PenDown Forward 0 PenUp # plot pixel
 SetPC Run Item :Corner [Green Yellow Magenta]
End
To Go
 New SetPC Black
 Repeat 40000 [Sier] # repeat 40000 times
End
```

Universal Sierpinski Gasket

The previous program has been re-written to allow for any order 
of Sierprinski curve. Just enter **Go** and the order, eg **Go 
6** to produce a curve of order 6. Try **Go** 111.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Sier :Order :Size :Frac :Ang
 Make "Angle :Ang*(Random :Order) # choose random corner
 SetX :Frac * ((First Pos) +:Size*Sin :Angle)
 SetY :Frac * ((Last Pos) +:Size*Cos :Angle)
 PenDown Forward 0 PenUp # plot pixel
End
To Go :Order
 New
 Make "Size (:Order-2)*180
 Make "Frac 1/(:Order-1)
 Make "Ang 360/:Order
 Repeat 10000 [Sier :Order :Size :Frac :Ang] # repeat 10000 times
End
```
