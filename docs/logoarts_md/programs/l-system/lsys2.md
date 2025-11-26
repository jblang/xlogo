# L-System
![L-System](../../art/400x400/lsys2.png)

Five different fractal curves are drawn using L-Systems with two replacement rules X and Y. There is no replacement for F (as in single L-Systems). Each system also includes a Start position and initial Size (Screen Size). This ensures each curve fits the drawing screen.  

All curves produce a single continuous line.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Display :LSys :Order
 # write header title, curve depth and info
 SetPC White SetH 0
 SetPos [-190 184] Label List (Word "L-System_ :LSys ":) :Name
 SetPos [-190 -178] Label (List "RuleX: "X= :RuleX)
 SetPos [-190 -192] Label (List "RuleY: "Y= :RuleY)
 SetPos [130 -192] Label List "Axiom: :Axiom
 SetPos [130 184] Label List "Order: :Order
 SetPos [130 170] Label List "Angle: :Angle
End
To Generate :String :Order
 If :Order < 1 [Output :String]
 Output Generate (ReWrite :String) :Order-1
End
To ReWrite :String
 # replace every X and Y with rule
 Make "NewString "
 For (List "N 1 Count :String) [
 Make "Symbol Item :N :String
 If :Symbol = "X [Make "NewString Word :NewString :RuleX] [
 If :Symbol = "Y [Make "NewString Word :NewString :RuleY] [
 Make "NewString Word :NewString :Symbol] ] ]
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
 Make "Name "Hilbert_Curve
 Make "Scale (Power 2 :Order)-1
 Make "Angle 90
 Make "Axiom "X
 Make "RuleX "-YF+XFX+FY-
 Make "RuleY "+XF-YFY-FX+
 Make "Screen 320
 SetPos [-160 -160] SetH 90
End
To L_Sys2 :Order
 Make "Name "Arrowhead_Curve
 Make "Scale Power 2 :Order
 Make "Angle 60
 Make "Axiom "YF
 Make "RuleX "YF-XF-Y
 Make "RuleY "XF+YF+X
 Make "Screen 360
 SetPos [-180 -160] SetH 90 If Odd? :Order [Left 60]
End
To L_Sys3 :Order
 Make "Name "Peano_Gosper_Curve
 Make "Scale Power (Power 7 0.5) :Order
 Make "Angle 60
 Make "Axiom "FX
 Make "RuleY "Y-XF--XF+FY++FYFY+XF-
 Make "RuleX "+FY-XFXF--XF-FY++FY+X
 Make "Screen 300
 SetPos [-160 110] SetH 90 Right 19.1*:Order
End
To L_Sys4 :Order
 Make "Name "Hilbert2_Curve
 Make "Scale (Power 3 :Order)
 Make "Angle 90
 Make "Axiom "X
 Make "RuleX "XFYFX-F-YFXFY+F+XFYFX
 Make "RuleY "YFXFY+F+XFYFX-F-YFXFY
 Make "Screen 320
 SetPos [-180 -160] SetH 90
End
To L_Sys5 :Order
 Make "Name "Dragon_Curve
 Make "Scale (Power 1.414 :Order) + :Order-3 #pr :Scale
 Make "Angle 90
 Make "Axiom "FX
 Make "RuleX "X-YF-
 Make "RuleY "+FX+Y
 Make "Screen 320
 SetPos [-100 40] SetH 90 Right 45*:Order
End
To Odd? :Num
 # return 'true' if Num odd, else return 'false'
 If Not (Mod :Num 2) = 0 [Output "True] [Output "False]
End
To Go :LSys :Order
 New
 Run Word "L_Sys :LSys :Order
 Display :LSys :Order SetPC Green
 Run Word "L_Sys :LSys :Order
 Make "Step :Screen/:Scale
 Make "DrawString Generate :Axiom :Order
 If :Order < 2 [Print :DrawString] [Print [printout supressed] ]
 PenDown Draw :DrawString PenUp
End
To GoX
 New Make "Order 3
 For [LSys 1 5] [
 Wash Home
 Run Word "L_Sys :LSys :Order
 Display :LSys :Order
 Make "DrawString :Axiom
 For (List "Ord 1 :Order) [
 Run Word "L_Sys :LSys :Ord # start pos
 Make "DrawString ReWrite :DrawString
 SetPC Item :Ord [2 1 4 5]
 SetPW Item (:Order+1)-:Ord [1 3 5 7]
 If :LSys = 5 [Run Word "L_Sys :LSys :Ord+3] #Dragon Curve
 Make "Step :Screen/:Scale
 PenDown Draw :DrawString PenUp
 Wait 60] Wait 120]
End
```

For individual curves, type **go + LSystem + Order** to run. For example **Go 1 4** will draw LSystem 1 to level 4.
