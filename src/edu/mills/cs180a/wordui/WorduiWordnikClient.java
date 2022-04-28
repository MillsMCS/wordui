package edu.mills.cs180a.wordui;

import java.io.*;
import java.nio.charset.*;
import java.util.*;
import edu.mills.cs180a.wordnik.client.api.*;
import edu.mills.cs180a.wordnik.client.invoker.*;
import edu.mills.cs180a.wordnik.client.model.*;
import edu.mills.cs180a.wordui.model.*;

public class WorduiWordnikClient {
    static final String FREQ_COUNT_KEY = "count";
    static final String FREQ_YEAR_KEY = "year";
    private static final int FREQ_YEAR = 2012;

    private WordApi wordApi;

    private WorduiWordnikClient(WordApi wordApi) {
        this.wordApi = wordApi;
    }

    public static WorduiWordnikClient getInstance() throws IOException {
        String key = getApiKey();
        ApiClient client = new ApiClient("api_key", key);
        WordApi wordApi = client.buildClient(WordApi.class);
        return new WorduiWordnikClient(wordApi);
    }

    public static WorduiWordnikClient getMockInstance() {
        return new WorduiWordnikClient(new MockWordApi());
    }

    private static String getApiKey() throws IOException {
        // File should be in same directory as this class.
        return getResource("api-key.txt");
    }

    private static String getResource(String filename) throws IOException {
        try (InputStream is = WorduiWordnikClient.class.getResourceAsStream(filename)) {
            if (is == null) {
                throw new IOException("Unable to open file " + filename);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();
        }
    }

    public int getFrequencyByYear(String word, int year) {
        FrequencySummary freqSummary = wordApi.getWordFrequency(word, "false", year, year);
        List<Object> freqObjects = freqSummary.getFrequency();
        // freqObjects is a List<Map> [{"year" = "2012", "count" = 179}] for "Java"
        if (freqObjects instanceof List) {
            List<Object> maps = (List<Object>) freqObjects;
            for (Object map : maps) {
                if (map instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> m = (Map<String, Object>) map;

                    if (m.containsKey(FREQ_YEAR_KEY)
                            && Integer.parseInt(m.get(FREQ_YEAR_KEY).toString()) == year
                            && m.containsKey(FREQ_COUNT_KEY)) {
                        return Integer.parseInt(m.get(FREQ_COUNT_KEY).toString());
                    }
                }
            }
        }
        return 0;
    }

    // public WordRecord getWordOfTheDay(String date) {
    // WordOfTheDay word = wordsApi.getWordOfTheDay(date);
    // List<Object> definitions = word.getDefinitions();
    // if (definitions != null && !definitions.isEmpty()) {
    // Object definition = definitions.get(0);
    // if (definition instanceof Map) {
    // @SuppressWarnings("unchecked")
    // Map<String, String> definitionAsMap = (Map<String, String>) definition;
    // return buildWordRecord(word.getWord(), definitionAsMap);
    // }
    // }
    // return new WordRecord(word.getWord(), 0, ""); // no frequency or definition
    // }

    private WordRecord buildWordRecord(String word, Map<String, String> definition) {
        return new WordRecord(word, getFrequencyByYear(word, FREQ_YEAR),
                definition.get("text").toString());
    }
}
