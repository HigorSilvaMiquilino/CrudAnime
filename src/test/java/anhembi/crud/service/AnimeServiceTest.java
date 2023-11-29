package anhembi.crud.service;

import anhembi.crud.domain.Anime;
import anhembi.crud.domain.Producer;
import anhembi.crud.repository.AnimeRepository;


import anhembi.crud.service.AnimeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.util.*;


import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

class AnimeServiceTest {

    @Mock
    private AnimeRepository animeRepository;

    @Mock
    private Scanner scanner;

    @InjectMocks
    private AnimeService animeService;

    @BeforeEach
    public void setUp() {
        animeRepository = mock(AnimeRepository.class);
        animeService = new AnimeService();
        animeService.SCANNER = mock(Scanner.class);
        animeService.animeRepository = animeRepository;
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void menu() {
    }

    @Test
    public void findByName_AnimesFound_PrintsAnimes() {

        when(animeService.SCANNER.nextLine()).thenReturn("SampleName");

        List<Anime> mockAnimes = Arrays.asList(
                Anime.builder().id(1).name("SampleAnime1").episodes(25).producer(Producer.builder().id(1).name("SampleProducer1").build()).build(),
                Anime.builder().id(2).name("SampleAnime2").episodes(30).producer(Producer.builder().id(2).name("SampleProducer2").build()).build()
        );
        when(animeRepository.findByName("SampleName")).thenReturn(mockAnimes);


        animeService.findByName();

        verify(animeRepository, times(1)).findByName("SampleName");
    }

    @Test
    public void findByName_NoAnimesFound_PrintsNoAnimes() {
        when(animeService.SCANNER.nextLine()).thenReturn("NonExistentName");
        when(animeRepository.findByName("NonExistentName")).thenReturn(Collections.emptyList());

        animeService.findByName();

        verify(animeRepository, times(1)).findByName("NonExistentName");
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------

    @Test
    public void delete_ConfirmationYes_DeletesAnime() {

        when(animeService.SCANNER.nextLine())
                .thenReturn("1")
                .thenReturn("s");

        animeService.delete();

        verify(animeRepository, times(1)).delete(1);
    }

    @Test
    public void delete_ConfirmationNo_DoesNotDeleteAnime() {
        when(animeService.SCANNER.nextLine())
                .thenReturn("1")
                .thenReturn("n");

        animeService.delete();

        verify(animeRepository, never()).delete(anyInt());
    }

    @Test
    public void delete_InvalidId_AnimeNotFound() {
        when(animeService.SCANNER.nextLine()).thenReturn("999");
        animeService.delete();
        verify(animeRepository, never()).delete(anyInt());
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------

    @Test
    public void save_ValidInput_CallsAnimeRepositorySave() {
        when(animeService.SCANNER.nextLine()).thenReturn("SampleName", "10", "1");
        animeService.save();
        verify(animeRepository, times(1)).save(any());
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------

    @Test
    void update_SuccessfulUpdate_CallsAnimeRepositoryUpdate() {
        int existingAnimeId = 1;
        when(animeService.SCANNER.nextLine())
                .thenReturn(String.valueOf(existingAnimeId))
                .thenReturn("NewName")
                .thenReturn("15");

        Anime existingAnime = Anime.builder()
                .id(existingAnimeId)
                .name("ExistingAnime")
                .episodes(12)
                .build();

        when(animeRepository.findById(existingAnimeId)).thenReturn(Optional.of(existingAnime));
        animeService.update();
        verify(animeRepository, times(1)).update(any());
    }

    @Test
    void update_AnimeNotFound_PrintsErrorMessage() {
        int nonExistingAnimeId = 100;
        when(animeService.SCANNER.nextLine()).thenReturn(String.valueOf(nonExistingAnimeId));
        when(animeRepository.findById(nonExistingAnimeId)).thenReturn(Optional.empty());

        animeService.update();

        verify(animeRepository, never()).update(any());
        verify(animeService.SCANNER, times(1)).nextLine();
        verify(animeService.SCANNER, times(1)).nextLine();
    }


    //---------------------------------------------------------------------------------------------------------------------------------------------------------

}
