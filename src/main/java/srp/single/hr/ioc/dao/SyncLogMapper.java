package srp.single.hr.ioc.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Mapper
@Component
public interface SyncLogMapper {

    @Select("select * from hr_sync_log_info where thread_id = #{threadId} and thread_level in (${level}) order by thread_time asc")
    List<Map<String, String>> getSyncLogList(Map<String, String> params) throws Exception;

    @Insert("insert into hr_sync_log_info values(#{threadId},#{level},#{time},#{msg},#{progress})")
    int log(String threadId, String level,String time, String msg, String progress) throws Exception;
}
