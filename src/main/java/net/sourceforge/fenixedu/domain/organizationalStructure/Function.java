/**
 * Copyright © 2002 Instituto Superior Técnico
 *
 * This file is part of FenixEdu Core.
 *
 * FenixEdu Core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Core.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.fenixedu.domain.organizationalStructure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.fenixedu.domain.DomainObjectUtil;
import net.sourceforge.fenixedu.domain.ExecutionYear;
import net.sourceforge.fenixedu.domain.Person;
import net.sourceforge.fenixedu.domain.exceptions.DomainException;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.NullComparator;
import org.fenixedu.bennu.core.domain.Bennu;
import org.joda.time.YearMonthDay;

import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

public class Function extends Function_Base {

    public static final Comparator<Function> COMPARATOR_BY_ORDER = new Comparator<Function>() {
        private ComparatorChain chain = null;

        @Override
        public int compare(Function one, Function other) {
            if (this.chain == null) {
                chain = new ComparatorChain();
                chain.addComparator(new BeanComparator("functionOrder", new NullComparator()));
                chain.addComparator(new BeanComparator("functionType", new NullComparator()));
                chain.addComparator(new BeanComparator("name"));
                chain.addComparator(DomainObjectUtil.COMPARATOR_BY_ID);
            }
            return chain.compare(one, other);
        }
    };

    public Function(MultiLanguageString functionName, YearMonthDay beginDate, YearMonthDay endDate, FunctionType type, Unit unit) {
        super();
        setValues(functionName, beginDate, endDate, type, unit, AccountabilityTypeEnum.MANAGEMENT_FUNCTION);
    }

    public Function(MultiLanguageString functionName, YearMonthDay beginDate, YearMonthDay endDate, FunctionType type, Unit unit,
            AccountabilityTypeEnum accountabilityTypeEnum) {
        super();
        setValues(functionName, beginDate, endDate, type, unit, accountabilityTypeEnum);
    }

    protected void setValues(MultiLanguageString functionName, YearMonthDay beginDate, YearMonthDay endDate, FunctionType type,
            Unit unit, AccountabilityTypeEnum accountabilityTypeEnum) {
        edit(functionName, beginDate, endDate, type);
        setUnit(unit);
        setType(accountabilityTypeEnum);
    }

    protected Function() {
        super();
    }

    public void edit(MultiLanguageString functionName, YearMonthDay beginDate, YearMonthDay endDate, FunctionType type) {
        setTypeName(functionName);
        setFunctionType(type);
        setBeginDateYearMonthDay(beginDate);
        setEndDateYearMonthDay(endDate);
    }

    @Override
    public void setUnit(Unit unit) {
        if (unit == null) {
            throw new DomainException("error.function.no.unit");
        }
        super.setUnit(unit);
    }

    public boolean isActive(YearMonthDay currentDate) {
        return belongsToPeriod(currentDate, currentDate);
    }

    public boolean isActive() {
        return isActive(new YearMonthDay());
    }

    public boolean belongsToPeriod(YearMonthDay beginDate, YearMonthDay endDate) {
        return ((endDate == null || !getBeginDateYearMonthDay().isAfter(endDate)) && (getEndDateYearMonthDay() == null || !getEndDateYearMonthDay()
                .isBefore(beginDate)));
    }

    public void delete() {
        if (!canBeDeleted()) {
            throw new DomainException("error.delete.function");
        }
        setParentInherentFunction(null);
        super.setUnit(null);
        setRootDomainObject(null);
        deleteDomainObject();
    }

    private boolean canBeDeleted() {
        return getAccountabilitiesSet().isEmpty() && getInherentFunctionsSet().isEmpty() && getPersonsInFunctionGroup() == null;
    }

    public List<PersonFunction> getPersonFunctions() {
        List<PersonFunction> personFunctions = new ArrayList<PersonFunction>();
        for (Accountability accountability : getAccountabilitiesSet()) {
            if (accountability.isPersonFunction()) {
                personFunctions.add((PersonFunction) accountability);
            }
        }
        return personFunctions;
    }

    public List<PersonFunction> getActivePersonFunctions() {
        List<PersonFunction> personFunctions = new ArrayList<PersonFunction>();
        YearMonthDay currentDate = new YearMonthDay();
        for (Accountability accountability : getAccountabilitiesSet()) {
            if (accountability.isPersonFunction() && accountability.isActive(currentDate)) {
                personFunctions.add((PersonFunction) accountability);
            }
        }
        return personFunctions;
    }

    public List<PersonFunction> getActivePersonFunctionsByPerson(final Person person) {
        List<PersonFunction> personFunctions = new ArrayList<PersonFunction>();
        YearMonthDay currentDate = new YearMonthDay();
        for (Accountability accountability : getAccountabilitiesSet()) {
            if (accountability.isPersonFunction() && accountability.isActive(currentDate)) {
                PersonFunction personFunction = (PersonFunction) accountability;
                if (personFunction.getPerson().equals(person)) {
                    personFunctions.add(personFunction);
                }
            }
        }
        return personFunctions;
    }

    public boolean isInherentFunction() {
        return (this.getParentInherentFunction() != null);
    }

    public void addParentInherentFunction(Function parentInherentFunction) {
        if (parentInherentFunction.equals(this)) {
            throw new DomainException("error.function.parentInherentFunction.equals.function");
        }
        setParentInherentFunction(null);
        setParentInherentFunction(parentInherentFunction);
    }

    @jvstm.cps.ConsistencyPredicate
    protected boolean checkDateInterval() {
        final YearMonthDay start = getBeginDateYearMonthDay();
        final YearMonthDay end = getEndDateYearMonthDay();
        return start != null && (end == null || !start.isAfter(end));
    }

    public static Function createVirtualFunction(Unit unit, MultiLanguageString name) {
        return new Function(name, new YearMonthDay(), null, FunctionType.VIRTUAL, unit);
    }

    public boolean isVirtual() {
        FunctionType type = getFunctionType();
        return type != null && type.equals(FunctionType.VIRTUAL);
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    public static Set<Function> readAllActiveFunctionsByType(FunctionType functionType) {
        Set<Function> result = new HashSet<Function>();
        YearMonthDay currentDate = new YearMonthDay();
        Collection<AccountabilityType> accountabilityTypes = Bennu.getInstance().getAccountabilityTypesSet();
        for (AccountabilityType accountabilityType : accountabilityTypes) {
            if (accountabilityType.isFunction() && ((Function) accountabilityType).getFunctionType() != null
                    && ((Function) accountabilityType).getFunctionType().equals(functionType)
                    && ((Function) accountabilityType).isActive(currentDate)) {
                result.add((Function) accountabilityType);
            }
        }
        return result;
    }

    public static Set<Function> readAllFunctionsByType(FunctionType functionType) {
        Set<Function> result = new HashSet<Function>();
        Collection<AccountabilityType> accountabilityTypes = Bennu.getInstance().getAccountabilityTypesSet();
        for (AccountabilityType accountabilityType : accountabilityTypes) {
            if (accountabilityType.isFunction() && ((Function) accountabilityType).getFunctionType() != null
                    && ((Function) accountabilityType).getFunctionType().equals(functionType)) {
                result.add((Function) accountabilityType);
            }
        }
        return result;
    }

    public List<PersonFunction> getActivePersonFunctionsStartingIn(ExecutionYear executionYear) {
        List<PersonFunction> personFunctions = new ArrayList<PersonFunction>();
        for (Accountability accountability : getAccountabilitiesSet()) {
            if (accountability.isPersonFunction()) {
                if (accountability.getBeginDate().isBefore(executionYear.getEndDateYearMonthDay())
                        && (accountability.getEndDate() == null || accountability.getEndDate().isAfter(
                                executionYear.getBeginDateYearMonthDay()))) {
                    personFunctions.add((PersonFunction) accountability);
                }
            }
        }
        return personFunctions;
    }

    public boolean isOfFunctionType(FunctionType functionType) {
        return getFunctionType().equals(functionType);
    }

    public boolean isOfAnyFunctionType(Collection<FunctionType> functionTypes) {
        for (FunctionType functionType : functionTypes) {
            if (isOfFunctionType(functionType)) {
                return getFunctionType().equals(functionType);
            }
        }
        return false;
    }

    @Deprecated
    public java.util.Date getBeginDate() {
        org.joda.time.YearMonthDay ymd = getBeginDateYearMonthDay();
        return (ymd == null) ? null : new java.util.Date(ymd.getYear() - 1900, ymd.getMonthOfYear() - 1, ymd.getDayOfMonth());
    }

    @Deprecated
    public void setBeginDate(java.util.Date date) {
        if (date == null) {
            setBeginDateYearMonthDay(null);
        } else {
            setBeginDateYearMonthDay(org.joda.time.YearMonthDay.fromDateFields(date));
        }
    }

    @Deprecated
    public java.util.Date getEndDate() {
        org.joda.time.YearMonthDay ymd = getEndDateYearMonthDay();
        return (ymd == null) ? null : new java.util.Date(ymd.getYear() - 1900, ymd.getMonthOfYear() - 1, ymd.getDayOfMonth());
    }

    @Deprecated
    public void setEndDate(java.util.Date date) {
        if (date == null) {
            setEndDateYearMonthDay(null);
        } else {
            setEndDateYearMonthDay(org.joda.time.YearMonthDay.fromDateFields(date));
        }
    }

}
