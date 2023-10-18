# Other Metrics useful for Image Compression Analysis

## Entropy Calculator
Picture entropy is a valuable statistic in the field of image compression. Compression algorithms can determine the optimal way to compress a picture by analysing the entropy of a picture.

Entropy measures an image's information content used in image processing. A high entropy number denotes a complex image with a wide range of pixel values, whereas a low entropy value denotes a more straightforward, uniform image.

Entropy of an image $H$ is given by
$$H = -\sum_{i=1}{n} p_i \log_2 p_i$$
where $p_i$ is the probability of the i-th pixel (in RGB).


