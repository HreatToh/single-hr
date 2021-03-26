package srp.single.hr.filter;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import srp.single.hr.config.AppConfig;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Data
@WebFilter(urlPatterns = "/*", filterName = "requestInitFilter")
public class RequestInitFilter implements Filter {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private String myWebAppName;

    private String myRealPath;

    /**
     * 初始化过滤器
     * @param filterConfig
     * @throws ServletException
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("init(FilterConfig)");
        myWebAppName = makeWebAppName(filterConfig.getServletContext().getContextPath());
        logger.info("init(FilterConfig)  myWebAppName:"+myWebAppName);
        myRealPath=filterConfig.getServletContext().getRealPath("/");
        logger.info("init(FilterConfig)  myRealPath:"+myRealPath);
    }

    /**
     * 过滤器主体
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = Convert.convert(HttpServletRequest.class,servletRequest);
        logger.info(StrUtil.format("url:{}" , request.getRequestURI()));
        request.setAttribute("originalURL", request.getRequestURI());
        request.setAttribute("app", myWebAppName);
        request.setAttribute("appName", AppConfig.getByDefault("system.config.appName","HR单机版取数测算"));
        request.setAttribute("myIp", NetUtil.LOCAL_IP);
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }


    /**
     * 将不一定规范的WebAppName格式化成 /bsys 类似的以 /开头，且结尾无/ 的格式
     *
     * @param name
     * @return
     */
    private String makeWebAppName(String name) {
        if ('/' != name.charAt(0)) {
            name = "/" + name;
        }

        if (name.endsWith("/")) {
            name = name.substring(0, name.length() - 1);
        }
        return name;
    }

}
