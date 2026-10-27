package com.soda2026.journi.activity;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityService {
    private final ActivityRepository repository;
    private final ActivityMapper mapper;

    public ActivityService(ActivityRepository repository, ActivityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<ActivityResponseDTO> findAll() {
        return mapper.toDtos(repository.findAll());
    }

    public ActivityResponseDTO findById(Long id) {
        return mapper.toDto(repository.findById(id).orElseThrow());
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }


}
