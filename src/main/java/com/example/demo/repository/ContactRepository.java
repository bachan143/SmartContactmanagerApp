package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.Contact;
import com.example.demo.model.User;

public interface ContactRepository extends JpaRepository<Contact, Integer> 
{
	
       //pagination
	   @Query("from Contact as c where c.user.id=:userId")
	   //Current page
	   //contact per page-5
	   public Page<Contact> findContactByUser(@Param("userId")int long1,Pageable pageable);
	   
	   public List<Contact> findByNameContainingAndUser(String name,User user);
	   
	   
	
	
	
	
	
	
	
	
}
