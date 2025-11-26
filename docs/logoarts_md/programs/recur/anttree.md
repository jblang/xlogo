# Antenna Tree
![Antenna Tree](../../art/400x400/anttree.png)

Antenna Tree curve. Similar to [TreeAnim](../tree/treeanim.md) at 90 degrees. Increase the order for more detail.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Init :Order
 Make "Size 96 # make Size and RootHalf global values
 Make "RootHalf 1/Sqrt 2
End
To Display :Order
 # write header title and curve level
 SetPC White SetPos [-190 184] 
 Label Sentence [Antenna Tree Level] :Order
End
To Tree :Order :Size
 If :Order < 1 [Stop] # ie if = 0
 SetPW :Order Forward :Size Left 90
 Tree :Order-1 :RootHalf*:Size
 SetPW :Order Left 90 Forward 2*:Size Left 90
 Tree :Order-1 :RootHalf*:Size
 SetPW :Order Left 90 Forward :Size
End
To Go :Order
 New Init :Order Display :Order
 Home Right 90 SetPC Green PenDown
 Tree :Order :Size
End
```

Type **Go order** for example **Go 7** to run.
