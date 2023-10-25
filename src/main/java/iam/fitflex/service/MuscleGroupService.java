package iam.fitflex.service;

import iam.fitflex.dto.MuscleGroupDto;
import iam.fitflex.dto.MuscleGroupResponseDto;
import iam.fitflex.dto.PageSliceDto;
import iam.fitflex.entity.MuscleGroup;
import iam.fitflex.exception.ResourceAlreadyExists;
import iam.fitflex.exception.ResourceNotFound;
import iam.fitflex.mappper.MuscleGroupMapper;
import iam.fitflex.repository.MuscleGroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@Slf4j
public record MuscleGroupService(MuscleGroupRepository muscleGroupRepository, MuscleGroupMapper muscleGroupMapper) {

    private static final String MUSCLE_GROUP_NOT_FOUND= "Muscle Group not found";

    public PageSliceDto getAllMuscleGroupsByPaginationAndSorting(int pageNo, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
        Slice<MuscleGroupResponseDto> muscleGroupPageSlice= muscleGroupRepository.findAll(paging)
                .map(muscleGroupMapper::convertToResponseDto);
        return new PageSliceDto(
                Collections.singletonList(muscleGroupPageSlice.getContent()),
                muscleGroupPageSlice.getSize(),
                muscleGroupPageSlice.getNumber(),
                muscleGroupPageSlice.getNumberOfElements()
        );
    }

    public MuscleGroupResponseDto getMuscleGroupByName(String muscleGroupName) {
        return muscleGroupRepository.findMuscleGroupByNameEqualsIgnoreCase(muscleGroupName)
                .map(muscleGroupMapper::convertToResponseDto)
                .orElseThrow(() -> {
                    log.error(MUSCLE_GROUP_NOT_FOUND);
                    return new ResourceNotFound(MUSCLE_GROUP_NOT_FOUND);}
                );
    }

    public MuscleGroupResponseDto createMuscleGroup(MuscleGroupDto muscleGroupDto) {
        //check if item already exists
        if (muscleGroupRepository.existsByName(muscleGroupDto.name())) {
            log.warn("MuscleGroup [{}] already exists", muscleGroupDto.name());
            throw new ResourceAlreadyExists("MuscleGroup already exists");
        }
        var savedMuscleGroup = muscleGroupRepository.save(muscleGroupMapper.convertToEntity(muscleGroupDto));
        return muscleGroupMapper.convertToResponseDto(savedMuscleGroup);
    }

    public MuscleGroupResponseDto updateMuscleGroup(String muscleGroupName, MuscleGroupDto tobeUpdatedDto) {
        var existingMuscleGroup = getExistingMuscleGroup(muscleGroupName);
        existingMuscleGroup.setName(tobeUpdatedDto.name());
        var saved = muscleGroupRepository.save(existingMuscleGroup);
        return muscleGroupMapper.convertToResponseDto(saved);
    }

    public void deleteMuscleGroup(String name) {
        var existingMuscleGroup = getExistingMuscleGroup(name);
        muscleGroupRepository.delete(existingMuscleGroup);
        log.warn("MuscleGroup with name[{}] deleted", name);
    }


    private MuscleGroup getExistingMuscleGroup(String muscleGroupName) {
        return muscleGroupRepository
                .findMuscleGroupByNameEqualsIgnoreCase(muscleGroupName)
                .orElseThrow(() -> {
                    log.error(MUSCLE_GROUP_NOT_FOUND);
                    return new ResourceNotFound(MUSCLE_GROUP_NOT_FOUND);
                });
    }
}
