# Fractal Fern
![Fractal Fern](../../art/400x400/fern2.png)

From the internet, here are function mappings for a fractal fern. Each mapping 
updates the point X,Y with a new point Xn,Yn.

w1(x,y)=(0.81x+0.07y+0.12, -0.04x+0.84y+0.195),  

w2(x,y)=(0.18x-0.25y+0.12, 0.27x+0.23y+0.02),  

w3(x,y)=(0.19x+0.275y+0.16, 0.238x-0.14y+0.12),  

w4(x,y)=0.0235x+0.087y+0.11, 0.045x+0.1666y).

The Henon curves used just one mapping equation. The fern uses four 
seperate mappings. Each mapping is chosen with a certain probability, 
given by:

p1=(0.701, p2=0.150, p3=0.129, p4=0.020).

So the probability of selecting the W1 mapping is 70.1% (or 701/1000). 
This procedure is called "The Chaos Game".

Putting all this together produces the following program. It uses 
25,000 iterations to draw the fern.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Fern
 If :Prob < 701 [ # 0 to 700 = 70.1%
 Map 0.81 0.07 0.12 Minus 0.04 0.84 0.195 Stop]
 If :Prob < 851 [ # 701 to 850 = 15%
 Map 0.18 Minus 0.25 0.12 0.27 0.23 0.02 Stop]
 If :Prob < 980 [ # 851 to 979 = 12.9%
 Map 0.19 0.275 0.16 0.238 Minus 0.14 0.12 Stop]
 # Else 980 to 999 = 2%
 Map 0.0235 0.087 0.11 0.045 0.1666 0
End
To Map :a :b :c :d :e :f
 Make "Xnew (:a*:X) + (:b*:Y) + :c
 Make "Y (:d*:X) + (:e*:Y) + :f
 Make "X :Xnew
End
To Scale :XorY
 # XorY are in the range 0 to 1, we need -200 to 200
 Output (400*:XorY)-200
End
To Go
 New
 Make "X 0 Make "Y 0 # Global variables
 Repeat 25000 [
 Make "Prob Random 1000
 Fern
 Dot List Scale :X Scale :Y ]
End
```
