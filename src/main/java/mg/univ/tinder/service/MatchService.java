package mg.univ.tinder.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class MatchService {
    public static final class MatchResult {
        private final int percent;
        private final List<String> commonInterests;

        public MatchResult(int percent, List<String> commonInterests) {
            this.percent = percent;
            this.commonInterests = commonInterests;
        }

        public int getPercent() { return percent; }
        public List<String> getCommonInterests() { return commonInterests; }
    }

    public MatchResult compute(Set<String> myInterests, Set<String> otherInterests, int ageDiff, boolean sameCity) {
        int interestScore = scoreInterests(myInterests, otherInterests); // /60
        int ageScore = scoreAge(ageDiff); // /25
        int cityScore = sameCity ? 15 : 0; // /15
        int total = clamp(interestScore + ageScore + cityScore, 0, 100);

        List<String> common = new ArrayList<>();
        if (myInterests != null && otherInterests != null) {
            for (String s : myInterests) {
                if (otherInterests.contains(s)) common.add(s);
            }
        }

        return new MatchResult(total, common);
    }

    private int scoreInterests(Set<String> a, Set<String> b) {
        if (a == null || b == null || a.isEmpty() || b.isEmpty()) return 0;
        int inter = 0;
        for (String s : a) if (b.contains(s)) inter++;
        int union = a.size();
        for (String s : b) if (!a.contains(s)) union++;
        double jaccard = union == 0 ? 0.0 : ((double) inter / (double) union);
        return (int) Math.round(jaccard * 60.0);
    }

    private int scoreAge(int diff) {
        diff = Math.abs(diff);
        if (diff <= 1) return 25;
        if (diff <= 3) return 22;
        if (diff <= 5) return 18;
        if (diff <= 8) return 12;
        if (diff <= 12) return 6;
        return 0;
    }

    private int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }
}

