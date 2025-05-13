package com.miniproj.exampletx;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TransactionExMapper {

  @Insert("insert into table_a values(#{id}, #{name})")
  int insertTablaA(@Param("id") int id, @Param("name") String name);

  @Insert("insert into table_b values(#{id}, #{name})")
  int insertTablaB(@Param("id") int id, @Param("name") String name);

}
