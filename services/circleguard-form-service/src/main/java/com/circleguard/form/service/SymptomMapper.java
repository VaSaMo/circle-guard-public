package com.circleguard.form.service;

import com.circleguard.form.model.HealthSurvey;
import com.circleguard.form.model.Questionnaire;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SymptomMapper {

    /**
     * Determines if the survey responses indicate symptoms.
     * Logic: If any question containing keywords (fever, cough) has a 'YES'
     * response.
     */
    public boolean hasSymptoms(HealthSurvey survey, Questionnaire questionnaire) {
        if (survey.getResponses() == null || questionnaire == null || questionnaire.getQuestions() == null) {
            return false;
        }

        Map<String, Object> responses = survey.getResponses();

        return questionnaire.getQuestions().stream()
                .filter(q -> responses.containsKey(q.getId().toString()))
                .anyMatch(q -> {
                    String answer = String.valueOf(responses.get(q.getId().toString()));
                    String text = q.getText().toLowerCase();

                    if ("YES".equalsIgnoreCase(answer)) {
                        return text.contains("fever") || text.contains("cough") || text.contains("breathing");
                    }

                    // For multi-choice, if anything is selected (assuming selecting anything
                    // indicates symptoms)
                    if (q.getType().toString().contains("CHOICE") && answer != null && !answer.isEmpty()
                            && !"[]".equals(answer)) {
                        return text.contains("symptoms");
                    }

                    return false;
                });
    }

    /**
     * Calculates a risk score based on the number of symptoms reported.
     */
    public int calculateRiskScore(HealthSurvey survey, Questionnaire questionnaire) {
        if (survey.getResponses() == null || questionnaire == null || questionnaire.getQuestions() == null) {
            return 0;
        }

        Map<String, Object> responses = survey.getResponses();
        int score = 0;

        for (var q : questionnaire.getQuestions()) {
            String answer = String.valueOf(responses.get(q.getId().toString()));
            if ("YES".equalsIgnoreCase(answer)) {
                String text = q.getText().toLowerCase();
                if (text.contains("fever"))
                    score += 5;
                if (text.contains("cough"))
                    score += 3;
                if (text.contains("breathing"))
                    score += 10;
            }
        }
        return score;
    }
}
