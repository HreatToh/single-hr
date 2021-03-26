package srp.single.hr.ioc.s;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import srp.single.hr.ioc.dao.TemplateMapper;
import srp.single.hr.ioc.utils.S;

import java.util.List;
import java.util.Map;

@Service
public class TemplateService {

    private Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    private TemplateMapper templateMapper;


    /**
     * 查询template对象
     * @param templateId
     * @return
     */
    public Map<String,Object> getTemplateInfo(String templateId) {
        Map<String,Object> templateInfo = MapUtil.newHashMap();
        List<Map<String, String>> columnList = ListUtil.list(true);
        Map<String,String> colTypeMap = MapUtil.newHashMap();
        String[] colCnameArray = new String[]{};
        String[] colEnameArray = new String[]{};
        String[] primaryKeyArray = new String[]{};
        String[] sourceKeyArray = new String[]{};
        try{
            templateInfo = templateMapper.getTemplateInfo(templateId);
            columnList = getTemplateColumnList(templateId);

            colEnameArray = getTemplateColumnArray(columnList);
            colCnameArray = getTemplateColumnNameArray(columnList);
            primaryKeyArray = getPrimaryKeyArray(columnList);
            sourceKeyArray = getSourceKeyArray(columnList);
            colTypeMap = getColTypeMap(columnList);
            
            templateInfo.put("colList",columnList);
            templateInfo.put("colCnameArray",colCnameArray);
            templateInfo.put("colEnameArray",colEnameArray);
            templateInfo.put("primaryKeyArray",primaryKeyArray);
            templateInfo.put("sourceKeyArray",sourceKeyArray);
            templateInfo.put("colTypeMap",colTypeMap);
        }catch (Exception e){
            logger.error("查询模版信息异常！",e);
        }
        return templateInfo;
    }

    /**
     * 列字段类型映射
     * @param columnList
     * @return
     */
    private Map<String, String> getColTypeMap(List<Map<String, String>> columnList) {
        Map<String,String> map = MapUtil.newHashMap();
        columnList.forEach((Map<String,String> col) -> {
            map.put(col.get("COLUMN_ID"),col.get("COLUMN_TYPE"));
        });
        return map;
    }

    /**
     *获取物理原表字段
     * @param columnList
     * @return
     */
    private String[] getSourceKeyArray(List<Map<String, String>> columnList) {
        List<String> list = ListUtil.list(false);
        String[] array = new String[]{};
        columnList.forEach((Map<String,String> col) ->{
            if (S.isY(col.get("IS_SOURCE"))){
                list.add(col.get("COLUMN_ID"));
            }
        });
        return list.toArray(array);
    }

    /**
     *获取主键数组
     * @param columnList
     * @return
     */
    private String[] getPrimaryKeyArray(List<Map<String, String>> columnList) {
        List<String> list = ListUtil.list(false);
        String[] array = new String[]{};
        columnList.forEach((Map<String,String> col) ->{
            if (S.isY(col.get("PRIMARY_KEY")) && S.isY(col.get("IS_SOURCE"))){
                list.add(col.get("COLUMN_ID"));
            }
        });
        return list.toArray(array);
    }


    /**
     * 获取模版列信息
     * @param tempateId
     * @return
     */
    public List<Map<String,String>> getTemplateColumnList(String tempateId){
        List<Map<String,String>> list = ListUtil.list(false);
        try{
            list = templateMapper.getTemplateColumnList(tempateId);
        }catch (Exception e){
            logger.error("查询模版信息异常！",e);
        }
        return list;
    }

    /**
     * 获取展示列表模版列信息
     * @return
     */
    public List<Map<String,String>> getTemplateColumnShowList(List<Map<String,String>> list){
        List<Map<String,String>> sublist = ListUtil.list(false);
        for (int i = 0; i < list.size(); i++) {
            String isShowList = list.get(i).get("COLUMN_IS_SHOWLIST");
            if (S.isY(isShowList)){
                sublist.add(list.get(i));
            }
        }
        return sublist;
    }


    /**
     * 获取导出列表模版列信息
     * @return
     */
    public List<Map<String,String>> getTemplateColumnExportList(List<Map<String,String>> list){
        List<Map<String,String>> sublist = ListUtil.list(false);
        for (int i = 0; i < list.size(); i++) {
            String isShowList = list.get(i).get("COLUMN_IS_EXPORT");
            if (S.isY(isShowList)){
                sublist.add(list.get(i));
            }
        }
        return sublist;
    }

    /**
     * 获取模版列信息
     * @return
     */
    public String[] getTemplateColumnArray(List<Map<String,String>> list){
        String[] fields = new String[list.size()];
        for (int i = 0; i < list.size() ; i++) {
            fields[i] = list.get(i).get("COLUMN_ID");
        }
        return fields;
    }

    /**
     * 获取模版中文列信息
     * @return
     */
    public String[] getTemplateColumnNameArray(List<Map<String,String>> list){
        String[] fields = new String[list.size()];
        for (int i = 0; i < list.size() ; i++) {
            fields[i] = list.get(i).get("COLUMN_NAME");
        }
        return fields;
    }
}
