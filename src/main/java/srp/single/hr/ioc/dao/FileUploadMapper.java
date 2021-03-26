package srp.single.hr.ioc.dao;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.Map;

@Mapper
@Component
public interface FileUploadMapper {

    @Update("update hr_template_info set template_file=#{templateFile} where template_id = #{templateId}")
    int uploadTemplate(Map<String, Object> params) throws Exception;

    @Select("select * from hr_template_info where template_id = #{templateId}")
    Map<String, Object> downloadTemplate(Map<String, Object> params) throws Exception;

    @Update("update hr_calc_avg_file set file=#{file} where data_date = #{dataDate}")
    int uploadCalcAvg(Map<String, Object> params) throws Exception;

    @Insert("insert into hr_calc_avg_file(data_date,file) values(#{dataDate},#{file})")
    int saveCalcAvg(Map<String, Object> params) throws Exception;

    @Delete("delete from hr_calc_avg_file where data_date = #{dataDate}")
    int delCalcAvg(Map<String, Object> params) throws Exception;


    @Select("select * from hr_calc_avg_file where data_date = #{dataDate}")
    Map<String, Object> queryCalcAvg(Map<String, Object> params) throws Exception;
}
