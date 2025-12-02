package com.rushcrew.queue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
	"com.rushcrew.queue",
	"com.rushcrew.common"
})
@EntityScan(basePackages = {
	"com.rushcrew.queue",
	"com.rushcrew.common"
})
public class QueueServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(QueueServiceApplication.class, args);
	}

}
