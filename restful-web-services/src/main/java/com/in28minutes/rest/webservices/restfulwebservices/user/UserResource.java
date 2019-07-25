package com.in28minutes.rest.webservices.restfulwebservices.user;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

import  static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
	//hateos:hypermedia as the engine of application state
	@GetMapping("/users/{id}")
	public Resource<User> getOneUser(@PathVariable int id) {
		User user= service.findOne(id);
		if(user==null)
			throw new UserNotFoundException("id="+id);
		Resource<User> resource=new Resource<User>(user); 
		ControllerLinkBuilder linkto= linkTo(methodOn(this.getClass()).retrieveAllUsers());
		resource.add(linkto.withRel("all-users"));
		return resource;
	}
	
	@DeleteMapping("/users/{id}")
	public void deleteOneUser(@PathVariable int id) {
		User user= service.deleteOne(id);
		if(user==null)
			throw new UserNotFoundException("id="+id);
		//return user;
	}
	
	
	//add user
	//send status as created: status 201
	@PostMapping("/users")
	public ResponseEntity<Object> addUser(@Valid @RequestBody User user)
	{
		User savedUser=service.save(user);
		URI location= ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedUser.getId()).toUri();
		return ResponseEntity.created(location).build();
	}
	
	
}
