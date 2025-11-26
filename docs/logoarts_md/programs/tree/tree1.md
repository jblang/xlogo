# Basic Tree
![Basic Tree](../../art/400x400/tree.png)

This program draws a simple recursive tree.  

The angle between the two new branches is set at 60 degrees (shown in red in the Go procedure). Changing this angle (to 
say 50 generates different types of 
tree. See [how to](../../ipt/info/tuts.md).

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Tree :Length
 If :Length< 10 [Stop] # ends recursion if length too small
 SetPW :Length/9 # reduce pen width as branch length gets smaller
 SetPC TreeCol :Length # branch color depends on length
 Forward :Length Left :Angle/2
 Tree :Length*0.75 Right :Angle # tree proc with 3/4 branch length
 Tree :Length*0.75 Left :Angle/2
 PenUp Back :Length PenDown # return to starting point
End
To TreeCol :Length
 Make "Green Round 2.5*(100-:Length) # green depends on length
 Output ( List 255 :Green 0 ) # red set to 255 and blue to 0
End
To Go
 New Back 160 PenDown
 Make "Angle 60 Tree 90 # initial angle of 60 shown in red
End
```

Also see [Binary Switch](../recur/switch.md).
