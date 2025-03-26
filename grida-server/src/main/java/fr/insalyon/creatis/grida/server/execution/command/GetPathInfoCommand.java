package fr.insalyon.creatis.grida.server.execution.command;

import fr.insalyon.creatis.grida.common.Communication;
import fr.insalyon.creatis.grida.common.Constants;
import fr.insalyon.creatis.grida.common.bean.GridPathInfo;
import fr.insalyon.creatis.grida.server.business.BusinessException;
import fr.insalyon.creatis.grida.server.business.OperationBusiness;
import fr.insalyon.creatis.grida.server.execution.Command;

public class GetPathInfoCommand extends Command {
    private String[] paths;
    private OperationBusiness operationBusiness;

    public GetPathInfoCommand(Communication communication,
                                      String proxyFileName, String... paths) {
        super(communication, proxyFileName);
        this.paths = paths;
        operationBusiness = new OperationBusiness(proxyFileName);
    }

    @Override
    public void execute() {
        try {
            for (String pathName : paths) {
                GridPathInfo pathInfo = operationBusiness.getPathInfo(pathName);
                String message = pathInfo.exist() + Constants.MSG_SEP_2 + pathInfo.getType();
                communication.sendMessage(message);
            }
        } catch (BusinessException ex) {
            communication.sendErrorMessage(ex.getMessage());
        }
        communication.sendEndOfMessage();
    }
}
