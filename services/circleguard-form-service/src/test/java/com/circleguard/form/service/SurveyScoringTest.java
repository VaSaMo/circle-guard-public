package com.circleguard.form.service;

import com.circleguard.form.model.HealthSurvey;
import com.circleguard.form.model.Question;
import com.circleguard.form.model.QuestionType;
import com.circleguard.form.model.Questionnaire;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SurveyScoringTest {

    private final SymptomMapper mapper = new SymptomMapper();

    @Test
    void shouldCalculateCorrectRiskScore() {
        UUID q1Id = UUID.randomUUID();
        UUID q2Id = UUID.randomUUID();
        
        Question q1 = Question.builder()
                .id(q1Id)
                .text("Do you have a fever?")
                .type(QuestionType.YES_NO)
                .build();
        Question q2 = Question.builder()
                .id(q2Id)
                .text("Difficulty breathing?")
                .type(QuestionType.YES_NO)
                .build();
        
        Questionnaire questionnaire = Questionnaire.builder()
                .questions(List.of(q1, q2))
                .build();
        
        HealthSurvey survey = HealthSurvey.builder()
                .responses(Map.of(
                        q1Id.toString(), "YES",
                        q2Id.toString(), "YES"
                ))
                .build();
        
        int score = mapper.calculateRiskScore(survey, questionnaire);
        
        assertEquals(15, score); // 5 (fever) + 10 (breathing)
    }
}
