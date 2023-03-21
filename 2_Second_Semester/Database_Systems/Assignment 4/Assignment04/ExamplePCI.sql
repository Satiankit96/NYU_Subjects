drop table INVOICE CASCADE CONSTRAINTS;

drop table CUSTOMER CASCADE CONSTRAINTS;

drop table PLANT CASCADE CONSTRAINTS;

create table INVOICE (
	I# INTEGER not null,
	AMT INTEGER null,
	IDATE DATE null,
	C# INTEGER not null, constraint INVOICE_PK primary key (I#) );

create table CUSTOMER (
	C# INTEGER not null,
	CNAME VARCHAR2(10) null,
	CITY VARCHAR2(10) null,
	P# INTEGER null, constraint CUSTOMER_PK primary key (C#) );

create table PLANT (
	P# INTEGER not null,
	PNAME VARCHAR2(10) null,
	CITY VARCHAR2(10) null,
	MARGIN FLOAT null, constraint PLANT_PK primary key (P#) );





Insert into Plant values(1,'alpha','Boston',null);
Insert into Plant values(2,'alpha','Boston',0.2);
Insert into Plant values(3,'beta','New York',0.5);

Insert into Customer(C#,Cname,P#) values(11,'a',1);
Insert into Customer values(12,null,null,null);

Insert into Invoice values (21,50,'12-JAN-2009',11);

alter table INVOICE
	add constraint CUSTOMER_INVOICE_FK1 foreign key (
		C#)
	 references CUSTOMER (
		C#);

alter table CUSTOMER
	add constraint PLANT_CUSTOMER_FK1 foreign key (
		P#)
	 references PLANT (
		P#);

-- Print description of the database


desc user_tables;

desc table_name from user_tables;

select table_name, constraint_name, constraint_type from user_constraints;

desc Plant;

desc Customer;

desc Invoice;

-- Print the database

select * from Plant;

select * from Customer;

select * from Invoice;



-- Run a trivial query

drop TABLE Temp0 CASCADE CONSTRAINTS;

create Table Temp0 AS
select I#, Plant.City
from Plant, Customer, Invoice
where Plant.P# = Customer.P# and Customer.C# = Invoice.C#;

select * from Temp0;
