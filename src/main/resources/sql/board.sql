-- member 테이블 생성
CREATE TABLE `member` (
  `memberId` varchar(8) NOT NULL,
  `memberPwd` varchar(200) NOT NULL,
  `memberName` varchar(12) DEFAULT NULL,
  `mobile` varchar(13) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `registerDate` datetime DEFAULT CURRENT_TIMESTAMP,
  `memberImg` varchar(100) NOT NULL DEFAULT 'avatar.png',
  `memberPoint` int DEFAULT '100',
  `gender` varchar(1) NOT NULL DEFAULT 'U',
  PRIMARY KEY (`memberId`),
  UNIQUE KEY `mobile_UNIQUE` (`mobile`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 회원저장
use kjw;
insert into member(memberId, memberPwd, memberName, mobile, email)
	values ('hong1234', sha1(md5('1234')), '홍길동', '010-1111-2222', 'hong1234@naver.com');

-- 계층형 게시판 테이블 생성
CREATE TABLE `kjw`.`hboard` (
  `boardNo` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(20) NOT NULL,
  `content` VARCHAR(2000) NULL,
  `writer` VARCHAR(8) NULL,
  `postDate` DATETIME NULL DEFAULT now(),
  `readCount` INT NULL DEFAULT 0,
  `ref` INT NULL DEFAULT 0,
  `step` INT NULL DEFAULT 0,
  `refOrder` INT NULL DEFAULT 0,
  PRIMARY KEY (`boardNo`),
  INDEX `fk_hboard_member_idx` (`writer` ASC) VISIBLE,
  CONSTRAINT `fk_hboard_member`
    FOREIGN KEY (`writer`)
    REFERENCES `kjw`.`member` (`memberId`)
    ON DELETE SET NULL
    ON UPDATE NO ACTION)
COMMENT = '계층형 게시판';

-- 글 등록
insert into hboard(title, content, writer)
	values('게시판 생성', '많은 이용 바랍니다', 'hong1234'); 



CREATE TABLE `boardupfiles` (
  `fileNo` int NOT NULL,
  `originalFileName` varchar(100) NOT NULL,
  `newFileName` varchar(150) NOT NULL,
  `thumbFileName` varchar(150) DEFAULT NULL,
  `isImage` tinyint DEFAULT '0',
  `ext` varchar(20) DEFAULT NULL,
  `size` bigint DEFAULT NULL,
  `boardNo` int DEFAULT NULL,
  `base64` longtext,
  `filePath` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`fileNo`),
  KEY `fk_upfiles_hboard_idx` (`boardNo`),
  CONSTRAINT `fk_upfiles_hboard` FOREIGN KEY (`boardNo`) REFERENCES `hboard` (`boardNo`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='게시글에 업로드되는 파일정보'


-- 업로드 파일 저장
-- insert into boardUpfiles (boardNo, originalFileName, newFileName, thumbFileName, isImage, ext, size, base64, filePath) values (#{boardNo}, #{originalFileName}, #{newFileName}, #{thumbFileName}, #{isImage}, #{ext}, #{size}, #{base64}, #{filePath});
CREATE TABLE `comment` (
                           `commentNo` int NOT NULL AUTO_INCREMENT,
                           `commenter` varchar(8) DEFAULT NULL,
                           `content` varchar(500) DEFAULT NULL,
                           `regDate` datetime DEFAULT CURRENT_TIMESTAMP,
                           `boardNo` int DEFAULT NULL,
                           PRIMARY KEY (`commentNo`),
                           KEY `fk_comm-mem_idx` (`commenter`),
                           KEY `fk_comm_hboard_idx` (`boardNo`),
                           CONSTRAINT `fk_comm-mem` FOREIGN KEY (`commenter`) REFERENCES `member` (`memberId`) ON DELETE CASCADE,
                           CONSTRAINT `fk_comm_hboard` FOREIGN KEY (`boardNo`) REFERENCES `hboard` (`boardNo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci