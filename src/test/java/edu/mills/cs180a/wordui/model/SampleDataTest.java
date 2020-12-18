package edu.mills.cs180a.wordui.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import edu.mills.cs180a.wordnik.client.api.WordApi;
import edu.mills.cs180a.wordnik.client.api.WordsApi;
import edu.mills.cs180a.wordnik.client.invoker.ApiClient;
import edu.mills.cs180a.wordnik.client.model.WordOfTheDay;
import edu.mills.cs180a.wordnik.client.model.FrequencySummary;

class SampleDataTest {
	private final WordApi mockWordApi = mock(WordApi.class);
	private final WordOfTheDay mockWordOfTheDay = mock(WordOfTheDay.class);
	private final WordsApi mockWordsApi = mock(WordsApi.class);
	private final ApiClient mockApiClient = mock(ApiClient.class);
	private static final Map<String, FrequencySummary> FREQS_MAP = Map.of("dog",
			makeFrequencySummary(List.of(makeMap(2000, 12), makeMap(2020, 34))), "apple",
			makeFrequencySummary(List.of(makeMap(2000, 339), makeMap(2001, 464))), "orange",
			makeFrequencySummary(List.of(makeMap(2000, 774), makeMap(2001, 941))));
	private static final Map<Object, Object> DEFINITION = Map.of("source", "my dog", "text",
			"An animal you take on walks.", "note", "best note ever", "PartOfSpeech", "noun");
	private static final List<Object> DEFINITIONS = makeDefinitions(DEFINITION);
	private static final String WORD = "dog";

	@BeforeEach
	void setup() {
		when(mockWordApi.getWordFrequency(anyString(), anyString(), anyInt(), anyInt()))
				.thenAnswer(invocation -> FREQS_MAP.get(invocation.getArgument(0)));
		when(mockWordsApi.getWordOfTheDay()).thenReturn(mockWordOfTheDay);
		when(mockWordOfTheDay.getWord()).thenReturn(WORD);
		when(mockWordOfTheDay.getDefinitions()).thenReturn(DEFINITIONS);
	}

	private static Map<Object, Object> makeMap(int year, int count) {
		return Map.of(SampleData.FREQ_YEAR_KEY, String.valueOf(year), SampleData.FREQ_COUNT_KEY, count);
	}

	private static FrequencySummary makeFrequencySummary(List<Object> freqs) {
		FrequencySummary fs = mock(FrequencySummary.class);
		when(fs.getFrequency()).thenReturn(freqs);
		return fs;
	}

	private static List<Object> makeDefinitions(Map<Object, Object> definition) {
		return List.of(definition);
	}

	@ParameterizedTest
	@CsvSource({ "dog, 2000, 12", "dog, 2020, 34", "apple,2000,339", "apple,2001,464", "apple,2020,0", "orange,2000,774", "orange,2001,941",
			"orange,2050,0" })
	void testGetFrequencyFromSummary(String word, int year, int count) {
		assertEquals(count, SampleData.getFrequencyByYear(mockWordApi, word, year));
	}

	@Test
	void testGetWordOfTheDay() {
		String w = "dog";
		WordOfTheDay wotd = SampleData.getWordOfTheDay(mockWordsApi);
		assertEquals(w, wotd.getWord());
	}

	@SuppressWarnings("unchecked")
	@ParameterizedTest
	@CsvSource({ "source, my dog", "text, An animal you take on walks.", "note, best note ever", "PartOfSpeech, noun" })
	void testGetDefinitions(String key, String value) {
		WordOfTheDay wotd = SampleData.getWordOfTheDay(mockWordsApi);
		String returnValue = ((Map<String, String>) wotd.getDefinitions().get(0)).get(key);
		assertEquals(value, returnValue);
	}

	@ParameterizedTest
	@CsvSource({ "dog" })
	void testAddWordOfTheDay(String s) {
		SampleData.client = mockApiClient;
		when(SampleData.client.buildClient(WordApi.class)).thenReturn(mockWordApi);
		LinkedList<WordRecord> list = new LinkedList<WordRecord>();
		WordOfTheDay wotd = SampleData.getWordOfTheDay(mockWordsApi);
		SampleData.addWordOfTheDay(list, wotd);

		assertEquals(s, list.get(0).getWord());
		assertEquals(1, list.size());
	}

}
