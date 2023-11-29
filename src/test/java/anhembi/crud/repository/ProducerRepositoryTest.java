package anhembi.crud.repository;

import anhembi.crud.conn.ConnectionFactory;
import anhembi.crud.domain.Producer;
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

class ProducerRepositoryTest {

    @Mock
    private ConnectionFactory connectionFactory;

    @InjectMocks
    private ProducerRepository producerRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findProducersByName_WithValidName_ShouldReturnListOfProducers() throws SQLException {
        String producerName = "Example Producer";

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getString("name")).thenReturn(producerName);

        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);

        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        ProducerRepository producerRepository = new ProducerRepository(connectionFactory);

        List<Producer> foundProducers = producerRepository.findByName(producerName);

        verify(connectionFactory, times(1)).getConnection();
        verify(preparedStatement, times(1)).setString(1, "%" + producerName + "%");
        verify(preparedStatement, times(1)).executeQuery();

        assertEquals(1, foundProducers.size());
        Producer foundProducer = foundProducers.get(0);
        assertEquals(1, foundProducer.getId());
        assertEquals(producerName, foundProducer.getName());
    }

    @Test
    void findProducersByName_WithNonExistentName_ShouldReturnEmptyList() throws SQLException {
        String nonExistentName = "NonExistent Producer";

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false);

        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);

        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        ProducerRepository producerRepository = new ProducerRepository(connectionFactory);

        List<Producer> foundProducers = producerRepository.findByName(nonExistentName);

        verify(connectionFactory, times(1)).getConnection();
        verify(preparedStatement, times(1)).setString(1, "%" + nonExistentName + "%");
        verify(preparedStatement, times(1)).executeQuery();

        assertTrue(foundProducers.isEmpty());
    }

    //--------------------------------------------------------------------------------------------------------------------------

    @Test
    void deleteProducer_WithValidId_ShouldDeleteSuccessfully() throws SQLException {
        int producerId = 1;

        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);

        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        ProducerRepository producerRepository = new ProducerRepository(connectionFactory);

        producerRepository.delete(producerId);

        verify(connectionFactory, times(1)).getConnection();
        verify(preparedStatement, times(1)).setInt(1, producerId);
        verify(preparedStatement, times(1)).execute();
    }

    @Test
    void deleteProducer_WithNonexistentId_ShouldNotThrowException() throws SQLException {
        int nonExistentProducerId = 999;

        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);

        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        ProducerRepository producerRepository = new ProducerRepository(connectionFactory);

        producerRepository.delete(nonExistentProducerId);

        verify(connectionFactory, times(1)).getConnection();
        verify(preparedStatement, times(1)).setInt(1, nonExistentProducerId);
        verify(preparedStatement, times(1)).execute();
    }

    //--------------------------------------------------------------------------------------------------------------------------

    @Test
    void saveProducer_WithValidData_ShouldSaveSuccessfully() throws SQLException {
        Producer producer = Producer.builder()
                .id(1)
                .name("Example Producer")
                .build();

        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);

        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        ProducerRepository producerRepository = new ProducerRepository(connectionFactory);

        producerRepository.save(producer);

        verify(connectionFactory, times(1)).getConnection();
        verify(preparedStatement, times(1)).setString(1, producer.getName());
        verify(preparedStatement, times(1)).execute();
    }

    @Test
    void saveProducer_WithInvalidData_ShouldNotThrowException() throws SQLException {
        Producer producer = Producer.builder()
                .id(null)
                .name(null)
                .build();

        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);

        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        ProducerRepository producerRepository = new ProducerRepository(connectionFactory);

        assertDoesNotThrow(() -> producerRepository.save(producer),
                "Saving producer should not throw an exception");

        verify(connectionFactory, times(1)).getConnection();
        verify(preparedStatement, times(1)).setString(1, producer.getName());
        verify(preparedStatement, times(1)).execute();
    }

    //--------------------------------------------------------------------------------------------------------------------------


    @Test
    void findByIdProducer_WithValidId_ShouldReturnProducer() throws SQLException {
        int validProducerId = 1;

        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);

        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        ResultSet resultSet = mock(ResultSet.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("id")).thenReturn(validProducerId);
        when(resultSet.getString("name")).thenReturn("Example Producer");

        ProducerRepository producerRepository = new ProducerRepository(connectionFactory);

        Optional<Producer> foundProducer = producerRepository.findById(validProducerId);

        assertTrue(foundProducer.isPresent());
        assertEquals(validProducerId, foundProducer.get().getId());
        assertEquals("Example Producer", foundProducer.get().getName());
    }

    @Test
    void findByIdProducer_WithInvalidId_ShouldReturnEmptyOptional() throws SQLException {
        int invalidProducerId = 999;

        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);

        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        ResultSet resultSet = mock(ResultSet.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        ProducerRepository producerRepository = new ProducerRepository(connectionFactory);

        Optional<Producer> foundProducer = producerRepository.findById(invalidProducerId);

        assertTrue(foundProducer.isEmpty());
    }

    @Test
    void findByIdProducer_WithSQLException_ShouldReturnEmptyOptional() throws SQLException {
        int producerId = 1;

        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);

        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Simulated SQL Exception"));

        ProducerRepository producerRepository = new ProducerRepository(connectionFactory);

        assertDoesNotThrow(() -> {
            Optional<Producer> foundProducer = producerRepository.findById(producerId);
            assertTrue(foundProducer.isEmpty());
        });
    }

    //--------------------------------------------------------------------------------------------------------------------------


    @Test
    void updateProducer_SuccessfullyUpdated_ShouldNotThrowException() throws SQLException {
        Producer producer = Producer.builder().id(1).name("Updated Producer").build();

        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);

        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        ProducerRepository producerRepository = new ProducerRepository(connectionFactory);

        assertDoesNotThrow(() -> producerRepository.update(producer),
                "Updating producer should not throw an exception");
    }

    @Test
    void updateProducer_WithSQLExceptionOnClose_ShouldHandleException() throws SQLException {
        Producer producer = Producer.builder().id(1).name("Updated Producer").build();

        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);

        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        doThrow(new SQLException("Simulated Close Exception")).when(connection).close();

        ProducerRepository producerRepository = new ProducerRepository(connectionFactory);

        assertDoesNotThrow(() -> producerRepository.update(producer),
                "Updating producer should not throw an exception");
    }

    @Test
    void updateProducer_WithSQLExceptionOnExecute_ShouldHandleException() throws SQLException {
        Producer producer = Producer.builder().id(1).name("Updated Producer").build();

        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);

        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        when(preparedStatement.execute()).thenThrow(new SQLException("Simulated SQL Exception"));

        ProducerRepository producerRepository = new ProducerRepository(connectionFactory);

        assertDoesNotThrow(() -> producerRepository.update(producer),
                "Updating producer should not throw an exception");
    }
}