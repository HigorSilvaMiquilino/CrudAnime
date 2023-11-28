package anhembi.crud.repository;


import anhembi.crud.conn.ConnectionFactory;
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
public class ProducerRepository {
    ConnectionFactory connection = new ConnectionFactory();

    public ProducerRepository(ConnectionFactory connectionFactory) {
        this.connection = connectionFactory;
    }

    public  List<Producer> findByName(String name) {
        log.info("Finding producers by name '{}'", name);
        String sql = "select * FROM anime_store.producer where name like ?;";
        List<Producer> producers = new ArrayList<>();
        try (PreparedStatement ps = connection.getConnection().prepareStatement(sql)) {
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();


            while (rs.next()) {
                Producer producer = Producer
                        .builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
                        .build();
                producers.add(producer);
            }
        } catch (SQLException e) {
            log.error("Error while trying to find all producers", e);
        }
        return producers;
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------

    public void delete(int id) {
        String sql = "DELETE FROM `anime_store`.`producer` WHERE (`id` = ?);";
        try (PreparedStatement ps = connection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.execute();
            log.info("Deleted producer '{}' from the database,", id);
        } catch (SQLException e) {
            log.error("Error while trying to delete producer '{}'", id, e);
        }
    }



    //---------------------------------------------------------------------------------------------------------------------------------------------------------


    public  void save(Producer producer) {
        log.info("Saving producer '{}'", producer);
        String sql = "INSERT INTO `anime_store`.`producer`  (`name`) VALUES (?);";
        try (PreparedStatement ps = connection.getConnection().prepareStatement(sql)) {
            ps.setString(1, producer.getName());
            ps.execute();
        } catch (SQLException e) {
            log.error("Error while trying to update producer '{}'", producer.getId(), e);
        }
    }



    //---------------------------------------------------------------------------------------------------------------------------------------------------------

    public  Optional<Producer> findById(Integer id) {
        log.info("Finding producers by id '{}'", id);
        String sql = "select * FROM anime_store.producer where id = ?;";
        try (PreparedStatement ps = connection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return Optional.empty();
            return Optional.of(Producer
                    .builder()
                    .id(rs.getInt("id"))
                    .name(rs.getString("name"))
                    .build());
        } catch (SQLException e) {
            log.error("Error while trying to find all producers", e);
        }
        return Optional.empty();
    }


    //---------------------------------------------------------------------------------------------------------------------------------------------------------


    public  void update(Producer producer) {
        log.info("Updating producer '{}'", producer);
        String sql = "UPDATE `anime_store`.`producer` SET `name` = ? WHERE (`id` = ?);";
        try (PreparedStatement ps = connection.getConnection().prepareStatement(sql)) {
            ps.setString(1, producer.getName());
            ps.setInt(2, producer.getId());
            ps.execute();
        } catch (SQLException e) {
            log.error("Error while trying to update producer '{}'", producer.getId(), e);
        }
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------

}
