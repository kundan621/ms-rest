package com.in28minutes.rest.webservices.restfulwebservices.user;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriBuilder;

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
		User user= service.findOne(id);
		if(user==null)
			throw new UserNotFoundException("id="+id);
		return user;
	}
	
	
	//add user
	@PostMapping("/users")
	public ResponseEntity<Object> addUser(@RequestBody User user)
	{
	
		User savedUser=service.save(user);
		URI location= ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedUser.getId()).toUri();
		return ResponseEntity.created(location).build();
	}
	
}
