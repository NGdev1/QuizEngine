package com.university.itis.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "quiz")
@Getter
@Setter
public class Quiz implements Comparable<Quiz> {

    @Id
    private String id;

    @Column(nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column
    private String description;

    @Column
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private Date startDate;

    @Column(name = "is_any_order")
    private boolean isAnyOrder;

    @Column(name = "is_public")
    private boolean isPublic;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizPassing> results = new ArrayList<>();

    @Override
    public int compareTo(Quiz o) {
        final int BEFORE = -1;
        final int AFTER = 1;
        if (o == null) {
            return BEFORE;
        }
        Date thisDate = this.getStartDate();
        Date oDate = o.getStartDate();
        if (thisDate == null) {
            return AFTER;
        } else if (oDate == null) {
            return BEFORE;
        } else {
            return thisDate.compareTo(oDate);
        }
    }
}
