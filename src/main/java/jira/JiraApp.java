package jira;

import com.saf.core.*;
import com.saf.spring.SAF;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"jira", "com.saf.spring"})
@EnableDiscoveryClient
public class JiraApp {
    public static void main(String[] args) {
        // Code minimum
        ActorSystem system = SAF.start(JiraApp.class, "ms-jira", false, args);
        DiscoveryClient dc = SAF.getContext().getBean(DiscoveryClient.class);

        System.out.println("========================================");
        System.out.println("Microservice JIRA démarre...");
        System.out.println("========================================");

        // Créer l'acteur JiraActor (centralisateur de tickets)
        ActorRef jira = system.createActor(JiraActor.class, "jira-manager");

        System.out.println("[INFO] Acteur JiraActor créé et prêt à recevoir des tickets!");


    }
}