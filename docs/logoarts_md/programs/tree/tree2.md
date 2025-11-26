# Random Trees
![Random Trees](../../art/400x400/treerls.png)

Random Branch Length  

In the first program, each new tree has branches three quaters 
(0.75) the length of the current branch. If we change this value, 
the whole tree is drawn proportionally smaller or larger.  

However, if we randomize this 
value, then each branch has a random length, which creates a more 
natural looking tree. Also, some branches truncate early, as they 
become too short (and others go on for longer). Length is set to ((Random 
4)+6)/10, which gives a value between 0.6 and just under 1.0.  

The **go** procedure has been modified to draw 99 random trees, 
with a short pause inbetween.

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
 Tree :Length * Pick [0.7 0.75 0.8] Right :Angle
 Tree :Length * Pick [0.7 0.75 0.8] Left :Angle/2
 PenUp Back :Length PenDown # return to starting point
End
To TreeCol :Length
 Make "Green Round 2.5*(100-:Length) # green depends on length
 Output ( List 255 :Green 0 ) # red set to 255 and blue to 0
End
To Go
 Repeat 12 [
 New PenUp Back 160 PenDown
 Make "Angle 60 Tree 80 Wait 200]
End
```

Random Branch Angle  

Alternativley, we can randomly alter the branch angle for each 
new tree. We now need two parameters for each procedure, length and 
angle. Length is reset to three quaters (0.75) as before. Angle is 
set to ((Random 60)+25), which gives a value between 25 and 85 degrees. 
This produces trees of a slightly messy, mixed specimen appearance.

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
 LocalMake "Angle Pick [24 32 40 48 56 64 72]
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
 Repeat 12 [ New PenUp Back 160 PenDown
 Tree 90 Wait 200]
End
```


Random Tree Angle  

Finally, we can alter the angle between one tree procedure 
and the next. I've named this 'TreeAngle' and used a procedure to 
return a TreeAngle of Random (**:**Angle-10)+**:**Angle/10, 
which for an **:**Angle of 50 degrees, gives a value between 5 and 45 degrees.  

Again, changing this angle (to say 70) 
generates different types of random trees (or shrubs).

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
 LocalMake "TreeAngle :Angle*Pick [0.25 0.375 0.5 0.625 0.75]
 Forward :Length
 Left :TreeAngle
 Tree :Length*0.75 Right :Angle # tree proc with 3/4 branch length
 Tree :Length*0.75 Left :Angle
 Right :TreeAngle
 PenUp Back :Length PenDown # return to starting point
End
To TreeCol :Length
 Make "Green Round 2.5*(100-:Length) # green depends on length
 Output ( List 255 :Green 0 ) # red set to 255 and blue to 0
End
To Go
 Repeat 12 [ New PenUp Back 160 PenDown
 Make "Angle 60 Tree 80 Wait 200]
End
```

- Gallery
