package comlisanshuxue.lisan.Mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ClassMapper {
    @Insert("insert into class(classname,teacher) values(#{classname},#{teacher})")
    public int addclass(@Param("classname")String classname,@Param("teacher") String teacher);

    @Select("Select classname from class where teacher = #{teacher}")
    public List<Map<String,String>> getclass(@Param("teacher") String teacher);

    @Insert("insert into class_student(classname,studentname) values(#{classname},#{studentname})")
    public int addstudent(@Param("classname") String classname,@Param("studentname") String studentname);

    @Select("Select studentname from class_student where classname = #{classname}")
    public List<Map<String,String>> getstudent(@Param("classname") String classname);
}
