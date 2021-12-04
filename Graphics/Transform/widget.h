#ifndef WIDGET_H
#define WIDGET_H

#include <QWidget>
#include <QtCore>
#include <QPainter>
#include <QVector2D>
#include <QPointF>

class Figure2D {
protected:
    float transMatLocal2Window[3][3];
    //float matrixTemp[_nDim + 1][_nDim + 1];
    QPointF centerRef;
    QPen pen;
    QBrush brush;
    bool bActive;
    QPen boundingPen;
    
public:
    static void leftMultiply(const float lhs[][3], const float rhs[][3], float r[][3]){
        float temp_r[3][3];
        float temp_l[3][3];
        memcpy(temp_r, rhs, sizeof(float) * 9);
        memcpy(temp_l, lhs, sizeof(float) * 9);
        for(int i = 0; i < 3; ++i){
            for(int j = 0; j < 3; ++j){
                double sum = 0.;
                for(int k = 0; k < 3; ++k){
                    sum += temp_l[i][k] * temp_r[k][j];
                }
                r[i][j] = sum;
            }
        }
    }
    
    //do after = matrix * before, where matrix is a 3 * 3 matrix, before is a 3 * 1 vector representing a point.
    static void transformPoint(const float matrix[][3], const float before[3], float after[3]){
        float temp_before[3];
        memcpy(temp_before, before, sizeof(float) * 3);
        for(int i = 0; i < 3; ++i){
            double sum = 0.;
            for(int j = 0; j < 3; ++j){
                sum += temp_before[j] * matrix[i][j];
            }
            after[i] = sum;
        }
    }
    
    static void resetMatrix(float m[][3]){
        memset(m, 0, sizeof(float) * (3) * (3));
        for(int i = 0; i < 3; ++i)
            m[i][i] = 1.f;
    }
    
    Figure2D() {
        resetMatrix(transMatLocal2Window);
        centerRef = QPointF(0.f, 0.f);
        pen.setWidthF(2);
        brush.setColor(QColor(rand() % 255, rand() % 255, rand() % 255));
        brush.setStyle(Qt::SolidPattern);
        bActive = false;
        

        boundingPen.setStyle(Qt::DashLine);
        boundingPen.setColor(Qt::gray);
        boundingPen.setWidthF(1);
    }
    
    void setActive(bool act){
        bActive = act;
    }
    
    //calculate each local point's Window Coordinate
    void transformLocal2Window(const float before[3], float after[3]) const {
        transformPoint(transMatLocal2Window, before, after);
    }
    
    //return the local origin point in Local Coordinate
    QPointF getRefCenterInLocal() const{
        return centerRef;
    }
    
    //return the local origin point in Window Coordinate
    QPointF getRefCenterInWindow() const{
        float before[3], after[3];
        before[0] = centerRef.x();
        before[1] = centerRef.y();
        before[2] = 1.;
        transformLocal2Window(before, after);
        return QPointF(after[0], after[1]);
    }
    
    //translating the local origin to somewhere measured in Window coordinate
    void translateInWindow(const QPointF& startPt, const QPointF& endPt){
        //qDebug() << centerRef;
        double delta[2] = {endPt.x() - startPt.x(), endPt.y() - startPt.y()};
        float matrixTemp[3][3];
        resetMatrix(matrixTemp);
        for(int i = 0; i < 2; ++i){
            matrixTemp[i][2] = delta[i];
        }
        leftMultiply(matrixTemp, transMatLocal2Window, transMatLocal2Window);
    }
    
    //translate local origin in Local Coordinate
    void setRefCenterLocal(const QPointF& nRefLocal ) {
        centerRef = nRefLocal;
    }
    
    //rotate each point WRT local origin in Local Coordinate
    //this function will change the local coordinate of each point
    void rotateLocal(double angle) {
        float matrixTemp[3][3];
        resetMatrix(matrixTemp);
        float t[3][3];
        resetMatrix(t);
        matrixTemp[0][2] = -centerRef.x();
        matrixTemp[1][2] = -centerRef.y();

        t[0][0] = qCos(angle);
        t[0][1] = -qSin(angle);
        t[1][0] = -t[0][1];
        t[1][1] = t[0][0];
        leftMultiply(t, matrixTemp, matrixTemp);
        t[0][2] = centerRef.x();
        t[1][2] = centerRef.y();
        leftMultiply(t, matrixTemp, matrixTemp);
        leftMultiply(matrixTemp, matrixTemp, matrixTemp);
        transformLocal(matrixTemp);
    }
    
    //translate each point WRT local origin in Local Coordinate
    //this function will change the coordinate of each point
    void translateLocal(float deltaX = 0., float deltaY = 0.){
        float matrixTemp[3][3];
        resetMatrix(matrixTemp);
        matrixTemp[0][2] = deltaX;
        matrixTemp[1][2] = deltaY;
        transformLocal(matrixTemp);
    }
    
    //rotate the point WRT refCenter measured in Window Coordinate.
    //this function will not change the value of the local coordinate of each point
    void rotateInWindow(const QPointF& startPt, const QPointF& endPt, QPointF centerInWindow = QPointF(0.f, 0.f)) {
        //QPointF centerInWindow = Figure2D::getRefCenterInWindow();
        QVector2D vs(startPt.x() - centerInWindow.x(), startPt.y() - centerInWindow.y());
        QVector2D ve(endPt.x() - centerInWindow.x(), endPt.y() - centerInWindow.y());
        double angle;
   
        vs.normalize();
        ve.normalize();
        angle = qAcos(QVector2D::dotProduct(ve, QVector2D(1,0))) - qAcos(QVector2D::dotProduct(vs, QVector2D(1,0)));
        if(vs.y() < 0 && ve.y() < 0) {
            angle = qAcos(QVector2D::dotProduct(vs, QVector2D(1,0))) - qAcos(QVector2D::dotProduct(ve, QVector2D(1,0)));
        }
        float temp[3][3];
        resetMatrix(temp);
        //transalte to rotate center : centerInWindow
        temp[0][2] = -centerInWindow.x();
        temp[1][2] = -centerInWindow.y();
        leftMultiply(temp, transMatLocal2Window, transMatLocal2Window);
        //rotate
        resetMatrix(temp);
        temp[0][0] = qCos(angle);
        temp[1][1] = temp[0][0];
        temp[0][1] = -qSin(angle);
        temp[1][0] = -temp[0][1];
        leftMultiply(temp, transMatLocal2Window, transMatLocal2Window);
        
        //translate back
        resetMatrix(temp);
        temp[0][2] = centerInWindow.x();
        temp[1][2] = centerInWindow.y();
        leftMultiply(temp, transMatLocal2Window, transMatLocal2Window);
    }
    
    void scaleInWindow(const QPointF& startPt, const QPointF& endPt, QPointF centerInWindow = QPointF(0.f, 0.f)) {
        float dx, dy, sx, sy;
        QRectF boundingBox = getBoundingRect();
        dx = qAbs(startPt.x() - centerInWindow.x());
        dy = qAbs(endPt.x() - centerInWindow.x());
        if(dx > dy){ //zoom in
            sx = 1.f - 0.1f;
            sy = 1.f - 0.1f;
        }
        else if(dx < dy){
            //zoom out
            sx = 1.f + 0.1f;
            sy = 1.f + 0.1f;
        }
        else{
            sx = 1.f;
            sy = 1.f;
        }

        float temp[3][3];
        resetMatrix(temp);
        //transalte to rotate center : centerInWindow
        temp[0][2] = -centerInWindow.x();
        temp[1][2] = -centerInWindow.y();
        leftMultiply(temp, transMatLocal2Window, transMatLocal2Window);
        //rotate
        resetMatrix(temp);
        temp[0][0] = sx;
        temp[1][1] = sy;

        leftMultiply(temp, transMatLocal2Window, transMatLocal2Window);
        
        //translate back
        resetMatrix(temp);
        temp[0][2] = centerInWindow.x();
        temp[1][2] = centerInWindow.y();
        leftMultiply(temp, transMatLocal2Window, transMatLocal2Window);
    }
  
    void printMatrix() const {
        for(int i = 0; i < 3; ++i){
            for(int j = 0; j < 2; ++j)
                printf("%f ", transMatLocal2Window[i][j]);
            printf("%f\n", transMatLocal2Window[i][2]);
        }
    }
    virtual QVector<QPointF> getPointListInWindow() const = 0;
    virtual void paint (QImage &) const = 0;
    virtual bool selected(const QPoint& clickPt) const  = 0;
    virtual void transformLocal(const float m[][3]) = 0;
    virtual QRectF getBoundingRect() const = 0;
    
};

class Line : public Figure2D{
private:
    float startPt[3], endPt[3];
public:
    Line(float t1[2], float t2[2]){
        for(int i = 0; i < 2; ++i){
            startPt[i] = t1[i];
            endPt[i] = t2[i];
        }
        startPt[2] = 1.f;
        endPt[2] = 1.f;
    }
    
    QVector<QPointF> getPointListInWindow() const {
        QVector<QPointF> ptAfterTransformedLst;
        float t[3];
        this->transformLocal2Window(startPt, t);
        ptAfterTransformedLst.push_back(QPointF(t[0], t[1]));
        this->transformLocal2Window(endPt, t);
        ptAfterTransformedLst.push_back(QPointF(t[0], t[1]));
        return ptAfterTransformedLst;
    };
    
    void paint (QImage &image) const {
        QVector<QPointF> ptAfterTransformedLst = getPointListInWindow();
        QPainter painter(&image);
        painter.setRenderHint(QPainter::Antialiasing);
        painter.setPen(pen);
        painter.setBrush(brush);
        painter.drawLine(ptAfterTransformedLst.at(0), ptAfterTransformedLst.at(1));
        QPointF t = Figure2D::getRefCenterInWindow();
        painter.drawRect(t.x() - 1, t.y() - 1, 3, 3);
        
        QLineF line(ptAfterTransformedLst.at(0), ptAfterTransformedLst.at(1));
        QRectF boundingBox;
        boundingBox.setTopLeft(ptAfterTransformedLst.at(0));
        boundingBox.setBottomRight(ptAfterTransformedLst.at(1));
        
        painter.setPen(boundingPen);
        painter.setBrush(Qt::NoBrush);
        if(bActive)
            painter.drawRect(boundingBox);
    };
    
    bool selected(const QPoint& clickPt) const{
        QVector<QPointF> ptAfterTransformedLst = getPointListInWindow();
        QVector2D v(clickPt.x(), clickPt.y());
        for(int i = 0; i < ptAfterTransformedLst.size(); ++i){
            QVector2D v1(ptAfterTransformedLst.at(i).x(), ptAfterTransformedLst.at(i).y());
            if(v.distanceToPoint(v1) < 3.)
                return true;
        }
        return false;
    }
    
    void transformLocal(const float m[][3]) {
        transformPoint(m, startPt, startPt);
        transformPoint(m, endPt, endPt);
    }
    QRectF getBoundingRect() const {
        return QRectF(startPt[0], startPt[1], endPt[0], endPt[1]);
    }
};


class MyPolygon : public Figure2D{
private:
    QVector<QPointF> ptList;

public:
    MyPolygon(const QVector<QPointF>& _list){
        ptList = _list;
        QPolygonF polygon (ptList);
        centerRef = polygon.boundingRect().center();
    }
    
    QVector<QPointF> getPointListInWindow() const {
        QVector<QPointF> ptAfterTransformedLst;
        ptAfterTransformedLst.reserve(ptList.size());
        for(int i = 0; i < ptList.size(); ++i){
            float t[3];
            t[0] = ptList.at(i).x();
            t[1] = ptList.at(i).y();
            t[2] = 1.f;
            transformLocal2Window(t, t);
            ptAfterTransformedLst << QPointF(t[0], t[1]);
        }

        return ptAfterTransformedLst;
    };
    
    void paint (QImage &image) const {
        QPolygonF polygon (getPointListInWindow());
        QPainter painter(&image);
        painter.setRenderHint(QPainter::Antialiasing);
        painter.setPen(pen);
        painter.setBrush(brush);
        painter.drawPolygon(polygon);
        QPointF t = Figure2D::getRefCenterInWindow();
        painter.drawRect(t.x() - 1, t.y() - 1, 3, 3);
        
        painter.setPen(boundingPen);
        painter.setBrush(Qt::NoBrush);
        
        if(bActive)
            painter.drawRect(polygon.boundingRect());
        
    };
    
    bool selected(const QPoint& clickPt) const {
        QPolygonF polygon (getPointListInWindow());
        if(polygon.containsPoint(clickPt, Qt::OddEvenFill))
            return true;
        return false;
    }
    
    void transformLocal(const float m[][3]) {
        float t[3];
        for(int i = 0; i < ptList.size(); ++i ){
            t[0] = ptList.at(i).x();
            t[1] = ptList.at(i).y();
            t[2] = 1.f;
            transformPoint(m, t, t);
            ptList[i].setX(t[0]);
            ptList[i].setY(t[1]);
        }
        
        //transform local origin
        t[0] = centerRef.x();
        t[1] = centerRef.y();
        t[2] = 1.f;
        transformPoint(m, t, t);
        centerRef.setX(t[0]);
        centerRef.setY(t[1]);
    }
    
    QRectF getBoundingRect() const {
        QPolygonF polygon (getPointListInWindow());
        return polygon.boundingRect();
    }
};



class Widget : public QWidget
{
    Q_OBJECT
    QVector<Figure2D *> figureLst;
    QImage *image;
    Figure2D* curFigurePtr;
    Figure2D* movingTmpFigure;
    bool isSelected;
    int transformType;
    
    QPoint startPt, endPt, initialPt;
    enum TRANSFORM{TRANSLATE, TRANSLATE_LOCAL, ROTATE, ROTATE_LOCAL, SCALE};
    
protected:
    virtual void paintEvent(QPaintEvent *event);
    virtual void mousePressEvent(QMouseEvent* event);
    virtual void mouseReleaseEvent(QMouseEvent *event);
    virtual void mouseMoveEvent(QMouseEvent *event);
public:
    Widget(QWidget *parent = 0);
    ~Widget();
};

#endif // WIDGET_H
