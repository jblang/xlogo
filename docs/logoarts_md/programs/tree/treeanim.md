# Recursive Tree
![Recursive Tree](../../art/400x400/tree.png)

This program draws a series of 
recursive trees, with varying branch angle from 0 to 180 degrees.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Tree :Length :Angle
 If :Length < 10 [Stop] # ends recursion if length too small
 SetPW :Length/9 
 # reduce pen width as branch length gets smaller
 SetPC TreeCol :Length # branch color depends on length
 Forward :Length Left :Angle/2
 Tree :Length*0.71 :Angle Right :Angle # tree with 0.71 branch length
 Tree :Length*0.71 :Angle Left :Angle/2
 PenUp Back :Length PenDown # return to starting point
End
To TreeCol :Length
 Make "Green Round 2.5*(100-:Length) # green depends on length
 Output ( List 255 :Green 0 ) # red set to 255 and blue to 0
End
To Go
 New Animation Back 150 PenDown
 For [Angle 0 180 4] [ # angle from 0 to 180
 Wash Tree 100 :Angle Refresh Wait 20]
End
```

Also see other recursive [tree](../top/trees.md) programs.
