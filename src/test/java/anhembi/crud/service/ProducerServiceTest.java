package anhembi.crud.service;


import anhembi.crud.domain.Producer;
import anhembi.crud.repository.AnimeRepository;
import anhembi.crud.repository.ProducerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProducerServiceTest {


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
    void menu() {
    }

    //---------------------------------------------------------------------------------------------------------------------------

    @Test
    void findByName_WhenNameExists_CorrectListReturned() {
        // Arrange
        String inputName = "ExistingProducer";
        when(producerService.SCANNER.nextLine()).thenReturn(inputName);

        List<Producer> producers = Arrays.asList(
                Producer.builder().id(1).name(inputName).build(),
                Producer.builder().id(2).name(inputName).build()
        );
        when(producerRepository.findByName(inputName)).thenReturn(producers);

        // Act
        producerService.findByName();

        // Assert
        verify(producerRepository).findByName(inputName);
    }

    @Test
    void findByName_WhenNameDoesNotExist_EmptyListReturned() {
        // Arrange
        String inputName = "NonExistingProducer";
        when(producerService.SCANNER.nextLine()).thenReturn(inputName);

        when(producerRepository.findByName(inputName)).thenReturn(Collections.emptyList());

        // Act
        producerService.findByName();

        // Assert
        verify(producerRepository).findByName(inputName);
    }

    @Test
    void findByName_WhenEmptyName_AllProducersReturned() {
        // Arrange
        when(producerService.SCANNER.nextLine()).thenReturn("");

        List<Producer> producers = Arrays.asList(
                Producer.builder().id(1).name("Producer1").build(),
                Producer.builder().id(2).name("Producer2").build()
        );
        when(producerRepository.findByName("")).thenReturn(producers);

        // Act
        producerService.findByName();

        // Assert
        verify(producerRepository).findByName("");
    }

    //---------------------------------------------------------------------------------------------------------------------------

    @Test
    void delete_DoesNotCallDelete_WhenUserDoesNotConfirm() {
        // Arrange
        int producerId = 1;
        String input = producerId + "\nn\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Simule o comportamento do scanner
        when(producerService.SCANNER.nextLine()).thenReturn(Integer.toString(producerId));

        // Act
        producerService.delete();

        // Assert
        verify(producerRepository, never()).delete(producerId);

        // Reset System.in
        System.setIn(System.in);
    }

    @Test
    void delete_DoesNotCallDelete_WhenProducerNotFound() {
        // Arrange
        int producerId = 1;
        String input = producerId + "\ns\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Simule o comportamento do scanner
        when(producerService.SCANNER.nextLine()).thenReturn(Integer.toString(producerId));

        // Simule o comportamento do método findById do producerRepository
        when(producerRepository.findById(producerId)).thenReturn(Optional.empty());

        // Act
        producerService.delete();

        // Assert
        verify(producerRepository, never()).delete(anyInt());

        // Reset System.in
        System.setIn(System.in);
    }


    //---------------------------------------------------------------------------------------------------------------------------

    @Test
    void save_SavesProducerSuccessfully() {
        // Arrange
        String producerName = "Test Producer";
        when(producerService.SCANNER.nextLine()).thenReturn(producerName);

        // Act
        producerService.save();

        // Assert
        verify(producerRepository, times(1)).save(any(Producer.class));
    }

    @Test
    void save_SavesProducerWithValidNameAndCorrectValues() {
        // Arrange
        String producerName = "ValidProducer";
        when(producerService.SCANNER.nextLine()).thenReturn(producerName);

        // Act
        producerService.save();

        // Assert
        // Verifica se o método save() foi chamado exatamente uma vez com um objeto Producer válido
        verify(producerRepository, times(1)).save(any(Producer.class));

        // Captura o argumento passado para o método save()
        ArgumentCaptor<Producer> producerCaptor = ArgumentCaptor.forClass(Producer.class);
        verify(producerRepository).save(producerCaptor.capture());

        // Obtém o produtor capturado
        Producer capturedProducer = producerCaptor.getValue();

        // Verifica se o nome do produtor capturado é o mesmo fornecido como entrada
        assertEquals(producerName, capturedProducer.getName());
    }

    //----------------------------------------------------------------------------------------------------------------


    @Test
    void update_SuccessfulUpdate_CallsProducerRepositoryUpdate() {
        // Arrange
        int existingProducerId = 1;
        when(producerService.SCANNER.nextLine())
                .thenReturn(String.valueOf(existingProducerId)) // ID válido
                .thenReturn("NewName");

        Producer existingProducer = Producer.builder()
                .id(existingProducerId)
                .name("ExistingProducer")
                .build();

        when(producerRepository.findById(existingProducerId)).thenReturn(Optional.of(existingProducer));

        // Act
        producerService.update();

        // Assert
        verify(producerRepository, times(1)).update(any()); // Verifica se o método update foi chamado uma vez
    }

    @Test
    void update_ProducerNotFound_PrintsErrorMessage() {
        // Arrange
        int nonExistingProducerId = 100;
        when(producerService.SCANNER.nextLine()).thenReturn(String.valueOf(nonExistingProducerId)); // ID inválido
        when(producerRepository.findById(nonExistingProducerId)).thenReturn(Optional.empty());

        // Act
        producerService.update();

        // Assert
        verify(producerRepository, never()).update(any());
        verify(producerService.SCANNER, times(1)).nextLine(); // Verifica se nextLine() foi chamado para ler o ID
        verify(producerService.SCANNER, times(1)).nextLine(); // Verifica se nextLine() foi chamado para exibir a mensagem de produtor não encontrado
    }


    //----------------------------------------------------------------------------------------------------------------
}