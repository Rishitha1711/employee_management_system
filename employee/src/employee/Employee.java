package employee;

import java.sql.*;

import java.util.Scanner;

public class Employee {
	private static String loggedInRole = null;
    private static int loggedInUserId = -1;
    
	public static void main(String[] args)throws Exception {
		// TODO Auto-generated method stub
		
		Class.forName("com.mysql.cj.jdbc.Driver");
		System.out.println("Class loaded onto memory");

		Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/ems","root","tiger");
		System.out.println("connected to db");
		Scanner s=new Scanner(System.in);
		
		if (!login(con, s)) {
            System.out.println("Invalid login. Exiting...");
            return;
        }
		
		while (true) {
			System.out.println("\nEmployee Management System");
            if ("admin".equals(loggedInRole)) {
            System.out.println("\nEmployee Management System");
            System.out.println("1. Add Employee");
            System.out.println("2. Edit Employee");
            System.out.println("3. View Employees");
            System.out.println("4. Delete Employee");
            System.out.println("5. Assign Department to Employee");
            System.out.println("6.Assign task to employee");
            System.out.println("7.Search Employee");
            System.out.println("8.View Department Details");
            System.out.println("9. Exit");
            System.out.print("Enter your choice: ");
            int choice = s.nextInt();
            s.nextLine(); // Consume newline

            switch (choice) {
                case 1 : addEmployee(con, s);
                break;
                case 2 : editEmployee(con, s);
                break;
                case 3 : viewEmployees(con);
                break;
                case 4 : deleteEmployee(con, s);
                break;
                case 5 : assignDepartment(con, s);
                break;
                case 6 : assignTask(con,s);
                break;
                case 7 : searchEmployee(con,s);
                break;
                case 8 : viewDepartmentDetails(con);
                break;
                case 9 : {
                    System.out.println("Logging out...");
                    loggedInRole = null;
                    if (!login(con, s)) {
                        System.out.println("Invalid login. Exiting...");
                        return;
                    }
                }
                default : System.out.println("Invalid choice. Try again.");
            }
        }
            else if ("employee".equals(loggedInRole)) {
                System.out.println("1. View Assigned Department");
                System.out.println("2. Update Personal Information");
                System.out.println("3. View Salary Details");
                System.out.println("4. Submit Feedback");
                System.out.println("5. View Assigned task");
                System.out.println("6. Logout");
                System.out.print("Enter your choice: ");
                int choice = s.nextInt();
                s.nextLine(); // Consume newline

                switch (choice) {
                    case 1 : viewAssignedDepartment(con);
                    break;
                    case 2 : updatePersonalInfo(con, s);
                    break;
                    case 3 : viewSalaryDetails(con);
                    break;
                    case 4 : submitFeedback(con, s);
                    break;
                    case 5 : viewAssignedTasks(con);
                    break;
                    case 6 : {
                        System.out.println("Logging out...");
                        loggedInRole = null;
                        loggedInUserId = -1;
                        if (!login(con, s)) {
                            System.out.println("Invalid login. Exiting...");
                            return;
                        }
                    }
                    default : System.out.println("Invalid choice. Try again.");
                }
            }
        }
    }

public static void addEmployee(Connection connection, Scanner scanner) throws SQLException {
    System.out.print("Enter employee name: ");
    String name = scanner.nextLine();
    System.out.print("Enter employee email: ");
    String email = scanner.nextLine();
    System.out.print("Enter department ID: ");
    int departmentId = scanner.nextInt();
    System.out.print("Enter salary: ");
    double salary = scanner.nextDouble();

    String sql = "INSERT INTO Employees (name, email, department_id, salary) VALUES (?, ?, ?, ?)";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setString(1, name);
        stmt.setString(2, email);
        stmt.setInt(3, departmentId);
        stmt.setDouble(4, salary);
        stmt.executeUpdate();
        System.out.println("Employee added successfully.");
    }
}
public static void editEmployee(Connection connection, Scanner scanner) throws SQLException {
    System.out.print("Enter employee ID to edit: ");
    int employeeId = scanner.nextInt();
    scanner.nextLine(); // Consume newline

    System.out.print("Enter new name: ");
    String name = scanner.nextLine();
    System.out.print("Enter new email: ");
    String email = scanner.nextLine();
    System.out.print("Enter new department ID: ");
    int departmentId = scanner.nextInt();
    System.out.print("Enter new salary: ");
    double salary = scanner.nextDouble();

    String sql = "UPDATE Employees SET name = ?, email = ?, department_id = ?, salary = ? WHERE employee_id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setString(1, name);
        stmt.setString(2, email);
        stmt.setInt(3, departmentId);
        stmt.setDouble(4, salary);
        stmt.setInt(5, employeeId);
        stmt.executeUpdate();
        System.out.println("Employee details updated successfully.");
    }
}
public static void viewEmployees(Connection connection) throws SQLException {
    String sql = "SELECT e.employee_id, e.name, e.email, d.department_name, e.salary FROM Employees e LEFT JOIN Departments d ON e.department_id = d.department_id";
    try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
        System.out.printf("%-10s %-20s %-30s %-20s %-10s\n", "ID", "Name", "Email", "Department", "Salary");
        System.out.println("------------------------------------------------------------------------------------------");
        while (rs.next()) {
            System.out.printf("%-10d %-20s %-30s %-20s %-10.2f\n",
                    rs.getInt("employee_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("department_name"),
                    rs.getDouble("salary"));
        }
    }
}
public static void deleteEmployee(Connection connection, Scanner scanner) throws SQLException {
    System.out.print("Enter employee ID to delete: ");
    int employeeId = scanner.nextInt();

    String sql = "DELETE FROM Employees WHERE employee_id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, employeeId);
        int rowsAffected = stmt.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Employee deleted successfully.");
        } else {
            System.out.println("Employee not found.");
        }
    }
}
public static void assignDepartment(Connection connection, Scanner scanner) throws SQLException {
    System.out.print("Enter employee ID: ");
    int employeeId = scanner.nextInt();
    System.out.print("Enter department ID: ");
    int departmentId = scanner.nextInt();

    String sql = "UPDATE Employees SET department_id = ? WHERE employee_id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, departmentId);
        stmt.setInt(2, employeeId);
        stmt.executeUpdate();
        System.out.println("Department assigned successfully.");
    }
}
public static void assignTask(Connection connection, Scanner scanner) throws SQLException {
    System.out.print("Enter employee ID to assign task: ");
    int employeeId = scanner.nextInt();
    scanner.nextLine(); // Consume newline

    System.out.print("Enter task description: ");
    String taskDescription = scanner.nextLine();

    System.out.print("Enter task deadline (YYYY-MM-DD): ");
    String taskDeadline = scanner.nextLine();

    String sql = "INSERT INTO Tasks (employee_id, task_description, task_deadline) VALUES (?, ?, ?)";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, employeeId);
        stmt.setString(2, taskDescription);
        stmt.setDate(3, Date.valueOf(taskDeadline));
        stmt.executeUpdate();
        System.out.println("Task assigned successfully.");
    }
}

public static void searchEmployee(Connection connection, Scanner scanner) throws SQLException {
    System.out.print("Enter search keyword (name/email): ");
    String keyword = scanner.nextLine();

    String sql = "SELECT e.employee_id, e.name, e.email, d.department_name, e.salary " +
                 "FROM Employees e LEFT JOIN Departments d ON e.department_id = d.department_id " +
                 "WHERE e.name LIKE ? OR e.email LIKE ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setString(1, "%" + keyword + "%");
        stmt.setString(2, "%" + keyword + "%");
        try (ResultSet rs = stmt.executeQuery()) {
            System.out.printf("%-10s %-20s %-30s %-20s %-10s\n", "ID", "Name", "Email", "Department", "Salary");
            System.out.println("------------------------------------------------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-10d %-20s %-30s %-20s %-10.2f\n",
                        rs.getInt("employee_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("department_name"),
                        rs.getDouble("salary"));
            }
        }
    }
}
public static void viewDepartmentDetails(Connection connection) throws SQLException {
    String sql = "SELECT d.department_id, d.department_name, COUNT(e.employee_id) AS employee_count " +
                 "FROM Departments d LEFT JOIN Employees e ON d.department_id = e.department_id " +
                 "GROUP BY d.department_id, d.department_name";
    try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
        System.out.printf("%-15s %-20s %-15s\n", "Department ID", "Department Name", "Employees");
        System.out.println("----------------------------------------------------");
        while (rs.next()) {
            System.out.printf("%-15d %-20s %-15d\n",
                    rs.getInt("department_id"),
                    rs.getString("department_name"),
                    rs.getInt("employee_count"));
        }
    }
}
public static boolean login(Connection connection, Scanner scanner) throws SQLException {
    System.out.println("\nLogin");
    System.out.print("Enter username: ");
    String username = scanner.nextLine();
    System.out.print("Enter password: ");
    String password = scanner.nextLine();

    String sql = "SELECT user_id, role FROM Users WHERE username = ? AND password = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setString(1, username);
        stmt.setString(2, password);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                loggedInUserId = rs.getInt("user_id");
                loggedInRole = rs.getString("role");
                System.out.println("Login successful as " + loggedInRole);
                return true;
            } else {
                System.out.println("Invalid username or password.");
                return false;
            }
        }
    }
}

public static void viewAssignedDepartment(Connection connection) throws SQLException {
    String sql = "SELECT d.department_name FROM Departments d "
               + "JOIN Employees e ON d.department_id = e.department_id WHERE e.employee_id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, loggedInUserId);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                System.out.println("Department: " + rs.getString("department_name"));
            } else {
                System.out.println("No department assigned.");
            }
        }
    }
}

public static void updatePersonalInfo(Connection connection, Scanner scanner) throws SQLException {
    System.out.print("Enter new name: ");
    String name = scanner.nextLine();
    System.out.print("Enter new email: ");
    String email = scanner.nextLine();

    String sql = "UPDATE Employees SET name = ?, email = ? WHERE employee_id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setString(1, name);
        stmt.setString(2, email);
        stmt.setInt(3, loggedInUserId);
        stmt.executeUpdate();
        System.out.println("Personal information updated successfully.");
    }
}

public static void viewSalaryDetails(Connection connection) throws SQLException {
    String sql = "SELECT salary FROM Employees WHERE employee_id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, loggedInUserId);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                System.out.println("Current Salary: " + rs.getDouble("salary"));
            } else {
                System.out.println("Salary details not found.");
            }
        }
    }
}
public static void viewAssignedTasks(Connection connection) throws SQLException {
    String sql = "SELECT task_id, task_description, task_deadline, task_status "
               + "FROM Tasks WHERE employee_id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, loggedInUserId);
        try (ResultSet rs = stmt.executeQuery()) {
            System.out.printf("%-10s %-50s %-15s %-15s\n", "Task ID", "Description", "Deadline", "Status");
            System.out.println("---------------------------------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-10d %-50s %-15s %-15s\n",
                        rs.getInt("task_id"),
                        rs.getString("task_description"),
                        rs.getDate("task_deadline"),
                        rs.getString("task_status"));
            }
        }
    }
}


public static void submitFeedback(Connection connection, Scanner scanner) throws SQLException {
    System.out.print("Enter your feedback: ");
    String feedback = scanner.nextLine();

    String sql = "INSERT INTO Feedback (employee_id, feedback) VALUES (?, ?)";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, loggedInUserId);
        stmt.setString(2, feedback);
        stmt.executeUpdate();
        System.out.println("Feedback submitted successfully.");
    }
}
}


