package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User userByName = userRepository.getUserByName(username);
		if (userByName == null)
			throw new UsernameNotFoundException("Colud not found user!!");
		CustomUserDetalis customUserDetalis = new CustomUserDetalis(userByName);
		return customUserDetalis;
	}

}
