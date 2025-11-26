# Dragon Curve
![Dragon Curve](../../art/400x400/drag.png)

This is a mathematical curve you can 
see by folding a long strip of paper and uncreasing to a 90 degree 
angle. The curve starts with a single left hand turn [L]. By inspecting 
the paper strip, you will see that each fold crates a complete series 
of folds given by

List + L + Inverse Reverse List, where L denotes a 90 degree left 
turn and R a 90 degree right turn.

The lines get too small above order 11.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Display :Level
 # write header title and curve level
 SetPC White SetPos [-190 184]
 Label Sentence [Dragon Curve Level] :Level
End
To Dragon :Level :Curve
 If :Level = 0 [Output :Curve]
 Make "ReflectCurve Reverse :Curve
 For (List "N 1 Count :Curve) [
 Make "Turn Item :N :ReflectCurve
 If :Turn = "L [Make "ReflectCurve SetItem :ReflectCurve :N "R]
 [Make "ReflectCurve SetItem :ReflectCurve :N "L] ]
 Make "Curve Sentence Sentence :Curve "L :ReflectCurve
 Output Dragon :Level-1 :Curve
End
To Draw :Level :Curve
 Make "Root2 SqRt 2
 Make "Dist 4*Power (:Root2) (9-:Level)
 Make "Dis 0.05 *Pi*:Dist
 Forward :Dist
 For (List "N 1 Count :Curve) [
 Make "Turn Item :N :Curve
 If :Turn = "L [TurnLeft :Dis] [TurnRite :Dis]]
 Forward :Dist
End
To TurnLeft :Dis
 Left 4.5 Repeat 10 [Forward :Dis Left 9] Right 4.5
End
To TurnRite :Dis
 Right 4.5 Repeat 10 [Forward :Dis Right 9] Left 4.5
End
To Go :Level
 New Display :Level SetPC Green
 Home SetX Minus 110
 Right (45 *:Level)-225 PenDown
 #draw dragon curve list
 Draw :Level Dragon :Level [L]
End
```

Type **go + level** for example **Go 5** to run.

For a square dragon curve (better for orders 10-13), replace Draw 
procedure with the following.

```logo
To Draw :Level :Curve
 Make "Root2 SqRt 2
 Make "Dist 4*Power (:Root2) (9-:Level)
 Forward :Dist
 For (List "N 1 Count :Curve) [
 Make "Turn Item :N :Curve
 If :Turn = "L [Forward :Dist Left 90 Forward :Dist]
 [Forward :Dist Right 90 Forward :Dist]]
 Forward :Dist
End
```
