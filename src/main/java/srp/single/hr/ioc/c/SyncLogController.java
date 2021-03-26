package srp.single.hr.ioc.c;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import srp.single.hr.ioc.s.SyncLogService;

import java.util.Map;

@Controller
@RequestMapping("syncLog")
public class SyncLogController {

    @Autowired
    private SyncLogService syncLogService;

    @RequestMapping
    public String syncLog(ModelMap modelMap, @RequestParam Map<String,String> params){
        modelMap.addAllAttributes(params);
        return "layerview/sync_log_view";
    }


    @PostMapping("getSyncLogList")
    @ResponseBody
    public Object getSyncLogList(@RequestParam Map<String,String> params){
        return syncLogService.getSyncLogList(params);
    }
}
