package com.ryuukee.mybatislearn;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@org.apache.ibatis.annotations.Mapper
public interface MapperStu {
  @Insert("insert into student_info(name,password,gender,birth_place)"+
      "values(#{name},#{password},#{gender},#{birthPlace})")
    Integer insert(StudentInfo bean);

    @Select("select * from student_info")
    List<StudentInfo> select();

    @Delete("delete from student_info where id = #{id}")
    Integer delete(Integer id);

    @Update("update student_info set password = #{password} where name = #{name} ")
    Integer update(String password,String name);

}


