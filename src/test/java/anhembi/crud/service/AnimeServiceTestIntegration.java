package anhembi.crud.service;


import anhembi.crud.domain.Anime;
import anhembi.crud.domain.Producer;
import anhembi.crud.repository.AnimeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AnimeServiceTestIntegration {

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
    void integrationTest_UpdateAnime_CallsRepositoryUpdate() {
        int existingAnimeId = 1;
        when(animeService.SCANNER.nextLine())
                .thenReturn(String.valueOf(existingAnimeId))
                .thenReturn("NewName")
                .thenReturn("15");


        AnimeRepository animeRepositoryMock = mock(AnimeRepository.class);
        animeService.animeRepository = animeRepositoryMock;

        Anime existingAnime = Anime.builder()
                .id(existingAnimeId)
                .name("ExistingAnime")
                .episodes(12)
                .producer(Producer.builder().id(1).name("Producer1").build())
                .build();

        when(animeRepositoryMock.findById(existingAnimeId)).thenReturn(Optional.of(existingAnime));

        animeService.update();

        verify(animeRepositoryMock, times(1)).update(any());
    }

    @Test
    void integrationTest_UpdateAnime_NotFound_PrintsErrorMessage() {
        int nonExistingAnimeId = 100;
        when(animeService.SCANNER.nextLine()).thenReturn(String.valueOf(nonExistingAnimeId));

        AnimeRepository animeRepositoryMock = mock(AnimeRepository.class);
        animeService.animeRepository = animeRepositoryMock;

        when(animeRepositoryMock.findById(nonExistingAnimeId)).thenReturn(Optional.empty());

        animeService.update();

        verify(animeRepositoryMock, never()).update(any());
        verify(animeService.SCANNER, times(1)).nextLine();
        verify(animeService.SCANNER, times(1)).nextLine();
    }

    @Test
    void integrationTest_UpdateAnime_SuccessfulUpdate_CallsRepositoryUpdate() {
        int existingAnimeId = 1;
        when(animeService.SCANNER.nextLine())
                .thenReturn(String.valueOf(existingAnimeId))
                .thenReturn("NewName")
                .thenReturn("15");

        AnimeRepository animeRepositoryMock = mock(AnimeRepository.class);
        animeService.animeRepository = animeRepositoryMock;

        Anime existingAnime = Anime.builder()
                .id(existingAnimeId)
                .name("ExistingAnime")
                .episodes(12)
                .producer(Producer.builder().id(1).name("Producer1").build())
                .build();

        when(animeRepositoryMock.findById(existingAnimeId)).thenReturn(Optional.of(existingAnime));

        animeService.update();

        verify(animeRepositoryMock, times(1)).update(any());
    }


    //-----------------------------------------------------------------------------------------------------------------

    @Test
    void integrationTest_DeleteAnime_CallsRepositoryDelete() {
        int existingAnimeId = 1;
        when(animeService.SCANNER.nextLine())
                .thenReturn(String.valueOf(existingAnimeId))
                .thenReturn("s");

        AnimeRepository animeRepositoryMock = mock(AnimeRepository.class);
        animeService.animeRepository = animeRepositoryMock;

        animeService.delete();

        verify(animeRepositoryMock, times(1)).delete(existingAnimeId);
    }

    @Test
    void integrationTest_DeleteAnime_ConfirmationNo_DoesNotCallRepositoryDelete() {
        int existingAnimeId = 1;
        when(animeService.SCANNER.nextLine())
                .thenReturn(String.valueOf(existingAnimeId))
                .thenReturn("n");

        AnimeRepository animeRepositoryMock = mock(AnimeRepository.class);
        animeService.animeRepository = animeRepositoryMock;

        animeService.delete();

        verify(animeRepositoryMock, never()).delete(existingAnimeId);
    }


    //-----------------------------------------------------------------------------------------------------------------

    @Test
    void integrationTest_SaveAnime_CallsRepositorySave() {
        when(animeService.SCANNER.nextLine())
                .thenReturn("NewAnime")
                .thenReturn("10")
                .thenReturn("1");


        AnimeRepository animeRepositoryMock = mock(AnimeRepository.class);
        animeService.animeRepository = animeRepositoryMock;

        animeService.save();

        verify(animeRepositoryMock, times(1)).save(any());
    }


    @Test
    void integrationTest_SaveAnime_ValidInput_SuccessfulSave() {
        when(animeService.SCANNER.nextLine())
                .thenReturn("NewAnime")
                .thenReturn("10")
                .thenReturn("1");

        AnimeRepository animeRepositoryMock = mock(AnimeRepository.class);
        animeService.animeRepository = animeRepositoryMock;

        animeService.save();

        verify(animeRepositoryMock, times(1)).save(any());
    }

    //-----------------------------------------------------------------------------------------------------------------

    @Test
    void integrationTest_FindByNameWithEmptyName_PrintsAllAnimes() {
        when(animeService.SCANNER.nextLine())
                .thenReturn("");

        AnimeRepository animeRepositoryMock = mock(AnimeRepository.class);
        animeService.animeRepository = animeRepositoryMock;

        List<Anime> expectedAnimes = new ArrayList<>();
        expectedAnimes.add(Anime.builder().id(1).name("Anime1").episodes(10).producer(Producer.builder().id(1).name("Studio1").build()).build());
        expectedAnimes.add(Anime.builder().id(2).name("Anime2").episodes(12).producer(Producer.builder().id(2).name("Studio2").build()).build());

        when(animeRepositoryMock.findByName("")).thenReturn(expectedAnimes);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        animeService.findByName();

        assertTrue(outContent.toString().contains("[1] - Anime1 10 Studio1"));
        assertTrue(outContent.toString().contains("[2] - Anime2 12 Studio2"));

        System.setOut(System.out);
    }

    @Test
    void integrationTest_FindByNameWithValidName_PrintsMatchingAnimes() {
        when(animeService.SCANNER.nextLine())
                .thenReturn("Anime");

        AnimeRepository animeRepositoryMock = mock(AnimeRepository.class);
        animeService.animeRepository = animeRepositoryMock;

        List<Anime> expectedAnimes = new ArrayList<>();
        expectedAnimes.add(Anime.builder().id(1).name("Anime1").episodes(10).producer(Producer.builder().id(1).name("Studio1").build()).build());
        expectedAnimes.add(Anime.builder().id(2).name("Anime2").episodes(12).producer(Producer.builder().id(2).name("Studio2").build()).build());

        when(animeRepositoryMock.findByName("Anime")).thenReturn(expectedAnimes);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        animeService.findByName();

        assertTrue(outContent.toString().contains("[1] - Anime1 10 Studio1"));
        assertTrue(outContent.toString().contains("[2] - Anime2 12 Studio2"));

        System.setOut(System.out);
    }

    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
}