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

package fr.insalyon.creatis.grida.server.execution.pool;

import fr.insalyon.creatis.grida.common.Communication;
import fr.insalyon.creatis.grida.common.bean.Operation.Type;
import fr.insalyon.creatis.grida.server.business.BusinessException;
import fr.insalyon.creatis.grida.server.business.PoolBusiness;
import fr.insalyon.creatis.grida.server.execution.Command;

/**
 *
 * @author Rafael Silva
 */
public class PoolAddOperationCommand extends Command {

    private String source;
    private String dest;
    private String operation;
    private String user;

    public PoolAddOperationCommand(Communication communication, String proxyFileName,
            String source, String dest, String operation, String user) {

        super(communication, proxyFileName);
        this.source = source;
        this.dest = dest;
        this.operation = operation;
        this.user = user;
    }

    @Override
    public void execute() {

        try {
            Type operationType = Type.valueOf(operation);
            PoolBusiness poolBusiness = new PoolBusiness();
            String id = poolBusiness.addOperation(proxyFileName, source, dest, 
                    operationType, user);
            
            communication.sendMessage(id);

        } catch (IllegalArgumentException ex) {
            communication.sendErrorMessage(ex.getMessage());
        } catch (BusinessException ex) {
            communication.sendErrorMessage(ex.getMessage());
        }
        communication.sendEndOfMessage();
    }
}
