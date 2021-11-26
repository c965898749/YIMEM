/*
Navicat MariaDB Data Transfer

Source Server         : 192.168.5.100
Source Server Version : 100329
Source Host           : 192.168.5.100:3307
Source Database       : yimem

Target Server Type    : MariaDB
Target Server Version : 100329
File Encoding         : 65001

Date: 2021-11-26 17:24:25
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for alsoask
-- ----------------------------
DROP TABLE IF EXISTS `alsoask`;
CREATE TABLE `alsoask` (
  `alsoAskId` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `askId` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `userId` varchar(255) CHARACTER SET utf8 DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for answer_ask
-- ----------------------------
DROP TABLE IF EXISTS `answer_ask`;
CREATE TABLE `answer_ask` (
  `answerAskId` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `answerAskText` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `askId` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `userId` varchar(255) DEFAULT NULL,
  `createtime` varchar(255) CHARACTER SET utf8 DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for app
-- ----------------------------
DROP TABLE IF EXISTS `app`;
CREATE TABLE `app` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `creatime` datetime DEFAULT NULL,
  `describe` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `downloadid` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for ask
-- ----------------------------
DROP TABLE IF EXISTS `ask`;
CREATE TABLE `ask` (
  `askId` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `askName` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `askText` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `createTime` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `solve` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `award` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `integralNeed` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `userId` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `type` varchar(255) CHARACTER SET utf8 DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for ask_fav
-- ----------------------------
DROP TABLE IF EXISTS `ask_fav`;
CREATE TABLE `ask_fav` (
  `askFavId` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `askId` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `favoriteId` varchar(255) CHARACTER SET utf8 DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for ask_label
-- ----------------------------
DROP TABLE IF EXISTS `ask_label`;
CREATE TABLE `ask_label` (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `askId` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `labelId` varchar(255) CHARACTER SET utf8 DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for attention
-- ----------------------------
DROP TABLE IF EXISTS `attention`;
CREATE TABLE `attention` (
  `attentionId` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `initiativePeId` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `passivityPeId` varchar(255) CHARACTER SET utf8 DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for blog
-- ----------------------------
DROP TABLE IF EXISTS `blog`;
CREATE TABLE `blog` (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `userid` int(255) DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `content` text CHARACTER SET utf8 DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `readCount` int(255) DEFAULT 0,
  `createtime` datetime DEFAULT NULL,
  `recomment` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `resource` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `lable` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `publishForm` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `category` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `img` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `type` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `stick` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for blog_replay
-- ----------------------------
DROP TABLE IF EXISTS `blog_replay`;
CREATE TABLE `blog_replay` (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `blog_id` int(255) DEFAULT NULL,
  `comment` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `commentuserid` int(255) DEFAULT NULL,
  `time` datetime DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `blog_replay_id` int(11) DEFAULT NULL,
  `replay_userid` int(11) DEFAULT NULL,
  `son_replay_count` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for collect
-- ----------------------------
DROP TABLE IF EXISTS `collect`;
CREATE TABLE `collect` (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `userid` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `collectDescribe` varchar(255) CHARACTER SET utf8 DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for collectitems
-- ----------------------------
DROP TABLE IF EXISTS `collectitems`;
CREATE TABLE `collectitems` (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `blogid` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `uploadID` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `askID` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `forumID` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `collectid` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `createTime` varchar(255) CHARACTER SET utf8 DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for dingding
-- ----------------------------
DROP TABLE IF EXISTS `dingding`;
CREATE TABLE `dingding` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for download
-- ----------------------------
DROP TABLE IF EXISTS `download`;
CREATE TABLE `download` (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `userid` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `dowid` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `createtime` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `replystate` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `title` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `price` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `size` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `leixin2` varchar(255) CHARACTER SET utf8 DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for downloadcategory
-- ----------------------------
DROP TABLE IF EXISTS `downloadcategory`;
CREATE TABLE `downloadcategory` (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `categoryname` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `pid` int(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=176 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for downloadreply
-- ----------------------------
DROP TABLE IF EXISTS `downloadreply`;
CREATE TABLE `downloadreply` (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `userid` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `content` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `createtime` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `appraise` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `dowid` varchar(255) CHARACTER SET utf8 DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for emil
-- ----------------------------
DROP TABLE IF EXISTS `emil`;
CREATE TABLE `emil` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `emil` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for fa_att
-- ----------------------------
DROP TABLE IF EXISTS `fa_att`;
CREATE TABLE `fa_att` (
  `faAttId` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `favoriteId` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `userId` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `collectID` varchar(255) CHARACTER SET utf8 DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for fans
-- ----------------------------
DROP TABLE IF EXISTS `fans`;
CREATE TABLE `fans` (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `fansedid` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `fansid` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `status` varchar(255) CHARACTER SET utf8 DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for favorite
-- ----------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
  `favorite_id` int(255) NOT NULL AUTO_INCREMENT,
  `favorite_name` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `user_id` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `favorite_describe` varchar(255) CHARACTER SET utf8 DEFAULT '',
  PRIMARY KEY (`favorite_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for fir_label
-- ----------------------------
DROP TABLE IF EXISTS `fir_label`;
CREATE TABLE `fir_label` (
  `firLabelId` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `firLabelName` varchar(255) CHARACTER SET utf8 DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for forum
-- ----------------------------
DROP TABLE IF EXISTS `forum`;
CREATE TABLE `forum` (
  `forumId` int(255) NOT NULL AUTO_INCREMENT,
  `forumTitle` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `forumContext` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `forumCreatime` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `userId` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `type` varchar(255) CHARACTER SET utf8 DEFAULT '',
  PRIMARY KEY (`forumId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for forumcategory
-- ----------------------------
DROP TABLE IF EXISTS `forumcategory`;
CREATE TABLE `forumcategory` (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `pid` int(255) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=176 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for information
-- ----------------------------
DROP TABLE IF EXISTS `information`;
CREATE TABLE `information` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `blog_id` int(11) DEFAULT NULL,
  `time` datetime DEFAULT NULL,
  `replay_userid` int(11) DEFAULT NULL,
  `content` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `status` int(1) unsigned zerofill DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for integrals
-- ----------------------------
DROP TABLE IF EXISTS `integrals`;
CREATE TABLE `integrals` (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `price` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `createtime` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `title` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `userid` varchar(255) CHARACTER SET utf8 DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for invitation
-- ----------------------------
DROP TABLE IF EXISTS `invitation`;
CREATE TABLE `invitation` (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `createtime` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `content` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `userid` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `status` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `categoryid` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `categoryid2` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `recomment` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `resource` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `lable` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `publishForm` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `readCount` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `img` varchar(255) CHARACTER SET utf8 DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for invitationreply
-- ----------------------------
DROP TABLE IF EXISTS `invitationreply`;
CREATE TABLE `invitationreply` (
  `id` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for likes
-- ----------------------------
DROP TABLE IF EXISTS `likes`;
CREATE TABLE `likes` (
  `id` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `blog_id` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `userid` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `status` varchar(255) CHARACTER SET utf8 DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for picture_info
-- ----------------------------
DROP TABLE IF EXISTS `picture_info`;
CREATE TABLE `picture_info` (
  `id` varchar(36) CHARACTER SET utf8mb4 NOT NULL COMMENT 'id',
  `directory` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '文件路径',
  `file_name` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '文件名称',
  `from_table` varchar(24) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '来自表格',
  `table_id` varchar(36) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '表格id',
  `pic_type` varchar(4) CHARACTER SET utf8mb4 DEFAULT '1' COMMENT '图片类型',
  `reserv1` varchar(2) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '备用字段1',
  `reserv2` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '备用字段2',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(36) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` varchar(36) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for power
-- ----------------------------
DROP TABLE IF EXISTS `power`;
CREATE TABLE `power` (
  `id` int(11) NOT NULL,
  `userid` int(11) DEFAULT NULL,
  `upload_access_level` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for resocollect
-- ----------------------------
DROP TABLE IF EXISTS `resocollect`;
CREATE TABLE `resocollect` (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `userid` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `dowid` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `createtime` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `replystate` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `title` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `price` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `size` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `leixin` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `leixin2` varchar(255) CHARACTER SET utf8 DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for scan_record
-- ----------------------------
DROP TABLE IF EXISTS `scan_record`;
CREATE TABLE `scan_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `totalAmount` decimal(10,0) DEFAULT NULL,
  `outTradeNo` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `subject` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL,
  `undiscountableAmount` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `sellerId` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `body` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `qrCode` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `userId` int(11) DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_payment` datetime DEFAULT NULL,
  `notify_id` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `buyer_logon_id` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `buyer_id` varchar(255) CHARACTER SET utf8 DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for se_label
-- ----------------------------
DROP TABLE IF EXISTS `se_label`;
CREATE TABLE `se_label` (
  `seLabelId` int(255) NOT NULL AUTO_INCREMENT,
  `seLabelName` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `firLabelId` varchar(255) CHARACTER SET utf8 DEFAULT '',
  PRIMARY KEY (`seLabelId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for specialist
-- ----------------------------
DROP TABLE IF EXISTS `specialist`;
CREATE TABLE `specialist` (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `img` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `name` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `blogcount` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `userId` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `briefintroduction` varchar(255) CHARACTER SET utf8 DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for sys_logininfor
-- ----------------------------
DROP TABLE IF EXISTS `sys_logininfor`;
CREATE TABLE `sys_logininfor` (
  `info_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(255) DEFAULT NULL,
  `ipaddr` varchar(255) DEFAULT NULL,
  `login_location` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `browser` varchar(255) DEFAULT NULL,
  `os` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `msg` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `login_time` datetime DEFAULT NULL,
  PRIMARY KEY (`info_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3427 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for t8_doc_manage
-- ----------------------------
DROP TABLE IF EXISTS `t8_doc_manage`;
CREATE TABLE `t8_doc_manage` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `folder_name` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `port_level` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `parent_id` int(11) DEFAULT NULL,
  `is_directory` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `upd_username` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `upd_date` datetime DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `crt_username` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `crt_date` datetime DEFAULT NULL,
  `crt_time` datetime DEFAULT NULL,
  `readcount` int(11) DEFAULT 0,
  `src` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `size` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `type` varchar(255) CHARACTER SET utf8 DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=134 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for upload
-- ----------------------------
DROP TABLE IF EXISTS `upload`;
CREATE TABLE `upload` (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `userid` int(255) DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `createtime` datetime DEFAULT NULL,
  `appraise` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `size` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `price` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `status` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `downloadCount` int(255) DEFAULT NULL,
  `src` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `intro` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `leixin` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `replyCount` int(255) DEFAULT NULL,
  `categoryid` int(255) DEFAULT NULL,
  `categoryid2` int(255) DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `leixin2` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `hot` int(11) DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=82 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `userId` int(255) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `userpassword` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `sex` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `nickname` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '昵称',
  `birthday` datetime DEFAULT NULL,
  `provinces` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `city` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `county` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `industry` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `job` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `askSuminter` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `headImg` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `blogCount` int(255) DEFAULT 0,
  `attentionCount` int(255) DEFAULT 0,
  `fansCount` int(255) DEFAULT 0,
  `resourceCount` int(255) DEFAULT 0,
  `forumCount` int(255) DEFAULT 0,
  `askCount` int(255) DEFAULT 0,
  `collectCount` int(255) DEFAULT 0,
  `downloadmoney` decimal(10,0) DEFAULT NULL,
  `commentCount` int(255) DEFAULT 0,
  `likeCount` int(255) DEFAULT 0,
  `level` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `visitorCount` int(255) DEFAULT 0,
  `ranking` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `downCount` int(255) DEFAULT 0,
  `askmoney` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `unreadreplaycount` int(255) DEFAULT 0,
  `readquerylikecount` int(255) DEFAULT 0,
  `unreadfanscount` int(255) DEFAULT 0,
  `openid` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `isEmil` int(11) DEFAULT 0,
  PRIMARY KEY (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for video
-- ----------------------------
DROP TABLE IF EXISTS `video`;
CREATE TABLE `video` (
  `videoId` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `subtitle` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `clickCount` int(11) DEFAULT NULL,
  `coverUrl` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `videoUrl` varchar(255) CHARACTER SET utf8 DEFAULT '',
  `state` int(255) DEFAULT NULL,
  `likeCount` int(255) DEFAULT NULL,
  `classifyId` int(11) DEFAULT NULL,
  `createTime` datetime DEFAULT NULL,
  `collectCount` int(255) DEFAULT NULL,
  `actor` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `type` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `region` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `director` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `douban` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `info` varchar(255) CHARACTER SET utf8 DEFAULT '',
  PRIMARY KEY (`videoId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;
