package com.marcuseisele.example.twolayercache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class TwolayercacheApplication {

	public static void main(String[] args) {
		SpringApplication.run(TwolayercacheApplication.class, args);
	}

}

