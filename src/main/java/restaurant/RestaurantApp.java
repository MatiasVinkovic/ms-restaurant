package restaurant;

import com.saf.core.*;
import com.saf.messages.OrderRequest;
import com.saf.messages.OrderResponse; // N'oublie pas de créer cette classe
import com.saf.spring.SAF;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"restaurant", "com.saf.spring"})
@EnableDiscoveryClient
public class RestaurantApp {
    public static void main(String[] args) {
        // 1. Démarrage du framework SAF (Spring + Engine d'acteurs) [cite: 7, 42]
        ActorSystem system = SAF.start(RestaurantApp.class, "ms-restaurant", false, args);

        // 2. Création de l'acteur "tom" [cite: 18, 27]
        // C'est lui qui recevra les messages du client
        system.createActor(RestaurantActor.class, "tom");

        System.out.println("Microservice Restaurant prêt et acteur 'tom' en ligne.");
    }
}