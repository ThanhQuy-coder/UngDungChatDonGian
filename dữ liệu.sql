INSERT INTO Users (userID, username, email, password, status) VALUES
('U001', 'Alice', 'alice@example.com', 'hashedpassword1', 'online'),
('U002', 'Bob', 'bob@example.com', 'hashedpassword2', 'offline'),
('U003', 'Charlie', 'charlie@example.com', 'hashedpassword3', 'online');
SELECT * FROM Users;
INSERT INTO ChatSessions (sessionID, user1ID, user2ID) VALUES
('S001', 'U001', 'U002'),
('S002', 'U002', 'U003'),
('S003', 'U001', 'U003');
SELECT * FROM ChatSessions;
INSERT INTO Messages (messageID, senderID, receiverID, content, timestamp, status) VALUES
('M001', 'U001', 'U002', 'Hello Bob!', GETDATE(), 'sent'),
('M002', 'U002', 'U001', 'Hi Alice!', GETDATE(), 'delivered'),
('M003', 'U003', 'U001', 'Hey Alice, how are you?', GETDATE(), 'read'),
('M004', 'U001', 'U003', 'I am good, Charlie!', GETDATE(), 'sent');
SELECT * FROM Messages;
