package comlisanshuxue.lisan.Assignment.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


import java.util.List;

@Mapper
public interface ClassStudentMapper {

    @Select("SELECT COUNT(DISTINCT studentname) FROM class_student WHERE classname = #{classname}")
    Integer countStudentsByClass(@Param("classname") String classname);

    @Select("SELECT DISTINCT classname FROM class_student")
    List<String> findAllClassNames();

    @Select("SELECT studentname FROM class_student WHERE classname = #{classname}")
    List<String> listStudentIdsByClass(String classname);
}
