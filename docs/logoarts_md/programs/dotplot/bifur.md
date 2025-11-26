# Bifurcation Diagram
![Bifurcation Diagram](../../art/400x400/bifur.png)

Bifurcation diagram is plotted 
from the function f(x) = kx (1-x) for different values of k ( a constant).

We start with k as 1 and x as 0.01. The new value of the function 
is calculated and plotted on the diagram. The value is then substituted 
back in the formulae and a new value of x calculated. This is repeated 
64 times. The value of k is then increased slightly and the whole 
process repeated.

When k is less than 2.8 a simple curve is produced. As k increases, 
the curve splits into two. The formulae has two stable states. At 
higher values of k, the formulae becomes chaotic, with multiple points 
being plotted.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Init
 # change the start and end for magnified plots
 Make "Start 1 Make "End 4
 Make "Step (:End-:Start)/360
End
To Function :K :X
 Output :K*:X * (1-:X)
End
To Display
 # write header title and footer values
 PenUp SetPC White SetH 0
 SetPos [-190 182] Label [Bifurcation Diagram]
 SetPos [-190 -190] Label ( List "k\ = :Start "to :End )
End
To Go
 New Init Display SetPC Green
 Make "X 0.1 Make "K :Start
 # first iterate to eliminate transients
 Repeat 128 [
 Make "Xn Function :K :X
 Make "X :Xn]
 # actual iteration
 For (List "K :Start :End :Step) w[
 Repeat 64 [
 Make "Xn Function :K :X
 Make "X :Xn
 # calculate horiz x
 Make "Xh (360/(:End-:Start))*:K-180-((360/(:End-:Start))*:Start)
 Dot List :Xh (360*:X)-180] ]
End
```
