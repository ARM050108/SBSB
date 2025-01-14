package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {



    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;

    }


    @GetMapping()
    public String showAllUsers(Model model, Principal principal) {

        model.addAttribute("users", userService.findAll());
        model.addAttribute("createUser", new User());
        model.addAttribute("currentUser", userService.findByUsername(principal.getName()));

        return "admin";
    }


    @GetMapping("/new")
    public String addNewUser(Model model) {

        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.getDemandedRoles());

        return "admin";
    }

    @PostMapping(value = "/addUser")
    public String createUser(@ModelAttribute("newUser") User user, HttpServletRequest request) {

        List<Role> roles = new ArrayList<>();
        String[] rolesId = request.getParameterValues("roles");

        for (String roleId : rolesId) {
            roles.add(roleService.getRoleById(Long.parseLong(roleId)));
        }

        user.setRoles(roles);
        userService.setEncryptedPassword(user);
        userService.saveUser(user);

        return "redirect:/admin";
    }

    @DeleteMapping("/delete")
    public String deleteUser(@RequestParam("id") long id) {

        userService.delete(id);

        return "redirect:/admin";
    }


    @PatchMapping("/save")
    public String updateUser(@ModelAttribute("user") User user, @RequestParam("id") int id,
                             @RequestParam(value = "roles") List<Role> roles) {

        userService.update(id, user, roles);

        return "redirect:/admin";
    }

}