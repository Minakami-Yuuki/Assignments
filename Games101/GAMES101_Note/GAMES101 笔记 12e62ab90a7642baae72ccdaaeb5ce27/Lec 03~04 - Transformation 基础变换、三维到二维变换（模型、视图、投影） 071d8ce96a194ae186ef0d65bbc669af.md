# Lec 03~04 - Transformation 基础变换、三维到二维变换（模型、视图、投影）

- 基础变换（仿射变换）：旋转、缩放、切变、平移
    - 先后顺序很重要，需要仔细想
    - 仿射变换=线性变换+平移
    - 线性变换=旋转+缩放+切变
- 齐次坐标系：n维的点/向量，用[n+1 x 1]的列向量表示
    - 变换用[n+1 x n+1]的矩阵左乘来表示
    - 多个变换可以组合起来变成一个矩阵→加速计算

旋转的表示：

- 欧拉角：roll, pitch, yaw
- 四元数：

---

MVP：

- Model transformation (placing objects)
    
    想象一下：世界坐标系下有很多Object，用一个变化矩阵把它们的顶点坐标从Local坐标系（相对）转换到世界Global坐标系（绝对）。这就是placing objects
    
- View transformation (placing camera)
    
    想象一下：我们看到的画面由摄像机捕捉，摄像机参数决定了我们在屏幕上看到的东西，这一步可以将世界坐标系转换到摄像机坐标系。
    
- Projection transformation
    
    摄像机坐标系，视锥体，再规整一下
    

Viewing (观测) transformation

- View (视图) / Camera transformation
- Projection (投影) transformation
    - Orthographic projection (cuboid to “canonical” cube [-1, 1]^3)
    - Perspective projection (frustum to “canonical” cube)
        - First “squish” the frustum into a cuboid
        - Then Do orthographic projection

---

向量以一列的矩阵表示（nx1）

矩阵(nxn)表示变换，在向量左边，左乘

- 旋转：绕某个轴旋转某个角度
    - 轴：向量，六个自由度（由两个点确定一条直线）
    - 角度：值
    - 绕经过原点的轴：Rodrigues’ Rotation Formula：
        
        $$\mathbf{R}(\mathbf{n}, \alpha)=\cos (\alpha) \mathbf{I}+(1-\cos (\alpha)) \mathbf{n} \mathbf{n}^{T}+\sin (\alpha)\left(\begin{array}{ccc} 0 & -n_{z} & n_{y} \\ n_{z} & 0 & -n_{x} \\ -n_{y} & n_{x} & 0 \end{array}\right)$$
        
    - 绕空间中任意轴旋转：[三维空间绕任意轴旋转矩阵的推导 - 知乎](https://zhuanlan.zhihu.com/p/56587491)
        - 记得哪本书里看见过……
- 平移：三个自由度
- 缩放：三个自由度

若绕静坐标系（世界坐标系）旋转，则左乘，也是变换矩阵坐标矩阵；若是绕动坐标系旋转（自身建立一个坐标系），则右乘，也就是坐标矩阵变换矩阵。 即，左乘是相对于坐标值所在的坐标系（世界坐标系）下的三个坐标轴进行旋转变换。而右乘则是以当前点为旋转中心，进行旋转变换。