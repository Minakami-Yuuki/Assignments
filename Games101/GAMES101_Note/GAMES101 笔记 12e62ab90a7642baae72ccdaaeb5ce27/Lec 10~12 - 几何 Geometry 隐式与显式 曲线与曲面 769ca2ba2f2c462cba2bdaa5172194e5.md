# Lec 10~12 - 几何 Geometry 隐式与显式 曲线与曲面

几何形体

## 几何的表达方式

- Implicit
    - Based on **classifying points**
    - Sampling Can Be Hard
    - Inside/Outside Tests Easy
    - 种类
        - algebraic surface
            - 最直接的，用数学公式
            - 不直观
        - Constructive Solid Geometry（CSG）
            - 基本形状的布尔操作组合成复杂形状
            - Combine implicit geometry via Boolean operations
        - distance functions
            - 相关：Signed Distance Field
            - 解析形式表达
        - level sets （水平集）
            - Grid方式描述distance
            - 例子：CT扫描
        - Fractals
            - 自相似
            - 递归
        - ...
    
    Pros:
    
    • compact description (e.g., a function)
    
    • certain queries easy (inside object, distance to surface)
    
    • good for ray-to-surface intersection (more later)
    
    • for simple shapes, exact description / no sampling error
    
    • easy to handle changes in topology (e.g., fluid) 
    
    Cons:
    
    • difficult to model complex shapes
    
- Explicit
    - All points are given directly or via parameter mapping
    - Sampling Is Easy
    - Inside/Outside Test Hard
    - 种类
        - point cloud
            - 一堆点
            - 可以表示任何几何
            - Useful for LARGE datasets (>>1 point/pixel)
            - Often converted into polygon mesh
            - Difficult to draw in undersampled regions
        - triangle/polygon mesh
            - Store vertices & polygons (often triangles or quads)
            - More complicated data structures
            - Perhaps most common representation in graphics
            - 例子：.obj格式
                - Just a text file that specifies vertices, normals, texture coordinates and their connectivities
        - subdivision, NURBS
        - Bezier surfaces
        - subdivision surfaces
        - NURBS
        - ...

No “Best” Representation, Each choice best suited to a different task/type of geometry

More Implicit Representations in Computer Graphics

## 曲线 Curve

### Bézier Curves  贝塞尔曲线

一条由四个点（其实是任意≥3个点）定义的曲线：

- p0和p3定义起点和终点
- p1和p2定义起点与终点的切线方向（与p0和p3一起）

Evaluating Bézier Curves (de Casteljau Algorithm)

例子：(quadratic Bezier)

![Lec%2010~12%20-%20%E5%87%A0%E4%BD%95%20Geometry%20%E9%9A%90%E5%BC%8F%E4%B8%8E%E6%98%BE%E5%BC%8F%20%E6%9B%B2%E7%BA%BF%E4%B8%8E%E6%9B%B2%E9%9D%A2%20769ca2ba2f2c462cba2bdaa5172194e5/Untitled.png](Lec%2010~12%20-%20%E5%87%A0%E4%BD%95%20Geometry%20%E9%9A%90%E5%BC%8F%E4%B8%8E%E6%98%BE%E5%BC%8F%20%E6%9B%B2%E7%BA%BF%E4%B8%8E%E6%9B%B2%E9%9D%A2%20769ca2ba2f2c462cba2bdaa5172194e5/Untitled.png)

![Lec%2010~12%20-%20%E5%87%A0%E4%BD%95%20Geometry%20%E9%9A%90%E5%BC%8F%E4%B8%8E%E6%98%BE%E5%BC%8F%20%E6%9B%B2%E7%BA%BF%E4%B8%8E%E6%9B%B2%E9%9D%A2%20769ca2ba2f2c462cba2bdaa5172194e5/Untitled%201.png](Lec%2010~12%20-%20%E5%87%A0%E4%BD%95%20Geometry%20%E9%9A%90%E5%BC%8F%E4%B8%8E%E6%98%BE%E5%BC%8F%20%E6%9B%B2%E7%BA%BF%E4%B8%8E%E6%9B%B2%E9%9D%A2%20769ca2ba2f2c462cba2bdaa5172194e5/Untitled%201.png)

![Lec%2010~12%20-%20%E5%87%A0%E4%BD%95%20Geometry%20%E9%9A%90%E5%BC%8F%E4%B8%8E%E6%98%BE%E5%BC%8F%20%E6%9B%B2%E7%BA%BF%E4%B8%8E%E6%9B%B2%E9%9D%A2%20769ca2ba2f2c462cba2bdaa5172194e5/Untitled%202.png](Lec%2010~12%20-%20%E5%87%A0%E4%BD%95%20Geometry%20%E9%9A%90%E5%BC%8F%E4%B8%8E%E6%98%BE%E5%BC%8F%20%E6%9B%B2%E7%BA%BF%E4%B8%8E%E6%9B%B2%E9%9D%A2%20769ca2ba2f2c462cba2bdaa5172194e5/Untitled%202.png)

计算方法：

![Lec%2010~12%20-%20%E5%87%A0%E4%BD%95%20Geometry%20%E9%9A%90%E5%BC%8F%E4%B8%8E%E6%98%BE%E5%BC%8F%20%E6%9B%B2%E7%BA%BF%E4%B8%8E%E6%9B%B2%E9%9D%A2%20769ca2ba2f2c462cba2bdaa5172194e5/Untitled%203.png](Lec%2010~12%20-%20%E5%87%A0%E4%BD%95%20Geometry%20%E9%9A%90%E5%BC%8F%E4%B8%8E%E6%98%BE%E5%BC%8F%20%E6%9B%B2%E7%BA%BF%E4%B8%8E%E6%9B%B2%E9%9D%A2%20769ca2ba2f2c462cba2bdaa5172194e5/Untitled%203.png)

一些性质：

![Lec%2010~12%20-%20%E5%87%A0%E4%BD%95%20Geometry%20%E9%9A%90%E5%BC%8F%E4%B8%8E%E6%98%BE%E5%BC%8F%20%E6%9B%B2%E7%BA%BF%E4%B8%8E%E6%9B%B2%E9%9D%A2%20769ca2ba2f2c462cba2bdaa5172194e5/Untitled%204.png](Lec%2010~12%20-%20%E5%87%A0%E4%BD%95%20Geometry%20%E9%9A%90%E5%BC%8F%E4%B8%8E%E6%98%BE%E5%BC%8F%20%E6%9B%B2%E7%BA%BF%E4%B8%8E%E6%9B%B2%E9%9D%A2%20769ca2ba2f2c462cba2bdaa5172194e5/Untitled%204.png)

- 仿射变换前后统一
- 凸包性质：形成的曲线一定在控制点形成的凸包内

高阶贝塞尔曲线很难控制，任何一个点就能影响全局

改善→Piecewise Bézier Curves

- [Bezier Curve Edit](http://math.hws.edu/eck/cs424/notes2013/canvas/bezier.html)
- chain many low-order Bézier curve
- 例如：Piecewise cubic Bézier
- 每次四个控制点
- 保证光滑（切线不突变）：内部控制点前后的切线点共线

![Lec%2010~12%20-%20%E5%87%A0%E4%BD%95%20Geometry%20%E9%9A%90%E5%BC%8F%E4%B8%8E%E6%98%BE%E5%BC%8F%20%E6%9B%B2%E7%BA%BF%E4%B8%8E%E6%9B%B2%E9%9D%A2%20769ca2ba2f2c462cba2bdaa5172194e5/Untitled%205.png](Lec%2010~12%20-%20%E5%87%A0%E4%BD%95%20Geometry%20%E9%9A%90%E5%BC%8F%E4%B8%8E%E6%98%BE%E5%BC%8F%20%E6%9B%B2%E7%BA%BF%E4%B8%8E%E6%9B%B2%E9%9D%A2%20769ca2ba2f2c462cba2bdaa5172194e5/Untitled%205.png)

连续性定义：

- C^0连续：无断点
- C^1连续：无突变

Spline (样条)：a continuous curve constructed so as to pass through a given set of points and have a certain number of continuous derivatives. （a curve under control）

B-splines

- basis splines 基函数样条
- Bernstein Polynomial作为基函数，
- 满足局部性
- 可能是图形学里面最复杂的一部分
- 是贝塞尔曲线的超集

Further：B样条、NURBS（非均匀有理B样条）[https://www.bilibili.com/video/av66548502](https://www.bilibili.com/video/av66548502)

## 曲面

### Bézier Surfaces贝塞尔曲面

两个不同时间t（u,v）

4x4个点，四条4个控制点的贝塞尔曲线，取同一时间（比如说u）获得四个控制点，取时间v，即获得最后的曲面上的点

### Mesh

更广泛的还是Mesh

Mesh Operations: Geometry Processing

- Mesh subdivision 细分 upsampling
    - Increase resolution
- Mesh simplification 简化 downsampling
    - Decrease resolution
    - Try to preserve shape/appearance
- Mesh regularization 正规化
    - （不会出现特别奇怪的三角形）
    - Modify sample distribution to improve quality

## Loop Subdivision

细分的应用场景1：Displacement mapping 位移贴图 需要模型足够细致，于是需要细分（最好是动态细分）

**Loop是发明者名字，跟循环没关系**

需要三角形Mesh

步骤： 

1. create more triangles (vertices)
    
    Split each triangle into four
    
2. **tune their positions （形状需要有改变）**
    
    Assign new vertex positions according to weights
    
    New / old vertices updated differently 新老点分别改变
    

![Lec%2010~12%20-%20%E5%87%A0%E4%BD%95%20Geometry%20%E9%9A%90%E5%BC%8F%E4%B8%8E%E6%98%BE%E5%BC%8F%20%E6%9B%B2%E7%BA%BF%E4%B8%8E%E6%9B%B2%E9%9D%A2%20769ca2ba2f2c462cba2bdaa5172194e5/Untitled%206.png](Lec%2010~12%20-%20%E5%87%A0%E4%BD%95%20Geometry%20%E9%9A%90%E5%BC%8F%E4%B8%8E%E6%98%BE%E5%BC%8F%20%E6%9B%B2%E7%BA%BF%E4%B8%8E%E6%9B%B2%E9%9D%A2%20769ca2ba2f2c462cba2bdaa5172194e5/Untitled%206.png)

![Lec%2010~12%20-%20%E5%87%A0%E4%BD%95%20Geometry%20%E9%9A%90%E5%BC%8F%E4%B8%8E%E6%98%BE%E5%BC%8F%20%E6%9B%B2%E7%BA%BF%E4%B8%8E%E6%9B%B2%E9%9D%A2%20769ca2ba2f2c462cba2bdaa5172194e5/Untitled%207.png](Lec%2010~12%20-%20%E5%87%A0%E4%BD%95%20Geometry%20%E9%9A%90%E5%BC%8F%E4%B8%8E%E6%98%BE%E5%BC%8F%20%E6%9B%B2%E7%BA%BF%E4%B8%8E%E6%9B%B2%E9%9D%A2%20769ca2ba2f2c462cba2bdaa5172194e5/Untitled%207.png)

## Catmull-Clark Subdivision (General Mesh)

通用Mesh

Non-quad face：非四边的面

Extraordinary vertex (奇异点)：指(degree != 4)的点

Each subdivision step:

1. Add vertex in each face
2. Add midpoint on each edge
3. Connect all new vertices

![Lec%2010~12%20-%20%E5%87%A0%E4%BD%95%20Geometry%20%E9%9A%90%E5%BC%8F%E4%B8%8E%E6%98%BE%E5%BC%8F%20%E6%9B%B2%E7%BA%BF%E4%B8%8E%E6%9B%B2%E9%9D%A2%20769ca2ba2f2c462cba2bdaa5172194e5/Untitled%208.png](Lec%2010~12%20-%20%E5%87%A0%E4%BD%95%20Geometry%20%E9%9A%90%E5%BC%8F%E4%B8%8E%E6%98%BE%E5%BC%8F%20%E6%9B%B2%E7%BA%BF%E4%B8%8E%E6%9B%B2%E9%9D%A2%20769ca2ba2f2c462cba2bdaa5172194e5/Untitled%208.png)

![Lec%2010~12%20-%20%E5%87%A0%E4%BD%95%20Geometry%20%E9%9A%90%E5%BC%8F%E4%B8%8E%E6%98%BE%E5%BC%8F%20%E6%9B%B2%E7%BA%BF%E4%B8%8E%E6%9B%B2%E9%9D%A2%20769ca2ba2f2c462cba2bdaa5172194e5/Untitled%209.png](Lec%2010~12%20-%20%E5%87%A0%E4%BD%95%20Geometry%20%E9%9A%90%E5%BC%8F%E4%B8%8E%E6%98%BE%E5%BC%8F%20%E6%9B%B2%E7%BA%BF%E4%B8%8E%E6%9B%B2%E9%9D%A2%20769ca2ba2f2c462cba2bdaa5172194e5/Untitled%209.png)

![Lec%2010~12%20-%20%E5%87%A0%E4%BD%95%20Geometry%20%E9%9A%90%E5%BC%8F%E4%B8%8E%E6%98%BE%E5%BC%8F%20%E6%9B%B2%E7%BA%BF%E4%B8%8E%E6%9B%B2%E9%9D%A2%20769ca2ba2f2c462cba2bdaa5172194e5/Untitled%2010.png](Lec%2010~12%20-%20%E5%87%A0%E4%BD%95%20Geometry%20%E9%9A%90%E5%BC%8F%E4%B8%8E%E6%98%BE%E5%BC%8F%20%E6%9B%B2%E7%BA%BF%E4%B8%8E%E6%9B%B2%E9%9D%A2%20769ca2ba2f2c462cba2bdaa5172194e5/Untitled%2010.png)

奇异点在第一次细分增加[非四边形面数量]，后续不变；所有非四边形面在第一次细分都会消失

Update Rules (Quad Mesh)：

![Lec%2010~12%20-%20%E5%87%A0%E4%BD%95%20Geometry%20%E9%9A%90%E5%BC%8F%E4%B8%8E%E6%98%BE%E5%BC%8F%20%E6%9B%B2%E7%BA%BF%E4%B8%8E%E6%9B%B2%E9%9D%A2%20769ca2ba2f2c462cba2bdaa5172194e5/Untitled%2011.png](Lec%2010~12%20-%20%E5%87%A0%E4%BD%95%20Geometry%20%E9%9A%90%E5%BC%8F%E4%B8%8E%E6%98%BE%E5%BC%8F%20%E6%9B%B2%E7%BA%BF%E4%B8%8E%E6%9B%B2%E9%9D%A2%20769ca2ba2f2c462cba2bdaa5172194e5/Untitled%2011.png)

## Mesh Simplification

Goal: reduce number of mesh elements while maintaining the overall shape

应用：移动端、远距离（LOD）

几何的层次结构

Edge Collapse：顶点合并

- 哪些边合并？如何合并？
    - Quadric Error Metrics（⼆次误差度量）放在二次误差之和最小的地方

Simplification via Quadric Error

- Garland & Heckbert 1997.
- iteratively collapse edge with smallest score
- 有问题，一条边的操作会影响其它边，需要更新
    - 数据结构：优先队列 or 堆
- 贪心算法，非全局最优
- 可以有的放矢