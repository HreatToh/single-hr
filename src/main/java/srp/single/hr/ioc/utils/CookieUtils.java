package srp.single.hr.ioc.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class  CookieUtils {


    /**
     * 设置cookie
     * @param response
     * @param cookie
     */
    public static void setCookie(HttpServletResponse response, Cookie cookie){
        cookie.setPath("/");
        response.addCookie(cookie);
    }


    /**
     * 设置cookie通过key value 的形式
     * @param response
     * @param key
     * @param value
     */
    public static void setCookie(HttpServletResponse response, String key, String value){
        setCookie(response,new Cookie(key,value));
    }


    /**
     * 获取Cookie 通过key
     * @param request
     * @param key
     */
    public static Cookie getCookie(HttpServletRequest request, String key){
        Cookie[] cookies = request.getCookies();
        if ( cookies != null ){
            for (int i = 0; i < cookies.length ; i++) {
                if (StrUtil.equals(key,cookies[i].getName())){
                    return cookies[i];
                }
            }
        }
        return null;
    }


    /**
     * 获取值
     * @param request
     * @param key
     * @return
     */
    public static String getValue(HttpServletRequest request, String key){
        Cookie cookie = getCookie(request, key);
        if (ObjectUtil.isNotNull(cookie)){
            return cookie.getValue();
        }
        return null;
    }


    /**
     * 获取值  带默认值
     * @param request
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getValue(HttpServletRequest request, String key,String defaultValue){
        String value = getValue(request, key);
        if (StrUtil.isNotBlank(value)){
            return value;
        }
        return defaultValue;
    }


    /**
     * 清空cookie
     * @param request
     * @param key
     */
    public static void removeCookie(HttpServletRequest request, String key){
        Cookie[] cookies = request.getCookies();
        for (int i = 0; i < cookies.length ; i++) {
            if (StrUtil.equals(key,cookies[i].getName())) {
                cookies[i].setValue(null);
                break;
            }
        }
    }
}
