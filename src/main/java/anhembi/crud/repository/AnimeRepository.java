package anhembi.crud.repository;


import anhembi.crud.conn.ConnectionFactory;
import anhembi.crud.domain.Anime;
import anhembi.crud.domain.Producer;


import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
public class AnimeRepository {

    public static List<Anime> findByName(String name) {
        log.info("Finding Anime by name '{}'", name);
        String sql = """
                SELECT a.id, a.nome, a.episodes, a.producer_id, p.name as 'producer_name'  FROM anime_store.anime a inner join
                anime_store.producer p on a.producer_id = p.id
                where a.nome like ?;
                """;
        List<Anime> animes = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createdPreparedStatementFindByName(conn, sql, name);
             ResultSet rs = ps.executeQuery()) {


            while (rs.next()) {
                Producer producer = Producer.builder().name(rs.getString("producer_name")).id(rs.getInt("producer_id")).build();
                Anime anime = Anime
                        .builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("nome"))
                        .episodes(rs.getInt("episodes"))
                        .producer(producer)
                        .build();
                animes.add(anime);
            }
        } catch (SQLException e) {
            log.error("Error while trying to find all animes", e);
        }
        return animes;
    }

    private static PreparedStatement createdPreparedStatementFindByName(Connection connection, String sql, String name) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, "%" + name + "%");
        return ps;
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------

    public static void delete(int id) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createdPreparedStatementDelete(conn, id)) {
            ps.execute();
            log.info("Deleted anime '{}' from the database,", id);
        } catch (SQLException e) {
            log.error("Error while trying to delete anime '{}'", id, e);
        }
    }

    private static PreparedStatement createdPreparedStatementDelete(Connection connection, Integer id) throws SQLException {
        String sql = "DELETE FROM `anime_store`.`anime` WHERE (`id` = ?);";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        return ps;
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------


    public static void save(Anime Anime) {
        log.info("Saving Anime '{}'", Anime);
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createPreparedStatementSave(conn, Anime)) {
            ps.execute();
        } catch (SQLException e) {
            log.error("Error while trying to update anime '{}'", Anime.getId(), e);
        }
    }

    private static PreparedStatement createPreparedStatementSave(Connection connection, Anime anime) throws SQLException {
        String sql = "INSERT INTO `anime_store`.`anime`  (`nome`,`episodes`,`producer_id`) VALUES (?, ?, ?);";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, anime.getName());
        ps.setInt(2, anime.getEpisodes());
        ps.setInt(3, anime.getProducer().getId());
        return ps;
    }


    //---------------------------------------------------------------------------------------------------------------------------------------------------------

    public static Optional<Anime> findById(Integer id) {
        log.info("Finding animes by id '{}'", id);
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createdPreparedStatementFindById(conn, id);
             ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) return Optional.empty();

            Producer producer = Producer.builder().name(rs.getString("producer_name")).id(rs.getInt("producer_id")).build();
            Anime anime = Anime
                    .builder()
                    .id(rs.getInt("id"))
                    .name(rs.getString("nome"))
                    .episodes(rs.getInt("episodes"))
                    .producer(producer)
                    .build();
            return Optional.of(anime);
        } catch (SQLException e) {
            log.error("Error while trying to find all animes", e);
        }
        return Optional.empty();
    }

    private static PreparedStatement createdPreparedStatementFindById(Connection connection, Integer id) throws SQLException {
        String sql = """
                SELECT a.id, a.nome, a.episodes, a.producer_id, p.name as 'producer_name'  FROM anime_store.anime a inner join
                anime_store.producer p on a.producer_id = p.id
                where a.id like ?
                """;
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        return ps;
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------


    public static void update(Anime Anime) {
        log.info("Updating Anime '{}'", Anime);
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createPreparedStatementUpdate(conn, Anime)) {
            ps.execute();
        } catch (SQLException e) {
            log.error("Error while trying to update Anime '{}'", Anime.getId(), e);
        }
    }

    private static PreparedStatement createPreparedStatementUpdate(Connection connection, Anime Anime) throws SQLException {
        String sql = "UPDATE `anime_store`.`anime` SET `nome` = ?, `episodes` = ? WHERE (`id` = ?);";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, Anime.getName());
        ps.setInt(2, Anime.getEpisodes());
        ps.setInt(3, Anime.getId());
        return ps;
    }


    //---------------------------------------------------------------------------------------------------------------------------------------------------------

}
