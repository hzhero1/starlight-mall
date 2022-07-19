package ltd.starlight.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("ltd.starlight.mall.dao")
@SpringBootApplication
public class StarlightMallApplication {

	public static void main(String[] args) {
		SpringApplication.run(StarlightMallApplication.class, args);
	}

}
