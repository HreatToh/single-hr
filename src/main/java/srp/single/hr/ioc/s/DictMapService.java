package srp.single.hr.ioc.s;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import srp.single.hr.ioc.dao.DictMapMapper;

import java.util.List;
import java.util.Map;

@Service
public class DictMapService {


    private Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    private DictMapMapper dictMapMapper ;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    /**
     * 获取全部的字典
     * @return
     */
    public Object getDictMap(){
        Dict dict = Dict.create();
        dict.set("DICT_USERNAME_MAP",getUserNameMap());
        dict.set("DICT_CONFIG_MAP",getConfigMap());
        return dict;
    }

    /**
     * 获取用户名的map
     * @return
     */
    public Dict getUserNameMap(){
        Dict dict = null;
        List<Map<String,String>> list = ListUtil.list(false);
        try{
            list = dictMapMapper.getUserNameMap();
            dict = buildDict(list);
        }catch (Exception e){
            logger.error(e);
        }
        return dict;
    }

    /**
     * 获取用户名的map
     * @return
     */
    public Dict getConfigMap(){
        Dict dict = null;
        List<Map<String,String>> list = ListUtil.list(false);
        try{
            list = dictMapMapper.getConfigMap();
            dict = buildDict(list);
        }catch (Exception e){
            logger.error(e);
        }
        return dict;
    }

    /**
     * 构建字典
     * @param list
     * @return
     * @throws Exception
     */
    private Dict buildDict(List<Map<String,String>> list) throws Exception{
        Dict dict = Dict.create();
        if (list !=null && list.size() > 0){
            for (int i = 0; i < list.size() ; i++) {
                String key = list.get(i).get("DICTKEY");
                String value = list.get(i).get("DICTVALUE");
                dict.set(key,value);
            }
        }
        return dict;
    }

    /**
     * 获取字典
     * @param params
     * @return
     * @throws Exception
     */
    public Object getDict(Map<String, String> params){
        Dict dict = null;
        List<Map<String,String>> list = ListUtil.list(false);
        try{
            String dictType = params.get("dictType");
            if (StrUtil.equals(dictType,"DICT_USERNAME_MAP")){
                dict = getUserNameMap();
            }else{
                list = dictMapMapper.getDict(dictType);
                dict = buildDict(list);
            }
        }catch (Exception e){
            logger.error(e);
        }
        return dict;
    }

    /**
     * 获取字典
     * @param key
     * @return
     * @throws Exception
     */
    public Object getDict(String key){
        Map<String,String> params = MapUtil.newHashMap();
        params.put("dictType",key);
        return getDict(params);
    }

    /**
     * 获取配置信息
     * @param key
     * @param subKey
     * @return
     */
    public String getConfig(String key,String subKey){
        String sql = "";
        String value = "";
        try {
            sql = "select dict_name as DICTNAME from hr_dict_info where dict_type = ? and dict_id = ? and is_sys='Y' and dict_enable = 'Y' order by dict_order asc";
            Map<String, Object> map = jdbcTemplate.queryForMap(sql, key, subKey);
            if (ObjectUtil.isNotNull(map)){
                value = Convert.convert(String.class,map.get("DICTNAME"));
            }else{
                logger.warn(StrUtil.format("配置 DICT_TYPE：{},DICT_ID：{} 未找到！" , key , subKey));
            }
        }catch (Exception e){
            logger.error(e);
        }
        return value;
    }


    /**
     * 获取配置信息带默认值
     * @param key
     * @param subKey
     * @return
     */
    public String getConfig(String key, String subKey, String defaultValue ){
        String config = getConfig(key, subKey);
        return StrUtil.isNotBlank(config)? config : defaultValue;
    }
}
