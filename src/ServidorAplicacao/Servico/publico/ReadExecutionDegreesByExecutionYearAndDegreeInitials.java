package ServidorAplicacao.Servico.publico;

/**
 * Servico LerSalas
 * 
 * @author tfc130
 * @version
 */

import DataBeans.InfoExecutionDegree;
import DataBeans.InfoExecutionYear;
import DataBeans.util.Cloner;
import Dominio.ICursoExecucao;
import Dominio.IExecutionYear;
import ServidorAplicacao.IServico;
import ServidorPersistente.ExcepcaoPersistencia;
import ServidorPersistente.ICursoExecucaoPersistente;
import ServidorPersistente.ISuportePersistente;
import ServidorPersistente.OJB.SuportePersistenteOJB;

public class ReadExecutionDegreesByExecutionYearAndDegreeInitials implements IServico
{

    private static ReadExecutionDegreesByExecutionYearAndDegreeInitials service =
        new ReadExecutionDegreesByExecutionYearAndDegreeInitials();
    /**
	 * The singleton access method of this class.
	 */
    public static ReadExecutionDegreesByExecutionYearAndDegreeInitials getService()
    {
        return service;
    }

    /**
	 * The actor of this class.
	 */
    private ReadExecutionDegreesByExecutionYearAndDegreeInitials()
    {
    }

    /**
	 * Devolve o nome do servico
	 */
    public final String getNome()
    {
        return "ReadExecutionDegreesByExecutionYearAndDegreeInitials";
    }

    public InfoExecutionDegree run(
        InfoExecutionYear infoExecutionYear,
        String degreeInitials,
        String nameDegreeCurricularPlan)
    {

        InfoExecutionDegree infoExecutionDegree = null;

        try
        {
            ISuportePersistente sp = SuportePersistenteOJB.getInstance();
            ICursoExecucaoPersistente executionDegreeDAO = sp.getICursoExecucaoPersistente();
            IExecutionYear executionYear =
                Cloner.copyInfoExecutionYear2IExecutionYear(infoExecutionYear);

            ICursoExecucao executionDegree =
                executionDegreeDAO.readByDegreeInitialsAndNameDegreeCurricularPlanAndExecutionYear(
                    degreeInitials,
                    nameDegreeCurricularPlan,
                    executionYear);
            if (executionDegree != null)
                infoExecutionDegree = Cloner.copyIExecutionDegree2InfoExecutionDegree(executionDegree);
        }
        catch (ExcepcaoPersistencia ex)
        {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        return infoExecutionDegree;
    }

}