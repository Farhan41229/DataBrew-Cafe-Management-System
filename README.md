# ☕ DataBrew Café Management System
A complete relational database–driven management system designed for cafes, built using **MySQL**, **SQL scripts**, and a structured **RDBMS** architecture.

---
<div align="center">
  <table width="80%">
    <tr>
      <td style="vertical-align: top; padding-right: 20px;">
        <strong>Authors:</strong><br>
        Md. Farhan Ishraq<br>
        Farhan Tahsin Khan<br>
        Tashin Mustakim
      </td>
      <td style="vertical-align: top; padding-left: 20px;">
        <strong>Supervisor:</strong><br>
        Mueeze Al Mushabbir<br>
        Lecturer, Dept. of CSE,<br>
        IUT
      </td>
    </tr>
  </table>
</div>

## 🖼️ ER Diagram
![ER Diagram](https://raw.githubusercontent.com/farhanishraq17/DataBrew-Cafe-Management-System/main/ERD.png)


## 📌 Overview
DataBrew Café Management System is a streamlined solution to manage core café operations efficiently.  
This system is designed to handle:

- Menu items & pricing  
- Customer orders  
- Billing & payments  
- Inventory tracking  
- Employee management  
- Branch-level data management  

The project demonstrates how an RDBMS can power real-world business workflows with consistency, security, and scalability.

---

## 🛠️ Features

### 🔹 Scalability
Efficiently handles expansion such as new menu items, categories, or branches.

### 🔹 Consistency
Ensures real-time updates across tables using referential constraints.

### 🔹 Security
Manages employee data (roles, access, shifts) with secure constraints and controlled privileges.

### 🔹 Integration
Easily integrates with POS systems, dashboards, or web applications.

### 🔹 Recovery
Includes SQL scripts that support easy backup and restoration.

---


## 🧩 Database Modules

### **1. Menu Management**
- Items  
- Categories  
- Prices  
- Availability  

### **2. Order Processing**
- Orders  
- Order Details  
- Bills  
- Payment Methods  

### **3. Inventory Management**
- Ingredients  
- Stock levels  
- Re-ordering alerts  

### **4. Employee Module**
- Staff records  
- Roles (Cashier, Manager, Chef, etc.)  
- Shift schedules  

### **5. Branch Management**
- Multi-branch café support  
- Manager assignment  
- Location data  

---

## ⚙️ Configuration

### Database Setup

1. Copy the example configuration file:
   ```bash
   cp resources/config.properties.example resources/config.properties
   ```

2. Edit `resources/config.properties` with your MySQL credentials:
   ```properties
   db.url=jdbc:mysql://localhost:3306/cafedb?useSSL=false&serverTimezone=UTC
   db.username=your_mysql_username
   db.password=your_mysql_password
   ```

3. Run the schema script to create the database:
   ```bash
   mysql -u your_username -p < resources/schema.sql
   ```

**Note:** The `config.properties` file is excluded from version control for security.

### Connection Pooling Setup

This project uses **HikariCP** for high-performance database connection pooling.

1. Download required JAR files:
   - HikariCP-5.1.0.jar
   - slf4j-api-2.0.9.jar
   - slf4j-simple-2.0.9.jar

2. Place them in the `lib/` folder

3. See [HIKARICP_SETUP.md](HIKARICP_SETUP.md) for detailed setup instructions

**Benefits:**
- ⚡ Up to 100x faster database operations
- 🔄 Efficient connection reuse
- 📊 Built-in monitoring and statistics
- 🛡️ Connection leak detection

---






