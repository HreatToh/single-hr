package srp.single.hr.ioc.c;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Map;

@Controller
@RequestMapping({"${server.error.path:${error.path:/error}}"})
public class BasicErrorController extends AbstractErrorController {

    private final ErrorProperties errorProperties;

    @Autowired
    public BasicErrorController(ErrorAttributes errorAttributes, ServerProperties serverProperties) {
        super(errorAttributes);
        this.errorProperties=serverProperties.getError();
    }

    /**
     * 错误的页面响应
     */
    @RequestMapping(produces = {"text/html"})
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = this.getStatus(request);
        Map<String, Object> model = Collections.unmodifiableMap(this.getErrorAttributes(request, this.isIncludeStackTrace(request, MediaType.TEXT_HTML)));
        response.setStatus(status.value());
        // 得到一个modelAndView对象
        ModelAndView modelAndView = this.resolveErrorView(request, response, status, model);
        if (ObjectUtil.isNull(modelAndView)){
            modelAndView = new ModelAndView("error/errorPage", model);
        }
        modelAndView.addObject("status", Convert.convert(String.class,status.value()));
        modelAndView.addObject("name",status.name());
        return modelAndView;
    }

    /**
     * 错误的json响应
     */
    @RequestMapping
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        HttpStatus status = this.getStatus(request);
        if (status == HttpStatus.NO_CONTENT) {
            return new ResponseEntity(status);
        } else {
            Map<String, Object> body = this.getErrorAttributes(request, this.isIncludeStackTrace(request, MediaType.ALL));
            return new ResponseEntity(body, status);
        }
    }

    @Override
    public String getErrorPath() {
        return errorProperties.getPath();
    }


    protected boolean isIncludeStackTrace(HttpServletRequest request, MediaType produces) {
        ErrorProperties.IncludeStacktrace include = getErrorProperties().getIncludeStacktrace();
        if (include == ErrorProperties.IncludeStacktrace.ALWAYS) {
            return true;
        }
        if (include == ErrorProperties.IncludeStacktrace.ON_TRACE_PARAM) {
            return getTraceParameter(request);
        }
        return false;
    }

    private ErrorProperties getErrorProperties() {
        return this.errorProperties;
    }
}