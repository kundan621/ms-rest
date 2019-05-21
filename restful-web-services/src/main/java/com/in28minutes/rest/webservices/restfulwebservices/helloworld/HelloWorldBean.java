package com.in28minutes.rest.webservices.restfulwebservices.helloworld;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HelloWorldBean {
	
	@Value("Hello")
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	
	

}
