package com.university.itis.repository;

import com.university.itis.model.Quiz;
import com.university.itis.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findAllByIsActiveIsTrue();
    List<Quiz> findAllByAuthor(User author);
}