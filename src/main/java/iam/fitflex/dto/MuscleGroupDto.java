package iam.fitflex.dto;

import jakarta.validation.constraints.NotBlank;

// This is the request DTO for MuscleGroup
public record MuscleGroupDto(
        @NotBlank(message = "Provide muscle-group name")
        String name) {
}
