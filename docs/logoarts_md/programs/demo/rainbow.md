# Rainbow
![Rainbow](../../art/400x400/rainbow.png)

This draw a simple rainbow. The colors 
are chosen in order from a color list. The six colored bands are 
drawn with the arc command.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Rainbow
 SetPW 10 # big pen
 Make "Cols [1 13 3 2 4 15]
 For [N 1 6] [
 SetPC Item :N :Cols
 Arch 180 (180-12*:N) Wait 30]
End
To Arch :Angle :Radius
 # symmetrical arc drawn relative to turtle heading
 Arc :Radius Heading-:Angle/2 
 Heading+:Angle/2
End
To Go
 New Rainbow
End
```

An alternative procedure, where the color names are used instead of 
numbers.

```logo
To Rainbow
 SetPW 10 # big pen
 Make "Cols [Red Orange Yellow Green Blue Purple]
 For [N 1 6] [
 SetPC Run Item :N :Cols
 Arch 180 (180-12*:N) Wait 30]
End
```
