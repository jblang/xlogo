# Dust Collecting
![Dust Collecting](../../art/400x400/dust.png)

A simulation of collecting drifting dust. 
A single particle of dust moves at random through the air. There 
is a slight bias towards the central origin. If the dust moves close 
to another dust particle it 'sticks' to it and stops moving. Another 
particle is then generated at a random position and it floats freely 
until it too meets the already gathered dust clump.

A typical pattern emerges. Dust usually sticks to the extremeties 
of the dust clump, as it is these points it is most likely to touch 
first. This produces extended dust fingers. As dust floats towards 
the centre, fingers tend to shield the space 'behind' them producing 
dust free areas.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Dust
 Home Right Random 360 Forward :Max Make "Col 0
 While [:Max < 182] [
 While [:Col = 0] [
 SetPC Black Dot Pos # erase previous dot
 SetH Towards [ 0 0 ]
 Left 30 Right Random 420 Forward 1
 Make "Col Vicinity
 SetPC Yellow Dot Pos]
 Make "MaxNew 2 + Distance [ 0 0 ]
 If :MaxNew > :Max [ Make "Max :MaxNew]
 Dust ]
End
To Vicinity
 # check occupation of surrounding pixels
 # return '1' if neighbouring dust is present
 For [I -1 1] [
 For [J -1 1] [
 Make "R Item 1 FindColor List :I+X :J+Y
 If :R = 255 [Output 1] ] ]
 Output 0
End
To Go
 New SetPC Yellow
 Dot Pos # draw a seed dust speck
 Make "Max 4
 Dust
End
```
