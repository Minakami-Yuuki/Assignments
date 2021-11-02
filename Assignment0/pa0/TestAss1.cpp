#include<cmath>
#include<eigen3/Eigen/Core>
#include<eigen3/Eigen/Dense>
#include<iostream>

// std:: 
// acos(-1) == Pi

int main(){
    
    // input a point (2,1)
    float x = 2, y = 1;
    // turn to homogeneours coordinates
    Eigen::Vector3f p(x, y, 1);
    // turn left 45 degree
    // Translation (1, 2)
    // warning: 
    // Translation Matrix in Linear Mapping is (0, 0, 1)
    Eigen::Matrix3f i;
    i << std::cos((45.0/180.0) * acos(-1)),
         std::sin((-45.0/180.0) * acos(-1)),
         1.0,
         std::sin((45.0/180.0) * acos(-1)),
         std::cos((45.0/180.0) * acos(-1)),
         2.0,
         0.0,
         0.0,
         1.0;

    // Point P
    std::cout << p << std::endl;
    std::cout << std::endl;
    // Matrix i
    std::cout << i << std::endl;
    std::cout << std::endl;
    // homocoor (include rotate and translate)
    // eventual result is a point .. as (X, Y, 1)
    std::cout << i * p << std::endl;

    std::cout << std::endl;
    return 0;
}