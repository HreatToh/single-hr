package srp.single.hr.ioc.utils;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.spec.ECPoint;
import java.util.Map;

public class S {

    /**
     * 判断是否等于Y
     * @param str
     * @return
     */
    public static boolean isY(String str){
        return StrUtil.equals("Y",str);
    }


    /**
     * 判断是否等于N
     * @param str
     * @return
     */
    public static boolean isN(String str){
        return StrUtil.equals("N",str);
    }


    /**
     * 判断是否不等于Y
     * @param str
     * @return
     */
    public static boolean isNotY(String str){
        return !isY(str);
    }

    /**
     * 判断是否不等于N
     * @param str
     * @return
     */
    public static boolean isNotN(String str){
        return !isN(str);
    }

    /**
     * 格式化字符串
     * @param str
     * @param params
     * @return
     */
    public static String format(String str, Map params){
        //自动根据用户引入的模板引擎库的jar来自动选择使用的引擎
        //TemplateConfig为模板引擎的选项，可选内容有字符编码、模板路径、模板加载方式等，默认通过模板字符串渲染
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig());

        //假设我们引入的是Beetl引擎，则：
        Template template = engine.getTemplate(str);
        //Dict本质上为Map，此处可用Map
        return template.render(params);
    }


}
