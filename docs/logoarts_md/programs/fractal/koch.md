# Koch Snowflake Curve
![Koch Snowflake Curve](../../art/400x400/koch.png)

The Koch Snowflake curve encloses a finite area but has infinite perimeter.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Init
 PenUp SetPos [-160 -90] PenDown
End
To KOut :Level :Side
 SetH 30 Repeat 3 [Koch :Level :Side Right 120]
End
To Koch :Level :Side
 If :Level < 1 [
 Wait Integer (0.2*:Side) # Comment out this line for no waits
 Forward :Side Stop]
 Koch :Level-1 :Side/3 Left 60
 Koch :Level-1 :Side/3 Right 120
 Koch :Level-1 :Side/3 Left 60
 Koch :Level-1 :Side/3
End
To Go :Level
 New Init KOut :Level 320
End
```

Type in **go** and the **level** eg **go 2** to draw a level 
2 Koch outside curve.

```logo
To KIn :Level :Side
 SetH 90 Repeat 3 [Koch :Level :Side Left 120]
End
To Go :Level
 New Init KIn :Level 320
End
```


Copy and paste the above two procedures into the XLogo editor and 
type in **go** and the **level** eg **go 2** to draw a level 
2 Koch inside curve.

```logo
To GoX :LevelIn :LevelOut
 New Init SetPC Gray KIn 0 320
 SetPC Red KIn :LevelIn 320
 SetPC Green KOut :LevelOut 320
End
```


Copy and paste the above procedure into XLogo editor and type in **GoX** and the inside and outside levels eg **GoX 3 2** to draw Koch curves 
both inside and outside in red and green.
