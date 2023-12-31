package iam.fitflex.mappper;

import iam.fitflex.dto.MuscleGroupDto;
import iam.fitflex.dto.MuscleGroupResponseDto;
import iam.fitflex.entity.MuscleGroup;
import org.springframework.stereotype.Component;

@Component
public class MuscleGroupMapper {

    public MuscleGroupResponseDto convertToResponseDto(MuscleGroup muscleGroup) {
        return new MuscleGroupResponseDto(muscleGroup.getId(), muscleGroup.getName());
    }

    public MuscleGroup convertToEntity(MuscleGroupDto muscleGroupDto) {
        // Replace spaces with hyphens in the exercise name
        String name = muscleGroupDto.name().replace(" ", "-");
        return new MuscleGroup(null, name);
    }
}
