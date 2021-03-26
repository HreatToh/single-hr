package srp.single.hr.ioc.c;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import srp.single.hr.ioc.s.CalculationService;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("calculation")
public class CalculationController {

    @Autowired
    private CalculationService calculationService;


    @PostMapping("getList")
    public Object getPayList(@RequestParam Map<String,String> params){
        return calculationService.getList(params);
    }


    @PostMapping("calcAvg")
    public Object calcAvg(@RequestParam Map<String,String> params){
        return calculationService.calcAvg(params);
    }

    @GetMapping("exportCalc")
    public void exportCalc(HttpServletResponse response , @RequestParam Map<String,String> params){
        calculationService.exportCalc(response, params);
    }
}
