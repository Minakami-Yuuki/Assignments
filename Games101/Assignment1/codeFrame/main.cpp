#include "Triangle.hpp"
#include "rasterizer.hpp"
#include <eigen3/Eigen/Eigen>
#include <iostream>
#include <opencv2/opencv.hpp>

constexpr double MY_PI = 3.1415926;

Eigen::Matrix4f get_view_matrix(Eigen::Vector3f eye_pos)
{
    Eigen::Matrix4f view = Eigen::Matrix4f::Identity();

    // view project transform
    // [1, 0, 0, -xe]
    // [0, 1, 0, -ye]
    // [0, 0, 1, -ze]
    // [0, 0, 0,   1]
    Eigen::Matrix4f translate;
    translate << 1, 0, 0, -eye_pos[0], 
                 0, 1, 0, -eye_pos[1], 
                 0, 0, 1, -eye_pos[2], 
                 0, 0, 0, 1;

    view = translate * view;

    return view;
}

Eigen::Matrix4f get_model_matrix(float rotation_angle)
{
    Eigen::Matrix4f model = Eigen::Matrix4f::Identity();

    // Complete by yourself
    Eigen::Matrix4f translate;

    float sinx = std::sin(rotation_angle / 180.0 * MY_PI);
    float cosx = std::cos(rotation_angle / 180.0 * MY_PI);

    // Rotating the triangle around the X aixs;
    Eigen::Matrix4f Rx;
    Rx << 1, 0, 0, 0,
          0, cosx, -sinx, 0,
          0, sinx, cosx, 0,
          0, 0, 0, 1;

    // Rotating the triangle around the Y aixs;
    Eigen::Matrix4f Ry;
    Ry << cosx, 0, sinx, 0,
          0, 1, 0, 0,
          -sinx, 0, cosx, 0,
          0, 0, 0, 1;
    
    // Rotating the triangle around the Z aixs;
    Eigen::Matrix4f Rz;
    Rz << cosx, -sinx, 0, 0,
          sinx, cosx, 0, 0,
          0, 0, 1, 0,
          0, 0, 0, 1;
    
    // Only rotate aroung Z aixs
    model = Rz * model;

    return model;
}

// eve_fov is the entitle graphics' angle
// aspect_ratio is width : length == r : t
Eigen::Matrix4f get_projection_matrix(float eye_fov, float aspect_ratio,
                                      float zNear, float zFar)
{
    // Complete by yourself

    Eigen::Matrix4f projection = Eigen::Matrix4f::Identity();

    // Top of Viewing cone is (0, 0, 0),
    // so the zNear and zFar is negative (< 0)
    // define negative (zNear > zFar)
    float n = -zNear, f = -zFar;

    // 1. (t / |n|) = ((eve_fov / 2) / 180) * Pi
    // 2. y aixs = |n| * (t / |n|) = t
    // 3. x aixs = |n| * (r / |n|) = r
    float angle = eye_fov / 360 * MY_PI;
    float t = tan(angle) * abs(n);
    float r = t * aspect_ratio;
    // Symmertrical viewing cone
    // bottom = -top, left = -right
    float b = -t, l = -r;

    // Perspective projection transform
    Eigen::Matrix4f Ppt;
    Ppt << n, 0, 0, 0,
           0, n, 0, 0,
           0, 0, n + f, -n * f,
           0, 0, 1, 0;

    // Orthographic projection transfrom
    
    // First Translation:
    // [0, 0, 0, -(r + l) / 2]
    // [0, 0, 0, -(t + b) / 2]
    // [0, 0, 0, -(n + f) / 2]
    // [0, 0, 0, 1]
    Eigen::Matrix4f OptTranslation;
    OptTranslation << 1, 0, 0, -(r+l)/2,
                      0, 1, 0, -(t+b)/2,
                      0, 0, 1, -(n+f)/2,
                      0, 0, 0, 1;

    // Last Scale:
    // [2 / (r - l), 0, 0, 0]
    // [0, 2 / (t - b), 0, 0]
    // [0, 0, 2 / (n - f), 0]
    // [0, 0, 0, 1]
    Eigen::Matrix4f OptScale;
    OptScale << 2/(r-l), 0, 0, 0,
                0, 2/(t-b), 0, 0,
                0, 0, 2/(n-f), 0,
                0, 0, 0, 1;
    
    Eigen::Matrix4f Opt = OptScale * OptTranslation;

    projection = Opt * Ppt;

    return projection;
}

int main(int argc, const char** argv)
{
    float angle = 0;
    bool command_line = false;
    std::string filename = "output.png";

    if (argc >= 3) {
        command_line = true;
        angle = std::stof(argv[2]); // -r by default
        if (argc == 4) {
            filename = std::string(argv[3]);
        }
        else
            return 0;
    }

    rst::rasterizer r(700, 700);

    Eigen::Vector3f eye_pos = {0, 0, 5};

    std::vector<Eigen::Vector3f> pos{{2, 0, -2}, {0, 2, -2}, {-2, 0, -2}};

    std::vector<Eigen::Vector3i> ind{{0, 1, 2}};

    auto pos_id = r.load_positions(pos);
    auto ind_id = r.load_indices(ind);

    int key = 0;
    int frame_count = 0;

    if (command_line) {
        r.clear(rst::Buffers::Color | rst::Buffers::Depth);

        r.set_model(get_model_matrix(angle));
        r.set_view(get_view_matrix(eye_pos));
        r.set_projection(get_projection_matrix(45, 1, 0.1, 50));

        r.draw(pos_id, ind_id, rst::Primitive::Triangle);
        cv::Mat image(700, 700, CV_32FC3, r.frame_buffer().data());
        image.convertTo(image, CV_8UC3, 1.0f);

        cv::imwrite(filename, image);

        return 0;
    }

    while (key != 27) {
        r.clear(rst::Buffers::Color | rst::Buffers::Depth);

        r.set_model(get_model_matrix(angle));
        r.set_view(get_view_matrix(eye_pos));
        r.set_projection(get_projection_matrix(45, 2, 0.1, 50));

        r.draw(pos_id, ind_id, rst::Primitive::Triangle);

        cv::Mat image(700, 700, CV_32FC3, r.frame_buffer().data());
        image.convertTo(image, CV_8UC3, 1.0f);
        cv::imshow("image", image);
        key = cv::waitKey(10);

        std::cout << "frame count: " << frame_count++ << '\n';

        if (key == 'a') {
            angle += 10;
        }
        else if (key == 'd') {
            angle -= 10;
        }
    }

    return 0;
}
