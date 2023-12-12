package iam.fitflex.service;

import iam.fitflex.dto.ExerciseDto;
import iam.fitflex.dto.ExerciseResponseDto;
import iam.fitflex.entity.Exercise;
import iam.fitflex.entity.MuscleGroup;
import iam.fitflex.enums.ExerciseGroup;
import iam.fitflex.exception.ResourceAlreadyExists;
import iam.fitflex.exception.ResourceNotFound;
import iam.fitflex.mappper.ExerciseMapper;
import iam.fitflex.repository.ExerciseRepository;
import iam.fitflex.util.InputFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Running the Exercise Service Tests")
@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {
    @Mock
    private ExerciseRepository exerciseRepository;
    @Mock
    private ExerciseMapper exerciseMapper;
    @Mock
    private InputFormatter inputFormatter;
    @InjectMocks
    private ExerciseService underTest;
    private Exercise exercise;
    private MuscleGroup muscleGroup;
    private ExerciseResponseDto exerciseResponseDto;
    private static final long EXERCISE_ID=1L;
    private static final int EXERCISE_SETS=3;
    private static final int EXERCISE_REPS=12;
    private static final String EXERCISE_NAME="bench_press";

    private static final ExerciseGroup EXERCISE_GROUP=ExerciseGroup.PUSH;


    @BeforeEach
    void setUp() {
        muscleGroup = new MuscleGroup(1L, "Chest");
        exercise = new Exercise(
                EXERCISE_ID,
                EXERCISE_NAME,
                EXERCISE_SETS,
                EXERCISE_REPS,
                LocalDate.now(),
                LocalDate.now(),
                muscleGroup,
                EXERCISE_GROUP
        );
        exerciseResponseDto = new ExerciseResponseDto(EXERCISE_ID, EXERCISE_NAME,EXERCISE_SETS,
                EXERCISE_REPS, muscleGroup.getName(),
                EXERCISE_GROUP);
    }


    @Test
    void givenValidPageNumberAndSize_getAllExercises() {
        //Given
        int pageNumber = 1;
        int pageSize = 5;
        Page<Exercise> exercisePage = new PageImpl<>(Collections.singletonList(exercise));
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by("id").descending());
        ArgumentCaptor<Pageable> pageableCaptor =
                ArgumentCaptor.forClass(Pageable.class);
        given(exerciseRepository.findAll(paging)).willReturn(exercisePage);
        //When
        underTest.getAllExercises(pageNumber, pageSize, "id");
        //Then
        verify(exerciseRepository, times(1)).findAll(pageableCaptor.capture());
        // Assert Pageable values
        var captorValue = pageableCaptor.getValue();
        assertAll(
                () -> assertEquals(pageNumber, captorValue.getPageNumber()),
                () -> assertEquals(pageSize, captorValue.getPageSize()),
                () -> assertEquals(Sort.by("id").descending(), captorValue.getSort())
        );
    }

    @Test
    void givenValidName_getExerciseByName_thenReturnExercise() {
        // Given
        String formattedName = "bench_press";
        given(inputFormatter.replaceSpacesWithHyphens(anyString())).willReturn(formattedName);
        given(exerciseRepository.findExerciseByNameEqualsIgnoreCase(formattedName))
                .willReturn(Optional.ofNullable(exercise));
        given(exerciseMapper.convertToResponseDto(exercise)).willReturn(exerciseResponseDto);

        // When
        var actual = underTest.getExerciseByName("Bench Press");

        // Then
        verify(inputFormatter).replaceSpacesWithHyphens("Bench Press");
        verify(exerciseRepository).findExerciseByNameEqualsIgnoreCase(formattedName);
        verify(exerciseMapper).convertToResponseDto(exercise);
        assertAll(
                ()-> assertNotNull(actual),
                // Assert on the entire ExerciseResponseDto
                () -> assertEquals(exerciseResponseDto.id(), actual.id()),
                () -> assertEquals(exerciseResponseDto.name(), actual.name()),
                () -> assertEquals(exerciseResponseDto.sets(), actual.sets()),
                () -> assertEquals(exerciseResponseDto.reps(), actual.reps()),
                () -> assertEquals(exerciseResponseDto.muscleGroupName(), actual.muscleGroupName()),
                () -> assertEquals(exerciseResponseDto.exerciseGroup(), actual.exerciseGroup())
        );
    }

    @Test
    void givenInvalidName_getExerciseByName_thenThrowResourceNotFoundException() {
        // Given
        String exerciseName = "Invalid Exercise";
        String formattedName = "invalid-exercise";
        given(inputFormatter.replaceSpacesWithHyphens(anyString())).willReturn(formattedName);
        given(exerciseRepository.findExerciseByNameEqualsIgnoreCase(formattedName))
                .willReturn(Optional.empty());

        // When + Then
        assertThrows(ResourceNotFound.class,
                () -> underTest.getExerciseByName(exerciseName),
                "Expected getExerciseByName to throw ResourceNotFound for invalid exercise name");

        // Verify method invocations
        verify(inputFormatter).replaceSpacesWithHyphens(exerciseName);
        verify(exerciseRepository).findExerciseByNameEqualsIgnoreCase(formattedName);
        verify(exerciseMapper, never()).convertToResponseDto(any());
    }


    @Test
    void givenValidExerciseDto_createExercise_thenReturnResponseDto() {
        //Given
        ExerciseDto inputDto = new ExerciseDto("Bench Press", EXERCISE_SETS,
                EXERCISE_REPS, muscleGroup.getName(), EXERCISE_GROUP);
        String formattedName = "bench_press";
        given(inputFormatter.replaceSpacesWithHyphens(anyString())).willReturn(formattedName);
        given(exerciseRepository.existsByNameEqualsIgnoreCase(formattedName))
                .willReturn(false);
        given(exerciseRepository.save(any())).willReturn(exercise);
        given(exerciseMapper.convertToResponseDto(exercise)).willReturn(exerciseResponseDto);

        //When
        var actual = underTest.createExercise(inputDto);
        //Then
        assertNotNull(actual);
        assertEquals(exercise.getId(), actual.id());
        assertEquals(exercise.getName(), actual.name());
        assertEquals(exercise.getSets(), actual.sets());
        assertEquals(exercise.getReps(), actual.reps());
        assertEquals(exercise.getMuscleGroup().getName(), actual.muscleGroupName());
        assertEquals(exercise.getExerciseGroup(), actual.exerciseGroup());
    }

    @Test
    void givenExerciseDtoAlreadyExists_createExercise_thenThrowResourceNotFoundException(){
        //Given
        ExerciseDto inputDto = new ExerciseDto("Bench Press", EXERCISE_SETS,
                EXERCISE_REPS, muscleGroup.getName(), EXERCISE_GROUP);
        String formattedName = "bench_press";

        given(inputFormatter.replaceSpacesWithHyphens(anyString())).willReturn(formattedName);
        given(exerciseRepository.existsByNameEqualsIgnoreCase(formattedName))
                .willReturn(true);

        // When + Then
        assertThrows(ResourceAlreadyExists.class,
                () -> underTest.createExercise(inputDto),
                "Expected createExercise to throw ResourceAlreadyExists for already existing exercise");

        // Verify method invocations
        verify(exerciseRepository, never()).save(any());
        verify(exerciseMapper, never()).convertToResponseDto(any());
    }

//    @Test
//    void updateExercise() {
//    }
//
//    @Test
//    void deleteExercise() {
//    }

}