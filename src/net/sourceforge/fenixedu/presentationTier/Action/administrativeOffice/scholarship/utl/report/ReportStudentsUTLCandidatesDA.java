package net.sourceforge.fenixedu.presentationTier.Action.administrativeOffice.scholarship.utl.report;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.fenixedu.presentationTier.Action.base.FenixDispatchAction;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.struts.annotations.Forward;
import pt.ist.fenixWebFramework.struts.annotations.Forwards;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;

@Mapping(path = "/reportStudentsUTLCandidates", module = "academicAdminOffice")
@Forwards({ @Forward(name = "prepare", path = "/academicAdminOffice/scholarship/utl/report/prepare.jsp"),
	@Forward(name = "showReport", path = "/academicAdminOffice/scholarship/utl/report/showReport.jsp"),
	@Forward(name = "viewDetails", path = "/academicAdminOffice/scholarship/utl/report/viewDetails.jsp") })
public class ReportStudentsUTLCandidatesDA extends FenixDispatchAction {

    public ActionForward prepare(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
	ReportStudentsUTLCandidatesBean bean = new ReportStudentsUTLCandidatesBean();
	request.setAttribute("bean", bean);

	return mapping.findForward("prepare");
    }

    public ActionForward showReport(final ActionMapping mapping, final ActionForm actionForm, final HttpServletRequest request,
	    final HttpServletResponse response) throws IOException {
	ReportStudentsUTLCandidatesBean bean = getRenderedObject("bean");

	POIFSFileSystem fs = new POIFSFileSystem(bean.getXlsFile());
	HSSFWorkbook wb = new HSSFWorkbook(fs);
	HSSFSheet sheet = wb.getSheetAt(0);

	if (sheet == null) {
	    addErrorMessage(request, "error", "error.academicAdminOffice.scholarship.utl.report.invalid.spreadsheet",
		    new String[0]);
	    return prepare(mapping, actionForm, request, response);
	}

	ReportStudentsUTLCandidates report = new ReportStudentsUTLCandidates(bean.getExecutionYear(), sheet);
	request.setAttribute("report", report);

	return mapping.findForward("showReport");
    }

    public ActionForward viewDetails(final ActionMapping mapping, final ActionForm actionForm, final HttpServletRequest request,
	    final HttpServletResponse response) {
	return mapping.findForward("viewDetails");
    }

    public ActionForward exportReport(final ActionMapping mapping, final ActionForm actionForm, final HttpServletRequest request,
	    HttpServletResponse response) throws IOException {
	ReportStudentsUTLCandidates report = getRenderedObject("report");

	HSSFWorkbook generateReport = report.generateReport();

	response.setContentType("application/vnd.ms-excel");
	response.setHeader("Content-Disposition", "attachment; filename=bolsa_accao_social_utl.xls");
	generateReport.write(response.getOutputStream());

	response.getOutputStream().flush();
	response.flushBuffer();

	return null;
    }
}