package jira;

import com.saf.core.*;
import com.saf.messages.*;
import com.saf.spring.SAF;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * JiraActor - Gestionnaire central de tickets
 * 
 * DESIGN PATTERNS UTILISÉS:
 * - Factory Pattern: création contrôlée de réparateurs
 * - Observer Pattern: notification des changements de statut
 * - Observer Pattern: notification aux clients
 */
@Component("JiraActor")
public class JiraActor implements Actor {
    private Map<String, TicketDTO> tickets = new HashMap<>();
    private Map<String, String> ticketToReparator = new HashMap<>();
    private List<ActorRef> observers = new ArrayList<>();
    private int ticketCounter = 1000;
    private int reparatorIdCounter = 0;
    
    // Queue pour éviter ConcurrentModificationException
    private Queue<RepairCreationTask> pendingRepairs = new java.util.LinkedList<>();

    @Override
    public void onReceive(Message msg, ActorContext ctx) throws Exception {
        if (msg instanceof CreateTicketRequest req) {
            handleCreateTicket(req, ctx);
        } else if (msg instanceof ListTicketsRequest req) {
            handleListTickets(req, ctx);
        } else if (msg instanceof DeleteTicketRequest req) {
            handleDeleteTicket(req, ctx);
        }
        
        // Traiter les réparations en attente APRÈS le message
        processPendingRepairs(ctx);
    }
    
    /**
     * Classe interne pour stocker les demandes de création de réparateurs
     */
    private static class RepairCreationTask {
        String ticketId;
        TicketDTO ticket;
        RepairCategory category;
        String reparatorId;
        
        RepairCreationTask(String ticketId, TicketDTO ticket, RepairCategory category, String reparatorId) {
            this.ticketId = ticketId;
            this.ticket = ticket;
            this.category = category;
            this.reparatorId = reparatorId;
        }
    }

    /**
     * Factory Pattern: création contrôlée d'une réparation
     * NE PAS créer l'acteur ici - mettre en queue pour éviter ConcurrentModificationException
     */
    private void handleCreateTicket(CreateTicketRequest req, ActorContext ctx) throws Exception {
        String ticketId = "JIRA-" + (++ticketCounter);
        String clientName = (ctx.sender() != null) ? ctx.sender().getName() : "Client_Système";

        // Créer le ticket
        TicketDTO ticket = new TicketDTO(
            ticketId,
            req.getTicket().getTitle(),
            req.getTicket().getDescription(),
            req.getTicket().getPriority(),
            TicketStatus.CREATED,
            clientName
        );
        tickets.put(ticketId, ticket);

        printTicketCreated(ticketId, ticket, clientName);

        // Factory Pattern: déterminer la catégorie de réparation
        RepairCategory category = categorizeTicket(ticket);

        // Factory Pattern: créer un réparateur (METTRE EN QUEUE)
        String reparatorId = "REPARATEUR-" + (++reparatorIdCounter);
        
        // ⚠️ NE PAS créer directement ici! Ajouter à la queue
        pendingRepairs.add(new RepairCreationTask(ticketId, ticket, category, reparatorId));

        // Enregistrer le ticket
        ticketToReparator.put(ticketId, reparatorId);

        // Observer Pattern: notifier les observateurs
        notifyObservers(new TicketStatusUpdate(ticketId, TicketStatus.OPEN, "Ticket assigné à un réparateur", reparatorId));

        // Répondre au client
        if (ctx.sender() != null) {
            ctx.reply(new TicketResponse(ticket, "✅ Ticket créé et assigné: " + ticketId));
        }
    }
    
    /**
     * Traiter les réparations en attente APRÈS le cycle de messages
     */
    private void processPendingRepairs(ActorContext ctx) {
        while (!pendingRepairs.isEmpty()) {
            RepairCreationTask task = pendingRepairs.poll();
            try {
                createReparator(task.ticketId, task.ticket, task.category, task.reparatorId, ctx);
            } catch (Exception e) {
                System.err.println("❌ Erreur lors de la création du réparateur: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Factory Pattern: catégorisation du ticket
     */
    private RepairCategory categorizeTicket(TicketDTO ticket) {
        String description = ticket.getDescription().toLowerCase();
        
        if (description.contains("bug") || description.contains("erreur") || description.contains("crash")) {
            return RepairCategory.SOFTWARE;
        } else if (description.contains("performance") || description.contains("lent")) {
            return RepairCategory.PERFORMANCE;
        } else if (description.contains("réseau") || description.contains("connexion")) {
            return RepairCategory.NETWORK;
        } else if (description.contains("sécurité") || description.contains("vuln")) {
            return RepairCategory.SECURITY;
        } else if (description.contains("base de données") || description.contains("données")) {
            return RepairCategory.DATABASE;
        } else {
            return RepairCategory.HARDWARE;
        }
    }

    /**
     * Factory Pattern: création d'un réparateur
     */
    private void createReparator(String ticketId, TicketDTO ticket, RepairCategory category, 
                                  String reparatorId, ActorContext ctx) throws Exception {
        ActorSystem system = SAF.getContext().getBean(ActorSystem.class);
        
        if (system != null) {
            try {
                @SuppressWarnings("unchecked")
                ActorRef reparateur = system.createActor(
                    (Class<? extends Actor>) Class.forName("jira.ReparatorActor"),
                    reparatorId
                );

                if (reparateur != null) {
                    printReparatorAssigned(ticketId, reparatorId, category);

                    // Envoyer la demande de réparation
                    AssignRepairRequest repairReq = new AssignRepairRequest(
                        ticketId,
                        ticket.getTitle(),
                        ticket.getDescription(),
                        ticket.getPriority(),
                        category,
                        reparatorId
                    );
                    
                    reparateur.tell(repairReq, ctx.self());

                    // Simuler le travail et nettoyer après
                    new Thread(() -> {
                        try {
                            Thread.sleep(5000);
                            system.killActor(reparatorId);
                            printReparatorCompleted(reparatorId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            } catch (ClassNotFoundException e) {
                System.err.println("❌ Erreur: Classe ReparatorActor non trouvée");
                e.printStackTrace();
            }
        }
    }

    /**
     * LISTER LES TICKETS
     */
    private void handleListTickets(ListTicketsRequest req, ActorContext ctx) {
        String requester = (ctx.sender() != null) ? ctx.sender().getName() : "Inconnu";
        
        printSeparator("📊 LISTE DES TICKETS");
        System.out.println("Demandé par: " + requester);
        System.out.println("Total: " + tickets.size() + " ticket(s)");
        System.out.println("");

        List<String> ticketList = new ArrayList<>();
        for (Map.Entry<String, TicketDTO> entry : tickets.entrySet()) {
            String info = String.format("  • %s: %s [%s] [Priorité: %s]",
                entry.getKey(),
                entry.getValue().getTitle(),
                entry.getValue().getStatus(),
                entry.getValue().getPriority()
            );
            ticketList.add(info);
            System.out.println(info);
        }
        System.out.println("");

        ctx.reply(new ListTicketsResponse(ticketList, tickets.size()));
    }

    /**
     * SUPPRIMER UN TICKET
     */
    private void handleDeleteTicket(DeleteTicketRequest req, ActorContext ctx) {
        String ticketId = req.getTicketId();
        
        if (tickets.containsKey(ticketId)) {
            TicketDTO ticket = tickets.get(ticketId);
            ticket.setStatus(TicketStatus.DELETED);
            tickets.remove(ticketId);
            ticketToReparator.remove(ticketId);
            
            System.out.println("\n❌ TICKET SUPPRIMÉ: " + ticketId);
            ctx.reply(new TicketResponse(ticket, "Ticket supprimé avec succès"));
        } else {
            System.out.println("\n⚠️  ERREUR: Ticket non trouvé: " + ticketId);
            ctx.reply(new TicketResponse(null, "Ticket introuvable"));
        }
    }

    /**
     * Observer Pattern: notifier les observateurs
     */
    private void notifyObservers(TicketStatusUpdate update) {
        System.out.println(update);
    }

    // ===== AFFICHAGE =====

    private void printTicketCreated(String ticketId, TicketDTO ticket, String clientName) {
        printSeparator("🎫 NOUVEAU TICKET CRÉÉ");
        System.out.println("ID: " + ticketId);
        System.out.println("Titre: " + ticket.getTitle());
        System.out.println("Description: " + ticket.getDescription());
        System.out.println("Priorité: " + ticket.getPriority());
        System.out.println("Créé par: " + clientName);
        System.out.println("Statut: " + ticket.getStatus());
        System.out.println("");
    }

    private void printReparatorAssigned(String ticketId, String reparatorId, RepairCategory category) {
        System.out.println("🔧 RÉPARATEUR ASSIGNÉ");
        System.out.println("   → Réparateur: " + reparatorId);
        System.out.println("   → Catégorie: " + category.getDisplayName());
        System.out.println("   → Ticket: " + ticketId);
        System.out.println("");
    }

    private void printReparatorCompleted(String reparatorId) {
        System.out.println("\n✅ RÉPARATEUR TERMINÉ ET NETTOYÉ: " + reparatorId);
    }

    private void printSeparator(String title) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("  " + title);
        System.out.println("=".repeat(70));
    }
}