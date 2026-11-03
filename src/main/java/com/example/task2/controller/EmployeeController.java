package com.example.task2.controller;

import com.example.task2.model.Employee;
import com.example.task2.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public String list(@RequestParam(required = false) String name,
                       @RequestParam(required = false, defaultValue = "all") String active,
                       Model model) {
        Boolean activeFilter = switch (active) {
            case "true" -> Boolean.TRUE;
            case "false" -> Boolean.FALSE;
            default -> null;
        };
        model.addAttribute("employees", employeeService.search(name, activeFilter));
        model.addAttribute("name", name == null ? "" : name);
        model.addAttribute("active", active);
        return "employees-list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("title", "Добавление сотрудника");
        return "employee-form";
    }

    // Сохранение: работает и для добавления (id == null), и для редактирования (id заполнен)
    @PostMapping("/add")
    public String save(@Valid @ModelAttribute("employee") Employee employee,
                       BindingResult bindingResult,
                       Model model) {
        if (!bindingResult.hasFieldErrors("email")
                && employeeService.emailExists(employee.getEmail(), employee.getId())) {
            bindingResult.rejectValue("email", "email.exists",
                    "Сотрудник с таким email уже существует");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("title", employee.getId() == null
                    ? "Добавление сотрудника" : "Редактирование сотрудника");
            return "employee-form";
        }
        employeeService.save(employee);
        return "redirect:/employees";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("employee", employeeService.getById(id));
        model.addAttribute("title", "Редактирование сотрудника");
        return "employee-form";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        employeeService.delete(id);
        return "redirect:/employees";
    }
}
