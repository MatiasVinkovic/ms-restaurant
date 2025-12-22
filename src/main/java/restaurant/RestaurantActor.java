package restaurant;

import com.saf.core.*;
import com.saf.messages.*; // Assure-toi d'avoir tes classes de messages ici
import org.springframework.stereotype.Component;

@Component
public class RestaurantActor implements Actor {


    @Override
    public void onReceive(Message msg, ActorContext ctx) throws Exception {
        if (msg instanceof OrderRequest req) {
            System.out.println("[RESTO] Reçu commande de : " + req.getDishName());

            // On simule une petite action de cuisine
            Thread.sleep(1000);

            // On répond à l'expéditeur via le contexte
            ctx.reply(new OrderResponse("PRÊT : " + req.getDishName() + " est sortie du four !"));
        }
    }
}
