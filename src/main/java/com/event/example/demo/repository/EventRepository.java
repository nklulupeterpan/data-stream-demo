package com.event.example.demo.repository;


import com.event.example.demo.model.Event;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;

@Repository
public interface EventRepository extends CrudRepository<Event, Long> {
}
