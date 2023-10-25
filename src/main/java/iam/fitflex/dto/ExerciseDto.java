package iam.fitflex.dto;

import iam.fitflex.enums.ExerciseGroup;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

// This is the request DTO for Exercise
public record ExerciseDto(
        @NotBlank(message = "Provide exercise name")
        String name,
        @Min(value = 1, message = "Sets should be at least 1")
        @Max(value = 10, message = "Sets should not be more than 10")
        int sets,
        @Min(value = 1, message = "Sets should be at least 1")
        @Max(value = 40, message = "Sets should not be more than 40")
        int reps,
        String muscleGroupName, // to represent the muscleGroupName
        ExerciseGroup exerciseGroup
        ) {
}
