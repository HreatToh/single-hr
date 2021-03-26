package srp.single.hr.ioc.c;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import srp.single.hr.ioc.comm.LoginManger;
import srp.single.hr.ioc.s.DictMapService;
import srp.single.hr.ioc.s.TemplateService;

import java.util.Map;

@Controller
public class PageController {

    @Autowired
    private TemplateService templateService;

    @Autowired
    private DictMapService dictMapService;

    @RequestMapping
    public String root(){
        return "redirect:/index";
    }
    @RequestMapping("index")
    public String login(ModelMap modelMap,@RequestParam(value = "timeout" , required = true ,defaultValue = "false") boolean timeout){
        modelMap.put("timeout",timeout);
        return "login";
    }

    @RequestMapping("main")
    public String main(ModelMap modelMap){
        modelMap.addAttribute("user", LoginManger.getUser());
        return "main";
    }

    @RequestMapping("home/index")
    public String index(){
        return "/home/index";
    }

    @RequestMapping("calculation/maintain")
    public String maintain(ModelMap modelMap , @RequestParam Map<String,Object> params){
        String templateId = Convert.convert(String.class,params.get("template_id"));
        modelMap.addAttribute("templateInfo",templateService.getTemplateInfo(templateId));
        modelMap.addAttribute("dictmap",dictMapService.getDictMap());
        return "/calculation/maintain";
    }

    @RequestMapping("calculation/calcavg")
    public String calcavg(ModelMap modelMap , @RequestParam Map<String,Object> params){
        modelMap.addAllAttributes(params);
        return "/calculation/calcavg";
    }

}
