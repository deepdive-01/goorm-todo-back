package com.example.goormtodoback.repository;

import com.example.goormtodoback.domain.entity.Todo;
import com.example.goormtodoback.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo>  findByUser(User user);
    List<Todo> findByUserAndDateType(User user, String dateType);
    List<Todo> findByUserAndIsCompleted(User user, boolean isCompleted);
    Optional<Todo> findByIdAndUser(Long id, User user);
}