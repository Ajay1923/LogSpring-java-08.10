package com.crud.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.crud.demo.model.User;
import com.crud.demo.repository.UserRepository;

import java.util.Optional;

import javax.servlet.http.HttpSession;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private HttpSession httpSession;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; 
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String username, Model model) {
        Optional<User> userOptional = userRepository.findByUsernameAndEnabled(username, true);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            httpSession.setAttribute("userId", user.getId()); 
            return "redirect:/webpage"; 
        } else {
            model.addAttribute("error", "User not found or disabled");
            return "login"; 
        }
    }
    
    @GetMapping("/logout")
    public String handleLogout() {
        httpSession.invalidate(); 
        return "redirect:/login"; 
    }

    @GetMapping("/settings")
    public String showSettingsPage(Model model) {
        Long userId = (Long) httpSession.getAttribute("userId");
        if (userId != null) {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isPresent()) {
                model.addAttribute("user", userOptional.get());
            } else {
                model.addAttribute("error", "User not found.");
            }
        }
        return "settings"; 
    }

    @GetMapping("/settings/edit")
    public String editUserDetails(@RequestParam Long userId, Model model) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            return "editUser"; // Assuming you have a separate edit page
        } else {
            model.addAttribute("error", "User not found.");
            return "redirect:/settings"; 
        }
    }

    @PostMapping("/Logging_cred")
    public String handleUserDetailsSubmission(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        // Validate required fields
        if (user.getFirstName().isEmpty() || user.getLastName().isEmpty() || 
            user.getDateOfBirth().isEmpty() || user.getGender().isEmpty() || 
            user.getEmail().isEmpty() || user.getMobileNumber().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "All fields are required.");
            return "redirect:/settings"; 
        }

        // Find the user by ID and update
        Optional<User> userOptional = userRepository.findById(user.getId());
        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            existingUser.setFirstName(user.getFirstName());
            existingUser.setMiddleName(user.getMiddleName());
            existingUser.setLastName(user.getLastName());
            existingUser.setDateOfBirth(user.getDateOfBirth());
            existingUser.setGender(user.getGender());
            existingUser.setEmail(user.getEmail());
            existingUser.setMobileNumber(user.getMobileNumber());

            userRepository.save(existingUser);
            redirectAttributes.addFlashAttribute("message", "User details updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "User not found.");
        }

        return "redirect:/settings"; 
    }
}
