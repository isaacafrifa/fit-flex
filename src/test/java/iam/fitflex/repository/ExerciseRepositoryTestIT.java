package iam.fitflex.repository;

import iam.fitflex.AbstractBaseContainer;
import iam.fitflex.entity.Exercise;
import iam.fitflex.entity.MuscleGroup;
import iam.fitflex.enums.ExerciseGroup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DisplayName("Running the Exercise Repository Integration Tests")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ExerciseRepositoryTestIT extends AbstractBaseContainer {
    @Autowired
    private ExerciseRepository underTest;
    @Autowired
    private MuscleGroupRepository muscleGroupRepository;
    private Exercise exercise;
    private MuscleGroup muscleGroup;

    @BeforeEach
    void setUp() {
        muscleGroup = new MuscleGroup(1L, "Chest");
        exercise = new Exercise(
                1L,
                "Bench Press",
                3,
                12,
                LocalDate.now(),
                LocalDate.now(),
                muscleGroup,
                ExerciseGroup.PUSH
        );
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void findExerciseByNameEqualsIgnoreCase_thenReturnPresentOptional() {
        //Given
        muscleGroupRepository.save(muscleGroup);
        underTest.save(exercise);
        //When
        var actual = underTest.findExerciseByNameEqualsIgnoreCase("bench press");
        //Then
        assertAll(
                () -> assertNotNull(actual),
                () -> assertTrue(actual.isPresent())
        );
    }

    @Test
    void findExerciseByNameEqualsIgnoreCase_thenReturnEmptyOptional() {
        //Given
        muscleGroupRepository.save(muscleGroup);
        underTest.save(exercise);
        //When
        var actual = underTest.findExerciseByNameEqualsIgnoreCase("squats");
        //Then
        assertAll(
                () -> assertNotNull(actual),
                () -> assertTrue(actual.isEmpty())
        );
    }

    @Test
    void existsByNameEqualsIgnoreCase_thenReturnTrue() {
        //Given
        muscleGroupRepository.save(muscleGroup);
        underTest.save(exercise);
        //When
        var actual = underTest.existsByNameEqualsIgnoreCase("bench press");
        //Then
        assertTrue(actual);
    }

    @Test
    void existsByNameEqualsIgnoreCase_thenReturnFalse() {
        //Given
        muscleGroupRepository.save(muscleGroup);
        underTest.save(exercise);
        //When
        var actual = underTest.existsByNameEqualsIgnoreCase("pullups");
        //Then
        assertFalse(actual);
    }
}