package restaurant;

import com.saf.core.*;
import com.saf.messages.*;
import java.util.*;

public class JiraActor implements Actor {
    private Map<String, String> tickets = new HashMap<>();
    private int ticketCounter = 1000;

    @Override
    public void onReceive(Message msg, ActorContext ctx) throws Exception {
        if (msg instanceof CreateTicketRequest req) {
            // Créer un nouveau ticket
            String ticketId = "JIRA-" + (++ticketCounter);
            String ticketInfo = "Title: " + req.getTitle() + " | Desc: " + req.getDescription() + " | Priority: " + req.getPriority();
            tickets.put(ticketId, ticketInfo);

            System.out.println("[JiraActor] Ticket créé: " + ticketId + " - " + req.getTitle());
            System.out.println("[JiraActor] Demandeur: " + ctx.getSender().getName());

            // Répondre au client
            ctx.reply(new TicketResponse(ticketId, "CREATED", "Ticket créé avec succès"));

        } else if (msg instanceof ListTicketsRequest req) {
            // Lister tous les tickets
            List<String> ticketList = new ArrayList<>();
            for (Map.Entry<String, String> entry : tickets.entrySet()) {
                ticketList.add(entry.getKey() + " -> " + entry.getValue());
            }

            System.out.println("[JiraActor] Liste des tickets demandée par: " + ctx.getSender().getName());
            System.out.println("[JiraActor] Total tickets: " + tickets.size());

            ctx.reply(new ListTicketsResponse(ticketList, tickets.size()));

        } else if (msg instanceof DeleteTicketRequest req) {
            // Supprimer un ticket
            String ticketId = req.getTicketId();
            if (tickets.containsKey(ticketId)) {
                tickets.remove(ticketId);
                System.out.println("[JiraActor] Ticket supprimé: " + ticketId);
                ctx.reply(new TicketResponse(ticketId, "DELETED", "Ticket supprimé avec succès"));
            } else {
                System.out.println("[JiraActor] Ticket non trouvé: " + ticketId);
                ctx.reply(new TicketResponse(ticketId, "NOT_FOUND", "Ticket introuvable"));
            }
        }
    }
}
