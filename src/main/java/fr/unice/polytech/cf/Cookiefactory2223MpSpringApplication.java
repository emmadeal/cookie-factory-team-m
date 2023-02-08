package fr.unice.polytech.cf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@EnableAspectJAutoProxy
public class Cookiefactory2223MpSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(Cookiefactory2223MpSpringApplication.class, args);
    }

}
