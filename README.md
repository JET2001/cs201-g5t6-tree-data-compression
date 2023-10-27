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
|Algo Desc| Compressed Time | Bytes Saved | Decompress Time | MAE | MSE | PSNR |
| :---: | :---: | :---: | :---: | :---: | :---: | :---: |
|Huffman with octree quantisation and uniform quantisation | (30, 56, 110) | (71620, 198407, 381078) | (7, 10.7, 28) | (14, 17, 20) | (887, 1192, 1544) | (887, 1192, 1544) |
|Quad Trees (threshold = 20) | (36, 103, 244) | (-582107, -54807, 337244) | (47, 130, 327) | (8, 13, 25) | (494, 1646, 3644) | (494, 1646, 3644) | 
|Quad Trees (quantised) (threshold = 20, channels = 25)| (45, 189, 407) | (-584648, -52968, 339818) | (52, 223, 519) | (8.57, 14.25, 26.14) | (490, 1694, 3683) | (490, 1694, 3683) |
