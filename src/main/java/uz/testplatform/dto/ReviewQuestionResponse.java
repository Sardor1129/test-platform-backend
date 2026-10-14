package uz.testplatform.dto;


import java.util.List;

public record ReviewQuestionResponse(
        Integer orderNumber,
        Long questionId,
        String text,
        String imageUrl,
        boolean answered,
        boolean correct,          // savolga to'g'ri javob berilganmi
        List<ReviewOptionResponse> options
) {}