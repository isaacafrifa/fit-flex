package iam.fitflex.repository;

import iam.fitflex.AbstractBaseContainer;
import iam.fitflex.entity.MuscleGroup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DisplayName("Running the MuscleGroup Repository Integration Tests")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MuscleGroupRepositoryTestIT extends AbstractBaseContainer {
    @Autowired
    private MuscleGroupRepository underTest;
    private MuscleGroup muscleGroup;

    @BeforeEach
    void setUp() {
        muscleGroup = new MuscleGroup(1L, "Chest");
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void findMuscleGroupByNameEqualsIgnoreCase_thenReturnEmptyOptional() {
        //Given
        underTest.save(muscleGroup);
        //When
        var actual = underTest.findMuscleGroupByNameEqualsIgnoreCase("triceps");
        //Then
        assertAll(
                ()-> assertNotNull(actual),
                ()-> assertTrue(actual.isEmpty())
        );
    }

    @Test
    void findMuscleGroupByNameEqualsIgnoreCase_thenReturnPresentOptional() {
        //Given
        underTest.save(muscleGroup);
        //When
        var actual = underTest.findMuscleGroupByNameEqualsIgnoreCase("chest");
        //Then
        assertAll(
                ()-> assertNotNull(actual),
                ()-> assertTrue(actual.isPresent())
        );
    }

    @Test
    void existsByNameEqualsIgnoreCase_thenReturnTrue() {
        //Given
        underTest.save(muscleGroup);
        //When
        var actual = underTest.existsByNameEqualsIgnoreCase("chest");
        //Then
         assertTrue(actual);
    }

    @Test
    void existsByNameEqualsIgnoreCase_thenReturnFalse() {
        //Given
        underTest.save(muscleGroup);
        //When
        var actual = underTest.existsByNameEqualsIgnoreCase("triceps");
        //Then
        assertFalse(actual);
    }
}