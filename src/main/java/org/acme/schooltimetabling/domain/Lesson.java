package org.acme.schooltimetabling.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;

@PlanningEntity
@Entity
public class Lesson {

    @PlanningId
    @Id @GeneratedValue
    private Long id;

    private String subject;
    private String teacher;
    private String studentGroup;
    private Duration duration; // Added field for lesson duration
    private String classroomType;
    //array of student groups that can optionally attend the lesson
    private String[] optionalStudentGroups;
    private String[] mandatoryStudentGroups;

    @PlanningVariable
    @ManyToOne
    private Timeslot timeslot;

    @PlanningVariable
    @ManyToOne
    private Room room;

    // No-arg constructor required for Hibernate and OptaPlanner
    public Lesson() {
    }

    public Lesson(String subject, String teacher, String studentGroup, Duration duration, String classroomType, String[] optionalStudentGroups, String[] mandatoryStudentGroups) {
        this.subject = subject;
        this.teacher = teacher;
        this.studentGroup = studentGroup;
        this.duration = duration;
        this.classroomType = classroomType;
        this.optionalStudentGroups = optionalStudentGroups;
        this.mandatoryStudentGroups = mandatoryStudentGroups;
    }

    public Lesson(long id, String subject, String teacher, String studentGroup, Duration duration, String[] optionalStudentGroups, String[] mandatoryStudentGroups ,String classroomType, Timeslot timeslot, Room room) {
        this(subject, teacher, studentGroup, duration, classroomType, optionalStudentGroups, mandatoryStudentGroups);
        this.id = id;
        this.timeslot = timeslot;
        this.room = room;
    }

    @Override
    public String toString() {
        return subject + "(" + id + ")";
    }

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    public Long getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getStudentGroup() {
        return studentGroup;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Timeslot getTimeslot() {
        return timeslot;
    }

    public void setTimeslot(Timeslot timeslot) {
        this.timeslot = timeslot;
    }

    public Room getRoom() {
        return room;
    }

    public String[] getOptionalStudentGroups() {
        return optionalStudentGroups;
    }

    public void setOptionalStudentGroups(String[] optionalStudentGroups) {
        this.optionalStudentGroups = optionalStudentGroups;
    }

    public String[] getMandatoryStudentGroups() {
        return mandatoryStudentGroups;
    }

    public void setMandatoryStudentGroups(String[] mandatoryStudentGroups) {
        this.mandatoryStudentGroups = mandatoryStudentGroups;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

     public LocalTime getStartTime() {
        if (this.timeslot != null) {
            return this.timeslot.getStartTime();
        }
        else return LocalTime.of(0, 0);
    }

    public LocalTime getEndTime() {
        if (this.timeslot != null) {
            return this.timeslot.getEndTime();
        }
        else return LocalTime.of(0, 0);
    }

    public DayOfWeek getDayOfWeek() {
        if (this.timeslot != null) {
            return this.timeslot.getDayOfWeek();
        }
        else return DayOfWeek.MONDAY;
    }

    public String getClassroomType() {
        return classroomType;
    }


}
