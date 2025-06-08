# 🚄 Train Reservation System

2025-1 Database Train Ticket Booking System,
Team "NOLLBACK"

## 📋 Project Overview

A console-based train reservation system built with Java and MySQL. It provides separate menu systems for customers and staff, managing the entire reservation process from train booking to seat management.

## 🛠️ Software Requirements

- **Language**: Java Development Kit (JDK) 17 or higher – for compiling and executing the application
- **Database**: MySQL Server 8.0 or higher – for managing the relational database
- **Build Tool**: Maven – build tool for compiling and running the application
- **JDBC Driver**: MySQL Connector/J 8.0.33 – JDBC driver for MySQL connectivity

## 📁 Project Structure

```
DB_Trainbooking/
├── src/main/java/com/team/project/
│   ├── Main.java                # Main application class
│   ├── ConnectionManager.java   # Database connection management
│   └── menu/
│       ├── DeleteMenu.java      # Data deletion features
│       ├── InputValidator.java  # Input validation features  
│       ├── InsertMenu.java      # Data insertion features
│       ├── SelectMenu.java      # Data query features
│       └── UpdateMenu.java      # Data update features
├── Database/
│   └── Schema.sql               # Database schema
└── pom.xml                      # Maven configuration file
```

## 🗄️ Database Schema

### Main Tables
- **User**: User information (user_id, name, phone, email)
- **Train**: Train information (train_id, train_name, train_type)
- **Route**: Route information (route_id, start_station, end_station)
- **Schedule**: Operation schedule (schedule_id, train_id, route_id, run_date, departure_time)
- **Seat**: Seat information (seat_id, schedule_id, seat_number, is_reserved)
- **Reservation**: Reservation information (reservation_id, user_id, schedule_id, seat_id)

## 🚀 Installation & Setup

This project is distributed with:

- Java source code files (`.java`) including comments
- A compiled `.jar` file for execution

To run the application using the `.jar` file, follow the instructions below.

### 1. Set Up the Database
Before running the application:

1. Start your MySQL server.
2. Download the following SQL scripts:
- **createschema.sql** : defines all tables, constraints, views, and triggers.
- **initdata.sql** : inserts initial sample data into the tables.
- **dropschema.sql** : drops all tables and views to reset the database.
3. Execute the following SQL script to initialize the database:

```bash
mysql -u root -p < createschema.sql
mysql -u root -p < initdata.sql
```

This will create necessary tables, views, and sample data.

---

### 2. Set the Database Password

The application retrieves the MySQL password using the environment variable `DB_PASSWORD`.


####  Use Environment Variable (secure and flexible): 

Set the environment variable:

```bash
# Mac / Linux
export DB_PASSWORD=your_mysql_password

# Windows (cmd)
set DB_PASSWORD=your_mysql_password
```

Or in IntelliJ:

1. Go to **Run > Edit Configurations**
2. Create a new Application configuration
3. Set **Main class** to `com.team.project.Main`
4. Add `DB_PASSWORD=your_mysql_password` in **Environment variables**

---

### 3. Run the Application

Use the following command:

```bash
java -jar TrainReservation.jar
```

Make sure the `.jar` file name matches the actual file provided in your project.

---

## 🎯 Main Features

### 👥 Customer Menu
1. **Train Booking/Inquiry**
    - Book train tickets
    - View available trains

2. **Reservation Management**
    - View reservations
    - Change seats
    - Cancel reservations

3. **User Management**
    - Register users
    - Edit information
    - Delete accounts

### 👨‍💼 Staff Menu (Password: `nollback`)
1. **Registration Menu**
    - Register trains
    - Register routes
    - Register schedules
    - Register seats

2. **View Menu**
    - Reservation status by date
    - Reserved seat details
    - All users list

## 🎨 Special Features

- **Color Output**: Express trains displayed in blue
- **Seat Map**: Visual seat layout provided
- **Input Validation**: Strong data validation
- **Transaction Processing**: Data consistency guaranteed
- **User-Friendly Interface**: Intuitive menu structure

## 📝 Usage Examples

### Train Booking Process
1. Select customer menu
2. Train Booking/Inquiry → Book Train Ticket
3. Enter name and email
4. Select desired schedule
5. Choose desired seat from seat map
6. Complete reservation

### Seat Map Display Example
```
A row: [1A] [2A] [3A] --  [5A] 
B row: [1B] --  [3B] [4B] [5B]
C row: --  [2C] [3C] [4C] [5C]
```
- `[seat_number]`: Available seats
- `--`: Already reserved seats

## ⚠️ Important Notes

- Database connection information must be configured individually
- MySQL server must be running

## 📄 License

This project was created for educational purposes.
