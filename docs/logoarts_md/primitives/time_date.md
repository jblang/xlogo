# Time & Date

| Primitive         | Action                                                  |
| ----------------- | ------------------------------------------------------- |
| **Wait** n        | Pause for n/60 seconds.                                 |
| **CountDown** n   | Start a **countdown** of n seconds.                     |
| **EndCountDown?** | Return true if the countdown has reached 0, else false. |
| **Date**          | Return date (date month year), [dd mm yy].              |
| **Time**          | Return time (hours minutes seconds), [hh mm ss].        |
| **PastTime**      | Return number of seconds since XLogo started.           |

Note

1. Wait times must be a positive integer. Use **Wait 30** to pause the running of a program for half a second. The turtle moves so fast, this can sometimes be useful to see what's going on.
2. **PastTime** can be used to generate a random seed.
