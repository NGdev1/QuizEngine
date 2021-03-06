package com.university.itis.mapper;

import com.university.itis.dto.quiz_passing.*;
import com.university.itis.model.QuizPassing;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class QuizPassingMapper {
    private final UserMapper userMapper;
    private final QuizMapper quizMapper;
    private final QuestionMapper questionMapper;
    private final QuestionAnswerMapper questionAnswerMapper;

    public QuizPassingDto toDtoConvert(QuizPassing quizPassing) {
        return QuizPassingDto.builder()
                .id(quizPassing.getId())
                .user(userMapper.toViewDto(quizPassing.getUser()))
                .quiz(quizMapper.toShortDtoConvert(quizPassing.getQuiz()))
                .questions(questionMapper.toListShortDtoConvert(quizPassing.getQuiz().getQuestions()))
                .answers(questionAnswerMapper.toListDtoConvert(quizPassing.getAnswers()))
                .isFinished(quizPassing.getIsFinished())
                .startDate(quizPassing.getStartDate())
                .build();
    }

    public FinishedQuizPassingDto toFinishedDtoConvert(QuizPassing quizPassing) {
        return FinishedQuizPassingDto.builder()
                .id(quizPassing.getId())
                .user(userMapper.toShortDto(quizPassing.getUser()))
                .quiz(quizMapper.toShortDtoConvert(quizPassing.getQuiz()))
                .questions(questionMapper.toListDtoConvert(quizPassing.getQuiz().getQuestions()))
                .answers(questionAnswerMapper.toListDtoConvert(quizPassing.getAnswers()))
                .isFinished(quizPassing.getIsFinished())
                .startDate(quizPassing.getStartDate())
                .result(
                        (float) quizPassing.getAnswers().stream().filter(item -> item.getOption().getIsCorrect()).count() /
                                quizPassing.getQuiz().getQuestions().size()
                )
                .build();
    }

    public QuizPassingShortDto toDtoShortConvert(QuizPassing quizPassing) {
        return QuizPassingShortDto.builder()
                .id(quizPassing.getId())
                .startDate(quizPassing.getStartDate())
                .quiz(quizMapper.toShortDtoConvert(quizPassing.getQuiz()))
                .result(
                        (float) quizPassing.getAnswers().stream().filter(item -> item.getOption().getIsCorrect()).count() /
                                quizPassing.getQuiz().getQuestions().size()
                )
                .build();
    }


    public QuizPassingParticipantDto toParticipantDtoConvert(QuizPassing quizPassing) {
        return QuizPassingParticipantDto.builder()
                .id(quizPassing.getId())
                .startDate(quizPassing.getStartDate())
                .user(userMapper.toShortDto(quizPassing.getUser()))
                .build();
    }

    public QuizPassingSummaryDto toSummaryDtoConvert(QuizPassing quizPassing) {
        return QuizPassingSummaryDto.builder()
                .id(quizPassing.getId())
                .startDate(quizPassing.getStartDate())
                .questionsCount(quizPassing.getQuiz().getQuestions().size())
                .correctAnswersCount((int) quizPassing.getAnswers().stream().filter(item -> item.getOption().getIsCorrect()).count())
                .build();
    }

    public List<QuizPassingShortDto> toListShortDtoConvert(List<QuizPassing> list) {
        return list
                .stream()
                .map(this::toDtoShortConvert)
                .collect(Collectors.toList());
    }

    public List<QuizPassingParticipantDto> toListParticipantDtoConvert(List<QuizPassing> list) {
        return list
                .stream()
                .map(this::toParticipantDtoConvert)
                .collect(Collectors.toList());
    }

    public List<QuizPassingSummaryDto> toListSummaryDtoConvert(List<QuizPassing> list) {
        return list
                .stream()
                .map(this::toSummaryDtoConvert)
                .collect(Collectors.toList());
    }
}
