package config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer
@SpringBootApplication
public class ConfigServer {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServer.class, args);

// Проверка classpath
//        try {
//            Class.forName("org.postgresql.Driver");
//            System.out.println("PostgreSQL driver found in classpath!");
//        } catch (ClassNotFoundException e) {
//            System.err.println("PostgreSQL driver NOT found in classpath!");
//            e.printStackTrace(); // Вывести полный stack trace для диагностики
//        }


    }

}