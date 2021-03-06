package com.university.itis.controller;

import com.university.itis.dto.TokenDto;
import com.university.itis.dto.authorization.LoginForm;
import com.university.itis.dto.authorization.RegisterForm;
import com.university.itis.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
class AuthenticationController {
    private final UserService userService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public TokenDto registerUser(@RequestBody RegisterForm registerForm) {
        return userService.register(registerForm);
    }

    @RequestMapping(value = "/auth", method = RequestMethod.POST)
    public TokenDto auth(@RequestBody LoginForm loginForm) {
        return userService.login(loginForm);
    }
}
