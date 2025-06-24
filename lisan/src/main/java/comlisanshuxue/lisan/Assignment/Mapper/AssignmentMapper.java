package comlisanshuxue.lisan.Assignment.Mapper;

import comlisanshuxue.lisan.Assignment.entity.Assignment;
import comlisanshuxue.lisan.Assignment.entity.AssignmentProblem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AssignmentMapper {

    @Select("SELECT assignment_id as assignmentId, title, description, created_at as createdAt, deadline, classname " +
            "FROM assignment WHERE (#{classname} IS NULL OR classname = #{classname}) " +
            "ORDER BY assignment_id DESC LIMIT #{offset}, #{pageSize}")
    List<Assignment> selectAssignments(@Param("classname") String classname, @Param("offset") int offset, @Param("pageSize") Integer pageSize);

    @Select("SELECT COUNT(*) FROM assignment WHERE (#{classname} IS NULL OR classname = #{classname})")
    long countAssignments(@Param("classname") String classname);

    @Select("SELECT id, assignment_id as assignmentId, problem_id as problemId, points " +
            "FROM assignment_problem WHERE assignment_id = #{assignmentId}")
    List<AssignmentProblem> selectProblemIds(@Param("assignmentId") Long assignmentId);

    @Select("SELECT assignment_id as assignmentId, title, description, created_at as createdAt, deadline, classname " +
            "FROM assignment WHERE assignment_id = #{id}")
    Assignment selectById(Long id);

    @Options(useGeneratedKeys = true, keyProperty = "assignmentId", keyColumn = "assignment_id")
    @Insert("INSERT INTO assignment (title, description, classname, created_at, deadline) VALUES (#{title}, #{description}, #{classname}, #{createdAt}, #{deadline})")
    void insert(Assignment assignment);

    @Update("UPDATE assignment SET title = #{title}, description = #{description}, " +
            "classname = #{classname}, deadline = #{deadline} WHERE assignment_id = #{assignmentId}")
    Integer update(Assignment assignment);

    @Delete("DELETE FROM assignment WHERE assignment_id = #{id}")
    Integer delete(@Param("id") Long id);

    @Select("SELECT assignment_id AS assignmentId, title, description, created_at AS createdAt, deadline, classname" +
            " FROM assignment WHERE (#{classname} IS NULL OR classname = #{classname})")
    List<Assignment> selectByCondition(String classname);
}
