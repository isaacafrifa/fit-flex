package iam.fitflex.controller;

import iam.fitflex.dto.MuscleGroupDto;
import iam.fitflex.dto.MuscleGroupResponseDto;
import iam.fitflex.dto.PageSliceDto;
import iam.fitflex.service.MuscleGroupService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/muscle-groups")
@Slf4j
public record MuscleGroupController(
        MuscleGroupService muscleGroupService
) {

    @GetMapping
    public PageSliceDto getAll(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        log.info("Get endpoint for ALL muscle groups invoked");
        String defaultSort = "id";
        return muscleGroupService.getAllMuscleGroupsByPaginationAndSorting(page, size, defaultSort);
    }

    @GetMapping("/{name}")
    public MuscleGroupResponseDto getOne(@PathVariable("name") String name) {
        log.info("Get endpoint for SINGLE muscle group with name {} invoked", name);
        return muscleGroupService.getMuscleGroupByName(name);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MuscleGroupResponseDto post(@RequestBody @Valid MuscleGroupDto requestDto) {
        log.info("Post endpoint for MuscleGroup() invoked using {}", requestDto);
        return muscleGroupService.createMuscleGroup(requestDto);
    }

    @PutMapping("/{name}")
    public MuscleGroupResponseDto update(@PathVariable("name") String name, @RequestBody @Valid MuscleGroupDto requestDto) {
        log.info("Put endpoint for MuscleGroup() invoked using {}", requestDto);
        return muscleGroupService.updateMuscleGroup(name, requestDto);
    }

    @DeleteMapping("/{name}")
    public void delete(@PathVariable("name") String name) {
        log.info("Delete endpoint for muscle group with name {} invoked", name);
        muscleGroupService.deleteMuscleGroup(name);
    }
}
