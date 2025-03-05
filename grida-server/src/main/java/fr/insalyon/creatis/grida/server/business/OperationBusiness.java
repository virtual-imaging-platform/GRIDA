/* Copyright CNRS-CREATIS
 *
 * Rafael Ferreira da Silva
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

import fr.insalyon.creatis.devtools.zip.FolderZipper;
import fr.insalyon.creatis.grida.common.bean.GridData;
import fr.insalyon.creatis.grida.common.bean.GridPathInfo;
import fr.insalyon.creatis.grida.server.Configuration;
import fr.insalyon.creatis.grida.server.operation.Operations;
import fr.insalyon.creatis.grida.server.operation.OperationException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * @author Rafael Ferreira da Silva
 */
public class OperationBusiness {

    private static final Logger logger = Logger.getLogger(OperationBusiness.class);
    private String proxy;
    private Operations operations;
    private DiskspaceManager diskManager;

    public OperationBusiness(String proxy, DiskspaceManager manager, Operations operations) {
        this.proxy = proxy;
        this.diskManager = manager;
        this.operations = operations;
    }

    public OperationBusiness(String proxy) {
        this(proxy, new DiskspaceManager(), Configuration.getInstance().getOperations());
    }

    public void setDiskManager(DiskspaceManager manager) {
        this.diskManager = manager;
    }

    public long getModificationDate(String path) throws BusinessException {

        try {
            return operations.getModificationDate(proxy, path);
        } catch (OperationException ex) {
            throw new BusinessException(ex);
        }
    }

    public GridPathInfo getPathInfo(String path) throws BusinessException {
        try {
            return operations.getPathInfo(proxy, path);
        } catch (OperationException ex) {
            throw new BusinessException(ex);
        }
    }

    public List<GridData> listFilesAndFolders(String path)
        throws BusinessException {

        try {
            return operations.listFilesAndFolders(proxy, path);
        } catch (OperationException ex) {
            throw new BusinessException(ex);
        }
    }

    /**
     * Downloads a remote file to a local folder.
     *
     * @param operationID Operation identification
     * @param localDirPath Local folder path
     * @param fileName Remote file name
     * @param remoteFilePath Remote file path
     * @return Local file path
     * @throws Exception
     */
    public String downloadFile(String operationID, String localDirPath,
            String fileName, String remoteFilePath) throws BusinessException {
        try {
            if ( ! isDownloadPossible(localDirPath, fileName, remoteFilePath)) {
                throw new BusinessException("Download impossible, there is not enough space on the disk!");
            } else {
                new File(localDirPath).mkdirs();
                return operations.downloadFile(operationID, proxy, localDirPath, fileName, remoteFilePath);
            }
        } catch (OperationException ex) {
            throw new BusinessException(ex);
        }
    }

    /**
     * Downloads a remote folder to a local folder.
     *
     * @param operationID Operation identification
     * @param localDirPath
     * @param remoteDirPath
     * @return
     * @throws BusinessException
     */
    public String downloadFolder(String operationID, String localDirPath,
            String remoteDirPath, boolean zipResult) throws BusinessException {

        transferPossible(remoteDirPath);
        try {
            File localDir = new File(localDirPath);
            localDir.mkdirs();

            List<String> errorFiles = new ArrayList<String>();
            if (exist(remoteDirPath)) {
                for (GridData data : listFilesAndFolders(remoteDirPath)) {
                    try {
                        if (data.getType() == GridData.Type.Folder) {
                            downloadFolder(operationID, localDirPath + "/" + data.getName(),
                                    remoteDirPath + "/" + data.getName(), false);
                        } else {
                            downloadFile(operationID, localDirPath, data.getName(),
                                    remoteDirPath + "/" + data.getName());
                        }
                    } catch (BusinessException ex) {
                        errorFiles.add(remoteDirPath + "/" + data.getName());
                    }
                }
                if (!errorFiles.isEmpty()) {
                    createErrorFile(errorFiles, localDirPath);
                }

                if (zipResult) {
                    FolderZipper.zipFolder(localDir.getAbsolutePath(), localDir.getAbsolutePath() + ".zip");
                    FileUtils.deleteDirectory(new File(localDir.getAbsolutePath()));
                    return localDir.getAbsolutePath() + ".zip";

                } else {
                    return localDir.getAbsolutePath();
                }
            } else {
                String error = "Remote folder does not exist: " + remoteDirPath;
                logger.error(error);
                throw new BusinessException(error);
            }
        } catch (IOException ex) {
            logger.error(ex);
            throw new BusinessException(ex);
        }
    }

    /**
     * Downloads an array of files to a local folder and zips it.
     *
     * @param operationID Operation identification
     * @param localDirPath
     * @param remoteFilesPath
     * @param packName
     * @return
     * @throws BusinessException
     */
    public String downloadFiles(String operationID, String localDirPath,
            String[] remoteFilesPath, String packName) throws BusinessException {

        try {
            List<String> downloadedFiles = new ArrayList<String>();
            List<String> errorFiles = new ArrayList<String>();

            for (String remoteFilePath : remoteFilesPath) {
                try {
                    String fileName = new File(remoteFilePath).getName();
                    String destPath = downloadFile(operationID, localDirPath,
                            fileName, remoteFilePath);
                    downloadedFiles.add(destPath);

                } catch (BusinessException ex) {
                    errorFiles.add(remoteFilePath);
                }
            }

            if (!errorFiles.isEmpty()) {
                downloadedFiles.add(createErrorFile(errorFiles, localDirPath));
            }

            File file = new File(localDirPath);
            String zipName = file.getParent() + "/" + packName + ".zip";

            FolderZipper.zipListOfData(downloadedFiles, zipName);
            FileUtils.deleteDirectory(file);

            return zipName;

        } catch (IOException ex) {
            logger.error(ex);
            throw new BusinessException(ex);
        }
    }

    /**
     * Uploads a local file to a remote folder.
     *
     * @param operationID
     * @param localFilePath
     * @param remoteDir
     * @return
     * @throws BusinessException
     */
    public String uploadFile(String operationID, String localFilePath, String remoteDir) throws BusinessException {
        try {
            return operations.uploadFile(
                operationID, proxy, localFilePath, remoteDir);
        } catch (OperationException ex) {
            throw new BusinessException(ex);
        }
    }

    /**
     * Replicates a file to the list of preferred SEs.
     *
     * @param sourcePath
     * @throws BusinessException
     */
    public void replicateFile(String sourcePath) throws BusinessException {
        try {
            operations.replicateFile(proxy, sourcePath);
        } catch (OperationException ex) {
            throw new BusinessException(ex);
        }
    }

    /**
     * Deletes a file/directory.
     *
     * @param proxy
     * @param path
     * @throws Exception
     */
    public void delete(String path) throws BusinessException {
        try {
            if (operations.isDir(proxy, path)) {
                operations.deleteFolder(proxy, path);
            } else {
                operations.deleteFile(proxy, path);
            }
        } catch (OperationException ex) {
            throw new BusinessException(ex);
        }
    }

    /**
     * Creates a new remote folder.
     *
     * @param newFolder
     * @throws BusinessException
     */
    public void createFolder(String newFolder) throws BusinessException {
        transferPossible(null);
        try {
            operations.createFolder(proxy, newFolder);
        } catch (OperationException ex) {
            throw new BusinessException(ex);
        }
    }

    public void rename(String oldPath, String newPath) throws BusinessException {
        try {
            operations.rename(proxy, oldPath, newPath);
        } catch (OperationException ex) {
            throw new BusinessException(ex);
        }
    }

    public boolean exist(String path) throws BusinessException {
        try {
            logger.info("Verifying existence of '" + path + "'.");
            return operations.exists(proxy, path);
        } catch (OperationException ex) {
            throw new BusinessException(ex);
        }
    }

    public boolean isFolder(String path) throws BusinessException {
        try {
            return operations.isDir(proxy, path);
        } catch (OperationException ex) {
            throw new BusinessException(ex);
        }
    }

    /**
     * Gets the size of a file or directory.
     *
     * @param path
     * @return
     * @throws BusinessException
     */
    public long getDataSize(String path) throws BusinessException {
        try {
            return operations.getDataSize(proxy, path);
        } catch (OperationException ex) {
            throw new BusinessException(ex);
        }
    }


    public boolean isDownloadPossible(String localDirPath, String fileName, String remoteFilePath) throws BusinessException {
        return isDownloadPossible(localDirPath, fileName, remoteFilePath, null);
    }

    public boolean isDownloadPossible(String localDirPath, String fileName, String remoteFilePath, Double size) throws BusinessException {
        File localDir = new File(localDirPath);
        File destFile = new File(localDir.getAbsolutePath() + "/" + fileName);
        long remoteModificationDate = getModificationDate(remoteFilePath);

        if (destFile.exists() && remoteModificationDate <= destFile.lastModified()) {
            logger.info("Avoiding download: file \"" + destFile.getAbsolutePath()
                    + "\" is up to date.");
            return true;
        } else if (size != null) {
            return diskManager.isTransferable(size.longValue());
        } else {
            return isTransferPossible(remoteFilePath);
        }
    }

    public boolean isTransferPossible(String pathFile) throws BusinessException {
        long fileSize = pathFile != null ? getDataSize(pathFile) : 0;

        return diskManager.isTransferable(fileSize);
    }

    /**
     * This will check is there is enought of place on the grida server to transfer the file !
     * @param pathFile (can be null if just want to check if there is enought of place, ex: folder creation)
     * @throws BusinessException
     */
    public void transferPossible(String pathFile) throws BusinessException {
        if ( ! isTransferPossible(pathFile)) {
            if (pathFile == null) {
                logger.warn("GRIDA Action authorized but the disk limit is reached !");
            } else {
                throw new BusinessException("Unable to download " + pathFile + "' due to disk space limits!");
            }
        }
    }

    /**
     *
     * @param errorFiles
     * @param localDirPath
     * @return
     * @throws IOException
     */
    private String createErrorFile(List<String> errorFiles, String localDirPath)
            throws IOException {

        String errorFileName = localDirPath + "/README.txt";
        FileWriter fstream = new FileWriter(errorFileName);
        BufferedWriter out = new BufferedWriter(fstream);
        out.write("Unfortunately the following files couldn't be downloaded:\n\n");
        for (String errorFile : errorFiles) {
            out.write(errorFile + "\n");
        }
        out.close();
        return errorFileName;
    }

    public void setComment(String lfn, String comment) throws BusinessException {
        try {
            operations.setComment(proxy, lfn, comment);
        } catch(OperationException e) {
            throw new BusinessException(e);
        }
    }
}
