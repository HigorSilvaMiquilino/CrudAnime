package anhembi.crud.conn;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;


class ConnectionFactoryTest {


    private ConnectionFactory connection = new ConnectionFactory();

    @BeforeEach
    public void setUp() throws SQLException {

    }

    @AfterEach
    public void tearDown() throws SQLException {
        if (connection != null && !connection.getConnection().isClosed()) {
            connection.getConnection().close();
        }
    }

    @Test
    @DisplayName("should obtain the connection")
    void testGetConnection() {
        Assertions.assertNotNull(connection);
    }

    @Test
    @DisplayName("should obtain the connection with correct properties")
    void testGetConnectionProperties() throws SQLException {
        Assertions.assertNotNull(connection);
        Assertions.assertEquals("jdbc:mysql://localhost:3306/anime_store", connection.getConnection().getMetaData().getURL());
        Assertions.assertTrue(connection.getConnection().getMetaData().getUserName().contains("root"));
    }

    @Test
    @DisplayName("should obtain a connection in the correct state")
    void testConnectionState() throws SQLException {
        Assertions.assertNotNull(connection);
        Assertions.assertTrue(connection.getConnection().getAutoCommit());
    }
}