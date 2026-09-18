-- ========================================================
-- Campus Lost and Found System - Database Schema
-- Course: CSE2006 Programming in Java (VIT Bhopal)
-- ========================================================

CREATE DATABASE IF NOT EXISTS campus_lostfound;
USE campus_lostfound;

-- Table: Users (Stores Students, Faculty, and Staff)
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    user_type VARCHAR(20) NOT NULL, -- STUDENT, FACULTY, STAFF
    reg_no VARCHAR(30),             -- For Students (e.g., 23BCE10001)
    employee_id VARCHAR(30),        -- For Faculty (e.g., EMP1024)
    staff_id VARCHAR(30),           -- For Staff (e.g., STF205)
    department VARCHAR(80),
    extra_info VARCHAR(100),        -- Hostel room for student, cabin for faculty, designation for staff
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table: Items (Stores both Lost and Found reports)
CREATE TABLE IF NOT EXISTS items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    report_type VARCHAR(10) NOT NULL, -- 'LOST' or 'FOUND'
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(50) NOT NULL,    -- ELECTRONICS, ID_CARD, BOOKS_STATIONERY, BAGS, KEYS, CLOTHING, BOTTLE, OTHER
    location VARCHAR(120) NOT NULL,
    item_date VARCHAR(20) NOT NULL,   -- Date of occurrence (YYYY-MM-DD)
    status VARCHAR(20) NOT NULL,      -- OPEN, CLAIM_PENDING, CLAIMED, CLOSED
    user_id INT NOT NULL,             -- Reporter ID
    reward_amount DOUBLE DEFAULT 0.0, -- Specific to Lost items
    is_identifiable BOOLEAN DEFAULT FALSE,
    storage_location VARCHAR(120),    -- Specific to Found items (e.g., Security Gate 1, Proctor Office)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Table: Claims (Stores claims filed on items)
CREATE TABLE IF NOT EXISTS claims (
    claim_id INT AUTO_INCREMENT PRIMARY KEY,
    lost_item_id INT,
    found_item_id INT NOT NULL,
    claimant_user_id INT NOT NULL,
    proof_details TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,      -- PENDING, APPROVED, REJECTED
    claim_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (found_item_id) REFERENCES items(item_id) ON DELETE CASCADE,
    FOREIGN KEY (claimant_user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Sample Initial Data for Testing
INSERT INTO users (name, email, password, phone, user_type, reg_no, department, extra_info)
VALUES ('Rahul Sharma', 'rahul.sharma@vitbhopal.ac.in', 'rahul123', '9876543210', 'STUDENT', '23BCE10123', 'SCOPE', 'Block 1 - Room 204')
ON DUPLICATE KEY UPDATE name=name;

INSERT INTO users (name, email, password, phone, user_type, employee_id, department, extra_info)
VALUES ('Dr. Ananya Verma', 'ananya.verma@vitbhopal.ac.in', 'prof123', '9811223344', 'FACULTY', 'FAC2045', 'SCSE', 'Cabin AB-1 305')
ON DUPLICATE KEY UPDATE name=name;

INSERT INTO items (report_type, title, description, category, location, item_date, status, user_id, reward_amount, is_identifiable)
VALUES ('LOST', 'Blue HP Laptop Bag with Charger', 'Navy blue HP 15.6 inch bag containing original 65W charger and wireless mouse', 'BAGS', 'Central Library Ground Floor', '2026-09-15', 'OPEN', 1, 500.0, TRUE);

INSERT INTO items (report_type, title, description, category, location, item_date, status, user_id, storage_location)
VALUES ('FOUND', 'Navy Blue HP Laptop Bag', 'Found a blue laptop bag near library study cubicle, contains mouse and adapter', 'BAGS', 'Central Library Ground Floor', '2026-09-15', 'OPEN', 2, 'Security Office AB-1');
