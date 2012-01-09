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
package fr.insalyon.creatis.grida.server.business;

import fr.insalyon.creatis.grida.common.bean.Operation;
import fr.insalyon.creatis.grida.server.dao.DAOException;
import fr.insalyon.creatis.grida.server.dao.DAOFactory;
import fr.insalyon.creatis.grida.server.dao.PoolDAO;
import fr.insalyon.creatis.grida.server.execution.PoolDelete;
import fr.insalyon.creatis.grida.server.execution.PoolDownload;
import fr.insalyon.creatis.grida.server.execution.PoolReplicate;
import fr.insalyon.creatis.grida.server.execution.PoolUpload;
import java.io.File;
import java.util.Date;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author Rafael Silva
 */
public class PoolBusiness {

    private static final Logger logger = Logger.getLogger(PoolBusiness.class);
    private PoolDAO poolDAO;

    public PoolBusiness() {

        poolDAO = DAOFactory.getDAOFactory().getPoolDAO();
    }

    /**
     * 
     * @param proxyFileName
     * @param source
     * @param dest
     * @param operation
     * @param user
     * @return
     * @throws BusinessException 
     */
    public String addOperation(String proxyFileName, String source, String dest,
            String operation, String user) throws BusinessException {

        try {
            String id = operation + "-" + System.nanoTime();
            Operation op = new Operation(id, source, dest, operation, user, proxyFileName);
            poolDAO.addOperation(op);

            switch (op.getType()) {

                case Download:
                case Download_Files:
                    PoolDownload.getInstance();
                    break;
                case Upload:
                    PoolUpload.getInstance();
                    break;
                case Delete:
                    PoolDelete.getInstance();
                    break;
                case Replicate:
                    PoolReplicate.getInstance();
                    break;
            }

            return id;

        } catch (DAOException ex) {
            throw new BusinessException(ex);
        }
    }

    /**
     * 
     * @return
     * @throws BusinessException 
     */
    public List<Operation> getOperations() throws BusinessException {

        try {
            return poolDAO.getAllOperations();

        } catch (DAOException ex) {
            throw new BusinessException(ex);
        }
    }

    /**
     * 
     * @param id
     * @return
     * @throws BusinessException 
     */
    public Operation getOperationById(String id) throws BusinessException {

        try {
            return poolDAO.getOperationById(id);

        } catch (DAOException ex) {
            throw new BusinessException(ex);
        }
    }

    /**
     * 
     * @param user
     * @return
     * @throws BusinessException 
     */
    public List<Operation> getOperationsByUser(String user) throws BusinessException {

        try {
            return poolDAO.getOperationsByUser(user);

        } catch (DAOException ex) {
            throw new BusinessException(ex);
        }
    }

    /**
     * 
     * @param id
     * @throws BusinessException 
     */
    public void removeOperationById(String id) throws BusinessException {

        try {
            logger.info("Deleting pool operation '" + id + "'.");
            Operation operation = poolDAO.getOperationById(id);
            poolDAO.removeOperationById(id);

            if (operation.getStatus() == Operation.Status.Done) {

                if (operation.getType() == Operation.Type.Download) {

                    String name = operation.getDest() + "/"
                            + FilenameUtils.getName(operation.getSource());
                    FileUtils.deleteQuietly(new File(name));

                    poolDAO.removeOperationBySourceAndType(operation.getSource(),
                            Operation.Type.Download);

                } else if (operation.getType() == Operation.Type.Download_Files) {

                    FileUtils.deleteQuietly(new File(operation.getDest()));
                    poolDAO.removeOperationByDestAndType(operation.getDest(),
                            Operation.Type.Download_Files);
                }
            }
        } catch (DAOException ex) {
            throw new BusinessException(ex);
        }
    }

    /**
     * 
     * @param user
     * @throws BusinessException 
     */
    public void removeOperationsByUser(String user) throws BusinessException {

        try {
            logger.info("Deleting pool operations for user '" + user + "'.");

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
        } catch (DAOException ex) {
            throw new BusinessException(ex);
        }
    }

    /**
     * 
     * @param user
     * @param limit
     * @param startDate
     * @return
     * @throws BusinessException 
     */
    public List<Operation> getOperationsByLimitDateUser(String user, int limit,
            Date startDate) throws BusinessException {

        try {
            return poolDAO.getOperationsByLimitDateUser(user, limit, startDate);

        } catch (DAOException ex) {
            throw new BusinessException(ex);
        }
    }
}