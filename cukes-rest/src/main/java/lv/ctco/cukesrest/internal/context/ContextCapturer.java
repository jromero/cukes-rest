package lv.ctco.cukesrest.internal.context;

import com.google.inject.*;

import java.util.*;
import java.util.regex.*;

public class ContextCapturer extends BaseContextHandler {

    public static final String GROUP_PATTERN_REGEX = "(.*)";

    @Inject
    GlobalWorldFacade world;

    public void capture(String pattern, String value) {
        List<String> groups = extractGroups(pattern);
        if (groups.size() < 1) return;
        String regexPattern = transformToRegex(pattern);
        if (doesNotMatchPattern(value, regexPattern)) return;
        captureValuesFromPattern(regexPattern, groups, value);
    }

    String transformToRegex(String pattern) {
        return pattern.replaceAll(GROUP_PATTERN, GROUP_PATTERN_REGEX);
    }

    void captureValuesFromPattern(String regexPattern, List<String> groups, String value) {
        Matcher matcher = Pattern.compile(regexPattern).matcher(value);
        for (int i = 1; matcher.find(); i++) {
            if (matcher.group().isEmpty()) return;
            String groupValue = matcher.group(i);
            String key = groups.get(i - 1);
            world.put(key, groupValue, ContextScope.SCENARIO);
        }
    }

    boolean doesNotMatchPattern(String value, String regexPattern) {
        return !value.matches(regexPattern);
    }
}
