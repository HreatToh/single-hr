package srp.single.hr.ioc.entity;

import lombok.Data;

import java.util.List;

@Data
public class Result<T> {

    /**
     * 请求成功  成功标识！
     */
    public final static String SUCCESS = "0";
    /**
     * 登录超时
     */
    public final static String LOGOUT = "1001";

    /**
     *请求成功  失败标识！
     */
    public final static String FAIL = "1";

    /**
     * 请求码
     */
    private String code;

    /**
     * 反馈信息
     */
    private String msg;

    /**
     * 认证
     */
    private String token;

    /**
     * 响应数据
     */
    private Object data;


    private Result() {

    }


    public Result(boolean success,String msg){
        this.code = success ? SUCCESS:FAIL;
        this.msg = msg;
    }

    public Result(boolean success,String msg, Object data ){
        this.code = success ? SUCCESS:FAIL;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 设置令牌
     * @param token
     * @return
     */
    public Result setToken(String token){
        this.token = token;
        return this;
    }

    /**
     * 是成功的
     * @return
     */
    public static Result isSuccess(){
        Result result = new Result();
        result.setCode(SUCCESS);
        return result;
    }


    /**
     * 是失败的
     * @return
     */
    public static Result isFail(){
        Result result = new Result();
        result.setCode(FAIL);
        return result;
    }


    /**
     * 退出登录
     * @return
     */
    public static Result logout(){
        Result result = new Result();
        result.setCode(LOGOUT);
        return result;
    }
}
