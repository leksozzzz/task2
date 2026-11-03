package com.example.task2.repository;

import com.example.task2.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    List<Employee> findByFullNameContainingIgnoreCase(String name);

    List<Employee> findByActive(boolean active);

    List<Employee> findByFullNameContainingIgnoreCaseAndActive(String name, boolean active);
}
