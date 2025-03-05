/* Copyright CNRS-CREATIS
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
package fr.insalyon.creatis.grida.server.operation;

import fr.insalyon.creatis.grida.common.bean.GridData;
import fr.insalyon.creatis.grida.common.bean.GridPathInfo;
import java.util.List;

public interface Operations {

    /**
     * @return the modification date as a timestamp in milliseconds
     */
    long getModificationDate(String proxy, String path)
        throws OperationException;

    /**
     * @return the path info (existence and folder or file type)
     */
    GridPathInfo getPathInfo(String proxy, String path) throws OperationException;

    /**
     * @return if path is a folder, the list of GridData about the files/folder
     * of this folder. If path is a file, it returns a list with a single
     * GridData about this file. If path does not exist, returns an empty list
     */
    List<GridData> listFilesAndFolders(String proxy, String path) throws OperationException;

    /**
     * Creates all the missing directories of localDirPath if necessary
     * Overrides the local file if one already exists
     * @return the local path of the downloaded file
     * @throws OperationException if the file does not exist or is a directory
     */
    String downloadFile(
        String operationID,
        String proxy,
        String localDirPath,
        String fileName,
        String remoteFilePath) throws OperationException;

    /** Upload the file to the first SE where it works. If it doesn't work on a
     *  SE, try the next one in the list that is configured.
     * Creates all the missing directories of remoteDir if necessary
     * Deletes the local file after the upload
     * @return the remote path of the uploaded file
     * @throws OperationException if a file or a folder with the same name
     * already exists on remoteDir, or if the local file does not exist or is a
     * directory
     */
    String uploadFile(
        String operationID,
        String proxy,
        String localFilePath,
        String remoteDir) throws OperationException;


    void replicateFile(String proxy, String sourcePath)
        throws OperationException;

    /**
     * @throws OperationException if the path does not exist
     */
    boolean isDir(String proxy, String path) throws OperationException;

    /**
     * Deletes folder and subfolders
     * @throws OperationException if the path does not exist or is a file
     */
    void deleteFolder(String proxy, String path) throws OperationException;

    /**
     * @throws OperationException if the path does not exist or is a folder
     */
    void deleteFile(String proxy, String path) throws OperationException;

    /**
     * creates all missing folders
     * do nothing if the folder already exists
     * @throws OperationException if path is a file
     */
    void createFolder(String proxy, String path) throws OperationException;

    /**
     * Creates all missing directories in newPath if necessary
     * @throws OperationException if a file or a folder already exists on
     * newPath, or if the oldPath does not exist or is a directory
     */
    void rename(String proxy, String oldPath, String newPath)
        throws OperationException;

    boolean exists(String proxy, String path) throws OperationException;

    /**
     * Gets the size of a file or a directory recursively.
     * @return OperationException if path does not exist
     */
    long getDataSize(String proxy, String path) throws OperationException;

    /* this is specific */
    @Deprecated
    void setComment(String proxy, String lfn, String comment)
        throws OperationException;
}
