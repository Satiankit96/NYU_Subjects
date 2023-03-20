from db.utils import DatabaseHelper

connection = DatabaseHelper.get_connection()

with open('db/schema.sql') as f:
    connection.executescript(f.read())

connection.commit()
connection.close()
