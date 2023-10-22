package iam.fitflex.dto;

import jakarta.validation.constraints.NotBlank;

public record MuscleGroupDto(
        @NotBlank(message = "Provide muscle-group name")
        String name) {
}
