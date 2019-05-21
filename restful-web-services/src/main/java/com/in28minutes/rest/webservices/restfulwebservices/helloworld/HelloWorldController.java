package com.in28minutes.rest.webservices.restfulwebservices.helloworld;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.in28minutes.rest.webservices.restfulwebservices.helloworld.HelloWorldBean;

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
		return bean;}
		

		@RequestMapping(method=RequestMethod.GET,path="/helloworld/path-variable/{name}")
		public HelloWorldBean getHelloWorldPathVariable(@PathVariable String name) {
			  bean.setMessage(name);
			  return bean;
	}
}
