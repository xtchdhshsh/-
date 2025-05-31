package comlisanshuxue.lisan.Mapper;

import lombok.Data;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface ClassMapper {
    @Insert("insert into class(classname,teacher,class) values(#{classname},#{teacher},#{cl})")
    public int addclass(@Param("classname")String classname,@Param("teacher") String teacher,@Param("cl") String cl);

    @Select("Select classname,class from class where teacher = #{teacher}")
    public List<Map<String,String>> getclass(@Param("teacher") String teacher);

    @Delete( "delete from class where classname = #{classname} and teacher = #{teacher} and class = #{cl}")
     public int delclass(@Param("classname") String classname,@Param("teacher") String teacher,@Param("cl") String cl);

    @Delete("delete from class_student where classname = #{classname} and teachername = #{teacher} and class = #{cl}")
    public int delstudent2(@Param("classname") String classname,@Param("teacher") String teacher,@Param("cl") String cl);

    @Insert("insert into class_student(classname,studentname,teachername,class) values(#{classname},#{studentname},#{teacher},#{cl})")
    public int addstudent(@Param("classname") String classname,@Param("studentname") String studentname,@Param("teacher") String teacher,@Param("cl") String cl);

    @Select("Select studentname from class_student where classname = #{classname} and teachername = #{teacher} and class = #{cl}")
    public List<Map<String,String>> getstudent(@Param("classname") String classname,@Param("teacher") String teacher,@Param("cl") String cl);

    @Delete("delete from class_student where classname = #{classname} and studentname = #{studentname} and teachername = #{teacher} and class = #{cl}")
     public int delstudent(@Param("classname") String classname,@Param("studentname") String studentname,@Param("teacher") String teacher,@Param("cl") String cl);

    @Update("UPDATE class set classname = #{newclassname} where classname = #{oldclassname} and teacher = #{teacher} and class = #{cl}")
    public int updateclassname(@Param("newclassname") String newclassname,@Param("oldclassname") String oldclassname,@Param("teacher") String teacher,@Param("cl") String cl);

    @Update("UPDATE class_student set classname = #{newclassname} where classname = #{oldclassname} and teachername = #{teacher} and `class` = #{cl}")
    public int updateclassname2(@Param("newclassname") String newclassname,@Param("oldclassname") String oldclassname,@Param("teacher") String teacher,@Param("cl") String cl);


}
