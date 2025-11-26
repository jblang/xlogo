# Ring Wave
![Ring Wave](../../art/400x400/ringwave.png)

Here, a circle (or ring) is drawn by repeating 360 times, a small step forward, then a turn through 1 degree. On top of the circle, a sine wave is added. If repeated twice, the circle is squashed to form an elliptical shape. The size, (amplitude) of the sine wave can be increased for more interesting curves.  

**Offset** and Radius can be tweaked to best fit the curve on screen.  

**Amp** is the amplitude of the added sine wave.  

**Order** is the number of times the sine wave is repeated.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Init :Radius
 # global values for each curve
 GlobalMake "Steps 360
 GlobalMake "StepAngle 1
 GlobalMake "StepSize (2*Pi*:Radius)/(360/:StepAngle)
End
To Title :Titles
 # label up to 4 titles
 SetPC White Repeat Count :Titles [
 Home Right (RepCount*90) -135 Forward 262 SetH 0
 If X>0 [SetFontJustify [2 1]] [SetFontJustify [0 1]]
 Label Item RepCount :Titles]
End
To Go :Offset :Radius :Amp :Order
 New Title (List "RingWave [] List :Amp :Order List :Offset :Radius)
 Init :Radius SetPC Green
 PenUp SetXY 0 :Offset SetH 88 PenDown
 Forward :StepSize/2
 For (List "Angle 0 358) [
 Right :StepAngle + :Amp * Cos (:Order*:Angle)
 Forward :StepSize Wait 1]
 Right :StepAngle + :Amp * Cos (:Order*359)
 Forward :StepSize/2
End
```

| For each order, there is a unique amplitude
where the curves just touch but do not cross one another. |  |
