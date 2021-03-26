package srp.single.hr.ioc.s;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import srp.single.hr.ioc.dao.SyncLogMapper;
import srp.single.hr.ioc.entity.Page;

import java.util.List;
import java.util.Map;

@Service
public class SyncLogService {

    private Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    private SyncLogMapper syncLogMapper;


    /**
     * 查询日志列表
     * @param params
     * @return
     */
    public Object getSyncLogList(Map<String, String> params) {
        Page page = null;
        List<Map<String,String>> list = ListUtil.list(false);
        try {
            if (logger.isDebugEnabled()){
                params.put("level","'INFO','DEBUG','WARN','ERROR'");
            }else{
                params.put("level","'INFO','WARN','ERROR'");
            }
            list = syncLogMapper.getSyncLogList(params);
            page = new Page(list);
        }catch (Exception e){
            logger.error(e);
            page = new Page(e);
        }
        return page;
    }


    /**
     * 插入日志
     * @param threadId
     * @param level
     * @param msg
     * @param progress
     * @return
     */
    public Object log(String threadId , String level , String msg ,String progress){
        int count = 0;
        try {
            count = syncLogMapper.log(threadId, level, DateUtil.format(DateUtil.date(), "yyyy-MM-dd HH:mm:ss SSS"), msg, progress);
        }catch (Exception e){
            logger.error(e);
        }
        return count;
    }


    /**
     * 级别INFO
     * @param threadId
     * @param msg
     * @param progress
     * @return
     */
    public Object info(String threadId , String msg ,String progress){
        return log(threadId, "INFO" ,msg ,progress);
    }

    /**
     * 级别DUGGER
     * @param threadId
     * @param msg
     * @param progress
     * @return
     */
    public Object debug(String threadId , String msg ,String progress){
        return log(threadId, "DEBUG" ,msg ,progress);
    }
    /**
     * 级别WARN
     * @param threadId
     * @param msg
     * @param progress
     * @return
     */
    public Object warn(String threadId , String msg ,String progress){
        return log(threadId, "WARN" ,msg ,progress);
    }

    /**
     * 级别ERROR
     * @param threadId
     * @param msg
     * @param progress
     * @return
     */
    public Object error(String threadId , String msg ,String progress){
        return log(threadId, "ERROR" ,msg ,progress);
    }


    /**
     * 任务完成
     * @param threadId
     * @return
     */
    public Object finish(String threadId){
        return log(threadId, "INFO" ,"任务完成[Complete]" ,"100.00%");
    }


    /**
     * 生成下载的超链接
     * @param threadId
     * @param path
     * @param fileName
     * @return
     */
    public Object download(String threadId , String path , String fileName){
        return log(threadId, "INFO" , StrUtil.format("{},{},{}","双击该行下载文件", Base64.encode(path),Base64.encode(fileName)),"100.00%");
    }
}
