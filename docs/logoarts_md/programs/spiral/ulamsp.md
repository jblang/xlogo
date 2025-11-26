# Prime Number Spiral
![Prime Number Spiral](../../art/400x400/ulam.png)

This produces an array of squares built in a square spiral formation from the centre. The single last square drawn is removed.

The idea here is that by arranging prime numbers in a spira, al pattern to their distribution may be seen. Starting with 41 does produce some long lines of prime numbers.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To DrawSpiral
 # draw spiral of 361 square dots from centre
 For [N 1 19 0.5] [
 Repeat Integer :N [
 Make "Count :Count+1
 If Member? :Count :Primes [SetPC Yellow] [SetPC [0 63 0] ]
 Dot Pos Forward 20 Wait 3]
 Left 90]
End
To Init
 Make "Count 0
 Make "Primes [2 3 5 7 11 13 17 19 23 29 31 37 41 43 47 53 59 61 67 71 73 79 83 89 97 101 103 107 109 113 127 131 137 139 149 151 157 163 167 173 179 181 191 193 197 199 211 223 227 229 233 239 241 251 257 263 269 271 277 281 283 293 307 311 313 317 331 337 347 349 353 359]
End
To Go
 New SetPS 0 SetPW 11 SetH 90
 Init DrawSpiral
End
```
