INSERT INTO user (user_id, nickname, email, password, profile, isActive, isValid, lastLogin, accountType) 
values (1, 'testuser', 'testuser@test.ch', '$2a$10$yO8MLIYCznKe2N/xTgsrD.hxrelL8rZzSLimpc.r7JoyKquqf8bTS', 1, 1, 1, now(), 'local');
INSERT INTO profile (profile_id, imagePath, birthday, score, language, difficulty) 
values (1, null, null, 0, 'de', 0);