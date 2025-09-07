-- springBootTest.About definition

CREATE TABLE `About` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `imageUrl` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDeleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- springBootTest.Admins definition

CREATE TABLE `Admins` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userName` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `permissions` varchar(255) DEFAULT NULL,
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `remarks` text,
  `isDeleted` tinyint(1) DEFAULT '0',
  `isEnable` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;


-- springBootTest.BasicInformation definition

CREATE TABLE `BasicInformation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `phone` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `address` varchar(255) NOT NULL,
  `weChatImage` varchar(255) DEFAULT NULL,
  `icp` varchar(255) DEFAULT NULL,
  `beianImage` varchar(255) DEFAULT NULL,
  `publicSecurity` varchar(255) DEFAULT NULL,
  `copyright` varchar(255) DEFAULT NULL,
  `isDeleted` tinyint(1) DEFAULT '0',
  `isEnable` tinyint(1) DEFAULT '1',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `homeTitle` varchar(20) DEFAULT NULL,
  `homeDescription` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;


-- springBootTest.BrandAuthorizationCertificates definition

CREATE TABLE `BrandAuthorizationCertificates` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT '证书名称',
  `description` text COMMENT '证书描述',
  `imageUrl` varchar(255) DEFAULT NULL COMMENT '证书图片URL地址',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDeleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;


-- springBootTest.Brands definition

CREATE TABLE `Brands` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` text,
  `logo` varchar(255) DEFAULT NULL,
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDeleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;


-- springBootTest.Businesses definition

CREATE TABLE `Businesses` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `contactInfo` varchar(255) NOT NULL,
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDeleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- springBootTest.Carousels definition

CREATE TABLE `Carousels` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `imageUrl` varchar(255) NOT NULL,
  `redirectUrl` varchar(255) DEFAULT NULL,
  `isEnabled` tinyint(1) DEFAULT '1',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDeleted` tinyint(1) DEFAULT '0',
  `title` varchar(20) DEFAULT NULL,
  `order` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;


-- springBootTest.ProductCategories definition

CREATE TABLE `ProductCategories` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '商品分类ID',
  `name` varchar(255) NOT NULL COMMENT '商品分类名称',
  `description` text COMMENT '商品分类描述',
  `isEnabled` tinyint(1) DEFAULT '1' COMMENT '启用禁用状态',
  `isDeleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='商品分类表';


-- springBootTest.ProductsCarousels definition

CREATE TABLE `ProductsCarousels` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `products` json NOT NULL,
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDeleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- springBootTest.`User` definition

CREATE TABLE `User` (
  `id` int(100) NOT NULL AUTO_INCREMENT,
  `userName` varchar(100) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `created` datetime DEFAULT CURRENT_TIMESTAMP,
  `isEnable` tinyint(1) DEFAULT '1',
  `isDeleted` tinyint(1) DEFAULT '0',
  `wechat_openid` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=473 DEFAULT CHARSET=utf8;


-- springBootTest.Orders definition

CREATE TABLE `Orders` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) DEFAULT NULL,
  `totalPrice` decimal(10,2) NOT NULL,
  `status` varchar(50) DEFAULT NULL,
  `createTime` datetime DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `userId` (`userId`),
  CONSTRAINT `Orders_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `User` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- springBootTest.Products definition

CREATE TABLE `Products` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `name` varchar(255) NOT NULL COMMENT '商品名称',
  `coverImage` varchar(255) DEFAULT NULL COMMENT '商品封面图片',
  `detailImages` json DEFAULT NULL COMMENT '商品详情图片',
  `description` text COMMENT '商品描述',
  `salePrice` decimal(10,2) NOT NULL COMMENT '销售价格',
  `costPrice` decimal(10,2) NOT NULL COMMENT '成本价格',
  `stockQuantity` int(11) NOT NULL COMMENT '库存数量',
  `brand` varchar(255) DEFAULT NULL COMMENT '商品品牌',
  `categoryId` int(11) DEFAULT NULL COMMENT '商品分类ID',
  `isAvailable` tinyint(1) DEFAULT '1' COMMENT '上下架状态',
  `isDeleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `categoryId` (`categoryId`),
  CONSTRAINT `Products_ibfk_1` FOREIGN KEY (`categoryId`) REFERENCES `ProductCategories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='商品信息表';


-- springBootTest.user_location definition

CREATE TABLE `user_location` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `latitude` decimal(9,6) DEFAULT NULL,
  `longitude` decimal(9,6) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `user_location_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `User` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;