/****** 建置chufa資料庫******/
/*CREATE DATABASE [chufa]*/

USE [chufa]
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[calendar](
	[date] [date] NOT NULL,
	[description] [varchar](255) NULL,
	[isHoliday] [bit] NULL,
	[week] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[date] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[city]    Script Date: 2025/1/11 下午 09:06:02 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[city](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[cityname] [varchar](255) NOT NULL,
	[district] [varchar](255) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[comments]    Script Date: 2025/1/11 下午 09:06:02 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[comments](
	[commentId] [int] IDENTITY(1,1) NOT NULL,
	[comment_created_at] [datetime2](6) NOT NULL,
	[commentstate] [int] NOT NULL,
	[comment_updated_at] [datetime2](6) NULL,
	[content] [varchar](max) NOT NULL,
	[parentid] [int] NULL,
	[postid] [int] NOT NULL,
	[user_id] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[commentId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[coupon]    Script Date: 2025/1/11 下午 09:06:02 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[coupon](
	[couponid] [int] NOT NULL,
	[content] [varchar](255) NULL,
	[endtime] [int] NULL,
	[picture] [varchar](255) NULL,
	[remaining] [varchar](255) NULL,
	[secondtitle] [varchar](255) NULL,
	[starttime] [int] NULL,
	[state] [varchar](255) NULL,
	[title] [varchar](255) NULL,
	[web] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[couponid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[event]    Script Date: 2025/1/11 下午 09:06:02 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[event](
	[event_content_id] [bigint] IDENTITY(1,1) NOT NULL,
	[end_date] [date] NOT NULL,
	[notes] [varchar](255) NULL,
	[start_date] [date] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[event_content_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[followlist]    Script Date: 2025/1/11 下午 09:06:02 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[followlist](
	[flid] [bigint] IDENTITY(1,1) NOT NULL,
	[followStatus] [varchar](255) NULL,
	[followTime] [datetime2](6) NULL,
PRIMARY KEY CLUSTERED 
(
	[flid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[interaction]    Script Date: 2025/1/11 下午 09:06:02 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[interaction](
	[actionId] [bigint] IDENTITY(1,1) NOT NULL,
	[interaction_time] [datetime2](6) NULL,
	[interaction_type] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[actionId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[members]    Script Date: 2025/1/11 下午 09:06:02 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[members](
	[userid] [bigint] IDENTITY(1,1) NOT NULL,
	[bio] [text] NULL,
	[birth] [datetime2](6) NULL,
	[email] [varchar](255) NOT NULL,
	[gender] [varchar](255) NOT NULL,
	[name] [varchar](20) NOT NULL,
	[nickname] [varchar](50) NULL,
	[password] [varbinary](255) NULL,
	[phone_number] [varchar](255) NOT NULL,
	[profile_picture] [varbinary](max) NULL,
	[role] [varchar](255) NOT NULL,
	[username] [varchar](255) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[userid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UK99xbxdwmyun0ehfiwpbntlqs5] UNIQUE NONCLUSTERED 
(
	[phone_number] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UK9d30a9u1qpg8eou0otgkwrp5d] UNIQUE NONCLUSTERED 
(
	[email] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UKlj4daw762ura5d2y6iu7g5n1i] UNIQUE NONCLUSTERED 
(
	[username] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[place]    Script Date: 2025/1/11 下午 09:06:02 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[place](
	[placeId] [int] IDENTITY(1,1) NOT NULL,
	[accommodationType] [varchar](255) NULL,
	[bookingUrl] [varchar](255) NULL,
	[businessHours] [varchar](255) NULL,
	[latitude] [float] NOT NULL,
	[longitude] [float] NOT NULL,
	[mealTime] [varchar](255) NULL,
	[placeAddress] [varchar](255) NULL,
	[placeImage] [varchar](255) NULL,
	[placeInfo] [varchar](255) NULL,
	[placeName] [varchar](255) NULL,
	[placePhone] [varchar](255) NULL,
	[placeType] [varchar](255) NULL,
	[price] [numeric](38, 2) NULL,
	[reservation] [varchar](255) NULL,
	[website] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[placeId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[post]    Script Date: 2025/1/11 下午 09:06:02 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[post](
	[postid] [bigint] IDENTITY(1,1) NOT NULL,
	[postContent] [varchar](max) NULL,
	[postLink] [varchar](255) NULL,
	[postStatus] [varchar](255) NULL,
	[postTime] [datetime2](6) NULL,
	[postTitle] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[postid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[schedule]    Script Date: 2025/1/11 下午 09:06:02 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[schedule](
	[trip_id] [bigint] IDENTITY(1,1) NOT NULL,
	[cover_photo] [varchar](255) NULL,
	[end_date] [date] NOT NULL,
	[start_date] [date] NOT NULL,
	[trip_name] [varchar](255) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[trip_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Tags]    Script Date: 2025/1/11 下午 09:06:02 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Tags](
	[tagId] [int] IDENTITY(1,1) NOT NULL,
	[tag_created_at] [datetime2](6) NOT NULL,
	[tag_name] [varchar](255) NOT NULL,
	[tag_state] [int] NOT NULL,
	[tag_updated_at] [datetime2](6) NULL,
PRIMARY KEY CLUSTERED 
(
	[tagId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[members]  WITH CHECK ADD CHECK  (([role]='USER' OR [role]='ADMIN'))
GO
