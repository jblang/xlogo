# Files

| Primitive                     | Alt        | Action                              |
| ----------------------------- | ---------- | ----------------------------------- |
| **SetDirectory [** abcd **]** | **SetDir** | Set current directory to abcd       |
| **ChangeDirectory** abcd      | **CD**     | Change to directory abcd.           |
| **Directory**                 | **Dir**    | Return current directory.           |
| **Files**                     | **LS**     | List contents of current directory. |

| Primitive          | Action                                                |
| ------------------ | ----------------------------------------------------- |
| **Save** word list | Save procedures in list in file named word.           |
| **Saved** word     | Save procedures currently defined in file named word. |
| **Load** word      | Opens and read file named word.                       |

| Primitive                              | Action                                               |
| -------------------------------------- | ---------------------------------------------------- |
| **OpenFlow** id file                   | Open a flow towards file with id.                    |
| **ListFlow**                           | List all open fluxes with their identifiers.         |
| **ReadLineFlow** id                    | Open flow id and read a line in the file.            |
| **ReadCharFlow** id                    | Open flow id and read a character in the file.       |
| **WriteLineFlow** id **[** abcd **]**  | Write text line contained in list to file id.        |
| **AppendLineFlow** id **[** abcd **]** | Write text line contained in list to end of file id. |
| **CloseFlow** id                       | Close flux id.                                       |
| **EndFlow?** id                        | Return true if end of file, else false.              |
