package srp.single.hr.ioc.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Mapper
@Component
public interface CalculationMapper {

    /**
     * 查询数据
     * @param params
     * @return
     * @throws Exception
     */
    @Select("select * from ${tableName} where 1=1 ${condition} ")
    List<Map<String, String>> getList(Map<String, String> params) throws Exception;

    /**
     * 查询单元格
     * @param params
     * @return
     * @throws Exception
     */
    @Select("select * from hr_calc_avg_info where row_id > 0 and col_id = #{colId} and data_date = #{dataDate} order by row_id asc")
    List<Map<String,String>> queryCalcAvgCell(Map<String, String> params) throws Exception;
}
