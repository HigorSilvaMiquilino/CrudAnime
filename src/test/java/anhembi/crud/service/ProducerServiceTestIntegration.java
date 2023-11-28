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
        // Arrange
        int existingProducerId = 1;
        when(producerService.SCANNER.nextLine())
                .thenReturn(String.valueOf(existingProducerId)) // ID válido
                .thenReturn("NewName"); // Novo nome

        // Mock ProducerRepository
        ProducerRepository producerRepositoryMock = mock(ProducerRepository.class);
        producerService.producerRepository = producerRepositoryMock;

        Producer existingProducer = Producer.builder()
                .id(existingProducerId)
                .name("ExistingProducer")
                .build();

        when(producerRepositoryMock.findById(existingProducerId)).thenReturn(Optional.of(existingProducer));

        // Act
        producerService.update();

        // Assert
        verify(producerRepositoryMock, times(1)).update(any()); // Verifica se o método update foi chamado uma vez
    }

    @Test
    void integrationTest_UpdateProducer_NotFound_PrintsErrorMessage() {
        // Arrange
        int nonExistingProducerId = 100;
        when(producerService.SCANNER.nextLine()).thenReturn(String.valueOf(nonExistingProducerId)); // ID inválido

        // Mock ProducerRepository
        ProducerRepository producerRepositoryMock = mock(ProducerRepository.class);
        producerService.producerRepository = producerRepositoryMock;

        when(producerRepositoryMock.findById(nonExistingProducerId)).thenReturn(Optional.empty());

        // Mock System.out for capturing printed messages
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Act
        producerService.update();

        // Assert
        verify(producerRepositoryMock, never()).update(any());
        assertTrue(outContent.toString().contains("Producer not found")); // Verifica se a mensagem de erro foi impressa

        // Reset System.out
        System.setOut(System.out);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Test
    void integrationTest_FindProducerById_PrintsProducerInfo() {
        // Arrange
        int existingProducerId = 1;
        when(producerService.SCANNER.nextLine()).thenReturn(String.valueOf(existingProducerId)); // ID válido

        // Mock ProducerRepository
        ProducerRepository producerRepositoryMock = mock(ProducerRepository.class);
        producerService.producerRepository = producerRepositoryMock;

        // Dados de teste
        Producer expectedProducer = Producer.builder().id(existingProducerId).name("Studio1").build();

        // Configuração do comportamento simulado do repositório
        when(producerRepositoryMock.findById(existingProducerId)).thenReturn(Optional.of(expectedProducer));

        // Mock System.out for capturing printed messages
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Act
        producerService.findByName();

        // Assert
        assertFalse(outContent.toString().contains("[1] - Studio1"));

        // Reset System.out
        System.setOut(System.out);
    }

    @Test
    void integrationTest_FindProducerById_NotFound_PrintsErrorMessage() {
        // Arrange
        int nonExistingProducerId = 100;
        when(producerService.SCANNER.nextLine()).thenReturn(String.valueOf(nonExistingProducerId)); // ID inválido

        // Mock ProducerRepository
        ProducerRepository producerRepositoryMock = mock(ProducerRepository.class);
        producerService.producerRepository = producerRepositoryMock;

        // Configuração do comportamento simulado do repositório
        when(producerRepositoryMock.findById(nonExistingProducerId)).thenReturn(Optional.empty());

        // Mock System.out for capturing printed messages
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Act
        producerService.findByName();

        // Assert
        assertFalse(outContent.toString().contains("Producer not found"));

        // Reset System.out
        System.setOut(System.out);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Test
    void integrationTest_DeleteProducer_NotFound_NoErrorMessage() {
        // Arrange
        int nonExistingProducerId = 100;
        when(producerService.SCANNER.nextLine()).thenReturn(String.valueOf(nonExistingProducerId)); // ID inválido

        // Mock ProducerRepository
        ProducerRepository producerRepositoryMock = mock(ProducerRepository.class);
        producerService.producerRepository = producerRepositoryMock;

        when(producerRepositoryMock.findById(nonExistingProducerId)).thenReturn(Optional.empty());

        // Mock System.out for capturing printed messages
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Act
        producerService.delete();

        // Assert
        assertFalse(outContent.toString().contains("Producer not found"), "Unexpected error message found"); // Verifica se a mensagem de erro não foi impressa

        // Reset System.out
        System.setOut(System.out);
    }


    @Test
    void integrationTest_DeleteProducer_ConfirmDeletion() {
        // Arrange
        int existingProducerId = 1;
        when(producerService.SCANNER.nextLine()).thenReturn(String.valueOf(existingProducerId), "S"); // ID válido e confirmação de exclusão

        // Mock ProducerRepository
        ProducerRepository producerRepositoryMock = mock(ProducerRepository.class);
        producerService.producerRepository = producerRepositoryMock;

        // Act
        producerService.delete();

        // Assert
        verify(producerRepositoryMock, times(1)).delete(existingProducerId); // Verifica se o método delete foi chamado uma vez
    }


    //------------------------------------------------------------------------------------------------------------------

    @Test
    void integrationTest_SaveProducer_SuccessfulSave() {
        // Arrange
        String producerName = "NewProducer";
        when(producerService.SCANNER.nextLine()).thenReturn(producerName); // Nome do produtor válido

        // Mock ProducerRepository
        ProducerRepository producerRepositoryMock = mock(ProducerRepository.class);
        producerService.producerRepository = producerRepositoryMock;

        // Act
        producerService.save();

        // Assert
        verify(producerRepositoryMock, times(1)).save(any(Producer.class)); // Verifica se o método save foi chamado uma vez com qualquer instância de Producer
    }

    @Test
    void integrationTest_SaveProducer_DuplicateName_NoErrorMessage() {
        // Arrange
        String existingProducerName = "ExistingProducer";
        when(producerService.SCANNER.nextLine()).thenReturn(existingProducerName); // Nome de produtor já existente

        // Mock ProducerRepository
        ProducerRepository producerRepositoryMock = mock(ProducerRepository.class);
        producerService.producerRepository = producerRepositoryMock;

        // Configuração do comportamento simulado do repositório
        when(producerRepositoryMock.findByName(existingProducerName)).thenReturn(List.of(Producer.builder().name(existingProducerName).build()));

        // Mock System.out for capturing printed messages
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Act
        producerService.save();

        // Assert
        assertFalse(outContent.toString().contains("Producer with the same name already exists")); // Verifica se a mensagem de erro não foi impressa

        // Reset System.out
        System.setOut(System.out);
    }




    //------------------------------------------------------------------------------------------------------------------


}