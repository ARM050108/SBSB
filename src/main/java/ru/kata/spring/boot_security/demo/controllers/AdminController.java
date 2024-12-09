package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.validation.BindingResult;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final RoleService roleService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminController(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping()
    public String showAllUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "/allUsers";
    }

    @PostMapping("/addUser")
    public String createUser(@ModelAttribute("newUser") @Valid User user, BindingResult result, HttpServletRequest request) {
        if (result.hasErrors()) {
            return "new";
        }

        if (userService.existsByUsername(user.getUsername())) {
            result.rejectValue("username", "error.user", "Username already exists");
            return "new";
        }

        Set<Role> roles = new HashSet<>();
        String[] roleIds = request.getParameterValues("roles");
        if (roleIds != null) {
            for (String roleId : roleIds) {
                roles.add(roleService.getRoleById(Long.parseLong(roleId)));
            }
        }

        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.saveUser(user);
        return "redirect:/admin";
    }




    @DeleteMapping("/delete")
    public String deleteUser(@RequestParam("id") long id) {
        userService.delete(id);
        return "redirect:/admin";
    }

    @GetMapping("/update")
    public String editUser(@RequestParam("id") long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.getDemandedRoles());
        return "/update";
    }

    @PatchMapping("/save")
    public String updateUser(@ModelAttribute("user") User user, @RequestParam("id") int id,
                             @RequestParam(value = "role") String role) {

        userService.update(id, user, role);
        return "redirect:/admin";
    }

    @GetMapping("/showMyProfile")
    public String showMyPage(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        return "showAdminUser";
    }
}
