package com.ruoyi;

import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

/**
 * 启动程序
 *
 * @author ruoyi
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class RuoYiApplication {
  public static void main(String[] args) {
    // 与 Jackson、MySQL 和 docker-compose 的 GMT+8 配置保持一致，避免不同主机时区导致预约时间错位。
    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    // System.setProperty("spring.devtools.restart.enabled", "false");
    SpringApplication.run(RuoYiApplication.class, args);
    System.out.println(
        "(♥◠‿◠)ﾉﾞ  若依启动成功   ლ(´ڡ`ლ)ﾞ  \n"
            + " .-------.       ____     __        \n"
            + " |  _ _   \\      \\   \\   /  /    \n"
            + " | ( ' )  |       \\  _. /  '       \n"
            + " |(_ o _) /        _( )_ .'         \n"
            + " | (_,_).' __  ___(_ o _)'          \n"
            + " |  |\\ \\  |  ||   |(_,_)'         \n"
            + " |  | \\ `'   /|   `-'  /           \n"
            + " |  |  \\    /  \\      /           \n"
            + " ''-'   `'-'    `-..-'              ");
  }
}
