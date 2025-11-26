# Animation Mode

| Primitive         | Alt          | Action                       |
| ----------------- | ------------ | ---------------------------- |
| **Animation**     | **Anim**     | **Animation** mode.          |
| **StopAnimation** | **StopAnim** | **Stop animation** mode.     |
| **Refresh**       | **Repaint**  | **Refresh** (update) screen. |
| **Wash**          |              | **Wash** (wipe) screen.      |

All of the above leave the turtles position and heading unchanged.

XLogo can display a number of screens in sucession, which give the illusion of a moving or animated image.

First you need to switch to animation mode with the command Animation.

Once in animation mode, XLogo behaves slightly differently. Any changes to the Draw Zone are not displayed immeadiately, but are held in a buffer. Then, the Refresh command updates the whole area all in one go. So you don't see the turtle moving around drawing things a bit at a time.

To produce the next 'frame' use the Wash command to clear the screen, (it will not reset the turtles position), before drawing everything again.

Alternatively, draw over some areas in the same color as your background to remove them and then redraw the next frame. This can be quicker than drawing the whole screen again.

Add in a wait time to slow the animation down. If you create a Pause variable, you can easily adjust the speed of the animation later.

Return to normal drawing mode with **StopAnimation** . Or click the camera icon.
