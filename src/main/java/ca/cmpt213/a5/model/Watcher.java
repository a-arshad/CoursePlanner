package ca.cmpt213.a5.model;

import ca.cmpt213.a5.model.planner.Course;
import ca.cmpt213.a5.model.planner.Section;
import ca.cmpt213.a5.model.planner.SectionComponent;

import java.util.List;

/**
 * Interface class that defines a Watcher
 * Uses the observer pattern to update changes in registered course
 *
 * @author Ali Arshad
 */
public interface Watcher {
    long getId();

    Object getDepartment();

    Object getCourse();

    List<String> getEvents();

    void stateChanged(Course updateCourse, Section section, SectionComponent sectionComponent);
}
