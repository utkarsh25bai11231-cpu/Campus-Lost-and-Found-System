package campus.lostfound.model;

public class MatchResult {
    private Item lostItem;
    private Item foundItem;
    private int score;
    private int categoryScore;
    private int locationScore;
    private int dateScore;
    private int keywordScore;
    private String matchedKeywords;
    private String reason;

    public MatchResult(Item lostItem, Item foundItem, int score, int categoryScore,
                       int locationScore, int dateScore, int keywordScore,
                       String matchedKeywords, String reason) {
        this.lostItem = lostItem;
        this.foundItem = foundItem;
        this.score = score;
        this.categoryScore = categoryScore;
        this.locationScore = locationScore;
        this.dateScore = dateScore;
        this.keywordScore = keywordScore;
        this.matchedKeywords = matchedKeywords;
        this.reason = reason;
    }

    public void displayExplanation() {
        System.out.println("--------------------------------------------------");
        System.out.println("                 POSSIBLE MATCH                   ");
        System.out.println("--------------------------------------------------");
        System.out.println("Lost Item:");
        System.out.printf("  [#%d] %s%n", lostItem.getItemId(), lostItem.getTitle());
        System.out.println("Found Item:");
        System.out.printf("  [#%d] %s%n", foundItem.getItemId(), foundItem.getTitle());
        System.out.println();
        System.out.printf("Category Match     : %d/25%n", categoryScore);
        System.out.printf("Location Match     : %d/25%n", locationScore);
        System.out.printf("Date Similarity    : %d/20%n", dateScore);
        System.out.printf("Description Match  : %d/30%n", keywordScore);
        System.out.println();
        System.out.printf("Total Score        : %d/100%n", score);
        System.out.println();
        System.out.println("Reason:");
        System.out.println(reason);
        if (matchedKeywords != null && !matchedKeywords.equals("None")) {
            System.out.println("Matched Keywords   : [" + matchedKeywords + "]");
        }
        System.out.println("--------------------------------------------------");
    }

    public Item getLostItem() {
        return lostItem;
    }

    public Item getFoundItem() {
        return foundItem;
    }

    public int getScore() {
        return score;
    }

    public int getCategoryScore() {
        return categoryScore;
    }

    public int getLocationScore() {
        return locationScore;
    }

    public int getDateScore() {
        return dateScore;
    }

    public int getKeywordScore() {
        return keywordScore;
    }

    public String getMatchedKeywords() {
        return matchedKeywords;
    }

    public String getReason() {
        return reason;
    }
}
