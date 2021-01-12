package com.example.demo.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Contact;
import com.example.demo.model.User;
import com.example.demo.repository.ContactRepository;
import com.example.demo.repository.UserRepository;

@RestController

public class SearchController {
	@Autowired

	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;

	// search handller
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal) {
		
		User userByName = userRepository.getUserByName(principal.getName());
		List<Contact> findByNameContainingAndUser = contactRepository.findByNameContainingAndUser(query, userByName);

		return ResponseEntity.ok(findByNameContainingAndUser);
	}

}
