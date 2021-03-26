package srp.single.hr.ioc.c;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import srp.single.hr.ioc.s.DictMapService;

import java.util.Map;

@RestController
@RequestMapping("dict")
public class DictMapController {

    @Autowired
    private DictMapService dictMapService;

    @PostMapping("getDict")
    public Object getDict(@RequestParam Map<String,String> params){
        return dictMapService.getDict(params);
    }


    @PostMapping("getAllDict")
    public Object getAllDict(){
        return dictMapService.getDictMap();
    }
}
