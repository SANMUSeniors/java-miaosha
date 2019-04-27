package com.linbin.miaosha.dao;

import com.linbin.miaosha.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserDao {
    @Select("select *form user where id = #{id}")
    public User getByld(@Param("id") int id);
    @Insert("insert into user(id,name)values(#{id},#{name}")
    public int insert(User user);

}
