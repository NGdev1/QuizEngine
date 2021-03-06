package com.university.itis.services;

import com.university.itis.dto.question_option.QuestionOptionDto;
import com.university.itis.model.User;

import java.util.List;

public interface QuestionOptionService {
    List<QuestionOptionDto> getAllByQuestionId(Long questionId);
    QuestionOptionDto save(Long questionId, QuestionOptionDto form, User user);
    QuestionOptionDto getById(Long questionId, Long questionOptionId);
    QuestionOptionDto update(Long questionId, Long questionOptionId, QuestionOptionDto form, User user);
    void delete(Long questionId, Long questionOptionId, User user);
}
