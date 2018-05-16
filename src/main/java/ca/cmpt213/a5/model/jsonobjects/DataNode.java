package ca.cmpt213.a5.model.jsonobjects;

/**
 * JSON object class that defines a singular node for a graph in the UI
 *
 * @author Ali Arshad
 */
public class DataNode implements Comparable<DataNode> {
    private int semesterCode;
    private int totalCoursesTaken;

    public DataNode(int semesterCode, int totalCoursesTaken) {
        this.semesterCode = semesterCode;
        this.totalCoursesTaken = totalCoursesTaken;
    }

    public int getSemesterCode() {
        return semesterCode;
    }

    public int getTotalCoursesTaken() {
        return totalCoursesTaken;
    }

    public void increaseEnrollmentTotal(int enrollmentTotal) {
        this.totalCoursesTaken += enrollmentTotal;
    }

    @Override
    public int compareTo(DataNode other) {
        return this.semesterCode - other.semesterCode;
    }
}
