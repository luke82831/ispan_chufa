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
(1, 2, '2025-01-20T10:00:00', 'ACTIVE'),  -- user1 ���` user2
(2, 3, '2025-01-21T14:30:00', 'ACTIVE'),  -- user2 ���` user3
(3, 1, '2025-01-22T09:15:00', 'BLOCKED'), -- user3 �Q user1 ���`�ê���
(1, 3, '2025-01-23T16:45:00', 'ACTIVE');  -- user1 ���` user3
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
('ACTIVE', '�ȹC', '2025-01-19 01:08:07', '2025-01-19 01:08:07'),
('ACTIVE', '����', '2025-01-19 01:08:07', '2025-01-19 01:08:07'),
('INACTIVE', '�B��', '2025-01-19 01:08:07', '2025-01-19 01:08:07'),
('ACTIVE', '����', '2025-01-19 01:08:07', '2025-01-19 01:08:07'),
('INACTIVE', '�q�v', '2025-01-19 01:08:07', '2025-01-19 01:08:07');
Go

-- interaction
INSERT INTO interaction (member_id, postid, interction_type, interaction_time) VALUES
(1, 1, 'LIKE', '2025-01-20T10:00:00'),  -- user1 �I�g�K�� 1
(2, 1, 'SHARE', '2025-01-21T14:30:00'), -- user2 ��o�K�� 1
(1, 2, 'COLLECT', '2025-01-22T09:15:00'), -- user1 ���öK�� 2
(2, 2, 'LIKE', '2025-01-23T16:45:00');   -- user2 �I�g�K�� 2
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
(1, 'active', 1, '2025-01-19 10:00:00', '�o�O�Ĥ@���d�������e', NULL),
(1, 'active', 2, '2025-01-19 11:00:00', '�o�O�ĤG���d�������e', NULL),
(1, 'active', 3, '2025-01-19 12:00:00', '�o�O�ĤT���d�������e', NULL),
(1, 'active', 2, '2025-01-19 13:00:00', '�o�O��Ĥ@���d�����^��', 1),
(1, 'active', 3, '2025-01-19 14:00:00', '�o�O��ĤG���d�����^��', 2);
Go

-- coupon
INSERT INTO coupon (coupon_code, remaining, Title, Subtitle, Content, State, Web, Picture, starttime, endtime, placeId) VALUES
('DISCOUNT50', 100, '���ɧ馩50%', '�ȭ�����', '�ϥΦ��u�f��i�� 50% �馩', 1, 'https://example.com/discount50', NULL, '2024-02-01 00:00:00', '2024-02-28 23:59:59', 1),

('FREESHIP', 500, '�K�B�O�u�f', '���O���d�K�B', '�b�������O�� 1000 ���A�Y�i�ɧK�B�u�f', 1, 'https://example.com/freeship', NULL, '2024-02-01 00:00:00', '2024-03-31 23:59:59', 2),

('WELCOME10', 300, '�s�|���M�� 10% �馩', '�����ʪ��u�f', '�s�|�����U�᭺���q��i�� 10% �馩', 1, 'https://example.com/welcome10', NULL, '2024-02-01 00:00:00', '2024-06-30 23:59:59', 3),

('HOLIDAY20', 200, '�`��S�f 20% �馩', '���q200�i', '�ϥΦ��u�f��i�b�`�������o 20% �馩', 1, 'https://example.com/holiday20', NULL, '2024-12-01 00:00:00', '2024-12-31 23:59:59', 4),

('FLASHSALE', 50, '���ɰ{�� 30% OFF', '���ɭ��q', '���q 50 �i�� 30% �馩�u�f��A���m��Ĺ', 1, 'https://example.com/flashsale', NULL, '2024-02-10 00:00:00', '2024-02-15 23:59:59', 5);

GO

-- mycoupon
INSERT INTO mycoupon (fk_couponid, fk_userid, gettime) VALUES
(1, 1, '2025-01-01T10:00:00'),  -- �ϥΪ� user1 ����u�f�� NEWYEAR2025
(2, 2, '2025-06-02T14:30:00'),  -- �ϥΪ� user2 ����u�f�� SUMMER25
(3, 3, '2025-09-03T09:00:00'),  -- �ϥΪ� user3 ����u�f�� FALL2025
(4, 4, '2025-12-04T16:45:00'),  -- �ϥΪ� user4 ����u�f�� WINTER2025
(5, 5, '2025-11-05T11:30:00');  -- �ϥΪ� user5 ����u�f�� BLACKFRIDAY25
GO

-- placewithposts
INSERT INTO placewithposts (fk_place_id, fk_post_id)
VALUES
    (1, 1),  -- �N placeId �� 1 ���a�I�P postId �� 101 ���K�����p
    (2, 2),  -- �N placeId �� 2 ���a�I�P postId �� 102 ���K�����p
    (3, 3),  -- �N placeId �� 3 ���a�I�P postId �� 103 ���K�����p
    (4, 4),  -- �N placeId �� 4 ���a�I�P postId �� 104 ���K�����p
    (5, 5);  -- �N placeId �� 5 ���a�I�P postId �� 105 ���K�����p
Go

-- post_tags
INSERT INTO post_tags (postid, tagId) VALUES
(1, 1), -- My First Post -> Travel
(2, 2); -- Another Post -> Adventure
Go

-- Tags_Members
INSERT INTO Tags_Members (tagsBean_tagId, memberBean_userid) VALUES
(1, 1),  -- ���� Travel �P ���� user1
(2, 2);  -- ���� Adventure �P ���� user2
Go

-- member_place
INSERT INTO myplace (userid, placeId) 
VALUES
(1, 1),  -- ����ID 1 �P �a�IID 101
(1, 2),  -- ����ID 1 �P �a�IID 102
(2, 3),  -- ����ID 2 �P �a�IID 103
(3, 4),  -- ����ID 3 �P �a�IID 104
(4, 5);  -- ����ID 4 �P �a�IID 105
Go

-- eventXplace
INSERT INTO eventXplace (fk_event_id, fk_place_id, place_order, arrival_time, stay_duration, notes)
VALUES
<<<<<<< HEAD
(1, 1, 1, '08:00:00', 120, '���W��F�A�w�ư��[�a��A�����I�C'),
(1, 2, 2, '10:30:00', 90, '���[�a�IB�A�]�t�@�p�ɪ������C'),
(1, 3, 3, '13:00:00', 150, '���\�M�𮧮ɶ��A�w�Ʀb�a�IC���\�U�C');
=======
(1, 1, 1, '08:00:00', '09:00:00', '���W��F�A�w�ư��[�a��A�����I�C'),
(1, 2, 2, '10:30:00', '11:30:00', '���[�a�IB�A�]�t�@�p�ɪ������C'),
(1, 3, 3, '13:00:00', '14:00:00', '���\�M�𮧮ɶ��A�w�Ʀb�a�IC���\�U�C');
>>>>>>> origin/dev
Go