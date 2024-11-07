package fr.insalyon.creatis.server.business;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import fr.insalyon.creatis.grida.server.business.BusinessException;
import fr.insalyon.creatis.grida.server.business.DiskspaceManager;
import fr.insalyon.creatis.grida.server.business.OperationBusiness;
import fr.insalyon.creatis.grida.server.operation.Operations;

public class OperationBusinessTest {

    private OperationBusiness business;

    @Mock
    private DiskspaceManager manager;

    @Mock
    private Operations operations;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        business = new OperationBusiness("", manager, operations);
    }

    @Test
    public void transferImpossibleLarge() throws Exception {
        when(operations.getDataSize("","")).thenReturn(101L);
        when(manager.getTotalSpace()).thenReturn(200L);
        when(manager.getFreeSpace()).thenReturn(100L);
        when(manager.getMinAvailableDiskSpace()).thenReturn(Double.valueOf(0.1));

        assertThrows(BusinessException.class, () -> business.isTransferPossible(""));
        verify(operations, times(1)).getDataSize("", "");
    }

    @Test
    public void transferImpossibleSmall() throws Exception {
        when(operations.getDataSize("","")).thenReturn(1L);
        when(manager.getTotalSpace()).thenReturn(200L);
        when(manager.getFreeSpace()).thenReturn(20L);
        when(manager.getMinAvailableDiskSpace()).thenReturn(Double.valueOf(0.1));

        assertThrows(BusinessException.class, () -> business.isTransferPossible(""));
        verify(operations, times(1)).getDataSize("", "");
    }

    @Test
    public void transferPossibleLarge() throws Exception {
        when(operations.getDataSize("","")).thenReturn(50L);

        when(manager.getTotalSpace()).thenReturn(200L);
        when(manager.getFreeSpace()).thenReturn(100L);
        when(manager.getMinAvailableDiskSpace()).thenReturn(Double.valueOf(0.1));

        assertDoesNotThrow(() -> business.isTransferPossible(""));
        verify(operations, times(1)).getDataSize("", "");
    }

    @Test
    public void transferPossibleSmall() throws Exception {
        when(operations.getDataSize("","")).thenReturn(1L);
        when(manager.getTotalSpace()).thenReturn(200L);
        when(manager.getFreeSpace()).thenReturn(102L);
        when(manager.getMinAvailableDiskSpace()).thenReturn(Double.valueOf(0.5));

        assertDoesNotThrow(() -> business.isTransferPossible(""));
        verify(operations, times(1)).getDataSize("", "");
    }
}
