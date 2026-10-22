package com.rayhan.email;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class EmailApplication {

	public static void main(String[] args) {
		// Muat file .env menjadi System property agar placeholder
		// ${MAIL_USERNAME} dan ${MAIL_PASSWORD} pada application.properties ter-resolve.
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
		dotenv.entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));

		SpringApplication.run(EmailApplication.class, args);
	}

}
