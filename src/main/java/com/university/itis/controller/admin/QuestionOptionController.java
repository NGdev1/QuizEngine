package com.university.itis.controller.admin;

import com.university.itis.model.Question;
import com.university.itis.model.QuestionOption;
import com.university.itis.model.Quiz;
import com.university.itis.repository.QuestionOptionRepository;
import com.university.itis.repository.QuestionRepository;
import com.university.itis.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping(value = "/admin")
public class QuestionOptionController {

    @Autowired
    QuizRepository quizRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    QuestionOptionRepository questionOptionRepository;

    @RequestMapping(value = "/quiz/{quizId}/question/{questionId}/add-option", method = RequestMethod.POST)
    public @ResponseBody
    Map addQuestionOption(@PathVariable Long quizId, @PathVariable Long questionId, @ModelAttribute QuestionOption questionOption, Authentication authentication) throws Exception {

        Optional<Quiz> quiz = quizRepository.findById(quizId);
        Optional<Question> question = questionRepository.findById(questionId);

        if (!quiz.isPresent()) {
            throw new Exception("Quiz not exists");
        }

        if (!question.isPresent()) {
            throw new Exception("Question not exists");
        }

        if (!quiz.get().getAuthor().getUsername().equals(authentication.getName())) {
            throw new Exception("Access is denied");
        }

        questionOptionRepository.save(questionOption);

        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("status", "ok");

        return map;
    }

    @RequestMapping(value = "/quiz/{quizId}/question/{questionId}/option/{questionOptionId}", method = RequestMethod.POST)
    public @ResponseBody
    Map addQuestionOption(@PathVariable Long questionOptionId, @ModelAttribute QuestionOption questionOption, Authentication authentication) throws Exception {

        Optional<QuestionOption> oldQuestionOption = questionOptionRepository.findById(questionOptionId);

        if (!oldQuestionOption.isPresent()) {
            throw new Exception("Question option not exists");
        }

        if (!oldQuestionOption.get().getQuestion().getQuiz().getAuthor().getUsername().equals(authentication.getName())) {
            throw new Exception("Access is denied");
        }

        oldQuestionOption.get().setCorrect(questionOption.isCorrect());
        oldQuestionOption.get().setText(questionOption.getText());
        questionOptionRepository.save(oldQuestionOption.get());

        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("status", "ok");

        return map;
    }

    @RequestMapping(value = "/quiz/{quizId}/question/{questionId}/option/{questionOptionId}", method = RequestMethod.DELETE)
    public @ResponseBody
    Map deleteQuestionOption(@PathVariable Long questionOptionId, @PathVariable Long quizId, Authentication authentication) throws Exception {

        Optional<Quiz> quiz = quizRepository.findById(quizId);

        if (!quiz.isPresent()) {
            throw new Exception("Quiz not exists");
        }

        if (!quiz.get().getAuthor().getUsername().equals(authentication.getName())) {
            throw new Exception("Access is denied");
        }

        questionOptionRepository.deleteById(questionOptionId);

        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("status", "ok");

        return map;
    }
}
