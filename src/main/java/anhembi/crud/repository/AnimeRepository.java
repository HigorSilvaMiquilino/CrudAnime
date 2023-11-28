package anhembi.crud.repository;


import anhembi.crud.conn.ConnectionFactory;
import anhembi.crud.domain.Anime;
import anhembi.crud.domain.Producer;


import lombok.extern.log4j.Log4j2;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
public class AnimeRepository {


    ConnectionFactory connection = new ConnectionFactory();

    public AnimeRepository(ConnectionFactory connectionFactory) {
        this.connection = connectionFactory;
    }

    public  List<Anime> findByName(String name) {
        log.info("Finding Anime by name '{}'", name);
        String sql = """
                SELECT a.id, a.name, a.episodes, a.producer_id, p.name as 'producer_name'  FROM anime_store.anime a inner join
                anime_store.producer p on a.producer_id = p.id
                where a.name like ?;
                """;
        List<Anime> animes = new ArrayList<>();
        try (PreparedStatement ps = connection.getConnection().prepareStatement(sql)) {
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Producer producer = Producer.builder().name(rs.getString("producer_name")).id(rs.getInt("producer_id")).build();
                Anime anime = Anime
                        .builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
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

    //---------------------------------------------------------------------------------------------------------------------------------------------------------

    public  void delete(int id) {
        String sql = "DELETE FROM `anime_store`.`anime` WHERE (`id` = ?);";
        try (PreparedStatement ps = connection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.execute();
            log.info("Deleted anime '{}' from the database,", id);
        } catch (SQLException e) {
            log.error("Error while trying to delete anime '{}'", id, e);
        }
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------




    public void save(Anime Anime) {
        log.info("Saving Anime '{}'", Anime);
        String sql = "INSERT INTO `anime_store`.`anime`  (`name`,`episodes`,`producer_id`) VALUES (?, ?, ?);";
        try (PreparedStatement ps = connection.getConnection().prepareStatement(sql)) {
            ps.setString(1, Anime.getName());
            ps.setInt(2, Anime.getEpisodes());
            ps.setInt(3, Anime.getProducer().getId());
            ps.execute();
        } catch (SQLException e) {
            log.error("Error while trying to update anime '{}'", Anime.getId(), e);
        }
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------

    public  Optional<Anime> findById(Integer id) {
        log.info("Finding animes by id '{}'", id);

        String sql = """
                SELECT a.id, a.name, a.episodes, a.producer_id, p.name as 'producer_name'  FROM anime_store.anime a inner join
                anime_store.producer p on a.producer_id = p.id
                where a.id like ?
                """;

        try (PreparedStatement ps = connection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return Optional.empty();

            Producer producer = Producer.builder().name(rs.getString("producer_name")).id(rs.getInt("producer_id")).build();
            Anime anime = Anime
                    .builder()
                    .id(rs.getInt("id"))
                    .name(rs.getString("name"))
                    .episodes(rs.getInt("episodes"))
                    .producer(producer)
                    .build();
            return Optional.of(anime);
        } catch (SQLException e) {
            log.error("Error while trying to find all animes", e);
        }
        return Optional.empty();
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------

    public  void update(Anime Anime) {
        log.info("Updating Anime '{}'", Anime);
        String sql = "UPDATE `anime_store`.`anime` SET `name` = ?, `episodes` = ? WHERE (`id` = ?);";
        try (PreparedStatement ps = connection.getConnection().prepareStatement(sql)) {
            ps.setString(1, Anime.getName());
            ps.setInt(2, Anime.getEpisodes());
            ps.setInt(3, Anime.getId());
            ps.execute();
        } catch (SQLException e) {
            log.error("Error while trying to update Anime '{}'", Anime.getId(), e);
        }
    }


    //---------------------------------------------------------------------------------------------------------------------------------------------------------



}