-- Create the database
CREATE DATABASE bank_App;

-- Use the newly created database
USE bank_App;

-- Create the user's table with account_number
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,  -- Still needed for internal reference
    account_number INT NOT NULL UNIQUE,       -- Account number for transactions
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,           -- Hashed password
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(15) NOT NULL UNIQUE,
    role ENUM('admin', 'customer') NOT NULL DEFAULT 'customer',
    balance BIGINT UNSIGNED NOT NULL DEFAULT 0, -- Stored in paise/cents
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('pending', 'created') NOT NULL DEFAULT 'pending'
);

alter table users add is_deleted boolean ; 

-- Create the transactions table using account_number for sender and receiver
CREATE TABLE transactions (
    txn_id INT AUTO_INCREMENT PRIMARY KEY,
    sender_account_number INT NOT NULL,           -- Account number of the sender
    receiver_account_number INT,                   -- Account number of the receiver (NULL if withdrawal/deposit)
    type ENUM('credit','debit','transfer') NOT NULL,
    amount BIGINT UNSIGNED NOT NULL,               -- Stored in paise/cents
    details VARCHAR(255),                          -- Optional description
    txn_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_account_number) REFERENCES users(account_number) ON DELETE CASCADE,
    FOREIGN KEY (receiver_account_number) REFERENCES users(account_number) ON DELETE CASCADE
);

-- Add indexes to the users and transactions tables for performance
CREATE INDEX idx_username ON users(username);
CREATE INDEX idx_email ON users(email);
CREATE INDEX idx_account_number ON users(account_number); -- Index for account_number
CREATE INDEX idx_sender_account_number ON transactions(sender_account_number);
CREATE INDEX idx_receiver_account_number ON transactions(receiver_account_number);
CREATE INDEX idx_txn_date ON transactions(txn_date);

-- Insert a sample admin user with account number
INSERT INTO users (account_number, username, password, email, phone, role, balance, status)
VALUES (
    10000001,  -- Example account number
    'admin01',
    '$2y$10$e0NRJjQH3xH5Z6RznhnQmeQqMf0rj3jQ2zFZKf2jZs3z4vLf7Fp8y', -- bcrypt hash for 'Admin@123'
    'admin@example.com',
    '+911234567890',
    'admin',
    0,
    'created'
);

-- Select the users to confirm
SELECT * FROM users;

UPDATE `bank_app`.`users`
SET `is_deleted` = FALSE
WHERE `user_id` = 1;
