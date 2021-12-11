#ifndef WIDGET_H
#define WIDGET_H

#include <QtWidgets>
#include <QWidget>
#include <QtCore>
#include <QPainter>
#include <QVector2D>
#include <QPointF>
#include <cassert>


class Patch3D {
    QVector<QVector4D> ptList;
    QColor color;
    QVector4D equation;

    void updateEquation() {
        QVector3D v1, v2;
        v1 = QVector3D(ptList.at(1) - ptList.at(0));
        v2 = QVector3D(ptList.at(2) - ptList.at(1));
        //(QVector3D::crossProduct(v1, v2)).
        QVector3D normal = QVector3D::crossProduct(v1, v2);
        normal.normalize();
        
        float d = -QVector3D::dotProduct(QVector3D(ptList.at(0)), normal);
        equation = QVector4D( normal.x(), normal.y(), normal.z(), d);
    }
    
    
public:
    Patch3D (float * _t, size_t size, QColor c) : color(c){
        assert(size % 3 == 0 && size > 6);
        ptList.reserve(size / 3);
        int num = size / 3;
        for(int i = 0; i < num; ++i){
            ptList << QVector4D( _t[3 * i], _t[3 * i + 1], _t[3 * i + 2], 1.f);
        }
        updateEquation();
    }
    
    Patch3D () {
        
    }
    
    void rotate(double degree = 0., QVector3D axis = QVector3D(0.f, 0.f, 1.f)){
        QMatrix4x4 rotation;
        rotation.rotate(degree, axis);
        for(QVector4D& pt : ptList){
            pt = rotation.map(pt);
        }
        updateEquation();
    }
    
    void translate(QVector3D offset) {
        QMatrix4x4 translation;
        translation.translate(offset);
        for(QVector4D& pt : ptList){
            pt = translation.map(pt);
        }
        updateEquation();
    }
    
    QPolygonF getXOYOrthoProjection() const{
        QPolygonF polygon;
        for(const QVector4D & p : ptList){
            polygon << QPointF(p.x(), p.y());
        }
        return polygon;
    }
    QPolygonF getPerspectiveProjection(const QVector3D &pos) const {
        QPolygonF polygon;
        for(const QVector4D & p : ptList){
            polygon << QPointF((p.z() * pos.x() - p.x() * pos.z()) / (p.z() - pos.z()),
                               (p.z() * pos.y() - p.y() * pos.z()) / (p.z() - pos.z()));
        }
        return polygon;
    }
    
    
    bool isOrthoXOY() const{
        return qAbs(equation.z()) < 1.e-6;
    }
    
    bool isOrthoToViewPlane(QVector3D) const{
        return qAbs(equation.z()) < 1.e-6;
    }
    
    double getZValue(const QPointF& pt) const {
//        if(qAbs(equation.z()) < 1.e-6)
//            return 100000000;
        double z = (-equation.w() - equation.x() * pt.x() - equation.y() * pt.y()) / equation.z();
        return z;
    }
    
    double getZValue(const QVector3D& linePoint, const QVector3D &lineVector) const {
        QVector3D planeVector(equation);
        QVector3D planePoint(ptList.at(0));
        float vp1, vp2, vp3, n1, n2, n3, v1, v2, v3, m1, m2, m3, t,vpt;
        vp1 = planeVector[0];
        vp2 = planeVector[1];
        vp3 = planeVector[2];
        n1 = planePoint[0];
        n2 = planePoint[1];
        n3 = planePoint[2];
        v1 = lineVector[0];
        v2 = lineVector[1];
        v3 = lineVector[2];
        m1 = linePoint[0];
        m2 = linePoint[1];  
        m3 = linePoint[2];  
        vpt = v1 * vp1 + v2 * vp2 + v3 * vp3;
        if(qAbs(vpt) < 1.e-6)
            return 100000000;
        else {
            t = ((n1 - m1) * vp1 + (n2 - m2) * vp2 + (n3 - m3) * vp3) / vpt;
            return m3 + v3 * t;
        }
    }
    QColor getColor() const {
        return color;
    }
    void display() const {
        qDebug () << ptList;
    }

};


class Widget : public QWidget
{
    Q_OBJECT
    QVector<Patch3D> patchList;
 
    QPoint startPt;
    QPoint endPt;
    QVector3D eye;
    QVector3D viewDirection;
    float nearPlane;
    void perspective(const QVector3D &pos, const QVector3D& direction, const float nearPlane){
        QPolygonF polygon;
        for(Patch3D& p : patchList){
            p.translate(QVector3D(-pos.x(), -pos.y(), -pos.z() - nearPlane));
        }
        direction.normalized();
        if(qAbs(direction.x()) < 1.e-6) {
            double angle = qRadiansToDegrees(qAcos(direction.z() / direction.length()));
            if(direction.y() > 0)
                angle = -angle;
            for(Patch3D& p : patchList) {
                p.rotate(-angle,QVector3D(1.f, 0.f, 0.f));
            }
            QMatrix4x4 temp;
            temp.rotate(-angle, QVector3D(1.f,0.f,0.f));
            viewDirection = temp.map(direction);
            eye = temp.map(pos);
        }
        else if (qAbs(direction.y()) < 1.e-6) {
            double angle = qRadiansToDegrees(qAcos(direction.z() / direction.length()));
            if(direction.x() < 0 )
                angle = -angle;
            for(Patch3D& p : patchList) {
                p.rotate(-angle,QVector3D(0.f, 1.f, 0.f));
            }
            QMatrix4x4 temp;
            temp.rotate(-angle, QVector3D(0.f,1.f,0.f));
            viewDirection = temp.map(direction);
            eye = temp.map(pos);
        }
        else {
            double angle = qAcos(direction.z() / direction.length());
            if(direction.x() < 0 )
                angle = -angle;
            if(QVector2D(direction.x(), direction.y()).length() > 1e-6){
                double theta = -qRadiansToDegrees(qAsin(direction.y() / QVector2D(direction.x(), direction.y()).length()));
                if(direction.y() < 0)
                    theta = -theta;
                for(Patch3D p : patchList) {
                    p.rotate(theta, QVector3D(0.f,0.f,1.f));
                }
                QMatrix4x4 temp;
                temp.rotate(theta, QVector3D(0.f,0.f,1.f));
                viewDirection = temp.map(direction);
                eye = temp.map(pos);
            }
            for(Patch3D& p : patchList) {
                p.rotate(-qRadiansToDegrees(angle),QVector3D(0.f, 1.f, 0.f));
            }
            QMatrix4x4 temp;
            temp.rotate(-qRadiansToDegrees(angle), QVector3D(0.f,1.f,0.f));
            viewDirection = temp.map(direction);
            eye = temp.map(pos);
        }
//
//        
//        double angle = qAcos(direction.z() / direction.length());
//        if(direction.x() < 0 )
//            angle = -angle;
//        if(QVector2D(direction.x(), direction.y()).length() > 1e-6){
//            double theta = -qRadiansToDegrees(qAsin(direction.y() / QVector2D(direction.x(), direction.y()).length()));
//            if(direction.y() < 0)
//                theta = -theta;
//            for(Patch3D p : patchList) {
//                p.rotate(theta, QVector3D(0.f,0.f,1.f));
//            }
//            QMatrix4x4 temp;
//            temp.rotate(theta, QVector3D(0.f,0.f,1.f));
//            eye = temp.map(pos);
//        }
//        if(qAbs(direction.x()) > 1.e-6){
//            for(Patch3D& p : patchList) {
//                p.rotate(-qRadiansToDegrees(angle),QVector3D(0.f, 1.f, 0.f));
//            }
//            QMatrix4x4 temp;
//            temp.rotate(-qRadiansToDegrees(angle), QVector3D(0.f,1.f,0.f));
//            eye = temp.map(pos);
//        }
//        else{
//            if(direction.y() > 0)
//                angle = -angle;
//            for(Patch3D& p : patchList) {
//                //p.display();
//                p.rotate(-qRadiansToDegrees(angle),QVector3D(1.f, 0.f, 0.f));
//                //p.display();
//            }
//            QMatrix4x4 temp;
//            temp.rotate(-qRadiansToDegrees(angle), QVector3D(1.f,0.f,0.f));
//            eye = temp.map(pos);
//        }
//        this->nearPlane = -pos.z() + nearPlane;
//        for(Patch3D& p : patchList){
//            p.translate(QVector3D(0, 0, -this->nearPlane));
//        }
        
        
        
    }
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
