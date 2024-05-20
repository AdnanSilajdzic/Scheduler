package org.acme.schooltimetabling;

import java.time.DayOfWeek;
import java.time.LocalTime;

import org.acme.schooltimetabling.domain.Lesson;
import org.acme.schooltimetabling.domain.Room;
import org.acme.schooltimetabling.domain.Timeslot;
import org.acme.schooltimetabling.persistence.LessonRepository;
import org.acme.schooltimetabling.persistence.RoomRepository;
import org.acme.schooltimetabling.persistence.TimeslotRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;

import java.time.Duration;

@SpringBootApplication
public class TimeTableSpringBootApp {

    public static void main(String[] args) {
        SpringApplication.run(TimeTableSpringBootApp.class, args);
    }

    @Value("${timeTable.demoData:SMALL}")
    private DemoData demoData;

    @Bean
    public CommandLineRunner demoData(
            TimeslotRepository timeslotRepository,
            RoomRepository roomRepository,
            LessonRepository lessonRepository) {
        return (args) -> {
            if (demoData == DemoData.NONE) {
                return;
            }
            String days[] = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};

            for (int i = 0; i<5; i++){
                for(int j = 9; j<=17; j++){
                    timeslotRepository.save(new Timeslot(DayOfWeek.valueOf(days[i]), LocalTime.of(j, 0), LocalTime.of(j+1,0)));
                    timeslotRepository.save(new Timeslot(DayOfWeek.valueOf(days[i]), LocalTime.of(j, 0), LocalTime.of(j+2,0)));
                    timeslotRepository.save(new Timeslot(DayOfWeek.valueOf(days[i]), LocalTime.of(j, 0), LocalTime.of(j+3,0)));
                }
            }

            
            roomRepository.save(new Room("Computer Lab 1", "computer lab"));
            roomRepository.save(new Room("Amphitheatre", "amphitheater"));
            roomRepository.save(new Room("Amphitheatre 2", "amphitheater"));
            roomRepository.save(new Room("Classroom 1", "regular"));
            roomRepository.save(new Room("Classroom 2", "regular"));
            roomRepository.save(new Room("Classroom 3", "regular"));

            lessonRepository.save(new Lesson("Computer Science", "A. Turing", "9th grade",Duration.ofHours(1), "computer lab", new String[]{"10th grade"}, new String[]{"9th grade"}));
            lessonRepository.save(new Lesson("Computer Science", "A. Turing", "9th grade",Duration.ofHours(1), "computer lab", new String[]{"10th grade"}, new String[]{"9th grade"}));
            lessonRepository.save(new Lesson("Physics", "M. Curie", "9th grade",Duration.ofHours(3), "any", new String[]{"10th grade"}, new String[]{"9th grade"}));
            lessonRepository.save(new Lesson("Chemistry", "M. Curie", "9th grade",Duration.ofHours(3), "any", new String[]{"10th grade"}, new String[]{"9th grade"}));
            lessonRepository.save(new Lesson("Biology", "C. Darwin", "9th grade",Duration.ofHours(3), "any", new String[]{"10th grade"}, new String[]{"9th grade"}));
            lessonRepository.save(new Lesson("History", "I. Jones", "9th grade",Duration.ofHours(3), "any", new String[]{"10th grade"}, new String[]{"9th grade"}));
            lessonRepository.save(new Lesson("English", "I. Jones", "9th grade",Duration.ofHours(3), "amphitheater", new String[]{}, new String[]{"9th grade"}));
            lessonRepository.save(new Lesson("English", "I. Jones", "9th grade",Duration.ofHours(3), "amphitheater", new String[]{}, new String[]{"9th grade"}));
            lessonRepository.save(new Lesson("Spanish", "P. Cruz", "9th grade",Duration.ofHours(3), "amphitheater", new String[]{}, new String[]{"9th grade"}));
            lessonRepository.save(new Lesson("Spanish", "P. Cruz", "9th grade",Duration.ofHours(3), "amphitheater", new String[]{}, new String[]{"9th grade"}));

            lessonRepository.save(new Lesson("Computer Science", "A. Turing", "10th grade",Duration.ofHours(2), "computer lab", new String[]{}, new String[]{"10th grade"}));
            lessonRepository.save(new Lesson("Computer Science", "A. Turing", "10th grade",Duration.ofHours(1), "computer lab", new String[]{}, new String[]{"10th grade"}));
            lessonRepository.save(new Lesson("Physics", "M. Curie", "10th grade",Duration.ofHours(3), "any", new String[]{}, new String[]{"10th grade"}));
            lessonRepository.save(new Lesson("Chemistry", "M. Curie", "10th grade",Duration.ofHours(3), "any", new String[]{}, new String[]{"10th grade"}));
            lessonRepository.save(new Lesson("French", "M. Curie", "10th grade",Duration.ofHours(3), "amphitheater", new String[]{}, new String[]{"10th grade"}));
            lessonRepository.save(new Lesson("Geography", "C. Darwin", "10th grade",Duration.ofHours(3), "any", new String[]{}, new String[]{"10th grade"}));
            lessonRepository.save(new Lesson("History", "I. Jones", "10th grade",Duration.ofHours(3), "any", new String[]{}, new String[]{"10th grade"}));
            lessonRepository.save(new Lesson("English", "P. Cruz", "10th grade",Duration.ofHours(3), "amphitheater", new String[]{}, new String[]{"10th grade"}));
            lessonRepository.save(new Lesson("Spanish", "P. Cruz", "10th grade",Duration.ofHours(3), "amphitheater", new String[]{}, new String[]{"10th grade"}));

            Lesson lesson = lessonRepository.findAll(Sort.by("id")).iterator().next();
            lesson.setTimeslot(timeslotRepository.findAll(Sort.by("id")).iterator().next());
            lesson.setRoom(roomRepository.findAll(Sort.by("id")).iterator().next());

            lessonRepository.save(lesson);
        };
    }

    public enum DemoData {
        NONE,
        SMALL,
        LARGE
    }

}
