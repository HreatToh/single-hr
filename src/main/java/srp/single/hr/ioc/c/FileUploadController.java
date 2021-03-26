package srp.single.hr.ioc.c;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import srp.single.hr.ioc.s.FileUploadService;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@RequestMapping("fileUpload")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @RequestMapping
    public String fileUpload(ModelMap modelMap, @RequestParam Map<String,String> params){
        modelMap.addAllAttributes(params);
        return "uploadview/file_upload_view";
    }

    @RequestMapping("uploadDataView")
    public String uploadDataView(ModelMap modelMap, @RequestParam Map<String,String> params){
        modelMap.addAllAttributes(params);
        return "uploadview/data_upload_view";
    }


    @PostMapping("uploadTemplate")
    @ResponseBody
    public Object uploadTemplate(MultipartFile file, @RequestParam Map<String,Object> params){
        return fileUploadService.uploadTemplate(file,params);
    }


    @PostMapping("uploadData")
    @ResponseBody
    public Object uploadData(MultipartFile file, @RequestParam Map<String,Object> params){
        return fileUploadService.uploadData(file,params);
    }


    @GetMapping("downloadTemplate")
    public void downloadTemplate(HttpServletResponse response, @RequestParam Map<String, Object> params){
        fileUploadService.downloadTemplate(response,params);
    }


    @PostMapping("buildExcelData")
    @ResponseBody
    public Object buildExcelData(@RequestParam Map<String, Object> params){
        return fileUploadService.buildExcelData(params);
    }

    @GetMapping("downloadFile")
    public void downloadFile(HttpServletResponse response, @RequestParam Map<String, Object> params){
        fileUploadService.downloadFile(response,params);
    }


    @PostMapping("uploadCalcAvg")
    @ResponseBody
    public Object uploadCalcAvg(MultipartFile file, @RequestParam Map<String,Object> params){
        return fileUploadService.uploadCalcAvg(file,params);
    }


    @PostMapping("queryCalcAvg")
    @ResponseBody
    public Object queryCalcAvg(@RequestParam Map<String,Object> params){
        return fileUploadService.queryCalcAvg(params);
    }
}
