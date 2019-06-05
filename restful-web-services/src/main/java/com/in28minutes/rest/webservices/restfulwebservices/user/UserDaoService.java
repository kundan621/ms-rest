package com.in28minutes.rest.webservices.restfulwebservices.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class UserDaoService {

	 private static List<User> users= new ArrayList<User>();
	 
	 static {
		 users.add(new User(1, "kundan", new Date()));
		 users.add(new User(2, "keerthu", new Date()));
		 users.add(new User(3, "Adam", new Date()));
		 users.add(new User(4, "xyz", new Date()));
	 }

	private static int usersCount=4;
	 
	 public List<User> findAll(){
		 return users;
	 }
	 
	 public User save(User user) {
		 if(user.getId()==null)
			 user.setId(++usersCount);
		 users.add(user);
		 return user;
	 }
	 
	 public User findOne(int id) {
		return users.parallelStream().filter(p->p.getId()==id).findAny().orElse(null);
	 }
	 
	 public User deleteOne(int id) {
			User user= users.parallelStream().filter(p->p.getId()==id).findAny().orElse(null);
			if(user!=null) {
				users.remove(user);
				return user;
			}
			else
				return null;
		 }
}
