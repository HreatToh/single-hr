package srp.single.hr.filter;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.context.support.WebApplicationContextUtils;
import srp.single.hr.config.AppConfig;
import srp.single.hr.ioc.comm.LoginManger;
import srp.single.hr.ioc.s.UserService;
import srp.single.hr.ioc.utils.CookieUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@Data
@WebFilter(urlPatterns = "/*", filterName = "loginFilter")
public class LoginFilter implements Filter {

    private final static Logger logger = LogManager.getLogger(LoginFilter.class);

    private UserService userService;

    /**
     * 初始化
     * @param filterConfig
     * @throws ServletException
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("初始化LoginFilter过滤器");
        userService = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext()).getBean(UserService.class);
    }

    /**
     * 过滤
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = Convert.convert(HttpServletRequest.class,servletRequest);
        String userId = CookieUtils.getValue(request, AppConfig.get("system.config.cookie.userkey"));
        if (StrUtil.isNotBlank(userId)){
            Map<String, String> user = userService.getUserById(userId);
            LoginManger.setUser(user);
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    /**
     * 过滤器销毁
     */
    @Override
    public void destroy() {

    }
}
