package org.acme.schooltimetabling.solver;

import java.time.DayOfWeek;
import java.time.LocalTime;

import org.acme.schooltimetabling.domain.Lesson;
import org.acme.schooltimetabling.domain.Room;
import org.acme.schooltimetabling.domain.TimeTable;
import org.acme.schooltimetabling.domain.Timeslot;
import org.junit.jupiter.api.Test;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

@SpringBootTest
class TimeTableConstraintProviderTest {

    private static final Room ROOM1 = new Room(1, "Room1", "computer lab");
    private static final Room ROOM2 = new Room(2, "Room2", "regular");
    private static final Timeslot TIMESLOT1 = new Timeslot(1, DayOfWeek.MONDAY, LocalTime.NOON);
    private static final Timeslot TIMESLOT2 = new Timeslot(2, DayOfWeek.TUESDAY, LocalTime.NOON);
    private static final Timeslot TIMESLOT3 = new Timeslot(3, DayOfWeek.TUESDAY, LocalTime.NOON.plusHours(1));
    private static final Timeslot TIMESLOT4 = new Timeslot(4, DayOfWeek.TUESDAY, LocalTime.NOON.plusHours(3));
    private static final Timeslot LATE = new Timeslot(DayOfWeek.TUESDAY,LocalTime.of(16, 0), LocalTime.of(19,0));
    private static final Timeslot OVERLAP1 = new Timeslot(DayOfWeek.TUESDAY,LocalTime.of(13, 0), LocalTime.of(15,0));
    private static final Timeslot OVERLAP2 = new Timeslot(DayOfWeek.TUESDAY,LocalTime.of(14, 0), LocalTime.of(16,0));
    private static final Timeslot SEQUENTIAL = new Timeslot(DayOfWeek.TUESDAY,LocalTime.of(9, 0), LocalTime.of(11,0));
    private static final Timeslot SEQUENTIAL2 = new Timeslot(DayOfWeek.TUESDAY,LocalTime.of(11, 0), LocalTime.of(14,0));
    private static final Timeslot SEQUENTIAL3 = new Timeslot(DayOfWeek.TUESDAY,LocalTime.of(14, 0), LocalTime.of(17,0));




    @Autowired
    ConstraintVerifier<TimeTableConstraintProvider, TimeTable> constraintVerifier;

    @Test
    void lateLectures() {
        Lesson firstLesson = new Lesson(1, "Subject1", "Teacher1", "Group1",Duration.ofHours(3),new String[]{},new String[]{}, "any",  LATE, ROOM1);
        constraintVerifier.verifyThat(TimeTableConstraintProvider::lateClassess)
                .given(firstLesson)
                .penalizesBy(1);
    }

    @Test
    void ElectiveLectures(){
        Lesson firstLesson = new Lesson(1, "Subject1", "Teacher1", "Group1",Duration.ofHours(3),new String[]{"Group2"},new String[]{"Group1"}, "any",  OVERLAP1, ROOM1);
        Lesson secondLesson = new Lesson(2, "Subject2", "Teacher2", "Group2",Duration.ofHours(3),new String[]{"Group1"},new String[]{"Group2"}, "any",  OVERLAP2, ROOM2);
        constraintVerifier.verifyThat(TimeTableConstraintProvider::electiveLessons)
                .given(firstLesson, secondLesson)
                .penalizesBy(1);
    }

    @Test
    void MandatoryStudentGroup(){
        Lesson firstLesson = new Lesson(1, "Subject1", "Teacher1", "Group1",Duration.ofHours(3),new String[]{},new String[]{"Group1", "Group2"}, "any",  OVERLAP1, ROOM1);
        Lesson secondLesson = new Lesson(2, "Subject2", "Teacher2", "Group2",Duration.ofHours(3),new String[]{"Group1"},new String[]{"Group2"}, "any",  OVERLAP2, ROOM2);
        constraintVerifier.verifyThat(TimeTableConstraintProvider::mandatoryStudentGroupConflict)
                .given(firstLesson, secondLesson)
                .penalizesBy(1);
    }

    @Test
    void maxTeachingHoursPerDay(){
        Lesson firstLesson = new Lesson(1, "Subject1", "Teacher1", "Group1",Duration.ofHours(3),new String[]{},new String[]{"Group1", "Group2"}, "any",  SEQUENTIAL, ROOM1);
        Lesson secondLesson = new Lesson(2, "Subject2", "Teacher1", "Group2",Duration.ofHours(3),new String[]{"Group1"},new String[]{"Group2"}, "any",  SEQUENTIAL2, ROOM2);
        Lesson thirdLesson = new Lesson(3, "Subject3", "Teacher1", "Group1",Duration.ofHours(3),new String[]{},new String[]{"Group1", "Group2"}, "any",  SEQUENTIAL3, ROOM1);   
        constraintVerifier.verifyThat(TimeTableConstraintProvider::maxTeachingHoursPerDay)
                .given(firstLesson, secondLesson, thirdLesson)
                .penalizesBy(1);     
    }

    @Test
    void maxStudentHoursPerDay(){
        Lesson firstLesson = new Lesson(1, "Subject1", "Teacher1", "Group1",Duration.ofHours(3),new String[]{},new String[]{"Group1", "Group2"}, "any",  SEQUENTIAL, ROOM1);
        Lesson secondLesson = new Lesson(2, "Subject2", "Teacher1", "Group1",Duration.ofHours(3),new String[]{"Group1"},new String[]{"Group2"}, "any",  SEQUENTIAL2, ROOM2);
        Lesson thirdLesson = new Lesson(3, "Subject3", "Teacher1", "Group1",Duration.ofHours(3),new String[]{},new String[]{"Group1", "Group2"}, "any",  SEQUENTIAL3, ROOM1);   
        constraintVerifier.verifyThat(TimeTableConstraintProvider::maxStudentHoursPerDay)
                .given(firstLesson, secondLesson, thirdLesson)
                .penalizesBy(1);     
    }

    @Test
    void minimizeGapsBetweenLectures(){
        Lesson firstLesson = new Lesson(1, "Subject1", "Teacher1", "Group1",Duration.ofHours(3),new String[]{},new String[]{"Group1", "Group2"}, "any",  SEQUENTIAL, ROOM1);
        Lesson thirdLesson = new Lesson(3, "Subject3", "Teacher1", "Group1",Duration.ofHours(3),new String[]{},new String[]{"Group1", "Group2"}, "any",  SEQUENTIAL3, ROOM1);   
        constraintVerifier.verifyThat(TimeTableConstraintProvider::minimizeGapsBetweenLectures)
                .given(firstLesson, thirdLesson)
                .penalizesBy(1);     
    }
    

}
