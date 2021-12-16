/*
SQLyog Community v13.1.6 (64 bit)
MySQL - 5.7.14 : Database - web
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`web` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `web`;

/*Table structure for table `book` */

DROP TABLE IF EXISTS `book`;

CREATE TABLE `book` (
  `bookid` int(10) NOT NULL AUTO_INCREMENT,
  `bookname` varchar(20) NOT NULL,
  `author` varchar(20) NOT NULL,
  `price` float NOT NULL,
  `instruction` varchar(100) NOT NULL,
  PRIMARY KEY (`bookid`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

/*Data for the table `book` */

insert  into `book`(`bookid`,`bookname`,`author`,`price`,`instruction`) values 
(4,'英语','老师',10,'鼻血'),
(6,'爱吃啥','霍金2',100,'bjkbjk'),
(7,'ads','霍金4',30.002,'bjkbjk'),
(8,'ads','霍金4',30.002,'bjkbjk'),
(11,'爱吃啥','霍金2',100,'你好');

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` int(10) NOT NULL,
  `username` varchar(20) NOT NULL,
  `password` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `user` */

insert  into `user`(`id`,`username`,`password`) values 
(1,'alan','123456');

/*Table structure for table `user_book` */

DROP TABLE IF EXISTS `user_book`;

CREATE TABLE `user_book` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `userId` int(10) NOT NULL,
  `bookId` int(10) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

/*Data for the table `user_book` */

insert  into `user_book`(`id`,`userId`,`bookId`) values 
(1,1,6),
(2,1,5),
(3,1,8),
(4,1,7),
(5,1,11);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
