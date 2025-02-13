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
('USER', 'user1', 0x616161616161, '0912345678', 'user1@example.com', 'User One', 'Male', 'U1', 'Hello, I am User One.', '1990-01-01'),
('USER', 'user2', 0x616161616161, '0923456789', 'user2@example.com', 'User Two', 'Female', 'U2', 'Hello, I am User Two.', '1992-02-02'),
('USER', 'user3', 0x616161616161, '0934567890', 'user3@example.com', 'User Three', NULL, 'U3', NULL, '1994-03-03'),
('USER', 'user4', 0x616161616161, '0945678901', 'user4@example.com', 'User Four', 'Male', 'U4', 'I love traveling.', '1996-04-04'),
('USER', 'user5', 0x616161616161, '0956789012', 'user5@example.com', 'User Five', 'Female', 'U5', 'I enjoy reading books.', '1998-05-05');

Go

-- schedule
INSERT INTO schedule (cover_photo, trip_name, start_date, end_date, FK_user)
VALUES 
(CAST('0x89504E470D0A1A0A0000000D49484452000000100000001008060000001F1F7A1F' AS VARBINARY(MAX)), 'Summer Vacation', '2025-06-15', '2025-06-25', 1),
(CAST('0x89504E470D0A1A0A0000000D4948445200000010000000100806000000C65A3F3D' AS VARBINARY(MAX)), 'Winter Getaway', '2025-12-01', '2025-12-10', 2),
(CAST('0x89504E470D0A1A0A0000000D49484452000000100000001008060000002ACBC21E' AS VARBINARY(MAX)), 'Spring Adventure', '2025-03-10', '2025-03-20', 3),
(CAST('0x89504E470D0A1A0A0000000D4948445200000010000000100806000000479F5C6D' AS VARBINARY(MAX)), 'Autumn Retreat', '2025-09-05', '2025-09-15', 4),
(CAST('0x89504E470D0A1A0A0000000D49484452000000100000001008060000009E81D7FE' AS VARBINARY(MAX)), 'City Break', '2025-07-20', '2025-07-25', 5);
Go

-- event
INSERT INTO event (start_time, end_time, notes, FK_schedule, FK_calendar) VALUES
('09:00:00', '10:00:00', 'Visit Tokyo and Kyoto.', 8, '2025-01-20'),
('09:10:00', '10:13:00', 'Explore Osaka and Nara.', 8, '2025-01-23'),
('11:50:00', '12:00:00', 'Relax in Hakone hot springs.', 8, '2025-01-26'),
('12:00:00', '22:00:00', 'Attend business meetings in Taipei.', 8, '2025-02-01'),
('22:00:00', '23:00:00', 'Family vacation in Bali.', 8, '2025-03-10');
Go

-- followlist
INSERT INTO followlist (follower_id, followed_id, followTime, followStatus) VALUES
(1, 2, '2025-01-20T10:00:00', 'ACTIVE'),  -- user1 ï¿½ï¿½ï¿½` user2
(2, 3, '2025-01-21T14:30:00', 'ACTIVE'),  -- user2 ï¿½ï¿½ï¿½` user3
(3, 1, '2025-01-22T09:15:00', 'BLOCKED'), -- user3 ï¿½Q user1 ï¿½ï¿½ï¿½`ï¿½Ãªï¿½ï¿½ï¿½
(1, 3, '2025-01-23T16:45:00', 'ACTIVE');  -- user1 ï¿½ï¿½ï¿½` user3
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
('ACTIVE', 'ï¿½È¹C', '2025-01-19 01:08:07', '2025-01-19 01:08:07'),
('ACTIVE', 'ï¿½ï¿½ï¿½ï¿½', '2025-01-19 01:08:07', '2025-01-19 01:08:07'),
('INACTIVE', 'ï¿½Bï¿½ï¿½', '2025-01-19 01:08:07', '2025-01-19 01:08:07'),
('ACTIVE', 'ï¿½ï¿½ï¿½ï¿½', '2025-01-19 01:08:07', '2025-01-19 01:08:07'),
('INACTIVE', 'ï¿½qï¿½v', '2025-01-19 01:08:07', '2025-01-19 01:08:07');
Go

-- interaction
INSERT INTO interaction (member_id, postid, interction_type, interaction_time) VALUES
(1, 1, 'LIKE', '2025-01-20T10:00:00'),  -- user1 ï¿½Iï¿½gï¿½Kï¿½ï¿½ 1
(2, 1, 'SHARE', '2025-01-21T14:30:00'), -- user2 ï¿½ï¿½oï¿½Kï¿½ï¿½ 1
(1, 2, 'COLLECT', '2025-01-22T09:15:00'), -- user1 ï¿½ï¿½ï¿½Ã¶Kï¿½ï¿½ 2
(2, 2, 'LIKE', '2025-01-23T16:45:00');   -- user2 ï¿½Iï¿½gï¿½Kï¿½ï¿½ 2
Go

-- place
INSERT INTO dbo.place (
    googlemapPlaceId, placeType, placeName, city, region, placeAddress, 
    longitude, latitude, photos, placePhone, businessHours, 
    placeInfo, rating, website, bookingUrl, priceLevel, 
    accommodationType, reservation, isClosed
) 
VALUES 
('ChIJD7fiBh9u5kcR7tJp0NxA-2c', 'Restaurant', 'The Seafood Place', 'New York', 'NY', '123 Ocean Ave, NY, 10001', 
-74.0060, 40.7128, '["https://image1.jpg", "https://image2.jpg"]', '123-456-7890', 'Mon-Sun 10:00-22:00', 
'Fresh seafood, great views', 4.5, 'https://www.seafoodplace.com', 'https://www.seafoodplace.com/book', 3, 
'Restaurant', 1, 0),

('ChIJCzYy5IS16lQR4oQJ6pZlFf0', 'Hotel', 'Sunset Resort', 'Miami', 'FL', '456 Beach Rd, Miami, FL, 33139', 
-80.1918, 25.7617, '["https://image3.jpg", "https://image4.jpg"]', '987-654-3210', 'Mon-Sun 24 hours', 
'Luxury resort with beach access', 4.7, 'https://www.sunsetresort.com', 'https://www.sunsetresort.com/book', 5, 
'Resort', 0, 0),

('ChIJwzS0bZVm1jURdX-Ktnklnmo', 'Cafe', 'The Cozy Cafe', 'San Francisco', 'CA', '789 Market St, San Francisco, CA, 94103', 
-122.4194, 37.7749, '["https://image5.jpg", "https://image6.jpg"]', '555-789-1234', 'Mon-Sun 07:00-19:00', 
'Cozy atmosphere with delicious pastries', 4.2, 'https://www.cozycafe.com', 'https://www.cozycafe.com/book', 2, 
'Cafe', 1, 0),

('ChIJPVxGlZoUwoARUqtnZQdZlck', 'Museum', 'Art Museum', 'Los Angeles', 'CA', '321 Museum Rd, Los Angeles, CA, 90015', 
-118.2500, 34.0522, '["https://image7.jpg", "https://image8.jpg"]', '222-333-4444', 'Mon-Sun 09:00-17:00', 
'Art and history exhibitions', 4.8, 'https://www.artmuseum.com', 'https://www.artmuseum.com/book', 4, 
'Museum', 0, 0),

('ChIJ2Z5sXwFm3FMRkFfb3lJl5Ug', 'Park', 'Central Park', 'New York', 'NY', 'Central Park, New York, NY, 10024', 
-73.9654, 40.7851, '["https://image9.jpg", "https://image10.jpg"]', 'N/A', 'Mon-Sun 06:00-23:00', 
'Large park with walking trails and ponds', 4.9, 'https://www.centralparknyc.org', 'https://www.centralparknyc.org/book', 1, 
'Park', 0, 0);

-- comments
INSERT INTO comments (postid, commentstate, user_id, comment_created_at, content, parentid)
VALUES 
(1, 'active', 1, '2025-01-19 10:00:00', 'ï¿½oï¿½Oï¿½Ä¤@ï¿½ï¿½ï¿½dï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½e', NULL),
(1, 'active', 2, '2025-01-19 11:00:00', 'ï¿½oï¿½Oï¿½Ä¤Gï¿½ï¿½ï¿½dï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½e', NULL),
(1, 'active', 3, '2025-01-19 12:00:00', 'ï¿½oï¿½Oï¿½Ä¤Tï¿½ï¿½ï¿½dï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½e', NULL),
(1, 'active', 2, '2025-01-19 13:00:00', 'ï¿½oï¿½Oï¿½ï¿½Ä¤@ï¿½ï¿½ï¿½dï¿½ï¿½ï¿½ï¿½ï¿½^ï¿½ï¿½', 1),
(1, 'active', 3, '2025-01-19 14:00:00', 'ï¿½oï¿½Oï¿½ï¿½Ä¤Gï¿½ï¿½ï¿½dï¿½ï¿½ï¿½ï¿½ï¿½^ï¿½ï¿½', 2);
Go

-- coupon
INSERT INTO coupon (coupon_code, remaining, Title, Subtitle, Content, State, Web, Picture, starttime, endtime, placeId) VALUES
('DISCOUNT50', 100, '­­®É§é¦©50%', '¶È­­¥»¤ë', '¨Ï¥Î¦¹Àu´f¨é¥i¨É 50% §é¦©', 1, 'https://example.com/discount50', NULL, '2024-02-01 00:00:00', '2024-02-28 23:59:59', 1),

('FREESHIP', 500, '§K¹B¶OÀu´f', '®ø¶Oº¡¤d§K¹B', '¦b¥»©±®ø¶Oº¡ 1000 ¤¸¡A§Y¥i¨É§K¹BÀu´f', 1, 'https://example.com/freeship', NULL, '2024-02-01 00:00:00', '2024-03-31 23:59:59', 2),

('WELCOME10', 300, '·s·|­û±MÄÝ 10% §é¦©', '­º¦¸ÁÊª«Àu´f', '·s·|­ûµù¥U«á­ºµ§­q³æ¥i¨É 10% §é¦©', 1, 'https://example.com/welcome10', NULL, '2024-02-01 00:00:00', '2024-06-30 23:59:59', 3),

('HOLIDAY20', 200, '¸`¤é¯S´f 20% §é¦©', '­­¶q200±i', '¨Ï¥Î¦¹Àu´f¨é¥i¦b¸`¤é´Á¶¡Àò±o 20% §é¦©', 1, 'https://example.com/holiday20', NULL, '2024-12-01 00:00:00', '2024-12-31 23:59:59', 4),

('FLASHSALE', 50, '­­®É°{ÁÊ 30% OFF', '­­®É­­¶q', '­­¶q 50 ±iªº 30% §é¦©Àu´f¨é¡A¥ý·m¥ýÄ¹', 1, 'https://example.com/flashsale', NULL, '2024-02-10 00:00:00', '2024-02-15 23:59:59', 5);

GO

-- mycoupon
INSERT INTO mycoupon (fk_couponid, fk_userid, gettime) VALUES
(1, 1, '2025-01-01T10:00:00'),  -- ï¿½Ï¥Îªï¿½ user1 ï¿½ï¿½ï¿½ï¿½uï¿½fï¿½ï¿½ NEWYEAR2025
(2, 2, '2025-06-02T14:30:00'),  -- ï¿½Ï¥Îªï¿½ user2 ï¿½ï¿½ï¿½ï¿½uï¿½fï¿½ï¿½ SUMMER25
(3, 3, '2025-09-03T09:00:00'),  -- ï¿½Ï¥Îªï¿½ user3 ï¿½ï¿½ï¿½ï¿½uï¿½fï¿½ï¿½ FALL2025
(4, 4, '2025-12-04T16:45:00'),  -- ï¿½Ï¥Îªï¿½ user4 ï¿½ï¿½ï¿½ï¿½uï¿½fï¿½ï¿½ WINTER2025
(5, 5, '2025-11-05T11:30:00');  -- ï¿½Ï¥Îªï¿½ user5 ï¿½ï¿½ï¿½ï¿½uï¿½fï¿½ï¿½ BLACKFRIDAY25
GO

-- placewithposts
INSERT INTO placewithposts (fk_place_id, fk_post_id)
VALUES
    (1, 1),  -- ï¿½N placeId ï¿½ï¿½ 1 ï¿½ï¿½ï¿½aï¿½Iï¿½P postId ï¿½ï¿½ 101 ï¿½ï¿½ï¿½Kï¿½ï¿½ï¿½ï¿½ï¿½p
    (2, 2),  -- ï¿½N placeId ï¿½ï¿½ 2 ï¿½ï¿½ï¿½aï¿½Iï¿½P postId ï¿½ï¿½ 102 ï¿½ï¿½ï¿½Kï¿½ï¿½ï¿½ï¿½ï¿½p
    (3, 3),  -- ï¿½N placeId ï¿½ï¿½ 3 ï¿½ï¿½ï¿½aï¿½Iï¿½P postId ï¿½ï¿½ 103 ï¿½ï¿½ï¿½Kï¿½ï¿½ï¿½ï¿½ï¿½p
    (4, 4),  -- ï¿½N placeId ï¿½ï¿½ 4 ï¿½ï¿½ï¿½aï¿½Iï¿½P postId ï¿½ï¿½ 104 ï¿½ï¿½ï¿½Kï¿½ï¿½ï¿½ï¿½ï¿½p
    (5, 5);  -- ï¿½N placeId ï¿½ï¿½ 5 ï¿½ï¿½ï¿½aï¿½Iï¿½P postId ï¿½ï¿½ 105 ï¿½ï¿½ï¿½Kï¿½ï¿½ï¿½ï¿½ï¿½p
Go

-- post_tags
INSERT INTO post_tags (postid, tagId) VALUES
(1, 1), -- My First Post -> Travel
(2, 2); -- Another Post -> Adventure
Go

-- Tags_Members
INSERT INTO Tags_Members (tagsBean_tagId, memberBean_userid) VALUES
(1, 1),  -- ï¿½ï¿½ï¿½ï¿½ Travel ï¿½P ï¿½ï¿½ï¿½ï¿½ user1
(2, 2);  -- ï¿½ï¿½ï¿½ï¿½ Adventure ï¿½P ï¿½ï¿½ï¿½ï¿½ user2
Go

-- member_place
INSERT INTO myplace (userid, placeId) 
VALUES
(1, 1),  -- ï¿½ï¿½ï¿½ï¿½ID 1 ï¿½P ï¿½aï¿½IID 101
(1, 2),  -- ï¿½ï¿½ï¿½ï¿½ID 1 ï¿½P ï¿½aï¿½IID 102
(2, 3),  -- ï¿½ï¿½ï¿½ï¿½ID 2 ï¿½P ï¿½aï¿½IID 103
(3, 4),  -- ï¿½ï¿½ï¿½ï¿½ID 3 ï¿½P ï¿½aï¿½IID 104
(4, 5);  -- ï¿½ï¿½ï¿½ï¿½ID 4 ï¿½P ï¿½aï¿½IID 105
Go

-- eventXplace
INSERT INTO eventXplace (fk_event_id, fk_place_id, place_order, arrival_time, stay_duration, notes)
VALUES
<<<<<<< HEAD
(1, 1, 1, '08:00:00', 120, 'ï¿½ï¿½ï¿½Wï¿½ï¿½Fï¿½Aï¿½wï¿½Æ°ï¿½ï¿½[ï¿½aï¿½ï¿½Aï¿½ï¿½ï¿½ï¿½ï¿½Iï¿½C'),
(1, 2, 2, '10:30:00', 90, 'ï¿½ï¿½ï¿½[ï¿½aï¿½IBï¿½Aï¿½]ï¿½tï¿½@ï¿½pï¿½Éªï¿½ï¿½ï¿½ï¿½ï¿½ï¿½C'),
(1, 3, 3, '13:00:00', 150, 'ï¿½ï¿½ï¿½\ï¿½Mï¿½ð®§®É¶ï¿½ï¿½Aï¿½wï¿½Æ¦bï¿½aï¿½ICï¿½ï¿½ï¿½\ï¿½Uï¿½C');
=======
(1, 1, 1, '08:00:00', '09:00:00', '¦­¤W©è¹F¡A¦w±Æ°ÑÆ[¦a¤èAªº´ºÂI¡C'),
(1, 2, 2, '10:30:00', '11:30:00', '°ÑÆ[¦aÂIB¡A¥]§t¤@¤p®Éªº¾ÉÄý¡C'),
(1, 3, 3, '13:00:00', '14:00:00', '¤ÈÀ\©M¥ð®§®É¶¡¡A¦w±Æ¦b¦aÂICªºÀ\ÆU¡C');
>>>>>>> origin/dev
Go