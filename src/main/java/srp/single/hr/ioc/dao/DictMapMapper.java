package srp.single.hr.ioc.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Mapper
@Component
public interface DictMapMapper {

    @Select(" select user_name as DICTKEY,user_cname as DICTVALUE from hr_user_info ")
    List<Map<String, String>> getUserNameMap() throws Exception;


    @Select(" select dict_id as DICTKEY,dict_name as DICTVALUE from hr_dict_info where dict_type = #{dictType} and dict_enable = 'Y' order by dict_order asc ")
    List<Map<String, String>> getDict(@Param("dictType") String dictType) throws Exception;


    @Select(" select concat(concat(dict_type,'.'),dict_id) as DICTKEY,dict_name as DICTVALUE from hr_dict_info where is_sys='Y' and dict_enable = 'Y' order by dict_order asc ")
    List<Map<String, String>> getConfigMap() throws Exception;
}
