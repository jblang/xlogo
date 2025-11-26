# Random Lines
![Random Lines](../../art/400x400/lines.png)

This program draws a dozen random 
lines and labels each point with its co-ordinates. It has 
4 procedures: New, RanPoint, LabXY and Go.
To run the program:

1. Hi-light **all** the code in the yellow box (click 
and drag).
2. **Copy** the code (Edit Copy).
3. Switch to **XLogo** and open the **Editor** window.
4. **Paste** in the code (Edit Paste).
5. Press the **Penguin** button to save the code and close 
the Editor.
6. Type in '**go**' and press <Enter> to run the 
program.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To RanPoint
 SetXY (Random 340)-170 (Random 380)-190
End
To LabXY
 SetPC White Label Pos
End
To Go
 New SetFontJustify [1 1] LabXY
 SetPenWidth 2 PenDown Repeat 12 [
 SetPC (1+Random 6) RanPoint LabXY Wait 30]
 SetPC (1+Random 6) Home
End
```

XLogo should create the graphic as shown on the right.  

To clear the graphics window and reset the turtle, type in '**new**'.  

Check out the code comments and explanation below.

```logo
'New' sets 
 the screen to black, hides the turtle and lowers the pen.
'Ranpoint' 
 picks a random pixel within the graphics window. Both x and 
 y axis range from -200 to +200 pixels. Points must not be too 
 close to the window edges, or the labels can partly disappear.
'LabXY' labels 
 each point with the turtles X abd Y position. Each label is 
 printed centred on 
 the point.
'Go' calls 
 the New, RanPoint and LabXY procedures. It also picks a random 
 pen color between 1 and 7, and pauses for half (30/60) second between each line.
 Finally, the pen returns home.
```
