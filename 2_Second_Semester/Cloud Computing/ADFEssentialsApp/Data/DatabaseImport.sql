CREATE DATABASE  IF NOT EXISTS `archemy` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `archemy`;
-- MySQL dump 10.13  Distrib 5.7.12, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: archemy
-- ------------------------------------------------------
-- Server version	5.7.13-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `areas`
--

DROP TABLE IF EXISTS `areas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `areas` (
  `AREA_ID` int(11) NOT NULL AUTO_INCREMENT,
  `AREA_PARENT_ID` int(11) DEFAULT NULL,
  `AREA_ORDER_NO` varchar(45) DEFAULT NULL,
  `AREA_DEPTH_LEVEL` int(11) DEFAULT NULL,
  `DIMENSION_ID` int(11) DEFAULT NULL,
  `AREA_NAME` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`AREA_ID`),
  KEY `DIMENSION_FK_idx` (`DIMENSION_ID`),
  KEY `AREA_ID_AREA_PARENT_FK_idx` (`AREA_PARENT_ID`),
  CONSTRAINT `AREA_DIMENSION_ID_FK` FOREIGN KEY (`DIMENSION_ID`) REFERENCES `dimensions` (`DIMENSION_ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `AREA_ID_AREA_PARENT_FK` FOREIGN KEY (`AREA_PARENT_ID`) REFERENCES `areas` (`AREA_ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=32110 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `areas`
--

LOCK TABLES `areas` WRITE;
/*!40000 ALTER TABLE `areas` DISABLE KEYS */;
INSERT INTO `areas` VALUES (1,NULL,'20',0,1,'Business Area'),(2,NULL,'100',0,1,'Functional Requirement'),(3,NULL,'180',0,1,'Non-Functional Requirements'),(4,NULL,'540',0,2,'Hybrid'),(5,NULL,'560',0,2,'Structural'),(6,NULL,'620',0,2,'Transformational'),(7,NULL,'700',0,3,'Business'),(8,NULL,'720',0,3,'Application'),(9,NULL,'740',0,3,'Information'),(10,NULL,'760',0,3,'Infrastructure'),(11,NULL,'780',0,3,'Master (Cross-Domain)'),(12,NULL,'800',0,3,'Hybrid / Views'),(13,NULL,'820',0,4,'Division'),(14,NULL,'840',0,4,'Enterprise'),(15,NULL,'860',0,4,'Partial'),(16,NULL,'880',0,4,'Project'),(17,NULL,'900',0,5,'Partial'),(18,NULL,'920',0,5,'Complete'),(19,NULL,'940',0,6,'Candidate'),(20,NULL,'960',0,6,'Best Practice'),(21,NULL,'980',0,7,'Ideation'),(22,NULL,'1040',0,7,'Inception'),(23,NULL,'1100',0,7,'Elaboration (High-Level Design)'),(24,NULL,'1200',0,7,'Design Elaboration (Detail-Level Design)'),(25,NULL,'1220',0,7,'Implementation Prototype'),(26,NULL,'1240',0,7,'Implementation'),(27,NULL,'1260',0,7,'Product'),(28,NULL,'1280',0,7,'Reference Architecture'),(29,NULL,'1300',0,8,'Framework'),(30,NULL,'1320',0,8,'Solution'),(31,NULL,'1340',0,8,'Layer'),(32,NULL,'1360',0,8,'Capability'),(33,NULL,'1820',0,8,'Sub-Capability'),(34,NULL,'1880',0,8,'Abstract Pattern'),(35,NULL,'1940',0,8,'Generic Pattern'),(36,NULL,'2020',0,8,'Implementation Pattern (Stack Specific)'),(37,NULL,'2100',0,9,'System of'),(38,NULL,'2200',0,9,'Industry'),(39,NULL,'2560',0,9,'Technology Service '),(40,NULL,'2820',0,9,'Technology Facility'),(41,NULL,'2960',0,9,'Horizontal - System Management Facility'),(42,NULL,'3120',0,9,'Horizontal - Task Management Common Facility'),(43,NULL,'3340',0,9,'Vertical - Industry Specific Facilities'),(44,NULL,'3360',0,10,'Emerging'),(45,NULL,'3380',0,10,'Adolescent'),(46,NULL,'3400',0,10,'Early Mainstream '),(47,NULL,'3420',0,10,'Mainstream'),(48,NULL,'3440',0,11,'Sustaining'),(49,NULL,'3460',0,11,'Enhancing'),(50,NULL,'3480',0,11,'Breakthrough'),(51,NULL,'3500',0,11,'Research/Invention'),(52,NULL,'3520',0,12,'Input'),(53,NULL,'3540',0,12,'Process'),(54,NULL,'3560',0,12,'Output'),(101,1,'40',1,1,'Input'),(102,1,'60',1,1,'Output'),(103,1,'80',1,1,'Process'),(201,2,'120',1,1,'BUC'),(202,2,'140',1,1,'Business Goal'),(203,2,'160',1,1,'Task'),(301,3,'200',1,1,'External-Economic Constraints'),(302,3,'220',1,1,'External-Ethical Requirements'),(303,3,'240',1,1,'External-Interoperability Requirements'),(304,3,'260',1,1,'External-Legislative Constraints '),(305,3,'280',1,1,'Process-Acceptance'),(306,3,'300',1,1,'Process-Documentation'),(307,3,'320',1,1,'Process-Operational'),(308,3,'340',1,1,'Process-Verification'),(309,3,'360',1,1,'Product-Interface'),(310,3,'380',1,1,'Product-Maintainability'),(311,3,'400',1,1,'Product-Performance'),(312,3,'420',1,1,'Product-Portability'),(313,3,'440',1,1,'Product-Quality (Usability, Capacity, Efficiency, Scalability, Plasticity, Versatility)'),(314,3,'460',1,1,'Product-Reliability'),(315,3,'480',1,1,'Product-Resource'),(316,3,'500',1,1,'Product-Safety'),(317,3,'520',1,1,'Product-Security'),(501,5,'580',1,2,'Abstract'),(502,5,'600',1,2,'Concrete'),(601,6,'640',1,2,'Methodology'),(602,6,'660',1,2,'Approach'),(603,6,'680',1,2,'Tools & Templates'),(2101,21,'1000',1,7,'Business Model Canvas stage'),(2102,21,'1020',1,7,'Innovation Management stage '),(2201,22,'1060',1,7,'Requirements Engineering Level'),(2202,22,'1080',1,7,'Requirements Modeling Level'),(2301,23,'1120',1,7,'Presentation Level'),(2302,23,'1140',1,7,'Conceptual Level'),(2303,23,'1160',1,7,'Logical Level'),(2304,23,'1180',1,7,'Physical Level'),(3201,32,'1380',1,8,'Business Capability'),(3202,3201,'1400',2,8,'Corporate-Accounting'),(3203,3201,'1420',2,8,'Corporate-Communications'),(3204,3201,'1440',2,8,'Corporate-Finance'),(3205,3201,'1460',2,8,'Corporate-HR'),(3206,3201,'1480',2,8,'Corporate-IT'),(3207,3201,'1500',2,8,'Corporate-Legal'),(3208,3201,'1520',2,8,'Corporate-RE/Facilities Mgt'),(3209,3201,'1540',2,8,'Manufacturing'),(3210,3201,'1560',2,8,'Marketing'),(3211,3201,'1580',2,8,'Multiple'),(3212,3201,'1600',2,8,'Purchasing/Procurement'),(3213,3201,'1620',2,8,'Sales'),(3301,33,'1840',1,8,'Business Sub-Capability'),(3302,33,'1860',1,8,'Technology Sub-Capability'),(3401,34,'1900',1,8,'Reference Element'),(3402,34,'1920',1,8,'Reference Style'),(3501,35,'1960',1,8,'Architectural Pattern'),(3502,35,'1980',1,8,'Architectural Style'),(3503,35,'2000',1,8,'Design Pattern'),(3601,36,'2040',1,8,'Idiom'),(3602,36,'2060',1,8,'Implementation Pattern'),(3603,36,'2080',1,8,'Implementation Style'),(3701,37,'2120',1,9,'Design'),(3702,37,'2140',1,9,'Engagement'),(3703,37,'2160',1,9,'Operation/Automation'),(3704,37,'2180',1,9,'Record'),(3801,38,'2220',1,9,'Any Industry'),(3802,38,'2240',1,9,'Banking & Financial Services'),(3803,38,'2260',1,9,'Communications'),(3804,38,'2280',1,9,'Consulting'),(3805,38,'2300',1,9,'Consumer Goods'),(3806,38,'2320',1,9,'Education'),(3807,38,'2340',1,9,'Energy & Utilities'),(3808,38,'2360',1,9,'Healthcare'),(3809,38,'2380',1,9,'Information Services'),(3810,38,'2400',1,9,'Insurance'),(3811,38,'2420',1,9,'Life Sciences'),(3812,38,'2440',1,9,'Manufacturing'),(3813,38,'2460',1,9,'Media & Entertainment'),(3814,38,'2480',1,9,'Retail'),(3815,38,'2500',1,9,'Technology'),(3816,38,'2520',1,9,'Transportation & Logistics'),(3817,38,'2540',1,9,'Travel & Hospitality'),(3901,39,'2580',1,9,'Event'),(3902,39,'2600',1,9,'Externalization'),(3903,39,'2620',1,9,'Integration'),(3904,39,'2640',1,9,'Licensing'),(3905,39,'2660',1,9,'Messaging'),(3906,39,'2680',1,9,'Naming'),(3907,39,'2700',1,9,'Persistence'),(3909,39,'2740',1,9,'Query'),(3910,39,'2760',1,9,'Security'),(3911,39,'2780',1,9,'Trading'),(3912,39,'2800',1,9,'Transaction'),(4001,40,'2840',1,9,'Horizontal - Information Mgt Common Facility'),(4002,40,'2860',1,9,'Data Encoding'),(4003,40,'2880',1,9,'Data Interchange'),(4004,40,'2900',1,9,'Information Interchange'),(4005,40,'2920',1,9,'Information Modeling'),(4006,40,'2940',1,9,'Information Storage and Retrieval'),(4101,41,'2980',1,9,'Customization'),(4102,41,'3000',1,9,'Data Collection'),(4103,41,'3020',1,9,'Instrumentation'),(4104,41,'3040',1,9,'Policy'),(4105,41,'3060',1,9,'QoS'),(4106,41,'3080',1,9,'Scheduling'),(4107,41,'3100',1,9,'Security'),(4201,42,'3140',1,9,'Agent'),(4202,42,'3160',1,9,'Automation'),(4203,42,'3180',1,9,'Rule Management'),(4204,42,'3200',1,9,'Workflow'),(4205,42,'3220',1,9,'Horizontal - UI Common Facility'),(4206,42,'3240',1,9,'Presentation'),(4207,42,'3260',1,9,'Scripting'),(4208,42,'3280',1,9,'User Support'),(4209,42,'3300',1,9,'UI Common Facility'),(4210,42,'3320',1,9,'Presentation'),(32101,32,'1640',1,8,'Technology Capability'),(32102,32101,'1660',2,8,'AI, Human-Computer Interaction'),(32103,32101,'1680',2,8,'Big Data'),(32104,32101,'1700',2,8,'Cloud'),(32105,32101,'1720',2,8,'Enterprise Analytics (includes Big Data)'),(32106,32101,'1740',2,8,'IoM'),(32107,32101,'1760',2,8,'IoT'),(32108,32101,'1780',2,8,'Mobile'),(32109,32101,'1800',2,8,'Social ');
/*!40000 ALTER TABLE `areas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cells`
--

DROP TABLE IF EXISTS `cells`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cells` (
  `CELL_ID` int(11) NOT NULL AUTO_INCREMENT,
  `AREA_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`CELL_ID`),
  KEY `CELL_AREA_ID_FK_idx` (`AREA_ID`),
  CONSTRAINT `CELL_AREA_ID_FK` FOREIGN KEY (`AREA_ID`) REFERENCES `areas` (`AREA_ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cells`
--

LOCK TABLES `cells` WRITE;
/*!40000 ALTER TABLE `cells` DISABLE KEYS */;
/*!40000 ALTER TABLE `cells` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_info`
--

DROP TABLE IF EXISTS `customer_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `customer_info` (
  `CUSTOMER_NAME` varchar(150) DEFAULT NULL,
  `INDUSTRY` varchar(150) DEFAULT NULL,
  `USER_ID` varchar(150) NOT NULL,
  PRIMARY KEY (`USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer_info`
--

LOCK TABLES `customer_info` WRITE;
/*!40000 ALTER TABLE `customer_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `customer_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dajoin`
--

DROP TABLE IF EXISTS `dajoin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dajoin` (
  `dimension_name` varchar(45) DEFAULT NULL,
  `dimension_id` int(11) NOT NULL DEFAULT '0',
  `area_name` varchar(100) DEFAULT NULL,
  `area_id` int(11) NOT NULL DEFAULT '0',
  `AREA_PARENT_ID` int(11) DEFAULT NULL,
  `area_depth_level` int(11) DEFAULT NULL,
  `area_order_no` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dajoin`
--

LOCK TABLES `dajoin` WRITE;
/*!40000 ALTER TABLE `dajoin` DISABLE KEYS */;
INSERT INTO `dajoin` VALUES ('Area of Applicability',9,'System of',37,NULL,0,'2100'),('Area of Applicability',9,'Industry',38,NULL,0,'2200'),('Area of Applicability',9,'Technology Service ',39,NULL,0,'2560'),('Area of Applicability',9,'Technology Facility',40,NULL,0,'2820'),('Area of Applicability',9,'Horizontal - System Management Facility',41,NULL,0,'2960'),('Area of Applicability',9,'Horizontal - Task Management Common Facility',42,NULL,0,'3120'),('Area of Applicability',9,'Vertical - Industry Specific Facilities',43,NULL,0,'3340'),('Area of Applicability',9,'Design',3701,37,1,'2120'),('Area of Applicability',9,'Engagement',3702,37,1,'2140'),('Area of Applicability',9,'Operation/Automation',3703,37,1,'2160'),('Area of Applicability',9,'Record',3704,37,1,'2180'),('Area of Applicability',9,'Any Industry',3801,38,1,'2220'),('Area of Applicability',9,'Banking & Financial Services',3802,38,1,'2240'),('Area of Applicability',9,'Communications',3803,38,1,'2260'),('Area of Applicability',9,'Consulting',3804,38,1,'2280'),('Area of Applicability',9,'Consumer Goods',3805,38,1,'2300'),('Area of Applicability',9,'Education',3806,38,1,'2320'),('Area of Applicability',9,'Energy & Utilities',3807,38,1,'2340'),('Area of Applicability',9,'Healthcare',3808,38,1,'2360'),('Area of Applicability',9,'Information Services',3809,38,1,'2380'),('Area of Applicability',9,'Insurance',3810,38,1,'2400'),('Area of Applicability',9,'Life Sciences',3811,38,1,'2420'),('Area of Applicability',9,'Manufacturing',3812,38,1,'2440'),('Area of Applicability',9,'Media & Entertainment',3813,38,1,'2460'),('Area of Applicability',9,'Retail',3814,38,1,'2480'),('Area of Applicability',9,'Technology',3815,38,1,'2500'),('Area of Applicability',9,'Transportation & Logistics',3816,38,1,'2520'),('Area of Applicability',9,'Travel & Hospitality',3817,38,1,'2540'),('Area of Applicability',9,'Event',3901,39,1,'2580'),('Area of Applicability',9,'Externalization',3902,39,1,'2600'),('Area of Applicability',9,'Integration',3903,39,1,'2620'),('Area of Applicability',9,'Licensing',3904,39,1,'2640'),('Area of Applicability',9,'Messaging',3905,39,1,'2660'),('Area of Applicability',9,'Naming',3906,39,1,'2680'),('Area of Applicability',9,'Persistence',3907,39,1,'2700'),('Area of Applicability',9,'Query',3909,39,1,'2740'),('Area of Applicability',9,'Security',3910,39,1,'2760'),('Area of Applicability',9,'Trading',3911,39,1,'2780'),('Area of Applicability',9,'Transaction',3912,39,1,'2800'),('Area of Applicability',9,'Horizontal - Information Mgt Common Facility',4001,40,1,'2840'),('Area of Applicability',9,'Data Encoding',4002,40,1,'2860'),('Area of Applicability',9,'Data Interchange',4003,40,1,'2880'),('Area of Applicability',9,'Information Interchange',4004,40,1,'2900'),('Area of Applicability',9,'Information Modeling',4005,40,1,'2920'),('Area of Applicability',9,'Information Storage and Retrieval',4006,40,1,'2940'),('Area of Applicability',9,'Customization',4101,41,1,'2980'),('Area of Applicability',9,'Data Collection',4102,41,1,'3000'),('Area of Applicability',9,'Instrumentation',4103,41,1,'3020'),('Area of Applicability',9,'Policy',4104,41,1,'3040'),('Area of Applicability',9,'QoS',4105,41,1,'3060'),('Area of Applicability',9,'Scheduling',4106,41,1,'3080'),('Area of Applicability',9,'Security',4107,41,1,'3100'),('Area of Applicability',9,'Agent',4201,42,1,'3140'),('Area of Applicability',9,'Automation',4202,42,1,'3160'),('Area of Applicability',9,'Rule Management',4203,42,1,'3180'),('Area of Applicability',9,'Workflow',4204,42,1,'3200'),('Area of Applicability',9,'Horizontal - UI Common Facility',4205,42,1,'3220'),('Area of Applicability',9,'Presentation',4206,42,1,'3240'),('Area of Applicability',9,'Scripting',4207,42,1,'3260'),('Area of Applicability',9,'User Support',4208,42,1,'3280'),('Area of Applicability',9,'UI Common Facility',4209,42,1,'3300'),('Area of Applicability',9,'Presentation',4210,42,1,'3320'),('Business Force',1,'Business Area',1,NULL,0,'20'),('Business Force',1,'Functional Requirement',2,NULL,0,'100'),('Business Force',1,'Non-Functional Requirements',3,NULL,0,'180'),('Business Force',1,'Input',101,1,1,'40'),('Business Force',1,'Output',102,1,1,'60'),('Business Force',1,'Process',103,1,1,'80'),('Business Force',1,'BUC',201,2,1,'120'),('Business Force',1,'Business Goal',202,2,1,'140'),('Business Force',1,'Task',203,2,1,'160'),('Business Force',1,'External-Economic Constraints',301,3,1,'200'),('Business Force',1,'External-Ethical Requirements',302,3,1,'220'),('Business Force',1,'External-Interoperability Requirements',303,3,1,'240'),('Business Force',1,'External-Legislative Constraints ',304,3,1,'260'),('Business Force',1,'Process-Acceptance',305,3,1,'280'),('Business Force',1,'Process-Documentation',306,3,1,'300'),('Business Force',1,'Process-Operational',307,3,1,'320'),('Business Force',1,'Process-Verification',308,3,1,'340'),('Business Force',1,'Product-Interface',309,3,1,'360'),('Business Force',1,'Product-Maintainability',310,3,1,'380'),('Business Force',1,'Product-Performance',311,3,1,'400'),('Business Force',1,'Product-Portability',312,3,1,'420'),('Business Force',1,'Product-Quality (Usability, Capacity, Efficiency, Scalability, Plasticity, Versatility)',313,3,1,'440'),('Business Force',1,'Product-Reliability',314,3,1,'460'),('Business Force',1,'Product-Resource',315,3,1,'480'),('Business Force',1,'Product-Safety',316,3,1,'500'),('Business Force',1,'Product-Security',317,3,1,'520'),('Completeness Level',5,'Partial',17,NULL,0,'900'),('Completeness Level',5,'Complete',18,NULL,0,'920'),('Coverage Domain',3,'Business',7,NULL,0,'700'),('Coverage Domain',3,'Application',8,NULL,0,'720'),('Coverage Domain',3,'Information',9,NULL,0,'740'),('Coverage Domain',3,'Infrastructure',10,NULL,0,'760'),('Coverage Domain',3,'Master (Cross-Domain)',11,NULL,0,'780'),('Coverage Domain',3,'Hybrid / Views',12,NULL,0,'800'),('Degree of Innovation',10,'Emerging',44,NULL,0,'3360'),('Degree of Innovation',10,'Adolescent',45,NULL,0,'3380'),('Degree of Innovation',10,'Early Mainstream ',46,NULL,0,'3400'),('Degree of Innovation',10,'Mainstream',47,NULL,0,'3420'),('Degree of Standardization',6,'Candidate',19,NULL,0,'940'),('Degree of Standardization',6,'Best Practice',20,NULL,0,'960'),('Granularity',8,'Framework',29,NULL,0,'1300'),('Granularity',8,'Solution',30,NULL,0,'1320'),('Granularity',8,'Layer',31,NULL,0,'1340'),('Granularity',8,'Capability',32,NULL,0,'1360'),('Granularity',8,'Sub-Capability',33,NULL,0,'1820'),('Granularity',8,'Abstract Pattern',34,NULL,0,'1880'),('Granularity',8,'Generic Pattern',35,NULL,0,'1940'),('Granularity',8,'Implementation Pattern (Stack Specific)',36,NULL,0,'2020'),('Granularity',8,'Business Capability',3201,32,1,'1380'),('Granularity',8,'Corporate-Accounting',3202,3201,2,'1400'),('Granularity',8,'Corporate-Communications',3203,3201,2,'1420'),('Granularity',8,'Corporate-Finance',3204,3201,2,'1440'),('Granularity',8,'Corporate-HR',3205,3201,2,'1460'),('Granularity',8,'Corporate-IT',3206,3201,2,'1480'),('Granularity',8,'Corporate-Legal',3207,3201,2,'1500'),('Granularity',8,'Corporate-RE/Facilities Mgt',3208,3201,2,'1520'),('Granularity',8,'Manufacturing',3209,3201,2,'1540'),('Granularity',8,'Marketing',3210,3201,2,'1560'),('Granularity',8,'Multiple',3211,3201,2,'1580'),('Granularity',8,'Purchasing/Procurement',3212,3201,2,'1600'),('Granularity',8,'Sales',3213,3201,2,'1620'),('Granularity',8,'Business Sub-Capability',3301,33,1,'1840'),('Granularity',8,'Technology Sub-Capability',3302,33,1,'1860'),('Granularity',8,'Reference Element',3401,34,1,'1900'),('Granularity',8,'Reference Style',3402,34,1,'1920'),('Granularity',8,'Architectural Pattern',3501,35,1,'1960'),('Granularity',8,'Architectural Style',3502,35,1,'1980'),('Granularity',8,'Design Pattern',3503,35,1,'2000'),('Granularity',8,'Idiom',3601,36,1,'2040'),('Granularity',8,'Implementation Pattern',3602,36,1,'2060'),('Granularity',8,'Implementation Style',3603,36,1,'2080'),('Granularity',8,'Technology Capability',32101,32,1,'1640'),('Granularity',8,'AI, Human-Computer Interaction',32102,32101,2,'1660'),('Granularity',8,'Big Data',32103,32101,2,'1680'),('Granularity',8,'Cloud',32104,32101,2,'1700'),('Granularity',8,'Enterprise Analytics (includes Big Data)',32105,32101,2,'1720'),('Granularity',8,'IoM',32106,32101,2,'1740'),('Granularity',8,'IoT',32107,32101,2,'1760'),('Granularity',8,'Mobile',32108,32101,2,'1780'),('Granularity',8,'Social ',32109,32101,2,'1800'),('Innovation Area',12,'Input',52,NULL,0,'3520'),('Innovation Area',12,'Process',53,NULL,0,'3540'),('Innovation Area',12,'Output',54,NULL,0,'3560'),('Innovation Extent',11,'Sustaining',48,NULL,0,'3440'),('Innovation Extent',11,'Enhancing',49,NULL,0,'3460'),('Innovation Extent',11,'Breakthrough',50,NULL,0,'3480'),('Innovation Extent',11,'Research/Invention',51,NULL,0,'3500'),('Maturity',7,'Ideation',21,NULL,0,'980'),('Maturity',7,'Inception',22,NULL,0,'1040'),('Maturity',7,'Elaboration (High-Level Design)',23,NULL,0,'1100'),('Maturity',7,'Design Elaboration (Detail-Level Design)',24,NULL,0,'1200'),('Maturity',7,'Implementation Prototype',25,NULL,0,'1220'),('Maturity',7,'Implementation',26,NULL,0,'1240'),('Maturity',7,'Product',27,NULL,0,'1260'),('Maturity',7,'Reference Architecture',28,NULL,0,'1280'),('Maturity',7,'Business Model Canvas stage',2101,21,1,'1000'),('Maturity',7,'Innovation Management stage ',2102,21,1,'1020'),('Maturity',7,'Requirements Engineering Level',2201,22,1,'1060'),('Maturity',7,'Requirements Modeling Level',2202,22,1,'1080'),('Maturity',7,'Presentation Level',2301,23,1,'1120'),('Maturity',7,'Conceptual Level',2302,23,1,'1140'),('Maturity',7,'Logical Level',2303,23,1,'1160'),('Maturity',7,'Physical Level',2304,23,1,'1180'),('Scope',4,'Division',13,NULL,0,'820'),('Scope',4,'Enterprise',14,NULL,0,'840'),('Scope',4,'Partial',15,NULL,0,'860'),('Scope',4,'Project',16,NULL,0,'880'),('Type',2,'Hybrid',4,NULL,0,'540'),('Type',2,'Structural',5,NULL,0,'560'),('Type',2,'Transformational',6,NULL,0,'620'),('Type',2,'Abstract',501,5,1,'580'),('Type',2,'Concrete',502,5,1,'600'),('Type',2,'Methodology',601,6,1,'640'),('Type',2,'Approach',602,6,1,'660'),('Type',2,'Tools & Templates',603,6,1,'680');
/*!40000 ALTER TABLE `dajoin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dimensions`
--

DROP TABLE IF EXISTS `dimensions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dimensions` (
  `DIMENSION_ID` int(11) NOT NULL AUTO_INCREMENT,
  `DIMENSION_NAME` varchar(45) DEFAULT NULL,
  `DOMAIN_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`DIMENSION_ID`),
  UNIQUE KEY `DIMENSIONS_NAME_UQ_idx` (`DIMENSION_NAME`,`DOMAIN_ID`),
  KEY `DOMAIN_ID_FK_idx` (`DOMAIN_ID`),
  CONSTRAINT `DIMENSION_DOMAIN_ID_FK` FOREIGN KEY (`DOMAIN_ID`) REFERENCES `domains` (`DOMAIN_ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dimensions`
--

LOCK TABLES `dimensions` WRITE;
/*!40000 ALTER TABLE `dimensions` DISABLE KEYS */;
INSERT INTO `dimensions` VALUES (9,'Area of Applicability',1),(1,'Business Force',1),(5,'Completeness Level',1),(3,'Coverage Domain',1),(10,'Degree of Innovation',1),(6,'Degree of Standardization',1),(8,'Granularity',1),(12,'Innovation Area',1),(11,'Innovation Extent',1),(7,'Maturity',1),(4,'Scope',1),(2,'Type',1);
/*!40000 ALTER TABLE `dimensions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `domains`
--

DROP TABLE IF EXISTS `domains`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `domains` (
  `DOMAIN_ID` int(11) NOT NULL AUTO_INCREMENT,
  `DOMAIN_NAME` varchar(45) NOT NULL COMMENT 'Contains Domain Description',
  `DOMAIN_DESCRIPTION` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`DOMAIN_ID`),
  UNIQUE KEY `DOMAIN_NAME_UNIQUE` (`DOMAIN_NAME`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `domains`
--

LOCK TABLES `domains` WRITE;
/*!40000 ALTER TABLE `domains` DISABLE KEYS */;
INSERT INTO `domains` VALUES (1,'Business Solutions','Business Solutions'),(2,'HealthTech',NULL),(3,'SocialMedia',NULL),(4,'DataIntegration',NULL),(5,'WebsiteGalleryMgt',NULL);
/*!40000 ALTER TABLE `domains` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iad_relationships`
--

DROP TABLE IF EXISTS `iad_relationships`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `iad_relationships` (
  `FROM_IAD_TO_CELL_ASSIGNMENT_ID` int(11) NOT NULL,
  `TO_IAD_TO_CELL_ASSIGNMENT_ID` int(11) NOT NULL,
  `RELATIONSHIP_TYPE_ID` int(11) NOT NULL,
  `IAD_RELATIONSHIP_ID` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`IAD_RELATIONSHIP_ID`),
  KEY `IAD_CELL_ASSGN_IAD_ID_idx` (`FROM_IAD_TO_CELL_ASSIGNMENT_ID`),
  KEY `IAD_CELL_ASSGN_CELL_ID_idx` (`TO_IAD_TO_CELL_ASSIGNMENT_ID`),
  KEY `IAD_RLTN_RELATION_ID_FK_idx` (`RELATIONSHIP_TYPE_ID`),
  CONSTRAINT `IAD_RLTN_IAD_CELL_CELL_ID_FK` FOREIGN KEY (`TO_IAD_TO_CELL_ASSIGNMENT_ID`) REFERENCES `iad_to_cell_assignments` (`CELL_ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `IAD_RLTN_IAD_CELL_IAD_ID_FK` FOREIGN KEY (`FROM_IAD_TO_CELL_ASSIGNMENT_ID`) REFERENCES `iad_to_cell_assignments` (`IAD_ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `IAD_RLTN_RELATION_ID_FK` FOREIGN KEY (`RELATIONSHIP_TYPE_ID`) REFERENCES `relationship_types` (`RELATIONSHIP_ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iad_relationships`
--

LOCK TABLES `iad_relationships` WRITE;
/*!40000 ALTER TABLE `iad_relationships` DISABLE KEYS */;
/*!40000 ALTER TABLE `iad_relationships` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iad_to_cell_assignments`
--

DROP TABLE IF EXISTS `iad_to_cell_assignments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `iad_to_cell_assignments` (
  `IAD_CELL_ASSIGNMENT_ID` int(11) NOT NULL AUTO_INCREMENT,
  `IAD_ID` int(11) DEFAULT NULL,
  `CELL_ID` int(11) DEFAULT NULL,
  `AREA_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`IAD_CELL_ASSIGNMENT_ID`),
  KEY `CELL_ASSGN_IAD_ID_FK_idx` (`IAD_ID`),
  KEY `CELL_ASSGN_CELL_ID_FK_idx` (`CELL_ID`),
  CONSTRAINT `CELL_ASSGN_CELL_ID_FK` FOREIGN KEY (`CELL_ID`) REFERENCES `cells` (`CELL_ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `CELL_ASSGN_IAD_ID_FK` FOREIGN KEY (`IAD_ID`) REFERENCES `iads` (`IAD_ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iad_to_cell_assignments`
--

LOCK TABLES `iad_to_cell_assignments` WRITE;
/*!40000 ALTER TABLE `iad_to_cell_assignments` DISABLE KEYS */;
/*!40000 ALTER TABLE `iad_to_cell_assignments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iad_to_node_assignments`
--

DROP TABLE IF EXISTS `iad_to_node_assignments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `iad_to_node_assignments` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `IAD_NODE_ASSIGN_NODE_ID` int(11) DEFAULT NULL,
  `IAD_NODE_ASSIGN_IAD_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `NODE_ASSGN_TO_IAD_ID_idx` (`IAD_NODE_ASSIGN_IAD_ID`),
  CONSTRAINT `NODE_ASSGN_TO_IAD_ID` FOREIGN KEY (`IAD_NODE_ASSIGN_IAD_ID`) REFERENCES `iads` (`IAD_ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `NODE_ASSGN_TO_NODE_ID` FOREIGN KEY (`ID`) REFERENCES `nodes` (`NODE_ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iad_to_node_assignments`
--

LOCK TABLES `iad_to_node_assignments` WRITE;
/*!40000 ALTER TABLE `iad_to_node_assignments` DISABLE KEYS */;
/*!40000 ALTER TABLE `iad_to_node_assignments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iads`
--

DROP TABLE IF EXISTS `iads`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `iads` (
  `IAD_ID` int(11) NOT NULL AUTO_INCREMENT,
  `IAD_NAME` varchar(100) DEFAULT NULL,
  `IAD_TYPE` varchar(100) NOT NULL,
  `DOMAIN_ID` int(11) DEFAULT NULL,
  `KAD_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`IAD_ID`),
  UNIQUE KEY `IAD_NAME_UNIQUE` (`IAD_NAME`,`DOMAIN_ID`,`KAD_ID`),
  KEY `KAD_DOMAIN_ID_FK_idx` (`DOMAIN_ID`),
  KEY `IAD_KAD_ID_FK_idx` (`KAD_ID`),
  CONSTRAINT `IAD_DOMAIN_ID_FK` FOREIGN KEY (`DOMAIN_ID`) REFERENCES `domains` (`DOMAIN_ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `IAD_KAD_ID_FK` FOREIGN KEY (`KAD_ID`) REFERENCES `kads` (`KAD_ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iads`
--

LOCK TABLES `iads` WRITE;
/*!40000 ALTER TABLE `iads` DISABLE KEYS */;
/*!40000 ALTER TABLE `iads` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kad_dimensions_area`
--

DROP TABLE IF EXISTS `kad_dimensions_area`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `kad_dimensions_area` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `DIMENSION_ID` int(11) NOT NULL,
  `KAD_ID` int(11) NOT NULL,
  `AREA_ID` int(11) DEFAULT NULL,
  `AREA_PARENT_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `KAD_UNQ_IDX` (`DIMENSION_ID`,`KAD_ID`,`AREA_ID`,`AREA_PARENT_ID`),
  KEY `KAD_DIMENSION_ID_FK_idx` (`DIMENSION_ID`),
  KEY `KAD_DIMENSIONS_KAD_ID_FK_idx` (`KAD_ID`),
  KEY `KAD_DIM_AREA_idx` (`AREA_ID`),
  CONSTRAINT `KAD_DIM_AREA` FOREIGN KEY (`AREA_ID`) REFERENCES `areas` (`AREA_ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `KAD_DIM_ID_FK` FOREIGN KEY (`DIMENSION_ID`) REFERENCES `dimensions` (`DIMENSION_ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `KAD_DIM_KAD_ID_FK` FOREIGN KEY (`KAD_ID`) REFERENCES `kads` (`KAD_ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=134 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kad_dimensions_area`
--

LOCK TABLES `kad_dimensions_area` WRITE;
/*!40000 ALTER TABLE `kad_dimensions_area` DISABLE KEYS */;
INSERT INTO `kad_dimensions_area` VALUES (1,2,1,5,0),(57,2,4,5,0),(84,2,5,4,0),(110,2,6,5,0),(4,3,1,9,0),(5,3,1,11,0),(6,3,1,12,0),(41,3,3,9,0),(42,3,3,11,0),(61,3,4,7,0),(62,3,4,8,0),(63,3,4,12,0),(86,3,5,7,0),(87,3,5,9,0),(88,3,5,12,0),(111,3,6,9,0),(130,3,10,NULL,9),(131,3,13,NULL,9),(7,4,1,13,0),(8,4,1,14,0),(43,4,3,15,0),(64,4,4,15,0),(89,4,5,14,0),(129,4,10,NULL,15),(9,5,1,18,0),(44,5,3,17,0),(65,5,4,17,0),(90,5,5,17,0),(112,5,6,17,0),(10,6,1,19,0),(45,6,3,19,0),(66,6,4,19,0),(91,6,5,19,0),(113,6,6,19,0),(11,7,1,23,0),(12,7,1,26,0),(46,7,3,25,0),(67,7,4,25,0),(92,7,5,25,0),(114,7,6,25,0),(132,7,13,2201,22),(13,8,1,30,0),(47,8,3,30,0),(68,8,4,29,0),(93,8,5,30,0),(115,8,6,30,0),(133,8,13,NULL,30),(25,10,1,46,0),(35,10,2,45,0),(54,10,3,46,0),(80,10,4,45,0),(106,10,5,46,0),(124,10,6,46,0),(26,11,1,49,0),(36,11,2,49,0),(55,11,3,49,0),(81,11,4,51,0),(125,11,6,49,0),(27,12,1,53,0),(37,12,2,53,0),(28,12,2,54,0),(38,12,3,54,0),(56,12,4,53,0),(107,12,5,52,0),(108,12,5,53,0),(109,12,5,54,0),(126,12,6,54,0),(128,12,10,NULL,53);
/*!40000 ALTER TABLE `kad_dimensions_area` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kad_registration`
--

DROP TABLE IF EXISTS `kad_registration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `kad_registration` (
  `USER_ID` varchar(150) NOT NULL,
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `KAD_ID` int(11) NOT NULL,
  `MATURITY_RATING` int(11) NOT NULL,
  `DEPLOYMENT_STATUS` varchar(100) NOT NULL,
  `APPLICABILITY_EXTENT` varchar(100) NOT NULL,
  `BENEFIT_RATING` int(11) NOT NULL,
  `COMMENTS` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `KAD_REGISTER_UK_IDX` (`USER_ID`,`KAD_ID`),
  KEY `KAD_REGISTER_TO_KAD_FK_idx` (`KAD_ID`),
  CONSTRAINT `KAD_REGISTER_TO_KAD_FK` FOREIGN KEY (`KAD_ID`) REFERENCES `kads` (`KAD_ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kad_registration`
--

LOCK TABLES `kad_registration` WRITE;
/*!40000 ALTER TABLE `kad_registration` DISABLE KEYS */;
/*!40000 ALTER TABLE `kad_registration` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kad_to_node_assignment`
--

DROP TABLE IF EXISTS `kad_to_node_assignment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `kad_to_node_assignment` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `KAD_ID` int(11) DEFAULT NULL,
  `NODE_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `KAD_NODE_ASGN_KAD_FK_idx` (`KAD_ID`),
  KEY `KAD_NODE_ASGN_NODE_ID_FK_idx` (`NODE_ID`),
  CONSTRAINT `KAD_NODE_ASGN_KAD_FK` FOREIGN KEY (`KAD_ID`) REFERENCES `kads` (`KAD_ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `KAD_NODE_ASGN_NODE_ID_FK` FOREIGN KEY (`NODE_ID`) REFERENCES `nodes` (`NODE_ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kad_to_node_assignment`
--

LOCK TABLES `kad_to_node_assignment` WRITE;
/*!40000 ALTER TABLE `kad_to_node_assignment` DISABLE KEYS */;
/*!40000 ALTER TABLE `kad_to_node_assignment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kads`
--

DROP TABLE IF EXISTS `kads`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `kads` (
  `KAD_ID` int(11) NOT NULL AUTO_INCREMENT,
  `KAD_NAME` varchar(100) DEFAULT NULL,
  `DOMAIN_ID` int(11) DEFAULT NULL,
  `KAD_LINK` varchar(300) DEFAULT NULL,
  `KAD_LINK_PUBLIC` varchar(300) DEFAULT NULL,
  `KAD_HIT_COUNTER` int(11) DEFAULT '0',
  `RECURRING_BUS_PROBLEM_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`KAD_ID`),
  UNIQUE KEY `KAD_NAME_UNIQUE` (`KAD_NAME`),
  KEY `DOMAIN_ID_KAD_FK_idx` (`DOMAIN_ID`),
  KEY `KAD_RECURRING_BUS_FK_idx` (`RECURRING_BUS_PROBLEM_ID`),
  CONSTRAINT `KAD_DOMAIN_ID_FK` FOREIGN KEY (`DOMAIN_ID`) REFERENCES `domains` (`DOMAIN_ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `KAD_RECURRING_BUS_FK` FOREIGN KEY (`RECURRING_BUS_PROBLEM_ID`) REFERENCES `recurring_bus_problem` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kads`
--

LOCK TABLES `kads` WRITE;
/*!40000 ALTER TABLE `kads` DISABLE KEYS */;
INSERT INTO `kads` VALUES (1,'Dynamic Multimedia Site Generator',1,'https://archemy.com/static/docs/showcase/NCDBSolutionSummary.pdf','https://archemy.com/static/docs/showcase/NCDBSolutionSummary.pdf',0,1),(2,'Hetergeneous Data Management Platform',1,'https://archemy.com/static/docs/showcase/HDMP.pdf','https://archemy.com/static/docs/showcase/HDMP.pdf',0,4),(3,'Biobanking Semantics Management Platform',1,'https://archemy.com/static/docs/showcase/BiobankKADSummary.pdf','https://archemy.com/static/docs/showcase/BiobankKADSummary.pdf',0,2),(4,'EAM as a Cloud-Based Service',1,'https://archemy.com/static/docs/showcase/EAMaaS.pdf','https://archemy.com/static/docs/showcase/EAMaaS.pdf',0,6),(5,'Real-Time Social Sentiment Analysis',1,'https://archemy.com/static/docs/showcase/ESRTSA.pdf','https://archemy.com/static/docs/showcase/ESRTSA.pdf',0,3),(6,'Health Metering Platform',1,'https://archemy.com/static/docs/showcase/ArchNav.pdf','https://archemy.com/static/docs/showcase/ArchNav.pdf',0,5),(10,'NCDB',1,'https://archemy.com/static/docs/showcase/NCDBSolutionSummary.pdf','https://archemy.com/static/docs/showcase/NCDBSolutionSummary.pdf',2,6),(13,'testjca',1,'http://www.archemy.com','http://www.archemy.com',0,1);
/*!40000 ALTER TABLE `kads` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `node_relationships`
--

DROP TABLE IF EXISTS `node_relationships`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `node_relationships` (
  `FROM_NODE_ID` int(11) DEFAULT NULL,
  `TO_NODE_ID` int(11) DEFAULT NULL,
  `NODE_RELATIONSHIPS_ID` int(11) NOT NULL AUTO_INCREMENT,
  `RELATIONSHIP_TYPE_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`NODE_RELATIONSHIPS_ID`),
  KEY `NODE_RLTN_NODE_NODE_ID_FK_idx` (`FROM_NODE_ID`),
  KEY `NODE_RLTN_NODE_ID_FK_2_idx` (`TO_NODE_ID`),
  KEY `NODE_RLTN_RLTN_TYPE_ID_idx` (`RELATIONSHIP_TYPE_ID`),
  CONSTRAINT `NODE_RLTN_NODE_ID_FK` FOREIGN KEY (`FROM_NODE_ID`) REFERENCES `nodes` (`NODE_ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `NODE_RLTN_NODE_ID_FK_2` FOREIGN KEY (`TO_NODE_ID`) REFERENCES `nodes` (`NODE_ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `NODE_RLTN_RLTN_TYPE_ID` FOREIGN KEY (`RELATIONSHIP_TYPE_ID`) REFERENCES `relationship_types` (`RELATIONSHIP_ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `node_relationships`
--

LOCK TABLES `node_relationships` WRITE;
/*!40000 ALTER TABLE `node_relationships` DISABLE KEYS */;
/*!40000 ALTER TABLE `node_relationships` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nodes`
--

DROP TABLE IF EXISTS `nodes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `nodes` (
  `NODE_ID` int(11) NOT NULL AUTO_INCREMENT,
  `NODE_NAME` varchar(100) NOT NULL,
  `ORGANIZATION_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`NODE_ID`),
  KEY `NODE_TO_ORGANIZATION_ID_idx` (`ORGANIZATION_ID`),
  CONSTRAINT `NODE_TO_ORGANIZATION_ID` FOREIGN KEY (`ORGANIZATION_ID`) REFERENCES `organizations` (`ORGANIZATION_ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nodes`
--

LOCK TABLES `nodes` WRITE;
/*!40000 ALTER TABLE `nodes` DISABLE KEYS */;
/*!40000 ALTER TABLE `nodes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `organization_types`
--

DROP TABLE IF EXISTS `organization_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `organization_types` (
  `ORGANIZATION_TYPE_ID` int(11) NOT NULL AUTO_INCREMENT,
  `ORGANIZATION_TYPE_NAME` varchar(150) DEFAULT NULL,
  `ORGANIZATION_TYPE_DESCRIPTION` varchar(300) DEFAULT NULL,
  `DOMAIN_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ORGANIZATION_TYPE_ID`),
  UNIQUE KEY `ORGANIZATION_TYPE_NAME_UNIQUE` (`ORGANIZATION_TYPE_NAME`,`DOMAIN_ID`),
  KEY `ORG_TYPE_TO_DOMAIN_ID_FK_idx` (`DOMAIN_ID`),
  CONSTRAINT `ORG_TYPE_TO_DOMAIN_ID_FK` FOREIGN KEY (`DOMAIN_ID`) REFERENCES `domains` (`DOMAIN_ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `organization_types`
--

LOCK TABLES `organization_types` WRITE;
/*!40000 ALTER TABLE `organization_types` DISABLE KEYS */;
/*!40000 ALTER TABLE `organization_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `organizations`
--

DROP TABLE IF EXISTS `organizations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `organizations` (
  `ORGANIZATION_ID` int(11) NOT NULL AUTO_INCREMENT,
  `ORGANIZATION_NAME` varchar(100) DEFAULT NULL,
  `DOMAIN_ID` int(11) DEFAULT NULL,
  `ORGANIZATION_TYPE_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ORGANIZATION_ID`),
  UNIQUE KEY `ORGANIZATION_NAME_UNIQUE` (`ORGANIZATION_NAME`,`DOMAIN_ID`),
  KEY `ORGANIZATION_TO_DOMAIN_ID_FK_idx` (`DOMAIN_ID`),
  KEY `ORGANIZATIONS_TO_ORG_TYPE_idx` (`ORGANIZATION_TYPE_ID`),
  CONSTRAINT `ORGANIZATIONS_TO_ORG_TYPE` FOREIGN KEY (`ORGANIZATION_TYPE_ID`) REFERENCES `organization_types` (`ORGANIZATION_TYPE_ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ORGANIZATION_TO_DOMAIN_ID_FK` FOREIGN KEY (`DOMAIN_ID`) REFERENCES `domains` (`DOMAIN_ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `organizations`
--

LOCK TABLES `organizations` WRITE;
/*!40000 ALTER TABLE `organizations` DISABLE KEYS */;
/*!40000 ALTER TABLE `organizations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `pcg`
--

DROP TABLE IF EXISTS `pcg`;
/*!50001 DROP VIEW IF EXISTS `pcg`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `pcg` AS SELECT 
 1 AS `dimension_name`,
 1 AS `Parent`,
 1 AS `Child`,
 1 AS `Grandchild`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `recurring_bus_problem`
--

DROP TABLE IF EXISTS `recurring_bus_problem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recurring_bus_problem` (
  `BUSINESS_PROBLEM` varchar(500) NOT NULL,
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `CONTEXT` varchar(80) DEFAULT NULL,
  `TYPE` varchar(80) DEFAULT NULL,
  `DESCRIPTION` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recurring_bus_problem`
--

LOCK TABLES `recurring_bus_problem` WRITE;
/*!40000 ALTER TABLE `recurring_bus_problem` DISABLE KEYS */;
INSERT INTO `recurring_bus_problem` VALUES ('Taxonomy Management',1,'Generic','Recurring','Taxonomy Management'),('Healthcare Taxonomy Management',2,'Industry','Recurring','Healthcare Taxonomy'),('Social Sentiment Analysis',3,'Generic','Recurring','Sentiment Analysis'),('Heterogeneous Data Integration',4,'Generic','Recurring','Data Management'),('Multidimensional Search',5,'Generic','Recurring','Dynamic Taxonomy-driven Search'),('Expert Service Resource  Management',6,'Generic','Recurring','AI/Cloud-based Expert Service Delivery');
/*!40000 ALTER TABLE `recurring_bus_problem` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `relationship_types`
--

DROP TABLE IF EXISTS `relationship_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `relationship_types` (
  `RELATIONSHIP_ID` int(11) NOT NULL AUTO_INCREMENT,
  `RELATIONSHIP_TYPE_NAME` varchar(100) DEFAULT NULL,
  `RELATIONSHIP_TYPE_DESC` varchar(300) DEFAULT NULL,
  `DOMAIN_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`RELATIONSHIP_ID`),
  UNIQUE KEY `RELATIONSHIP_TYPE_NAME_UNIQUE` (`RELATIONSHIP_TYPE_NAME`,`DOMAIN_ID`),
  KEY `RELATIONSHIP_TO_DOMAIN_ID_FK_idx` (`DOMAIN_ID`),
  CONSTRAINT `RELATIONSHIP_TO_DOMAIN_ID_FK` FOREIGN KEY (`DOMAIN_ID`) REFERENCES `domains` (`DOMAIN_ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `relationship_types`
--

LOCK TABLES `relationship_types` WRITE;
/*!40000 ALTER TABLE `relationship_types` DISABLE KEYS */;
/*!40000 ALTER TABLE `relationship_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'archemy'
--

--
-- Dumping routines for database 'archemy'
--
/*!50003 DROP PROCEDURE IF EXISTS `insert_into_kad` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `insert_into_kad`(
IN kad_name varchar(100), 
IN kad_link varchar(300),
IN kad_public_link varchar(300),
IN domain_id int(11), 
IN business_problem Integer,
OUT kad_id int(11))
BEGIN
insert into kads(kad_name,domain_id,kad_link,kad_link_public,RECURRING_BUS_PROBLEM_ID) 
values (kad_name,domain_id,kad_link,kad_public_link,business_problem);
select last_insert_id() into kad_id;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `insert_into_kad_dim_area` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `insert_into_kad_dim_area`(
IN in_kad_id Integer, 
IN in_area_id Integer,
IN in_area_child_id Integer,
IN in_dimension_id Integer
)
BEGIN
insert into kad_dimensions_area(kad_id,dimension_id,area_id,area_parent_id) 
values (in_kad_id,in_dimension_id,in_area_child_id,in_area_id);
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Final view structure for view `pcg`
--

/*!50001 DROP VIEW IF EXISTS `pcg`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `pcg` AS select `d`.`DIMENSION_NAME` AS `dimension_name`,`a`.`AREA_NAME` AS `Parent`,`c`.`AREA_NAME` AS `Child`,`g`.`AREA_NAME` AS `Grandchild` from (((`dimensions` `d` left join `areas` `a` on(((`a`.`DIMENSION_ID` = `d`.`DIMENSION_ID`) and (`a`.`AREA_DEPTH_LEVEL` = 0)))) left join `areas` `c` on(((`c`.`AREA_PARENT_ID` = `a`.`AREA_ID`) and (`c`.`AREA_DEPTH_LEVEL` = 1)))) left join `areas` `g` on(((`g`.`AREA_PARENT_ID` = `c`.`AREA_ID`) and (`g`.`AREA_DEPTH_LEVEL` = 2)))) order by `d`.`DIMENSION_ID`,`a`.`AREA_ORDER_NO`,`c`.`AREA_ORDER_NO`,`g`.`AREA_ORDER_NO` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-07-26 12:31:46
