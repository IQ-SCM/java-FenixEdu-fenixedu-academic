<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="net.sourceforge.fenixedu.presentationTier.Action.sop.utils.SessionConstants" %>
<%@ page import="net.sourceforge.fenixedu.dataTransferObject.InfoExecutionDegree" %>
<%@ page import="net.sourceforge.fenixedu.domain.degree.DegreeType" %>

<!-- Import new CSS for this section: #navlateral  -->
<style>@import url(<%= request.getContextPath() %>/CSS/navlateralnew.css);</style>

<logic:present name="<%= SessionConstants.MASTER_DEGREE %>"  >
	<bean:define id="infoExecutionDegree" name="<%= SessionConstants.MASTER_DEGREE %>" scope="session" type="InfoExecutionDegree" />
	<bean:define id="infoDegreeCurricularPlan" name="infoExecutionDegree" property="infoDegreeCurricularPlan" />
	<bean:define id="degreeCurricularPlanID" name="infoDegreeCurricularPlan" property="idInternal" />
	<bean:define id="executionDegreeID" name="infoExecutionDegree" property="idInternal" />

	<%-- Start of Master Degree Coordinator Options --%>
	<logic:equal name="infoExecutionDegree" 
		property="infoDegreeCurricularPlan.infoDegree.tipoCurso" 
		value="<%= DegreeType.MASTER_DEGREE.toString() %>">
			<strong><bean:message key="link.coordinator.candidate"/></strong>
			<ul>
	        	<li>
		        	<html:link page="<%= "/candidateOperation.do?method=getCandidates&action=visualize&page=0&degreeCurricularPlanID=" + degreeCurricularPlanID %>">
		        	<bean:message key="link.coordinator.visualizeCandidate" /></html:link>
		        </li>
	        	<li>
	        		<bean:define id="link">/prepareCandidateApproval.do?method=chooseExecutionDegree&page=0&degreeCurricularPlanID=
	        		</bean:define>
		        	<bean:define id="prepareCandidateApprovalLink">
    					<bean:write name="link"/><bean:write name="degreeCurricularPlanID"/>
    				</bean:define> 	
		        	
		        	<html:link page='<%= pageContext.findAttribute("prepareCandidateApprovalLink").toString() %>'>
		        	<bean:message key="link.coordinator.approveCandidates" /></html:link>	        	
		        </li>
				<li>
					<bean:define id="link2">/displayCandidateListToMakeStudyPlan.do?method=prepareSelectCandidates&page=0&degreeCurricularPlanID=
	        		</bean:define>
		        	<bean:define id="displayCandidateListToMakeStudyPlanLink">
    					<bean:write name="link2"/><bean:write name="degreeCurricularPlanID"/>
    				</bean:define> 	
					<html:link page="<%= pageContext.findAttribute("displayCandidateListToMakeStudyPlanLink").toString() %>">
					<bean:message key="link.masterDegree.administrativeOffice.makeStudyPlan" /></html:link>
				</li>
				<li>
					<bean:define id="link3">/printAllCandidatesList.do?method=prepare&degreeCurricularPlanID=
	        		</bean:define>
		        	<bean:define id="printAllCandidatesListLink">
    					<bean:write name="link3"/><bean:write name="degreeCurricularPlanID"/>
    				</bean:define> 	
					<html:link page="<%= pageContext.findAttribute("printAllCandidatesListLink").toString() %>">
					<bean:message key="link.masterDegree.candidateListFilter.printListAllCandidatesFilterMenu"/></html:link>
				</li>
			</ul>
			<br/><br/>
			<strong><bean:message key="link.coordinator.student"/></strong>
			<ul>
				<li>
					<bean:define id="link2">/listStudentsForCoordinator.do?method=getStudentsFromDCP&page=0&degreeCurricularPlanID=
	        		</bean:define>
		        	<bean:define id="listStudentsForCoordinator">
    					<bean:write name="link2"/><bean:write name="degreeCurricularPlanID"/>
    				</bean:define> 	
					<html:link page="<%= pageContext.findAttribute("listStudentsForCoordinator").toString() %>">
					<bean:message key="link.coordinator.studentListByDegree" /></html:link>
				</li>
				<li>				
					<bean:define id="link2">/studentListByDegree.do?method=getCurricularCourses&jspTitle=title.studentListByCourse&page=0&degreeCurricularPlanID=
	        		</bean:define>
		        	<bean:define id="studentListByDegree">
    					<bean:write name="link2"/><bean:write name="degreeCurricularPlanID"/>
    				</bean:define> 	
					<html:link page="<%= pageContext.findAttribute("studentListByDegree").toString() %>">
					<bean:message key="link.studentListByCourse" /></html:link>					
				</li>
				<li>
					<html:link page='<%= "/studentsGratuityList.do?method=coordinatorStudentsGratuityList&amp;order=studentNumber&amp;degreeCurricularPlanID=" + degreeCurricularPlanID.toString()%>'>
						<bean:message key="link.coordinator.studentAndGratuityListByDegree"/>
					</html:link>
				</li>
				
				<li>
					<html:link page='<%= "/student/displayStudentThesisList.faces?degreeCurricularPlanID=" + degreeCurricularPlanID.toString()%>'>
						<bean:message key="link.coordinator.studentByThesis"/>
					</html:link>
				</li>			
			</ul>
			<br/><br/>
	</logic:equal>
	
	<%-- Start of Degree Coordinator Options --%>
	<logic:notEqual name="infoExecutionDegree" property="infoDegreeCurricularPlan.infoDegree.tipoCurso" value="<%= DegreeType.MASTER_DEGREE.toString() %>">
		<strong><bean:message key="label.coordinator.degreeSite.informations"/></strong>
		<ul>
			
			<li>
				<html:link page="<%= "/tutorManagement.do?method=prepareChooseTutor&executionDegreeId=" + executionDegreeID + "&degreeCurricularPlanID=" + degreeCurricularPlanID %>">				
					<bean:message key="label.coordinator.tutors"/>
				</html:link>
			</li>
		</ul>
		<br/><br/>
		<strong><bean:message key="label.coordinator.degreeSite.students"/></strong>
		<ul>
			<li>
				<html:link page="<%= "/students.faces?degreeCurricularPlanID=" + degreeCurricularPlanID %>">
				    <bean:message key="list.students"/>
				</html:link>			
			</li>
			<li>
				<html:link page="<%= "/viewStudentCurriculum.do?method=prepareView&amp;executionDegreeId=" + executionDegreeID + "&degreeCurricularPlanID=" + degreeCurricularPlanID %>">
				    <bean:message key="label.coordinator.studentInformation"/>
				</html:link>			
			</li>		
			<li>
				<html:link page="<%= "/curricularCoursesEnrollment.do?method=prepareEnrollmentChooseStudent&amp;degreeCurricularPlanID=" + degreeCurricularPlanID + "&amp;executionDegreeId=" + executionDegreeID %>">
			    <bean:message key="link.student.enrollment" /></html:link>
			</li>
			<li>
				<html:link page="<%= "/weeklyWorkLoad.do?method=prepare&amp;page=0&degreeCurricularPlanID=" + degreeCurricularPlanID %>">
				    <bean:message key="link.weekly.work.load"/>
				</html:link>			
			</li>
		</ul>
		<br /><br />
	</logic:notEqual> 
	
	<%-- Start of Common Options --%>	
	<strong><bean:message key="label.coordinator.management"/></strong>
	<ul>
		<li>
			<html:link page="<%= "/executionCoursesInformation.do?method=prepareChoiceForCoordinator&degreeCurricularPlanID=" + degreeCurricularPlanID %>">
				<bean:message key="link.coordinator.executionCoursesInformation"/>
			</html:link>
		</li>
		
		<li>
			<html:link page="<%= "/teachersInformation.do?executionDegreeId=" + executionDegreeID + "&degreeCurricularPlanID=" + degreeCurricularPlanID %>">				
				<bean:message key="link.coordinator.teachersInformation"/>
			</html:link>
		</li>
		<li>
			<html:link page="<%="/degreeCurricularPlanManagement.do?method=showActiveCurricularCourses&amp;degreeCurricularPlanID=" + degreeCurricularPlanID %>">
				<bean:message key="link.coordinator.degreeCurricularPlan.management"/>
			</html:link> 
		</li>

		<li>
			<html:link page="<%= "/viewCoordinationTeam.do?method=chooseExecutionYear&degreeCurricularPlanID="+ 
			degreeCurricularPlanID.toString()  %>" >
				<bean:message key="link.coordinator.degreeCurricularPlan.coordinationTeam"/>
			</html:link> 
		</li>
	
		<li>
			<html:link page="<%= "/degreeSiteManagement.do?method=subMenu&amp;degreeCurricularPlanID=" + degreeCurricularPlanID.toString()%>">
				<bean:message key="link.coordinator.degreeSite.management"/>		
			</html:link> 
		</li>

       	<li>
        	<html:link page="<%= "/manageFinalDegreeWork.do?method=showChooseExecutionDegreeForm&amp;degreeCurricularPlanID=" + degreeCurricularPlanID.toString()%>">
	        	<bean:message key="link.coordinator.managefinalDegreeWorks" />
	        </html:link>
        </li>

       	<li>
        	<html:link page="<%= "/manageFinalDegreeWorks.do?method=prepare&amp;page=0&amp;degreeCurricularPlanID=" + degreeCurricularPlanID.toString()%>">
	        	<bean:message key="link.coordinator.managefinalDegreeWorks" />
	        </html:link>
        </li>
	</ul>
	
	<br /><br />
	<strong><bean:message key="label.coordinator.manageEvaluations" /></strong>
	<ul>
		<li>
			<html:link page="<%="/evaluation/evaluationsCalendar.faces?degreeCurricularPlanID=" + degreeCurricularPlanID.toString() %>">
	        	<bean:message key="link.calendar" />
	        </html:link>
		</li>
<!--
		<li>
			<html:link page="<%="/evaluation/showWrittenTestsForExecutionCourses.faces?degreeCurricularPlanID=" + degreeCurricularPlanID.toString() %>">
	        	<bean:message key="link.writtenTests" />
	        </html:link>
		</li>
		<li>
			<html:link page="<%="/evaluation/showProjectsForExecutionCourses.faces?degreeCurricularPlanID=" + degreeCurricularPlanID.toString() %>">
	        	<bean:message key="link.projects" />
	        </html:link>
		</li>
-->
	</ul>
	<br/>
</logic:present>
