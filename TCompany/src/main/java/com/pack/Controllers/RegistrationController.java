package com.pack.Controllers;

import com.pack.Entities.User;
import com.pack.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/registration")
    public String addUser(@Valid User userForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "registration";
        }
        if (userForm.getUsername().equals("")||userForm.getFirstname().equals("")||userForm.getLastname().equals("")||userForm.getPassword().equals("")||userForm.getEmail().equals("")){
            return "registration";
        }
        if (!userForm.getPassword().equals(userForm.getPasswordConfirm())){
            model.addAttribute("passwordError", "Введите пароль заново");
            return "registration";
        }
        String answer = userService.saveUser(userForm);
        if (!answer.equals("")){
            model.addAttribute("usernameError", answer);
            return "registration";
        }
        return "redirect:/login";
    }
}