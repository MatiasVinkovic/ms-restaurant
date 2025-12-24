package restaurant;

import com.saf.core.*;
import com.saf.messages.*;
import java.util.*;

public class JiraActor implements Actor {
    private Map<String, TicketDTO> tickets = new HashMap<>();
    private int ticketCounter = 1000;

    @Override
    public void onReceive(Message msg, ActorContext ctx) throws Exception {
        if (msg instanceof CreateTicketRequest req) {
            String ticketId = "JIRA-" + (++ticketCounter);

            String ticketTitle = req.getTicket().getTitle();
            String ticketDescription = req.getTicket().getDescription();
            TicketPriority ticketPriority = req.getTicket().getPriority();

            TicketStatus ticketStatus = TicketStatus.CREATED;
            String ticketCreatedByName = ctx.sender().getName();

            TicketDTO ticket = new TicketDTO(ticketId,ticketTitle,ticketDescription,ticketPriority,ticketStatus,ticketCreatedByName);
            tickets.put(ticketId,ticket);

            System.out.println("[JiraActor] Ticket créé: " + ticketId + " - " + ticketTitle);
            System.out.println("[JiraActor] Demandeur: " + ticketCreatedByName);

            // Répondre au client
            ctx.reply(new TicketResponse(ticket, "Ticket créé avec succès"));

       // } else if (msg instanceof ListTicketsRequest req) {
            // Lister tous les tickets
         //   List<String> ticketList = new ArrayList<>();
           // for (Map.Entry<String, String> entry : tickets.entrySet()) {
             //   ticketList.add(entry.getKey() + " -> " + entry.getValue());
            //}

            //System.out.println("[JiraActor] Liste des tickets demandée par: " + ctx.getSender().getName());
            //System.out.println("[JiraActor] Total tickets: " + tickets.size());

            //ctx.reply(new ListTicketsResponse(ticketList, tickets.size()));

        } else if (msg instanceof DeleteTicketRequest req) {
            // Supprimer un ticket
            String ticketId = req.getTicketId();
            if (tickets.containsKey(ticketId)) {
                TicketDTO t =  tickets.get(ticketId);
                t.setStatus(TicketStatus.DELETED);
                tickets.remove(ticketId);
                System.out.println("[JiraActor] Ticket supprimé: " + ticketId);
                ctx.reply(new TicketResponse(t, "Ticket supprimé avec succès"));
            } else {
                System.out.println("[JiraActor] Ticket non trouvé: " + ticketId);
                ctx.reply(new TicketResponse(null,"Ticket introuvable"));
            }
        }
    }
}
