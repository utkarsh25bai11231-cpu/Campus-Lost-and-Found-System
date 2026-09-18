package campus.lostfound.util;

import campus.lostfound.model.Item;
import campus.lostfound.model.MatchResult;
import campus.lostfound.service.ItemService;
import campus.lostfound.service.MatchService;

import java.util.List;

public class MatchFinderTask implements Runnable {
    private final Item newlyReportedItem;
    private final ItemService itemService;
    private final MatchService matchService;

    public MatchFinderTask(Item newlyReportedItem, ItemService itemService, MatchService matchService) {
        this.newlyReportedItem = newlyReportedItem;
        this.itemService = itemService;
        this.matchService = matchService;
    }

    @Override
    public void run() {
        try {
            // Simulate brief background scan time
            Thread.sleep(700);

            List<Item> candidates;
            if ("LOST".equalsIgnoreCase(newlyReportedItem.getReportType())) {
                candidates = itemService.getFoundItems();
            } else {
                candidates = itemService.getLostItems();
            }

            int matchCount = 0;
            for (Item candidate : candidates) {
                MatchResult res;
                if ("LOST".equalsIgnoreCase(newlyReportedItem.getReportType())) {
                    res = matchService.evaluateMatch(newlyReportedItem, candidate);
                } else {
                    res = matchService.evaluateMatch(candidate, newlyReportedItem);
                }
                if (res.getScore() >= 50) {
                    matchCount++;
                }
            }

            if (matchCount > 0) {
                String alert = String.format("Match engine identified %d potential match(es) for your item '%s' (#%d).",
                        matchCount, newlyReportedItem.getTitle(), newlyReportedItem.getItemId());
                NotificationCenter.addNotification(alert);
                System.out.printf("%n>>> [BACKGROUND ALERT] %s%n> ", alert);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            // Keep background task silent on non-critical error
        }
    }
}
