package ca.cmpt213.a5.controller;

import ca.cmpt213.a5.exceptions.IdDoesNotExistException;
import ca.cmpt213.a5.model.Watcher;
import ca.cmpt213.a5.model.jsonobjects.DataNode;
import ca.cmpt213.a5.model.jsonobjects.Offering;
import ca.cmpt213.a5.model.jsonobjects.PlannerInfo;
import ca.cmpt213.a5.model.jsonobjects.WatcherInfo;
import ca.cmpt213.a5.model.planner.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Rest controller class
 *
 * @author Ali Arshad
 */
@RestController
public class CoursePlannerController {
    private Planner planner;
    private PlannerInfo plannerInfo;
    private AtomicLong nextWatcherId;

    public CoursePlannerController() {
        final int FAILURE = 1;

        nextWatcherId = new AtomicLong();
        plannerInfo = new PlannerInfo();

        try {
            String inputFilePath = "data/course_data_2018.csv";

            planner = new Planner();
            planner.loadOfferingsFromCsv(inputFilePath);
        } catch (IOException ex) {
            System.out.println("Error: Invalid Input File");
            System.exit(FAILURE);
        }
    }

    @GetMapping("/api/dump-model")
    public void dumpModel() {
        System.out.println(planner.getPlannerSummary());
    }

    @GetMapping("/api/about")
    public PlannerInfo getPlannerInfo() {
        return (plannerInfo);
    }

    @GetMapping("/api/departments")
    public List<Department> getAllDepartments() {
        List<Department> departments = planner.getDepartments();

        // Anonymous Comparator which sorts a list of departments alphabetically by name
        departments.sort((Comparator<? super Department>) (o1, o2) -> {
            if ((o1.getClass() == Department.class) && (o2.getClass() == Department.class)) {
                String name = o1.getName();
                String otherName = o2.getName();
                return name.compareTo(otherName);
            }
            throw new IllegalArgumentException();
        });
        return departments;
    }

    @GetMapping("/api/departments/{deptId}/courses")
    public List<Course> getAllCourses(@PathVariable("deptId") long deptId) throws IdDoesNotExistException {
        Department department = planner.getDepartment(deptId);
        List<Course> courses = department.getCourses();

        // Anonymous Comparator class which sorts a list of courses numerically based off of their catalog number
        courses.sort((Comparator<? super Course>) (o1, o2) -> {
            if ((o1.getClass() == Course.class) && (o2.getClass() == Course.class)) {
                String courseCatalogNumber = o1.getCatalogNumber();
                String otherCatalogNumber = o2.getCatalogNumber();
                return courseCatalogNumber.compareTo(otherCatalogNumber);
            }
            throw new IllegalArgumentException();
        });
        return courses;
    }

    @GetMapping("/api/departments/{deptId}/courses/{courseId}/offerings")
    public List<Section> getOfferings(@PathVariable("deptId") long deptId,
                                      @PathVariable("courseId") long courseId) throws IdDoesNotExistException {
        Department department = planner.getDepartment(deptId);
        Course courses = department.getCourse(courseId);
        List<Section> sections = courses.getSections();

        // Anonymous class class which sorts a list of sections numerically based off of their semester code
        sections.sort((Comparator<? super Section>) (o1, o2) -> {
            if ((o1.getClass() == Section.class) && (o2.getClass() == Section.class)) {
                int semester = o1.getSemesterCode();
                int otherSemester = o2.getSemesterCode();
                return semester - otherSemester;
            }
            throw new IllegalArgumentException();
        });
        return sections;
    }

    @GetMapping("/api/departments/{deptId}/courses/{courseId}/offerings/{courseOfferingId}")
    public List<SectionComponent> getOfferings(@PathVariable("deptId") long deptId,
                                               @PathVariable("courseId") long courseId,
                                               @PathVariable("courseOfferingId") long courseOfferingId)
            throws IdDoesNotExistException {

        Department department = planner.getDepartment(deptId);
        Course courses = department.getCourse(courseId);
        Section section = courses.getSection(courseOfferingId);
        return section.getSectionComponents();
    }

    private int calculateNextSemesterCode(int prevSemesterCode) {
        final int SEMESTER_CODE_OFFSET = 3;
        final int BASE_TEN_MULTIPLIER = 10;

        int nextSemesterCode = prevSemesterCode + SEMESTER_CODE_OFFSET;

        if (nextSemesterCode % BASE_TEN_MULTIPLIER == 0)
            nextSemesterCode++;
        return nextSemesterCode;
    }

    @GetMapping("/api/stats/students-per-semester")
    public List<DataNode> getDataNodesForDepartment(@RequestParam("deptId") long deptId) throws IdDoesNotExistException {
        List<DataNode> dataNodes = new ArrayList<>(planner.getEnrollmentData(deptId));
        Collections.sort(dataNodes);

        if (!dataNodes.isEmpty()) {
            int index = 0;
            int nextSemesterCode = dataNodes.get(index).getSemesterCode();

            for (; index < dataNodes.size(); index++) {
                DataNode dataNode = dataNodes.get(index);

                // Generate empty dataNodes between defined dataNodes for missing semesters
                while (dataNode.getSemesterCode() > nextSemesterCode) {
                    dataNodes.add(index, new DataNode(nextSemesterCode, 0));
                    nextSemesterCode = calculateNextSemesterCode(nextSemesterCode);
                    index++;
                }
                nextSemesterCode = calculateNextSemesterCode(nextSemesterCode);
            }
        }
        return dataNodes;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/addoffering")
    public void addOffering(@RequestBody Offering offering) {
        Department department = planner.buildOffering(offering.semesterCode,
                offering.subjectName,
                offering.catalogNumber,
                offering.location,
                offering.enrollmentCap,
                offering.type,
                offering.enrollmentTotal,
                offering.instructors);
        planner.addDepartment(department);
    }

    @GetMapping("/api/watchers")
    public List<Watcher> getAllWatchers() {
        List<Watcher> watchers = planner.getAllWatchers();

        // Anonymous class class which sorts a list of watchers numerically based off of their id
        watchers.sort((Comparator<? super Watcher>) (o1, o2) -> {
            if ((o1 instanceof Watcher) && (o2 instanceof Watcher)) {
                long id = o1.getId();
                long otherId = o2.getId();
                return (int) (id - otherId);
            }
            throw new IllegalArgumentException();
        });
        return watchers;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/watchers")
    public void createWatcher(@RequestBody WatcherInfo watcherInfo) throws IdDoesNotExistException {
        Department department = planner.getDepartment(watcherInfo.deptId);
        Course course = department.getCourse(watcherInfo.courseId);

        department.addWatcher(new Watcher() {
            private long id = nextWatcherId.incrementAndGet();
            private Department watchedDepartment = department;
            private Course watchedCourse = course;
            private List<String> events = new ArrayList<>();

            @Override
            public long getId() {
                return id;
            }

            @Override
            public Department getDepartment() {
                return watchedDepartment;
            }

            @Override
            public Course getCourse() {
                return watchedCourse;
            }

            @Override
            public List<String> getEvents() {
                return new ArrayList<>(events);
            }

            @Override
            public void stateChanged(Course updatedCourse, Section section, SectionComponent sectionComponent) {
                if (watchedCourse.equals(updatedCourse)) {
                    Date date = Calendar.getInstance().getTime();

                    events.add(date.toString() + " Added section " +
                            sectionComponent.getType() + " with enrollment (" +
                            sectionComponent.getEnrollmentTotal() + " / " +
                            sectionComponent.getEnrollmentCap() + ") to offering " +
                            section.getTerm() + " " + section.getYear());
                }
            }
        });
    }

    @GetMapping("/api/watchers/{id}")
    public List<String> getWatcherEvent(@PathVariable("id") long watcherId) throws IdDoesNotExistException {
        List<Watcher> watchers = planner.getAllWatchers();

        for (Watcher watcher : watchers) {

            if (watcher.getId() == watcherId)
                return watcher.getEvents();
        }
        throw new IdDoesNotExistException("Watcher with id " + watcherId + " does not exist.");
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/api/watchers/{id}")
    public void deleteWatcher(@PathVariable("id") long watcherId) throws IdDoesNotExistException {
        planner.deleteWatcher(watcherId);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(IdDoesNotExistException.class)
    public void IdDoesNotExistExceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        request.setAttribute(IdDoesNotExistException.class.getName() + ".ERROR", null);
        try {
            response.sendError(HttpStatus.NOT_FOUND.value(), exception.getMessage());
        } catch (IOException ignored) {
        }
    }
}
