package org.fenixedu.core.ui.teacher;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.fenixedu.domain.ExecutionCourse;
import net.sourceforge.fenixedu.domain.Grouping;

import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

public class ProjectGroupBean {
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";

    private String name;
    private String projectDescription;
    @DateTimeFormat(pattern = DATE_FORMAT)
    private DateTime enrolmentBeginDay;
    @DateTimeFormat(pattern = DATE_FORMAT)
    private DateTime enrolmentEndDay;
    private Boolean automaticEnrolment;
    private Boolean differentiatedCapacity;
    private String shiftType;
    private Boolean atomicEnrolmentPolicy;
    private Integer minimumGroupCapacity;
    private Integer maximumGroupCapacity;
    private Integer idealGroupCapacity;
    private Integer maxGroupNumber;
    private Map<String, Integer> differentiatedCapacityShifts;
    private String externalId;

    public ProjectGroupBean() {
        super();
        this.enrolmentBeginDay = new DateTime();
        this.differentiatedCapacityShifts = new HashMap<String, Integer>();
    }

    public ProjectGroupBean(ExecutionCourse executionCourse) {
        this();
        this.enrolmentBeginDay = new DateTime();

        executionCourse.getShiftsOrderedByLessons().stream().forEach(shift -> {
            if (shift.getShiftGroupingProperties() != null) {
                differentiatedCapacityShifts.put(shift.getExternalId(), shift.getShiftGroupingProperties().getCapacity());
            } else {
                differentiatedCapacityShifts.put(shift.getExternalId(), null);
            }
        });
    }

    public ProjectGroupBean(Grouping grouping, ExecutionCourse executionCourse) {
        this(executionCourse);
        this.name = grouping.getName();
        this.projectDescription = grouping.getProjectDescription();
        this.enrolmentBeginDay = grouping.getEnrolmentBeginDayDateDateTime();
        this.enrolmentEndDay = grouping.getEnrolmentEndDayDateDateTime();
        this.automaticEnrolment = grouping.getAutomaticEnrolment();
        this.differentiatedCapacity = grouping.getDifferentiatedCapacity();
        this.shiftType = grouping.getShiftType() == null ? "" : grouping.getShiftType().toString();
        this.atomicEnrolmentPolicy = grouping.getEnrolmentPolicy().getType() == 1 ? true : false;
        this.minimumGroupCapacity = grouping.getMinimumCapacity();
        this.maximumGroupCapacity = grouping.getMaximumCapacity();
        this.idealGroupCapacity = grouping.getIdealCapacity();
        this.maxGroupNumber = grouping.getGroupMaximumNumber();
        this.setExternalId(grouping.getExternalId());

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public DateTime getEnrolmentBeginDay() {
        return enrolmentBeginDay;
    }

    public void setEnrolmentBeginDay(DateTime enrolmentBeginDay) {
        this.enrolmentBeginDay = enrolmentBeginDay;
    }

    public DateTime getEnrolmentEndDay() {
        return enrolmentEndDay;
    }

    public void setEnrolmentEndDay(DateTime enrolmentEndDay) {
        this.enrolmentEndDay = enrolmentEndDay;
    }

    public Boolean getAutomaticEnrolment() {
        return automaticEnrolment;
    }

    public void setAutomaticEnrolment(Boolean automaticEnrolment) {
        this.automaticEnrolment = automaticEnrolment;
    }

    public String getShiftType() {
        return shiftType;
    }

    public void setShiftType(String shiftType) {
        this.shiftType = shiftType;
    }

    public Boolean getAtomicEnrolmentPolicy() {
        return atomicEnrolmentPolicy;
    }

    public void setAtomicEnrolmentPolicy(Boolean atomicEnrolmentPolicy) {
        this.atomicEnrolmentPolicy = atomicEnrolmentPolicy;

    }

    public Boolean getDifferentiatedCapacity() {
        return differentiatedCapacity;
    }

    public void setDifferentiatedCapacity(Boolean differentiatedCapacity) {
        this.differentiatedCapacity = differentiatedCapacity;
    }

    public Integer getMinimumGroupCapacity() {
        return minimumGroupCapacity;
    }

    public void setMinimumGroupCapacity(Integer minimumGroupCapacity) {
        this.minimumGroupCapacity = minimumGroupCapacity;
    }

    public Integer getMaximumGroupCapacity() {
        return maximumGroupCapacity;
    }

    public void setMaximumGroupCapacity(Integer maximumGroupCapacity) {
        this.maximumGroupCapacity = maximumGroupCapacity;
    }

    public Integer getIdealGroupCapacity() {
        return idealGroupCapacity;
    }

    public void setIdealGroupCapacity(Integer idealGroupCapacity) {
        this.idealGroupCapacity = idealGroupCapacity;
    }

    public Integer getMaxGroupNumber() {
        return maxGroupNumber;
    }

    public void setMaxGroupNumber(Integer maxGroupNumber) {
        this.maxGroupNumber = maxGroupNumber;
    }

    public Map<String, Integer> getDifferentiatedCapacityShifts() {
        return differentiatedCapacityShifts;
    }

    public void setDifferentiatedCapacityShifts(Map<String, Integer> differentiatedCapacityShifts) {
        this.differentiatedCapacityShifts = differentiatedCapacityShifts;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

}
