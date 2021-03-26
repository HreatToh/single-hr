package srp.single.hr.ioc.s;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.meta.JdbcType;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import srp.single.hr.config.AppConfig;
import srp.single.hr.ioc.dao.CalculationMapper;
import srp.single.hr.ioc.dao.FileUploadMapper;
import srp.single.hr.ioc.dao.TemplateMapper;
import srp.single.hr.ioc.entity.Page;
import srp.single.hr.ioc.entity.Result;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

@Service
public class CalculationService {

    private Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    private CalculationMapper calculationMapper;

    @Autowired
    private TemplateMapper templateMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private FileUploadMapper fileUploadMapper;

    public Object getList(Map<String, String> params) {
        Page page = null;
        List<Map<String,String>> list = ListUtil.list(false);
        try{
            Integer start = Convert.convert(Integer.class,params.get("page"));
            Integer limit = Convert.convert(Integer.class,params.get("limit"));
            String templateId = params.get("templateId");
            Map<String, Object> templateInfo = templateMapper.getTemplateInfo(templateId);
            String tableName = Convert.convert(String.class,templateInfo.get("TABLE_NAME"));
            params.put("tableName",tableName);
            PageHelper.startPage(start,limit);
            list = calculationMapper.getList(params);
            page = new Page<Map<String,String>>(list);
        }catch (Exception e){
            logger.error("查询表信息失败",e);
            page = new Page(e);
        }
        return page;
    }

    /**
     * 计算
     * @param params
     * @return
     */
    public Object calcAvg(Map<String, String> params) {
        String msg = "";
        boolean success = false;
        List<Object[]> args = ListUtil.list(false);
        try {
            String dataDate = Convert.convert(String.class,params.get("dataDate"));
            params.put("colId","1");
            List<Map<String,String>> empNos = calculationMapper.queryCalcAvgCell(params);
            params.put("colId","3");
            List<Map<String,String>> startDates = calculationMapper.queryCalcAvgCell(params);
            params.put("colId","4");
            List<Map<String,String>> endDates = calculationMapper.queryCalcAvgCell(params);
            params.put("colId","6");
            List<Map<String,String>> vals = calculationMapper.queryCalcAvgCell(params);
            args = buildData(empNos , startDates ,endDates ,vals);
            jdbcTemplate.batchUpdate("update hr_calc_avg_info set val = ? where row_id = ? and col_id = ? and data_date= ? ", args);

            msg = "计算完成！";
            success = true;
        }catch (Exception e){
            logger.error("计算平均值异常！",e);
            success = false;
            msg = StrUtil.format("计算平均值异常！{}",e.getMessage()) ;
        }
        return new Result(success,msg,args);
    }


    /**
     * 组建数据
     * @return
     */
    private List<Object[]> buildData(List<Map<String,String>> empNos ,List<Map<String,String>> startDates ,List<Map<String,String>> endDates ,List<Map<String,String>> vals) {
        String sql = "select avg(pay_emp_total) from hr_pay_info_view where pay_yyyymm between ? and ? and pay_emp_id = ?";
        List<Object[]> args = ListUtil.list(false);
        for (int i = 0; i < empNos.size(); i++) {
            Object[] v = new Object[4];
            String empNo = empNos.get(i).get("VAL");
            String startDate = startDates.get(i).get("VAL");
            String endDate = endDates.get(i).get("VAL");

            startDate = DateUtil.parse(startDate,"yyyy-MM-dd").toString("yyyyMM");
            endDate = DateUtil.parse(endDate,"yyyy-MM-dd").toString("yyyyMM");

            BigDecimal avg = jdbcTemplate.queryForObject(sql, BigDecimal.class, startDate, endDate, empNo);

            v[0] = avg.setScale(2,BigDecimal.ROUND_HALF_UP).toString();
            v[1] = vals.get(i).get("ROW_ID");
            v[2] = vals.get(i).get("COL_ID");
            v[3] = vals.get(i).get("DATA_DATE");
            args.add(v);
        }
        return args;
    }

    public void exportCalc(HttpServletResponse response, Map<String, String> params) {
        File tempFile = null;
        FileInputStream fis = null;
        ExcelReader reader = null;
        ExcelWriter writer = null;
        OutputStream os = null;
        try{
            String dataDate = params.get("dataDate");
            String sql = "select * from hr_calc_avg_file where data_date = ?";
            Map<String, Object> map = jdbcTemplate.queryForMap(sql , dataDate);
            String fileName = StrUtil.format("平均值统计_{}{}", DateUtil.today() , ".xls");
            if (MapUtil.isNotEmpty(map)){
                byte[] fileBytes = (byte[]) map.get("FILE");
                String baseTempDirPath = AppConfig.get("system.config.temp.path");
                String tempDirPath = StrUtil.format("{}{}{}",baseTempDirPath,File.separator,IdUtil.simpleUUID());
                tempFile = fileUploadService.getTempFile(fileBytes, tempDirPath);
                fis = new FileInputStream(tempFile);
                reader = ExcelUtil.getReader(fis);
                List<String> sheetNames = reader.getSheetNames();
                IoUtil.close(reader);
                IoUtil.close(fis);
                writer = ExcelUtil.getWriter(tempFile, sheetNames.get(0));
                params.put("colId","6");
                List<Map<String,String>> vals = calculationMapper.queryCalcAvgCell(params);
                for (int i = 0; i < vals.size(); i++) {
                    Integer rowId = Convert.convert(Integer.class ,vals.get(i).get("ROW_ID"));
                    Integer colId = Convert.convert(Integer.class ,vals.get(i).get("COL_ID"));
                    String val = vals.get(i).get("VAL");
                    writer.writeCellValue(colId, rowId , val);
                }
                IoUtil.close(writer);
                fis = new FileInputStream(tempFile);
                os = response.getOutputStream();

                response.setContentType("application/bin");
                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
                IoUtil.copy(fis,os);
            }else{
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().println(StrUtil.format("<script>alert('{},{}');window.history.back();</script>", "未上传模板，","文件不存在！"));
            }
        }catch (Exception e){
            try {
                logger.error("下载文件异常",e);
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().println("<script>alert('下载文件异常！');window.history.back();</script>");
            } catch (IOException ioException) {
                logger.error(e);
            }
        }finally {
            IoUtil.close(writer);
            IoUtil.close(reader);
            IoUtil.close(os);
            IoUtil.close(fis);
            if (tempFile != null){
                FileUtil.del(tempFile);
            }
        }
    }
}
