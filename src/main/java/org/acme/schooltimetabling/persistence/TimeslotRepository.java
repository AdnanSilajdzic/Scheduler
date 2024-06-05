package org.acme.schooltimetabling.persistence;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import org.acme.schooltimetabling.domain.Timeslot;

public interface TimeslotRepository extends PagingAndSortingRepository<Timeslot, Long> {

    @SuppressWarnings("null")
    @Override
    List<Timeslot> findAll();

}
