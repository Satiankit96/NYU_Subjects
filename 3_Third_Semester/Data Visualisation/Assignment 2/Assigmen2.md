Assignment 2 										as14128
Data Wrangling with Python 

Part 1 
Steps taken:
1.	Correcting Age column: Some of the values in the age column were numerical, while others included the word "years" alongside the number. To simply maintain the number portion, I eliminated them. 
2.	Splitting Appropriate columns: Both numbers were noted in the Blood pressure column. For easier usability, we divided them on the slash (/).
3.	Handling Missing Values: There were a few missing values in the Height and Weight column. We drop those rows. 
4.	Dropping irrelevant columns: The old blood pressure column had no use after splitting so we dropped it. 
5.	Data Type Correction: We changed the data type of some columns appropriately

Part 2 – Data Set is attached
Steps taken:
1.	Irrelevant Columns: Since no metadata was specified, some columns, such as Item1, Item2,... etc., and nameless columns, had to be removed.
2.	Handling Missing Values: Some columns like Children, Age, Income, etc which had missing values in rows where dropped. 
3.	Correcting Binary Valued Columns: Many indicators had Yes and No. I replaced all such that every indicator had 1 or 0. 
4.	Rounding off Column values: Some columns needed to be rounded off. Total charge and Additional charges were rounded off to 2 decimal places. 
5.	Data Types Correction: I changed the data type of certain columns for better usability.
