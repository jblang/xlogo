# Harmonograph
![Harmonograph](../../art/400x400/harmonograph.png)

A harmonograph is an instrument for drawing complex curves. They 
can often be found in good toy shops.

There are several models available, but generally they consist of 
a pen resting lightly on a flat plate. Both the pen and the plate 
move independently and are connected to heavy pendulums. Setting 
both pendulums swinging results in a complex pattern being drawn.

The pattern becomes smaller as friction reduces the swing of the 
pendulums, (the pendulums are damped).

This program mimics the harmonograph by two equations, one for horizontal 
motion and one for vertical. They are similar to the [Spirograph](spirog.md) equations, except that they incorporate a damping factor, which slowly 
reduces the size of the curve.

X = ke-rt [ Sin (f1t + p1) + Sin 
(f2t + p2) ]  

Y = ke-rt [ Sin (f3t + p3) + Sin 
(f4t + p4) ]

where

k = 90 just a constant to fill the drawing screen (c is the speed 
of light)  

e = 2.718, a famous constant  

r = 0.00015 another constant, the 'damping' factor  

f1,f2,f3,f4 = random pendulum frequencies, 1,2,3...9  

p1,p2,p3,p4 = random phase angles, 0...359  

T = time steps, 0,1,2,3,...5000

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Spir
 SetPos XYCurve 0 PenDown
 For [T 0 5000] [SetPC Hue2 :T SetPos XYCurve :T]
End
To XYCurve :T
 Make "KK :K * Power :e ( Minus 0.00015 * :T )
 Make "X :KK*((Cos ((:f1*:T) + :p1 )) + (Cos ((:f2*:T) + :p2 )))
 Make "Y :KK*((Cos ((:f3*:T) + :p3 )) + (Cos ((:f4*:T) + :p4 )))
 Output (List :X :Y)
End
To Init
 Make "K 90
 Make "e 2.718
 Make "f1 Integer 1+ Random 9
 Make "f2 Integer 1+ Random 9
 Make "f3 Integer 1+ Random 9
 Make "f4 Integer 1+ Random 9
 Make "p1 Integer Random 360
 Make "p2 Integer Random 360
 Make "p3 Integer Random 360
 Make "p4 Integer Random 360
End
To Display
 # write header title and footer values
 SetPC White
 SetPos [-190 184] Label "Harmonograph
 SetPos [-190 -190] Label ( List "Freqs: :f1 :f2 :f3 :f4 )
 SetPos [34 -190] Label ( List "Phase: :p1 :p2 :p3 :p4 )
End
To Hue2 :Theta
 # Output RGB hue list from angle :Theta
 Make "Red Abs 255*Sin :Theta
 Make "Green Abs 255*Sin (:Theta+120)
 Make "Blue Abs 255*Sin (:Theta+240)
 Output (List :Red :Green :Blue)
End
To Go
 Repeat 12 [New Init Display Spir Wait 200]
End
```
