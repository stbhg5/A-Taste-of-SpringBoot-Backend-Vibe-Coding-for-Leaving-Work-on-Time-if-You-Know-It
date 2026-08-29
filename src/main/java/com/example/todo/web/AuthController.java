package com.example.todo.web;

import com.example.todo.dto.UserDtos.UserRequest;
import com.example.todo.dto.UserDtos.UserResponse;
import com.example.todo.exception.DuplicateEmailException;
import com.example.todo.exception.InvalidCredentialsException;
import com.example.todo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String root(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(SessionConst.LOGIN_USER_ID) != null) {
            return "redirect:/todos";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password,
                         HttpServletRequest request, RedirectAttributes redirectAttributes) {
        try {
            UserResponse user = userService.login(email, password);
            HttpSession session = request.getSession(true);
            session.setAttribute(SessionConst.LOGIN_USER_ID, user.id());
            session.setAttribute(SessionConst.LOGIN_USER_EMAIL, user.email());
            return "redirect:/todos";
        } catch (InvalidCredentialsException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/login";
        }
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String email, @RequestParam String password,
                            @RequestParam String passwordConfirm,
                            Model model, RedirectAttributes redirectAttributes) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            model.addAttribute("error", "올바른 이메일 형식을 입력해주세요.");
            model.addAttribute("email", email);
            return "register";
        }
        if (password == null || password.isBlank() || password.length() < 4) {
            model.addAttribute("error", "비밀번호는 4자 이상이어야 합니다.");
            model.addAttribute("email", email);
            return "register";
        }
        if (!password.equals(passwordConfirm)) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            model.addAttribute("email", email);
            return "register";
        }
        try {
            userService.create(new UserRequest(email, password));
        } catch (DuplicateEmailException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("email", email);
            return "register";
        }
        redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다. 로그인해주세요.");
        return "redirect:/login";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login";
    }
}
