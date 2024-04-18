package org.acme.schooltimetabling.solver;

import java.time.Duration;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

import org.acme.schooltimetabling.domain.Lesson;

public class TimeTableConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                // Hard constraints
                roomConflict(constraintFactory),
                teacherConflict(constraintFactory),
                studentGroupConflict(constraintFactory),
                lessonDuration(constraintFactory),
                classroomTypeConflict(constraintFactory),
                //lectures of the same subject to the same student group should be at least 2 days apart
                seperatedLectures(constraintFactory),
                consecutiveProfessorLectures(constraintFactory),
                // Soft constraints
                teacherRoomStability(constraintFactory),
                teacherTimeEfficiency(constraintFactory),
                studentGroupSubjectVariety(constraintFactory),
                lateClassess(constraintFactory)
        };
    }

    Constraint roomConflict(ConstraintFactory constraintFactory) {
        // A room can accommodate at most one lesson at the same time.
        return constraintFactory
                // Select each pair of 2 different lessons ...
                .forEachUniquePair(Lesson.class,
                        // ... in the same timeslot ...
                        Joiners.overlapping(Lesson::getStartTime, Lesson::getEndTime),
                        // ... in the same room ...
                        Joiners.equal(Lesson::getRoom),
                        Joiners.equal(Lesson::getDayOfWeek))
                // ... and penalize each pair with a hard weight.
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Room conflict");
    }

    Constraint teacherConflict(ConstraintFactory constraintFactory) {
        // A teacher can teach at most one lesson at the same time.
        return constraintFactory
                .forEachUniquePair(Lesson.class,
                        Joiners.overlapping(Lesson::getStartTime, Lesson::getEndTime),
                        Joiners.equal(Lesson::getDayOfWeek),
                        Joiners.equal(Lesson::getTeacher))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Teacher conflict");
    }

    Constraint studentGroupConflict(ConstraintFactory constraintFactory) {
        // A student can attend at most one lesson at the same time.
        return constraintFactory
                .forEachUniquePair(Lesson.class,
                        Joiners.overlapping(Lesson::getStartTime, Lesson::getEndTime),
                        Joiners.equal(Lesson::getDayOfWeek),
                        Joiners.equal(Lesson::getStudentGroup))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Student group conflict");
    }

        Constraint lessonDuration(ConstraintFactory constraintFactory) {
                // A lesson must be scheduled in a single timeslot.
                return constraintFactory
                        .forEach(Lesson.class)
                        .filter(lesson -> {
                        Duration lessonDuration = lesson.getDuration();
                        Duration timeslotDuration = Duration.between(lesson.getTimeslot().getStartTime(),
                                lesson.getTimeslot().getEndTime());
                        return !lessonDuration.equals(timeslotDuration);
                        })
                        .penalize(HardSoftScore.ONE_HARD)
                        .asConstraint("Lesson duration");
        }

        Constraint classroomTypeConflict(ConstraintFactory constraintFactory) {
                // A lesson must be scheduled in a room of the correct type.
                return constraintFactory
                        .forEach(Lesson.class)
                        .filter(lesson -> !lesson.getClassroomType().equals("any") &&
                                !lesson.getClassroomType().equals(lesson.getRoom().getType()))
                        .penalize(HardSoftScore.ONE_HARD)
                        .asConstraint("Classroom type conflict");
        }

        Constraint seperatedLectures(ConstraintFactory constraintFactory) {
                // a lesson in the same subject to the same student group should be at least 2 days apart
                return constraintFactory
                        .forEachUniquePair(Lesson.class,
                                Joiners.equal(Lesson::getSubject),
                                Joiners.equal(Lesson::getStudentGroup),
                                Joiners.equal(Lesson::getTeacher))
                        .filter((lesson1, lesson2) -> {
                                String days[] = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};
                                int between = Math.abs(java.util.Arrays.asList(days).indexOf(lesson1.getDayOfWeek().toString()) - java.util.Arrays.asList(days).indexOf(lesson2.getDayOfWeek().toString()));
                                return between < 2;
                                })
                        .penalize(HardSoftScore.ONE_HARD)
                        .asConstraint("Seperated lectures");
                        }

        Constraint consecutiveProfessorLectures(ConstraintFactory constraintFactory) {
                // a professor should not have more than 3 consecutive hours of lectures
                return constraintFactory
                        .forEachUniquePair(Lesson.class,
                                Joiners.equal(Lesson::getTeacher),
                                Joiners.equal(Lesson::getDayOfWeek))
                        .filter((lesson1, lesson2) -> {
                                        if (lesson1.getTimeslot().getEndTime() == lesson2.getTimeslot().getStartTime()){

                                        Duration between = Duration.between(lesson1.getTimeslot().getStartTime(), 
                                        lesson2.getTimeslot().getEndTime());
                                        
                                        return between.compareTo(Duration.ofMinutes(180)) >= 0;
                                        }
                                        else if (lesson1.getTimeslot().getStartTime() == lesson2.getTimeslot().getEndTime()){
                                        Duration between = Duration.between(lesson2.getTimeslot().getStartTime(),
                                        lesson1.getTimeslot().getEndTime());

                                        return between.compareTo(Duration.ofMinutes(180)) >= 0;
                                        }
                                        return false;
                        }
                                )
                        .penalize(HardSoftScore.ONE_HARD)
                        .asConstraint("Consecutive professor lectures");
                        }

    Constraint teacherRoomStability(ConstraintFactory constraintFactory) {
        // A teacher prefers to teach in a single room.
        return constraintFactory
                .forEachUniquePair(Lesson.class,
                        Joiners.equal(Lesson::getTeacher))
                .filter((lesson1, lesson2) -> lesson1.getRoom() != lesson2.getRoom())
                .penalize(HardSoftScore.ONE_SOFT)
                .asConstraint("Teacher room stability");
    }

    Constraint teacherTimeEfficiency(ConstraintFactory constraintFactory) {
        // A teacher prefers to teach sequential lessons and dislikes gaps between lessons.
        return constraintFactory
                .forEach(Lesson.class)
                .join(Lesson.class, Joiners.equal(Lesson::getTeacher),
                        Joiners.equal((lesson) -> lesson.getTimeslot().getDayOfWeek()))
                .filter((lesson1, lesson2) -> {
                    Duration between = Duration.between(lesson1.getTimeslot().getEndTime(),
                            lesson2.getTimeslot().getStartTime());
                    return !between.isNegative() && between.compareTo(Duration.ofMinutes(30)) <= 0;
                })
                .reward(HardSoftScore.ONE_SOFT)
                .asConstraint("Teacher time efficiency");
    }

    Constraint studentGroupSubjectVariety(ConstraintFactory constraintFactory) {
        // A student group dislikes sequential lessons on the same subject.
        return constraintFactory
                .forEach(Lesson.class)
                .join(Lesson.class,
                        Joiners.equal(Lesson::getSubject),
                        Joiners.equal(Lesson::getStudentGroup),
                        Joiners.equal((lesson) -> lesson.getTimeslot().getDayOfWeek()))
                .filter((lesson1, lesson2) -> {
                    Duration between = Duration.between(lesson1.getTimeslot().getEndTime(),
                            lesson2.getTimeslot().getStartTime());
                    return !between.isNegative() && between.compareTo(Duration.ofMinutes(30)) <= 0;
                })
                .penalize(HardSoftScore.ONE_SOFT)
                .asConstraint("Student group subject variety");
    }

        Constraint lateClassess(ConstraintFactory constraintFactory) {
                // A student group dislikes late lessons.
                return constraintFactory
                        .forEach(Lesson.class)
                        .filter(lesson -> lesson.getTimeslot().getEndTime().getHour() > 18)
                        .penalize(HardSoftScore.ONE_SOFT)
                        .asConstraint("Late classes");
        }

}
