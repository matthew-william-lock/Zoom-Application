import mysql.connector

mydb = mysql.connector.connect (
	host= "localhost",
	user= "admin",
	passwd= "password",
	database= "zoom_app"
)

mycursor = mydb.cursor()

#insert data example
sql = "INSERT INTO user_details (id, username, ID_number, Full_name, hashed_password, city) VALUES (%s,%s,%s,%s,%s,%s)"
val = [

	("3", "user4", "00000003", "User Four", "abcd6789", "Cape Town"),
	("4", "user5", "00000004", "User Five", "abcd6789", "Johannesburg"),
	("5", "user6", "00000005", "User Six", "abcd6789", "Durban")
	]

mycursor.executemany(sql, val)
mydb.commit()

print(mycursor.rowcount, " records inserted.")

#read data example
sql = "SELECT * FROM user_details"
mycursor.execute(sql)
myresult = mycursor.fetchall()

for x in myresult:
	print(x)