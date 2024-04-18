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


    @Autowired
    ConstraintVerifier<TimeTableConstraintProvider, TimeTable> constraintVerifier;

    @Test
    void lateLectures() {
        Lesson firstLesson = new Lesson(1, "Subject1", "Teacher1", "Group1",Duration.ofHours(3), "any", LATE, ROOM1);
        constraintVerifier.verifyThat(TimeTableConstraintProvider::lateClassess)
                .given(firstLesson)
                .penalizesBy(1);
    }

    

}
