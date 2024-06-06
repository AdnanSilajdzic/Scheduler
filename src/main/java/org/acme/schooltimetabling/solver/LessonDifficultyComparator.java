package org.acme.schooltimetabling.solver;

import java.util.Comparator;
import org.acme.schooltimetabling.domain.Lesson;

public class LessonDifficultyComparator implements Comparator<Lesson> {

    @Override
    public int compare(Lesson lesson1, Lesson lesson2) {
        return lesson1.getDuration().compareTo(lesson2.getDuration());
    }
}
