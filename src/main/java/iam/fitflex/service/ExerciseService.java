package iam.fitflex.service;

import iam.fitflex.dto.ExerciseRequestDto;
import iam.fitflex.dto.ExerciseResponseDto;
import iam.fitflex.dto.PageSliceDto;
import iam.fitflex.entity.Exercise;
import iam.fitflex.exception.ResourceAlreadyExists;
import iam.fitflex.exception.ResourceNotFound;
import iam.fitflex.mappper.ExerciseMapper;
import iam.fitflex.repository.ExerciseRepository;
import iam.fitflex.repository.MuscleGroupRepository;
import iam.fitflex.util.InputFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@Slf4j
public record ExerciseService (ExerciseRepository exerciseRepository,
                               ExerciseMapper exerciseMapper,
                               MuscleGroupRepository muscleGroupRepository,
                               InputFormatter inputFormatter){

    private static final String EXERCISE_NOT_FOUND= "Exercise not found";

    public PageSliceDto getAllExercises(int pageNo, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
        Slice<ExerciseResponseDto> exercisePageSlice= exerciseRepository.findAll(paging)
                .map(exerciseMapper::convertToResponseDto);
        return new PageSliceDto(
                Collections.singletonList(exercisePageSlice.getContent()),
                exercisePageSlice.getSize(),
                exercisePageSlice.getNumber(),
                exercisePageSlice.getNumberOfElements()
        );
    }

    public ExerciseResponseDto getExerciseByName(String exerciseName) {
        // Replace spaces with hyphens in the exercise name
        String formattedExerciseName = inputFormatter.replaceSpacesWithHyphens(exerciseName);
        return exerciseRepository.findExerciseByNameEqualsIgnoreCase(formattedExerciseName)
                .map(exerciseMapper::convertToResponseDto)
                .orElseThrow(() -> {
                    log.error(EXERCISE_NOT_FOUND);
                    return new ResourceNotFound(EXERCISE_NOT_FOUND);}
                );
    }

    public ExerciseResponseDto createExercise(ExerciseRequestDto exerciseRequestDto) {

        //check if item already exists
        String exerciseName = inputFormatter.replaceSpacesWithHyphens(exerciseRequestDto.name());   // Replace spaces with hyphens in the exercise name
        if (exerciseRepository.existsByNameEqualsIgnoreCase(exerciseName)) {
            log.warn("Exercise [{}] already exists", exerciseRequestDto.name());
            throw new ResourceAlreadyExists("Exercise already exists");
        }
        var savedExercise = exerciseRepository.save(exerciseMapper.convertToEntity(exerciseRequestDto));
        return exerciseMapper.convertToResponseDto(savedExercise);
    }

    public ExerciseResponseDto updateExercise(String exerciseName, ExerciseRequestDto tobeUpdatedDto) {
        var existingExercise= getExercise(exerciseName);

        existingExercise.setName(inputFormatter.replaceSpacesWithHyphens(tobeUpdatedDto.name()));
        existingExercise.setSets(tobeUpdatedDto.sets());
        existingExercise.setReps(tobeUpdatedDto.reps());
        existingExercise.setExerciseGroup(tobeUpdatedDto.exerciseGroup());
        // Get muscleGroup using muscle name
        var muscleGroup = muscleGroupRepository
                .findMuscleGroupByNameEqualsIgnoreCase(tobeUpdatedDto.muscleGroupName())
                .orElseThrow(() -> {
                    // If the MuscleGroup doesn't exist, throw NOT FOUND exception
                    log.error("Provided muscle group not found");
                    return new ResourceNotFound("Muscle group name not found");
                });
        existingExercise.setMuscleGroup(muscleGroup);

        var saved = exerciseRepository.save(existingExercise);
        return exerciseMapper.convertToResponseDto(saved);
    }

    public void deleteExercise(String name) {
        var existingExercise= getExercise(name);
        exerciseRepository.delete(existingExercise);
        log.warn("Exercise with name[{}] deleted", name);
    }


    private Exercise getExercise(String exerciseName) {
        String formattedExerciseName = inputFormatter.replaceSpacesWithHyphens(exerciseName);
        return exerciseRepository
                .findExerciseByNameEqualsIgnoreCase(formattedExerciseName)
                .orElseThrow(() -> {
                    log.error(EXERCISE_NOT_FOUND);
                    return new ResourceNotFound(EXERCISE_NOT_FOUND);
                });
    }

}
