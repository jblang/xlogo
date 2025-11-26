# After Image

This illusion is animated and can only be seen in XLogo. The gallery image won't produce any effect.  

Focus on the central dot. The 'missing' rotating pink dot will show as green.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Stamp
 Forward 150 SetPC SC Dot Pos
 Wait 14
 SetPC Magenta Dot Pos
 Back 150 Right 30
End
To Go
 New
 SetSC LightGray SetPW 7
 SetPC Black Dot Pos SetPW 30
 Forever [Stamp]
End
```
