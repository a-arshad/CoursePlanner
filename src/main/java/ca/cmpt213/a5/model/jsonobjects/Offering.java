package ca.cmpt213.a5.model.jsonobjects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * JSON object class that merges fields from the Segment and SegmentComponent classes
 *
 * @author Ali Arshad
 */
public class Offering {
    @JsonProperty(value="semester")
    public int semesterCode;
    public String subjectName;
    public String catalogNumber;
    public String location;
    public int enrollmentCap;
    @JsonProperty(value="component")
    public String type;
    public int enrollmentTotal;
    @JsonProperty(value="instructor")
    public String instructors;
}
