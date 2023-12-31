package iam.fitflex.mappper;

import iam.fitflex.dto.ExerciseRequestDto;
import iam.fitflex.dto.ExerciseResponseDto;
import iam.fitflex.entity.Exercise;
import iam.fitflex.entity.MuscleGroup;
import iam.fitflex.repository.MuscleGroupRepository;
import iam.fitflex.util.InputFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class ExerciseMapper {

    private final MuscleGroupRepository muscleGroupRepository;
    private final InputFormatter inputFormatter;

    public ExerciseResponseDto convertToResponseDto(Exercise exercise) {
        return new ExerciseResponseDto(exercise.getId(),
                exercise.getName(),
                exercise.getSets(),
                exercise.getReps(),
                exercise.getMuscleGroup().getName(),
                exercise.getExerciseGroup());
    }

    public Exercise convertToEntity(ExerciseRequestDto exerciseRequestDto) {
        Exercise exercise = new Exercise();

        // Replace spaces with hyphens in the exercise name
        String exerciseName = inputFormatter.replaceSpacesWithHyphens(exerciseRequestDto.name());
        exercise.setName(exerciseName);
        exercise.setSets(exerciseRequestDto.sets());
        exercise.setReps(exerciseRequestDto.reps());

        // Retrieve the MuscleGroup entity by its name
        var muscleGroupName = exerciseRequestDto.muscleGroupName();
        var muscleGroup = muscleGroupRepository
                .findMuscleGroupByNameEqualsIgnoreCase(muscleGroupName)
                .orElseGet(() -> {
                    // If the MuscleGroup doesn't exist, create a new one
                    MuscleGroup newMuscleGroup = new MuscleGroup();
                    newMuscleGroup.setName(muscleGroupName);
                    return muscleGroupRepository.save(newMuscleGroup);
                });
        exercise.setMuscleGroup(muscleGroup);
        exercise.setExerciseGroup(exerciseRequestDto.exerciseGroup());
        return exercise;
    }
}
