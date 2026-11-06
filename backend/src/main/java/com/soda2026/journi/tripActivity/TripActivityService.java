 package com.soda2026.journi.activity;

 import com.soda2026.journi.tripActivity.TripActivityMapper;
 import com.soda2026.journi.tripActivity.TripActivityRepository;
 import com.soda2026.journi.tripActivity.dto.TripActivityResponseDTO;
 import org.springframework.stereotype.Service;

 import java.util.List;

 @Service
 public class TripActivityService {
     private final TripActivityRepository repository;
     private final TripActivityMapper mapper;

     public TripActivityService(TripActivityRepository repository, TripActivityMapper mapper) {
         this.repository = repository;
         this.mapper = mapper;
     }


     public List<TripActivityResponseDTO> findAll() {
         return mapper.toDtos(repository.findAll());
     }

     public TripActivityResponseDTO findById(Long id) {
         return mapper.toDto(repository.findById(id).orElseThrow());
     }

     public void delete(Long id) {
         repository.deleteById(id);
     }


 }
