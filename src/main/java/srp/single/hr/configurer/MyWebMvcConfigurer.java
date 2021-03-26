package srp.single.hr.configurer;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import srp.single.hr.interceptor.LoginInterceptor;
import srp.single.hr.interceptor.MyInterceptor;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;


@Configuration
public class MyWebMvcConfigurer implements WebMvcConfigurer {

    public static List<String> EXCLUDE_PATH = Arrays.asList("/static/**","/css/**","/images/**","/component/**","/js/**","/json/**","/layuiadmin/**","/error/**");

    @Resource
    private MyInterceptor myInterceptor ;

    @Resource
    private LoginInterceptor loginInterceptor;
    /**
     * 添加拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(myInterceptor).addPathPatterns("/**").excludePathPatterns(EXCLUDE_PATH);
        //对全路径进行拦截  检查用户是否已经登录  检查session
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**").excludePathPatterns(EXCLUDE_PATH);
    }


    /**
     * 设置首页
     * @param registry
     */
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("redirect:/index");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }
}


