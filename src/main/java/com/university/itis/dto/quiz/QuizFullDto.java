package com.university.itis.dto.quiz;

import com.university.itis.dto.question.QuestionDto;
import com.university.itis.dto.quiz_passing.QuizPassingParticipantDto;
import com.university.itis.dto.user.UserShortDto;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuizFullDto {
    private String id;
    private String title;
    private UserShortDto author;
    private String description;
    private Date startDate;
    private Boolean isAnyOrder;
    private Boolean isPublic;
    private List<QuestionDto> questions;
    private List<QuizPassingParticipantDto> results;
}
