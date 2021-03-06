package com.university.itis.services.impl;

import com.university.itis.dto.question.QuestionDto;
import com.university.itis.exceptions.InvalidTokenException;
import com.university.itis.exceptions.NotFoundException;
import com.university.itis.exceptions.ValidationException;
import com.university.itis.mapper.QuestionMapper;
import com.university.itis.model.Question;
import com.university.itis.model.Quiz;
import com.university.itis.model.User;
import com.university.itis.repository.QuestionRepository;
import com.university.itis.repository.QuizRepository;
import com.university.itis.services.QuestionService;
import com.university.itis.utils.ErrorEntity;
import com.university.itis.utils.Validator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final Validator validator;
    private final QuestionMapper questionMapper;
    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;

    @Override
    public List<QuestionDto> getAllByQuizId(String quizId) {
        return questionMapper.toListDtoConvert(questionRepository.findAllByQuizId(quizId));
    }

    @Override
    public QuestionDto save(String quizId, QuestionDto form, User user) {
        Optional<ErrorEntity> formErrorOrNull = validator.getSaveQuestionFormError(form);
        if (formErrorOrNull.isPresent()) {
            throw new ValidationException(formErrorOrNull.get());
        }
        Question questionToSave = questionMapper.toQuestion(form);
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NotFoundException("Quiz with id " + quizId + " not found"));
        if (quiz.getAuthor().getId().equals(user.getId()) == false) {
            throw new InvalidTokenException("Доступ запрещен");
        }
        questionToSave.setQuiz(quiz);
        Question savedQuestion = questionRepository.save(questionToSave);
        return questionMapper.toDtoConvert(savedQuestion);
    }

    @Override
    public QuestionDto update(String quizId, Long questionId, QuestionDto form, User user) {
        Optional<ErrorEntity> formErrorOrNull = validator.getSaveQuestionFormError(form);
        if (formErrorOrNull.isPresent()) {
            throw new ValidationException(formErrorOrNull.get());
        }
        Question question = getQuestion(quizId, questionId, user);
        Question questionToSave = questionMapper.toQuestion(form, question);
        Question savedQuestion = questionRepository.save(questionToSave);
        return questionMapper.toDtoConvert(savedQuestion);
    }

    @Override
    public QuestionDto getById(String quizId, Long questionId) {
        return questionMapper.toDtoConvert(
                questionRepository.findByIdAndQuizId(questionId, quizId)
                        .orElseThrow(() -> new NotFoundException("Quiz with id " + quizId + " or question with id " + questionId + " not found"))
        );
    }

    @Override
    public void delete(String quizId, Long questionId, User user) {
        questionRepository.delete(getQuestion(quizId, questionId, user));
    }

    private Question getQuestion(String quizId, Long questionId, User user) {
        Question question = questionRepository.findByIdAndQuizId(questionId, quizId)
                .orElseThrow(() -> new NotFoundException("Quiz with id " + quizId + " or question with id " + questionId + " not found"));
        if (question.getQuiz().getAuthor().getId().equals(user.getId()) == false) {
            throw new InvalidTokenException("Доступ запрещен");
        }
        return question;
    }
}
