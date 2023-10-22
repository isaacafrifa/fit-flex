package iam.fitflex.repository;

import iam.fitflex.entity.MuscleGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface MuscleGroupRepository extends JpaRepository<MuscleGroup,Long> {

 Optional<MuscleGroup> findMuscleGroupByNameEqualsIgnoreCase(String muscleGroupName);

 boolean existsByName(String muscleGroupName);
}
