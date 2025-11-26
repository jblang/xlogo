# Maths

| Primitive            | Alt      | Action                                                      |
| -------------------- | -------- | ----------------------------------------------------------- |
| **Alea**             |          | Return random decimal number between 0 and 1.               |
| **Pi**               |          | Return value of **pi** (3.14159...)                         |
| **Absolute** xx      | **Abs**  | Return **absolute** (positive) value of xx.                 |
| **Exp** xx           |          | Return e (Euler's constant 2.71828...) to the power xx.     |
| **Integer** xx       | **Int**  | Return **integer** part of xx.                              |
| **Log** xx           |          | Return (natural) **log** of xx, base e.                     |
| **Log10** xx         |          | Return **log** of xx, base 10.                              |
| **Minus** xx         |          | Return negated value (invert sign) of xx.                   |
| **Random** xx        | **Ran**  | Return a **random** whole number between 0 and xx-1.        |
| **Round** xx         | **Rnd**  | Return nearest whole number to xx.                          |
| **SquareRoot** xx    | **Sqrt** | Return **square root** of xx.                               |
| **Difference** aa bb | **Diff** | Return **difference** between aa and bb. (aa - bb)          |
| **Divide** aa bb     | **Div**  | Return **division** of aa by bb. (aa / bb)                  |
| **Modulo** xx yy     | **Mod**  | Return **modulo** of xx divided by yy.                      |
| **Power** xx yy      |          | Return xx raised to the **power** yy.                       |
| **Product** aa bb    |          | Return **product** (multiplication) of aa and bb. (aa x bb) |
| **Quotient** xx yy   |          | Return **quotient** (integer part) of xx divided by yy.     |
| **Remainder** xx yy  | **Rem**  | Return **remainder** (decimal part) of xx divided by yy.    |
| **Sum** aa bb        |          | Return **sum** of aa and bb. (aa + bb)                      |

Trig

| Primitive         | Alt      | Action                                                  |
| ----------------- | -------- | ------------------------------------------------------- |
| **Cosine** xx     | **Cos**  | Return **cosine** of angle xx in degrees.               |
| **Sine** xx       | **Sin**  | Return **sine** of angle xx in degrees.                 |
| **Tangent** xx    | **Tan**  | Return **tangent** of angle xx in degrees.              |
| **ArcCosine** xx  | **ACos** | Return the angle (range 0-180) whose **cosine** is xx.  |
| **ArcSine** xx    | **ASin** | Return the angle (range 0-180) whose **sine** is xx.    |
| **ArcTangent** xx | **ATan** | Return the angle (range 0-180) whose **tangent** is xx. |

Note

1. **Product** and **Sum** can be contained in parenthesis. Eg (Sum 1 2 3) returns 6.
2. **SquareRoot** XX is mathematically the same as **Power** XX 0.5
3. Use **Minus** for primitives requiring two parameters. Eg **SetXY 55 Minus 44** not **SetXY 55 - 44** . This will not work, as evaluated to 11.
4. Both **Modulo** arguments need to be integers.
5. Use **SetDigits** to set the number of significant digits.
