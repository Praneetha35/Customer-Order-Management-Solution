Assignment-4

Developed By:Praneetha Chandra Prakash


Description:
------------------------------------------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------------------------------------------

This program is a console-based application for managing customer orders using a MySQL database. 
It allows users to perform various operations such as adding a customer, adding an order, removing an order, shipping an order, printing pending orders, and more.


Prerequisites:
------------------------------------------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------------------------------------------
Java Development Kit (JDK) version 11 or higher
MySQL Connector/J
MySQL server

Files Included:
------------------------------------------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------------------------------------------

Main.java:Contains the main logic and functionality of the application, including the menu system, database operations, and user interactions.

mySQLConnector.java: Handles the connection to the MySQL database and provides utility methods for executing SQL queries and updates.

mysql-connector-j-8.3.0.jar: The MySQL Connector/J library for Java, which enables communication with the MySQL database.

Usage Instructions:
------------------------------------------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------------------------------------------

1. Compile the Code:

   javac Main.java

2. Run the Program:

   java -cp .:mysql-connector-j-8.3.0.jar Main

Ensure all files are kept in the same location for proper execution.


Menu Options:
------------------------------------------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------------------------------------------

The application presents a menu of options for managing orders. 

The available options are:

1. Add a Customer: Add a new customer to the database.
2. Add an Order: Place a new order for a customer.
3. Remove an Order: Delete an existing order from the database.
4. Ship an Order: Mark an order as shipped and update shipping details.
5. Print Pending Orders: Display a list of pending orders along with customer information.
6. More Options: Access additional features 
7. Exit: Close the program and database connection.

Extra Features:

The program includes an additional menu option "6. More Options", which allows the user to access extra features such as:

i)Generate Invoice: Create an invoice for a specific order.
ii)Update Order Information: Modify details of an existing order.
iii)View Customers, Employees, and Shippers: This feature allows managers or administrators to view and access information about the customers, employees, and shippers associated with the business. 
iv)Return to Main Menu: Go back to the main menu.


