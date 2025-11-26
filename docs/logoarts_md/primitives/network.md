# Network

XLogo can communicate with another computer on an Ethernet network.

| Primitive                          | Action                                                       |
| ---------------------------------- | ------------------------------------------------------------ |
| **ListenTCP**                      | Listen for instructions received from another computer.      |
| **ExecuteTCP** id **[** abcd **]** | Execute instructions in list received from another computer. |
| **ChatTCP** id **[** abcd **]**    | Display chat window and allow chat between two computers.    |
| **SendTCP** id **[** abcd **]**    | Send data in list to computer with IP address id.            |
| **ExternalCommand**                | Launch an external command from XLogo.                       |
