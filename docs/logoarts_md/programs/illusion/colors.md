# The Stroop Effect

Try to say the color of each word, instead of the word itself. It is quite difficult. This is because a conflict arises between the two sides of your brain. The right half of your brain is trying to say the color, but the left half is trying to say the word.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Go
 New SetFontSize 24
 Make "Cols [Red Green Blue Yellow Purple Brown White Gray]
 Make "X 60
 For [Y 100 -140 -25] [
 Make "X Minus :X
 SetXY :X :Y
 Make "ColName Pick :Cols
 SetPC Run Sentence Pick :Cols [ ]
 SetX (First Pos) - (LabelLength :ColName)/2 - :Y/2
 Label :ColName]
End
```
