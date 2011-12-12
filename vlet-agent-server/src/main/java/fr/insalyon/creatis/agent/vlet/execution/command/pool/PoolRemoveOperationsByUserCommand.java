/* Copyright CNRS-CREATIS
 *
 * Rafael Silva
 * rafael.silva@creatis.insa-lyon.fr
 * http://www.rafaelsilva.com
 *
 * This software is a grid-enabled data-driven workflow manager and editor.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package fr.insalyon.creatis.agent.vlet.execution.command.pool;

import fr.insalyon.creatis.agent.vlet.Communication;
import fr.insalyon.creatis.agent.vlet.common.bean.Operation;
import fr.insalyon.creatis.agent.vlet.dao.DAOException;
import fr.insalyon.creatis.agent.vlet.dao.DAOFactory;
import fr.insalyon.creatis.agent.vlet.dao.PoolDAO;
import fr.insalyon.creatis.agent.vlet.execution.command.Command;
import java.io.File;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Rafael Silva
 */
public class PoolRemoveOperationsByUserCommand extends Command {

    private static final Logger logger = Logger.getLogger(PoolRemoveOperationsByUserCommand.class);
    private String user;

    public PoolRemoveOperationsByUserCommand(Communication communication, String proxyFileName, String user) {
        super(communication, proxyFileName);
        this.user = user;
    }

    @Override
    public void execute() {

        try {
            logger.info("Deleting pool operations for user '" + user + "'.");

            PoolDAO poolDAO = DAOFactory.getDAOFactory().getPoolDAO();
            List<Operation> operations = poolDAO.getOperationsByUser(user);
            poolDAO.removeOperationsByUser(user);

            for (Operation operation : operations) {
                if (operation.getStatus() == Operation.Status.Done) {

                    if (operation.getType() == Operation.Type.Download) {

                        String name = operation.getDest() + "/"
                                + new File(operation.getSource()).getName();
                        new File(name).delete();
                        poolDAO.removeOperationBySourceAndType(operation.getSource(),
                                Operation.Type.Download);

                    } else if (operation.getType() == Operation.Type.Download_Files) {

                        new File(operation.getDest()).delete();
                        poolDAO.removeOperationByDestAndType(operation.getDest(),
                                Operation.Type.Download_Files);
                    }
                }
            }
            communication.sendSucessMessage();

        } catch (DAOException ex) {
            logException(logger, ex);
            communication.sendErrorMessage(ex.getMessage());
        }
        communication.sendEndOfMessage();
    }
}
