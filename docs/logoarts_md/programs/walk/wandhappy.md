# Wanderer
![Wanderer](../../art/400x400/wander.png)

An optimused version of this program can be found [here](wandhappyopt.md).  

The turtle follows a simple formulae. It starts at initial heading 'IA'. Then, it moves forward a set distance 'LL' and turns right through angle 'SA'. This is repeated 'CY' number of times.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Wander :IA :SA :LL :CY
 New SetPC White Display :IA :SA
 Home SetPC Green PenDown
 Repeat :CY [Forward :LL Right :IA Make "IA :IA+:SA]
End
To Display :IA :SA
 SetXY -192 184 Label [Happy Wanderer]
 SetPos [-40 184] Label Sentence [Initial Angle] :IA
 SetPos [90 184] Label Sentence [Step Angle] :SA
End
To Go
 Wander 25 54 15 180
End
```
  

| Wander 0 7 12 1000 
 Wander 0 11 12 1000 
 Wander 0 13 12 1000 
 Wander 0 123 18 400 
 Wander 0 55 28 400 
 Wander 0 49 12 2000 
 Wander 10 80 48 36 
 Wander 11 80 48 400 
 Wander 30 90 42 24 
 Wander 0 71 13 600 
 Wander 15 50 11 500 # linear 
 Wander 17 54 35 200 
 Wander 25 54 15 200 # sun 
 Wander 29 54 15 200 # second sun 
 Wander 25 57 7 720 # complex sun 
 Wander 2 69 7 800 | Wander IA SA LL CY 
 
 The four values are: 
 
 IA - Initial Angle 
 SA - Step Angle 
 LL - Line Length 
 CY - No of Cycles |
| Copy and paste anyoneline of code 
for a different wandering. |
