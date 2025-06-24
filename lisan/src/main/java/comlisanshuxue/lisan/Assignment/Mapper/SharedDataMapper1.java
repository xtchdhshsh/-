package comlisanshuxue.lisan.Assignment.Mapper;

import comlisanshuxue.lisan.entity.SharedData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SharedDataMapper1 {

    List<SharedData> findByIds(@Param("ids") List<Long> problemIds);
}
