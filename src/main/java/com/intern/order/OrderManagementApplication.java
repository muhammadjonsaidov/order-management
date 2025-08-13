package com.intern.order;

import gg.jte.springframework.boot.autoconfigure.JteAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(
		exclude = { JteAutoConfiguration.class }
)
@EnableCaching
public class OrderManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderManagementApplication.class, args);
	}

}
