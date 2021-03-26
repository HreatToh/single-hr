package srp.single.hr.ioc.s;

import cn.hutool.core.map.MapUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import srp.single.hr.ioc.dao.UserMapper;

import java.util.Map;

@Service
public class UserService {

    private final Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    private UserMapper userMapper;
    /**
     * 查询用户信息通过id
     * @param username
     * @return
     */
    public Map<String,String> getUserById(String username){
        Map<String,String> user = MapUtil.newHashMap();
        try {
            user = userMapper.getUserById(username);
        } catch (Exception e) {
            logger.error("查询用户失败！",e);
        }
        return user;
    }
}
