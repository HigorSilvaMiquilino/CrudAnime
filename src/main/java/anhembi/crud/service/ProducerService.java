package anhembi.crud.service;


import anhembi.crud.conn.ConnectionFactory;
import anhembi.crud.domain.Producer;
import anhembi.crud.repository.ProducerRepository;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;


public class ProducerService {
    private  final Scanner SCANNER = new Scanner(System.in);

    ConnectionFactory connectionFactory = new ConnectionFactory();
    ProducerRepository producerRepository = new ProducerRepository(connectionFactory);

    public  void menu(int op) {
        switch (op) {
            case 1 -> findByName();
            case 2 -> delete();
            case 3 -> save();
            case 4 -> update();
            default -> throw new IllegalArgumentException("Not a valid option");
        }
    }

    private  void findByName() {
        System.out.println("Type the name or empty to all");
        String name = SCANNER.nextLine();
        List<Producer> producers = producerRepository.findByName(name);
        producers.forEach(p -> System.out.printf("[%d] - %s%n", p.getId(), p.getName()));

    }


    //---------------------------------------------------------------------------------------------------------------------------------------------------------


    private  void delete() {
        System.out.println("Type the id of the producer you want to delete");
        System.out.println("Type one of the ids below to delete");
        int id = Integer.parseInt(SCANNER.nextLine());
        System.out.println("Are you sure? S/N");
        String choice = SCANNER.nextLine();
        if ("s".equalsIgnoreCase(choice)) {
            producerRepository.delete(id);
        }
    }


    //---------------------------------------------------------------------------------------------------------------------------------------------------------


    private  void save() {
        System.out.println("Type the name of the producer");
        String name = SCANNER.nextLine();
        Producer producer = Producer.builder().name(name).build();
        producerRepository.save(producer);
    }


    //---------------------------------------------------------------------------------------------------------------------------------------------------------

    private  void update() {
        System.out.println("Type the id of the object you want to update");
        int id = Integer.parseInt(SCANNER.nextLine());
        Optional<Producer> producerOptional = producerRepository.findById(id);
        if (producerOptional.isEmpty()) {
            System.out.println("Producer not found");
            return;
        }
        Producer producerFromDB = producerOptional.get();
        System.out.println("Producer found: " + producerFromDB);
        System.out.println("Type the new name or enter to keep the same");
        String name = SCANNER.nextLine();
        name = name.isEmpty() ? producerFromDB.getName() : name;
        Producer producerToUpdate = Producer.builder().id(producerFromDB.getId()).name(name).build();

        producerRepository.update(producerToUpdate);
    }

}
