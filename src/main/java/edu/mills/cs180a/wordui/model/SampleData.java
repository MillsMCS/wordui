package edu.mills.cs180a.wordui.model;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import com.google.common.annotations.VisibleForTesting;
import edu.mills.cs180a.wordnik.client.ApiClientHelper;
import edu.mills.cs180a.wordnik.client.api.WordApi;
import edu.mills.cs180a.wordnik.client.api.WordsApi;
import edu.mills.cs180a.wordnik.client.invoker.ApiClient;
import edu.mills.cs180a.wordnik.client.model.FrequencySummary;
import edu.mills.cs180a.wordnik.client.model.WordOfTheDay;
import javafx.collections.ObservableList;

/**
 * Create sample data to display on the list in JavaFX.
 *
 * @author Ellen Spertus
 * @author Makie Maekawa
 *
 */
public class SampleData {
    @VisibleForTesting
    protected static final String WORD_KEY = "word";
    @VisibleForTesting
    protected static final String FREQ_COUNT_KEY = "count";
    @VisibleForTesting
    protected static final String FREQ_YEAR_KEY = "year";
    @VisibleForTesting
    protected static final int FREQ_YEAR = 2012;
    private static ApiClient client; // set in fillSampleData()


    @VisibleForTesting
    protected static int getFrequencyByYear(WordApi wordApi, String word, int year) {
        FrequencySummary fs = wordApi.getWordFrequency(word, "false", year, year);
        return getFrequencyFromSummary(fs, year);
    }

    private static int getFrequencyFromSummary(FrequencySummary fs, int year) {
        List<Object> freqObjects = fs.getFrequency();
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

    @VisibleForTesting
    public static List<Object> getDefinitions(WordsApi WordsApi) {
        return WordsApi.getWordOfTheDay().getDefinitions();
    }

    @VisibleForTesting
    public static String getWord(WordsApi WordsApi) {
        return WordsApi.getWordOfTheDay().getWord();
    }

    protected static WordOfTheDay getWordOfTheDay(WordsApi WordsApi) {
        return WordsApi.getWordOfTheDay();
    }

    /**
     * Get definitions and create WordRecord data.
     *
     * @param wordsApi API key client
     * @return Return data to be displayed on the screen. return null, if definitions is null or
     *         empty
     */
    public static WordRecord addWordOfTheDay(WordsApi wordsApi) {
        List<Object> definitions = getWordOfTheDay(wordsApi).getDefinitions();
        if (definitions != null && !definitions.isEmpty()) {
            Object definition = definitions.get(0);
            if (definition instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<Object, Object> definitionAsMap = (Map<Object, Object>) definition;
                return (buildWordRecord(getWordOfTheDay(wordsApi).getWord(), definitionAsMap));
            }
        }
        return null;
    }

    /**
     * Create sample data for display on the list
     *
     * @param backingList A list that allows listeners to track when something changes
     */
    public static void fillSampleData(ObservableList<WordRecord> backingList) {
        try {
            client = ApiClientHelper.getApiClient();
            WordsApi wordsApi = client.buildClient(WordsApi.class);
            backingList.add(addWordOfTheDay(wordsApi));
        } catch (IOException e) {
            System.err.println("Unable to get API key.");
        }

        backingList.add(new WordRecord("buffalo", 5153, "The North American bison."));
        backingList.add(new WordRecord("school", 23736, "A large group of aquatic animals."));
        backingList.add(
                new WordRecord("Java", 179, "An island of Indonesia in the Malay Archipelago"));
        backingList.add(
                new WordRecord("random", 794, "Having no specific pattern, purpose, or objective"));
    }

    protected static WordRecord buildWordRecord(String word, Map<Object, Object> definition) {
        WordApi wordApi = client.buildClient(WordApi.class);
        return new WordRecord(word, getFrequencyByYear(wordApi, word, FREQ_YEAR),
                definition.get("text").toString());
    }
}
