package anhembi.crud.service;

import anhembi.crud.conn.ConnectionFactory;
import anhembi.crud.domain.Producer;
import anhembi.crud.repository.ProducerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


class ProducerServiceTestIntegration {

    @Mock
    private ProducerRepository producerRepository;

    @Mock
    private Scanner scanner;

    @InjectMocks
    private ProducerService producerService;

    @BeforeEach
    void setUp() {
        producerRepository = mock(ProducerRepository.class);
        producerService = new ProducerService();
        producerService.SCANNER = mock(Scanner.class);
        producerService.producerRepository = producerRepository;
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void integrationTest_UpdateProducer_SuccessfulUpdate() {
        int existingProducerId = 1;
        when(producerService.SCANNER.nextLine())
                .thenReturn(String.valueOf(existingProducerId))
                .thenReturn("NewName");

        ProducerRepository producerRepositoryMock = mock(ProducerRepository.class);
        producerService.producerRepository = producerRepositoryMock;

        Producer existingProducer = Producer.builder()
                .id(existingProducerId)
                .name("ExistingProducer")
                .build();

        when(producerRepositoryMock.findById(existingProducerId)).thenReturn(Optional.of(existingProducer));

        producerService.update();

        verify(producerRepositoryMock, times(1)).update(any());
    }

    @Test
    void integrationTest_UpdateProducer_NotFound_PrintsErrorMessage() {

        int nonExistingProducerId = 100;
        when(producerService.SCANNER.nextLine()).thenReturn(String.valueOf(nonExistingProducerId));

        ProducerRepository producerRepositoryMock = mock(ProducerRepository.class);
        producerService.producerRepository = producerRepositoryMock;

        when(producerRepositoryMock.findById(nonExistingProducerId)).thenReturn(Optional.empty());

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        producerService.update();

        verify(producerRepositoryMock, never()).update(any());
        assertTrue(outContent.toString().contains("Producer not found"));

        System.setOut(System.out);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Test
    void integrationTest_FindProducerById_PrintsProducerInfo() {
        int existingProducerId = 1;
        when(producerService.SCANNER.nextLine()).thenReturn(String.valueOf(existingProducerId));

        ProducerRepository producerRepositoryMock = mock(ProducerRepository.class);
        producerService.producerRepository = producerRepositoryMock;

        Producer expectedProducer = Producer.builder().id(existingProducerId).name("Studio1").build();

        when(producerRepositoryMock.findById(existingProducerId)).thenReturn(Optional.of(expectedProducer));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        producerService.findByName();

        assertFalse(outContent.toString().contains("[1] - Studio1"));

        System.setOut(System.out);
    }

    @Test
    void integrationTest_FindProducerById_NotFound_PrintsErrorMessage() {
        int nonExistingProducerId = 100;
        when(producerService.SCANNER.nextLine()).thenReturn(String.valueOf(nonExistingProducerId));

        ProducerRepository producerRepositoryMock = mock(ProducerRepository.class);
        producerService.producerRepository = producerRepositoryMock;

        when(producerRepositoryMock.findById(nonExistingProducerId)).thenReturn(Optional.empty());

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        producerService.findByName();

        assertFalse(outContent.toString().contains("Producer not found"));

        System.setOut(System.out);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Test
    void integrationTest_DeleteProducer_NotFound_NoErrorMessage() {
        int nonExistingProducerId = 100;
        when(producerService.SCANNER.nextLine()).thenReturn(String.valueOf(nonExistingProducerId));

        ProducerRepository producerRepositoryMock = mock(ProducerRepository.class);
        producerService.producerRepository = producerRepositoryMock;

        when(producerRepositoryMock.findById(nonExistingProducerId)).thenReturn(Optional.empty());

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        producerService.delete();

        assertFalse(outContent.toString().contains("Producer not found"), "Unexpected error message found");

        System.setOut(System.out);
    }


    @Test
    void integrationTest_DeleteProducer_ConfirmDeletion() {
        int existingProducerId = 1;
        when(producerService.SCANNER.nextLine()).thenReturn(String.valueOf(existingProducerId), "S");

        ProducerRepository producerRepositoryMock = mock(ProducerRepository.class);
        producerService.producerRepository = producerRepositoryMock;

        producerService.delete();

        verify(producerRepositoryMock, times(1)).delete(existingProducerId);
    }


    //------------------------------------------------------------------------------------------------------------------

    @Test
    void integrationTest_SaveProducer_SuccessfulSave() {
        String producerName = "NewProducer";
        when(producerService.SCANNER.nextLine()).thenReturn(producerName);

        ProducerRepository producerRepositoryMock = mock(ProducerRepository.class);
        producerService.producerRepository = producerRepositoryMock;

        producerService.save();

        verify(producerRepositoryMock, times(1)).save(any(Producer.class));
    }

    @Test
    void integrationTest_SaveProducer_DuplicateName_NoErrorMessage() {
        String existingProducerName = "ExistingProducer";
        when(producerService.SCANNER.nextLine()).thenReturn(existingProducerName);

        ProducerRepository producerRepositoryMock = mock(ProducerRepository.class);
        producerService.producerRepository = producerRepositoryMock;

        when(producerRepositoryMock.findByName(existingProducerName)).thenReturn(List.of(Producer.builder().name(existingProducerName).build()));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        producerService.save();

        assertFalse(outContent.toString().contains("Producer with the same name already exists"));

        System.setOut(System.out);
    }

    //------------------------------------------------------------------------------------------------------------------


}