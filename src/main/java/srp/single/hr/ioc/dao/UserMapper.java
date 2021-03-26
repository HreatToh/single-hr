package srp.single.hr.ioc.dao;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.Map;

@Mapper
@Component
public interface UserMapper {

    @Select("select t.* from hr_user_info t where t.user_name = #{username}")
    Map<String, String> getUserById(@Param("username") String username) throws Exception;

}
