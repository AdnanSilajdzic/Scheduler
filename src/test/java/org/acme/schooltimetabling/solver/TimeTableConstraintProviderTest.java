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

    private static final Room ROOM1 = new Room(1, "Room1");
    private static final Room ROOM2 = new Room(2, "Room2");
    private static final Timeslot TIMESLOT1 = new Timeslot(1, DayOfWeek.MONDAY, LocalTime.NOON);
    private static final Timeslot TIMESLOT2 = new Timeslot(2, DayOfWeek.TUESDAY, LocalTime.NOON);
    private static final Timeslot TIMESLOT3 = new Timeslot(3, DayOfWeek.TUESDAY, LocalTime.NOON.plusHours(1));
    private static final Timeslot TIMESLOT4 = new Timeslot(4, DayOfWeek.TUESDAY, LocalTime.NOON.plusHours(3));

    @Autowired
    ConstraintVerifier<TimeTableConstraintProvider, TimeTable> constraintVerifier;

    @Test
    void roomConflict() {
        Lesson firstLesson = new Lesson(1, "Subject1", "Teacher1", "Group1",Duration.ofHours(3), TIMESLOT1, ROOM1);
        Lesson conflictingLesson = new Lesson(2, "Subject2", "Teacher2", "Group2",Duration.ofHours(3), TIMESLOT1, ROOM1);
        Lesson nonConflictingLesson = new Lesson(3, "Subject3", "Teacher3", "Group3",Duration.ofHours(3), TIMESLOT2, ROOM1);
        constraintVerifier.verifyThat(TimeTableConstraintProvider::roomConflict)
                .given(firstLesson, conflictingLesson, nonConflictingLesson)
                .penalizesBy(1);
    }

    @Test
    void teacherConflict() {
        String conflictingTeacher = "Teacher1";
        Lesson firstLesson = new Lesson(1, "Subject1", conflictingTeacher, "Group1",Duration.ofHours(3), TIMESLOT1, ROOM1);
        Lesson conflictingLesson = new Lesson(2, "Subject2", conflictingTeacher, "Group2",Duration.ofHours(3), TIMESLOT1, ROOM2);
        Lesson nonConflictingLesson = new Lesson(3, "Subject3", "Teacher2", "Group3",Duration.ofHours(3), TIMESLOT2, ROOM1);
        constraintVerifier.verifyThat(TimeTableConstraintProvider::teacherConflict)
                .given(firstLesson, conflictingLesson, nonConflictingLesson)
                .penalizesBy(1);
    }

    @Test
    void studentGroupConflict() {
        String conflictingGroup = "Group1";
        Lesson firstLesson = new Lesson(1, "Subject1", "Teacher1", conflictingGroup,Duration.ofHours(3), TIMESLOT1, ROOM1);
        Lesson conflictingLesson = new Lesson(2, "Subject2", "Teacher2", conflictingGroup,Duration.ofHours(3), TIMESLOT1, ROOM2);
        Lesson nonConflictingLesson = new Lesson(3, "Subject3", "Teacher3", "Group3",Duration.ofHours(3), TIMESLOT2, ROOM1);
        constraintVerifier.verifyThat(TimeTableConstraintProvider::studentGroupConflict)
                .given(firstLesson, conflictingLesson, nonConflictingLesson)
                .penalizesBy(1);
    }

    @Test
    void teacherRoomStability() {
        String teacher = "Teacher1";
        Lesson lessonInFirstRoom = new Lesson(1, "Subject1", teacher, "Group1",Duration.ofHours(3), TIMESLOT1, ROOM1);
        Lesson lessonInSameRoom = new Lesson(2, "Subject2", teacher, "Group2",Duration.ofHours(3), TIMESLOT1, ROOM1);
        Lesson lessonInDifferentRoom = new Lesson(3, "Subject3", teacher, "Group3",Duration.ofHours(3), TIMESLOT1, ROOM2);
        constraintVerifier.verifyThat(TimeTableConstraintProvider::teacherRoomStability)
                .given(lessonInFirstRoom, lessonInDifferentRoom, lessonInSameRoom)
                .penalizesBy(2);
    }

    @Test
    void teacherTimeEfficiency() {
        String teacher = "Teacher1";
        Lesson singleLessonOnMonday = new Lesson(1, "Subject1", teacher, "Group1",Duration.ofHours(3), TIMESLOT1, ROOM1);
        Lesson firstTuesdayLesson = new Lesson(2, "Subject2", teacher, "Group2",Duration.ofHours(3), TIMESLOT2, ROOM1);
        Lesson secondTuesdayLesson = new Lesson(3, "Subject3", teacher, "Group3",Duration.ofHours(3), TIMESLOT3, ROOM1);
        Lesson thirdTuesdayLessonWithGap = new Lesson(4, "Subject4", teacher, "Group4",Duration.ofHours(3), TIMESLOT4, ROOM1);
        constraintVerifier.verifyThat(TimeTableConstraintProvider::teacherTimeEfficiency)
                .given(singleLessonOnMonday, firstTuesdayLesson, secondTuesdayLesson, thirdTuesdayLessonWithGap)
                .rewardsWith(1); // Second tuesday lesson immediately follows the first.
    }

    @Test
    void studentGroupSubjectVariety() {
        String studentGroup = "Group1";
        String repeatedSubject = "Subject1";
        Lesson mondayLesson = new Lesson(1, repeatedSubject, "Teacher1", studentGroup,Duration.ofHours(3), TIMESLOT1, ROOM1);
        Lesson firstTuesdayLesson = new Lesson(2, repeatedSubject, "Teacher2", studentGroup,Duration.ofHours(3), TIMESLOT2, ROOM1);
        Lesson secondTuesdayLesson = new Lesson(3, repeatedSubject, "Teacher3", studentGroup,Duration.ofHours(3), TIMESLOT3, ROOM1);
        Lesson thirdTuesdayLessonWithDifferentSubject = new Lesson(4, "Subject2", "Teacher4", studentGroup,Duration.ofHours(3), TIMESLOT4, ROOM1);
        Lesson lessonInAnotherGroup = new Lesson(5, repeatedSubject, "Teacher5", "Group2",Duration.ofHours(3), TIMESLOT1, ROOM1);
        constraintVerifier.verifyThat(TimeTableConstraintProvider::studentGroupSubjectVariety)
                .given(mondayLesson, firstTuesdayLesson, secondTuesdayLesson, thirdTuesdayLessonWithDifferentSubject,
                        lessonInAnotherGroup)
                .penalizesBy(1); // Second tuesday lesson immediately follows the first.
    }

}
