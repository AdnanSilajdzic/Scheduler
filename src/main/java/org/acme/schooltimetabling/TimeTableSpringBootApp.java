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
            String days[] = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};

            for (int i = 0; i<7; i++){
                for(int j = 9; j<=17; j++){
                    timeslotRepository.save(new Timeslot(DayOfWeek.valueOf(days[i]), LocalTime.of(j, 0), LocalTime.of(j+1,0)));
                    timeslotRepository.save(new Timeslot(DayOfWeek.valueOf(days[i]), LocalTime.of(j, 0), LocalTime.of(j+2,0)));
                    timeslotRepository.save(new Timeslot(DayOfWeek.valueOf(days[i]), LocalTime.of(j, 0), LocalTime.of(j+3,0)));
                }
            }

            roomRepository.save(new Room("Room A"));
            roomRepository.save(new Room("Room B"));
            roomRepository.save(new Room("Room C"));
            if (demoData == DemoData.LARGE) {
                roomRepository.save(new Room("Room D"));
                roomRepository.save(new Room("Room E"));
                roomRepository.save(new Room("Room F"));
            }
            lessonRepository.save(new Lesson("Math", "A. Turing", "9th grade",Duration.ofHours(1)));
            lessonRepository.save(new Lesson("Math", "A. Turing", "9th grade",Duration.ofHours(1)));
            lessonRepository.save(new Lesson("Physics", "M. Curie", "9th grade",Duration.ofHours(3)));
            lessonRepository.save(new Lesson("Chemistry", "M. Curie", "9th grade",Duration.ofHours(3)));
            lessonRepository.save(new Lesson("Biology", "C. Darwin", "9th grade",Duration.ofHours(3)));
            lessonRepository.save(new Lesson("History", "I. Jones", "9th grade",Duration.ofHours(3)));
            lessonRepository.save(new Lesson("English", "I. Jones", "9th grade",Duration.ofHours(3)));
            lessonRepository.save(new Lesson("English", "I. Jones", "9th grade",Duration.ofHours(3)));
            lessonRepository.save(new Lesson("Spanish", "P. Cruz", "9th grade",Duration.ofHours(3)));
            lessonRepository.save(new Lesson("Spanish", "P. Cruz", "9th grade",Duration.ofHours(3)));

            lessonRepository.save(new Lesson("Math", "A. Turing", "10th grade",Duration.ofHours(3)));
            lessonRepository.save(new Lesson("Math", "A. Turing", "10th grade",Duration.ofHours(3)));
            lessonRepository.save(new Lesson("Math", "A. Turing", "10th grade",Duration.ofHours(3)));
            lessonRepository.save(new Lesson("Physics", "M. Curie", "10th grade",Duration.ofHours(3)));
            lessonRepository.save(new Lesson("Chemistry", "M. Curie", "10th grade",Duration.ofHours(3)));
            lessonRepository.save(new Lesson("French", "M. Curie", "10th grade",Duration.ofHours(3)));
            lessonRepository.save(new Lesson("Geography", "C. Darwin", "10th grade",Duration.ofHours(3)));
            lessonRepository.save(new Lesson("History", "I. Jones", "10th grade",Duration.ofHours(3)));
            lessonRepository.save(new Lesson("English", "P. Cruz", "10th grade",Duration.ofHours(3)));
            lessonRepository.save(new Lesson("Spanish", "P. Cruz", "10th grade",Duration.ofHours(3)));

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
