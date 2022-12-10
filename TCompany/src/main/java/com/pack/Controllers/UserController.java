package com.pack.Controllers;

import com.pack.Entities.User;
import com.pack.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/personal")
    public String lk(@AuthenticationPrincipal User user, Model model){
        model.addAttribute("nickname",user.getUsername());
        model.addAttribute("email",user.getEmail());
        model.addAttribute("name",user.getFirstname());
        model.addAttribute("lastname",user.getLastname());
        model.addAttribute("roles",user.getRoles());
        model.addAttribute("count",user.getTours().size());
        model.addAttribute("usersTours",user.getTours());
        return "personalAcc";
    }

    @GetMapping("/")
    public String nick(@AuthenticationPrincipal User user, Model model){
        if (user!=null) {
            model.addAttribute("nickname", user.getUsername());
            model.addAttribute("name", user.getFirstname());
            model.addAttribute("lastname", user.getLastname());
        }
        return "index";
    }
}