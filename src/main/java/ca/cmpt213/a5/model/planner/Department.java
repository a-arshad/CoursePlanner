package ca.cmpt213.a5.model.planner;

import ca.cmpt213.a5.exceptions.IdDoesNotExistException;
import ca.cmpt213.a5.model.Watcher;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Class that maintains department information and holds given courses of the department
 * Course implements the observer pattern in the form of the Watcher class
 *
 * @author Ali Arshad
 */
public class Department implements Iterable<Course>, Cloneable {
    private String name;
    private long deptId;
    private List<Watcher> watchers;
    private AtomicLong nextCourseId;
    private Map<Long, Course> courses;

    Department(String name, long deptId) {
        this.name = name;
        this.deptId = deptId;
        watchers = new ArrayList<>();
        nextCourseId = new AtomicLong();
        this.courses = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public long getDeptId() {
        return deptId;
    }

    void addCourse(Course newCourse) {
        boolean containsCourse = false;

        for (Course course : courses.values()) {

            // if course is already in this department, add its sections to the pre-existing course
            if (course.equals(newCourse)) {
                containsCourse = true;

                for (Section section : newCourse) {
                    course.addSection(section);
                    notifyWatchers(course, section);
                }
            }
        }

        if (!containsCourse) {
            newCourse.setCourseId(nextCourseId.incrementAndGet());
            courses.put(nextCourseId.get(), newCourse);
        }
    }

    @Override
    public Iterator<Course> iterator() {
        return courses.values().iterator();
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == this.getClass()) {
            Department otherDepartment = (Department) other;
            return (this.name.equals(otherDepartment.name));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode() * 11;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @JsonIgnore
    public List<Course> getCourses() {
        return (new ArrayList<>(courses.values()));
    }

    @JsonIgnore
    public Course getCourse(long courseId) throws IdDoesNotExistException {
        if (courses.containsKey(courseId)) {
            try {
                return (Course) (courses.get(courseId).clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        throw new IdDoesNotExistException("Course with id " + courseId + " does not exist.");
    }

    @JsonIgnore
    public List<Watcher> getWatchers() {
        return new ArrayList<>(watchers);
    }

    public void addWatcher(Watcher watcher) {
        watchers.add(watcher);
    }

    private void notifyWatchers(Course course, Section section) {
        for (SectionComponent sectionComponent : section) {
            for (Watcher watcher : watchers) {
                watcher.stateChanged(course, section, sectionComponent);
            }
        }
    }

    public void deleteWatcherAt(int indexToDelete) {
        watchers.remove(indexToDelete);
    }
}
