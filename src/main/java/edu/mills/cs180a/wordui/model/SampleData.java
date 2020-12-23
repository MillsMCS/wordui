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


public class SampleData {
    /**
     * String key for the count.
     */
    @VisibleForTesting
    protected static final String FREQ_COUNT_KEY = "count";
    /**
     * String key for the frequency.
     */
    @VisibleForTesting
    protected static final String FREQ_YEAR_KEY = "year";
    private static final int FREQ_YEAR = 2012;
    private static ApiClient client; // set in fillSampleData()

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

    // TODO: Move to spring-swagger-wordnik-client

    @VisibleForTesting
    protected static int getFrequencyByYear(WordApi wordApi, String word, int year) {
        FrequencySummary fs = wordApi.getWordFrequency(word, "false", year, year);
        return getFrequencyFromSummary(fs, year);
    }

    private static WordRecord buildWordRecord(String word, Map<Object, Object> definition,
            WordApi wordApi) {
        return new WordRecord(
                word,
                getFrequencyByYear(wordApi, word, FREQ_YEAR),
                definition.get("text").toString());
    }

    /**
     * Gets a word of the day object.
     * 
     * @param wordsApi the words API
     * @return the word of the day
     */
    @VisibleForTesting
    protected static WordOfTheDay getWordOfTheDay(WordsApi wordsApi) {
        return wordsApi.getWordOfTheDay();
    }

    /**
     * Adds word records to a passed list.
     * 
     * @param backingList the list of word records
     */
    public static void fillSampleData(ObservableList<WordRecord> backingList) {
        try {
            client = ApiClientHelper.getApiClient();
            WordsApi wordsApi = client.buildClient(WordsApi.class);
            WordApi wordApi = client.buildClient(WordApi.class);
            addWordOfTheDay(backingList, wordsApi, wordApi);
        } catch (IOException e) {
            System.err.println("Unable to get API key.");
        }

        backingList.add(new WordRecord("buffalo", 5153, "The North American bison."));
        backingList.add(new WordRecord("school", 23736, "A large group of aquatic animals."));
        backingList.add(new WordRecord("Java",
                179, "An island of Indonesia in the Malay Archipelago"));
        backingList.add(new WordRecord("random",
                794, "Having no specific pattern, purpose, or objective"));
    }

    /**
     * Adds the word to the passed list.
     * 
     * @param backingList the list of word records
     * @param wordsApi the words API
     * @param wordApi the word API
     */
    @VisibleForTesting
    protected static void addWordOfTheDay(ObservableList<WordRecord> backingList,
            WordsApi wordsApi, WordApi wordApi) {
        WordOfTheDay word = getWordOfTheDay(wordsApi);

        List<Object> definitions = word.getDefinitions();
        if (definitions != null && !definitions.isEmpty()) {
            Object definition = definitions.get(0);
            if (definition instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<Object, Object> definitionAsMap = (Map<Object, Object>) definition;
                backingList.add(buildWordRecord(word.getWord(), definitionAsMap, wordApi));
            }
        }
    }
}
