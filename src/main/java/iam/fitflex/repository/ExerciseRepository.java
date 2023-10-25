package iam.fitflex.repository;

import iam.fitflex.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    Optional<Exercise> findExerciseByNameEqualsIgnoreCase(String exerciseName);

    boolean existsByNameEqualsIgnoreCase(String exerciseName);
}
