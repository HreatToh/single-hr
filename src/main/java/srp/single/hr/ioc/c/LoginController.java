package srp.single.hr.ioc.c;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import srp.single.hr.ioc.entity.Result;
import srp.single.hr.ioc.s.LoginService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;


    @PostMapping("login")
    public Result login(HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, String> params){
        return loginService.login(request,response,params);
    }


    @PostMapping("logout")
    public Result  logout(HttpServletRequest request, HttpServletResponse response){
        return loginService.logout(request, response);
    }
}
