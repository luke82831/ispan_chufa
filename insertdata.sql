-- calendar
INSERT INTO calendar (date, week, isHoliday, description) VALUES
('2025-01-20', 'Monday', 0, 'Working day'),
('2025-01-23', 'Thursday', 0, 'Working day'),
('2025-01-26', 'Sunday', 1, 'Weekend'),
('2025-02-01', 'Saturday', 1, 'Weekend'),
('2025-03-10', 'Monday', 0, 'Working day');
Go

-- members
INSERT INTO members (role, username, password, phone_number, email, name, gender, nickname, bio, birth) VALUES
('USER', 'user1', 0x616263, '0912345678', 'user1@example.com', 'User One', 'Male', 'U1', 'Hello, I am User One.', '1990-01-01'),
('USER', 'user2', 0x626364, '0923456789', 'user2@example.com', 'User Two', 'Female', 'U2', 'Hello, I am User Two.', '1992-02-02'),
('USER', 'user3', 0x636465, '0934567890', 'user3@example.com', 'User Three', NULL, 'U3', NULL, '1994-03-03'),
('USER', 'user4', 0x646566, '0945678901', 'user4@example.com', 'User Four', 'Male', 'U4', 'I love traveling.', '1996-04-04'),
('USER', 'user5', 0x656667, '0956789012', 'user5@example.com', 'User Five', 'Female', 'U5', 'I enjoy reading books.', '1998-05-05');
Go

-- schedule
INSERT INTO schedule (trip_name, start_date, end_date, cover_photo, FK_user)
VALUES 
('Trip 1', '2025-02-01', '2025-02-06', 0x89504E470D0A1A0A0000000D49484452, 1),
('Trip 2', '2025-03-15', '2025-03-20', 0x89504E470D0A1A0A0000000D49484452, 1),
('Trip 3', '2025-04-10', '2025-04-15', 0x89504E470D0A1A0A0000000D49484452, 1),
('Trip 4', '2025-05-05', '2025-05-10', 0x89504E470D0A1A0A0000000D49484452, 1),
('Trip 5', '2025-06-01', '2025-06-06', 0x89504E470D0A1A0A0000000D49484452, 1);

Go

-- event
INSERT INTO event (start_time, end_time, notes, FK_schedule, FK_calendar) VALUES
('09:00:00', '10:00:00', 'Visit Tokyo and Kyoto.', 1, '2025-01-20'),
('09:10:00', '10:13:00', 'Explore Osaka and Nara.', 1, '2025-01-23'),
('11:50:00', '12:00:00', 'Relax in Hakone hot springs.', 1, '2025-01-26'),
('12:00:00', '22:00:00', 'Attend business meetings in Taipei.', 2, '2025-02-01'),
('22:00:00', '23:00:00', 'Family vacation in Bali.', 3, '2025-03-10');
Go

-- followlist
INSERT INTO followlist (follower_id, followed_id, followTime, followStatus) VALUES
(1, 2, '2025-01-20T10:00:00', 'ACTIVE'),  -- user1 關注 user2
(2, 3, '2025-01-21T14:30:00', 'ACTIVE'),  -- user2 關注 user3
(3, 1, '2025-01-22T09:15:00', 'BLOCKED'), -- user3 被 user1 關注並阻擋
(1, 3, '2025-01-23T16:45:00', 'ACTIVE');  -- user1 關注 user3
Go

-- post
INSERT INTO post (postStatus, postTitle, postTime, postContent, userid, postLink) VALUES
('ACTIVE', 'My First Post', '2025-01-20T10:00:00', 'This is the content of my first post.', 1, 'https://example.com/post1'),
('INACTIVE', 'Another Post', '2025-01-21T14:30:00', 'This is another post content.', 2, 'https://example.com/post2'),
('ACTIVE', 'My First Post', '2025-01-20T10:00:00', 'aaaaa', 1, 'https://example.com/post1'),
('INACTIVE', 'Another Post', '2025-01-21T14:30:00', 'vvvv', 2, 'https://example.com/post2'),
('ACTIVE', 'My First Post', '2025-01-20T10:00:00', 'ddddt.', 1, 'https://example.com/post1'),
('INACTIVE', 'Another Post', '2025-01-21T14:30:00', 'effefecontent.', 2, 'https://example.com/post2');
Go

-- tags
INSERT INTO tags (tag_state, tag_name, tag_created_at, tag_updated_at)
VALUES
('ACTIVE', '旅遊', '2025-01-19 01:08:07', '2025-01-19 01:08:07'),
('ACTIVE', '美食', '2025-01-19 01:08:07', '2025-01-19 01:08:07'),
('INACTIVE', '運動', '2025-01-19 01:08:07', '2025-01-19 01:08:07'),
('ACTIVE', '音樂', '2025-01-19 01:08:07', '2025-01-19 01:08:07'),
('INACTIVE', '電影', '2025-01-19 01:08:07', '2025-01-19 01:08:07');
Go

-- interaction
INSERT INTO interaction (member_id, postid, interction_type, interaction_time) VALUES
(1, 1, 'LIKE', '2025-01-20T10:00:00'),  -- user1 點讚貼文 1
(2, 1, 'SHARE', '2025-01-21T14:30:00'), -- user2 轉發貼文 1
(1, 2, 'COLLECT', '2025-01-22T09:15:00'), -- user1 收藏貼文 2
(2, 2, 'LIKE', '2025-01-23T16:45:00');   -- user2 點讚貼文 2
Go

-- place
INSERT INTO place (placeType, placeName, city, region, placeAddress, longitude, latitude, placePhone, businessHours, placeInfo, rating, website, bookingUrl, price, accommodationType, reservation)
VALUES
('餐廳', '城市餐廳', '台北', '中正區', '台北市中正區市府路100號', 121.5074, 25.0300, '02-12345678', '08:00-22:00', '提供各式美食', 4.5, 'http://example.com', 'http://example.com/booking', 1200.50, '餐廳', 1),
('景點', '台北101', '台北', '信義區', '台北市信義區信義路五段7號', 121.5654, 25.0330, '02-87654321', '09:00-18:00', '台灣的地標建築', 4.8, 'http://taipei101.com', 'http://taipei101.com/booking', 0.00, '景點', 0),
('旅館', '幸福旅館', '高雄', '左營區', '高雄市左營區自由路1號', 120.2769, 22.6273, '07-12345678', '24小時營業', '提供舒適住宿', 4.3, 'http://happinesshotel.com', 'http://happinesshotel.com/booking', 3000.00, '旅館', 1),
('餐廳', '海鮮大餐', '台中', '南區', '台中市南區建國路88號', 120.6620, 24.1460, '04-12398765', '10:00-21:00', '新鮮海鮮料理', 4.7, 'http://seafoodrestaurant.com', 'http://seafoodrestaurant.com/booking', 1500.00, '餐廳', 1),
('景點', '日月潭', '南投', '魚池鄉', '南投縣魚池鄉日月潭', 120.9217, 23.8531, '04-98765432', '08:00-17:00', '台灣著名的風景區', 4.6, 'http://sunmoonlake.com', 'http://sunmoonlake.com/booking', 0.00, '景點', 0);

-- place_photos
INSERT INTO place_photos (place_placeId, photos)
VALUES 
(1, 'http://example.com/photo1.jpg'),
(1, 'http://example.com/photo2.jpg');
Go

-- comments
INSERT INTO comments (postid, commentstate, user_id, comment_created_at, content, parentid)
VALUES 
(1, 'active', 1, '2025-01-19 10:00:00', '這是第一條留言的內容', NULL),
(1, 'active', 2, '2025-01-19 11:00:00', '這是第二條留言的內容', NULL),
(1, 'active', 3, '2025-01-19 12:00:00', '這是第三條留言的內容', NULL),
(1, 'active', 2, '2025-01-19 13:00:00', '這是對第一條留言的回覆', 1),
(1, 'active', 3, '2025-01-19 14:00:00', '這是對第二條留言的回覆', 2);
Go

-- coupon
INSERT INTO coupon (coupon_code, remaining, Title, Subtitle, Content, State, Web, Picture, starttime, endtime, placeId) VALUES
('NEWYEAR2025', 500, 'New Year Sale', 'NY Sale', 'Get 20% off on all items this New Year', 1, 'https://newyear.com', 'newyear2025.jpg', '2025-01-01T00:00:00', '2025-01-31T23:59:59', 1),  -- New Year Sale
('SUMMER25', 1000, 'Summer Discount', 'Summer Deal', 'Save 25% on all summer products', 1, 'https://summerdeal.com', 'summer25.jpg', '2025-06-01T00:00:00', '2025-08-31T23:59:59', 2),  -- Summer Discount
('FALL2025', 300, 'Fall Promotion', 'Fall Offer', 'Get $10 off your purchase over $50', 1, 'https://fallpromo.com', 'fall2025.jpg', '2025-09-01T00:00:00', '2025-11-30T23:59:59', 3),  -- Fall Promotion
('WINTER2025', 200, 'Winter Clearance', 'Winter Sale', 'Up to 50% off on winter clothes', 1, 'https://wintersale.com', 'winter2025.jpg', '2025-12-01T00:00:00', '2025-12-31T23:59:59', 4),  -- Winter Clearance
('BLACKFRIDAY25', 1500, 'Black Friday Sale', 'BF Deal', 'Get 25% off on selected items this Black Friday', 1, 'https://blackfriday.com', 'blackfriday25.jpg', '2025-11-27T00:00:00', '2025-11-27T23:59:59', 5);  -- Black Friday Sale
GO

-- mycoupon
INSERT INTO mycoupon (fk_couponid, fk_userid, gettime) VALUES
(1, 1, '2025-01-01T10:00:00'),  -- 使用者 user1 領取優惠券 NEWYEAR2025
(2, 2, '2025-06-02T14:30:00'),  -- 使用者 user2 領取優惠券 SUMMER25
(3, 3, '2025-09-03T09:00:00'),  -- 使用者 user3 領取優惠券 FALL2025
(4, 4, '2025-12-04T16:45:00'),  -- 使用者 user4 領取優惠券 WINTER2025
(5, 5, '2025-11-05T11:30:00');  -- 使用者 user5 領取優惠券 BLACKFRIDAY25
GO

-- placewithposts
INSERT INTO placewithposts (fk_place_id, fk_post_id)
VALUES
    (1, 1),  -- 將 placeId 為 1 的地點與 postId 為 101 的貼文關聯
    (2, 2),  -- 將 placeId 為 2 的地點與 postId 為 102 的貼文關聯
    (3, 3),  -- 將 placeId 為 3 的地點與 postId 為 103 的貼文關聯
    (4, 4),  -- 將 placeId 為 4 的地點與 postId 為 104 的貼文關聯
    (5, 5);  -- 將 placeId 為 5 的地點與 postId 為 105 的貼文關聯
Go

-- post_tags
INSERT INTO post_tags (postid, tagId) VALUES
(1, 1), -- My First Post -> Travel
(2, 2); -- Another Post -> Adventure
Go

-- Tags_Members
INSERT INTO Tags_Members (tagsBean_tagId, memberBean_userid) VALUES
(1, 1),  -- 標籤 Travel 與 成員 user1
(2, 2);  -- 標籤 Adventure 與 成員 user2
Go

-- member_place
INSERT INTO myplace (userid, placeId) 
VALUES
(1, 1),  -- 成員ID 1 與 地點ID 101
(1, 2),  -- 成員ID 1 與 地點ID 102
(2, 3),  -- 成員ID 2 與 地點ID 103
(3, 4),  -- 成員ID 3 與 地點ID 104
(4, 5);  -- 成員ID 4 與 地點ID 105
Go

-- eventXplace
INSERT INTO eventXplace (fk_event_id, fk_place_id, place_order, arrival_time, stay_duration, notes)
VALUES
(1, 1, 1, '08:00:00', '09:00:00', '早上抵達，安排參觀地方A的景點。'),
(1, 2, 2, '10:30:00', '11:30:00', '參觀地點B，包含一小時的導覽。'),
(1, 3, 3, '13:00:00', '14:00:00', '午餐和休息時間，安排在地點C的餐廳。');
Go