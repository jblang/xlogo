# XLogo

XLogo is a Logo interpreter written in Java and licensed under the [GPL](COPYING.md). It has many extensions relative to other Logo implementations, such as networking, sound, 3D, and animation support.  It supports nine languages: French, English, Spanish, Arabic, Portuguese, German, Esperanto, Galician and Greek.

Logo is a programming language developed in the 1970's by Wally Feurzeig, Seymour Papert, and Cynthia Solomon. It is an excellent language to begin learning with, as it teaches the basics of things like loops, tests, procedures, etc. The user moves an object called a "turtle" around the screen using commands as simple as forward, back, right, and so on. As it moves, the turtle leaves a trail behind it, and so it is therefore possible to create drawings. Operations on lists and words are also possible.

Here is a simple example that produces the image in [this screenshot](xlogo.png):

- `forward 3 right 2` will first make the turtle move 3 steps forward, and then turn the turtle 2° to the right. 
- `repeat 180 [forward 3 right 2]` repeats this motion 180 times to draw a circle
- `repeat 20 [repeat 180 [forward 3 right 2] right 18]` draws 20 circles offset by 18° to make a flower.

This very intuitive graphical approach makes Logo an ideal language for beginners, including children.

## Download

Pre-compiled jars available on the [Releases](https://github.com/jblang/xlogo/releases) page. You can also still download the [old version](http://xlogo.tuxfamily.org/en/download-en.html) from the XLogo website.

Note: You will need Java on your computer to run XLogo. Java 11 or Java 17 is recommended. Java 8 may also work but has scaling issues on high resolution displays. If you don't already have a JDK installed, [Microsoft](https://docs.microsoft.com/en-us/java/openjdk/download) offers free OpenJDK downloads for Windows, Mac, and Linux.

## Documentation

Extensive documentation is available from the [XLogo website](http://xlogo.tuxfamily.org/). Reference manuals are available in many languages. Some languages and formats may be more up-to-date than others, and some also have tutorials and other resources.  I have summarized what's available in the table below.

| Document | English | French | German | Italian | Portuguese | Spanish | Esperanto |
| - | - | - | - | - | - | - | - |
| Reference Manual (online) | [English](https://downloads.tuxfamily.org/xlogo/downloads-en/manual-html-en/manual-en.html) | [French](https://downloads.tuxfamily.org/xlogo/downloads-fr/manual-html-fr/manual-fr.html) | [German](https://downloads.tuxfamily.org/xlogo/downloads-de/manual-html-de/manual-de.html) | [Italian](https://downloads.tuxfamily.org/xlogo/downloads-it/manual-html-it/manual-it.html) | [Portuguese](https://xlogo.tuxfamily.org/pt/xlogo.htm) | [Spanish](https://xlogo.tuxfamily.org/sp/html/manual-sp/index.html) | [Esperanto](https://downloads.tuxfamily.org/xlogo/downloads-eo/manual-html-eo/manual-eo.html)
| Reference Manual (zipped html) | [English](https://downloads.tuxfamily.org/xlogo/downloads-en/manual-html-en.zip) | [French](https://downloads.tuxfamily.org/xlogo/downloads-fr/manual-html-fr.zip) | [German](https://downloads.tuxfamily.org/xlogo/downloads-de/manual-html-de.zip) | [Italian](https://downloads.tuxfamily.org/xlogo/downloads-it/manual-html-it.zip) | [Portuguese](https://downloads.tuxfamily.org/xlogo/downloads-pt/xmanualPT3.zip) | [Spanish](https://downloads.tuxfamily.org/xlogo/downloads-sp/manual-html-sp.zip) | [Esperanto](https://downloads.tuxfamily.org/xlogo/downloads-eo/manual-html-eo.zip)
| Reference Manual (pdf) | [English](https://downloads.tuxfamily.org/xlogo/downloads-en/manual-en.pdf) | [French](https://downloads.tuxfamily.org/xlogo/downloads-fr/manual-fr.pdf) | [German](https://downloads.tuxfamily.org/xlogo/downloads-de/manual-de.pdf) | [Italian](https://downloads.tuxfamily.org/xlogo/downloads-it/manual-it.pdf) | [Portuguese](https://downloads.tuxfamily.org/xlogo/downloads-pt/manualPT.pdf) | [Spanish](https://downloads.tuxfamily.org/xlogo/downloads-sp/manual-sp.pdf) | [Esperanto](https://downloads.tuxfamily.org/xlogo/downloads-eo/manual-eo.pdf)
| Examples | *[LogoArts](http://www.cr31.co.uk/logoarts/prog/top/all.html)* | [French](http://xlogo.tuxfamily.org/fr/examples-fr.html) | [German](https://xlogo.tuxfamily.org/de/examples-de.html) | [Italian](http://xlogo.tuxfamily.org/it/examples-it.html) | [Portuguese](http://xlogo.tuxfamily.org/pt/examples.html) | [Spanish](http://xlogo.tuxfamily.org/sp/ejemplos.html) | [Esperanto](https://xlogo.tuxfamily.org/eo/examples-eo.html)
| XLogo Robotics | [English](https://xlogo.tuxfamily.org/en/robot-en.html) | | | [Italian](http://xlogo.tuxfamily.org/it/robot-it.html) | [Portuguese](https://xlogo.tuxfamily.org/pt/robot.html) | [Spanish](https://xlogo.tuxfamily.org/sp/robotica.html) |
| Tutorial (pdf) | | [French](http://downloads.tuxfamily.org/xlogo/downloads-fr/tutorial-fr.pdf) | | | [Portuguese](https://downloads.tuxfamily.org/xlogo/downloads-pt/tutlogo.pdf) | [Spanish](https://downloads.tuxfamily.org/xlogo/downloads-sp/tutorial.pdf)
| Tutorial (zipped html) | | | | | | [Spanish](https://downloads.tuxfamily.org/xlogo/downloads-sp/tutorial-html-sp.zip)
| Introductory Course | | | | | | [Spanish](https://xlogo.tuxfamily.org/sp/curso/curso.html)
| Installation (pdf) | [English](https://downloads.tuxfamily.org/xlogo/downloads-en/start-en.pdf) | [French](https://downloads.tuxfamily.org/xlogo/downloads-fr/start-fr.pdf) | [German](https://downloads.tuxfamily.org/xlogo/downloads-de/start-de.pdf) |  |  | [Spanish](https://downloads.tuxfamily.org/xlogo/downloads-sp/start-sp.pdf) |

[TeX sources](docs) for each language's documentation are available on the XLogo website but have also been added to this 
repo, if available.

## Logo Arts

Guy Walker's wonderful [Logo Arts](http://www.cr31.co.uk/logoarts/index.html) website contains many XLogo resources:
- Lots of [Example Programs](http://www.cr31.co.uk/logoarts/prog/top/all.html) with screenshots
- Comprehensive [Primitive Reference](http://www.cr31.co.uk/logoarts/ipt/top/prim.html)
- Excellent [Book Recommendations](http://www.cr31.co.uk/logoarts/ipt/info/books.html)

## Current Status

XLogo's development ceased in 2012. I tried to contact the original author in 2022 but got no response. I migrated the Subversion repository to GitHub and started working on some improvements:

- Fix blurry/pixelated scaling on high resolution displays
- Make a nice looking themeable UI using FlatLaf
- Fix bugs and improve code quality

If you want to help improve the code, check out the [Developer Notes](DEVNOTES.md) to get started. 

## Credits

### Developers

- Loïc Le Coq: Original Developer 
- J.B. Langston: UI upgrade and GitHub migration

### Translators

- Loïc Le Coq: French
- Kevin Donnely, Guy Walker: English
- Marcelo Duschkin, Alvaro Valdes Menendez: Spanish
- El Houcine Jarad: Arabic
- Alexandre Soares: Portugese
- Miriam Abresch, Michael Malien: German
- Michel Gaillard, Carlos Enrique, Carleos Artime: Esperanto
- Justo Freire: Galician
- Anastasios Drakopoulos: Greek
- Marco Bascietto: Italian
- David Arso: Catalan
- Jozsef Varga: Hungarian

### Third-party

- [FlatLaf](https://www.formdev.com/flatlaf/): Swing look and feel (Apache license)
- [IntelliJ IDEA](https://jetbrains.design/intellij/resources/icons_list/): SVG Icons (Apache license)
- [Java3D](https://github.com/hharrison/java3d-core): 3D scene graph library (GPL license)
- [Jogamp](https://jogamp.org/): OpenGL libraries (BSD/MIT/Apache license)
- [JavaHelp](https://github.com/javaee/javahelp): Help library (GPL license)
- [JLayer](https://github.com/umjammer/jlayer): MP3 library (LGPL license)