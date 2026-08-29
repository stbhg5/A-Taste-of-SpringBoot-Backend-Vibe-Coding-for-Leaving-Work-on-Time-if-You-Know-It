package com.example.todo.web;

import com.example.todo.dto.TodoDtos.TodoResponse;
import com.example.todo.exception.ResourceNotFoundException;
import com.example.todo.service.TodoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/todos")
public class TodoViewController {

    private final TodoService todoService;

    public TodoViewController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "all") String filter, HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute(SessionConst.LOGIN_USER_ID);
        List<TodoResponse> todos = todoService.findAllForUser(userId, filter);
        model.addAttribute("todos", todos);
        model.addAttribute("filter", filter);
        model.addAttribute("userEmail", session.getAttribute(SessionConst.LOGIN_USER_EMAIL));
        return "todos";
    }

    @PostMapping
    public String create(@RequestParam String title, @RequestParam(defaultValue = "all") String filter,
                          HttpSession session, RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute(SessionConst.LOGIN_USER_ID);
        if (title == null || title.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "할 일 내용을 입력해주세요.");
        } else {
            todoService.createForUser(userId, title.trim());
        }
        return "redirect:/todos?filter=" + filter;
    }

    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id, @RequestParam(defaultValue = "all") String filter,
                          HttpSession session, RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute(SessionConst.LOGIN_USER_ID);
        try {
            todoService.toggleForUser(userId, id);
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/todos?filter=" + filter;
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id, @RequestParam String title,
                        @RequestParam(defaultValue = "all") String filter,
                        HttpSession session, RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute(SessionConst.LOGIN_USER_ID);
        if (title == null || title.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "할 일 내용을 입력해주세요.");
        } else {
            try {
                todoService.updateForUser(userId, id, title.trim());
            } catch (ResourceNotFoundException e) {
                redirectAttributes.addFlashAttribute("error", e.getMessage());
            }
        }
        return "redirect:/todos?filter=" + filter;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, @RequestParam(defaultValue = "all") String filter,
                          HttpSession session, RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute(SessionConst.LOGIN_USER_ID);
        try {
            todoService.deleteForUser(userId, id);
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/todos?filter=" + filter;
    }
}
