package ca.cmpt213.a5.model.planner;

import ca.cmpt213.a5.exceptions.IdDoesNotExistException;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Class that maintains course information and holds given sections of the course
 *
 * @author Ali Arshad
 */
public class Course implements Iterable<Section>, Cloneable {
    private String catalogNumber;
    private long courseId;
    private AtomicLong nextCourseOfferingId;
    private Map<Long, Section> sections;

    Course(String catalogNumber) {
        this.catalogNumber = catalogNumber;
        nextCourseOfferingId = new AtomicLong();
        this.sections = new HashMap<>();
    }

    public String getCatalogNumber() {
        return catalogNumber;
    }

    void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public long getCourseId() {
        return courseId;
    }

    void addSection(Section newSection) {
        boolean containsSection = false;

        for (Section section : sections.values()) {

            // if section is already in this course, add its components to the pre-existing section
            if (section.equals(newSection)) {
                containsSection = true;

                for (SectionComponent sectionComponent : newSection)
                    section.addComponent(sectionComponent);
            }
        }

        if (!containsSection) {
            newSection.setCourseOfferingId(nextCourseOfferingId.incrementAndGet());
            sections.put(nextCourseOfferingId.get(), newSection);
        }
    }

    @JsonIgnore
    public List<Section> getSections() {
        return (new ArrayList<>(sections.values()));
    }

    @JsonIgnore
    public Section getSection(long courseOfferingId) throws IdDoesNotExistException {
        if (sections.containsKey(courseOfferingId)) {
            try {
                return (Section) (sections.get(courseOfferingId).clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        throw new IdDoesNotExistException("Offering with the id " + courseOfferingId + "does not exist.");
    }

    @Override
    public Iterator<Section> iterator() {
        return sections.values().iterator();
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == this.getClass()) {
            Course otherCourse = (Course) other;
            return (this.catalogNumber.equals(otherCourse.catalogNumber));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return catalogNumber.hashCode() * 11;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
