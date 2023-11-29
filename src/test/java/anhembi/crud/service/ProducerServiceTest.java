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
        String inputName = "ExistingProducer";
        when(producerService.SCANNER.nextLine()).thenReturn(inputName);

        List<Producer> producers = Arrays.asList(
                Producer.builder().id(1).name(inputName).build(),
                Producer.builder().id(2).name(inputName).build()
        );
        when(producerRepository.findByName(inputName)).thenReturn(producers);

        producerService.findByName();

        verify(producerRepository).findByName(inputName);
    }

    @Test
    void findByName_WhenNameDoesNotExist_EmptyListReturned() {
        String inputName = "NonExistingProducer";
        when(producerService.SCANNER.nextLine()).thenReturn(inputName);

        when(producerRepository.findByName(inputName)).thenReturn(Collections.emptyList());

        producerService.findByName();

        verify(producerRepository).findByName(inputName);
    }

    @Test
    void findByName_WhenEmptyName_AllProducersReturned() {
        when(producerService.SCANNER.nextLine()).thenReturn("");

        List<Producer> producers = Arrays.asList(
                Producer.builder().id(1).name("Producer1").build(),
                Producer.builder().id(2).name("Producer2").build()
        );
        when(producerRepository.findByName("")).thenReturn(producers);

        producerService.findByName();

        verify(producerRepository).findByName("");
    }

    //---------------------------------------------------------------------------------------------------------------------------

    @Test
    void delete_DoesNotCallDelete_WhenUserDoesNotConfirm() {
        int producerId = 1;
        String input = producerId + "\nn\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        when(producerService.SCANNER.nextLine()).thenReturn(Integer.toString(producerId));

        producerService.delete();

        verify(producerRepository, never()).delete(producerId);

        System.setIn(System.in);
    }

    @Test
    void delete_DoesNotCallDelete_WhenProducerNotFound() {
        int producerId = 1;
        String input = producerId + "\ns\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        when(producerService.SCANNER.nextLine()).thenReturn(Integer.toString(producerId));

        when(producerRepository.findById(producerId)).thenReturn(Optional.empty());

        producerService.delete();

        verify(producerRepository, never()).delete(anyInt());

        System.setIn(System.in);
    }


    //---------------------------------------------------------------------------------------------------------------------------

    @Test
    void save_SavesProducerSuccessfully() {
        String producerName = "Test Producer";
        when(producerService.SCANNER.nextLine()).thenReturn(producerName);

        producerService.save();

        verify(producerRepository, times(1)).save(any(Producer.class));
    }

    @Test
    void save_SavesProducerWithValidNameAndCorrectValues() {
        String producerName = "ValidProducer";
        when(producerService.SCANNER.nextLine()).thenReturn(producerName);

        producerService.save();

        verify(producerRepository, times(1)).save(any(Producer.class));

        ArgumentCaptor<Producer> producerCaptor = ArgumentCaptor.forClass(Producer.class);
        verify(producerRepository).save(producerCaptor.capture());

        Producer capturedProducer = producerCaptor.getValue();

        assertEquals(producerName, capturedProducer.getName());
    }

    //----------------------------------------------------------------------------------------------------------------


    @Test
    void update_SuccessfulUpdate_CallsProducerRepositoryUpdate() {
        int existingProducerId = 1;
        when(producerService.SCANNER.nextLine())
                .thenReturn(String.valueOf(existingProducerId))
                .thenReturn("NewName");

        Producer existingProducer = Producer.builder()
                .id(existingProducerId)
                .name("ExistingProducer")
                .build();

        when(producerRepository.findById(existingProducerId)).thenReturn(Optional.of(existingProducer));

        producerService.update();

        verify(producerRepository, times(1)).update(any());
    }

    @Test
    void update_ProducerNotFound_PrintsErrorMessage() {
        int nonExistingProducerId = 100;
        when(producerService.SCANNER.nextLine()).thenReturn(String.valueOf(nonExistingProducerId));
        when(producerRepository.findById(nonExistingProducerId)).thenReturn(Optional.empty());

        producerService.update();

        verify(producerRepository, never()).update(any());
        verify(producerService.SCANNER, times(1)).nextLine();
        verify(producerService.SCANNER, times(1)).nextLine();
    }


    //----------------------------------------------------------------------------------------------------------------
}