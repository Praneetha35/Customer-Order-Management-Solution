import java.sql.*;
import java.util.Scanner;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents a simple database application for managing customers and orders.
 */

public class Main {
  // Global scanner for user input	 
  public static Scanner input = new Scanner(System.in);

  // Database connector instance
  public static mySQLConnector table;

  /**
   * Main method to start the application.
   * @param args Command-line arguments (not used)a
   * @throws SQLException If an SQL exception occurs
   */
  public static void main(String[] args) throws SQLException {
    table = new mySQLConnector();
    selector();
  }

  /**
   * Displays the main menu and prompts the user to select an option.
   * @throws SQLException If an SQL exception occurs
   */
  private static void selector() throws SQLException {
  
    System.out.print("\n\nWelcome to Northwind Customer Order Management Solution!");
    while (true) {

      // Display main menu options
            
      System.out.print("\n\n-------------------------------------\nSelect an option from the menu below:\n-------------------------------------\n(1) add a customer  \n" +
        "(2) add an order  \n" + "(3) remove an order \n" + "(4) ship an order \n" +
        "(5) print pending orders (not shipped yet) with customer information \n" +
        "(6) more options\n" + "(7) exit  \n" + "\n-------------------------------------\n\nEnter the number corresponding to your choice: ");

      // Read user choice
      int choice = input.nextInt();
      input.nextLine();

      // Perform action based on user choice
      switch (choice) {
      case 1:
        addCustomer();
        break;
      case 2:
        addOrder();
        break;
      case 3:
        remtheOrder();
        break;
      case 4:
        shipTheOrder();
        break;
      case 5:
        printPendingOrder();
        break;
      case 6:
        moreOptions();
        break;
      case 7:
        exit();
        return;
      case 8:
        printProductQuantity();
        break;
      default:
        System.out.println("Not a valid choice\n\n");
        break;
      }
    }
  }

  /**
   * Displays additional options menu and prompts the user to select an option.
   * @throws SQLException If an SQL exception occurs
   */

  private static void moreOptions() throws SQLException {
    while (true) {

      // Display more options menu
      System.out.print("\n\n\n---------------------------\nMore Options:\n---------------------------\n" +
        "(1) generateInvoice\n" +
        "(2) updateOrderInformation\n" +
        "(3) View customers, employees, and shippers\n" +
        "(4) Return to Main Menu\n" +
        "\n ------------------------------\n\nEnter the number corresponding to your choice: ");

      // Read user choice
      int choice = input.nextInt();
      input.nextLine();

      switch (choice) {
      case 1:
        generateInvoice();
        break;
      case 2:
        updateOrderInformation();
        break;
      case 3:
        viewCustomersEmployeesShippers();
        break;
      case 4:
        return; // Return to the main menu
      default:
        System.out.println("Not a valid choice\n\n");
        break;
      }
    }
  }

  /**
   * Displays the list of customers, employees, and shippers.
   * @throws SQLException If an SQL exception occurs
   */
  private static void viewCustomersEmployeesShippers() throws SQLException {
    while (true) {
      // Display options to view different categories
      System.out.println("\n\n\n---------------------------\nView Options:\n---------------------------\n" +
        "(1) View Customers\n" +
        "(2) View Employees\n" +
        "(3) View Shippers\n" +
        "(4) Return to More Options\n" +
        "\n ------------------------------\n\nEnter the number corresponding to your choice: ");

      // Read user choice
      int choice = input.nextInt();
      input.nextLine();

      switch (choice) {
      case 1:
        viewCustomers(); // Display list of customers
        break;
      case 2:
        viewEmployees(); // Display list of employees
        break;
      case 3:
        viewShippers(); // Display list of shippers
        break;
      case 4:
        return; // Return to more options
      default:
        System.out.println("Not a valid choice\n\n"); // Inform user of invalid choice
        break;
      }
    }
  }

  /**
   * Starts a transaction by disabling auto-commit mode.
   * @throws SQLException If an SQL exception occurs
   */
  private static void startTransaction() throws SQLException {
    table.conn.setAutoCommit(false); // Disable auto-commit mode
  }

  /**
   * Commits the transaction and re-enables auto-commit mode.
   * @throws SQLException If an SQL exception occurs
   */
  private static void commitTransaction() throws SQLException {
    table.conn.commit(); // Commit the transaction
    table.conn.setAutoCommit(true); // Re-enable auto-commit mode
  }

  /**
   * Rolls back the transaction and re-enables auto-commit mode.
   */
  private static void rollbackTransaction() {
    try {
      table.conn.rollback(); // Roll back the transaction
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        table.conn.setAutoCommit(true); // Re-enable auto-commit mode
      } catch (SQLException ex) {
        ex.printStackTrace();
      }
    }
  }

  /**
   * Checks if the provided email address is in a valid format.
   * @param email The email address to validate
   * @return true if the email is valid, false otherwise
   */
  private static boolean isValidEmail(String email) {
    // Regular expression for email validation
    String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    Pattern pattern = Pattern.compile(emailRegex);
    Matcher matcher = pattern.matcher(email);
    return matcher.matches();
  }

  /**
   * Checks if the provided ZIP code is in a valid format (assumes 5-digit format).
   * @param zip The ZIP code to validate
   * @return true if the ZIP code is valid, false otherwise
   */
  private static boolean isValidZipCode(String zip) {
    // Regular expression for ZIP code validation
    String zipRegex = "^\\d{5}$";
    Pattern pattern = Pattern.compile(zipRegex);
    Matcher matcher = pattern.matcher(zip);
    return matcher.matches();
  }

  /**
   * Checks if the provided mobile number is in a valid format (assumes format: XXX-XXX-XXXX).
   * @param mobileNumber The mobile number to validate
   * @return true if the mobile number is valid, false otherwise
   */
  private static boolean isValidMobileNumber(String mobileNumber) {
    // Regular expression for mobile number validation
    String mobileRegex = "^\\d{3}-\\d{3}-\\d{4}$";
    Pattern pattern = Pattern.compile(mobileRegex);
    Matcher matcher = pattern.matcher(mobileNumber);
    return matcher.matches();
  }

  /**
   * Checks if a customer already exists in the database based on first name, last name, and email.
   * @param firstName The first name of the customer
   * @param lastName The last name of the customer
   * @param email The email of the customer
   * @return true if the customer exists, false otherwise
   * @throws SQLException If an SQL exception occurs
   */
  private static boolean customerExists(String firstName, String lastName, String email) throws SQLException {
    String query = "SELECT * FROM Customers WHERE FirstName = ? AND LastName = ? AND Email = ?";
    PreparedStatement stmt = table.conn.prepareStatement(query);
    stmt.setString(1, firstName);
    stmt.setString(2, lastName);
    stmt.setString(3, email);
    ResultSet rs = stmt.executeQuery();
    return rs.next();
  }

  /**
   * Retrieves the name and unit price of a product by its ID from the database.
   * @param productId The ID of the product to retrieve.
   * @return An array containing the product name at index 0 and unit price at index 1.
   */
  private static Object[] getProductById(int productId) throws SQLException {
    Object[] productInfo = new Object[2]; // Array to hold product name and unit price
    // Prepare and execute SQL query to retrieve product name and unit price by ID
    PreparedStatement productQuery = table.conn.prepareStatement("SELECT ProductName, ListPrice FROM Products WHERE ID = ?");
    productQuery.setInt(1, productId);
    ResultSet productResult = productQuery.executeQuery();
    // If a product with the given ID is found, retrieve its name and unit price
    if (productResult.next()) {
      productInfo[0] = productResult.getString("ProductName");
      productInfo[1] = productResult.getDouble("ListPrice");
    }
    return productInfo;
  }

  /**
   * Adds a new customer to the database.
   */
  private static void addCustomer() throws SQLException {
    try {
      startTransaction(); // Start a new transaction

      // Prompt user for customer information
      System.out.print("Enter customer's first name: ");
      String firstName = input.nextLine();

      System.out.print("Enter customer's last name: ");
      String lastName = input.nextLine();

      System.out.print("Enter customer's email ID: ");
      String email = input.nextLine();

      // Validate email format
      if (!isValidEmail(email)) {
        System.out.println("Invalid email format.");
        rollbackTransaction(); // Roll back the transaction
        return;
      }

      System.out.print("Enter street name: ");
      String address = input.nextLine();

      System.out.print("Enter city: ");
      String city = input.nextLine();

      System.out.print("Enter state: ");
      String state = input.nextLine();

      System.out.print("Enter zip code: ");
      String zip = input.nextLine();

      // Validate zip code format
      if (!isValidZipCode(zip)) {
        System.out.println("Invalid zip code format.");
        rollbackTransaction(); // Roll back the transaction
        return;
      }

      System.out.print("Enter country: ");
      String country = input.nextLine();

      System.out.print("Enter customer mobile number (_ _ _ -_ _ _ -_ _ _ _): ");
      String mobileNumber = input.nextLine();

      // Validate mobile number format
      if (!isValidMobileNumber(mobileNumber)) {
        System.out.println("Invalid mobile number format.");
        rollbackTransaction(); // Roll back the transaction
        return;
      }

      // Check if customer already exists
      if (customerExists(firstName, lastName, email)) {
        System.out.println("Customer already exists.");
        rollbackTransaction(); // Roll back the transaction
        return;
      }

      // Insert customer into database
      String insertCustomerQuery = "INSERT INTO Customers (FirstName, LastName, Email, Address, City, State, ZIP, Country, BusinessPhone) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
      PreparedStatement stmt = table.conn.prepareStatement(insertCustomerQuery);
      stmt.setString(1, firstName);
      stmt.setString(2, lastName);
      stmt.setString(3, email);
      stmt.setString(4, address);
      stmt.setString(5, city);
      stmt.setString(6, state);
      stmt.setString(7, zip);
      stmt.setString(8, country);
      stmt.setString(9, mobileNumber);
      stmt.executeUpdate();

      commitTransaction(); // Commit the transaction
      System.out.println("\nCustomer added successfully.\n\n\n");
    } catch (SQLException e) {
      rollbackTransaction(); // Roll back the transaction if an exception occurs
      System.out.println("Error while adding the customer:");
      e.printStackTrace();
    }
  }

  /**
   * Removes an order from the database.
   * @throws SQLException If an SQL exception occurs
   */
  private static void remtheOrder() throws SQLException {
    try {
      startTransaction(); // Start a new transaction

      System.out.print("Enter Order ID for deletion: ");
      int oid = input.nextInt();
      input.nextLine();

      try {
        // Delete order details associated with the order
        String command = "DELETE FROM Order_Details WHERE OrderID=" + oid + ";";
        // Delete order from the orders table
        String c1 = "DELETE FROM Orders WHERE OrderID=" + oid + ";";
        table.executeUpdate(command);
        table.executeUpdate(c1);
      } catch (Exception e) {
        // Ignore exceptions if order details deletion fails
      }

      // Delete order from the orders table
      String command = "DELETE FROM Orders WHERE OrderID=" + oid + ";";
      table.executeUpdate(command);

      commitTransaction(); // Commit the transaction
      System.out.print("\n---------------\n Order deleted\n\n");
    } catch (Exception e) {
      rollbackTransaction(); // Roll back the transaction if an exception occurs
      System.out.print("Order number does not match records\n\n");
    }
  }

  /**
   * Adds a new order to the database.
   * @throws SQLException If an SQL exception occurs
   */
  private static void addOrder() throws SQLException {
    try {
      startTransaction(); // Start a new transaction

      // Prompt user for customer ID
      System.out.print("Enter CustomerID: ");
      int customerId = input.nextInt();
      input.nextLine(); // Consume newline character

      // Check if the provided CustomerID exists in the Customers table
      ResultSet customerQuery = table.executeQuery("SELECT * FROM Customers WHERE ID = " + customerId);
      if (!customerQuery.next()) {
        System.out.println("Customer with ID " + customerId + " does not exist.");
        rollbackTransaction(); // Roll back the transaction
        return; // Return without proceeding to insert the order
      }

      // Prompt user for employee ID
      System.out.print("Enter EmployeeID: ");
      int employeeId = input.nextInt();
      input.nextLine(); // Consume newline character

      // Check if the provided EmployeeID exists in the Employees table
      ResultSet employeeQuery = table.executeQuery("SELECT * FROM Employees WHERE ID = " + employeeId);
      if (!employeeQuery.next()) {
        System.out.println("Employee with ID " + employeeId + " does not exist.");
        rollbackTransaction(); // Roll back the transaction
        return; // Return without proceeding to insert the order
      }

      // All required IDs exist, proceed with order insertion
      String command = "INSERT INTO Orders (EmployeeID, CustomerID, OrderDate, ShipName, ShipAddress) VALUES (";

      // Construct the SQL command for inserting the order
      command += employeeId + ", " + customerId + ", '";

      // Validate and prompt user for order date until a valid date is entered
      Pattern datePattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
      String orderDate;
      do {
        System.out.print("Enter OrderDate (yyyy-mm-dd): ");
        orderDate = input.nextLine();
        if (!datePattern.matcher(orderDate).matches()) {
          System.out.println("Invalid date format. Please enter the date in yyyy-mm-dd format.");
        }
      } while (!datePattern.matcher(orderDate).matches());
      command += orderDate + "', '";

      // Prompt user for ship name
      System.out.print("Enter ShipName: ");
      command += input.nextLine() + "', '";

      // Prompt user for shipping address
      System.out.print("Enter Shipping Address: ");
      command += input.nextLine() + "')";

      // Execute the SQL command to insert the order
      table.executeUpdate(command);

      // Get the last inserted OrderID
      ResultSet rs = table.executeQuery("SELECT LAST_INSERT_ID()");
      rs.next();
      int orderId = rs.getInt(1);

      boolean hasProducts = false;
      while (true) {
        // Prompt user for product ID
        System.out.print("Enter Product-ID (or 0 to finish): ");
        int productId = input.nextInt();
        input.nextLine(); // Consume newline character

        if (productId == 0) {
          break;
        }

        // Check if the provided ProductID exists in the Products table
        ResultSet productQuery = table.executeQuery("SELECT * FROM Products WHERE ID = " + productId);
        if (!productQuery.next()) {
          System.out.println("Product with ID " + productId + " does not exist.");
          continue;
        }

        // Check if the product is discontinued
        boolean discontinued = productQuery.getBoolean("Discontinued");
        if (discontinued) {
          System.out.println("Product with ID " + productId + " is discontinued and cannot be ordered.");
          continue;
        }

        // Prompt user for quantity
        System.out.print("Enter Quantity: ");
        int quantity = input.nextInt();
        input.nextLine(); // Consume newline character

        // Get the product's list price
        double listPrice = productQuery.getDouble("ListPrice");

        // Insert order details into the Order_Details table
        String orderDetailsCommand = "INSERT INTO Order_Details (OrderID, ProductID, Quantity, UnitPrice) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = table.conn.prepareStatement(orderDetailsCommand);
        stmt.setInt(1, orderId);
        stmt.setInt(2, productId);
        stmt.setInt(3, quantity);
        stmt.setDouble(4, listPrice);
        stmt.executeUpdate();

        hasProducts = true;
      }

      if (hasProducts) {
        commitTransaction(); // Commit the transaction
        System.out.println("\n---------------\nOrder Added\n\n");
      } else {
        rollbackTransaction(); // Roll back the transaction
        System.out.println("\n---------------\nNo products added to the order\n\n");
      }
    } catch (SQLException e) {
      rollbackTransaction(); // Roll back the transaction if an exception occurs
      System.out.println("Error while including the Order-Records:");
      e.printStackTrace();
    }
  }

  /**
   * Ships the specified order by updating its status and shipping details in the database.
   */
  private static void shipTheOrder() {
    try {
      startTransaction(); // Start a new transaction

      // Prompt user for the Order ID to ship
      System.out.print("Enter the Order ID to Ship: ");
      int orderId = input.nextInt();
      input.nextLine(); // Consume newline character

      // Check if the order exists
      PreparedStatement orderQuery = table.conn.prepareStatement("SELECT * FROM Orders WHERE OrderID = ?");
      orderQuery.setInt(1, orderId);
      ResultSet orderResult = orderQuery.executeQuery();
      if (!orderResult.next()) {
        System.out.println("Order with ID " + orderId + " does not exist.");
        rollbackTransaction(); // Roll back the transaction
        return;
      }

      // Check if the order has already been shipped
      Timestamp shippedDate = orderResult.getTimestamp("ShippedDate");
      if (shippedDate != null) {
        System.out.println("Order with ID " + orderId + " has already been shipped on " + shippedDate + ".");
        rollbackTransaction(); // Roll back the transaction
        return;
      }

      // Get the Shipper ID and Shipping Fee from the user
      System.out.print("Enter Shipper ID: ");
      int shipperId = input.nextInt();
      input.nextLine(); // Consume newline character
      System.out.print("Enter Shipping Fee: ");
      double shippingFee = input.nextDouble();
      input.nextLine(); // Consume newline character

      // Get products in the order and check available stock
      PreparedStatement orderDetailsQuery = table.conn.prepareStatement("SELECT * FROM Order_Details WHERE OrderID = ?");
      orderDetailsQuery.setInt(1, orderId);
      ResultSet orderDetailsResult = orderDetailsQuery.executeQuery();
      while (orderDetailsResult.next()) {
        int productId = orderDetailsResult.getInt("ProductID");
        double quantity = orderDetailsResult.getDouble("Quantity");

        // Check available stock for each product
        PreparedStatement inventoryQuery = table.conn.prepareStatement("SELECT (SUM(Quantity) - COALESCE((SELECT SUM(Quantity) FROM Inventory_Transactions WHERE ProductID = ? AND TransactionType = 2), 0)) AS AvailableQuantity FROM Inventory_Transactions WHERE ProductID = ?");
        inventoryQuery.setInt(1, productId);
        inventoryQuery.setInt(2, productId);
        ResultSet inventoryResult = inventoryQuery.executeQuery();
        if (inventoryResult.next()) {
          double availableQuantity = inventoryResult.getDouble("AvailableQuantity");
          if (availableQuantity < quantity) {
            System.out.println("Not enough units in stock for ProductID " + productId + ".");
            rollbackTransaction(); // Roll back the transaction
            return;
          }
        } else {
          System.out.println("Product with ID " + productId + " does not exist in inventory.");
          rollbackTransaction(); // Roll back the transaction
          return;
        }

        // Insert inventory transaction for sold items
        PreparedStatement inventoryInsertStmt = table.conn.prepareStatement("INSERT INTO Inventory_Transactions (TransactionType, TransactionCreatedDate, ProductID, Quantity, CustomerOrderID) VALUES (?, NOW(), ?, ?, ?)");
        inventoryInsertStmt.setInt(1, 2); // 2 for Sold
        inventoryInsertStmt.setInt(2, productId);
        inventoryInsertStmt.setDouble(3, -quantity); // Negative quantity for sold items
        inventoryInsertStmt.setInt(4, orderId);
        inventoryInsertStmt.executeUpdate();
      }

      // Update order with shipping details
      PreparedStatement updateOrderStmt = table.conn.prepareStatement("UPDATE Orders SET ShippedDate = NOW(), ShipperID = ?, ShippingFee = ? WHERE OrderID = ?");
      updateOrderStmt.setInt(1, shipperId);
      updateOrderStmt.setDouble(2, shippingFee);
      updateOrderStmt.setInt(3, orderId);
      updateOrderStmt.executeUpdate();

      commitTransaction(); // Commit the transaction
      System.out.println("\n---------------\nOrder with ID " + orderId + " has been shipped successfully.\n");

    } catch (SQLException e) {
      rollbackTransaction(); // Roll back the transaction if an exception occurs
      System.out.println("Error while shipping the order:");
      e.printStackTrace();
    }
  }

  /**
   * Prints a list of pending orders along with customer information.
   * @throws SQLException If an SQL exception occurs
   */
  private static void printPendingOrder() throws SQLException {
    // SQL query to retrieve pending orders and associated customer details
    String query = "SELECT Orders.OrderID, Orders.OrderDate, Customers.ID, Customers.FirstName, Customers.LastName, Customers.Email, Customers.Address, Customers.City, Customers.State, Customers.Country " +
      "FROM Orders " +
      "JOIN Customers ON Orders.CustomerID = Customers.ID " +
      "WHERE Orders.ShippedDate IS NULL " +
      "ORDER BY Orders.OrderDate;";

    // Execute the query
    ResultSet result = table.executeQuery(query);

    // Print header for pending orders list
    System.out.println("Pending Orders:");
    System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------");
    System.out.printf("%-10s %-20s %-20s %-20s %-20s %-40s %-20s %-20s %-20s %-20s\n", "OrderID", "OrderDate", "ID", "FirstName", "LastName", "Email", "Address", "City", "State", "Country");
    System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------");

    // Iterate through the result set and print each pending order
    while (result.next()) {
      int orderID = result.getInt("OrderID");
      String orderDate = result.getString("OrderDate");
      int customerID = result.getInt("ID");
      String firstName = result.getString("FirstName");
      String lastName = result.getString("LastName");
      String email = result.getString("Email");
      String address = result.getString("Address");
      String city = result.getString("City");
      String state = result.getString("State");
      String country = result.getString("Country");

      // Print each pending order and associated customer information
      System.out.printf("%-10s %-20s %-20s %-20s %-20s %-40s %-20s %-20s %-20s %-20s\n", orderID, orderDate, customerID, firstName, lastName, email, address, city, state, country);
    }

    // Print footer for pending orders list
    System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------");
    System.out.println("\nEnd of Pending Orders List...\n");
  }

  /**
   * Generates an invoice for a given order.
   */
  private static void generateInvoice() {
    try {
      // Prompt user to enter the Order ID
      System.out.print("Enter Order ID for which you want to generate an invoice: ");
      int orderId = input.nextInt();
      input.nextLine(); // Consume newline character

      // Retrieve order information from the database
      PreparedStatement orderQuery = table.conn.prepareStatement("SELECT * FROM Orders WHERE OrderID = ?");
      orderQuery.setInt(1, orderId);
      ResultSet orderResult = orderQuery.executeQuery();

      // Check if the order exists
      if (!orderResult.next()) {
        System.out.println("Order with ID " + orderId + " does not exist.");
        return;
      }

      // Retrieve customer information associated with the order
      int customerId = orderResult.getInt("CustomerID");
      PreparedStatement customerQuery = table.conn.prepareStatement("SELECT * FROM Customers WHERE ID = ?");
      customerQuery.setInt(1, customerId);
      ResultSet customerResult = customerQuery.executeQuery();

      // Check if the customer exists
      if (!customerResult.next()) {
        System.out.println("Customer with ID " + customerId + " does not exist.");
        return;
      }

      // Retrieve order details for the given order ID
      PreparedStatement orderDetailsQuery = table.conn.prepareStatement("SELECT * FROM Order_Details WHERE OrderID = ?");
      orderDetailsQuery.setInt(1, orderId);
      ResultSet orderDetailsResult = orderDetailsQuery.executeQuery();

      // Calculate total amount and display product details
      double totalAmount = 0.0;
      System.out.println("Products:");
      while (orderDetailsResult.next()) {
        int productId = orderDetailsResult.getInt("ProductID");
        Object[] productInfo = getProductById(productId);
        String productName = (String) productInfo[0];
        double unitPrice = orderDetailsResult.getDouble("UnitPrice");
        double quantity = orderDetailsResult.getDouble("Quantity");
        totalAmount += unitPrice * quantity;
        System.out.printf("Product: %-30s Quantity: %5.2f Price: $%10.2f%n", productName, quantity, unitPrice);
      }

      // Calculate taxes based on the total amount and tax rate
      double taxRate = orderResult.getDouble("TaxRate");
      double taxes = totalAmount * (taxRate / 100);

      // Generate and display invoice details
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      Date invoiceDate = new Date();
      System.out.println("\nInvoice Date: " + sdf.format(invoiceDate));
      System.out.println("Order ID: " + orderId);
      System.out.println("Customer Name: " + customerResult.getString("FirstName") + " " + customerResult.getString("LastName"));
      System.out.println("Total Amount: $" + totalAmount);
      System.out.println("Tax: $" + taxes);
      System.out.println("Amount Due: $" + (totalAmount + taxes));

      // Optionally, you can calculate the amount due here

    } catch (SQLException e) {
      System.out.println("Error generating invoice:");
      e.printStackTrace();
    }
  }

  /**
   * Updates information for an existing order in the database.
   */
  private static void updateOrderInformation() {
    try {
      startTransaction(); // Start a new transaction

      // Prompt user for the order ID to update
      System.out.print("Enter the Order ID to Update: ");
      int orderId = input.nextInt();
      input.nextLine(); // Consume newline character

      // Check if the order exists
      PreparedStatement orderQuery = table.conn.prepareStatement("SELECT * FROM Orders WHERE OrderID = ?");
      orderQuery.setInt(1, orderId);
      ResultSet orderResult = orderQuery.executeQuery();
      if (!orderResult.next()) {
        System.out.println("Order with ID " + orderId + " does not exist.");
        rollbackTransaction(); // Roll back the transaction
        return;
      }

      // Display original order products
      System.out.println("\nOriginal Order Products:");
      PreparedStatement orderProductsQuery = table.conn.prepareStatement("SELECT * FROM Order_Details WHERE OrderID = ?");
      orderProductsQuery.setInt(1, orderId);
      ResultSet orderProductsResult = orderProductsQuery.executeQuery();
      while (orderProductsResult.next()) {
        int productId = orderProductsResult.getInt("ProductID");
        double quantity = orderProductsResult.getDouble("Quantity");
        double listPrice = orderProductsResult.getDouble("UnitPrice");
        Object[] productInfo = getProductById(productId);
        String productName = (String) productInfo[0];
        System.out.println("ProductID: " + productId + ", Product Name: " + productName + ", Quantity: " + quantity + ", Unit Price: " + listPrice);
      }
      System.out.println(); // Blank line for readability

      // Prompt user for updated order information
      System.out.println("Enter New Order Information:");
      System.out.print("Order Date (yyyy-mm-dd): ");
      String newOrderDate = input.nextLine();
      System.out.print("Ship Name: ");
      String newShipName = input.nextLine();
      System.out.print("Ship Address: ");
      String newShipAddress = input.nextLine();

      // Delete existing order details
      PreparedStatement deleteOrderDetailsStmt = table.conn.prepareStatement("DELETE FROM Order_Details WHERE OrderID = ?");
      deleteOrderDetailsStmt.setInt(1, orderId);
      deleteOrderDetailsStmt.executeUpdate();

      // Prompt user to update products
      System.out.println("\nUpdate Order Products:");

      // Prompt user to enter products until they choose to stop
      while (true) {
        System.out.print("Enter Product ID (or 0 to finish): ");
        int productId = input.nextInt();
        input.nextLine(); // Consume newline character

        if (productId == 0) {
          break;
        }

        System.out.print("Enter Quantity: ");
        double quantity = input.nextDouble();
        input.nextLine(); // Consume newline character

        // Insert updated order details
        PreparedStatement insertOrderDetailsStmt = table.conn.prepareStatement("INSERT INTO Order_Details (OrderID, ProductID, Quantity, UnitPrice ) VALUES (?, ?, ?, ?)");
        insertOrderDetailsStmt.setInt(1, orderId);
        insertOrderDetailsStmt.setInt(2, productId);
        insertOrderDetailsStmt.setDouble(3, quantity);
        Object[] productInfo = getProductById(productId);
        double listPrice = (double) productInfo[1];
        insertOrderDetailsStmt.setDouble(4, listPrice);
        insertOrderDetailsStmt.executeUpdate();
      }

      commitTransaction(); // Commit the transaction
      System.out.println("\n---------------\nOrder with ID " + orderId + " has been updated successfully.\n");

    } catch (SQLException e) {
      rollbackTransaction(); // Roll back the transaction if an exception occurs
      System.out.println("Error while updating order information:");
      e.printStackTrace();
    }
  }

  /**
   * View all employees in the database.
   * @throws SQLException If an SQL exception occurs
   */
  private static void viewEmployees() throws SQLException {
    // Construct SQL query to fetch employee details
    String query = "SELECT ID, FirstName, LastName, Email, JobTitle FROM Employees;";
    ResultSet result = table.executeQuery(query);

    // Display header for the employee list
    System.out.println("Employees:");
    System.out.println("--------------------------------------------------------------------------------------------------------------------------------");
    System.out.printf("%-5s %-20s %-20s %-40s %-20s\n", "ID", "FirstName", "LastName", "Email", "JobTitle");
    System.out.println("--------------------------------------------------------------------------------------------------------------------------------");

    // Iterate over the result set and display employee details
    while (result.next()) {
      int employeeID = result.getInt("ID");
      String firstName = result.getString("FirstName");
      String lastName = result.getString("LastName");
      String email = result.getString("Email");
      String jobtitle = result.getString("JobTitle");
      System.out.printf("%-5s %-20s %-20s %-40s %-20s\n", employeeID, firstName, lastName, email, jobtitle);
    }

    // Display footer for the employee list
    System.out.println("--------------------------------------------------------------------------------------------------------------------------------");
    System.out.println("\nEnd of Employee List...\n");
  }

  /**
   * View all shippers in the database.
   * @throws SQLException If an SQL exception occurs
   */
  private static void viewShippers() throws SQLException {
    // Construct SQL query to fetch shipper details
    String query = "SELECT * FROM Shippers;";
    ResultSet result = table.executeQuery(query);

    // Display header for the shipper list
    System.out.println("Shippers:");
    System.out.println("-----------------------------------------------------------------------------------------------------------------------------------");
    System.out.printf("%-5s %-20s %-20s %-20s %-20s %-20s %-20s\n", "ID", "Company", "Address", "City", "State", "ZIP", "Country");
    System.out.println("-----------------------------------------------------------------------------------------------------------------------------------");

    // Iterate over the result set and display shipper details
    while (result.next()) {
      int ID = result.getInt("ID");
      String company = result.getString("Company");
      String address = result.getString("Address");
      String city = result.getString("City");
      String state = result.getString("State");
      String ZIP = result.getString("ZIP");
      String country = result.getString("Country");

      System.out.printf("%-5s %-20s %-20s %-20s %-20s %-20s %-20s \n", ID, company, address, city, state, ZIP, country);
    }

    // Display footer for the shipper list
    System.out.println("-------------------------------------------------------------------------------------------------------------------------------------");
    System.out.println("\nEnd of Shipper List...\n");
  }

  /**
   * View all customers in the database.
   */
  private static void viewCustomers() {
    try {
      // Construct SQL query to fetch customer details
      String query = "SELECT ID, FirstName, LastName, Email FROM Customers;";
      ResultSet result = table.executeQuery(query);

      // Display header for the customer list
      System.out.println("Customers:");
      System.out.println("-------------------------------------------------------------------------------");
      System.out.printf("%-5s %-20s %-20s %-20s\n", "ID", "FirstName", "LastName", "Email");
      System.out.println("-------------------------------------------------------------------------------");

      // Iterate over the result set and display customer details
      while (result.next()) {
        int customerId = result.getInt("ID");
        String firstName = result.getString("FirstName");
        String lastName = result.getString("LastName");
        String email = result.getString("Email");
        System.out.printf("%-5s %-20s %-20s %-20s\n", customerId, firstName, lastName, email);
      }

      // Display footer for the customer list
      System.out.println("--------------------------------------------------------------------");
      System.out.println("\nEnd of Customer List...\n");
    } catch (SQLException e) {
      // Handle any SQL exceptions that occur
      System.out.println("Error while fetching customers:");
      e.printStackTrace();
    }
  }
  private static void printProductQuantity() {
    try {
      String query = "SELECT p.ID, p.ProductName, COALESCE(SUM(it.Quantity), 0) AS AvailableQuantity " +
        "FROM Products p " +
        "LEFT JOIN Inventory_Transactions it ON p.ID = it.ProductID " +
        "GROUP BY p.ID, p.ProductName";
      ResultSet result = table.executeQuery(query);

      System.out.println("Product Quantities:");
      System.out.println("------------------------------------");
      System.out.printf("%-5s %-20s %-20s\n", "ID", "ProductName", "AvailableQuantity");
      System.out.println("------------------------------------");

      while (result.next()) {
        int productId = result.getInt("ID");
        String productName = result.getString("ProductName");
        int availableQuantity = result.getInt("AvailableQuantity");

        System.out.printf("%-5s %-20s %-20s\n", productId, productName, availableQuantity);
      }

      System.out.println("------------------------------------");
      System.out.println("\nEnd of Product Quantities List...\n");
    } catch (SQLException e) {
      System.out.println("Error while fetching product quantities:");
      e.printStackTrace();
    }
  }

  /**
   * Exits the program and closes the database connection.
   */
  private static void exit() throws SQLException {
    // Display exit message
    System.out.print("\n---------------\n Exiting Program \n\n");

    // Close the database connection
    table.conn.close();
  }

}