package comlisanshuxue.lisan.Mapper;

import comlisanshuxue.lisan.entity.SharedData;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface SharedDataMapper {
    @Insert("INSERT INTO shared_data(content) VALUES(#{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SharedData sharedData);


    @Select("SELECT * FROM shared_data WHERE id = #{id}")
    SharedData selectById(Long id);


    @Select("SELECT * FROM shared_data ORDER BY id DESC")
    List<SharedData> selectAll();


    @Delete("DELETE FROM shared_data WHERE id = #{id}")
    int deleteById(Long id);
}