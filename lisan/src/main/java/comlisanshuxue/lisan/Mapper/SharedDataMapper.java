package comlisanshuxue.lisan.Mapper;

import comlisanshuxue.lisan.entity.SharedData;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface SharedDataMapper {

    // 修改了插入语句
    @Insert("INSERT INTO shared_data(type, content) VALUES(#{type}, #{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SharedData sharedData);

    // 新增了根据id和type的查询方法
    @Select("SELECT * FROM shared_data WHERE id = #{id} AND type = #{type}")
    SharedData selectByIdAndType(@Param("id") Long id, @Param("type") String type);

    // 新增了根据type的查询方法
    @Select("SELECT * FROM shared_data WHERE type = #{type}")
    List<SharedData> selectByType(String type);


    @Select("SELECT * FROM shared_data WHERE id = #{id}")
    SharedData selectById(Long id);


    @Select("SELECT * FROM shared_data ORDER BY id DESC")
    List<SharedData> selectAll();


    @Delete("DELETE FROM shared_data WHERE id = #{id}")
    int deleteById(Long id);
}