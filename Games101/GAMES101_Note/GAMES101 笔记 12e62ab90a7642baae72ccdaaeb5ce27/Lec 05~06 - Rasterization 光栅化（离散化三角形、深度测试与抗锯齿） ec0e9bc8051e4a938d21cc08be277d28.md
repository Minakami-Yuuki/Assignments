# Lec 05~06 - Rasterization 光栅化（离散化三角形、深度测试与抗锯齿）

屏幕由像素点构成，是离散的，二维的

计算机生成图像中，最基本的二维元素是三角形，对其的离散化操作就是生成图像的重点

如何离散化（光栅化，Rasterization）？Sampling采样！

一个像素点对应一个坐标点，对这个坐标点采样，判断它在不在三角形里面，

采样的缺点：以点代面，有失偏颇 → Aliasing 走样，表现为锯齿

Sampling Artifacts (Errors / Mistakes / Inaccuracies) in Computer Graphics：

- Jaggies (Staircase Pattern)：an example of “aliasing” – a sampling error
    - 空间上Signals are changing too frequent (high frequency), but sampled too slowly
- Moiré Patterns in Imaging 摩尔纹
- Wagon Wheel Illusion (False Motion)：倒着转的轮子
    - 时间上Signals are changing too fast (high frequency), but sampled too slowly
- ……

# Lec06 - Rasterization 光栅化（）

走样与信号的关系

Antialiased Sampling

一种方法：先Filter后采样

- Filter包括诸如卷积的方法

什么是Filter？数据经过一定的处理

Filtering = Getting rid of certain frequency contents 滤波器

- Convolution (= Averaging) 卷积（可以看作一种低通滤波器）

Sampling = Repeating Frequency Contents

![sampling 是在频域上 copy & paste 波形](Lec%2005~06%20-%20Rasterization%20%E5%85%89%E6%A0%85%E5%8C%96%EF%BC%88%E7%A6%BB%E6%95%A3%E5%8C%96%E4%B8%89%E8%A7%92%E5%BD%A2%E3%80%81%E6%B7%B1%E5%BA%A6%E6%B5%8B%E8%AF%95%E4%B8%8E%E6%8A%97%E9%94%AF%E9%BD%BF%EF%BC%89%20ec0e9bc8051e4a938d21cc08be277d28/Untitled.png)

sampling 是在频域上 copy & paste 波形

Aliasing = Mixed Frequency Contents

![采样率不够的 copy & paste 导致波形会有重叠](Lec%2005~06%20-%20Rasterization%20%E5%85%89%E6%A0%85%E5%8C%96%EF%BC%88%E7%A6%BB%E6%95%A3%E5%8C%96%E4%B8%89%E8%A7%92%E5%BD%A2%E3%80%81%E6%B7%B1%E5%BA%A6%E6%B5%8B%E8%AF%95%E4%B8%8E%E6%8A%97%E9%94%AF%E9%BD%BF%EF%BC%89%20ec0e9bc8051e4a938d21cc08be277d28/Untitled%201.png)

采样率不够的 copy & paste 导致波形会有重叠

How Can We Reduce Aliasing Error?

- Option 1: Increase sampling rate
    - increasing the distance between replicas in the Fourier domain
    - Higher resolution displays, sensors, framebuffers
    - costly & may need very high resolution
- Option 2: Antialiasing
    - Filtering out high frequencies before sampling
    - 于是就不会有重叠了：
    
    ![Lec%2005~06%20-%20Rasterization%20%E5%85%89%E6%A0%85%E5%8C%96%EF%BC%88%E7%A6%BB%E6%95%A3%E5%8C%96%E4%B8%89%E8%A7%92%E5%BD%A2%E3%80%81%E6%B7%B1%E5%BA%A6%E6%B5%8B%E8%AF%95%E4%B8%8E%E6%8A%97%E9%94%AF%E9%BD%BF%EF%BC%89%20ec0e9bc8051e4a938d21cc08be277d28/Untitled%202.png](Lec%2005~06%20-%20Rasterization%20%E5%85%89%E6%A0%85%E5%8C%96%EF%BC%88%E7%A6%BB%E6%95%A3%E5%8C%96%E4%B8%89%E8%A7%92%E5%BD%A2%E3%80%81%E6%B7%B1%E5%BA%A6%E6%B5%8B%E8%AF%95%E4%B8%8E%E6%8A%97%E9%94%AF%E9%BD%BF%EF%BC%89%20ec0e9bc8051e4a938d21cc08be277d28/Untitled%202.png)
    
    - Antialiasing = Limiting, then repeating

Antialiased Sampling：Pre-Filter → Sample

convolving = filtering = averaging

先模糊再采样 vs 先采样再模糊

- 先采样后模糊（Filter）是不行的
    - 波形重叠的情况下截断依然会有重叠

最理想的状态：像素的颜色值为负责的区域内的平均值。

但是连续域的平均值是很难求的，计算量巨大

Antialiasing By Supersampling (MSAA)

- 近似方法
- Monte-Carlo
- 像素的颜色值为负责的区域内取样多次颜色值的平均。

Antialiasing Today

- No free lunch!
    - MSAA：每个像素多次采样，求平均。太浪费性能
    - 优化：不使用均匀分布，采样复用
    - 怎样分布样本才能达到最好的覆盖效果：Blue Noise?
- Milestones：目前得到广泛应用
    - FXAA (Fast Approximate AA)：先获得有锯齿的图，再后处理去除锯齿（很快）
        - 找到边界，换成没有锯齿的边界，（图像匹配）非常快
        - 方法和采样无关，采样虽然有误，但是这种方法可以弥补
    - TAA (Tem‘poral AA) ：时序信息，借助前面帧的信息
        - 最近刚刚兴起
        - 静态场景，相邻两帧同一像素用不同的位置来sample
        - 把MSAA的Sampling分布在时间上
        - 运动情况下怎么办？
- Super resolution / super sampling 超分辨率
    - From low resolution to high resolution
    - Essentially still “not enough samples” problem 类似抗锯齿
    - DLSS (Deep Learning Super Sampling) 猜

Visibility / Occlusion

## 深度缓存 Z-buffer

Painter's Algorithm：由远及近画画，覆盖

- 深度计算与排序：O(nlogn)
- 可能有无法排序的情况：例如三个三角形互相重叠

Z-buffer：对每个像素多存一个深度

- 实际coding中，(smaller z -> further, larger z -> closer)

![Lec%2005~06%20-%20Rasterization%20%E5%85%89%E6%A0%85%E5%8C%96%EF%BC%88%E7%A6%BB%E6%95%A3%E5%8C%96%E4%B8%89%E8%A7%92%E5%BD%A2%E3%80%81%E6%B7%B1%E5%BA%A6%E6%B5%8B%E8%AF%95%E4%B8%8E%E6%8A%97%E9%94%AF%E9%BD%BF%EF%BC%89%20ec0e9bc8051e4a938d21cc08be277d28/Untitled%203.png](Lec%2005~06%20-%20Rasterization%20%E5%85%89%E6%A0%85%E5%8C%96%EF%BC%88%E7%A6%BB%E6%95%A3%E5%8C%96%E4%B8%89%E8%A7%92%E5%BD%A2%E3%80%81%E6%B7%B1%E5%BA%A6%E6%B5%8B%E8%AF%95%E4%B8%8E%E6%8A%97%E9%94%AF%E9%BD%BF%EF%BC%89%20ec0e9bc8051e4a938d21cc08be277d28/Untitled%203.png)

复杂度：O(n) for n triangles 并不是排序，而是只要最值

需要保证三角形进入顺序和结果无关

无法处理透明物体