package aifu.project.libraryweb.lucene;

import java.util.HashMap;
import java.util.Map;

public class KeyboardLayoutCorrector {

    private static final Map<Character, Character> engToRusLayout = new HashMap<>();
    private static final Map<Character, Character> rusToEngLayout = new HashMap<>();

    private static final Map<String, String> engToRusPhonetic = new HashMap<>();
    private static final Map<String, String> rusToEngPhonetic = new HashMap<>();

    static {
        String eng = "qwertyuiop[]asdfghjkl;'zxcvbnm,./";
        String rus = "йцукенгшщзхъфывапролджэячсмитьбю.";

        for (int i = 0; i < eng.length(); i++) {
            char lowerEng = eng.charAt(i);
            char lowerRus = rus.charAt(i);
            engToRusLayout.put(lowerEng, lowerRus);
            rusToEngLayout.put(lowerRus, lowerEng);
        }

        String[][] phoneticPairs = {
                {"shch", "щ"}, {"sha", "щ"}, {"yo", "ё"}, {"j", "ж"}, {"ts", "ц"}, {"ch", "ч"},
                {"sh", "ш"}, {"yu", "ю"}, {"ya", "я"},
                {"a", "а"}, {"b", "б"}, {"v", "в"}, {"g", "г"}, {"d", "д"},
                {"e", "е"}, {"z", "з"}, {"i", "и"}, {"y", "й"}, {"k", "к"},
                {"l", "л"}, {"m", "м"}, {"n", "н"}, {"o", "о"}, {"p", "п"},
                {"r", "р"}, {"s", "с"}, {"t", "т"}, {"u", "у"}, {"f", "ф"},
                {"h", "х"}, {"x", "х"}
        };

        for (String[] pair : phoneticPairs) {
            engToRusPhonetic.put(pair[0], pair[1]);
            rusToEngPhonetic.put(pair[1], pair[0]);
        }
    }

    public static String correctLayout(String input) {
        StringBuilder result = new StringBuilder();
        for (char ch : input.toLowerCase().toCharArray()) {
            result.append(engToRusLayout.getOrDefault(ch, ch));
        }
        return result.toString();
    }

    public static String correctLayoutReverse(String input) {
        StringBuilder result = new StringBuilder();
        for (char ch : input.toLowerCase().toCharArray()) {
            result.append(rusToEngLayout.getOrDefault(ch, ch));
        }
        return result.toString();
    }

    public static String transliterateToRussian(String input) {
        StringBuilder result = new StringBuilder();
        String lower = input.toLowerCase();

        int i = 0;
        while (i < lower.length()) {
            int matchLength = getMatchLength(lower, i);
            if (matchLength > 0) {
                result.append(engToRusPhonetic.get(lower.substring(i, i + matchLength)));
                i += matchLength;
            } else {
                result.append(lower.charAt(i));
                i++;
            }
        }

        return result.toString();
    }

    public static String transliterateToEnglish(String input) {
        StringBuilder result = new StringBuilder();
        String lower = input.toLowerCase();

        for (int i = 0; i < lower.length(); i++) {
            String ch = String.valueOf(lower.charAt(i));
            result.append(rusToEngPhonetic.getOrDefault(ch, ch));
        }

        return result.toString();
    }

    private static int getMatchLength(String text, int index) {
        for (int len = 4; len >= 1; len--) {
            if (index + len <= text.length()) {
                String sub = text.substring(index, index + len);
                if (KeyboardLayoutCorrector.engToRusPhonetic.containsKey(sub)) {
                    return len;
                }
            }
        }
        return 0;
    }

    private KeyboardLayoutCorrector() {
    }
}
