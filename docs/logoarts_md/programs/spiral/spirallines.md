# Spiral Lines
![Spiral Lines](../../art/400x400/spiral.png)

Procedure 'spiral' draws a line, turns through a fixed angle, then 
calls itself with a line length 4 pixels longer. The result is a 
series of angled lines giving a twisted spiral effect.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Spiral :Length :Angle
 SetPC PenCol :Length # pen color depends on line length
 If :Length > 390 # if line length too long ...
 [Forward :Length/2 Stop] # draw half line length and stop
 Forward :Length Left :Angle # draw line and rotate a fixed angle
 Spiral :Length+4 :Angle # do again with a length 4 pixels longer
End
To Pencol :Length
 Make "Green Round :Length/1.62 # green depends on length
 Output ( List 0 :Green 0 ) # red and blue set to 0
End
To Go
 For [Angle 158 176 2] [ # repeat 10 times
 New PenDown
 Wait 5 Spiral 1 :Angle Wait 200] # begin spiral with a length 1
End
```
