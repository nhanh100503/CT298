package com.gis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.gis.mapper")
public class GisApplication {

	public static void main(String[] args) {
		SpringApplication.run(GisApplication.class, args);
	}

}
