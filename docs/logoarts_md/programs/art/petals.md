# Petals
![Petals](../../art/400x400/petals.png)

From a very simple procedure, a series 
of 7 flower patterns is produced.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Curve
 Repeat 15 [Forward 5 Right 6]
 Make "Col Abs :Col-17 # alternate between colors 2 and 15
 SetPC :Col
 Repeat 15 [Forward 5 Right 6]
End
To Go
 New Make "Col 2
 For [N 1 7] [
 PenUp SetPos [-50 -30] PenDown Wash
 Repeat 8 [Right 45 Repeat :N [Curve Right 90 Wait 5]]
 Wait 128]
End
```

Remove the **wash** command for overlaid patterns.
