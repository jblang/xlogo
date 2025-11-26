 // Create red/green menu blobs
function blob(title) 
{	var title2=second(document.title)
	if (title==title2) 
	document.write('<img src=\"../../art/blob2.gif\" width=\"8\" height=\"8\">');
	else document.write('<img src=\"../../art/blob1.gif\" width=\"8\" height=\"8\">');	
}	

// Create array to contain image values 
image = new Array

image[1]="demo"
image[2]="peano"
image[3]="r_g_b"
image[4]="spiral"
image[5]="spiro"
image[6]="tree"
image[7]="butterfly"
image[8]="goldenratio"
image[9]="hilbert"
image[10]="polygons"
image[11]="sierpinski"
image[12]="spirograph"
image[13]="archimedes"
image[14]="gingerbread"
image[15]="henon"
image[16]="pursuit"
image[17]="petals"
image[18]="sunflower"
image[19]="condensation"
image[20]="dust"
image[21]="flowers"
image[22]="harmonograph"
image[23]="mystic_rose"
image[24]="rainbow"
image[25]="drain"
image[26]="fields"
image[27]="fern"
image[28]="sier_curve"
image[29]="snowflake"
image[30]="squares"
image[31]="15_puzzle"
image[32]="cardioid"
image[33]="ferris_wheel"
image[34]="mandelbrot"
image[35]="shapes"
image[36]="weeds"
image[37]="border"
image[38]="edge"
image[39]="lace_curve"
image[40]="surface"
image[41]="tiles"
image[42]="wire_shapes"
image[43]="cubes"
image[44]="knight's_tour"
image[45]="linkages"
image[46]="oct_tiles"
image[47]="popcorn"
image[48]="sphinx"
image[49]="arc_wave"
image[50]="clover_art"
image[51]="kite_tree"
image[52]="pebbles"
image[53]="stars"
image[54]="yin_yang"
image[55]="bulge"
image[56]="cafe_wall"
image[57]="intertwine"
image[58]="neon"
image[59]="orbinson"
image[60]="zoom"

// Create a random number between 1 and 60
random_num = (Math.round((Math.random()*59)+1))

function capWords(str) {
	var words=str.split("_");
	for (var i=0;i<words.length;i++){
		var testwd=words[i];
		var firLet=testwd.substr(0,1);
		var rest=testwd.substr(1,testwd.length-1);
		words[i]=firLet.toUpperCase()+rest;
	}
	return(words.join(" "));
}

function second(str) {
	var words=str.split(" | ");
	return(words[1]);
}

randimg = capWords(image[random_num])
page = Math.round (1 + (Math.floor ( (random_num-1) / 6 )))

var randstr='';
randstr+='    Random Image<br>';
randstr+='    <script type="text\/javascript">document.write("<a href=..\/..\/pict\/gall" + page + "\/" + image[random_num] + ".html>");<\/script>';
randstr+='    <script type="text\/javascript">document.write("<img src=..\/..\/art\/random\/" + image[random_num] + ".gif");<\/script>';
randstr+='    width=\"100\" height=\"100\"><\/a><br>';
randstr+='    <script type="text\/javascript">document.write(randimg);<\/script>';


// Create array to contain color values 
color = new Array

color[1]="Red"
color[2]="Blue"
color[3]="Yellow"
color[4]="Green"
color[5]="Black"
color[6]="Teal"
color[7]="Gray"
color[8]="Purple"
color[9]="White"

// Create 4 random numbers between 1 and 9
random_num1 = (Math.round((Math.random()*8)+1))
random_num2 = (Math.round((Math.random()*8)+1))
random_num3 = (Math.round((Math.random()*8)+1))
random_num4 = (Math.round((Math.random()*8)+1))

// Create 4 random colors
/*col1 = color[random_num1]
col2 = color[random_num2]
col3 = color[random_num3]
col4 = color[random_num4]*/


// Create logologo string
var str='';
str+='<table height="65px" width="100px" align="center">';
str+='  <tr> ';
str+='    <td><\/td>';
str+='    <td width="1"><\/td>';
str+='    <td width="13"><\/td>';
str+='    <td width="1"><\/td>';
str+='    <td width="13"><\/td>';
str+='    <td width="1"><\/td>';
str+='    <td width="6"><\/td>';
str+='    <td width="1"><\/td>';
str+='    <td width="16"><\/td>';
str+='    <td width="1"><\/td>';
str+='    <td width="6"><\/td>';
str+='    <td width="1"><\/td>';
str+='    <td width="8"><\/td>';
str+='    <td width="7"><\/td>';
str+='    <td width="1"><\/td>';
str+='    <td width="6"><\/td>';
str+='    <td width="1"><\/td>';
str+='    <td width="16"><\/td>';
str+='    <td width="1"><\/td>';
str+='  <\/tr>';
str+='  <tr> ';
str+='    <td height="2"><\/td>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num1] + "><\/td>");<\/script>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num1] + "><\/td>");<\/script>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num1] + "><\/td>");<\/script>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='  <\/tr>';
str+='  <tr> ';
str+='    <td height="28"><\/td>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num1] + "><\/td>");<\/script>';
str+='    <td><\/td>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num1] + "><\/td>");<\/script>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='  <\/tr>';
str+='  <tr> ';
str+='    <td height="2"><\/td>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num1] + "><\/td>");<\/script>';
str+='    <td><\/td>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num1] + "><\/td>");<\/script>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num2] + "><\/td>");<\/script>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num2] + "><\/td>");<\/script>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num2] + "><\/td>");<\/script>';
str+='    <td><\/td>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num3] + "><\/td>");<\/script>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num3] + "><\/td>");<\/script>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num3] + "><\/td>");<\/script>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num3] + "><\/td>");<\/script>';
str+='    <td><\/td>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num4] + "><\/td>");<\/script>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num4] + "><\/td>");<\/script>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num4] + "><\/td>");<\/script>';
str+='  <\/tr>';
str+='  <tr> ';
str+='    <td height="14"><\/td>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num1] + "><\/td>");<\/script>';
str+='    <td><\/td>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num1] + "><\/td>");<\/script>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num2] + "><\/td>");<\/script>';
str+='    <td><\/td>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num2] + "><\/td>");<\/script>';
str+='    <td><\/td>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num3] + "><\/td>");<\/script>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num4] + "><\/td>");<\/script>';
str+='    <td><\/td>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num4] + "><\/td>");<\/script>';
str+='  <\/tr>';
str+='  <tr> ';
str+='    <td height="2"><\/td>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num1] + "><\/td>");<\/script>';
str+='    <td><\/td>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num1] + "><\/td>");<\/script>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num1] + "><\/td>");<\/script>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num1] + "><\/td>");<\/script>';
str+='    <td><\/td>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num2] + "><\/td>");<\/script>';
str+='    <td><\/td>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num2] + "><\/td>");<\/script>';
str+='    <td><\/td>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num3] + "><\/td>");<\/script>';
str+='    <td><\/td>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num3] + "><\/td>");<\/script>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num3] + "><\/td>");<\/script>';
str+='    <td><\/td>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num4] + "><\/td>");<\/script>';
str+='    <td><\/td>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num4] + "><\/td>");<\/script>';
str+='  <\/tr>';
str+='  <tr> ';
str+='    <td height="14"><\/td>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num1] + "><\/td>");<\/script>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num1] + "><\/td>");<\/script>';
str+='    <td><\/td>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num2] + "><\/td>");<\/script>';
str+='    <td><\/td>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num2] + "><\/td>");<\/script>';
str+='    <td><\/td>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num3] + "><\/td>");<\/script>';
str+='    <td><\/td>';
str+='    <td><\/td>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num3] + "><\/td>");<\/script>';
str+='    <td><\/td>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num4] + "><\/td>");<\/script>';
str+='    <td><\/td>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num4] + "><\/td>");<\/script>';
str+='  <\/tr>';
str+='  <tr> ';
str+='    <td height="3"><\/td>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num1] + "><\/td>");<\/script>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num1] + "><\/td>");<\/script>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num1] + "><\/td>");<\/script>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num1] + "><\/td>");<\/script>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num1] + "><\/td>");<\/script>';
str+='    <td><\/td>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num2] + "><\/td>");<\/script>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num2] + "><\/td>");<\/script>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num2] + "><\/td>");<\/script>';
str+='    <td><\/td>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num3] + "><\/td>");<\/script>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num3] + "><\/td>");<\/script>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num3] + "><\/td>");<\/script>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num3] + "><\/td>");<\/script>';
str+='    <td><\/td>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num4] + "><\/td>");<\/script>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num4] + "><\/td>");<\/script>';
str+='    <script type="text\/javascript">document.write("<td bgColor=" + color[random_num4] + "><\/td>");<\/script>';
str+='  <\/tr>';
str+='<\/table>';