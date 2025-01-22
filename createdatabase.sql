USE [master]
GO
/****** Object:  Database [chufa]    Script Date: 2025/1/22 下午 03:44:44 ******/
CREATE DATABASE [chufa]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'chufa', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER\MSSQL\DATA\chufa.mdf' , SIZE = 8192KB , MAXSIZE = UNLIMITED, FILEGROWTH = 65536KB )
 LOG ON 
( NAME = N'chufa_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER\MSSQL\DATA\chufa_log.ldf' , SIZE = 139264KB , MAXSIZE = 2048GB , FILEGROWTH = 65536KB )
 WITH CATALOG_COLLATION = DATABASE_DEFAULT, LEDGER = OFF
GO
ALTER DATABASE [chufa] SET COMPATIBILITY_LEVEL = 160
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [chufa].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [chufa] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [chufa] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [chufa] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [chufa] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [chufa] SET ARITHABORT OFF 
GO
ALTER DATABASE [chufa] SET AUTO_CLOSE OFF 
GO
ALTER DATABASE [chufa] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [chufa] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [chufa] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [chufa] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [chufa] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [chufa] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [chufa] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [chufa] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [chufa] SET  DISABLE_BROKER 
GO
ALTER DATABASE [chufa] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [chufa] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [chufa] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [chufa] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [chufa] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [chufa] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [chufa] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [chufa] SET RECOVERY FULL 
GO
ALTER DATABASE [chufa] SET  MULTI_USER 
GO
ALTER DATABASE [chufa] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [chufa] SET DB_CHAINING OFF 
GO
ALTER DATABASE [chufa] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [chufa] SET TARGET_RECOVERY_TIME = 60 SECONDS 
GO
ALTER DATABASE [chufa] SET DELAYED_DURABILITY = DISABLED 
GO
ALTER DATABASE [chufa] SET ACCELERATED_DATABASE_RECOVERY = OFF  
GO
EXEC sys.sp_db_vardecimal_storage_format N'chufa', N'ON'
GO
ALTER DATABASE [chufa] SET QUERY_STORE = ON
GO
ALTER DATABASE [chufa] SET QUERY_STORE (OPERATION_MODE = READ_WRITE, CLEANUP_POLICY = (STALE_QUERY_THRESHOLD_DAYS = 30), DATA_FLUSH_INTERVAL_SECONDS = 900, INTERVAL_LENGTH_MINUTES = 60, MAX_STORAGE_SIZE_MB = 1000, QUERY_CAPTURE_MODE = AUTO, SIZE_BASED_CLEANUP_MODE = AUTO, MAX_PLANS_PER_QUERY = 200, WAIT_STATS_CAPTURE_MODE = ON)
GO
USE [chufa]
GO
/****** Object:  Table [dbo].[calendar]    Script Date: 2025/1/22 下午 03:44:44 ******/
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
/****** Object:  Table [dbo].[comments]    Script Date: 2025/1/22 下午 03:44:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[comments](
	[commentId] [bigint] IDENTITY(1,1) NOT NULL,
	[comment_created_at] [datetime2](6) NOT NULL,
	[commentstate] [varchar](255) NOT NULL,
	[comment_updated_at] [datetime2](6) NULL,
	[content] [varchar](max) NOT NULL,
	[parentid] [bigint] NULL,
	[user_id] [bigint] NOT NULL,
	[postid] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[commentId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[coupon]    Script Date: 2025/1/22 下午 03:44:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[coupon](
	[couponId] [bigint] IDENTITY(1,1) NOT NULL,
	[Content] [varchar](255) NULL,
	[coupon_code] [varchar](255) NOT NULL,
	[EndTime] [datetime2](6) NULL,
	[Picture] [varchar](255) NULL,
	[remaining] [int] NULL,
	[StartTime] [datetime2](6) NULL,
	[State] [bit] NULL,
	[Subtitle] [varchar](255) NULL,
	[Title] [varchar](255) NULL,
	[Web] [varchar](255) NULL,
	[placeId] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[couponId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UKsre2vcap4vs6qucaksomk3c7s] UNIQUE NONCLUSTERED 
(
	[coupon_code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[event]    Script Date: 2025/1/22 下午 03:44:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[event](
	[event_id] [bigint] IDENTITY(1,1) NOT NULL,
	[end_time] [time](7) NOT NULL,
	[notes] [varchar](255) NULL,
	[start_time] [time](7) NOT NULL,
	[FK_calendar] [date] NOT NULL,
	[FK_schedule] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[event_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[eventXplace]    Script Date: 2025/1/22 下午 03:44:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[eventXplace](
	[Eventmapping_id] [bigint] IDENTITY(1,1) NOT NULL,
	[arrival_time] [time](7) NULL,
	[notes] [varchar](max) NULL,
	[place_order] [int] NULL,
	[stay_duration] [int] NULL,
	[fk_event_id] [bigint] NOT NULL,
	[fk_place_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[Eventmapping_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[followlist]    Script Date: 2025/1/22 下午 03:44:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[followlist](
	[flid] [bigint] IDENTITY(1,1) NOT NULL,
	[followStatus] [varchar](255) NULL,
	[followTime] [datetime2](6) NULL,
	[followed_id] [bigint] NOT NULL,
	[follower_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[flid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[interaction]    Script Date: 2025/1/22 下午 03:44:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[interaction](
	[actionId] [bigint] IDENTITY(1,1) NOT NULL,
	[interaction_time] [datetime2](6) NULL,
	[interction_type] [varchar](255) NULL,
	[member_id] [bigint] NOT NULL,
	[postid] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[actionId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[members]    Script Date: 2025/1/22 下午 03:44:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[members](
	[userid] [bigint] IDENTITY(1,1) NOT NULL,
	[bio] [text] NULL,
	[birth] [datetime2](6) NULL,
	[email] [varchar](255) NOT NULL,
	[gender] [varchar](255) NULL,
	[lineUserId] [varchar](255) NULL,
	[name] [varchar](20) NOT NULL,
	[nickname] [varchar](50) NULL,
	[password] [varbinary](255) NULL,
	[phone_number] [varchar](255) NULL,
	[profile_picture] [varbinary](max) NULL,
	[role] [varchar](255) NULL,
	[username] [varchar](255) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[userid] ASC
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
/****** Object:  Table [dbo].[mycoupon]    Script Date: 2025/1/22 下午 03:44:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[mycoupon](
	[mycoupon_id] [bigint] IDENTITY(1,1) NOT NULL,
	[gettime] [datetime2](6) NULL,
	[fk_couponid] [bigint] NULL,
	[fk_userid] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[mycoupon_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[myplace]    Script Date: 2025/1/22 下午 03:44:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[myplace](
	[userid] [bigint] NOT NULL,
	[placeId] [bigint] NOT NULL
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[place]    Script Date: 2025/1/22 下午 03:44:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[place](
	[placeId] [bigint] IDENTITY(1,1) NOT NULL,
	[accommodationType] [varchar](255) NULL,
	[bookingUrl] [varchar](255) NULL,
	[businessHours] [varchar](max) NULL,
	[city] [varchar](255) NULL,
	[googlemapPlaceId] [varchar](255) NULL,
	[isClosed] [bit] NOT NULL,
	[latitude] [float] NOT NULL,
	[longitude] [float] NOT NULL,
	[photos] [varchar](max) NULL,
	[placeAddress] [varchar](255) NULL,
	[placeInfo] [varchar](255) NULL,
	[placeName] [varchar](255) NULL,
	[placePhone] [varchar](255) NULL,
	[placeType] [varchar](255) NULL,
	[priceLevel] [int] NULL,
	[rating] [float] NULL,
	[region] [varchar](255) NULL,
	[reservation] [bit] NOT NULL,
	[website] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[placeId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[placewithposts]    Script Date: 2025/1/22 下午 03:44:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[placewithposts](
	[fk_Place_Id] [bigint] NOT NULL,
	[fk_Post_Id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[fk_Place_Id] ASC,
	[fk_Post_Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[post]    Script Date: 2025/1/22 下午 03:44:44 ******/
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
	[userid] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[postid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[post_tags]    Script Date: 2025/1/22 下午 03:44:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[post_tags](
	[postid] [bigint] NOT NULL,
	[tagId] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[postid] ASC,
	[tagId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[schedule]    Script Date: 2025/1/22 下午 03:44:44 ******/
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
	[FK_user] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[trip_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[tags]    Script Date: 2025/1/22 下午 03:44:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[tags](
	[tagId] [bigint] IDENTITY(1,1) NOT NULL,
	[tag_created_at] [datetime2](6) NOT NULL,
	[tag_name] [varchar](255) NOT NULL,
	[tag_state] [varchar](255) NOT NULL,
	[tag_updated_at] [datetime2](6) NULL,
PRIMARY KEY CLUSTERED 
(
	[tagId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UK2c6s9hekidseaj5vbgb3pgy3k] UNIQUE NONCLUSTERED 
(
	[tag_name] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[tags_members]    Script Date: 2025/1/22 下午 03:44:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[tags_members](
	[tagsBean_tagId] [bigint] NOT NULL,
	[memberBean_userid] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[tagsBean_tagId] ASC,
	[memberBean_userid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UK99xbxdwmyun0ehfiwpbntlqs5]    Script Date: 2025/1/22 下午 03:44:44 ******/
CREATE UNIQUE NONCLUSTERED INDEX [UK99xbxdwmyun0ehfiwpbntlqs5] ON [dbo].[members]
(
	[phone_number] ASC
)
WHERE ([phone_number] IS NOT NULL)
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
ALTER TABLE [dbo].[comments]  WITH CHECK ADD  CONSTRAINT [fk_comments_member] FOREIGN KEY([user_id])
REFERENCES [dbo].[members] ([userid])
GO
ALTER TABLE [dbo].[comments] CHECK CONSTRAINT [fk_comments_member]
GO
ALTER TABLE [dbo].[comments]  WITH CHECK ADD  CONSTRAINT [fk_comments_post] FOREIGN KEY([postid])
REFERENCES [dbo].[post] ([postid])
GO
ALTER TABLE [dbo].[comments] CHECK CONSTRAINT [fk_comments_post]
GO
ALTER TABLE [dbo].[coupon]  WITH CHECK ADD  CONSTRAINT [FKs4vpe0ajv51wb2vtr8ur75qgs] FOREIGN KEY([placeId])
REFERENCES [dbo].[place] ([placeId])
GO
ALTER TABLE [dbo].[coupon] CHECK CONSTRAINT [FKs4vpe0ajv51wb2vtr8ur75qgs]
GO
ALTER TABLE [dbo].[event]  WITH CHECK ADD  CONSTRAINT [FK3kcqil5gg1c1hio9gjhtf9io6] FOREIGN KEY([FK_calendar])
REFERENCES [dbo].[calendar] ([date])
GO
ALTER TABLE [dbo].[event] CHECK CONSTRAINT [FK3kcqil5gg1c1hio9gjhtf9io6]
GO
ALTER TABLE [dbo].[event]  WITH CHECK ADD  CONSTRAINT [FKcdqpyhal6mv5p3pdkqre44sd6] FOREIGN KEY([FK_schedule])
REFERENCES [dbo].[schedule] ([trip_id])
GO
ALTER TABLE [dbo].[event] CHECK CONSTRAINT [FKcdqpyhal6mv5p3pdkqre44sd6]
GO
ALTER TABLE [dbo].[eventXplace]  WITH CHECK ADD  CONSTRAINT [FK97qtoug39i4shd4eid1ke5he6] FOREIGN KEY([fk_place_id])
REFERENCES [dbo].[place] ([placeId])
GO
ALTER TABLE [dbo].[eventXplace] CHECK CONSTRAINT [FK97qtoug39i4shd4eid1ke5he6]
GO
ALTER TABLE [dbo].[eventXplace]  WITH CHECK ADD  CONSTRAINT [FKak4xj1nevxndw6h78issipri3] FOREIGN KEY([fk_event_id])
REFERENCES [dbo].[event] ([event_id])
GO
ALTER TABLE [dbo].[eventXplace] CHECK CONSTRAINT [FKak4xj1nevxndw6h78issipri3]
GO
ALTER TABLE [dbo].[followlist]  WITH CHECK ADD  CONSTRAINT [FKc4pth9omw88slrjyl7kkqhrj8] FOREIGN KEY([follower_id])
REFERENCES [dbo].[members] ([userid])
GO
ALTER TABLE [dbo].[followlist] CHECK CONSTRAINT [FKc4pth9omw88slrjyl7kkqhrj8]
GO
ALTER TABLE [dbo].[followlist]  WITH CHECK ADD  CONSTRAINT [FKkrqfhuas80mxg018y8c8i284g] FOREIGN KEY([followed_id])
REFERENCES [dbo].[members] ([userid])
GO
ALTER TABLE [dbo].[followlist] CHECK CONSTRAINT [FKkrqfhuas80mxg018y8c8i284g]
GO
ALTER TABLE [dbo].[interaction]  WITH CHECK ADD  CONSTRAINT [FK5b220faa2fmu749vlketayr7e] FOREIGN KEY([member_id])
REFERENCES [dbo].[members] ([userid])
GO
ALTER TABLE [dbo].[interaction] CHECK CONSTRAINT [FK5b220faa2fmu749vlketayr7e]
GO
ALTER TABLE [dbo].[interaction]  WITH CHECK ADD  CONSTRAINT [FKbpn4kaj576hhsbkfgo6007ksn] FOREIGN KEY([postid])
REFERENCES [dbo].[post] ([postid])
GO
ALTER TABLE [dbo].[interaction] CHECK CONSTRAINT [FKbpn4kaj576hhsbkfgo6007ksn]
GO
ALTER TABLE [dbo].[mycoupon]  WITH CHECK ADD  CONSTRAINT [FK3vg3khqytvxpciskg86m16en5] FOREIGN KEY([mycoupon_id])
REFERENCES [dbo].[mycoupon] ([mycoupon_id])
GO
ALTER TABLE [dbo].[mycoupon] CHECK CONSTRAINT [FK3vg3khqytvxpciskg86m16en5]
GO
ALTER TABLE [dbo].[mycoupon]  WITH CHECK ADD  CONSTRAINT [FKcglm2oachj9t8mdqfaxary2um] FOREIGN KEY([fk_userid])
REFERENCES [dbo].[members] ([userid])
GO
ALTER TABLE [dbo].[mycoupon] CHECK CONSTRAINT [FKcglm2oachj9t8mdqfaxary2um]
GO
ALTER TABLE [dbo].[mycoupon]  WITH CHECK ADD  CONSTRAINT [FKnob6f3pl42axxbe0lhber91s9] FOREIGN KEY([fk_couponid])
REFERENCES [dbo].[coupon] ([couponId])
GO
ALTER TABLE [dbo].[mycoupon] CHECK CONSTRAINT [FKnob6f3pl42axxbe0lhber91s9]
GO
ALTER TABLE [dbo].[myplace]  WITH CHECK ADD  CONSTRAINT [FKkxf1t74ra8cotr94w55babopd] FOREIGN KEY([placeId])
REFERENCES [dbo].[place] ([placeId])
GO
ALTER TABLE [dbo].[myplace] CHECK CONSTRAINT [FKkxf1t74ra8cotr94w55babopd]
GO
ALTER TABLE [dbo].[myplace]  WITH CHECK ADD  CONSTRAINT [FKqftwj95usd2999csspx7qc8l4] FOREIGN KEY([userid])
REFERENCES [dbo].[members] ([userid])
GO
ALTER TABLE [dbo].[myplace] CHECK CONSTRAINT [FKqftwj95usd2999csspx7qc8l4]
GO
ALTER TABLE [dbo].[placewithposts]  WITH CHECK ADD  CONSTRAINT [placeId] FOREIGN KEY([fk_Place_Id])
REFERENCES [dbo].[place] ([placeId])
GO
ALTER TABLE [dbo].[placewithposts] CHECK CONSTRAINT [placeId]
GO
ALTER TABLE [dbo].[placewithposts]  WITH CHECK ADD  CONSTRAINT [postid] FOREIGN KEY([fk_Post_Id])
REFERENCES [dbo].[post] ([postid])
GO
ALTER TABLE [dbo].[placewithposts] CHECK CONSTRAINT [postid]
GO
ALTER TABLE [dbo].[post]  WITH CHECK ADD  CONSTRAINT [fk_posts_member] FOREIGN KEY([userid])
REFERENCES [dbo].[members] ([userid])
GO
ALTER TABLE [dbo].[post] CHECK CONSTRAINT [fk_posts_member]
GO
ALTER TABLE [dbo].[post_tags]  WITH CHECK ADD  CONSTRAINT [FKbevmctjw3hmtgafuqydd9hy82] FOREIGN KEY([tagId])
REFERENCES [dbo].[tags] ([tagId])
GO
ALTER TABLE [dbo].[post_tags] CHECK CONSTRAINT [FKbevmctjw3hmtgafuqydd9hy82]
GO
ALTER TABLE [dbo].[post_tags]  WITH CHECK ADD  CONSTRAINT [FKhbneyoiffiu8jwvsb6ool4ujk] FOREIGN KEY([postid])
REFERENCES [dbo].[post] ([postid])
GO
ALTER TABLE [dbo].[post_tags] CHECK CONSTRAINT [FKhbneyoiffiu8jwvsb6ool4ujk]
GO
ALTER TABLE [dbo].[schedule]  WITH CHECK ADD  CONSTRAINT [FKgdca640socgalo9m4lxlvky3q] FOREIGN KEY([FK_user])
REFERENCES [dbo].[members] ([userid])
GO
ALTER TABLE [dbo].[schedule] CHECK CONSTRAINT [FKgdca640socgalo9m4lxlvky3q]
GO
ALTER TABLE [dbo].[tags_members]  WITH CHECK ADD  CONSTRAINT [FK3j3tpp1hp2aave07tttxveelg] FOREIGN KEY([tagsBean_tagId])
REFERENCES [dbo].[tags] ([tagId])
GO
ALTER TABLE [dbo].[tags_members] CHECK CONSTRAINT [FK3j3tpp1hp2aave07tttxveelg]
GO
ALTER TABLE [dbo].[tags_members]  WITH CHECK ADD  CONSTRAINT [FKlmyctqcmdmlme2xm8qcpllymf] FOREIGN KEY([memberBean_userid])
REFERENCES [dbo].[members] ([userid])
GO
ALTER TABLE [dbo].[tags_members] CHECK CONSTRAINT [FKlmyctqcmdmlme2xm8qcpllymf]
GO
ALTER TABLE [dbo].[members]  WITH CHECK ADD CHECK  (([role]='USER' OR [role]='ADMIN'))
GO
USE [master]
GO
ALTER DATABASE [chufa] SET  READ_WRITE 
GO
