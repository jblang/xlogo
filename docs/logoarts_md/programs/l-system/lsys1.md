# L-System
![L-System](../../art/400x400/lsys1.png)

Seven different fractal curves are drawn using L-Systems with a single replacement rule for F. All curves produce a single continuous line.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Display :LSys :Order
 # write header title, curve depth and info
 SetPC White
 SetPos [-190 184] Label (List "L-System: :LSys :Name)
 SetPos [-190 -192] Label (List "Rule: "F: :Rule)
 SetPos [130 -192] Label List "Axiom: :Axiom
 SetPos [130 184] Label List "Order: :Order
 SetPos [130 170] Label List "Angle: :Angle
End
To Generate :String :Order
 If :Order < 1 [Output :String]
 Output Generate (ReWrite :String) :Order-1
End
To ReWrite :String
 # replace every F with rule
 Make "NewString "
 For (List "N 1 Count :String) [
 Make "Symbol Item :N :String
 If :Symbol = "F [Make "NewString Word :NewString :Rule] [
 Make "NewString Word :NewString :Symbol] ]
 Output :NewString
End
To Draw :String
 # render each symbol in string
 ForEach "Symbol :String [
 If :Symbol = "F [Forward :Step] [
 If :Symbol = "+ [Right :Angle] [
 If :Symbol = "- [Left :Angle] ] ] ]
End
To L_Sys1 :Order
 Make "Name "Snowflake_Curve
 Make "Scale 3
 Make "Angle 60
 Make "Rule "F-F++F-F
End
To L_Sys2 :Order
 Make "Name "Koch_Square
 Make "Scale 3
 Make "Angle 90
 Make "Rule "F-F+F+F-F
End
To L_Sys3 :Order
 Make "Name "Peano_Curve
 Make "Scale 3
 Make "Angle 90
 Make "Rule "F-F+F+F+F-F-F-F+F
End
To L_Sys4 :Order
 Make "Name "Quadratic_Koch_Curve
 Make "Scale 4
 Make "Angle 90
 Make "Rule "F+F-F-FF+F+F-F # or add in extra central F
End
To L_Sys5 :Order
 Make "Name "Modified_Snowflake_Curve
 Make "Scale 4
 Make "Angle 60
 Make "Rule "F-F+F+F-F
End
To L_Sys6 :Order
 Make "Name "Modified_Snowflake_Curve2
 Make "Scale 4
 Make "Angle 60
 Make "Rule "F-F++FF--F+F
End
To L_Sys7 :Order
 Make "Name "Modified_Snowflake_Curve3
 Make "Scale 3
 Make "Angle 60
 Make "Rule "F-F++F++F--F--F+F
End
To Go :LSys :Order
 New
 Make "Screen 384 Make "Axiom "F
 Wash Home
 Run Word "L_Sys :LSys :Order
 Display :Lsys :Order SetPC Green
 Make "Step :Screen/Power :Scale :Order
 Make "DrawString Generate :Axiom :Order
 If :Order < 2 [Print :DrawString] [Print [printout supressed] ]
 SetPos List Minus :Screen/2 0 SetH 90
 PenDown Draw :DrawString PenUp
End
To GoX
 New
 Make "Screen 384 Make "Order 3 Make "Axiom "F
 For [LSys 1 7] [
 Wash Home Make "Step :Screen
 Run Word "L_Sys :LSys :Order
 Display :Lsys :Order
 Make "DrawString :Axiom
 For (List "Ord 1 :Order) [
 Make "DrawString ReWrite :DrawString
 SetPos List Minus :Screen/2 0 SetH 90
 Make "Step :Step / :Scale
 SetPC Item :Ord [3 1 6 2]
 SetPW Item (:Order+1)-:Ord [1 5 9 11]
 PenDown Draw :DrawString PenUp Wait 60] Wait 120]
End
```

For individual curves, type **go + LSystem + Order** to run. For example **Go 1 4** will draw LSystem 1 to level 4.
