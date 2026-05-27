package mg.univ.tinder.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class MatchService {
    public static final class MatchResult {
        private final int percent;
        private final List<String> commonInterests;
        private final List<String> commonPreferenceTags;

        public MatchResult(int percent, List<String> commonInterests, List<String> commonPreferenceTags) {
            this.percent = percent;
            this.commonInterests = commonInterests;
            this.commonPreferenceTags = commonPreferenceTags;
        }

        public int getPercent() { return percent; }
        public List<String> getCommonInterests() { return commonInterests; }
        public List<String> getCommonPreferenceTags() { return commonPreferenceTags; }
    }

    public MatchResult compute(Set<String> myInterests, Set<String> otherInterests,
                               Set<String> myPreferenceTags, Set<String> otherPreferenceTags,
                               int ageDiff, boolean sameCity) {
        int interestScore = scoreInterests(myInterests, otherInterests); // /45
        int prefScore = scorePreferences(myPreferenceTags, otherPreferenceTags); // /30
        int ageScore = scoreAge(ageDiff); // /15
        int cityScore = sameCity ? 10 : 0; // /10
        int total = clamp(interestScore + prefScore + ageScore + cityScore, 0, 100);

        List<String> common = new ArrayList<>();
        if (myInterests != null && otherInterests != null) {
            for (String s : myInterests) {
                if (otherInterests.contains(s)) common.add(s);
            }
        }
        List<String> commonPrefs = new ArrayList<>();
        if (myPreferenceTags != null && otherPreferenceTags != null) {
            for (String s : myPreferenceTags) {
                if (otherPreferenceTags.contains(s)) commonPrefs.add(s);
            }
        }

        return new MatchResult(total, common, commonPrefs);
    }

    private int scoreInterests(Set<String> a, Set<String> b) {
        if (a == null || b == null || a.isEmpty() || b.isEmpty()) return 0;
        int inter = 0;
        for (String s : a) if (b.contains(s)) inter++;
        int union = a.size();
        for (String s : b) if (!a.contains(s)) union++;
        double jaccard = union == 0 ? 0.0 : ((double) inter / (double) union);
        return (int) Math.round(jaccard * 45.0);
    }

    private int scorePreferences(Set<String> a, Set<String> b) {
        if (a == null || b == null || a.isEmpty() || b.isEmpty()) return 0;
        int inter = 0;
        for (String s : a) if (b.contains(s)) inter++;
        int union = a.size();
        for (String s : b) if (!a.contains(s)) union++;
        double jaccard = union == 0 ? 0.0 : ((double) inter / (double) union);
        return (int) Math.round(jaccard * 30.0);
    }

    private int scoreAge(int diff) {
        diff = Math.abs(diff);
        if (diff <= 1) return 15;
        if (diff <= 3) return 13;
        if (diff <= 5) return 11;
        if (diff <= 8) return 7;
        if (diff <= 12) return 3;
        return 0;
    }

    private int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }
}

