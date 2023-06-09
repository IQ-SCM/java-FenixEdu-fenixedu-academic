/**
 * Copyright © 2014 Instituto Superior Técnico
 *
 * This file is part of FenixEdu Spaces.
 *
 * FenixEdu Spaces is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Spaces is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Spaces.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.core.ui.teacher;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import net.sourceforge.fenixedu.domain.Attends;
import net.sourceforge.fenixedu.domain.ExecutionCourse;
import net.sourceforge.fenixedu.domain.Grouping;
import net.sourceforge.fenixedu.domain.Shift;
import net.sourceforge.fenixedu.domain.ShiftType;
import net.sourceforge.fenixedu.domain.StudentGroup;
import net.sourceforge.fenixedu.domain.student.Registration;
import net.sourceforge.fenixedu.util.EnrolmentGroupPolicyType;

import org.fenixedu.spaces.core.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

@Service
public class StudentGroupService {

    @Autowired
    MessageSource messageSource;

    @Autowired(required = false)
    NotificationService notificationService;

    public StudentGroupService() {
    }

    @Atomic
    public Grouping createOrEditGrouping(ProjectGroupBean bean, ExecutionCourse executionCourse) {
        EnrolmentGroupPolicyType enrolmentPolicyType =
                bean.getAtomicEnrolmentPolicy() != null && bean.getAtomicEnrolmentPolicy() ? new EnrolmentGroupPolicyType(1) : new EnrolmentGroupPolicyType(
                        2);
                Grouping grouping;
                ShiftType shiftType =
                        bean.getShiftType() == null || bean.getShiftType().isEmpty() ? null : ShiftType.valueOf(bean.getShiftType());
                if (executionCourse.getGroupingByName(bean.getName()) == null) {
                    grouping =
                            Grouping.create(bean.getName(), bean.getEnrolmentBeginDay().toDate(), bean.getEnrolmentEndDay().toDate(),
                                    enrolmentPolicyType, bean.getMaxGroupNumber(), bean.getIdealGroupCapacity(),
                                    bean.getMaximumGroupCapacity(), bean.getMinimumGroupCapacity(), bean.getProjectDescription(),
                                    shiftType, bean.getAutomaticEnrolment(), bean.getDifferentiatedCapacity(), executionCourse,
                                    bean.getDifferentiatedCapacityShifts());

                } else {
                    grouping = executionCourse.getGroupingByName(bean.getName());
                    grouping.edit(bean.getName(), bean.getEnrolmentBeginDay().toDate(), bean.getEnrolmentEndDay().toDate(),
                            enrolmentPolicyType, bean.getMaxGroupNumber(), bean.getIdealGroupCapacity(), bean.getMaximumGroupCapacity(),
                            bean.getMinimumGroupCapacity(), bean.getProjectDescription(), shiftType, bean.getAutomaticEnrolment(),
                            bean.getDifferentiatedCapacity(), bean.getDifferentiatedCapacityShifts());
                }

                return grouping;
    }

    @Atomic
    public void updateGroupingAttends(Grouping grouping, Map<String, Boolean> studentsToRemove, Map<String, Boolean> studentsToAdd) {
        for (Map.Entry<String, Boolean> entry : studentsToRemove.entrySet()) {
            if (entry.getValue()) {
                Attends attends = (Attends) FenixFramework.getDomainObject(entry.getKey());
                if (attends != null) {
                    grouping.getStudentGroupsSet().forEach(studentGroup -> studentGroup.removeAttends(attends));
                    grouping.removeAttends(attends);
                }
            }
        }

        for (Map.Entry<String, Boolean> entry : studentsToAdd.entrySet()) {
            if (entry.getValue()) {
                Registration registration = (Registration) FenixFramework.getDomainObject(entry.getKey());

                if (grouping.getAttendsSet().stream().noneMatch(attends -> attends.getRegistration().equals(registration))) {
                    Optional<Attends> opt =
                            registration.getAssociatedAttendsSet().stream()
                            .filter(attends -> grouping.getExecutionCourses().stream().anyMatch(ec -> attends.isFor(ec)))
                            .findAny();
                    if (opt.isPresent()) {
                        grouping.addAttends(opt.get());
                    }
                }
            }
        }
    }

    @Atomic
    public StudentGroup createStudentGroup(Grouping grouping, Shift shift) {
        ArrayList<Registration> students = new ArrayList<Registration>();

        int groupNumber = grouping.findMaxGroupNumber() + 1;
        grouping.createStudentGroup(shift, groupNumber, students);
        return grouping.readStudentGroupBy(groupNumber);
    }

    @Atomic
    public void updateStudentGroupMembers(StudentGroup studentGroup, Map<String, Boolean> studentsToRemove,
            Map<String, Boolean> studentsToAdd) {

        for (Map.Entry<String, Boolean> entry : studentsToRemove.entrySet()) {
            if (entry.getValue()) {
                Attends attends = (Attends) FenixFramework.getDomainObject(entry.getKey());
                if (attends != null) {
                    studentGroup.removeAttends(attends);
                }
            }
        }

        for (Map.Entry<String, Boolean> entry : studentsToAdd.entrySet()) {
            if (entry.getValue()) {
                Attends attends = (Attends) FenixFramework.getDomainObject(entry.getKey());
                if (attends != null) {
                    studentGroup.addAttends(attends);
                }
            }
        }
    }

    @Atomic
    public void updateStudentGroupShift(StudentGroup studentGroup, Shift shift) {
        studentGroup.setShift(shift);

    }

    @Atomic
    public void deleteStudentGroup(StudentGroup studentGroup) {
        studentGroup.delete();
    }

    @Atomic
    public void deleteGrouping(Grouping grouping) {
        grouping.delete();
    }

}
