package ru.kata.spring.boot_security.demo.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final RoleRepository roleRepository;
    private UserService userService;
    private RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.roleRepository = roleRepository;

    }


    @GetMapping()
    public String showAllUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "/allUsers";
    }


    @GetMapping("/showUserByID")
    public String getUserByID(@RequestParam("id") long id, Model model) { // (value = "id", required = false, defaultValue = "0")
        model.addAttribute("user", userService.getUserById(id));
        return "showAdminUser";
    }


    @GetMapping("/new")
    public String addNewUser(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.getDemandedRoles());
        return "new";
    }

    @PostMapping(value = "/addUser")
    public String createUser(@ModelAttribute("newUser") User user, HttpServletRequest request) {
        String[] rolesId = request.getParameterValues("roles");
        List<Role> roles = Arrays.stream(rolesId)
                .map(roleId -> roleService.getRoleById(Long.parseLong(roleId)))
                .collect(Collectors.toList());
        user.setRoles(roles);
        userService.setEncryptedPassword(user); // Вызов метода сервиса
        userService.saveUser(user);
        return "redirect:/admin";
    }

//    @PostMapping(value = "/addUser")
//    public String createUser(@ModelAttribute("newUser") User user, HttpServletRequest request) {
//        List<Role> roles = new ArrayList<>();
//        String[] rolesId = request.getParameterValues("roles");
//
//        for (String roleId : rolesId) {
//            roles.add(roleService.getRoleById(Long.parseLong(roleId))); // Получаем объект Role по ID
//        }
//
//        user.setRoles(roles);
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        userService.saveUser(user);
//        return "redirect:/admin";
//    }


    @DeleteMapping("/delete")
    public String deleteUser(@RequestParam("id") long id) {
        userService.delete(id);
        return "redirect:/admin";
    }

    @GetMapping("/update")
    public String editUser(@RequestParam("id") long id, Model model) {
        User user = userService.getUserById(id);
        if (user.getRoles() == null) {
            user.setRoles(new ArrayList<>());
            model.addAttribute("user", user);
        }
        model.addAttribute("user", userService.getUserById(id));
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Optional<User> user = userService.findByUsername(username);

        model.addAttribute("user", user);
        return "showAdminUser";
    }

    @DeleteMapping(value = "/removeRole")
    public String deleteUserRole(@RequestParam("userId") Long userId,
                                 @RequestParam("roleId") Long roleId) {
        List<Role> userRole = userService.getUserById(userId).getRoles();
        userRole = userRole.stream().filter(role -> !role.getId().equals(roleId)).collect(Collectors.toList());
        User user = userService.getUserById(userId);
        user.setRoles(userRole);
        userService.saveUser(user);
        return "redirect:/admin";
    }

    @PatchMapping("/addRole")
    @Transactional
    public String addRole(@RequestParam("userId") Long userId, @RequestParam("roleId") String roleId) {
        Optional<Role> newRole = roleRepository.getRoleByName(roleId);

        User user = userService.getUserById(userId);
        userService.update(userId, user, String.valueOf(newRole.get()));

        return "redirect:/admin";
    }

}


