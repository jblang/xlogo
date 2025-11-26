# Wandering Trails
![Wandering Trails](../../art/400x400/trails.png)

Here, the turtle is allowed to 
roam around the graphics screen unhindered.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Roam
 SetPC Angcol Forward 8 Left (30-Random 60)
End
To AngCol
 Make "Red Round 127*(1+Cos (120+Heading))
 Make "Green Round 127*(1+Cos Heading)
 Output ( List :Red :Green 0 )
End
To Go
 New Wrap PenDown
 Repeat 3200 [Roam]
End
```
