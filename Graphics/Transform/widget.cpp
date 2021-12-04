#include "widget.h"
#include <QtWidgets>

Widget::Widget(QWidget *parent)
    : QWidget(parent)
{
    float t[] = {0.f, 0.f, 50.f, 90.f};
    Line *line = new Line(t, t + 2);
    t[0] = 30.f, t[1] = 30.f;
    
    figureLst.push_back(line);
    QVector<QPointF> p;
    p << QPointF(100,10) << QPointF(80,80) << QPointF(10, 100) << QPointF(80, 110) << QPointF(20, 140) << QPointF(85, 125) \
    << QPointF(85, 150) << QPointF(20, 153) << QPointF(60, 170) << QPointF( 45, 175) << QPointF( 80, 185) << QPointF(60, 210) \
    << QPointF(120, 185) << QPointF(165, 175) << QPointF( 100, 170) << QPointF(110, 150) << QPointF(120, 125) << QPointF(140, 140) \
    << QPointF(139, 110) << QPointF(108, 90) << QPointF(149, 90) << QPointF(148, 70 ) << QPointF(120, 70);
    figureLst.push_back(new MyPolygon(p));
    
    float mat[3][3];
    Figure2D::resetMatrix(mat);
    mat[0][2] = 100;
    mat[1][2] = 100;
    figureLst.back()->transformLocal(mat);

    resize(800, 600);
    //image = new QImage(800, 600, QImage::Format_RGB32);
    //image->fill(Qt::white);
    
    isSelected = false;
    transformType = -1;
    curFigurePtr = NULL;

}

Widget::~Widget()
{
    for(int i = 0; i < figureLst.size(); ++i)
        delete figureLst[i];

}

void Widget::paintEvent(QPaintEvent *event){
    QPainter painter(this);
    QImage image(800, 600, QImage::Format_RGB32);
    image.fill(Qt::white);
    for(int i = 0; i < figureLst.size(); ++i)
        figureLst[i]->paint(image);
    painter.drawImage(0,0, image);
}

void Widget::mousePressEvent(QMouseEvent *event){
    if(event->button() == Qt::LeftButton){
        Figure2D* lastSelected = curFigurePtr;
        for(int i = 0; i < figureLst.size(); ++i){
            if(figureLst.at(i)->selected(event->pos())){
                isSelected = true;
                curFigurePtr = figureLst.at(i);
                curFigurePtr->setActive(true);
                startPt = event->pos();
                initialPt = startPt;
            }
        }
        if(lastSelected != curFigurePtr && lastSelected != NULL){
            lastSelected->setActive(false);
        }
    }
    if(isSelected){
        switch (event->modifiers()) {
            case Qt::CTRL:
                transformType = ROTATE;
                break;
            case Qt::ALT:
                transformType = SCALE;
                break;
            case Qt::ALT | Qt::CTRL:
                transformType = ROTATE_LOCAL;
                
                break;
            default:
                transformType = TRANSLATE;
        break;
        }
    }
}

void Widget::mouseReleaseEvent(QMouseEvent *event){
    if(event->button() == Qt::LeftButton){
        if(isSelected){
            isSelected = false;
            endPt = event->pos();
            switch (transformType) {
                case TRANSLATE:
                    curFigurePtr->translateInWindow(startPt, endPt);
                    break;
                case ROTATE:
                    curFigurePtr->rotateInWindow(startPt, endPt, QPointF(0.f, 0.f));
                    break;
                case ROTATE_LOCAL:
                    curFigurePtr->rotateInWindow(startPt, endPt, curFigurePtr->getRefCenterInWindow());
                    break;
                case SCALE:
                    curFigurePtr->scaleInWindow(startPt, endPt, curFigurePtr->getRefCenterInWindow());
                    break;
                default:
                    break;
            }
        }
    }
    transformType = TRANSLATE;
    update();
}

void Widget::mouseMoveEvent(QMouseEvent *event){
    if(isSelected){
        //qDebug() << "MOVE";
        endPt = event->pos();
        switch (transformType) {
            case TRANSLATE:
                curFigurePtr->translateInWindow(startPt, endPt);
                ;
                break;
            case ROTATE:
                curFigurePtr->rotateInWindow(startPt, endPt, QPointF(0.f, 0.f));
                
                break;
            case ROTATE_LOCAL:
                curFigurePtr->rotateInWindow(startPt, endPt, curFigurePtr->getRefCenterInWindow());
                break;
            case SCALE:
                curFigurePtr->scaleInWindow(startPt, endPt, curFigurePtr->getRefCenterInWindow());
                break;
            default:
                break;
        }
    }
    startPt = endPt;
    update();
}


