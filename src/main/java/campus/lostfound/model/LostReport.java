package campus.lostfound.model;

import campus.lostfound.enums.ItemCategory;
import campus.lostfound.enums.ItemStatus;

public class LostReport extends Item {
    private double rewardAmount;
    private boolean isIdentifiable;

    public LostReport() {
        super();
    }

    public LostReport(int itemId, String title, String description, ItemCategory category,
                      String location, String itemDate, ItemStatus status, int userId,
                      double rewardAmount, boolean isIdentifiable) {
        super(itemId, title, description, category, location, itemDate, status, userId);
        this.rewardAmount = rewardAmount;
        this.isIdentifiable = isIdentifiable;
    }

    @Override
    public String getReportType() {
        return "LOST";
    }

    @Override
    public String getSpecialDetail() {
        return "Reward: ₹" + String.format("%.2f", rewardAmount) + " | Identifiable marks: " + (isIdentifiable ? "Yes" : "No");
    }

    public double getRewardAmount() {
        return rewardAmount;
    }

    public void setRewardAmount(double rewardAmount) {
        this.rewardAmount = rewardAmount;
    }

    public boolean isIdentifiable() {
        return isIdentifiable;
    }

    public void setIdentifiable(boolean identifiable) {
        isIdentifiable = identifiable;
    }
}
