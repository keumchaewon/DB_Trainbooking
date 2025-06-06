CREATE TABLE User (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(50),
    email VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE Route (
    route_id INT AUTO_INCREMENT PRIMARY KEY,
    start_station VARCHAR(100) NOT NULL,
    end_station VARCHAR(100) NOT NULL
);

CREATE TABLE Train (
    train_id INT AUTO_INCREMENT PRIMARY KEY,
    train_name VARCHAR(50) NOT NULL,
    train_type VARCHAR(50) NOT NULL
);

CREATE TABLE Schedule (
    schedule_id INT AUTO_INCREMENT PRIMARY KEY,
    train_id INT NOT NULL,
    route_id INT NOT NULL,
    run_date DATE NOT NULL,
    departure_time TIME NOT NULL,
    FOREIGN KEY (train_id) REFERENCES Train(train_id),
    FOREIGN KEY (route_id) REFERENCES Route(route_id)
);

CREATE TABLE Seat (
    seat_id INT AUTO_INCREMENT PRIMARY KEY,
    schedule_id INT NOT NULL,
    seat_number VARCHAR(10) NOT NULL,
    is_reserved BOOLEAN DEFAULT 0,
    FOREIGN KEY (schedule_id) REFERENCES Schedule(schedule_id)
    UNIQUE (schedule_id, seat_number)
);

CREATE TABLE Reservation (
    reservation_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    schedule_id INT NOT NULL,
    seat_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE,
    FOREIGN KEY (schedule_id) REFERENCES Schedule(schedule_id),
    FOREIGN KEY (seat_id) REFERENCES Seat(seat_id)
);
