package comlisanshuxue.lisan.Assignment.Mapper;

import comlisanshuxue.lisan.Assignment.entity.AssignmentCreateDTO;
import comlisanshuxue.lisan.Assignment.entity.AssignmentProblem;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

@Mapper
public interface AssignmentProblemMapper {

    void batchInsert(@Param("assignmentId") Long assignmentId,
                     @Param("problems") List<AssignmentCreateDTO.ProblemDTO> problems);

    @Delete("DELETE FROM assignment_problem WHERE assignment_id = #{assignmentId}")
    void deleteByAssignmentId(@Param("assignmentId") Long id);

    @Select("SELECT id, assignment_id as assignmentId, problem_id as problemId, points " +
            "FROM assignment_problem WHERE assignment_id = #{assignmentId}")
    List<AssignmentProblem> findByAssignmentId(@Param("assignmentId") Long assignmentId);

    List<AssignmentProblem> findByAssignmentIds(@Param("assignmentIds") Set<Long> assignmentIds);
}
