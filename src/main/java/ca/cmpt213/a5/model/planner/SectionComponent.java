package ca.cmpt213.a5.model.planner;

/**
 * Class that maintains section component information
 *
 * @author Ali Arshad
 */
public class SectionComponent {
    private String type;
    private int enrollmentTotal;
    private int enrollmentCap;

    SectionComponent(String type, int enrollmentTotal, int enrollmentCap) {
        this.type = type;
        this.enrollmentTotal = enrollmentTotal;
        this.enrollmentCap = enrollmentCap;
    }

    public String getType() {
        return type;
    }

    public int getEnrollmentTotal() {
        return enrollmentTotal;
    }

    public int getEnrollmentCap() {
        return enrollmentCap;
    }

    void mergeComponents(SectionComponent sectionComponent) {
        enrollmentTotal += sectionComponent.getEnrollmentTotal();
        enrollmentCap += sectionComponent.getEnrollmentCap();
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == this.getClass()) {
            SectionComponent otherSectionComponent = (SectionComponent) other;
            return (type.equals(otherSectionComponent.type));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return type.hashCode() * 11;
    }
}
