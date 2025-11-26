# Single Line CA
![Single Line CA](../../art/400x400/ca.png)

Single Line or 1-Dimensional CA are built up line 
by line. The first line or seed line is a single central dot. Each 
dot can be on (green) or off (black/dark blue). A 'rule' determines 
which dots are to be drawn. All rules below 255 will draw a pattern. 
Some 'good' rules are:

**Go 30** random pattern  

**Go 90** nested pattern  

**Go 110** random pattern  

**Go 250** checkerboard pattern

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Init
 Make "Size 2
 SetPW :Size
End
To Display :Rule :BinList
 # write header info
 SetPC White PenUp
 SetPos [-190 184] Label [1D Cellular Automata]
 SetPos [70 184] Label List "Rule :Rule
 SetPos [70 170] Label :BinList
End
To Draw :CAList
 PenDown For (List "N 1 (Count :CAList)) [
 SetPC (List 0 (255*Item :N :CAList) 64)
 Forward :Size]
 SetPC Black Forward 0 PenUp
End
To NextLine :CAList
 Make "NewCAList []
 Make "CAList Sentence :CAList [0 0] #add 0 0 to end
 Make "LL [0 0 0]
 For (List "N 1 (Count :CAList)) [
 Make "LL ButFirst LPut Item :N :CAList :LL
 #convert LL into a decimal number
 Make "X Bin2Dec :LL
 #convert x to item x of binlist
 Make "X Item (8-:X) :BinList
 #add x to newcalist
 Make "NewCAList LPut :X :NewCAList]
 Output :NewCAList
End
To Dec2Bin :Num
 # convert decimal number Num to an 8 item binary list
 If :Num >255 [Print [Rule is too large!] Stop]
 LocalMake "Bin []
 For [C 7 0 -1] [
 LocalMake "Bin LPut (Quotient :Num Power 2 :C) :Bin
 LocalMake "Num :Num -((Power 2 :C) *Last :Bin)]
 Output :Bin
End
To Bin2Dec :BinList
 #return the decimal value of a 3 bit binary list
 Output (4*Item 1 :BinList)+(2*Item 2 :BinList)+(Item 3 :BinList)
End
To Go :Rule
 New SetPS 0 Init
 Make "BinList Dec2Bin :Rule
 Display :Rule :BinList SetH 90
 Make "CAList [1]
 For (List "N 0 191 :Size) [
 SetXY (0-:N) (150-:N)
 Draw :CAList
 Make "CAList NextLine :CAList]
End
```

Try different rule numbers instead of 30. **Enter Go 90**, 110 or 250.  

You can change the resolution of the ca by altering the **:**Size shown in red in the Init procedure.
