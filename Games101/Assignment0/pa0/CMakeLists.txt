cmake_minimum_required (VERSION 2.8.11)
project (Transformation)
project (Test)

find_package(Eigen3 REQUIRED)
include_directories(EIGEN3_INCLUDE_DIR)

add_executable (Transformation main.cpp)
add_executable (Test TestAss1.cpp)
