# Star sequences

Contains a program which can process image generated via the Astronomical Multi-exposure Wide Plates Method (see https://www.researchgate.net/publication/276113570_Dimov_D_and_M_Dimitrijevic_Image_Processing_Suggestions_to_Improve_the_Astronomical_Multiexposure_Wide_Plates_Method_In_M_Dmitrievich_M_Tzvetkov_Eds_Proceedings_of_VIII_Serbian-Bulgarian_Astronomical_).

![Sample input image](https://github.com/vladimirmarinov/star_sequences/blob/master/star_sequences_small.png?raw=true)

The algorithm will preprocess and process the image, identify individual star sequences and finally try to find sequences which potentially captured some interesting event (e.g. a sequences in which the corresponding star "blinked").

Here's an example of a couple of star sequences identified by the algorithm.

![enter image description here](https://github.com/vladimirmarinov/star_sequences/blob/master/identified_sequences.png?raw=true)

## How to run
To execute the program simply run Main.java. It gets two optional command line arguments:

 - Path to input BMP file - default is ***input.bmp***
 - Binarization threshold - default is ***107***

Here's an example usage:

    javac Main.java
    java Main some_input_file.bmp 119
