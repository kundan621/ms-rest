package com.in28minutes.rest.webservices.restfulwebservices.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserResource {

	@Autowired
	private UserDaoService service;
	//retrieve all users
	@GetMapping("/users")
	public List<User> retrieveAllUsers(){
		return service.findAll();
	}
	

	//retrieve one user
	@GetMapping("/users/{id}")
	public User getOneUser(@PathVariable int id) {
		return service.findOne(id);
	}
	
	
	//add user
	@PostMapping("/users")
	public void addUser(@RequestBody User user)
	{
	
		User savedUser=service.save(user);
	}
	
}
