package fr.insalyon.creatis.grida.server.operation;

import fr.insalyon.creatis.grida.common.bean.GridData;
import fr.insalyon.creatis.grida.common.bean.GridData.Type;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class LocalOperations implements Operations {

    private final static Logger logger = Logger.getLogger(LocalOperations.class);

    @Override
    public long getModificationDate(String proxy, String path) throws OperationException {
        if ( ! exists(proxy, path)) {
            throw new OperationException("Path " + path + " does not exist");
        }
        try {
            BasicFileAttributes basicFileAttributes = Files.readAttributes(Paths.get(path), BasicFileAttributes.class);
            return basicFileAttributes.lastModifiedTime().toMillis();
        } catch (IOException e) {
            logger.error("Cannot get modification date of : " + path, e);
            throw new OperationException(e);
        }
    }

    @Override
    public List<GridData> listFilesAndFolders(String proxy, String path, boolean listComment) throws OperationException {
        if ( ! exists(proxy, path)) {
            return Collections.emptyList();
        }
        if ( Files.isDirectory(Paths.get(path))) {
            return listFilesFromFolder(path);
        } else {
            return Collections.singletonList(getGridData(Paths.get(path)));
        }
    }

    private List<GridData> listFilesFromFolder(String path) throws OperationException {
        try {
            List<GridData> gridDataList = new ArrayList<>();
            for (Path p : Files.list(Paths.get(path)).collect(Collectors.toList())) {
                gridDataList.add(getGridData(p));
            }
            return gridDataList;
        } catch (IOException e) {
            logger.error("Cannot list files from folder " + path, e);
            throw new OperationException(e);
        }
    }

    private GridData getGridData(Path path) throws OperationException {
        BasicFileAttributes fileAttributes = null;
        Set<PosixFilePermission> posixFilePermissions = null;
        try {
            fileAttributes = Files.readAttributes(path, BasicFileAttributes.class);
            posixFilePermissions = Files.getPosixFilePermissions(path);
        } catch (IOException e) {
            logger.error("Cannot get file attributes of : " + path, e);
            throw new OperationException(e);
        }
        DateTimeFormatter diracTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.from(ZoneOffset.UTC));

        String name = path.getFileName().toString();
        GridData.Type type = Files.isDirectory(path) ? Type.Folder : Type.File;
        long size = fileAttributes.size();
        String modificationDate =  diracTimeFormatter.format(fileAttributes.lastModifiedTime().toInstant());
        String comment = "";
        String permissions = PosixFilePermissions.toString(posixFilePermissions);
        String replicas = "-";
        return new GridData(name, type, size, modificationDate, replicas, permissions, comment);
    }

    @Override
    public String downloadFile(String operationID, String proxy, String localDirPath, String fileName, String remoteFilePath) throws OperationException {
        if ( ! exists(proxy, remoteFilePath)) {
            throw new OperationException("Cannot download " + remoteFilePath + ", file does not exist");
        }
        if ( Files.isDirectory(Paths.get(remoteFilePath))) {
            throw new OperationException("Cannot download " + remoteFilePath + ", it is a directory");
        }
        try {
            Files.createDirectories(Paths.get(localDirPath));

            Path newFile = Files.copy(
                    Paths.get(remoteFilePath),
                    Paths.get(localDirPath,fileName),
                    StandardCopyOption.REPLACE_EXISTING);
            Set<PosixFilePermission> perms = Files.getPosixFilePermissions(newFile);
            perms.addAll(Arrays.asList(
                    PosixFilePermission.GROUP_READ,
                    PosixFilePermission.OTHERS_READ));
            Files.setPosixFilePermissions(newFile, perms);
            return newFile.toString();
        } catch (IOException e) {
            logger.error("Cannot copy file to " + localDirPath, e);
            throw new OperationException(e);
        }
    }

    @Override
    public String uploadFile(String operationID, String proxy, String localFilePath, String remoteDir) throws OperationException {
        if ( ! exists(proxy, localFilePath)) {
            throw new OperationException("Cannot upload " + localFilePath + ", file does not exist");
        }
        if ( Files.isDirectory(Paths.get(localFilePath))) {
            throw new OperationException("Cannot upload " + localFilePath + ", it is a directory");
        }
        Path dest = Paths.get(remoteDir, Paths.get(localFilePath).getFileName().toString());
        if ( Files.exists(dest)) {
            throw new OperationException("Cannot upload to " + dest + ", file already exists");
        }
        try {
            Files.createDirectories(Paths.get(remoteDir));
            Files.copy(Paths.get(localFilePath), dest);
        } catch (IOException e) {
            logger.error("Cannot copy file to " + dest, e);
            throw new OperationException(e);
        }
        try {
            Files.delete(Paths.get(localFilePath));
        } catch (AccessDeniedException e) {
            logger.warn("cannot delete uploaded file " + localFilePath + " : access denied");
        } catch (IOException e) {
            logger.error("Cannot copy file to " + dest, e);
            throw new OperationException(e);
        }
        return dest.toString();
    }

    @Override
    public void replicateFile(String proxy, String sourcePath) throws OperationException {
        logger.info("Replicate " + sourcePath + " in local mode : do nothing");
    }

    @Override
    public boolean isDir(String proxy, String path) throws OperationException {
        if ( ! exists(proxy, path)) {
            throw new OperationException("Cannot find if " + path + " is a directory because it does not exist");
        }
        return Files.isDirectory(Paths.get(path));
    }

    @Override
    public void deleteFolder(String proxy, String path) throws OperationException {
        if ( ! exists(proxy, path)) {
            throw new OperationException("Cannot delete " + path + " because it does not exist");
        }
        if(Files.isRegularFile(Paths.get(path))) {
            throw new OperationException("Cannot delete folder" + path + " because it is a file");
        }
        try {
            Files.walk(Paths.get(path))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            throw new OperationException("Cannot delete directory " + path, e);
        }
    }

    @Override
    public void deleteFile(String proxy, String path) throws OperationException {
        if ( ! exists(proxy, path)) {
            throw new OperationException("Cannot delete " + path + " because it does not exist");
        }
        if(Files.isDirectory(Paths.get(path))) {
            throw new OperationException("Cannot delete file" + path + " because it is a directory");
        }
        try {
            Files.delete(Paths.get(path));
        } catch (IOException e) {
            throw new OperationException("Cannot delete file " + path, e);
        }
    }

    @Override
    public void createFolder(String proxy, String path) throws OperationException {
        if ( exists(proxy, path) && Files.isRegularFile(Paths.get(path))) {
            throw new OperationException("Cannot create folder" + path + " because it is a file");
        }
        try {
            Files.createDirectories(Paths.get(path));
        } catch (IOException e) {
            throw new OperationException("Cannot create folder " + path, e);
        }
    }

    @Override
    public void rename(String proxy, String oldPath, String newPath) throws OperationException {
        if ( ! exists(proxy, oldPath) || Files.isDirectory(Paths.get(oldPath))) {
            throw new OperationException("Cannot rename " + oldPath + " because it does not exist or is a directory");
        }
        if ( exists(proxy, newPath)) {
            throw new OperationException("Cannot rename to " + newPath + " because it already exists");
        }
        try {
            Files.createDirectories(Paths.get(newPath).getParent());
            Files.move(Paths.get(oldPath), Paths.get(newPath));
        } catch (IOException e) {
            throw new OperationException("Cannot rename " + oldPath + " to " + newPath, e);
        }
    }

    @Override
    public boolean exists(String proxy, String path) throws OperationException {
        return Files.exists(Paths.get(path));
    }

    @Override
    public long getDataSize(String proxy, String path) throws OperationException {
        if ( ! exists(proxy, path)) {
            throw new OperationException("Cannot get size of " + path + " because it does not exist");
        }
        if(Files.isDirectory(Paths.get(path))) {
            return FileUtils.sizeOfDirectory(Paths.get(path).toFile());
        } else {
            try {
                return Files.size(Paths.get(path));
            } catch (IOException e) {
                throw new OperationException("Cannot get size of " + path, e);
            }
        }
    }

    @Override
    public void setComment(String proxy, String lfn, String comment) throws OperationException {
        throw new OperationException("Setting comment not supported on grida local mode");
    }
}
