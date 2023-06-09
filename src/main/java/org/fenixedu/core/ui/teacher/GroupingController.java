package org.fenixedu.core.ui.teacher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import net.sourceforge.fenixedu.domain.ExecutionCourse;
import net.sourceforge.fenixedu.domain.ExportGrouping;
import net.sourceforge.fenixedu.domain.Grouping;
import net.sourceforge.fenixedu.domain.Shift;
import net.sourceforge.fenixedu.domain.ShiftType;
import net.sourceforge.fenixedu.domain.StudentGroup;
import net.sourceforge.fenixedu.domain.student.Registration;
import net.sourceforge.fenixedu.presentationTier.Action.teacher.ManageExecutionCourseDA;
import net.sourceforge.fenixedu.util.ProposalState;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.RedirectView;

import pt.ist.fenixframework.FenixFramework;

@Controller
@RequestMapping("/teacher/{executionCourse}/student-groups/")
public class GroupingController extends ExecutionCourseController {

    @Autowired
    StudentGroupService studentGroupService;

    @Override
    protected Class<?> getFunctionalityType() {
        return ManageExecutionCourseDA.class;
    }

    @RequestMapping(value = "/show", method = RequestMethod.GET)
    public TeacherView showStudentGroups(Model model) {
        return new TeacherView("viewProjectsAndLink");
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public TeacherView create(Model model) {
        model.addAttribute("projectGroup", new ProjectGroupBean(this.executionCourse));
        return new TeacherView("insertGroupProperties");
    }

    @RequestMapping(value = "/edit/{grouping}", method = RequestMethod.GET)
    public TeacherView edit(Model model, Grouping grouping) {
        model.addAttribute("projectGroup", new ProjectGroupBean(grouping, this.executionCourse));
        return new TeacherView("insertGroupProperties");
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public AbstractUrlBasedView create(Model model, @ModelAttribute("projectGroup") ProjectGroupBean projectGroup,
            @PathVariable ExecutionCourse executionCourse, BindingResult bindingResult) {

        ArrayList<String> errors = new ArrayList<>();

        if (bindingResult.hasErrors()) {
            errors.add("error.groupProperties.errorsDetected");
        }

        if (projectGroup.getName().isEmpty()) {
            errors.add("error.groupProperties.missingName");
        }

        ShiftType shiftType =
                projectGroup.getShiftType() == null || projectGroup.getShiftType().isEmpty() ? null : ShiftType
                        .valueOf(projectGroup.getShiftType());

        if (projectGroup.getDifferentiatedCapacity()
                && projectGroup.getMaximumGroupCapacity() != null
                && projectGroup
                .getDifferentiatedCapacityShifts()
                .entrySet()
                .stream()
                        .anyMatch(
                        entry -> ((Shift) FenixFramework.getDomainObject(entry.getKey())).getTypes().contains(shiftType)
                        && entry.getValue() != null
                        && ((Shift) FenixFramework.getDomainObject(entry.getKey())).getLotacao() != 0 /* it means it was locked from students enrolment only*/
                        && entry.getValue() * projectGroup.getMaximumGroupCapacity() > ((Shift) FenixFramework
                                .getDomainObject(entry.getKey())).getLotacao())) {
            errors.add("error.groupProperties.capacityOverflow");
        }

        if (smallerThan(projectGroup.getMaximumGroupCapacity(), projectGroup.getMinimumGroupCapacity())) {
            errors.add("error.groupProperties.minimum");
        }

        if (smallerThan(projectGroup.getMaximumGroupCapacity(), projectGroup.getIdealGroupCapacity())) {
            errors.add("error.groupProperties.ideal.maximum");
        }

        if (smallerThan(projectGroup.getIdealGroupCapacity(), projectGroup.getMinimumGroupCapacity())) {
            errors.add("error.groupProperties.ideal.minimum");
        }

        if (projectGroup.getEnrolmentBeginDay() == null) {
            errors.add("error.groupProperties.missingEnrolmentBeginDay");
        }

        if (projectGroup.getEnrolmentEndDay() == null) {
            errors.add("error.groupProperties.missingEnrolmentEndDay");
        }

        if (projectGroup.getEnrolmentEndDay() != null && projectGroup.getEnrolmentEndDay() != null
                && projectGroup.getEnrolmentEndDay().isBefore(projectGroup.getEnrolmentBeginDay())) {
            errors.add("error.manager.wrongDates");
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return new TeacherView("insertGroupProperties");
        }

        Grouping grouping = studentGroupService.createOrEditGrouping(projectGroup, executionCourse);
        return new RedirectView("/teacher/" + executionCourse.getExternalId() + "/student-groups/view/"
                + grouping.getExternalId(), true);
    }

    private static Boolean smallerThan(Integer a, Integer b) {
        return a != null && b != null && a < b;
    }

    @RequestMapping(value = "/view/{grouping}", method = RequestMethod.GET)
    public TeacherView viewGrouping(Model model, @PathVariable Grouping grouping) {
        List<Shift> shiftList = new ArrayList<Shift>();

        if (grouping.getShiftType() != null) {
            shiftList =
                    grouping.getExportGroupingsSet().stream().map(ExportGrouping::getExecutionCourse)
                    .flatMap(ec -> ec.getAssociatedShifts().stream()).sorted(Shift.SHIFT_COMPARATOR_BY_NAME)
                    .filter(shift -> shift.containsType(grouping.getShiftType())).collect(Collectors.toList());
        }

        HashMap<Shift, TreeSet<StudentGroup>> studentGroupsByShift = new HashMap<Shift, TreeSet<StudentGroup>>();

        for (Shift shift : shiftList) {
            TreeSet<StudentGroup> tree = new TreeSet<StudentGroup>(StudentGroup.COMPARATOR_BY_GROUP_NUMBER);
            tree.addAll(shift.getAssociatedStudentGroups(grouping));
            studentGroupsByShift.put(shift, tree);
        }

        if (shiftList.isEmpty()) {
            TreeSet<StudentGroup> studentGroups = new TreeSet<StudentGroup>(StudentGroup.COMPARATOR_BY_GROUP_NUMBER);
            studentGroups.addAll(grouping.getStudentGroupsSet());
            model.addAttribute("studentGroups", studentGroups);
        }

        model.addAttribute("studentGroupsByShift", studentGroupsByShift);
        model.addAttribute("grouping", grouping);
        model.addAttribute("shifts", shiftList);
        return new TeacherView("viewShiftsAndGroups");
    }

    @RequestMapping(value = "/viewAttends/{grouping}", method = RequestMethod.GET)
    public TeacherView viewAttends(Model model, @PathVariable Grouping grouping) {
        model.addAttribute("grouping", grouping);

        ArrayList<Registration> studentsNotAttending = new ArrayList<Registration>();
        for (final ExportGrouping exportGrouping : grouping.getExportGroupingsSet()) {
            if (exportGrouping.getProposalState().getState() == ProposalState.ACEITE
                    || exportGrouping.getProposalState().getState() == ProposalState.CRIADOR) {
                exportGrouping.getExecutionCourse().getAttendsSet().stream()
                .filter(attend -> !grouping.getAttendsSet().contains(attend))
                .forEach(attend -> studentsNotAttending.add(attend.getRegistration()));

            }
        }

        model.addAttribute("studentsNotAttending", studentsNotAttending);
        return new TeacherView("viewAttendsSet");

    }

    @RequestMapping(value = "/editAttends/{grouping}", method = RequestMethod.POST)
    public TeacherView editAttends(Model model, @PathVariable Grouping grouping,
            @ModelAttribute("attends") @Validated AttendsBean attendsBean, @PathVariable ExecutionCourse executionCourse,
            BindingResult bindingResult) {

        Map<String, Boolean> studentsToRemove = attendsBean.getRemoveStudent();
        Map<String, Boolean> studentsToAdd = attendsBean.getAddStudent();
        if (bindingResult.hasErrors()) {
            model.addAttribute("removeStudent", studentsToRemove);
            model.addAttribute("addStudent", studentsToAdd);
            model.addAttribute("errors", "binding error " + bindingResult.getAllErrors());
            return viewAttends(model, grouping);
        }

        studentGroupService.updateGroupingAttends(grouping, studentsToRemove, studentsToAdd);

        return viewAttends(model, grouping);

    }

    @RequestMapping(value = "/viewAllStudentsAndGroups/{grouping}", method = RequestMethod.GET)
    public TeacherView viewAllStudentsAndGroups(Model model, @PathVariable Grouping grouping) {
        model.addAttribute("grouping", grouping);
        model.addAttribute("studentsInStudentGroupsSize",
                grouping.getStudentGroupsSet().stream().mapToInt(sg -> sg.getAttendsSet().size()).sum());
        return new TeacherView("viewAllStudentsAndGroups");

    }

    @RequestMapping(value = "/viewStudentsAndGroupsByShift/{grouping}", method = RequestMethod.GET)
    public TeacherView viewStudentsAndGroupsByShift(Model model, @PathVariable Grouping grouping) {
        model.addAttribute("grouping", grouping);

        return new TeacherView("viewStudentsAndGroupsByShift");
    }

    @RequestMapping(value = "/viewStudentsAndGroupsByShift/{grouping}/shift/{shift}", method = RequestMethod.GET)
    public TeacherView viewStudentsAndGroupsByShift(Model model, @PathVariable Grouping grouping, @PathVariable Shift shift) {
        model.addAttribute("shift", shift);
        model.addAttribute("grouping", grouping);
        TreeSet<StudentGroup> studentsByGroup = new TreeSet<StudentGroup>(StudentGroup.COMPARATOR_BY_GROUP_NUMBER);
        studentsByGroup.addAll(shift.getAssociatedStudentGroups(grouping));
        model.addAttribute("studentsByGroup", studentsByGroup);

        return new TeacherView("viewStudentsAndGroupsByShift");
    }

    @RequestMapping(value = "/deleteGrouping/{grouping}", method = RequestMethod.POST)
    public TeacherView deleteGrouping(Model model, @PathVariable Grouping grouping) {
        studentGroupService.deleteGrouping(grouping);
        return new TeacherView("viewProjectsAndLink");

    }
}