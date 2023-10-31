# cs201-g5t6-tree-data-compression
CS201 G5T6 Tree Data Compression

## Run the Project
To run the project, add your implementation of Utility in a different java package **and modify the import in `App.java`**. 

Go to `App.java` between lines 10-20:
```
// ========== ADD YOUR UTILITY IMPLEMENTATION HERE ====
import <YOUR_PACKAGE_NAME>.Utility;
<Comment out other imports>
// ====================================================
```

In the main directory, run 
```
.\compile-and-run.bat
```
MAC commands may be slightly different. If you are on MAC and this doesn't work, please add the mac version as well.

Note that upon successful running of the programme, you should see this:
![image](https://github.com/JET2001/cs201-g5t6-tree-data-compression/assets/91585955/fd0e5a6e-352b-468f-92bb-87da43ec22a9)

for all 7 images.

## Image files
Place the images in the root directory (NOT inside the `app/` directory). The paths to the folders have been modified in the package app (they have been prefixed with a `../` from the original code given.)

## Directory Structure
```
/ - CS201-G5T6-Tree-Data-Comp
    | - app/
        | - App.java
        | - ...
    | - Compressed/
        | - ...
    | - Decompressed/
        | - ...
    | - Original/
        | - ...
    | - utility/
        | - Utility.java
    | - <OTHER UTILITY IMPLEMENTATION>/
        | - Utility.java // it should also be named Utility.java
```
## Results
If there are 3 values in 1 cell, they correspond to `(min, avg, max)`
|Algo Desc| Compressed Time | Bytes Saved | Decompress Time | MAE | MSE | PSNR | % Saved |
| :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
|Huffman Only | (27, 61, 129) | (-340756, -201055, -39746) | (16, 20.3, 30) | (0, 0, 0) | (0, 0.031, 0.169) | (55.8, Inf, Inf) | (-160, -75, -7.91) |
|Huffman + Uniform Quantisation | (51, 101, 177) | (8560, 142288.1, 315653) | (15, 30, 61) | (7.7, 8.3, 9.5) | (230, 271, 343) | (22.7, 23.8, 24.5) | (4.02, 40.2, 62.7) |
|Huffman with octree quantisation and uniform quantisation | (30, 56, 110) | (71620, 198407, 381078) | (9, 14.5, 26) | (14, 17, 20) | (887, 1192, 1544) | (16.2, 17.4, 18.7) | (33.6, 59.0, 75.7) |  
|Quad Trees (threshold = 20) | (36, 103, 244) | (-582107, -54807, 337244) | (47, 130, 327) | (8, 13, 25) | (494, 1646, 3644) | (12.5, 16.8, 21.2) | (-134, -10.4, 72) | 
|Quad Trees (quantised) (threshold = 20, channels = 25)| (45, 212, 564) | (-584648, -52968, 339818) | (52, 223, 519) | (8.57, 14.25, 26.14) | (490, 1694, 3683) | (12.5, 16.7, 21.2) | (-131, -10, 72) |
|Huffman with KD-Tree Quantisation and uniform quantisation | (281, 432, 1077) | (8549, 142361, 315796) | (225, 312.3, 536) | (7.8, 8.3, 9.58) | (242.6, 280.1, 357.8) | (22.6, 23.6, 24.3) |
|Huffman with KD-Tree and uniform quantisation with max-depth limit | (296, 484, 715) | (8549, 142361, 315796) | (14, 27, 63) | (7.8, 8.3, 9.58) | (242.6, 280.1, 357.8) | (22.6, 23.6, 24.3) | (4.0, 40.3, 62.8) |
