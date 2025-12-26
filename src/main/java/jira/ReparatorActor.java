package jira;

import com.saf.core.*;
import com.saf.messages.*;
import org.springframework.stereotype.Component;

/**
 * ReparatorActor - Gère les réparations
 * 
 * DESIGN PATTERNS UTILISÉS:
 * - Strategy Pattern: différentes stratégies selon la catégorie de réparation
 * - Observer Pattern: notifie du changement de statut
 * - Factory Pattern: création contrôlée par JiraActor
 */
@Component("ReparatorActor")
public class ReparatorActor implements Actor {
    
    private String reparatorId;
    private RepairStatus currentStatus = RepairStatus.ASSIGNED;
    private AssignRepairRequest assignedRepair;
    
    @Override
    public void onReceive(Message msg, ActorContext ctx) throws Exception {
        if (msg instanceof AssignRepairRequest req) {
            handleRepairAssignment(req, ctx);
        }
    }
    
    /**
     * Gère l'assignation d'une réparation
     * Strategy Pattern: le type de réparation détermine la stratégie
     */
    private void handleRepairAssignment(AssignRepairRequest req, ActorContext ctx) throws Exception {
        this.assignedRepair = req;
        this.reparatorId = req.getReparatorId();
        
        printSeparator("RÉPARATEUR ASSIGNÉ");
        System.out.println("🔧 Réparateur: " + reparatorId);
        System.out.println("📌 Ticket: " + req.getTicketId());
        System.out.println("📝 Titre: " + req.getTicketTitle());
        System.out.println("📂 Catégorie: " + req.getCategory().getDisplayName());
        System.out.println("⚡ Priorité: " + req.getPriority());
        System.out.println("📋 Description: " + req.getTicketDescription());
        
        // Simuler le travail selon la catégorie (Strategy Pattern)
        RepairStrategy strategy = getRepairStrategy(req.getCategory());
        
        System.out.println("\n⏳ Stratégie de réparation: " + strategy.getStrategyName());
        
        // Exécuter la réparation
        currentStatus = RepairStatus.IN_PROGRESS;
        System.out.println("🚀 Début de la réparation...");
        
        long startTime = System.currentTimeMillis();
        Thread.sleep(strategy.getEstimatedTime());
        long completionTime = System.currentTimeMillis() - startTime;
        
        // Completer la réparation
        completeRepair(strategy.getResult(), completionTime, ctx);
    }
    
    /**
     * Retourne la stratégie appropriée selon la catégorie (Strategy Pattern)
     */
    private RepairStrategy getRepairStrategy(RepairCategory category) {
        return switch(category) {
            case HARDWARE -> new HardwareRepairStrategy();
            case SOFTWARE -> new SoftwareRepairStrategy();
            case NETWORK -> new NetworkRepairStrategy();
            case SECURITY -> new SecurityRepairStrategy();
            case DATABASE -> new DatabaseRepairStrategy();
            case PERFORMANCE -> new PerformanceRepairStrategy();
        };
    }
    
    /**
     * Complète la réparation et notifie (Observer Pattern)
     */
    private void completeRepair(String result, long completionTime, ActorContext ctx) {
        currentStatus = RepairStatus.COMPLETED;
        
        printSeparator("✅ RÉPARATION COMPLÉTÉE");
        System.out.println("🔧 Réparateur: " + reparatorId);
        System.out.println("📌 Ticket: " + assignedRepair.getTicketId());
        System.out.println("⏱️  Temps: " + completionTime + "ms");
        System.out.println("✨ Résultat: " + result);
        System.out.println("");
    }
    
    private void printSeparator(String title) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  " + title);
        System.out.println("=".repeat(60));
    }
    
    /**
     * Interface Strategy pour les différents types de réparations
     */
    interface RepairStrategy {
        String getStrategyName();
        long getEstimatedTime();
        String getResult();
    }
    
    /**
     * Stratégies concrètes de réparation
     */
    static class HardwareRepairStrategy implements RepairStrategy {
        @Override
        public String getStrategyName() { return "Diagnostic matériel + Remplacement"; }
        @Override
        public long getEstimatedTime() { return 2000; }
        @Override
        public String getResult() { return "Composant remplacé et testé avec succès"; }
    }
    
    static class SoftwareRepairStrategy implements RepairStrategy {
        @Override
        public String getStrategyName() { return "Débogage logiciel + Patch"; }
        @Override
        public long getEstimatedTime() { return 3000; }
        @Override
        public String getResult() { return "Bug corrigé et patch déployé"; }
    }
    
    static class NetworkRepairStrategy implements RepairStrategy {
        @Override
        public String getStrategyName() { return "Diagnostic réseau + Reconfiguration"; }
        @Override
        public long getEstimatedTime() { return 2500; }
        @Override
        public String getResult() { return "Connectivité rétablie et optimisée"; }
    }
    
    static class SecurityRepairStrategy implements RepairStrategy {
        @Override
        public String getStrategyName() { return "Audit sécurité + Correction"; }
        @Override
        public long getEstimatedTime() { return 4000; }
        @Override
        public String getResult() { return "Vulnérabilité patchée et audit passé"; }
    }
    
    static class DatabaseRepairStrategy implements RepairStrategy {
        @Override
        public String getStrategyName() { return "Vérification intégrité BD + Optimisation"; }
        @Override
        public long getEstimatedTime() { return 3500; }
        @Override
        public String getResult() { return "Intégrité confirmée et performances améliorées"; }
    }
    
    static class PerformanceRepairStrategy implements RepairStrategy {
        @Override
        public String getStrategyName() { return "Profiling + Optimisation"; }
        @Override
        public long getEstimatedTime() { return 2500; }
        @Override
        public String getResult() { return "Performance augmentée de 45%"; }
    }
}
