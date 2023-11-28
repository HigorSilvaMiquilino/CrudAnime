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
        // Criação de um nome fictício para o teste
        String producerName = "Example Producer";

        // Criação de um ResultSet fictício
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getString("name")).thenReturn(producerName);

        // Mock da ConnectionFactory usando Mockito
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);

        // Mock do PreparedStatement usando Mockito
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        // Mock do executeQuery para retornar o ResultSet fictício
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        // Criando uma instância de ProducerRepository usando a ConnectionFactory mockada
        ProducerRepository producerRepository = new ProducerRepository(connectionFactory);

        // Testando o método findByName
        List<Producer> foundProducers = producerRepository.findByName(producerName);

        // Verificando se o método apropriado foi chamado com o parâmetro correto
        verify(connectionFactory, times(1)).getConnection();
        verify(preparedStatement, times(1)).setString(1, "%" + producerName + "%");
        verify(preparedStatement, times(1)).executeQuery();

        // Verificando se a lista de produtores contém os dados esperados
        assertEquals(1, foundProducers.size());
        Producer foundProducer = foundProducers.get(0);
        assertEquals(1, foundProducer.getId());
        assertEquals(producerName, foundProducer.getName());
    }

    @Test
    void findProducersByName_WithNonExistentName_ShouldReturnEmptyList() throws SQLException {
        // Criação de um nome fictício que não existe no banco de dados
        String nonExistentName = "NonExistent Producer";

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

        // Criando uma instância de ProducerRepository usando a ConnectionFactory mockada
        ProducerRepository producerRepository = new ProducerRepository(connectionFactory);

        // Testando o método findByName para um nome que não existe
        List<Producer> foundProducers = producerRepository.findByName(nonExistentName);

        // Verificando se o método apropriado foi chamado com o parâmetro correto
        verify(connectionFactory, times(1)).getConnection();
        verify(preparedStatement, times(1)).setString(1, "%" + nonExistentName + "%");
        verify(preparedStatement, times(1)).executeQuery();

        // Verificando se a lista de produtores está vazia
        assertTrue(foundProducers.isEmpty());
    }

    //--------------------------------------------------------------------------------------------------------------------------

    @Test
    void deleteProducer_WithValidId_ShouldDeleteSuccessfully() throws SQLException {
        // Criação de um ID fictício para o teste
        int producerId = 1;

        // Mock da ConnectionFactory usando Mockito
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);

        // Mock do PreparedStatement usando Mockito
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        // Criando uma instância de ProducerRepository usando a ConnectionFactory mockada
        ProducerRepository producerRepository = new ProducerRepository(connectionFactory);

        // Testando o método delete
        producerRepository.delete(producerId);

        // Verificando se o método apropriado foi chamado com o parâmetro correto
        verify(connectionFactory, times(1)).getConnection();
        verify(preparedStatement, times(1)).setInt(1, producerId);
        verify(preparedStatement, times(1)).execute();
        // Adicione mais verificações conforme necessário, dependendo do seu código real
    }

    @Test
    void deleteProducer_WithNonexistentId_ShouldNotThrowException() throws SQLException {
        // Criação de um ID fictício que não existe no banco de dados
        int nonExistentProducerId = 999;

        // Mock da ConnectionFactory usando Mockito
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);

        // Mock do PreparedStatement usando Mockito
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        // Criando uma instância de ProducerRepository usando a ConnectionFactory mockada
        ProducerRepository producerRepository = new ProducerRepository(connectionFactory);

        // Testando o método delete para um ID que não existe
        producerRepository.delete(nonExistentProducerId);

        // Verificando se o método apropriado foi chamado com o parâmetro correto
        verify(connectionFactory, times(1)).getConnection();
        verify(preparedStatement, times(1)).setInt(1, nonExistentProducerId);
        verify(preparedStatement, times(1)).execute();
        // Adicione mais verificações conforme necessário, dependendo do seu código real
    }

    //--------------------------------------------------------------------------------------------------------------------------

    @Test
    void saveProducer_WithValidData_ShouldSaveSuccessfully() throws SQLException {
        // Criação de uma instância fictícia de Producer para o teste
        Producer producer = Producer.builder()
                .id(1)
                .name("Example Producer")
                .build();

        // Mock da ConnectionFactory usando Mockito
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);

        // Mock do PreparedStatement usando Mockito
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        // Criando uma instância de ProducerRepository usando a ConnectionFactory mockada
        ProducerRepository producerRepository = new ProducerRepository(connectionFactory);

        // Testando o método save
        producerRepository.save(producer);

        // Verificando se o método apropriado foi chamado com os parâmetros corretos
        verify(connectionFactory, times(1)).getConnection();
        verify(preparedStatement, times(1)).setString(1, producer.getName());
        verify(preparedStatement, times(1)).execute();
        // Adicione mais verificações conforme necessário, dependendo do seu código real
    }

    @Test
    void saveProducer_WithInvalidData_ShouldNotThrowException() throws SQLException {
        // Criação de uma instância fictícia de Producer com dados inválidos para o teste
        Producer producer = Producer.builder()
                .id(null)  // ID inválido
                .name(null) // Nome inválido
                .build();

        // Mock da ConnectionFactory usando Mockito
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);

        // Mock do PreparedStatement usando Mockito
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        // Criando uma instância de ProducerRepository usando a ConnectionFactory mockada
        ProducerRepository producerRepository = new ProducerRepository(connectionFactory);

        // Testando o método save com dados inválidos
        assertDoesNotThrow(() -> producerRepository.save(producer),
                "Saving producer should not throw an exception");

        // Verificando se o método apropriado foi chamado com os parâmetros corretos
        verify(connectionFactory, times(1)).getConnection();
        verify(preparedStatement, times(1)).setString(1, producer.getName());
        verify(preparedStatement, times(1)).execute();
        // Adicione mais verificações conforme necessário, dependendo do seu código real
    }

    //--------------------------------------------------------------------------------------------------------------------------


    @Test
    void findByIdProducer_WithValidId_ShouldReturnProducer() throws SQLException {
        // Criação de um ID fictício para o teste
        int validProducerId = 1;

        // Mock da ConnectionFactory usando Mockito
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);

        // Mock do PreparedStatement usando Mockito
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        // Mock do ResultSet usando Mockito
        ResultSet resultSet = mock(ResultSet.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("id")).thenReturn(validProducerId);
        when(resultSet.getString("name")).thenReturn("Example Producer");

        // Criando uma instância de ProducerRepository usando a ConnectionFactory mockada
        ProducerRepository producerRepository = new ProducerRepository(connectionFactory);

        // Testando o método findById
        Optional<Producer> foundProducer = producerRepository.findById(validProducerId);

        // Verificando se o Producer foi encontrado corretamente
        assertTrue(foundProducer.isPresent());
        assertEquals(validProducerId, foundProducer.get().getId());
        assertEquals("Example Producer", foundProducer.get().getName());
    }

    @Test
    void findByIdProducer_WithInvalidId_ShouldReturnEmptyOptional() throws SQLException {
        // Criação de um ID fictício que não existe no banco de dados
        int invalidProducerId = 999;

        // Mock da ConnectionFactory usando Mockito
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);

        // Mock do PreparedStatement usando Mockito
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        // Mock do ResultSet usando Mockito
        ResultSet resultSet = mock(ResultSet.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // Criando uma instância de ProducerRepository usando a ConnectionFactory mockada
        ProducerRepository producerRepository = new ProducerRepository(connectionFactory);

        // Testando o método findById
        Optional<Producer> foundProducer = producerRepository.findById(invalidProducerId);

        // Verificando se o Optional está vazio
        assertTrue(foundProducer.isEmpty());
    }

    @Test
    void findByIdProducer_WithSQLException_ShouldReturnEmptyOptional() throws SQLException {
        // Criação de um ID fictício para o teste
        int producerId = 1;

        // Mock da ConnectionFactory usando Mockito
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);

        // Mock do PreparedStatement usando Mockito
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        // Simulando uma exceção SQLException durante a execução do SQL
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Simulated SQL Exception"));

        // Criando uma instância de ProducerRepository usando a ConnectionFactory mockada
        ProducerRepository producerRepository = new ProducerRepository(connectionFactory);

        // Testando o método findById e verificando se a exceção é tratada corretamente
        assertDoesNotThrow(() -> {
            Optional<Producer> foundProducer = producerRepository.findById(producerId);
            assertTrue(foundProducer.isEmpty());
        });
    }

    //--------------------------------------------------------------------------------------------------------------------------


    @Test
    void updateProducer_SuccessfullyUpdated_ShouldNotThrowException() throws SQLException {
        // Criação de um produtor fictício para o teste
        Producer producer = Producer.builder().id(1).name("Updated Producer").build();

        // Mock da ConnectionFactory usando Mockito
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);

        // Mock do PreparedStatement usando Mockito
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        // Criando uma instância de ProducerRepository usando a ConnectionFactory mockada
        ProducerRepository producerRepository = new ProducerRepository(connectionFactory);

        // Testando o método update sem lançar exceções
        assertDoesNotThrow(() -> producerRepository.update(producer),
                "Updating producer should not throw an exception");
    }

    @Test
    void updateProducer_WithSQLExceptionOnClose_ShouldHandleException() throws SQLException {
        // Criação de um produtor fictício para o teste
        Producer producer = Producer.builder().id(1).name("Updated Producer").build();

        // Mock da ConnectionFactory usando Mockito
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);

        // Mock do PreparedStatement usando Mockito
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        // Simulando uma exceção SQLException ao fechar a conexão
        doThrow(new SQLException("Simulated Close Exception")).when(connection).close();

        // Criando uma instância de ProducerRepository usando a ConnectionFactory mockada
        ProducerRepository producerRepository = new ProducerRepository(connectionFactory);

        // Testando o método update e verificando se a exceção ao fechar a conexão é tratada corretamente
        assertDoesNotThrow(() -> producerRepository.update(producer),
                "Updating producer should not throw an exception");
    }

    @Test
    void updateProducer_WithSQLExceptionOnExecute_ShouldHandleException() throws SQLException {
        // Criação de um produtor fictício para o teste
        Producer producer = Producer.builder().id(1).name("Updated Producer").build();

        // Mock da ConnectionFactory usando Mockito
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);

        // Mock do PreparedStatement usando Mockito
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        // Simulando uma exceção SQLException durante a execução do SQL
        when(preparedStatement.execute()).thenThrow(new SQLException("Simulated SQL Exception"));

        // Criando uma instância de ProducerRepository usando a ConnectionFactory mockada
        ProducerRepository producerRepository = new ProducerRepository(connectionFactory);

        // Testando o método update e verificando se a exceção durante a execução do SQL é tratada corretamente
        assertDoesNotThrow(() -> producerRepository.update(producer),
                "Updating producer should not throw an exception");
    }
}