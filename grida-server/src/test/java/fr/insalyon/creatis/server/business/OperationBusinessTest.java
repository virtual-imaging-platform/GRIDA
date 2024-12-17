package fr.insalyon.creatis.server.business;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import fr.insalyon.creatis.grida.server.business.BusinessException;
import fr.insalyon.creatis.grida.server.business.DiskspaceManager;
import fr.insalyon.creatis.grida.server.business.OperationBusiness;
import fr.insalyon.creatis.grida.server.operation.Operations;

public class OperationBusinessTest {

    private OperationBusiness business;

    @Spy
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
        doReturn(Double.valueOf(0.1)).when(manager).getMinAvailableDiskSpace();

        assertThrows(BusinessException.class, () -> business.transferPossible(""));
        verify(operations, times(1)).getDataSize("", "");
    }

    @Test
    public void transferImpossibleSmall() throws Exception {
        when(operations.getDataSize("","")).thenReturn(1L);
        when(manager.getTotalSpace()).thenReturn(200L);
        when(manager.getFreeSpace()).thenReturn(20L);
        doReturn(Double.valueOf(0.1)).when(manager).getMinAvailableDiskSpace();

        assertThrows(BusinessException.class, () -> business.transferPossible(""));
        verify(operations, times(1)).getDataSize("", "");
    }

    @Test
    public void transferPossibleLarge() throws Exception {
        when(operations.getDataSize("","")).thenReturn(50L);
        when(manager.getTotalSpace()).thenReturn(200L);
        when(manager.getFreeSpace()).thenReturn(100L);
        doReturn(Double.valueOf(0.1)).when(manager).getMinAvailableDiskSpace();

        assertDoesNotThrow(() -> business.transferPossible(""));
        verify(operations, times(1)).getDataSize("", "");
    }

    @Test
    public void transferPossibleSmall() throws Exception {
        when(operations.getDataSize("","")).thenReturn(1L);
        when(manager.getTotalSpace()).thenReturn(200L);
        when(manager.getFreeSpace()).thenReturn(102L);
        doReturn(Double.valueOf(0.5)).when(manager).getMinAvailableDiskSpace();

        assertDoesNotThrow(() -> business.transferPossible(""));
        verify(operations, times(1)).getDataSize("", "");
    }
}
