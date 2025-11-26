# Sound

| Primitive                   | Alt          | Action                                         |
| --------------------------- | ------------ | ---------------------------------------------- |
| **Set** **Instrument** x    | **SetInstr** | Set the instrument sound.                      |
| **Sequence** [..list..]     | **Seq**      | Put sequence list into memory.                 |
| **Play**                    |              | Play sequence.                                 |
| **Set** **IndexSequence** x | **SIndSeq**  | Set cursor index position in current sequence. |
| **DeleteSequence**          | **DelSeq**   | Delete sequence from memory.                   |
| **PlayMP3** "name           |              | **Play MP3** file "name.                       |
| **StopMP3** "name           |              | **Stop** playing **MP3** file "name.w          |

Underlined **Set** can be removed from a primitive name in order to return information.   
 eg. **SetHeading** sets the heading of the turtle. **Heading** returns the heading of the turtle.

The note names used are:   
 Doh, Ray, Me, Fah, Soh, Lah, Te

A complete list of available instruments can be found at Tools / Preferences / Sound tab.   
 You can also select or change a sound from the list. Sometimes, the list doesn't show, but you can still use sounds in your programs. Here is a quick sound test:

To Test   
 Seq [0.5 Soh Lah Te Soh]   
 Play   
 End
