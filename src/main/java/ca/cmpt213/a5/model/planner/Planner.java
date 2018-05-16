package ca.cmpt213.a5.model.planner;

import ca.cmpt213.a5.exceptions.IdDoesNotExistException;
import ca.cmpt213.a5.model.Watcher;
import ca.cmpt213.a5.model.jsonobjects.DataNode;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Class that builds and holds department and course information
 * @author Ali Arshad
 */
public class Planner {
    private Map<Long,Department> departments;
    private AtomicLong nextDeptId;

    public Planner() {
        departments = new HashMap<>();
        nextDeptId = new AtomicLong();
    }

    public void loadOfferingsFromCsv(String filePath) throws IOException {
        File file = new File(filePath);

        int semesterCode;
        String subjectName;
        String catalogNumber;
        String location;
        int enrollmentCap;
        int enrollmentTotal;
        String instructors;
        String type;

        try (Scanner scanner = new Scanner(file)) {

            scanner.useDelimiter("[,\\n]");

            scanner.nextLine();
            while (scanner.hasNextInt()) {
                semesterCode = scanner.nextInt();
                subjectName = (scanner.next()).trim();
                catalogNumber = (scanner.next()).trim();
                location = (scanner.next()).trim();
                enrollmentTotal = scanner.nextInt();
                enrollmentCap = scanner.nextInt();

                instructors = (scanner.findInLine("\".*\""));
                if (instructors == null)
                    instructors = (scanner.next()).trim();
                else
                    instructors = instructors.substring(1, instructors.length() - 1);

                type = (scanner.next()).trim();

                Department department = buildOffering(semesterCode,
                        subjectName,
                        catalogNumber,
                        location,
                        enrollmentCap,
                        type,
                        enrollmentTotal,
                        instructors);

                addDepartment(department);
            }
        }
    }

    public Department buildOffering(int semesterCode,
                                    String subjectName,
                                    String catalogNumber,
                                    String location,
                                    int enrollmentCap,
                                    String type,
                                    int enrollmentTotal,
                                    String instructors) {

        SectionComponent sectionComponent = new SectionComponent(type,
                enrollmentTotal,
                enrollmentCap);

        Section section = new Section(semesterCode,
                location,
                instructors);
        section.addComponent(sectionComponent);

        Course course = new Course(catalogNumber);
        course.addSection(section);

        Department department = new Department(subjectName, nextDeptId.getAndIncrement());
        department.addCourse(course);

        return department;
    }

    public void addDepartment(Department newDepartment) {
        boolean containsCourse = false;

        for (Department department : departments.values()) {

            if (department.equals(newDepartment)) {
                containsCourse = true;

                for (Course course : newDepartment)
                    department.addCourse(course);
            }
        }

        if (!containsCourse)
            departments.put(newDepartment.getDeptId(), newDepartment);
        else
            nextDeptId.decrementAndGet();
    }

    public String getPlannerSummary() {
        StringBuilder sb = new StringBuilder();

        for (Department department : departments.values()) {

            for (Course course : department) {
                sb.append(department.getName());
                sb.append(' ');
                sb.append(course.getCatalogNumber());
                sb.append('\n');

                for (Section section : course) {
                    sb.append('\t');
                    sb.append(section.getSemesterCode());
                    sb.append(" in ");
                    sb.append(section.getLocation());
                    sb.append(" by ");
                    sb.append(section.getInstructors());
                    sb.append('\n');

                    for (SectionComponent sectionComponent : section) {
                        sb.append("\t\tType=");
                        sb.append(sectionComponent.getType());
                        sb.append(", Enrollment=");
                        sb.append(sectionComponent.getEnrollmentTotal());
                        sb.append('/');
                        sb.append(sectionComponent.getEnrollmentCap());
                        sb.append('\n');
                    }
                }
            }
        }
        return (sb.toString());
    }

    public Collection<DataNode> getEnrollmentData(long deptId) throws IdDoesNotExistException{
        Map<Integer, DataNode> dataNodes = new HashMap<>();
        DataNode dataNode;
        int semesterCode;
        String type;
        int enrollmentTotal;
        Department department = getDepartment(deptId);

        for(Course course : department){
            for (Section section : course){
                semesterCode = section.getSemesterCode();

                for(SectionComponent sectionComponent : section){
                    type = sectionComponent.getType();
                    enrollmentTotal = sectionComponent.getEnrollmentTotal();

                    if(type.equals("LEC")){
                        if(dataNodes.containsKey(semesterCode)){
                            dataNode = dataNodes.get(semesterCode);
                            dataNode.increaseEnrollmentTotal(enrollmentTotal);
                        }
                        else
                            dataNodes.put(semesterCode, new DataNode(semesterCode, enrollmentTotal));
                    }
                }
            }
        }
        return dataNodes.values();
    }

    public List<Department> getDepartments(){
        return (new ArrayList<>(departments.values()));
    }

    public Department getDepartment(long deptId) throws IdDoesNotExistException{

        if(departments.containsKey(deptId)) {
            try {
                return (Department) (departments.get(deptId).clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        throw new IdDoesNotExistException("Department with id " + deptId + " does not exist.");
    }

    public List<Watcher> getAllWatchers() {
        List<Watcher> watchers = new ArrayList<>();
        for(Department department : departments.values()){
            watchers.addAll(department.getWatchers());
        }
        return watchers;
    }

    public void deleteWatcher(long watcherId) throws IdDoesNotExistException {
        int indexToDelete;

        for(Department department : departments.values()){
            indexToDelete = 0;

            for(Watcher watcher : department.getWatchers()) {

                if(watcher.getId() == watcherId) {
                    department.deleteWatcherAt(indexToDelete);
                    return;
                }
                indexToDelete++;
            }
        }
        throw new IdDoesNotExistException("Watcher with id " + watcherId + " does not exist.");
    }
}
