-- Xóa database nếu tồn tại
DROP DATABASE IF EXISTS ChatApp;
GO

-- Tạo mới database
CREATE DATABASE ChatApp;
GO

-- Chọn database
USE ChatApp;
GO

-- Tạo bảng Users
CREATE TABLE Users (
    userID VARCHAR(50) PRIMARY KEY,
    username NVARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    status NVARCHAR(50) DEFAULT 'offline'
);
GO

-- Tạo bảng ChatSessions
CREATE TABLE ChatSessions (
    sessionID VARCHAR(50) PRIMARY KEY,
    user1ID VARCHAR(50) NOT NULL,
    user2ID VARCHAR(50) NOT NULL,
    FOREIGN KEY (user1ID) REFERENCES Users(userID),
    FOREIGN KEY (user2ID) REFERENCES Users(userID)
);
GO

-- Tạo bảng Messages
CREATE TABLE Messages (
    messageID VARCHAR(50) PRIMARY KEY,
    senderID VARCHAR(50) NOT NULL,
    receiverID VARCHAR(50) NOT NULL,
    content NVARCHAR(MAX) NOT NULL,
    timestamp DATETIME DEFAULT GETDATE(),
    status NVARCHAR(50) DEFAULT 'sent',
    FOREIGN KEY (senderID) REFERENCES Users(userID),
    FOREIGN KEY (receiverID) REFERENCES Users(userID)
);
GO

-- Kiểm tra danh sách bảng
SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE';
GO
