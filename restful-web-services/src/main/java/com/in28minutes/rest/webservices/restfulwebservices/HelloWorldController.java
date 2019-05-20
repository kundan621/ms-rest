package com.in28minutes.rest.webservices.restfulwebservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

	@Autowired
	private HelloWorldBean bean;
	
	//@GetMapping(path="/hello")
	@RequestMapping(method=RequestMethod.GET,path="/hello")
	public String getHelloWorld() {
		return "Hello World";
	}
	
	
	@RequestMapping(method=RequestMethod.GET,path="/hellobean")
	public HelloWorldBean getHelloWorldBean() {
		return bean;
	}
}
