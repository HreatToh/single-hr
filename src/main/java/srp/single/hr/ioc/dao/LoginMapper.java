package srp.single.hr.ioc.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Mapper
@Component
public interface LoginMapper {

    Map<String, String> getUser(Map<String, String> params) throws Exception;

}
