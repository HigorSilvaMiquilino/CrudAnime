package anhembi.crud.repository;

import anhembi.crud.conn.ConnectionFactory;
import anhembi.crud.domain.Anime;
import anhembi.crud.domain.Producer;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.core.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Log4j2
class AnimeRepositoryTest {


    @Mock
    private ConnectionFactory connectionFactory;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private Logger logger;

    @InjectMocks
    private AnimeRepository animeRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findByName_ShouldReturnListOfAnimes_WhenNameExists() throws SQLException {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        when(connectionFactory.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getString("name")).thenReturn("Naruto");
        when(resultSet.getInt("episodes")).thenReturn(10);
        when(resultSet.getInt("producer_id")).thenReturn(1);
        when(resultSet.getString("producer_name")).thenReturn("Studio XYZ");

        // Act
        List<Anime> result = animeRepository.findByName("Naruto");

        // Assert
        assertEquals(1, result.size());
        Anime anime = result.get(0);
        assertEquals(1, anime.getId());
        assertEquals("Naruto", anime.getName());
        assertEquals(10, anime.getEpisodes());
        assertEquals(1, anime.getProducer().getId());
        assertEquals("Studio XYZ", anime.getProducer().getName());
    }

    @Test
    void findByName_ShouldReturnEmptyList_WhenNameDoesNotExist() throws SQLException {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        when(connectionFactory.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // Act
        List<Anime> result = animeRepository.findByName("NonexistentAnime");

        // Assert
        assertTrue(result.isEmpty());
    }

    //----------------------------------------------------------------------------------------------------------

    @Test
    void delete_ShouldDeleteAnime_WhenIdExists() throws SQLException {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);

        when(connectionFactory.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        // Act
        animeRepository.delete(1);

        // Assert
        verify(preparedStatement, times(1)).execute();
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------

    @Test
    void saveAnime_ShouldCallCorrectMethods() throws SQLException {
        // Criação de uma instância fictícia do Anime para o teste
        Anime anime = Anime.builder()
                .id(1)
                .name("Example Anime")
                .episodes(12)
                .producer(Producer.builder().id(1).name("Example Producer").build())
                .build();

        // Mock da ConnectionFactory usando Mockito
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);

        // Mock do PreparedStatement usando Mockito
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        // Criando uma instância de AnimeRepository usando a ConnectionFactory mockada
        AnimeRepository animeRepository = new AnimeRepository(connectionFactory);

        // Testando o método save
        animeRepository.save(anime);

        // Verificando se os métodos apropriados foram chamados com os parâmetros corretos
        verify(connectionFactory, times(1)).getConnection();
        verify(connection, times(1)).prepareStatement("INSERT INTO `anime_store`.`anime`  (`name`,`episodes`,`producer_id`) VALUES (?, ?, ?);");
        verify(preparedStatement, times(1)).setString(1, "Example Anime");
        verify(preparedStatement, times(1)).setInt(2, 12);
        verify(preparedStatement, times(1)).setInt(3, 1);
        verify(preparedStatement, times(1)).execute();
    }

    @Test
    void saveAnime_WithValidAnime_ShouldCallDatabaseMethodsWithValidParameters() throws SQLException {
        // Criação de uma instância fictícia do Anime para o teste
        Anime anime = Anime.builder()
                .id(1)
                .name("Example Anime")
                .episodes(12)
                .producer(Producer.builder().id(1).name("Example Producer").build())
                .build();

        // Mock da ConnectionFactory usando Mockito
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);

        // Mock do PreparedStatement usando Mockito
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        // Criando uma instância de AnimeRepository usando a ConnectionFactory mockada
        AnimeRepository animeRepository = new AnimeRepository(connectionFactory);

        // Testando o método save com um Anime válido
        animeRepository.save(anime);

        // Verificando se os métodos apropriados foram chamados com os parâmetros corretos
        verify(connectionFactory, times(1)).getConnection();
        verify(connection, times(1)).prepareStatement("INSERT INTO `anime_store`.`anime`  (`name`,`episodes`,`producer_id`) VALUES (?, ?, ?);");
        verify(preparedStatement, times(1)).setString(1, "Example Anime");
        verify(preparedStatement, times(1)).setInt(2, 12);
        verify(preparedStatement, times(1)).setInt(3, 1);  // ID do produtor definido como 1
        verify(preparedStatement, times(1)).execute();
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------

    @Test
    void findById_WhenAnimeExists_ShouldReturnOptionalWithAnime() throws SQLException {
        // Criação de um ID fictício para o teste
        Integer animeId = 1;

        // Criação de um ResultSet fictício com os dados do anime
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("id")).thenReturn(animeId);
        when(resultSet.getString("name")).thenReturn("Example Anime");
        when(resultSet.getInt("episodes")).thenReturn(12);
        when(resultSet.getInt("producer_id")).thenReturn(1);
        when(resultSet.getString("producer_name")).thenReturn("Example Producer");

        // Mock da ConnectionFactory usando Mockito
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);

        // Mock do PreparedStatement usando Mockito
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        // Mock do executeQuery para retornar o ResultSet fictício
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        // Criando uma instância de AnimeRepository usando a ConnectionFactory mockada
        AnimeRepository animeRepository = new AnimeRepository(connectionFactory);

        // Testando o método findById
        Optional<Anime> foundAnime = animeRepository.findById(animeId);

        // Verificando se o método apropriado foi chamado com o parâmetro correto
        verify(connectionFactory, times(1)).getConnection();
        verify(preparedStatement, times(1)).setInt(1, animeId);
        verify(preparedStatement, times(1)).executeQuery();

        // Verificando se o Optional contém o anime correto
        assertTrue(foundAnime.isPresent());
        assertEquals(animeId, foundAnime.get().getId());
        assertEquals("Example Anime", foundAnime.get().getName());
        assertEquals(12, foundAnime.get().getEpisodes());
        assertEquals(1, foundAnime.get().getProducer().getId());
        assertEquals("Example Producer", foundAnime.get().getProducer().getName());
    }

    @Test
    void findById_WhenAnimeDoesNotExist_ShouldReturnEmptyOptional() throws SQLException {
        // Criação de um ID fictício para o teste
        Integer animeId = 1;

        // Criação de um ResultSet fictício vazio
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false);

        // Mock da ConnectionFactory usando Mockito
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);

        // Mock do PreparedStatement usando Mockito
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        // Mock do executeQuery para retornar o ResultSet fictício vazio
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        // Criando uma instância de AnimeRepository usando a ConnectionFactory mockada
        AnimeRepository animeRepository = new AnimeRepository(connectionFactory);

        // Testando o método findById
        Optional<Anime> foundAnime = animeRepository.findById(animeId);

        // Verificando se o método apropriado foi chamado com o parâmetro correto
        verify(connectionFactory, times(1)).getConnection();
        verify(preparedStatement, times(1)).setInt(1, animeId);
        verify(preparedStatement, times(1)).executeQuery();

        // Verificando se o Optional está vazio
        assertTrue(foundAnime.isEmpty());
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------

    @Test
    void updateAnime_WithValidAnime_ShouldUpdateDatabase() throws SQLException {
        // Criação de uma instância fictícia do Anime para o teste
        Anime anime = Anime.builder()
                .id(1)
                .name("Updated Anime")
                .episodes(24)
                .producer(Producer.builder().id(1).name("Example Producer").build())
                .build();

        // Mock da ConnectionFactory usando Mockito
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);

        // Mock do PreparedStatement usando Mockito
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        // Criando uma instância de AnimeRepository usando a ConnectionFactory mockada
        AnimeRepository animeRepository = new AnimeRepository(connectionFactory);

        // Testando o método update
        animeRepository.update(anime);

        // Verificando se o método apropriado foi chamado com os parâmetros corretos
        verify(connectionFactory, times(1)).getConnection();
        verify(preparedStatement, times(1)).setString(1, "Updated Anime");
        verify(preparedStatement, times(1)).setInt(2, 24);
        verify(preparedStatement, times(1)).setInt(3, 1);  // ID do Anime definido como 1
        verify(preparedStatement, times(1)).execute();
    }

    @Test
    void updateAnime_WithValidAnime_ShouldNotThrowException() throws SQLException {
        // Criação de uma instância fictícia do Anime para o teste
        Anime anime = Anime.builder()
                .id(1)
                .name("Updated Anime")
                .episodes(24)
                .producer(Producer.builder().id(1).name("Example Producer").build())
                .build();

        // Mock da ConnectionFactory usando Mockito
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);

        // Mock do PreparedStatement usando Mockito
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        // Criando uma instância de AnimeRepository usando a ConnectionFactory mockada
        AnimeRepository animeRepository = new AnimeRepository(connectionFactory);

        // Testando o método update
        assertDoesNotThrow(() -> animeRepository.update(anime),
                "Updating Anime should not throw an exception");

        // Verificando se o método apropriado foi chamado com os parâmetros corretos
        verify(connectionFactory, times(1)).getConnection();
        verify(preparedStatement, times(1)).setString(1, "Updated Anime");
        verify(preparedStatement, times(1)).setInt(2, 24);
        verify(preparedStatement, times(1)).setInt(3, 1);  // ID do Anime definido como 1
        verify(preparedStatement, times(1)).execute();
    }
}