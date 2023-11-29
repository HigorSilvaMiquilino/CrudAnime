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

        List<Anime> result = animeRepository.findByName("Naruto");

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
        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        when(connectionFactory.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        List<Anime> result = animeRepository.findByName("NonexistentAnime");

        assertTrue(result.isEmpty());
    }

    //----------------------------------------------------------------------------------------------------------

    @Test
    void delete_ShouldDeleteAnime_WhenIdExists() throws SQLException {
        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);

        when(connectionFactory.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        animeRepository.delete(1);

        verify(preparedStatement, times(1)).execute();
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------

    @Test
    void saveAnime_ShouldCallCorrectMethods() throws SQLException {
        Anime anime = Anime.builder()
                .id(1)
                .name("Example Anime")
                .episodes(12)
                .producer(Producer.builder().id(1).name("Example Producer").build())
                .build();

        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);

        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        AnimeRepository animeRepository = new AnimeRepository(connectionFactory);

        animeRepository.save(anime);

        verify(connectionFactory, times(1)).getConnection();
        verify(connection, times(1)).prepareStatement("INSERT INTO `anime_store`.`anime`  (`name`,`episodes`,`producer_id`) VALUES (?, ?, ?);");
        verify(preparedStatement, times(1)).setString(1, "Example Anime");
        verify(preparedStatement, times(1)).setInt(2, 12);
        verify(preparedStatement, times(1)).setInt(3, 1);
        verify(preparedStatement, times(1)).execute();
    }

    @Test
    void saveAnime_WithValidAnime_ShouldCallDatabaseMethodsWithValidParameters() throws SQLException {
        Anime anime = Anime.builder()
                .id(1)
                .name("Example Anime")
                .episodes(12)
                .producer(Producer.builder().id(1).name("Example Producer").build())
                .build();

        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);

        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        AnimeRepository animeRepository = new AnimeRepository(connectionFactory);

        animeRepository.save(anime);

        verify(connectionFactory, times(1)).getConnection();
        verify(connection, times(1)).prepareStatement("INSERT INTO `anime_store`.`anime`  (`name`,`episodes`,`producer_id`) VALUES (?, ?, ?);");
        verify(preparedStatement, times(1)).setString(1, "Example Anime");
        verify(preparedStatement, times(1)).setInt(2, 12);
        verify(preparedStatement, times(1)).setInt(3, 1);
        verify(preparedStatement, times(1)).execute();
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------

    @Test
    void findById_WhenAnimeExists_ShouldReturnOptionalWithAnime() throws SQLException {
        Integer animeId = 1;

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("id")).thenReturn(animeId);
        when(resultSet.getString("name")).thenReturn("Example Anime");
        when(resultSet.getInt("episodes")).thenReturn(12);
        when(resultSet.getInt("producer_id")).thenReturn(1);
        when(resultSet.getString("producer_name")).thenReturn("Example Producer");

        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);

        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        AnimeRepository animeRepository = new AnimeRepository(connectionFactory);

        Optional<Anime> foundAnime = animeRepository.findById(animeId);

        verify(connectionFactory, times(1)).getConnection();
        verify(preparedStatement, times(1)).setInt(1, animeId);
        verify(preparedStatement, times(1)).executeQuery();

        assertTrue(foundAnime.isPresent());
        assertEquals(animeId, foundAnime.get().getId());
        assertEquals("Example Anime", foundAnime.get().getName());
        assertEquals(12, foundAnime.get().getEpisodes());
        assertEquals(1, foundAnime.get().getProducer().getId());
        assertEquals("Example Producer", foundAnime.get().getProducer().getName());
    }

    @Test
    void findById_WhenAnimeDoesNotExist_ShouldReturnEmptyOptional() throws SQLException {
        Integer animeId = 1;

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false);

        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);

        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        AnimeRepository animeRepository = new AnimeRepository(connectionFactory);

        Optional<Anime> foundAnime = animeRepository.findById(animeId);

        verify(connectionFactory, times(1)).getConnection();
        verify(preparedStatement, times(1)).setInt(1, animeId);
        verify(preparedStatement, times(1)).executeQuery();

        assertTrue(foundAnime.isEmpty());
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------

    @Test
    void updateAnime_WithValidAnime_ShouldUpdateDatabase() throws SQLException {
        Anime anime = Anime.builder()
                .id(1)
                .name("Updated Anime")
                .episodes(24)
                .producer(Producer.builder().id(1).name("Example Producer").build())
                .build();

        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);

        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        AnimeRepository animeRepository = new AnimeRepository(connectionFactory);

        animeRepository.update(anime);

        verify(connectionFactory, times(1)).getConnection();
        verify(preparedStatement, times(1)).setString(1, "Updated Anime");
        verify(preparedStatement, times(1)).setInt(2, 24);
        verify(preparedStatement, times(1)).setInt(3, 1);
        verify(preparedStatement, times(1)).execute();
    }

    @Test
    void updateAnime_WithValidAnime_ShouldNotThrowException() throws SQLException {
        Anime anime = Anime.builder()
                .id(1)
                .name("Updated Anime")
                .episodes(24)
                .producer(Producer.builder().id(1).name("Example Producer").build())
                .build();

        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);

        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        AnimeRepository animeRepository = new AnimeRepository(connectionFactory);

        assertDoesNotThrow(() -> animeRepository.update(anime),
                "Updating Anime should not throw an exception");

        verify(connectionFactory, times(1)).getConnection();
        verify(preparedStatement, times(1)).setString(1, "Updated Anime");
        verify(preparedStatement, times(1)).setInt(2, 24);
        verify(preparedStatement, times(1)).setInt(3, 1);
        verify(preparedStatement, times(1)).execute();
    }
}