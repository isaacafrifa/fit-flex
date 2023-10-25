package iam.fitflex.dto;

import iam.fitflex.enums.ExerciseGroup;

// This is the request DTO for Exercise
public record ExerciseResponseDto(
        Long id,
        String name,
        int sets,
        int reps,
        String muscleGroupName,
        ExerciseGroup exerciseGroup
        ) {
}
