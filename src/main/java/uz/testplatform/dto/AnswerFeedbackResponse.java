package uz.testplatform.dto;

public record AnswerFeedbackResponse(
       boolean correct,
       Long correctOptionId
) {}
