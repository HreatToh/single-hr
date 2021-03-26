package srp.single.hr.ioc.comm;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.Data;

import java.util.Map;

public final class LoginManger {

    private final static ThreadLocal<Map<String,String>> loginTL = new ThreadLocal<Map<String,String>>();


    /**
     * 获取用户信息
     * @return
     */
    public static Map<String,String> getUser(){
        Map<String, String> user = loginTL.get();
        if (ObjectUtil.isNull(user)){
            user = MapUtil.newHashMap();
            loginTL.set(user);
        }
        return user;
    }

    /**
     * 设置用户
     * @param user
     */
    public static void setUser(Map<String, String> user){
        loginTL.set(user);
    }

    /**
     * 获取用户登录名
     * @return
     */
    public static String getUserId(){
        return loginTL.get().get("userName");
    }
}
