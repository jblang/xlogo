# Screen Saver
![Screen Saver](../../art/400x400/saver1.png)

A simple style screen saver of the Qix 
variety. This program could easily be made shorter. Try different 
settings of random speed. Change the wait time to suit your own 
computer.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Init
 # initialise starting speeds and coordinate lists
 Make "dX RanSpeed Make "dY RanSpeed
 Make "P 0 Make "Q 0
 Make "eX RanSpeed Make "eY RanSpeed
 Make "R 0 Make "S 0
 Make "Pp [ 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 ]
 Make "Qp [ 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 ]
 Make "Rp [ 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 ]
 Make "Sp [ 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 ]
End
To DrawLine
 # move to point PQ and draw a line to point RS
 Make "P :P+:dX Make "Q :Q+:dY
 If :P > 180 [Make "dX Minus RanSpeed]
 If :P < -180 [Make "dX RanSpeed]
 If :Q > 180 [Make "dY Minus RanSpeed]
 If :Q < -180 [Make "dY RanSpeed]
 PenUp SetXY :P :Q
 Make "Pp LPut First Pos :Pp
 Make "Qp LPut Last Pos :Qp
 Make "R :R+:eX Make "S :S+:eY
 If :R > 180 [Make 
 "eX Minus RanSpeed]
 If :R < -180 [Make "eX RanSpeed]
 If :S > 180 [Make 
 "eY Minus RanSpeed]
 If :S < -180 [Make "eY RanSpeed]
 PenDown SetXY :R :S
 Make "Rp LPut First Pos :Rp
 Make "Sp LPut Last Pos :Sp
End
To EraseLine
 # move to point PQ and draw a black line to point RS
 PenUp
 SetXY (First :Pp) (First :Qp)
 PenDown SetPC Black
 SetXY (First :Rp) (First :Sp)
 Make "Pp ButFirst :Pp
 Make "Qp ButFirst :Qp
 Make "Rp ButFirst :Rp
 Make "Sp ButFirst :Sp
End
To RanSpeed
 # return a random speed between 4 and 13
 Output 4 + Random 10
End
To Hue :Theta
 # Output RGB hue list from angle :Theta
 Make "Red Round 127.5*(1+Sin :Theta)
 Make "Green Round 127.5*(1+Sin (:Theta+120))
 Make "Blue Round 127.5*(1+Sin (:Theta+240))
 Output (List :Red :Green :Blue)
End
To Go
 New Init
 Forever [
 SetPC Hue :P+:Q+:R+:S
 DrawLine EraseLine Wait 4]
End
```
