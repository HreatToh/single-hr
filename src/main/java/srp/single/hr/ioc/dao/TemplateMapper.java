package srp.single.hr.ioc.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Mapper
@Component
public interface TemplateMapper {

    @Select("select * from hr_template_info where template_id = #{templateId}")
    Map<String, Object> getTemplateInfo(@Param("templateId") String templateId) throws Exception;

    @Select("select * from hr_template_column where template_id = #{templateId} and column_enable = 'Y' order by column_order asc")
    List<Map<String, String>> getTemplateColumnList(String tempateId) throws Exception;
}
