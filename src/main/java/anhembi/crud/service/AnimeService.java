package anhembi.crud.service;

import anhembi.crud.conn.ConnectionFactory;
import anhembi.crud.domain.Anime;
import anhembi.crud.domain.Producer;
import anhembi.crud.repository.AnimeRepository;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;


public class AnimeService {
    public Scanner SCANNER = new Scanner(System.in);

    ConnectionFactory connectionFactory = new ConnectionFactory();
    AnimeRepository animeRepository = new AnimeRepository(connectionFactory);

    public  void menu(int op) {
        switch (op) {
            case 1 -> findByName();
            case 2 -> delete();
            case 3 -> save();
            case 4 -> update();
        }
    }

    public  void findByName() {
        System.out.println("Type the name or empty to all");
        String name = SCANNER.nextLine();
        List<Anime> animes = animeRepository.findByName(name);
        animes.forEach(p -> System.out.printf("[%d] - %s %d %s%n", p.getId(), p.getName(), p.getEpisodes(), p.getProducer().getName()));

    }


    //---------------------------------------------------------------------------------------------------------------------------------------------------------


    public  void delete() {
        System.out.println("Type the id of the anime you want to delete");
        int id = Integer.parseInt(SCANNER.nextLine());
        System.out.println("Are you sure? S/N");
        String choice = SCANNER.nextLine();
        if ("s".equalsIgnoreCase(choice)) {
            animeRepository.delete(id);
        }
    }


    //---------------------------------------------------------------------------------------------------------------------------------------------------------


    public  void save() {
        System.out.println("Type the name of the anime");
        String name = SCANNER.nextLine();
        System.out.println("Type the number of episodes");
        int episodes = Integer.parseInt(SCANNER.nextLine());
        System.out.println("Type the id of the producer");
        Integer producerId = Integer.parseInt(SCANNER.nextLine());
        Anime anime = Anime.builder()
                .episodes(episodes)
                .name(name)
                .producer(Producer.builder().id(producerId).build())
                .build();
        animeRepository.save(anime);
    }


    //---------------------------------------------------------------------------------------------------------------------------------------------------------

    public void update() {
        System.out.println("Type the id of the object you want to update");
        int id = Integer.parseInt(SCANNER.nextLine());
        Optional<Anime> animeOptional = animeRepository.findById(id);
        if (animeOptional.isEmpty()) {
            System.out.println("Anime not found");
            return;
        }
        Anime animeFromDB = animeOptional.get();
        System.out.println("Anime found: " + animeFromDB);
        System.out.println("Type the new name or enter to keep the same");
        String name = SCANNER.nextLine();
        name = name.isEmpty() ? animeFromDB.getName() : name;

        System.out.println("Type the new number of episodes");
        int episodes = Integer.parseInt(SCANNER.nextLine());

        Anime animeToUpdate = Anime.builder()
                .id(animeFromDB.getId())
                .episodes(episodes)
                .producer(animeFromDB.getProducer())
                .name(name).build();

        animeRepository.update(animeToUpdate);
    }

}