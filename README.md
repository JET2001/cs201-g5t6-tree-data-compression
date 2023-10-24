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
