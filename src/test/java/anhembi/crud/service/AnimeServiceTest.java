package anhembi.crud.service;

import anhembi.crud.domain.Anime;
import anhembi.crud.domain.Producer;
import anhembi.crud.repository.AnimeRepository;


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
        // Arrange
            when(animeService.SCANNER.nextLine()).thenReturn("SampleName");

        List<Anime> mockAnimes = Arrays.asList(
                Anime.builder().id(1).name("SampleAnime1").episodes(25).producer(Producer.builder().id(1).name("SampleProducer1").build()).build(),
                Anime.builder().id(2).name("SampleAnime2").episodes(30).producer(Producer.builder().id(2).name("SampleProducer2").build()).build()
        );
        when(animeRepository.findByName("SampleName")).thenReturn(mockAnimes);

        // Act
        animeService.findByName();

        // Assert (use appropriate assertions based on your testing framework)
        verify(animeRepository, times(1)).findByName("SampleName");
        // Add assertions to check if the expected output is printed
    }

    @Test
    public void findByName_NoAnimesFound_PrintsNoAnimes() {
        // Arrange
        when(animeService.SCANNER.nextLine()).thenReturn("NonExistentName");
        when(animeRepository.findByName("NonExistentName")).thenReturn(Collections.emptyList());

        // Act
        animeService.findByName();

        // Assert (use appropriate assertions based on your testing framework)
        verify(animeRepository, times(1)).findByName("NonExistentName");
        // Add assertions to check if the expected output for no animes found is printed
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------

    @Test
    public void delete_ConfirmationYes_DeletesAnime() {
        // Arrange
        when(animeService.SCANNER.nextLine())
                .thenReturn("1")   // ID do anime que você deseja excluir
                .thenReturn("s");// Confirmação de exclusão

        // Act
        animeService.delete();

        // Assert
        verify(animeRepository, times(1)).delete(1); // Verifica se o método delete foi chamado com o ID correto
    }

    @Test
    public void delete_ConfirmationNo_DoesNotDeleteAnime() {
        // Arrange
        when(animeService.SCANNER.nextLine())
                .thenReturn("1")   // ID do anime que você deseja excluir
                .thenReturn("n");

        // Act
        animeService.delete();

        // Assert
        verify(animeRepository, never()).delete(anyInt()); // Verifica se o método delete não foi chamado
    }

    @Test
    public void delete_InvalidId_AnimeNotFound() {
        // Arrange
        when(animeService.SCANNER.nextLine()).thenReturn("999"); // ID que não corresponde a nenhum anime existente

        // Act
        animeService.delete();

        // Assert
        verify(animeRepository, never()).delete(anyInt()); // Verifica se o método delete não foi chamado
        // Adicione assertivas para verificar se a mensagem "Anime not found" foi impressa
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------

    @Test
    public void save_ValidInput_CallsAnimeRepositorySave() {
        // Arrange
        when(animeService.SCANNER.nextLine()).thenReturn("SampleName", "10", "1"); // Entrada válida

        // Act
        animeService.save();

        // Assert
        verify(animeRepository, times(1)).save(any()); // Verifica se o método save foi chamado com quaisquer parâmetros
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------

    @Test
    void update_SuccessfulUpdate_CallsAnimeRepositoryUpdate() {
        // Arrange
        int existingAnimeId = 1;
        when(animeService.SCANNER.nextLine())
                .thenReturn(String.valueOf(existingAnimeId)) // ID válido
                .thenReturn("NewName")
                .thenReturn("15"); // Novo nome e número de episódios

        Anime existingAnime = Anime.builder()
                .id(existingAnimeId)
                .name("ExistingAnime")
                .episodes(12)
                .build();

        when(animeRepository.findById(existingAnimeId)).thenReturn(Optional.of(existingAnime));

        // Act
        animeService.update();

        // Assert
        verify(animeRepository, times(1)).update(any()); // Verifica se o método update foi chamado uma vez
    }

    @Test
    void update_AnimeNotFound_PrintsErrorMessage() {
        // Arrange
        int nonExistingAnimeId = 100;
        when(animeService.SCANNER.nextLine()).thenReturn(String.valueOf(nonExistingAnimeId)); // ID inválido
        when(animeRepository.findById(nonExistingAnimeId)).thenReturn(Optional.empty());

        // Act
        animeService.update();

        // Assert
        verify(animeRepository, never()).update(any());
        verify(animeService.SCANNER, times(1)).nextLine(); // Verifica se nextLine() foi chamado para ler o ID
        verify(animeService.SCANNER, times(1)).nextLine(); // Verifica se nextLine() foi chamado para exibir a mensagem de anime não encontrado
    }
    //---------------------------------------------------------------------------------------------------------------------------------------------------------

}
