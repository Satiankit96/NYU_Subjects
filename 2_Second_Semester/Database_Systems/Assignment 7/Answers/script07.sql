-- Replace zk1 with your NetID

-- execute data setup script
@dataSetupScript07.sql

spool spool07.txt

PROMPT as1428
/*

YOU START YOUR ANSWERS AFTER THE END OF THIS COMMENT BLOCK

ANSWER0 is an example to show you the structure of what you need to produce, unless instructed otherwise in the assignment. Please read carefully what you are asked to do.

Insert your solution for ANSWERX between the string -- ANSWERX and the string PROMPT ANSWERX

You DO NOT have to run the "spool on" comand yourself. This script file does that for you.

The spool07.txt file generated when you run this script will be a part of your submission.

*/

-- Answer 0 represents a sample quert that uses the temp table TEMP0 just to demonstrate the usage of temp table.
-- ANSWER0
CREATE TABLE TEMP0
AS SELECT *
FROM DOG;

CREATE TABLE ANSWER0
AS SELECT DISTINCT Breed
FROM TEMP0
ORDER BY Breed DESC;


PROMPT ANSWER0
SELECT * FROM ANSWER0;


-- ANSWER1


CREATE TABLE TEMP8
AS SELECT * 
FROM DOG
WHERE DOG.Price != 0;

CREATE TABLE ANSWER1
AS SELECT DISTINCT STUDENT.NNumber, TEMP8.SerialNumber, (STUDENT.Deposit / TEMP8.Price) AS DepositPriceRatio 
FROM STUDENT, TEMP8
WHERE (STUDENT.NNumber = TEMP8.NNumber) AND (STUDENT.Deposit / TEMP8.Price > 2) AND (STUDENT.NetID IS NULL)
ORDER BY STUDENT.NNumber ASC, TEMP8.SerialNumber ASC, DepositPriceRatio ASC;
PROMPT ANSWER1
SELECT * FROM ANSWER1;

-- ANSWER2


CREATE TABLE TEMP1
AS SELECT DISTINCT STUDENT.NNumber, count(*) AS Cnt 
FROM STUDENT, DOG
WHERE STUDENT.NNumber = DOG.NNumber
GROUP BY STUDENT.NNumber;

CREATE TABLE ANSWER2
AS SELECT DISTINCT STUDENT.NNumber
FROM STUDENT, TEMP1
WHERE ( TEMP1.NNumber = STUDENT.NNumber AND TEMP1.Cnt >= 3 ) AND ( STUDENT.Deposit >= 100 ) 
ORDER BY STUDENT.NNumber ASC;

PROMPT ANSWER2
SELECT * FROM ANSWER2;

-- ANSWER3

CREATE TABLE TEMP2
AS SELECT DISTINCT DOG.Breed
FROM DOG
WHERE DOG.NNumber = 2;

CREATE TABLE TEMP3
AS SELECT DISTINCT DOG.NNumber, TEMP2.Breed
FROM DOG, TEMP2;

CREATE TABLE TEMP4
AS SELECT DISTINCT *
FROM TEMP3
MINUS
SELECT NNumber, Breed
FROM DOG;

CREATE TABLE ANSWER3
AS SELECT DISTINCT NNumber FROM DOG
WHERE NNumber <> 2
MINUS
SELECT DISTINCT NNumber FROM TEMP4
ORDER BY NNumber ASC;
PROMPT ANSWER3
SELECT * FROM ANSWER3;

-- ANSWER4

DELETE FROM STUDENT WHERE STUDENT.NNumber = 7;
PROMPT ANSWER4
SELECT * FROM ANSWER4;

-- ANSWER5

UPDATE STUDENT SET STUDENT.Deposit = STUDENT.Deposit - 5
WHERE STUDENT.NNumber = 6;

DELETE FROM DOG WHERE DOG.SerialNumber = 2005;
PROMPT ANSWER5
SELECT * FROM STUDENT;

-- ANSWER6

UPDATE DOG 
SET DOG.Breed = 'Poodle'
WHERE DOG.SerialNumber = 3;
PROMPT ANSWER6
SELECT * FROM STUDENT;
SELECT * FROM DOG;

-- ANSWER7


INSERT INTO STUDENT (NNumber, Deposit, NetID) VALUES ('2502', 50, NULL);
PROMPT ANSWER7
SELECT * FROM DOG;

-- ANSWER8

INSERT INTO STUDENT (NNumber, Deposit, NetID) VALUES ('2503', 50, 'aa1');
PROMPT ANSWER8
SELECT * FROM STUDENT;

-- ANSWER9
PROMPT ANSWER9
/*
Write your solution below. No need to create table like previous questions. Just SELECT ..
*/
SELECT DISTINCT A, B
FROM R
LEFT OUTER JOIN S
ON R.A = S.C AND R.B = S.D
WHERE C IS NULL AND D IS NULL
ORDER BY A ASC, B ASC;