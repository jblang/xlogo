# Orbit Explorer
![Orbit Explorer](../../art/400x400/orbit.png)

This is similar to Pursuit curves. 
Three turtles are created. Turtle 3 orbits turtle 2, turtle 2 orbits 
turtle 1 and turtle 1 orbits turtle 3. Hence all three turtles are 
locked together. They are each given a weight. This determines how 
fast they orbit. The speed is calculated as Distance divided by the 
weight.

This is still an experimental program. Add another weight into the 
list for four orbiting turtles. This is very random / unstable.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Init :N
 If :N <2 [Print [Need more than one turtle!] Stop]
 # Make "Weights [8 -4 2]
 # Make "Weights [8 -4 24]
 # Make "Weights [8 -14 11] # neat
 # Make "Weights [13 -5 -9]
 Make "Weights [20 -14 8]
 Repeat :N [
 SetTurtle RepCount
 Make "Angle RepCount*360/:N
 SetPC Hue :Angle PenUp
 SetXY (40*Sin :Angle) (40*Cos :Angle) PenDown]
End
To Hue :Theta
 # Output RGB hue list from angle :Theta
 Make "Red Round 127.5*(1+Sin :Theta)
 Make "Green Round 127.5*(1+Sin (:Theta+120))
 Make "Blue Round 127.5*(1+Sin (:Theta+240))
 Output (List :Red :Green :Blue)
End
To Go :N
 New SetTurtlesMax :N+2 Init :N
 Forever [
 Repeat :N [
 SetTurtle RepCount Make "myPos Pos
 SetTurtle RepCount+1
 If RepCount+1>:N [SetTurtle 1]
 HideTurtle SetH Towards :myPos
 Make "Dist Distance :myPos
 Left 90 ShowTurtle
 Forward (Item RepCount :Weights) 
 /:Dist ] ]
End
```

Also see [Spiro Explorer](../spiro/spiroexplo.md)
