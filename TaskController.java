package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;

@Controller
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("tasks", taskRepository.findAll());
        return "index";
    }

    @PreAuthorize("!principal.username.equals('bot')")
    @PostMapping("/addTask")
    public String addTask(@ModelAttribute Task task) {
        taskRepository.save(task);
        return "redirect:/";
    }

    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    @GetMapping("/deleteTask/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskRepository.deleteById(id);
        return "redirect:/";
    }

    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    @PostMapping("/updateTask/{id}")
    public String updateTask(@PathVariable Long id, @RequestParam boolean completed) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task != null) {
            task.setCompleted(completed);
            taskRepository.save(task);
        }
        return "redirect:/";
    }

    @PreAuthorize("!principal.username.equals('bot')")
    @GetMapping("/search")
    public String searchTasks(@RequestParam String query, Model model) {
        model.addAttribute("tasks", taskRepository.findByTitleContainingOrDescriptionContaining(query, query));
        return "index";
    }
}
