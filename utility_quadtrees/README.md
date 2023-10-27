# QuadTrees Implementation

## Results
A higher threshold gives us a more lossy image, because the quadrant is split when the (diff of image RGB) and (diff of quadrant avg-RGB) exceeds THRESHOLD.
Compression size of threshold = 20 performs better than 10. When 10 is used we find that most compressed sizes is bigger.

### RMSE_THRESHOLD = 20
```
Compilation successful. Now running...
width = 500
height = 375
Compress Execution Time for 10188041.png : 185 milliseconds
Size of the original file for 10188041.png: 221231 bytes
Size of the compressed file for 10188041.png: 160371 bytes
Bytes saved from compression of 10188041.png: 60860 bytes
Decompress Execution Time for 10188041.png : 206 milliseconds
Mean Absolute Error of :10188041.png is 8.260332444444444
Mean Squared Error of :10188041.png is 745.624752
PSNR of :10188041.png is 19.405600443028472
width = 500
height = 375
Compress Execution Time for 10287332.png : 170 milliseconds
Size of the original file for 10287332.png: 216980 bytes
Size of the compressed file for 10287332.png: 128751 bytes
Bytes saved from compression of 10287332.png: 88229 bytes
Decompress Execution Time for 10287332.png : 163 milliseconds
Mean Absolute Error of :10287332.png is 8.325301333333334
Mean Squared Error of :10287332.png is 807.3885333333334
PSNR of :10287332.png is 19.05997783672512
width = 500
height = 375
Compress Execution Time for 10350842.png : 117 milliseconds
Size of the original file for 10350842.png: 212614 bytes
Size of the compressed file for 10350842.png: 191340 bytes
Bytes saved from compression of 10350842.png: 21274 bytes
Decompress Execution Time for 10350842.png : 195 milliseconds
Mean Absolute Error of :10350842.png is 8.530538666666667
Mean Squared Error of :10350842.png is 655.905184
PSNR of :10350842.png is 19.9623929747053
width = 500
height = 375
Compress Execution Time for 10404007.png : 68 milliseconds
Size of the original file for 10404007.png: 502730 bytes
Size of the compressed file for 10404007.png: 165486 bytes
Bytes saved from compression of 10404007.png: 337244 bytes
Decompress Execution Time for 10404007.png : 118 milliseconds
Mean Absolute Error of :10404007.png is 25.964433777777778
Mean Squared Error of :10404007.png is 3644.253856  
PSNR of :10404007.png is 12.514717388841827
width = 500
height = 375
Compress Execution Time for 10863862.png : 215 milliseconds
Size of the original file for 10863862.png: 317432 bytes
Size of the compressed file for 10863862.png: 428769 bytes
Bytes saved from compression of 10863862.png: **-111337 bytes**
Decompress Execution Time for 10863862.png : 301 milliseconds
Mean Absolute Error of :10863862.png is 15.041573333333334
Mean Squared Error of :10863862.png is 1670.6314826666667
PSNR of :10863862.png is 15.901996995411704
width = 500
height = 375
Compress Execution Time for 11079715.png : 679 milliseconds
Size of the original file for 11079715.png: 443815 bytes
Size of the compressed file for 11079715.png: 1025922 bytes
Bytes saved from compression of 11079715.png: **-582107 bytes**
Decompress Execution Time for 11079715.png : 738 milliseconds
Mean Absolute Error of :11079715.png is 15.882256
Mean Squared Error of :11079715.png is 2142.3355786666666
PSNR of :11079715.png is 14.821928605027372
width = 500
height = 375
Compress Execution Time for 11382381.png : 58 milliseconds
Size of the original file for 11382381.png: 312486 bytes
Size of the compressed file for 11382381.png: 87273 bytes
Bytes saved from compression of 11382381.png: 225213 bytes
Decompress Execution Time for 11382381.png : 65 milliseconds
Mean Absolute Error of :11382381.png is 14.375539555555555
Mean Squared Error of :11382381.png is 1382.631152
PSNR of :11382381.png is 16.72374023141916
width = 500
height = 375
Compress Execution Time for 11551286.png : 89 milliseconds
Size of the original file for 11551286.png: 271883 bytes
Size of the compressed file for 11551286.png: 135726 bytes
Bytes saved from compression of 11551286.png: 136157 bytes
Decompress Execution Time for 11551286.png : 101 milliseconds
Mean Absolute Error of :11551286.png is 8.527786666666668
Mean Squared Error of :11551286.png is 494.928192   
PSNR of :11551286.png is 21.18538168157345
width = 500
height = 375
Compress Execution Time for 11610969.png : 271 milliseconds
Size of the original file for 11610969.png: 368720 bytes
Size of the compressed file for 11610969.png: 631695 bytes
Bytes saved from compression of 11610969.png: **-262975 bytes**
Decompress Execution Time for 11610969.png : 359 milliseconds
Mean Absolute Error of :11610969.png is 15.647104
Mean Squared Error of :11610969.png is 1996.7341333333334
PSNR of :11610969.png is 15.12760118787895
width = 500
height = 375
Compress Execution Time for 1254659.png : 451 milliseconds
Size of the original file for 1254659.png: 342646 bytes
Size of the compressed file for 1254659.png: 803280 bytes
Bytes saved from compression of 1254659.png: **-460634 bytes**
Decompress Execution Time for 1254659.png : 547 milliseconds
Mean Absolute Error of :1254659.png is 17.919697777777778
Mean Squared Error of :1254659.png is 2921.433088   
PSNR of :1254659.png is 13.474844171136855
```
### RMSE_THRESHOLD = 10
```
Compilation successful. Now running...
width = 500
height = 375
Compress Execution Time for 10188041.png : 311 milliseconds
Size of the original file for 10188041.png: 221231 bytes
Size of the compressed file for 10188041.png: 505959 bytes
Bytes saved from compression of 10188041.png: **-284728 bytes**
Decompress Execution Time for 10188041.png : 354 milliseconds
Mean Absolute Error of :10188041.png is 5.281628444444444
Mean Squared Error of :10188041.png is 308.538272
PSNR of :10188041.png is 23.23771317984906
width = 500
height = 375
Compress Execution Time for 10287332.png : 231 milliseconds
Size of the original file for 10287332.png: 216980 bytes
Size of the compressed file for 10287332.png: 308148 bytes
Bytes saved from compression of 10287332.png: **-91168 bytes**
Decompress Execution Time for 10287332.png : 221 milliseconds
Mean Absolute Error of :10287332.png is 5.473322666666666
Mean Squared Error of :10287332.png is 356.07837866666665
PSNR of :10287332.png is 22.615347570640214
width = 500
height = 375
Compress Execution Time for 10350842.png : 194 milliseconds
Size of the original file for 10350842.png: 212614 bytes
Size of the compressed file for 10350842.png: 414726 bytes
Bytes saved from compression of 10350842.png: **-202112 bytes**
Decompress Execution Time for 10350842.png : 246 milliseconds
Mean Absolute Error of :10350842.png is 5.997349333333333
Mean Squared Error of :10350842.png is 397.22088    
PSNR of :10350842.png is 22.140482916627807
width = 500
height = 375
Compress Execution Time for 10404007.png : 414 milliseconds
Size of the original file for 10404007.png: 502730 bytes
Size of the compressed file for 10404007.png: 829227 bytes
Bytes saved from compression of 10404007.png: **-326497 bytes**
Decompress Execution Time for 10404007.png : 560 milliseconds
Mean Absolute Error of :10404007.png is 22.111061333333332
Mean Squared Error of :10404007.png is 2744.0276266666665
PSNR of :10404007.png is 13.746918813665461
width = 500
height = 375
Compress Execution Time for 10863862.png : 497 milliseconds
Size of the original file for 10863862.png: 317432 bytes
Size of the compressed file for 10863862.png: 1082652 bytes
Bytes saved from compression of 10863862.png: **-765220 bytes**
Decompress Execution Time for 10863862.png : 656 milliseconds
Mean Absolute Error of :10863862.png is 10.205667555555555
Mean Squared Error of :10863862.png is 854.4045973333333
PSNR of :10863862.png is 18.814167843492452
width = 500
height = 375
Compress Execution Time for 11079715.png : 594 milliseconds
Size of the original file for 11079715.png: 443815 bytes
Size of the compressed file for 11079715.png: 1582806 bytes
Bytes saved from compression of 11079715.png: **-1138991 bytes**
Decompress Execution Time for 11079715.png : 852 milliseconds
Mean Absolute Error of :11079715.png is 14.229843555555556
Mean Squared Error of :11079715.png is 1856.72552   
PSNR of :11079715.png is 15.443326541986824
width = 500
height = 375
Compress Execution Time for 11382381.png : 242 milliseconds
Size of the original file for 11382381.png: 312486 bytes
Size of the compressed file for 11382381.png: 604911 bytes
Bytes saved from compression of 11382381.png: **-292425 bytes**
Decompress Execution Time for 11382381.png : 364 milliseconds
Mean Absolute Error of :11382381.png is 5.875749333333333
Mean Squared Error of :11382381.png is 244.84518933333334
PSNR of :11382381.png is 24.24188785356279
width = 500
height = 375
Compress Execution Time for 11551286.png : 214 milliseconds
Size of the original file for 11551286.png: 271883 bytes
Size of the compressed file for 11551286.png: 454437 bytes
Bytes saved from compression of 11551286.png: **-182554 bytes**
Decompress Execution Time for 11551286.png : 294 milliseconds
Mean Absolute Error of :11551286.png is 5.708784
Mean Squared Error of :11551286.png is 247.08546666666666
PSNR of :11551286.png is 24.202331595050545
width = 500
height = 375
Compress Execution Time for 11610969.png : 471 milliseconds
Size of the original file for 11610969.png: 368720 bytes
Size of the compressed file for 11610969.png: 1280835 bytes
Bytes saved from compression of 11610969.png: **-912115 bytes**
Decompress Execution Time for 11610969.png : 738 milliseconds
Mean Absolute Error of :11610969.png is 11.805436444444444
Mean Squared Error of :11610969.png is 1244.1209173333334
PSNR of :11610969.png is 17.18217768954734
width = 500
height = 375
Compress Execution Time for 1254659.png : 505 milliseconds
Size of the original file for 1254659.png: 342646 bytes
Size of the compressed file for 1254659.png: 1296831 bytes
Bytes saved from compression of 1254659.png: **-954185 bytes**
Decompress Execution Time for 1254659.png : 680 milliseconds
Mean Absolute Error of :1254659.png is 14.720204444444445
Mean Squared Error of :1254659.png is 2212.0623946666665
PSNR of :1254659.png is 14.682829881099043
```
## Issues
Note this is a WORK IN PROGRESS.
There is a bug.

Current Issues:
- I am struggling for cases where there the image cannot be divide into 4 subquadrants.
- I think that the quadrants seem to be overlapping, even though I don't explicitly define them to be overlapping, which is odd.
