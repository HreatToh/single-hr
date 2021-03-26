package srp.single.hr.ioc.c;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import srp.single.hr.ioc.s.TemplateService;

@Controller
public class TemplateController {

    @Autowired
    private TemplateService templateService;


}
