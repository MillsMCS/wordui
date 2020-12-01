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
    @VisibleForTesting
    protected static final String FREQ_COUNT_KEY = "count";
    @VisibleForTesting
    protected static final String FREQ_YEAR_KEY = "year";
    private static final int FREQ_YEAR = 2012;
    @VisibleForTesting
    protected static ApiClient client; // set in fillSampleData()

    private static int getFrequencyFromSummary(FrequencySummary fs, int year) {
        List<Object> freqObjects = fs.getFrequency();
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
    
    @VisibleForTesting
    protected static WordOfTheDay getWordOfTheDay(WordsApi wordsApi) {
    	return wordsApi.getWordOfTheDay();
    }

    // TODO: Move to spring-swagger-wordnik-client
    @VisibleForTesting
    protected static int getFrequencyByYear(WordApi wordApi, String word, int year) {
        FrequencySummary fs = wordApi.getWordFrequency(word, "false", year, year);
        return getFrequencyFromSummary(fs, year);
    }

    @VisibleForTesting
    protected static WordRecord buildWordRecord(String word, Map<Object, Object> definition) {
        WordApi wordApi = client.buildClient(WordApi.class);
        return new WordRecord(
                word,
                getFrequencyByYear(wordApi, word, FREQ_YEAR),
                definition.get("text").toString());
    }
    
    /***
     * Adds the current word of the day to a given list of words.
     * 
     * @param backingList a list to which the word of the day will be added
     * @param wordsApi the API used to get today's word of the day
     */
    @VisibleForTesting
    protected static void addWordOfTheDay(List<WordRecord> backingList, WordOfTheDay word) {
        List<Object> definitions = word.getDefinitions();
        if(definitions != null && !definitions.isEmpty()) {
            Object definition = definitions.get(0);
            if (definition instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<Object, Object> definitionAsMap = (Map<Object, Object>) definition;
                backingList.add(buildWordRecord(word.getWord(), definitionAsMap));
            }
        }
    }

    /***
     * Creates a list of standard sample words and their definitions along
     * with the word of the day. Implements the wordnik API.
     * 
     * @param backingList a list of sample word records
     */
    public static void fillSampleData(ObservableList<WordRecord> backingList) {
        try {
            client = ApiClientHelper.getApiClient();
            WordsApi wordsApi = client.buildClient(WordsApi.class);
            WordOfTheDay word = getWordOfTheDay(wordsApi);
            System.out.println("word (wotd): " + word.getWord());
            System.out.println("defs: " + word.getDefinitions());
            addWordOfTheDay(backingList, word);
            
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
}
