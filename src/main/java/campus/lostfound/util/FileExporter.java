package campus.lostfound.util;

import campus.lostfound.model.Claim;
import campus.lostfound.model.Item;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;

public class FileExporter {

    public static boolean exportReportsToFile(List<Item> items, List<Claim> claims, String filePath) {
        File file = new File(filePath);
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
            writer.println("================================================================================");
            writer.println("               CAMPUS LOST AND FOUND SYSTEM - EXPORT REPORT                     ");
            writer.println("                 Generated on: " + LocalDateTime.now());
            writer.println("================================================================================");
            writer.println();

            writer.println(">>> 1. ALL ITEMS & REPORTS (" + items.size() + " total) <<<");
            writer.println("--------------------------------------------------------------------------------");
            writer.printf("%-5s | %-6s | %-20s | %-16s | %-12s | %-15s | %-10s%n",
                    "ID", "TYPE", "TITLE", "CATEGORY", "STATUS", "LOCATION", "DATE");
            writer.println("--------------------------------------------------------------------------------");

            for (Item item : items) {
                String title = item.getTitle();
                if (title.length() > 20) title = title.substring(0, 17) + "...";
                String loc = item.getLocation();
                if (loc.length() > 15) loc = loc.substring(0, 12) + "...";

                writer.printf("%-5d | %-6s | %-20s | %-16s | %-12s | %-15s | %-10s%n",
                        item.getItemId(),
                        item.getReportType(),
                        title,
                        item.getCategory().name(),
                        item.getStatus().name(),
                        loc,
                        item.getItemDate());
                writer.println("   Description : " + item.getDescription());
                writer.println("   Details     : " + item.getSpecialDetail());
                writer.println();
            }

            writer.println();
            writer.println(">>> 2. ALL CLAIMS (" + (claims != null ? claims.size() : 0) + " total) <<<");
            writer.println("--------------------------------------------------------------------------------");
            writer.printf("%-5s | %-12s | %-10s | %-12s | %-20s%n",
                    "ID", "FOUND ITEM#", "CLAIMANT#", "STATUS", "DATE");
            writer.println("--------------------------------------------------------------------------------");

            if (claims != null) {
                for (Claim c : claims) {
                    writer.printf("%-5d | %-12d | %-10d | %-12s | %-20s%n",
                            c.getClaimId(),
                            c.getFoundItemId(),
                            c.getClaimantUserId(),
                            c.getStatus().name(),
                            c.getClaimDate());
                    writer.println("   Proof Details: " + c.getProofDetails());
                    writer.println();
                }
            }

            writer.println("================================================================================");
            writer.println("                              END OF REPORT                                     ");
            writer.println("================================================================================");
            return true;
        } catch (IOException e) {
            System.err.println("[FileExporter Error] Failed to export file: " + e.getMessage());
            return false;
        }
    }
}
