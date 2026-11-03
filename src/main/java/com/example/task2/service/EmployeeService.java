package com.example.task2.service;

import com.example.task2.exception.EmployeeNotFoundException;
import com.example.task2.model.Employee;
import com.example.task2.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * Поиск с учётом фильтров.
     * name   — часть ФИО (null или пустая строка — без фильтра по имени);
     * active — true / false / null (null — все сотрудники).
     */
    public List<Employee> search(String name, Boolean active) {
        boolean hasName = name != null && !name.isBlank();
        String query = hasName ? name.trim() : null;

        if (hasName && active != null) {
            return employeeRepository.findByFullNameContainingIgnoreCaseAndActive(query, active);
        }
        if (hasName) {
            return employeeRepository.findByFullNameContainingIgnoreCase(query);
        }
        if (active != null) {
            return employeeRepository.findByActive(active);
        }
        return employeeRepository.findAll();
    }

    public Employee getById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    public boolean emailExists(String email) {
        return employeeRepository.existsByEmail(email);
    }

    /** Занят ли email кем-то другим (для редактирования). */
    public boolean emailExists(String email, Long excludeId) {
        if (excludeId == null) {
            return employeeRepository.existsByEmail(email);
        }
        return employeeRepository.existsByEmailAndIdNot(email, excludeId);
    }

    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    public void delete(Long id) {
        employeeRepository.deleteById(id);
    }
}
