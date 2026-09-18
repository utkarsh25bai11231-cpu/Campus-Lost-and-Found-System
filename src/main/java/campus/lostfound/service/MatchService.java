package campus.lostfound.service;

import campus.lostfound.model.Item;
import campus.lostfound.model.MatchResult;
import campus.lostfound.util.DateUtil;

import java.util.*;

public class MatchService {
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "the", "is", "at", "which", "on", "a", "an", "and", "or", "in", "to", "for",
            "with", "near", "my", "it", "of", "was", "has", "have", "by", "from", "i", "lost", "found"
    ));

    public List<MatchResult> findMatchesForLostItem(Item lostItem, List<Item> foundItems, int threshold) {
        List<MatchResult> matches = new ArrayList<>();
        for (Item found : foundItems) {
            MatchResult res = evaluateMatch(lostItem, found);
            if (res.getScore() >= threshold) {
                matches.add(res);
            }
        }
        // Sort highest score first
        matches.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
        return matches;
    }

    public List<MatchResult> findAllPossibleMatches(List<Item> lostItems, List<Item> foundItems, int threshold) {
        List<MatchResult> matches = new ArrayList<>();
        for (Item lost : lostItems) {
            for (Item found : foundItems) {
                MatchResult res = evaluateMatch(lost, found);
                if (res.getScore() >= threshold) {
                    matches.add(res);
                }
            }
        }
        // Sort highest score first
        matches.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
        return matches;
    }

    public MatchResult evaluateMatch(Item lost, Item found) {
        int categoryScore = 0;
        int locationScore = 0;
        int dateScore = 0;
        int keywordScore = 0;

        List<String> reasonParts = new ArrayList<>();

        // 1. Category comparison (Max 25 pts)
        if (lost.getCategory() == found.getCategory()) {
            categoryScore = 25;
            reasonParts.add("Category matches (" + lost.getCategory().name() + ").");
        } else {
            reasonParts.add("Categories differ (" + lost.getCategory().name() + " vs " + found.getCategory().name() + ").");
        }

        // 2. Location comparison (Max 25 pts)
        String loc1 = (lost.getLocation() != null) ? lost.getLocation().trim().toLowerCase() : "";
        String loc2 = (found.getLocation() != null) ? found.getLocation().trim().toLowerCase() : "";

        if (!loc1.isEmpty() && !loc2.isEmpty()) {
            if (loc1.equals(loc2)) {
                locationScore = 25;
                reasonParts.add("Locations are an exact match ('" + lost.getLocation() + "').");
            } else if (loc1.contains(loc2) || loc2.contains(loc1)) {
                locationScore = 20;
                reasonParts.add("Locations strongly overlap.");
            } else {
                // Check if campus area keywords overlap (e.g. library, ab1, cafeteria, food street)
                String[] words1 = loc1.split("\\s+");
                boolean partialLoc = false;
                for (String w : words1) {
                    if (w.length() > 3 && loc2.contains(w)) {
                        locationScore = 15;
                        partialLoc = true;
                        reasonParts.add("Both reports mention '" + w + "' area.");
                        break;
                    }
                }
                if (!partialLoc) {
                    reasonParts.add("Locations are in different areas.");
                }
            }
        }

        // 3. Date proximity comparison (Max 20 pts)
        long diffDays = DateUtil.daysBetween(lost.getItemDate(), found.getItemDate());
        if (diffDays == 0) {
            dateScore = 20;
            reasonParts.add("Dates are identical (" + lost.getItemDate() + ").");
        } else if (diffDays <= 2) {
            dateScore = 18;
            reasonParts.add("Dates are very close (within 2 days).");
        } else if (diffDays <= 5) {
            dateScore = 14;
            reasonParts.add("Dates are close (within 5 days).");
        } else if (diffDays <= 10) {
            dateScore = 10;
            reasonParts.add("Dates are within 10 days.");
        } else if (diffDays <= 20) {
            dateScore = 5;
            reasonParts.add("Dates have moderate gap (within 20 days).");
        } else {
            reasonParts.add("Dates are far apart (" + diffDays + " days).");
        }

        // 4. Description keyword similarity (Max 30 pts)
        Set<String> wordsLost = extractKeywords(lost.getTitle() + " " + lost.getDescription());
        Set<String> wordsFound = extractKeywords(found.getTitle() + " " + found.getDescription());

        List<String> commonWords = new ArrayList<>();
        for (String w : wordsLost) {
            if (wordsFound.contains(w)) {
                commonWords.add(w);
            }
        }

        int matchCount = commonWords.size();
        if (matchCount >= 4) {
            keywordScore = 30;
            reasonParts.add("Descriptions contain high keyword overlap.");
        } else if (matchCount == 3) {
            keywordScore = 25;
            reasonParts.add("Descriptions contain similar keywords.");
        } else if (matchCount == 2) {
            keywordScore = 18;
            reasonParts.add("Descriptions share multiple key identifiers.");
        } else if (matchCount == 1) {
            keywordScore = 10;
            reasonParts.add("Descriptions share at least one keyword.");
        } else {
            reasonParts.add("No description keyword overlap.");
        }

        int totalScore = categoryScore + locationScore + dateScore + keywordScore;
        String matchedWordsStr = commonWords.isEmpty() ? "None" : String.join(", ", commonWords);
        String reasonStr = String.join("\n", reasonParts);

        return new MatchResult(lost, found, totalScore, categoryScore, locationScore, dateScore, keywordScore, matchedWordsStr, reasonStr);
    }

    private Set<String> extractKeywords(String text) {
        Set<String> set = new HashSet<>();
        if (text == null) return set;
        String clean = text.toLowerCase().replaceAll("[^a-zA-Z0-9\\s]", " ");
        String[] tokens = clean.split("\\s+");
        for (String token : tokens) {
            token = token.trim();
            if (token.length() > 2 && !STOP_WORDS.contains(token)) {
                set.add(token);
            }
        }
        return set;
    }
}
