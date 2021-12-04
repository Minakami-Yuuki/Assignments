#include "widget.h"
#include <QtWidgets>

#define PERSPECTIVE
float patch[][12] = {
    {
        0.f, 0.f, 0.f, \
        0.f, 100.f, 0.f, \
        100.f, 100.f, 0.f, \
        100.f, 0.f, 0.f, \
    },
    {
        0.f, 0.f, 50.f, \
        100.f, 0.f, 50.f,\
        100.f, 100.f, 50.f, \
        0.f, 100.f, 50.f, \
    },
    
    {
        0.f, 0.f, 0.f,\
        100.f, 0.f, 0.f, \
        100.f, 0.f, 50.f,\
        0.f, 0.f, 50.f
    },
    
    {
        0.f, 100.f, 0.f,\
        100.f, 100.f, 0.f,\
        100.f, 100.f, 50.f,\
        0.f, 100.f, 50.f
        
    },
    
    {
        0.f, 0.f, 0.f, \
        0.f, 0.f, 50.f, \
        0.f, 100.f, 50.f, \
        0.f, 100.f, 0.f
    },
    
    {
        100.f, 0.f, 0.f, \
        100.f, 0.f, 50.f,\
        100.f, 100.f, 50.f, \
        100.f, 100.f, 0.f
    }

};

void rotate(QVector<Patch3D>& patchList, double degree, QPointF refCenter, QVector3D axis) {
    //int i = 0;
    for(Patch3D& p : patchList){
        p.translate(QVector3D(-refCenter.x(), -refCenter.y(), 0.f));
        p.rotate(degree, axis);
        p.translate(QVector3D(refCenter.x(), refCenter.y(), 0.f));
    }
    
}

int patch_size = 6;

Widget::Widget(QWidget *parent)
    : QWidget(parent)
{
    eye = QVector3D(0.f, 0.f, -1000.f);
    nearPlane = 1000;
    viewDirection = QVector3D(0.f, -0.0f, 1.0f);
 
    resize(800, 600);
    
    QFile file("D:/Assignments/Graphics/ZBuffer/data_ch.txt");
    if(file.open(QIODevice::ReadOnly | QIODevice::Text)){
        QTextStream inStream(&file);
        while(!inStream.atEnd()){
            int n;
            inStream >> n;
            if(n == 0)
                break;
            QVector<float> list;
            list.reserve(n * 3);
            for(int i = 0; i < n; ++i){
                int a, b, c;
                inStream >> a >> b >> c;
                list << a << b << c;
            }
            patchList.push_back(Patch3D(list.data(), list.size(), QColor( rand() % 255, rand() % 255, rand() % 255) ));
        }
    }
    else{
        for(int i = 0; i < patch_size; ++i){
            patchList.push_back(Patch3D(patch[i], 12, QColor( rand() % 255, rand() % 255, rand() % 255) ));
        }
//        for(int i = 0; i < patch_size; ++i){
//            patchList.push_back(Patch3D(patch[i], 12, QColor( rand() % 255, rand() % 255, rand() % 255) ));
//        }

        for(int i = 0; i < patch_size; ++i){
            patchList[i].translate(QVector3D(400, 300, 0));
        }
//        for(int i = patch_size; i < 2 * patch_size; ++i){
//            patchList[i].translate(QVector3D(200, 200, 100));
//        }
        //rotate(patchList, 45, QPointF(400, 300), QVector3D(0.f, 1.f,0));
        //rotate(patchList, 45, QPointF(400, 300), QVector3D(1.f, 0.,0));
    }
    //patchList.first().translate(QVector3D(0, 0 ,-1));
    //patchList.first().rotate( 30, QVector3D(1.f, 0.f, 0.f) );
    //patchList.first().translate(QVector3D(0, 0 ,1));
#ifdef PERSPECTIVE
    perspective(eye, viewDirection, nearPlane);
#endif

}

Widget::~Widget()
{


}

void ZBuffer(QImage &image, const QVector<Patch3D>& patchList, QVector4D eye){
    QVector<QPolygonF> planarList;
    planarList.reserve(patchList.size());
    QVector<int> patchIdList;
    for(int i = 0; i < patchList.size(); ++i){
        const Patch3D& patch = patchList.at(i);
#ifdef PERSPECTIVE
        patchIdList << i;
        planarList << patch.getPerspectiveProjection(QVector3D(eye));
#else
        if(!patch.isOrthoXOY()) {
            patchIdList << i;
            planarList << patch.getXOYOrthoProjection();
        }
#endif
    }
    QVector<float> zbuffer;
    zbuffer.resize(800 * 600);
    zbuffer.fill(1000000.);
    for(int i = 0; i < planarList.size(); ++i){
        const QPolygonF& p = planarList.at(i);
        QRectF boundingBox = p.boundingRect();
        for(int x = boundingBox.left(); x < boundingBox.right(); ++x) {
            if(x <= 0 || x >=800 )
                continue;
            for(int y = boundingBox.top() ; y < boundingBox.bottom(); ++y){
                if(y <= 0 || y >= 600)
                    continue;
                unsigned char *scanLine = image.scanLine(y);
                if(p.containsPoint(QPointF(x,y), Qt::OddEvenFill)) {
                    double z;
                    if(eye.w() == 0)
                        z = patchList.at(patchIdList.at(i)).getZValue(QPointF(x, y));
                    else
                        z = patchList.at(patchIdList.at(i)).getZValue(QVector3D(eye), QVector3D(x - eye.x(), y - eye.y(), - eye.z()).normalized());
                    if(z < zbuffer[y * 800 + x]) {
                        zbuffer[y * 800 + x] = z;
                        QColor c = patchList.at(patchIdList.at(i)).getColor();
                        scanLine[x * 3 + 0] = c.red();
                        scanLine[x * 3 + 1] = c.green();
                        scanLine[x * 3 + 2] = c.blue();
                    }
                }
            }
        }
    }
}



void Widget::paintEvent(QPaintEvent *event){
    QImage image(800, 600, QImage::Format_RGB888);
    image.fill(qRgb(255,255,255));
    //QPainter painter_img(&image);
#ifdef PERSPECTIVE
    ZBuffer(image, patchList, QVector4D(eye,1.f));
#else
    ZBuffer(image, patchList, QVector4D(eye,0.f));
#endif
    QPainter painter(this);
    painter.setRenderHint(QPainter::Antialiasing);
    painter.drawImage(0,0, image);
#ifdef PERSPECTIVE
    painter.drawText(QPointF(10,20),QString("Eye: %1, %2, %3").arg(eye.x()).arg(eye.y()).arg(eye.z()));
    painter.drawText(QPointF(10,40),QString("Near Plane: z=%1, View Direction:%2, %3, %4").arg(nearPlane).arg(viewDirection.x()).arg(viewDirection.y()).arg(viewDirection.z()));
#endif
}

void Widget::mousePressEvent(QMouseEvent *event){
    startPt = event->pos();
    //rotate(patchList, 5, QPointF(400, 300), QVector3D(0.f, 1.f, 0.f));
    //update();
}

void Widget::mouseReleaseEvent(QMouseEvent *event){
 
    update();
}

void Widget::mouseMoveEvent(QMouseEvent *event){
    endPt = event->pos();
    //QVector3D v1(endPt.x() - 400, , endPt.y() - 300);
    rotate(patchList, qRadiansToDegrees((-endPt.x() + startPt.x()) /400.f) , QPointF(400,300), QVector3D(0.f,1.f, 0.f) );
    rotate(patchList, qRadiansToDegrees((endPt.y() - startPt.y()) /400.f) , QPointF(400,300), QVector3D(1.f,0.f, 0.f) );
    update();
    startPt = endPt;
}


