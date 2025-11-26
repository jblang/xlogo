# Orbinson Hering Ehrenstein

Set of 3 illusions using radiating or concentric lines to distort a shape or figure.

Orbinson Illusion:

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Square :Side
 # square (side x side) drawn from centre (tp)
 Back :Side/2 Right 90 Back :Side/2 PenDown
 Repeat 4 [
 Forward :Side Left 90]
 PenUp Forward :Side/2 Left 90 Forward :Side/2 # r2c
End
To Go
 New SetPC Red SetPW 3
 SetPos [0 -70] Square 200
 SetPos [0 275] SetH 204 SetPW 1 SetPC Yellow PenDown
 Repeat 17 [Forward 510 Back 510 Left 3]
End
```

Hering Figure:

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Go
 New SetPC Yellow PenDown
 Repeat 2 [
 Left 20 Repeat 29 [Forward 300 Back 300 Left 5]
 Left 15]
 SetPC Red SetPW 3
 PenUp SetPos [-60 -200] PenDown Forward 400
 PenUp SetPos [60 -200] PenDown Forward 400
End
```


Ehrenstein Illusion:

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Square :Side
 # square (side x side) drawn from centre (tp)
 Back :Side/2 Right 90 Back :Side/2 PenDown
 Repeat 4 [
 Forward :Side Left 90]
 PenUp Forward :Side/2 Left 90 Forward :Side/2 # r2c
End
To Go
 New SetPC Yellow
 For [Radius 55 190 15] [
 Circle :Radius]
 SetPW 3 SetPC Red Right 45 Square 240
End
```
