package comlisanshuxue.lisan.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ExperimentMapper {
    @Select("select experiment from experiment_teacher where teacher = #{teacher}")
    List<String> getexperiment(String teacher);

    @Select("select student,mark,class from experiment,user where experiment.student=user.username and experiment.teacher=#{teacher} and experiment.experiment=#{experiment}")
    List<Map<String,String>> getmark(@Param("teacher") String teacher,@Param("experiment") String experiment);
}
