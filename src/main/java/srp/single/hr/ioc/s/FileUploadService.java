package srp.single.hr.ioc.s;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import srp.single.hr.config.AppConfig;
import srp.single.hr.ioc.comm.LoginManger;
import srp.single.hr.ioc.dao.FileUploadMapper;
import srp.single.hr.ioc.entity.Result;
import srp.single.hr.ioc.utils.S;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Service
public class FileUploadService {

    /**
     * 实例日志对象
     */
    private Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    private FileUploadMapper fileUploadMapper;

    @Autowired
    private SyncLogService syncLogService;

    @Autowired
    private DictMapService dictMapService;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    /**
     * 上传文件的逻辑
     * @param file
     * @param params
     * @return
     */
    public Object uploadTemplate(MultipartFile file, Map<String, Object> params) {
        String msg = "";
        boolean success = false;
        try {
            params.put("templateFile",file.getBytes());
            fileUploadMapper.uploadTemplate(params);
            msg = "模板上传成功！";
            success = true;
        }catch (Exception e){
            logger.error("上传模板文件异常",e);
            success = false;
            msg = StrUtil.format("模板上传异常！{}",e.getCause()) ;
        }
        return new Result(success,msg);
    }


    /**
     * 导入数据文件的逻辑
     * @param file
     * @param params
     * @return
     */
    public Object uploadData(MultipartFile file, Map<String, Object> params) {
        String msg = "";
        boolean success = false;
        String threadId = IdUtil.simpleUUID();

        try {
            String baseTempDirPath = AppConfig.get("system.config.temp.path");
            String tempDirPath = StrUtil.format("{}{}{}",baseTempDirPath,File.separator,threadId);
            /*创建临时目录*/
            File tempDir = new File(tempDirPath);
            if (!tempDir.exists()){
                tempDir.mkdirs();
            }
            File tempFile = getTempFile(file.getBytes(), tempDir.getAbsolutePath());
            //创建线程
            Runnable runnable = () -> {
                importExcelData(threadId , tempFile , params);
            };
            //执行线程
            ThreadUtil.execute(runnable);
            success = true;
            msg = threadId;
        }catch (Exception e){
            logger.error("文件导入失败异常",e);
            success = false;
            msg = StrUtil.format("文件导入失败异常！{}",e.getCause()) ;
        }
        return new Result(success,msg);
    }

    /**
     * 导入文件的主要逻辑
     * @param threadId
     * @param params
     */
    private void importExcelData(String threadId, File file, Map<String, Object> params) {
        InputStream is = null;
        String sql = "";
        try{
            /*获取基础信息*/
            syncLogService.info(threadId,"任务开始执行...","0.00%");
            syncLogService.info(threadId,"初始化基础参数","1.00%");
            String templateId = Convert.convert(String.class,params.get("templateId"));
            Map<String,Object> templateInfo = templateService.getTemplateInfo(templateId);
            String[] primaryKeyArray = (String[]) templateInfo.get("primaryKeyArray");
            Map<String,String> colTypeMap = Convert.convert(Map.class,templateInfo.get("colTypeMap"));
            String improtTable = Convert.convert(String.class,templateInfo.get("IMPORT_TABLE"));
            String tempTable = StrUtil.concat(true,"temp_", DateUtil.format(DateUtil.date(),"yyyyMMddHHmmssSSS"));
            String readLine = dictMapService.getConfig(templateId,"READ_LINE","2");
            Dict titleDict = Convert.convert(Dict.class , dictMapService.getDict(StrUtil.format("IMP_TEMPLATE_{}",templateId))) ;

            syncLogService.info(threadId,StrUtil.format("导入目标表：{}",improtTable),"2.00%");
            syncLogService.info(threadId,StrUtil.format("导入临时表：{}",tempTable),"3.00%");
            syncLogService.info(threadId,StrUtil.format("导入读取行数：{}",readLine),"4.00%");

            /*创建临时表*/
            sql = StrUtil.format("create table {} like {}",tempTable , improtTable);
            jdbcTemplate.execute(sql);
            syncLogService.info(threadId,StrUtil.format("创建临时表：{}",tempTable),"10.00%");

            /*读取文件*/
            syncLogService.info(threadId,StrUtil.format("读取临时文件：{}",file.getAbsolutePath()),"15.00%");
            is = new FileInputStream(file);
            ExcelReader reader = ExcelUtil.getReader(is);
            List<List<Object>> list = ListUtil.list(false);
            List<Object> title = ListUtil.list(false);
            List<Object> k = ListUtil.list(false);
            List<Object> v = ListUtil.list(false);
            int sheetCount = reader.getSheetCount();
            for (int i = 0; i < sheetCount; i++) {
                reader = reader.setSheet(i);
                int rowCount = reader.getRowCount();
                int startRow = Integer.parseInt(readLine);
                if (i == 0){
                    title = reader.readRow(startRow - 1);
                }
                list = reader.read(startRow);
            }
            syncLogService.info(threadId,StrUtil.format("读取文件成功,一共读取：{}条数据",list.size()),"20.00%");



            syncLogService.info(threadId,"开始组建插入参数...","25.00%");
            List<Object[]> rows = ListUtil.list(false);
            for (int ii = 0; ii < list.size(); ii++) {
                List<String> row = ListUtil.list(false);
                for (int i = 0; i < title.size(); i++) {
                    String cellKey = Convert.convert(String.class,title.get(i));
                    cellKey = Convert.convert(String.class,titleDict.get(cellKey));
                    String value = Convert.convert(String.class,list.get(ii).get(i));
                    if (StrUtil.contains(cellKey,",")){
                        for (int j = 0; j < cellKey.split(",").length; j++) {
                            String[] split = cellKey.split(",")[j].split(":");
                            String[] split1 = split[1].split("-");
                            if (ii == 0){
                                k.add(split[0]);
                                v.add("?");
                            }
                            row.add(StrUtil.sub(value,Integer.parseInt(split1[0]),Integer.parseInt(split1[1])));
                        }
                    }else{
                        value = StrUtil.equals(colTypeMap.get(cellKey),"number")? (StrUtil.isNotBlank(value)?value:"0.00"):value;
                        if (ii == 0){
                            k.add(cellKey);
                            v.add("?");
                        }
                        row.add(value);
                    }
                }
                rows.add(row.toArray());
            }
            syncLogService.info(threadId,"组件插入参数完成！","50.00%");


            syncLogService.info(threadId,"开始批量插入临时表！","60.00%");
            /*执行批量插入操作*/
            sql = StrUtil.format("insert into {}({}) values({})",tempTable,CollUtil.join(k,","),CollUtil.join(v,","));
            jdbcTemplate.batchUpdate(sql,rows);
            syncLogService.info(threadId,"插入成功！","70.00%");

            syncLogService.info(threadId,"执行迭代新增操作...","80.00%");
            /*执行新增操作*/
            int i = addData(primaryKeyArray, tempTable , improtTable);
            syncLogService.info(threadId,StrUtil.format("执行成功,插入[{}]条数据,忽略[{}]条数据.",i,list.size() - i),"90.00%");

            sql = StrUtil.format("DROP TABLE {} ",tempTable);
            jdbcTemplate.execute(sql);
            syncLogService.info(threadId,"删除临时表！","95.00%");

        }catch (Exception e){
            logger.error(e);
            syncLogService.error(threadId,StrUtil.format("生成文件异常！{}",e.getMessage()),"100.00%");
        }finally {
            syncLogService.finish(threadId);
        }
    }


    /**
     * 执行add操作
     * @param primaryKeyArray
     * @param tempTable
     * @param improtTable
     * @return
     */
    private int addData(String[] primaryKeyArray, String tempTable, String improtTable) {
        for (int i = 0; i < primaryKeyArray.length; i++) {
            primaryKeyArray[i] = StrUtil.format("t.{} = t1.{}" , primaryKeyArray[i],primaryKeyArray[i]);
        }
        String colSql = CollUtil.join(CollUtil.newArrayList(primaryKeyArray)," and ");
        String sql = StrUtil.format("insert into {} select * from {} t where not exists ( select 1 from {} t1 where {})",improtTable,tempTable,improtTable,colSql);
        return jdbcTemplate.update(sql);
    }

    /**
     * 模板下载
     * @param response
     * @param params
     */
    public void downloadTemplate(HttpServletResponse response, Map<String, Object> params) {
        Map<String,Object> templateInfo = MapUtil.newHashMap();
        ByteArrayInputStream bais = null;
        OutputStream os = null;
        String fileName = "";
        try {
            templateInfo = fileUploadMapper.downloadTemplate(params);
            String templateName = Convert.convert(String.class,templateInfo.get("TEMPLATE_NAME"));
            fileName = StrUtil.format("{}_{}_模板.xls",templateName, DateUtil.today());
            Object file = templateInfo.get("TEMPLATE_FILE");
            if (ObjectUtil.isNotNull(file)){
                byte[] filebytes = (byte[]) file;
                bais = new ByteArrayInputStream(filebytes);
                os = response.getOutputStream();
                response.setContentType("application/bin");
                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
                IoUtil.copy(bais,os);
            }else{
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().println("<script>alert('暂未上传模板文件！');window.history.back();</script>");
            }
            response.flushBuffer();
        }catch (Exception e){
            try {
                logger.error("下载模板异常",e);
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().println("<script>alert('下载模板异常！');window.history.back();</script>");
            } catch (IOException ioException) {
                logger.error(e);
            }
        }finally {
            IoUtil.close(os);
            IoUtil.close(bais);
        }
    }

    /**
     * 组建数据
     * @param params
     * @return
     */
    public Object buildExcelData(Map<String, Object> params) {
        String msg = "";
        boolean success = false;
        String threadId = IdUtil.simpleUUID();
        try{
            //创建线程
            Runnable runnable = () -> {
                buildExcelData(threadId , params);
            };
            //执行线程
            ThreadUtil.execute(runnable);
            success = true;
            msg = threadId;
        }catch (Exception e){
            logger.error(e);
        }
        return new Result(success,msg);
    }

    /**
     * 生成文件主要逻辑
     * @param threadId
     * @param params
     */
    public void buildExcelData(String threadId , Map<String, Object> params){
        try{
            /*获取基础信息*/
            syncLogService.info(threadId,"任务开始执行...","0.00%");
            syncLogService.info(threadId,"初始化基础参数","1.00%");
            String templateId = Convert.convert(String.class,params.get("templateId"));
            Map<String,Object> templateInfo = fileUploadMapper.downloadTemplate(params);
            Object file = templateInfo.get("TEMPLATE_FILE");
            String readLine = dictMapService.getConfig(templateId,"READ_LINE","2");
            String exportFont = dictMapService.getConfig(templateId,"EXPORT_FONT","仿宋");
            String sheetName = dictMapService.getConfig(templateId,"SHEET_NAME","<data>");
            String excelMaxRow = dictMapService.getConfig(templateId,"EXCEL_MAX_ROW","60000");
            Dict titleDict = Convert.convert(Dict.class , dictMapService.getDict(StrUtil.format("EXP_TEMPLATE_{}",templateId))) ;
            String baseTempDirPath = AppConfig.get("system.config.temp.path");

            syncLogService.info(threadId,StrUtil.format("读取模版开始行：{}",readLine),"3.00%");
            syncLogService.info(threadId,StrUtil.format("导出EXCEL字体：{}",exportFont),"4.00%");
            syncLogService.info(threadId,StrUtil.format("Sheet页名称：{}",sheetName),"5.00%");
            syncLogService.info(threadId,StrUtil.format("最大导出行数：{}",excelMaxRow),"6.00%");
            syncLogService.info(threadId,StrUtil.format("临时文件基础路径：{}",baseTempDirPath),"7.00%");

            String tempDirPath = StrUtil.format("{}{}{}",baseTempDirPath,File.separator,threadId);
            syncLogService.info(threadId,"基础参数初始化完成！","10.00%");
            /*创建临时目录*/
            File tempDir = new File(tempDirPath);
            if (!tempDir.exists()){
                tempDir.mkdirs();
            }
            syncLogService.info(threadId,StrUtil.format("创建临时目录完成！目录：{}" , tempDir.getAbsolutePath()),"15.00%");

            syncLogService.info(threadId,"检查模版是否存在...","16.00%");
            /*检查模版是否存在*/
            if(ObjectUtil.isNotNull(file)){
                byte[] filebytes = (byte[]) file;
                File tempFile = getTempFile(filebytes,tempDir.getAbsolutePath());
                syncLogService.info(threadId,StrUtil.format("模版存在并创建临时文件成功！，文件：{}",tempFile.getAbsoluteFile()),"20.00%");
                String sql = "select * from ${tableName} where 1=1 ${condition} ";
                params.put("tableName",templateInfo.get("TABLE_NAME"));
                sql = S.format(sql, params);
                syncLogService.debug(threadId,StrUtil.format("查询数据SQL:{}",sql),"20.00%");
                int total = jdbcTemplate.queryForObject(StrUtil.format("select count(1) from ({}) t",sql),Integer.class);
                jdbcTemplate.query(sql,(ResultSet rs)->{
                    ExcelReader reader = null;
                    ExcelWriter writer = null;
                    syncLogService.info(threadId,"开始边读边写..","25.00%");
                    int count = 0;
                    try{
                        int page = 0;
                        int startRow = Integer.parseInt(readLine);
                        reader = ExcelUtil.getReader(tempFile);
                        writer = ExcelUtil.getWriter(tempFile,"<data>");
                        writer.renameSheet(0,sheetName);
                        CellStyle cellStyle = writer.createCellStyle();
                        cellStyle.setBorderBottom(BorderStyle.THIN);
                        cellStyle.setBorderTop(BorderStyle.THIN);
                        cellStyle.setBorderLeft(BorderStyle.THIN);
                        cellStyle.setBorderRight(BorderStyle.THIN);
                        Font font = writer.createFont();
                        font.setColor(IndexedColors.GREY_80_PERCENT.index);
                        font.setFontHeightInPoints((short)9);
                        font.setFontName(exportFont);
                        cellStyle.setFont(font);
                        List<Object> title = reader.readRow(startRow - 1);
                        IoUtil.close(reader);
                        while (true){
                            int r = startRow + count;
                            Row row = writer.getOrCreateRow(r);
                            for (int i = 0; i < title.size(); i++) {
                                String cellKey = Convert.convert(String.class,title.get(i));
                                String cellValue = rs.getString(Convert.convert(String.class,titleDict.get(cellKey)));
                                Cell cell = row.createCell(i);
                                cell.setCellStyle(cellStyle);
                                cell.setCellValue(cellValue);
                            }
                            count ++ ;

                            if (count % (total/7 > 0 ? total/7 : 1) == 0){
                                syncLogService.info(threadId,StrUtil.format("已经写入 [{}] 条数据",count),StrUtil.format("{}%", BigDecimal.valueOf((count / (total/70D)) + 25).setScale(2,BigDecimal.ROUND_HALF_UP)));
                            }
                            /*先不开发分页逻辑*/

                            //如果不存在退出
                            if (!rs.next()){
                                break;
                            }
                        }
                    }catch (Exception e){
                        logger.error(e);
                    }finally {
                        IoUtil.close(writer);
                    }
                    syncLogService.info(threadId,StrUtil.format("数据全部写入完成！总条数：{}",count ),"95.00%");
                });
                syncLogService.download(threadId,tempFile.getAbsolutePath(),StrUtil.format("{}_{}{}",templateInfo.get("TEMPLATE_NAME"),DateUtil.today(),".xls"));
            }else{
                syncLogService.info(threadId,StrUtil.format("[{}]模版未上传，文件生成失败！" , templateInfo.get("TEMPLATE_NAME")),"99.00%");
            }
        }catch (Exception e){
            logger.error(e);
            syncLogService.error(threadId,StrUtil.format("生成文件异常！{}",e.getMessage()),"100.00%");
        }finally {
            syncLogService.finish(threadId);
        }
    }

    /**
     * 创建临时文件
     * @param filebytes
     * @param absolutePath
     * @return
     */
    public File getTempFile(byte[] filebytes, String absolutePath) {
        ByteArrayInputStream bais = null;
        FileOutputStream fos = null;
        File tempFile = null;
        String tempFileName = StrUtil.format("{}{}", DateUtil.format(DateUtil.date(),"yyyyMMddHHmmssSSS"),".temp");
        try {
            tempFile = new File(absolutePath, tempFileName);
            tempFile = FileUtil.touch(tempFile);
            bais = new ByteArrayInputStream(filebytes);
            fos = new FileOutputStream(tempFile);
            IoUtil.copy(bais,fos);
        }catch (Exception e){
            logger.error(e);
        }finally {
            IoUtil.close(fos);
            IoUtil.close(bais);
        }
        return tempFile;
    }


    /**
     * 下载文件
     * @param response
     * @param params
     */
    public void downloadFile(HttpServletResponse response, Map<String, Object> params) {
        Map<String,Object> templateInfo = MapUtil.newHashMap();
        FileInputStream fis = null;
        OutputStream os = null;
        File file = null;
        try {
            String path = Convert.convert(String.class,params.get("path"));
            String fileName = Convert.convert(String.class,params.get("filename"));

            /*解密路径*/
            path = Base64.decodeStr(path);
            fileName = Base64.decodeStr(fileName);

            file = new File(path);
            if (file.exists()){
                fis = new FileInputStream(file);
                os = response.getOutputStream();
                response.setContentType("application/bin");
                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
                IoUtil.copy(fis,os);
            }else{
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().println(StrUtil.format("<script>alert('{},{}');window.history.back();</script>",URLEncoder.encode(path, "UTF-8"),"文件不存在！"));
            }
            response.flushBuffer();
        }catch (Exception e){
            try {
                logger.error("下载文件异常",e);
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().println("<script>alert('下载文件异常！');window.history.back();</script>");
            } catch (IOException ioException) {
                logger.error(e);
            }
        }finally {
            IoUtil.close(os);
            IoUtil.close(fis);
            if (file != null){
                file.deleteOnExit();
            }
        }
    }

    /**
     * 上传平均值统计的模板
     * @param file
     * @param params
     * @return
     */
    public Object uploadCalcAvg(MultipartFile file, Map<String, Object> params) {
        String msg = "";
        boolean success = false;
        Map<String , Object> result = MapUtil.newHashMap();
        try {
            String dataDate = Convert.convert(String.class,params.get("dataDate"));
            params.put("file",file.getBytes());
            Map<String, Object> map = fileUploadMapper.queryCalcAvg(params);
            if (MapUtil.isEmpty(map)){
                fileUploadMapper.saveCalcAvg(params);
            }else{
                fileUploadMapper.uploadCalcAvg(params);
            }

            result = buildTableDataView(dataDate,file.getInputStream());

            msg = "模板解析成功！";
            success = true;
        }catch (Exception e){
            logger.error("解析模板异常！",e);
            try {
                fileUploadMapper.delCalcAvg(params);
                success = false;
                msg = StrUtil.format("解析模板异常！{}",e.getCause()) ;
            } catch (Exception ee) {
                logger.error(ee);
            }
        }
        return new Result(success,msg,result);
    }

    private Map<String, Object> buildTableDataView(String dataDate ,InputStream is) throws Exception{
        Map<String,Object> result = MapUtil.newHashMap();
        ExcelReader reader = null;
        List<String> cols = null;
        List<Object[]> args = null;
        List<List<String>> rows = null;
        List<List<Integer>> mergaCols = null;
        List<List<Integer>> mergaRows = null;
        try {
            /*开始解析excel*/
            reader = ExcelUtil.getReader(is);
            rows = ListUtil.list(false);
            mergaCols = ListUtil.list(false);
            mergaRows = ListUtil.list(false);
            args = ListUtil.list(false);
            int rowCount = reader.getRowCount();
            int columnCount = reader.getColumnCount();
            for (int i = 0; i < rowCount; i++) {
                cols = ListUtil.list(false);
                for (int j = 0; j < columnCount; j++) {
                    Object[] vals = new Object[5];
                    vals[0] = dataDate;
                    vals[1] = j;
                    vals[2] = i;
                    if (!isMergedRegion(reader, i ,j , cols , vals)){
                        Object o = reader.readCellValue(j, i);
                        String value = o != null ? o.toString() : "";
                        cols.add(value);
                        vals[3] = value ;
                        vals[4] = "N";
                    }
                    args.add(vals);
                }
                rows.add(cols);
            }
            /*组建合并单元格*/
            buildMergedRegionInfo(reader , mergaCols , mergaRows);

            String sql = "delete from hr_calc_avg_info where data_date = ? ";
            jdbcTemplate.update( sql , dataDate );
            sql = "insert into hr_calc_avg_info(data_date,col_id,row_id,val,is_merge) values(?,?,?,?,?)";
            jdbcTemplate.batchUpdate( sql,args );
            result.put("rows",rows);
            result.put("mergaCols",mergaCols);
            result.put("mergaRows",mergaRows);
        }catch (Exception e){
            logger.error(e);
        }finally {
            IoUtil.close(reader);
            IoUtil.close(is);
        }
        return result;
    }

    /**
     * 处理合并单元格
     * @param reader
     * @param rowid
     * @param colid
     * @param cols
     * @return
     */
    private boolean isMergedRegion(ExcelReader reader, int rowid, int colid, List<String> cols ,Object[] vals) {
        Sheet sheet = reader.getSheet();
        int mergedRegions = sheet.getNumMergedRegions();
        for(int i = 0 ; i < mergedRegions ; i++ ){
            CellRangeAddress cra = sheet.getMergedRegion(i);
            int firstColumn = cra.getFirstColumn();
            int lastColumn = cra.getLastColumn();
            int firstRow = cra.getFirstRow();
            int lastRow = cra.getLastRow();

            /*如果是合并单元格  则处理合并的值合并的列行坐标*/
            if(rowid >= firstRow && rowid <= lastRow){
                if(colid >= firstColumn && colid <= lastColumn){
                    Object o = reader.readCellValue(firstColumn, firstRow);
                    String value = ObjectUtil.isNotNull(o) ? Convert.convert(String.class,o) : "";
                    cols.add( value );
                    vals[3] = value ;
                    vals[4] = "Y";
                    return true ;
                }
            }
        }
        return false;
    }

    /**
     * 获取合并单元格信息
     * @param reader
     * @param mergaCols
     * @param mergaRows
     */
    private void buildMergedRegionInfo(ExcelReader reader , List<List<Integer>> mergaCols, List<List<Integer>> mergaRows){
        Sheet sheet = reader.getSheet();
        int mergedRegions = sheet.getNumMergedRegions();
        for(int i = 0 ; i < mergedRegions ; i++ ){
            CellRangeAddress cra = sheet.getMergedRegion(i);
            int firstColumn = cra.getFirstColumn();
            int lastColumn = cra.getLastColumn();
            int firstRow = cra.getFirstRow();
            int lastRow = cra.getLastRow();
            List<Integer> cols = ListUtil.list(false);
            List<Integer> rows = ListUtil.list(false);
            for (int j = firstColumn; j <= lastColumn; j++) {
                cols.add(j);
            }
            for (int j = firstRow; j <= lastRow; j++) {
                rows.add(j - 1);
            }
            mergaCols.add(cols);
            mergaRows.add(rows);
        }
    }

    /**
     * 查询模板信息
     * @param params
     * @return
     */
    public Object queryCalcAvg(Map<String, Object> params) {
        String msg = "";
        boolean success = false;
        Map<String , Object> result = MapUtil.newHashMap();
        try {
            String dataDate = Convert.convert(String.class,params.get("dataDate"));
            Map<String, Object> map = fileUploadMapper.queryCalcAvg(params);
            if (MapUtil.isNotEmpty(map)){
                byte[] fileBytes = (byte[]) map.get("FILE");
                String baseTempDirPath = AppConfig.get("system.config.temp.path");
                String tempDirPath = StrUtil.format("{}{}{}",baseTempDirPath,File.separator,IdUtil.simpleUUID());
                File tempFile = getTempFile(fileBytes, tempDirPath);
                result = buildTableDataView(dataDate,new FileInputStream(tempFile));
                FileUtil.del(tempFile);
                msg = "模板解析成功！";
            }else{
                msg = "暂无模板信息，请上传！";
            }
            success = true;
        }catch (Exception e){
            logger.error("解析模板异常！",e);
            try {
                fileUploadMapper.delCalcAvg(params);
                success = false;
                msg = StrUtil.format("解析模板异常！{}",e.getCause()) ;
            } catch (Exception ee) {
                logger.error(ee);
            }
        }
        return new Result(success,msg,result);
    }
}
