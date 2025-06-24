package comlisanshuxue.lisan.Assignment.Mapper;

import comlisanshuxue.lisan.entity.SubmitRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SubmitRecordMapper1 {

    Integer countSubmittedStudents(@Param("classname") String classname, @Param("problemIds") List<Long> problemIds);

    Integer countDistinctStudentsByProblems(@Param("classname") String classname, @Param("problemIds") List<Long> problemIds);

    List<SubmitRecord> findLastSubmitsForAssignment(@Param("problemIds") List<Long> problemIds);
}
