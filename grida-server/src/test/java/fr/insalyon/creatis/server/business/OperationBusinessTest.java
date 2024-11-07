package fr.insalyon.creatis.server.business;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import fr.insalyon.creatis.grida.server.business.BusinessException;
import fr.insalyon.creatis.grida.server.business.DiskspaceManager;
import fr.insalyon.creatis.grida.server.business.OperationBusiness;

public class OperationBusinessTest {

    @Mock
    private DiskspaceManager manager;

    @Spy
    private OperationBusiness business;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        business.setDiskManager(manager);
    }

    @Test
    public void transferImpossibleLarge() throws BusinessException {
        doReturn(Long.valueOf(101)).when(business).getDataSize("");
        when(manager.getTotalSpace()).thenReturn(Long.valueOf(200));
        when(manager.getFreeSpace()).thenReturn(Long.valueOf(100));
        when(manager.getMinAvailableDiskSpace()).thenReturn(Double.valueOf(0.1));

        assertThrows(BusinessException.class, () -> business.isTransferPossible(""));
    }

    @Test
    public void transferImpossibleSmall() throws BusinessException {
        doReturn(Long.valueOf(1)).when(business).getDataSize("");
        when(manager.getTotalSpace()).thenReturn(Long.valueOf(200));
        when(manager.getFreeSpace()).thenReturn(Long.valueOf(20));
        when(manager.getMinAvailableDiskSpace()).thenReturn(Double.valueOf(0.1));

        assertThrows(BusinessException.class, () -> business.isTransferPossible(""));
    }

    @Test
    public void transferPossibleLarge() throws BusinessException {
        doReturn(Long.valueOf(50)).when(business).getDataSize("");
        when(manager.getTotalSpace()).thenReturn(Long.valueOf(200));
        when(manager.getFreeSpace()).thenReturn(Long.valueOf(100));
        when(manager.getMinAvailableDiskSpace()).thenReturn(Double.valueOf(0.1));

        assertDoesNotThrow(() -> business.isTransferPossible(""));
    }

    @Test
    public void transferPossibleSmall() throws BusinessException {
        doReturn(Long.valueOf(1)).when(business).getDataSize("");
        when(manager.getTotalSpace()).thenReturn(Long.valueOf(200));
        when(manager.getFreeSpace()).thenReturn(Long.valueOf(102));
        when(manager.getMinAvailableDiskSpace()).thenReturn(Double.valueOf(0.5));

        assertDoesNotThrow(() -> business.isTransferPossible(""));
    }
}
