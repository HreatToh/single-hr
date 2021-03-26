package srp.single.hr.interceptor;

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


@Component
public class MyInterceptor implements HandlerInterceptor {

    /**
     * 获取日志对象
     */
    private static final Logger logger = LoggerFactory.getLogger(HandlerInterceptor.class);

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
        if (logger.isDebugEnabled()){
            logger.debug("拦截url："+request.getRequestURI());
        }
        String uri = request.getRequestURI();
        String appPath = environment.getProperty("server.servlet.context-path");
        if (!StrUtil.startWith(uri,appPath)){
            response.sendRedirect(StrUtil.format("{}/{}",appPath,uri));
            return false;
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
        logger.debug("拦截通过之后："+httpServletRequest.toString());
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
        logger.debug("渲染页面后："+httpServletRequest.toString());
    }

}
