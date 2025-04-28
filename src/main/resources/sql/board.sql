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
