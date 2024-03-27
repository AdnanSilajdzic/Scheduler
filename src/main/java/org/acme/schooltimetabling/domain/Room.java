package org.acme.schooltimetabling.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.optaplanner.core.api.domain.lookup.PlanningId;

@Entity
public class Room {

    @PlanningId
    @Id @GeneratedValue
    private Long id;

    private String name;
    private String type;

    // No-arg constructor required for Hibernate
    public Room() {
    }

    public Room(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public Room(long id, String name, String type) {
        this(name, type);
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

}
