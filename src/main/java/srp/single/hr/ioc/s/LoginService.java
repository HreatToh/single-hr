package srp.single.hr.ioc.s;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import srp.single.hr.config.AppConfig;
import srp.single.hr.ioc.comm.LoginManger;
import srp.single.hr.ioc.dao.LoginMapper;
import srp.single.hr.ioc.entity.Result;
import srp.single.hr.ioc.utils.CookieUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Service
public class LoginService {

    /**
     * 实例日志对象
     */
    private Logger logger = LogManager.getLogger(this.getClass());


    @Autowired
    private LoginMapper loginMapper;

    /**
     * 登录
     * @param params
     * @return
     */
    public Result login(HttpServletRequest request, HttpServletResponse response, Map<String, String> params) {
        Map<String,Object> result = MapUtil.newHashMap();
        boolean success = false;
        String msg = "";
        String token = "";
        try {
            Map<String,String> userInfo = loginMapper.getUser(params);
            if (ObjectUtil.isNotNull(userInfo)){
                token = getToken(request,response, userInfo);
                LoginManger.setUser(userInfo);
                success = true;
                msg = "登录成功！";
            }else{
                success = false;
                msg = "帐号或密码错误！";
            }
        } catch (Exception e) {
            success = false;
            msg = "查询用户异常!";
            logger.error("查询用户异常！",e);
        }
        return new Result(success,msg).setToken(token);
    }

    /**
     * 获取令牌
     * @param request
     * @param userInfo
     * @return
     * @throws Exception
     */
    private String getToken(HttpServletRequest request, HttpServletResponse response ,Map<String, String> userInfo) throws Exception{
        String token = IdUtil.simpleUUID();
        HttpSession session = request.getSession();
        userInfo.put("token",token);
        session.setAttribute(AppConfig.get("system.config.cookie.userkey"),userInfo.get("USER_NAME"));
        session.setAttribute(AppConfig.get("system.config.cookie.tokenkey"),token);

        CookieUtils.setCookie(response,AppConfig.get("system.config.cookie.userkey"),userInfo.get("USER_NAME"));
        CookieUtils.setCookie(response,AppConfig.get("system.config.cookie.tokenkey"),token);

        return token;
    }


    /**
     * 登出
     * @param request
     * @param response
     */
    public Result logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        /** 清空session*/
        session.removeAttribute(AppConfig.get("system.config.cookie.userkey"));
        session.removeAttribute(AppConfig.get("system.config.cookie.tokenkey"));

        /** 清空cookie*/
        CookieUtils.removeCookie(request,AppConfig.get("system.config.cookie.userkey"));
        CookieUtils.removeCookie(request,AppConfig.get("system.config.cookie.tokenkey"));
        logger.info("退出登录成功！");
        return Result.logout();
    }
}

