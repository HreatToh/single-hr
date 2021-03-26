package srp.single.hr.interceptor;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@Component
public class LoginInterceptor implements HandlerInterceptor {

    /**
     * 获取日志
     */
    private final static Logger logger = LoggerFactory.getLogger(HandlerInterceptor.class);

    @Autowired
    private Environment environment;
    /**
     * 拦截的逻辑
     * @param request
     * @param response
     * @param o
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        HttpSession session = request.getSession();
        String appPath = environment.getProperty("server.servlet.context-path");
        Object token = session.getAttribute("token");
        String uri = request.getRequestURI();
        //首页和登录操作的url放行
        if (!( StrUtil.equals(uri ,StrUtil.format("{}/index",appPath)) || StrUtil.equals(uri , StrUtil.format("{}/login",appPath)) )){
            if (ObjectUtil.isNull(token)){
                response.sendRedirect(StrUtil.format("{}/index",appPath));
                return false;
            }
        }

        if (StrUtil.equals(uri ,StrUtil.format("{}/index",appPath)) || StrUtil.equals(uri , StrUtil.format("{}/login",appPath))){
            if (ObjectUtil.isNotNull(token)){
                response.sendRedirect(StrUtil.format("{}/main",appPath));
                return false;
            }
        }
        return true;
    }



    /**
     * 拦截通过之后的方法
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    /**
     * 渲染页面后执行的方法
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @param e
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }

}
