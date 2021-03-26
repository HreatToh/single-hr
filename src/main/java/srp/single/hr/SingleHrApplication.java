package srp.single.hr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Import;

@Import(cn.hutool.extra.spring.SpringUtil.class)
@SpringBootApplication
@ServletComponentScan("srp.single.hr.filter")
public class SingleHrApplication {

    public static void main(String[] args) {
        SpringApplication.run(SingleHrApplication.class, args);
    }

}
