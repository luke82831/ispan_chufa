USE [chufa]
GO

/****** 建立city資料表******/
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

/****** 建麗景點資料表******/
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



