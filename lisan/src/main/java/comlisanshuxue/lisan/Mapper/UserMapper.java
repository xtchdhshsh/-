package comlisanshuxue.lisan.Mapper;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Select("select * from user where username = #{username} and password = #{password} and isteacher = 0")
    public String login(@Param("username") String username,@Param("password") String password);

    @Select("select * from user where username = #{username} and password = #{password} and isteacher = 1")
    public String TeacherLogin(@Param("username") String username,@Param("password") String password);

    @Insert("insert into user(username,password,isteacher) values(#{username},#{password},0)")
    public int register(@Param("username") String username,@Param("password") String password);

    @Insert("insert into user(username,password,isteacher) values(#{username},#{password},1)")
    public int TeacherRegister(@Param("username") String username,@Param("password") String password);

    @Insert("insert into user(username,password,isteacher,class,realname) values(#{username},#{username},0,#{cl},#{realname})")
    public int newregister(@Param("realname") String username,@Param("username") String password,@Param("cl") String cl);
}

