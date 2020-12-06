package edu.mills.cs180a.wordui.model;

import static edu.mills.cs180a.wordui.model.SampleData.FREQ_COUNT_KEY;
import static edu.mills.cs180a.wordui.model.SampleData.FREQ_YEAR;
import static edu.mills.cs180a.wordui.model.SampleData.FREQ_YEAR_KEY;
import static edu.mills.cs180a.wordui.model.SampleData.getFrequencyByYear;
import static edu.mills.cs180a.wordui.model.SampleData.getWordOfTheDay;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import edu.mills.cs180a.wordnik.client.api.WordApi;
import edu.mills.cs180a.wordnik.client.api.WordsApi;
import edu.mills.cs180a.wordnik.client.model.FrequencySummary;
import edu.mills.cs180a.wordnik.client.model.WordOfTheDay;

class SampleDataTest {
    private final WordApi mockWordApi = mock(WordApi.class);
    private final WordsApi mockWordsApi = mock(WordsApi.class);

    private static final Map<String, FrequencySummary> FREQS_MAP = Map.of("apple",
            makeFrequencySummary(List.of(makeMap(2000, 339), makeMap(2001, 464))), "orange",
            makeFrequencySummary(List.of(makeMap(2000, 774), makeMap(2001, 941))), "blueberry",
            makeFrequencySummary(List.of(makeMap(2000, 10), makeMap(2001, 58))), "airplane",
            makeFrequencySummary(List.of(makeMap(2012, 353))));

    private static final WordOfTheDay MOCK_WORD = makeWordOfTheDay("airplane",
            List.of("Any of various winged vehicles capable of flight, "
                    + "generally heavier than air and driven by jet engines or propellers."));

    private static final List<Object> DIFINITION_MAP = new ArrayList<Object>(
            Arrays.asList(makeMapDefin("Any of various winged vehicles capable of flight,"
                    + "generally heavier than air and driven by jet engines or propellers.")));

    private static final WordRecord MOCK_WORDRECORD =
            new WordRecord("airplane", 353, "Any of various winged vehicles capable of flight,"
                    + "generally heavier than air and driven by jet engines or propellers.");

    @BeforeEach
    void setup() {
        when(mockWordApi.getWordFrequency(anyString(), anyString(), anyInt(), anyInt()))
                .thenAnswer(invocation -> FREQS_MAP.get(invocation.getArgument(0)));
        when(mockWordsApi.getWordOfTheDay()).thenReturn(MOCK_WORD);
        when(mockWordsApi.getWordOfTheDay().getWord()).thenReturn("blueberry");
        when(mockWordsApi.getWordOfTheDay().getDefinitions())
                .thenReturn(List.of("Any of various plants of the genus"));
    }

    private static Map<Object, Object> makeMap(int year, int count) {
        return Map.of(FREQ_YEAR_KEY, String.valueOf(year), FREQ_COUNT_KEY, count);
    }

    private static FrequencySummary makeFrequencySummary(List<Object> freqs) {
        FrequencySummary fs = mock(FrequencySummary.class);
        when(fs.getFrequency()).thenReturn(freqs);
        return fs;
    }

    private static Map<Object, Object> makeMapDefin(String defin) {
        Map<Object, Object> map = new LinkedHashMap<>(Map.of("text", String.valueOf(defin)));
        return map;
    }

    @ParameterizedTest
    @CsvSource({"apple,2000,339", "apple,2001,464", "apple,2020,0", "orange,2000,774",
            "orange,2001,941", "orange,2050,0", "airplane,2012,353"})
    void getFrequencyByYear_Equal_correctValue(String word, int year, int count) {
        assertEquals(count, getFrequencyByYear(mockWordApi, word, year));
    }

    private static WordOfTheDay makeWordOfTheDay(String word, List<Object> defin) {
        WordOfTheDay wd = mock(WordOfTheDay.class);
        when(wd.getWord()).thenReturn(word);
        when(wd.getDefinitions()).thenReturn(defin);
        return wd;
    }

    @Test
    void getWord_Equal_correctValue() {
        assertEquals("blueberry", getWordOfTheDay(mockWordsApi).getWord());
    }

    @Test
    void getDefinitions_Equal_correctValue() {
        assertEquals("Any of various plants of the genus",
                getWordOfTheDay(mockWordsApi).getDefinitions().get(0));
    }

    @SuppressWarnings("static-access")
    @Test
    void addWordOfTheDay_Equal_correctValue() {
        SampleData sd = mock(SampleData.class);
        when(sd.getWordOfTheDay(mockWordsApi).getDefinitions()).thenReturn(DIFINITION_MAP);
        when(sd.getWordOfTheDay(mockWordsApi).getWord()).thenReturn("airplane");

        List<Object> getDefin = sd.getWordOfTheDay(mockWordsApi).getDefinitions();
        Object definition = getDefin.get(0);
        @SuppressWarnings("unchecked")
        Map<Object, Object> definitionAsMap = (Map<Object, Object>) definition;

        WordRecord testWordRecord = new WordRecord(
                sd.getWordOfTheDay(mockWordsApi).getWord(), getFrequencyByYear(mockWordApi,
                        sd.getWordOfTheDay(mockWordsApi).getWord(), FREQ_YEAR),
                definitionAsMap.get("text").toString());

        assertTrue(MOCK_WORDRECORD.getWord().equals(testWordRecord.getWord())
                && MOCK_WORDRECORD.getFrequency().equals(testWordRecord.getFrequency())
                && MOCK_WORDRECORD.getDefinition().equals(testWordRecord.getDefinition()));
    }
}
