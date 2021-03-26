package srp.single.hr.ioc.entity;

import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class Page<T> extends PageInfo {

    private final static String success_code = "200";

    private final static String fail_code = "500";

    private String code;

    private String msg;


    public Page() {
        super();
        this.code = "500";
        this.msg = "数据加载异常！";
    }

    public Page(Throwable e){
        this();
        this.msg += e.getCause();
    }

    public Page(List<T> list) {
        this(list,8);
        this.code = success_code;
        this.msg = "数据加载成功！";
    }

    public Page(List<T> list, int navigatePages) {
        super(list,navigatePages);
    }

    public Page(String code, String msg, List<T> list){
        super(list);
        this.code = code;
        this.msg = msg;
    }

}
