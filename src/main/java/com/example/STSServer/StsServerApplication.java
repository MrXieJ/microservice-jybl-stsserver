package com.example.STSServer;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class StsServerApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(
				StsServerApplication.class)
				.web(true).run(args);
	}
}