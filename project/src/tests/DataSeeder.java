package tests;

import filehandling.FileManager;
import repository.ComplaintRepository;
import utils.SampleDataGenerator;

public class DataSeeder {
    public static void main(String[] args) {
        FileManager.initializeDirectories();
        SampleDataGenerator.generateSampleDataIfEmpty();
        ComplaintRepository repo = ComplaintRepository.getInstance();
        repo.reloadFromFile();
        System.out.println("Data seeding complete! Total complaints: " + repo.count());
    }
}
