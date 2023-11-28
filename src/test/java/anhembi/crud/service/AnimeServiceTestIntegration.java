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
        // Arrange
        int existingAnimeId = 1;
        when(animeService.SCANNER.nextLine())
                .thenReturn(String.valueOf(existingAnimeId)) // ID válido
                .thenReturn("NewName")
                .thenReturn("15"); // Novo nome e número de episódios

        // Mock AnimeRepository
        AnimeRepository animeRepositoryMock = mock(AnimeRepository.class);
        animeService.animeRepository = animeRepositoryMock;

        Anime existingAnime = Anime.builder()
                .id(existingAnimeId)
                .name("ExistingAnime")
                .episodes(12)
                .producer(Producer.builder().id(1).name("Producer1").build())
                .build();

        when(animeRepositoryMock.findById(existingAnimeId)).thenReturn(Optional.of(existingAnime));

        // Act
        animeService.update();

        // Assert
        verify(animeRepositoryMock, times(1)).update(any()); // Verifica se o método update foi chamado uma vez
    }

    @Test
    void integrationTest_UpdateAnime_NotFound_PrintsErrorMessage() {
        // Arrange
        int nonExistingAnimeId = 100;
        when(animeService.SCANNER.nextLine()).thenReturn(String.valueOf(nonExistingAnimeId)); // ID inválido

        // Mock AnimeRepository
        AnimeRepository animeRepositoryMock = mock(AnimeRepository.class);
        animeService.animeRepository = animeRepositoryMock;

        when(animeRepositoryMock.findById(nonExistingAnimeId)).thenReturn(Optional.empty());

        // Act
        animeService.update();

        // Assert
        verify(animeRepositoryMock, never()).update(any());
        verify(animeService.SCANNER, times(1)).nextLine(); // Verifica se nextLine() foi chamado para ler o ID
        verify(animeService.SCANNER, times(1)).nextLine(); // Verifica se nextLine() foi chamado para exibir a mensagem de anime não encontrado
    }

    @Test
    void integrationTest_UpdateAnime_SuccessfulUpdate_CallsRepositoryUpdate() {
        // Arrange
        int existingAnimeId = 1;
        when(animeService.SCANNER.nextLine())
                .thenReturn(String.valueOf(existingAnimeId)) // ID válido
                .thenReturn("NewName")
                .thenReturn("15"); // Novo nome e número de episódios

        // Mock AnimeRepository
        AnimeRepository animeRepositoryMock = mock(AnimeRepository.class);
        animeService.animeRepository = animeRepositoryMock;

        Anime existingAnime = Anime.builder()
                .id(existingAnimeId)
                .name("ExistingAnime")
                .episodes(12)
                .producer(Producer.builder().id(1).name("Producer1").build())
                .build();

        when(animeRepositoryMock.findById(existingAnimeId)).thenReturn(Optional.of(existingAnime));

        // Act
        animeService.update();

        // Assert
        verify(animeRepositoryMock, times(1)).update(any()); // Verifica se o método update foi chamado uma vez
    }


    //-----------------------------------------------------------------------------------------------------------------

    @Test
    void integrationTest_DeleteAnime_CallsRepositoryDelete() {
        // Arrange
        int existingAnimeId = 1;
        when(animeService.SCANNER.nextLine())
                .thenReturn(String.valueOf(existingAnimeId)) // ID válido
                .thenReturn("s"); // Confirmação

        // Mock AnimeRepository
        AnimeRepository animeRepositoryMock = mock(AnimeRepository.class);
        animeService.animeRepository = animeRepositoryMock;

        // Act
        animeService.delete();

        // Assert
        verify(animeRepositoryMock, times(1)).delete(existingAnimeId); // Verifica se o método delete foi chamado uma vez
    }

    @Test
    void integrationTest_DeleteAnime_ConfirmationNo_DoesNotCallRepositoryDelete() {
        // Arrange
        int existingAnimeId = 1;
        when(animeService.SCANNER.nextLine())
                .thenReturn(String.valueOf(existingAnimeId)) // ID válido
                .thenReturn("n"); // Confirmação como "não"

        // Mock AnimeRepository
        AnimeRepository animeRepositoryMock = mock(AnimeRepository.class);
        animeService.animeRepository = animeRepositoryMock;

        // Act
        animeService.delete();

        // Assert
        verify(animeRepositoryMock, never()).delete(existingAnimeId); // Verifica se o método delete não foi chamado
    }



    //-----------------------------------------------------------------------------------------------------------------

    @Test
    void integrationTest_SaveAnime_CallsRepositorySave() {
        // Arrange
        when(animeService.SCANNER.nextLine())
                .thenReturn("NewAnime") // Novo nome
                .thenReturn("10") // Número de episódios
                .thenReturn("1"); // ID do produtor

        // Mock AnimeRepository
        AnimeRepository animeRepositoryMock = mock(AnimeRepository.class);
        animeService.animeRepository = animeRepositoryMock;

        // Act
        animeService.save();

        // Assert
        verify(animeRepositoryMock, times(1)).save(any()); // Verifica se o método save foi chamado uma vez
    }


    @Test
    void integrationTest_SaveAnime_ValidInput_SuccessfulSave() {
        // Arrange
        when(animeService.SCANNER.nextLine())
                .thenReturn("NewAnime") // Novo nome
                .thenReturn("10") // Número de episódios
                .thenReturn("1"); // ID do produtor válido

        // Mock AnimeRepository
        AnimeRepository animeRepositoryMock = mock(AnimeRepository.class);
        animeService.animeRepository = animeRepositoryMock;

        // Act
        animeService.save();

        // Assert
        verify(animeRepositoryMock, times(1)).save(any()); // Verifica se o método save foi chamado uma vez
    }

    //-----------------------------------------------------------------------------------------------------------------

    @Test
    void integrationTest_FindByNameWithEmptyName_PrintsAllAnimes() {
        // Arrange
        when(animeService.SCANNER.nextLine())
                .thenReturn(""); // Nome vazio

        // Mock AnimeRepository
        AnimeRepository animeRepositoryMock = mock(AnimeRepository.class);
        animeService.animeRepository = animeRepositoryMock;

        // Dados de teste
        List<Anime> expectedAnimes = new ArrayList<>();
        expectedAnimes.add(Anime.builder().id(1).name("Anime1").episodes(10).producer(Producer.builder().id(1).name("Studio1").build()).build());
        expectedAnimes.add(Anime.builder().id(2).name("Anime2").episodes(12).producer(Producer.builder().id(2).name("Studio2").build()).build());

        // Configuração do comportamento simulado do repositório
        when(animeRepositoryMock.findByName("")).thenReturn(expectedAnimes);

        // Mock System.out for capturing printed messages
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Act
        animeService.findByName();

        // Assert
        assertTrue(outContent.toString().contains("[1] - Anime1 10 Studio1"));
        assertTrue(outContent.toString().contains("[2] - Anime2 12 Studio2"));

        // Reset System.out
        System.setOut(System.out);
    }

    @Test
    void integrationTest_FindByNameWithValidName_PrintsMatchingAnimes() {
        // Arrange
        when(animeService.SCANNER.nextLine())
                .thenReturn("Anime"); // Nome existente

        // Mock AnimeRepository
        AnimeRepository animeRepositoryMock = mock(AnimeRepository.class);
        animeService.animeRepository = animeRepositoryMock;

        // Dados de teste
        List<Anime> expectedAnimes = new ArrayList<>();
        expectedAnimes.add(Anime.builder().id(1).name("Anime1").episodes(10).producer(Producer.builder().id(1).name("Studio1").build()).build());
        expectedAnimes.add(Anime.builder().id(2).name("Anime2").episodes(12).producer(Producer.builder().id(2).name("Studio2").build()).build());

        // Configuração do comportamento simulado do repositório
        when(animeRepositoryMock.findByName("Anime")).thenReturn(expectedAnimes);

        // Mock System.out for capturing printed messages
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Act
        animeService.findByName();

        // Assert
        assertTrue(outContent.toString().contains("[1] - Anime1 10 Studio1"));
        assertTrue(outContent.toString().contains("[2] - Anime2 12 Studio2"));

        // Reset System.out
        System.setOut(System.out);
    }


    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
}