# Snowflake
![Snowflake](../../art/400x400/snowflakes/s1.png)

A random path is created with 
only turns of multiples of 60 degrees. This is duplicated by twelve 
turtle in six pairs. Each pair starts 60 degrees apart and draws a 
mirror image, as one turns right, the other turns left.  

Turtle 0 leads the way and 'Stops' the loop if the random path is 
too far from the origin.  

No two the same!

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Check
 SetTurtle 0
 Left :Dir Forward :Length
 If (Distance [0 0]) >180 [
 Back :Length Right :Dir Output "True] [Output "False]
End
To Snowflake
 Make "Dir 60 * (Random 6)
 Make "Length 1+Random 32
 If Check [Make "Edge "False Stop]
 For [N 1 12] [
 SetTurtle :N
 SetPC 6.5 + (:Parity/2) # 6 or 7
 Left :Parity * :Dir Forward :Length
 Make "Parity Minus 1 * :Parity]
End
To Go
 New
 # SetPC Red PenDown # comment in to see check turtle 0
 Forever [
 SetTurtle 0 Home Forward 32 Wash
 Make "Parity 1 Make "Edge "True
 For [N 1 12] [
 SetTurtle :N PenUp Home
 SetH 15+(:N * 30) # SetHeading 60 * Round (:N/2)
 Forward 32 PenDown]
 While [:Edge] [
 Repeat 16 [Snowflake] ]
 Wait 240]
End
```
