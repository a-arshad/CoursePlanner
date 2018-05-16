package ca.cmpt213.a5.model.planner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class that maintains section information and holds given section components of the department
 *
 * @author Ali Arshad
 */
public class Section implements Iterable<SectionComponent>, Cloneable {
    private int semesterCode;
    private long courseOfferingId;
    private String location;
    private String instructors;
    private List<SectionComponent> sectionComponents;
    private final int BASE_TEN_MULTIPLIER = 10;

    Section(int semesterCode, String location, String instructors) {
        this.semesterCode = semesterCode;
        this.location = location;
        this.instructors = instructors;
        sectionComponents = new ArrayList<>();
    }

    public int getYear() {
        // Adds the first 3 digits of the semesterCode to the ESTABLISHED_CENTUARY
        final int ESTABLISHED_CENTUARY = 1900;
        return (int) Math.floor(semesterCode / BASE_TEN_MULTIPLIER) + ESTABLISHED_CENTUARY;
    }

    public String getTerm() {
        //Get the last digit of the semester code
        int termNumber = semesterCode % BASE_TEN_MULTIPLIER;
        String term = null;
        final int SPRING = 1;
        final int SUMMER = 4;
        final int FALL = 7;
        switch (termNumber) {
            case (FALL):
                term = "Fall";
                break;
            case (SPRING):
                term =  "Spring";
                break;
            case (SUMMER):
                term = "SUMMER";
                break;
            default:
                assert (false);
                break;
        }
        return term;
    }

    public long getCourseOfferingId() {
        return courseOfferingId;
    }

    void setCourseOfferingId(long courseOfferingId) {
        this.courseOfferingId = courseOfferingId;
    }

    public int getSemesterCode() {
        return semesterCode;
    }

    public String getLocation() {
        return location;
    }

    public String getInstructors() {
        return instructors;
    }

    void addComponent(SectionComponent newComponent) {
        boolean containsComponent = false;

        for (SectionComponent sectionComponent : sectionComponents) {

            // if the section component is already in this section, merge the components
            if (sectionComponent.equals(newComponent)) {
                containsComponent = true;
                sectionComponent.mergeComponents(newComponent);
            }
        }

        if (!containsComponent)
            sectionComponents.add(newComponent);
    }

    @Override
    public Iterator<SectionComponent> iterator() {
        return sectionComponents.iterator();
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == this.getClass()) {
            Section otherSection = (Section) other;
            return ((semesterCode == otherSection.semesterCode) &&
                    (location.equals(otherSection.location)));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Integer.valueOf(semesterCode).hashCode() * 11 +
                location.hashCode() * 17;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public List<SectionComponent> getSectionComponents() {
        return (new ArrayList<>(sectionComponents));
    }

}
