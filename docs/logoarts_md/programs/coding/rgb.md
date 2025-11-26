# R G B
![R G B](../../art/400x400/rgb.png)

A common requirement is to generate 
a color from some input, usually an angle or distance from the point 
0 0. These procedures use up to three out of step sine waves to generate 
a three number list, representing the amount of Red, Green and Blue 
in the color. Each number must be between 0 (min) and 255 (max).    

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To RGB
 Make "Red [Abs 255 *Sin (:X/2)]
 Make "Gre [Abs 255 *Sin (:X/2 + 120)]
 Make "Blu [Abs 255 *Sin (:X/2 + 240)]
End
To Disp
 SetPC White SetPos [-180 150]
 Label [RGB Color Palette]
 SetPos [-180 147]
 Right 90 PenDown Forward 100 PenUp Left 90
 SetPos [-170 100] SetPC Red
 Label Sentence "Red= :Red
 SetPos [-170 80] SetPC Green
 Label Sentence "Grn= :Gre
 SetPos [-170 60] SetPC Blue
 Label Sentence "Blu= :Blu
End
To Axiss
 SetPC White SetPos [-180 0] PenDown
 SetPos [-180 -86] SetPos [181 -86] PenUp
 SetPos [-190 8] Label "255
 SetPos [-192 -88] Label "0
 SetPos [184 -88] Label "X
 SetPos [-182 -104]
 For [A 0 360 60] [Label :A Right 90 Forward 58 Left 90]
End
To Graph
 For [X 0 360] [
 Make "R Run :Red
 Make "G Run :Gre
 Make "B Run :Blu
 SetPC ( List :R 0 0 ) Dot List :X-180 :R/3-85
 SetPC ( List 0 :G 0 ) Dot List :X-180 :G/3-85
 SetPC ( List 0 0 :B ) Dot List :X-180 :B/3-85
 SetPC ( List :R :G :B ) Spectrum :X]
End
To Spectrum :X
 SetXY :X-180 Minus 130 PenDown SetY -160 PenUp
 SetXY 140 150 SetH :X SetPW 2 PenDown
 Forward 30 PenUp SetPW 1
End
To Go
 New RGB Disp Axiss Graph
End
```

Try out an alternative color generator by pasting in one of the 
following procedures.

```logo
To RGB
  # this is library item 'Hue'
  Make "Red [127.5 *(1+Sin :X)]
  Make "Gre [127.5 *(1+Sin (:X + 120))]
  Make "Blu [127.5 *(1+Sin (:X + 220))]   # not 240!
End
```

```logo
To RGB
  # this is library item 'Hue2'
  Make "Red [Abs 255 *Sin :X]
  Make "Gre [Abs 255 *Sin (:X + 120)]
  Make "Blu [Abs 255 *Sin (:X + 240)]
End
```

```logo
To RGB
  Make "Red [127 *((Sin :X) + Abs Sin :X)]
  Make "Gre [127 *((Sin :X + 120) + Abs Sin :X + 120)]
  Make "Blu [127 *((Sin :X + 240) + Abs Sin :X + 240)]
End
```

```logo
To RGB
  Make "Red [Abs 255 *Sin (:X/2)]
  Make "Gre [Abs 255 *Sin (:X/2 + 120)]
  Make "Blu [Abs 255 *Sin (:X/2 + 240)]
End
```
