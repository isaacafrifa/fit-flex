package iam.fitflex.controller;

import iam.fitflex.dto.ExerciseDto;
import iam.fitflex.dto.ExerciseResponseDto;
import iam.fitflex.dto.PageSliceDto;
import iam.fitflex.service.ExerciseService;
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
@RequestMapping("api/v1/exercises")
@Slf4j
public record ExerciseController(
        ExerciseService exerciseService
) {

    @GetMapping
    public PageSliceDto getAll(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        log.info("Get endpoint for ALL exercises invoked");
        String defaultSort = "id";
        return exerciseService.getAllExercises(page, size, defaultSort);
    }

    @GetMapping("/{name}")
    public ExerciseResponseDto getOne(@PathVariable("name") String name) {
        log.info("Get endpoint for SINGLE exercise with name [{}] invoked", name);
        return exerciseService.getExerciseByName(name);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExerciseResponseDto post(@RequestBody @Valid ExerciseDto requestDto) {
        log.info("Post endpoint for exercise() invoked using {}", requestDto);
        return exerciseService.createExercise(requestDto);
    }

    @PutMapping("/{name}")
    public ExerciseResponseDto update(@PathVariable("name") String name, @RequestBody @Valid ExerciseDto requestDto) {
        log.info("Put endpoint for exercise() invoked using {}", requestDto);
        return exerciseService.updateExercise(name, requestDto);
    }

    @DeleteMapping("/{name}")
    public void delete(@PathVariable("name") String name) {
        log.info("Delete endpoint for exercise with name [{}] invoked", name);
        exerciseService.deleteExercise(name);
    }
}
