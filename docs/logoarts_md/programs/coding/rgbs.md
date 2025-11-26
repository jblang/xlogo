# R G B
![R G B](../../art/400x400/rgb.png)

Array of color swatches, using evenly spaced RGB values. Order can be 2, 3, 4, 5 or 6.  

For order 3, note that 'Pink' and 'Purple' are slightly different to XLogo names.  

For order 6, comment out code to label rgb values instead of Hex values.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To GridRect :OrderH :OrderV :SqSize
 # draw tile at each column x row grid position
 LocalMake "OffsetH (1+ :OrderH) /2
 LocalMake "OffsetV (1+ :OrderV) /2
 LocalMake "SideH :SqSize /:OrderH
 LocalMake "SideV :SqSize /:OrderV
 For (List "Col 1 :OrderH) [
 For (List "Row 1 :OrderV) [
 SetXY :SideH*(:Col-:OffsetH) :SideV*(:Row-:OffsetV)
 Tile :Col :Row :SideH :SideV] ]
End
To Tile :Col :Row :Wide :High
 # draw color swatch
 Make "Red Int (Int (:Row-1)/:Order) *:Step
 Make "Green Int (:Col-1) *:Step
 Make "Blue Int (Mod (:Row-1) :Order) *:Step
 Make "Hue (List :Red :Green :Blue) SetPC :Hue
 FillPolygon [PD Rectangle :Wide-:Border :High-:Border]
 # label rgb values plus color name or hex number
 If (:Red+:Green<128) [SetPC White] [SetPC Black]
 If Or :Order=2 :Order=3 [Back 6 Label Name :Hue Forward 12]
 If :Order=5 [SetFS 11] If :Order>5 [SetFS 10]
 If :Order=6 [Make "Hue Hex :Hue]
 Label :Hue Wait 4
End
To Hex :Hue
 # return 3 digit hex value of rgb list
 Make "Hex "
 ForEach "Value :Hue [
 Make "Value Round :Value/17
 If (:Value>9) [Make "Value Char :Value+55]
 Make "Hex Word :Hex :Value]
 Output :Hex
End
To Name :Hue
 # return color name of rgb list
 Make "Names [[[Black Navy Blue][Moss Teal Aqua][Green Leaf Cyan]]
 [[Maroon Purple Violet][Olive Gray Steel][Lime Mint Sky]]
 [[Red Rose Magenta][Orange Salmon Pink][Yellow Corn White]]]
 ForEach "Value :Hue [
 Make "Value Int (1+(:Value/127))
 Make "Names Item :Value :Names]
 Output :Names
End
To Rectangle :Wide :High
 # rectangle (wide x high) drawn from centre (tp)
 Back :High/2 Right 90 Back :Wide/2 PenDown
 Repeat 2 [
 Forward :Wide Left 90 Forward :High Left 90]
 PenUp Forward :Wide/2 Left 90 Forward :High/2 # r2c
End
To Go
 New SetFontJustify [1 1]
 Read [Enter order: 2 to 6. Default (no entry) is 3] "Order
 If :Order =" [Make "Order 3] # defauult value if no entry
 GlobalMake "Border 4*(6-:Order)
 GlobalMake "Step 255/(:Order-1)
 GridRect :Order (:Order*:Order) (400-:Border)
End
```
