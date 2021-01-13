package com.example.demo.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.helpper.Message;
import com.example.demo.model.Contact;
import com.example.demo.model.User;
import com.example.demo.repository.ContactRepository;
import com.example.demo.repository.UserRepository;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;

	@ModelAttribute
	public void addCommondata(Model model, Principal principal) {

		String name = principal.getName();
		
		// get the user name
		User userByName = userRepository.getUserByName(name);
		
		model.addAttribute("user", userByName);

	}

	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "User DashBoard");

		return "normal/user_dasboard";
	}

	// open add form handller
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

	@PostMapping("/process-contact")
	public String saveContactForm(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {
		try {
			String name = principal.getName();

			User userByName = userRepository.getUserByName(name);
			// processing and uplodaing file
			if (file.isEmpty()) {
			
				contact.setImage("contact1.png");
			} else {
				contact.setImage(file.getOriginalFilename());
				File file2 = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(file2.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
			}

			userByName.getContacts().add(contact);
			contact.setUser(userByName);
			userRepository.save(userByName);
			session.setAttribute("message", new Message("Your contact is Added!!" + " Add more...", "success"));

		} catch (Exception e) {
			
			session.setAttribute("message", new Message("Something went wrong!! Try again...", "danger"));
		}
		return "normal/add_contact_form";
	}

	// show contacts handler
	// per page-5[n[
	// current page -0[page]
	@RequestMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") int page, Model model, Principal principal) {
		model.addAttribute("title", "Show Contacts");
		String name = principal.getName();
		User userByName = userRepository.getUserByName(name);
		Pageable pageable = PageRequest.of(page, 5);

		Page<Contact> findContactByUser = contactRepository.findContactByUser(userByName.getId(), pageable);
		model.addAttribute("contacts", findContactByUser);
		model.addAttribute("currentPage", page);
	
		model.addAttribute("totalPage", findContactByUser.getTotalPages());
		return "normal/show_contasts";
	}

	// showing Particular Contact Detalis
	@RequestMapping("/{cId}/contact")
	public String showContactDetails(@PathVariable("cId") int cId, Model model, Principal principal) {
		
		Optional<Contact> findById = contactRepository.findById(cId);

		Contact contact = findById.get();

		String username = principal.getName();
		User userByName = userRepository.getUserByName(username);
		if (userByName.getId() == contact.getUser().getId())
			model.addAttribute("contact", contact);

		return "normal/contact_detail";
	}

	// delete Contact handler
	@RequestMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId") int cId, Model model, HttpSession session,
			Principal principal) {
		Optional<Contact> findById = contactRepository.findById(cId);
		Contact contact = findById.get();
//					   contact.setUser(null);
//					    contactRepository.delete(contact);

		User userByName = userRepository.getUserByName(principal.getName());

		userByName.getContacts().remove(contact);
		userRepository.save(userByName);

		session.setAttribute("message", new Message("contact deleted successfull!!", "success"));

		return "redirect:/user/show-contacts/0";

	}

	// updating contact handller
	@PostMapping("/update-contact/{cId}")
	public String updateContact(@PathVariable("cId") int cId, Model model) {
		model.addAttribute("title", "Update Contact");
		Contact contact = contactRepository.findById(cId).get();
		model.addAttribute("contact", contact);

		return "normal/update_form";
	}

	@PostMapping("/process-product")
	public String saveupdateContact(@ModelAttribute() Contact contact, @RequestParam("profileImage") MultipartFile file,
			Model model, HttpSession session, Principal principal) {

		try

		{

			// delete old photo

			// old Contact Details
			Contact contact2 = contactRepository.findById(contact.getcId()).get();
			if (!file.isEmpty()) {
				File deleteFile = new ClassPathResource("static/img").getFile();
				File file1 = new File(deleteFile, contact2.getImage());

				file1.delete();

				// update new Photo
				File file2 = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(file2.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());

			} else {
				contact.setImage(contact2.getImage());
			}
			User user = userRepository.getUserByName(principal.getName());
			contact.setUser(user);
			contactRepository.save(contact);
			session.setAttribute("message", new Message("Your contact is Updated..", "success"));
		} catch (Exception e) {
			e.printStackTrace();

		}

		
		return "redirect:/user/" + contact.getcId() + "/contact";
	}

	// Your profile
	@GetMapping("/profile")
	public String yourprofile(Model model) {
		model.addAttribute("title", "Profile Page");
		return "normal/profile";
	}

	// open settings handler
	@GetMapping("/settings")
	public String openSettings() {
		return "normal/settings";
	}

	@PostMapping("/change-password")
	public String chnagePassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword, Principal principal, HttpSession session) {
		

		User userByName = userRepository.getUserByName(principal.getName());

		if (bCryptPasswordEncoder.matches(oldPassword, userByName.getPassword())) {
			// chnage the password
			userByName.setPassword(bCryptPasswordEncoder.encode(newPassword));
			userRepository.save(userByName);
			session.setAttribute("message", new Message("Your Password is sucessfull Changed", "success"));
		} else {
			session.setAttribute("message", new Message("Please correct oldPassword!!!", "danger"));
			return "redirect:/user/settings";

		}

		return "redirect:/user/index";
	}
}
