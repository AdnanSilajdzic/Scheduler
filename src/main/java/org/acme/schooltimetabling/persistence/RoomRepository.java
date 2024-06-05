package org.acme.schooltimetabling.persistence;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import org.acme.schooltimetabling.domain.Room;

public interface RoomRepository extends PagingAndSortingRepository<Room, Long> {

    @SuppressWarnings("null")
    @Override
    List<Room> findAll();

}
