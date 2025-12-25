package restaurant;

import com.saf.core.*;
import com.saf.messages.*;
import com.saf.spring.SAF;
import org.springframework.stereotype.Component;
import java.util.*;

@Component("JiraActor")
public class JiraActor implements Actor {
    private Map<String, TicketDTO> tickets = new HashMap<>();
    private int ticketCounter = 1000;

    @Override
    public void onReceive(Message msg, ActorContext ctx) throws Exception {

        // --- 1. CRÉATION DE TICKET  ---
        if (msg instanceof CreateTicketRequest req) {
            String ticketId = "JIRA-" + (++ticketCounter);

            String ticketTitle = req.getTicket().getTitle();
            String ticketDescription = req.getTicket().getDescription();
            TicketPriority ticketPriority = req.getTicket().getPriority();
            TicketStatus ticketStatus = TicketStatus.CREATED;

            // Sécurité pour le nom du créateur (évite le crash getName())
            String ticketCreatedByName = (ctx.sender() != null) ? ctx.sender().getName() : "SystemTest";

            TicketDTO ticket = new TicketDTO(ticketId, ticketTitle, ticketDescription, ticketPriority, ticketStatus, ticketCreatedByName);
            tickets.put(ticketId, ticket);

            System.out.println("[JiraActor] Ticket créé: " + ticketId + " - " + ticketTitle);
            System.out.println("[JiraActor] Demandeur: " + ticketCreatedByName);

            // >>> GESTION DU RÉPARATEUR <<<
            ActorSystem system = SAF.getContext().getBean(ActorSystem.class);
            if (system != null) {
                String workerName = "Reparateur-" + ticketId;

                // Utilisation de createActor validée par ton IDE
                ActorRef reparateur = system.createActor(ReparatorActor.class, workerName);

                if (reparateur != null) {
                    System.out.println("[JiraActor] > Un réparateur est assigné : " + workerName);

                    // Suppression immédiate après assignation
                    system.killActor(workerName);
                    System.out.println("[JiraActor] > Travail terminé. Le réparateur " + workerName + " a été supprimé.");
                }
            }

            // Réponse sécurisée au client
            if (ctx.sender() != null) {
                ctx.reply(new TicketResponse(ticket, "Ticket créé avec succès"));
            } else {
                System.out.println("[JiraActor] Test terminé (pas de réponse à envoyer).");
            }

            // --- 2. LISTER LES TICKETS  ---
        } else if (msg instanceof ListTicketsRequest req) {
            List<String> ticketList = new ArrayList<>();
            for (Map.Entry<String, TicketDTO> entry : tickets.entrySet()) {
                ticketList.add(entry.getKey() + " -> " + entry.getValue().getTitle());
            }

            String requester = (ctx.sender() != null) ? ctx.sender().getName() : "Inconnu";
            System.out.println("[JiraActor] Liste des tickets demandée par: " + requester);
            System.out.println("[JiraActor] Total tickets: " + tickets.size());

            ctx.reply(new ListTicketsResponse(ticketList, tickets.size()));

            // --- 3. SUPPRIMER UN TICKET ---
        } else if (msg instanceof DeleteTicketRequest req) {
            String ticketId = req.getTicketId();
            if (tickets.containsKey(ticketId)) {
                TicketDTO t = tickets.get(ticketId);
                t.setStatus(TicketStatus.DELETED);
                tickets.remove(ticketId);
                System.out.println("[JiraActor] Ticket supprimé: " + ticketId);
                ctx.reply(new TicketResponse(t, "Ticket supprimé avec succès"));
            } else {
                System.out.println("[JiraActor] Ticket non trouvé: " + ticketId);
                ctx.reply(new TicketResponse(null, "Ticket introuvable"));
            }
        }
    }
}