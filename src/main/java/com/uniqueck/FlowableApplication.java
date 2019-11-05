package com.uniqueck;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
public class FlowableApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlowableApplication.class, args);
	}

}


@Slf4j
@Service
class EmailService {

	Map<String, AtomicInteger> sends = new ConcurrentHashMap<>();

	public void sendWelcomeEmail(String customerid, String email) {
		log.info("sending welcome email for {} to {}", customerid, email);
		sends.computeIfAbsent(email, e-> new AtomicInteger());
		sends.get(email).incrementAndGet();
	}

}




