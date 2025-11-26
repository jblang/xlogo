# Leaning Tree
![Leaning Tree](../../art/400x400/tree_lean.png)

Here, the left hand branches are shorter than branches to the right 
(0.65 against 0.85). The result is a leaning or wind swept tree. One 
sided growth in nature produces spirals seen in animal horns and sea 
shells.  

The **New** procedure starts the tree to the left with a 5 degree 
angle.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Tree :Length
 If :Length< 10 [Stop] # ends recursion if branch length too small
 SetPW :Length/9 # reduce pen width as branch length gets smaller
 SetPC TreeCol :Length # branch color depends on length
 Forward :Length Left :Angle/2
 Tree :Length*0.65 Right :Angle # short branch length
 Tree :Length*0.85 Left :Angle/2 # long branch length
 PenUp Back :Length PenDown # return to starting point
End
To TreeCol :Length
 Make "Green Round 2.5*(100-:Length) # green depends on length
 Output ( List 255 :Green 0 ) # red set to 255 and blue to 0
End
To Go
 New SetPos [-94 -160] Right 5 PenDown
 Make "Angle 50 Tree 85 # initial angle of 50 shown in red
End
```
