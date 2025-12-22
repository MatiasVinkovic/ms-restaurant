package restaurant;

import com.saf.core.*;
import com.saf.messages.OrderRequest;
import com.saf.spring.SAF;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"restaurant", "com.saf.spring"})
@EnableDiscoveryClient
public class RestaurantApp {
    public static void main(String[] args) {
        // Code minimum
        ActorSystem system = SAF.start(RestaurantApp.class, "ms-restaurant", false, args);
        DiscoveryClient dc = SAF.getContext().getBean(DiscoveryClient.class);

        System.out.println("Microservice Restaurant prêt et acteur 'tom' en ligne.");

        // Je créer 2 acteurs dans mon microservice, qui vont communiquer entre eux (communication local)
        ActorRef tom = system.createActor(RestaurantActor.class, "tom");
        ActorRef matias  = system.createActor(RestaurantActor.class, "matias");
        matias.tell(new OrderRequest("Pâtes Carbonara", "Léa"));



    }
}