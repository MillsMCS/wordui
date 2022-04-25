package edu.mills.cs180a.wordui;

import java.util.*;
import edu.mills.cs180a.wordnik.client.api.*;
import edu.mills.cs180a.wordnik.client.model.*;

public class MockWordApi implements WordApi {
    private class MockFrequencySummary extends FrequencySummary {
        // These should be returned by getFrequency() for "apple" and "orange"
        private static final List<Object> APPLE_FREQUENCIES =
                List.of(makeMap(2000, 339), makeMap(2001, 464), makeMap(2020, 0));
        private static final List<Object> ORANGE_FREQUENCIES =
                List.of(makeMap(2000, 774), makeMap(2001, 941));

        private static Map<Object, Object> makeMap(int year, int count) {
            return Map.of(WorduiWordnikClient.FREQ_YEAR_KEY, String.valueOf(year),
                    WorduiWordnikClient.FREQ_COUNT_KEY, count);
        }

        private String word;

        private MockFrequencySummary(String word) {
            this.word = word;
        }

        @Override
        public List<Object> getFrequency() {
            return switch (word) {
                case "apple" -> APPLE_FREQUENCIES;
                case "orange" -> ORANGE_FREQUENCIES;
                default -> new ArrayList<Object>();
            };
        }
    }

    @Override
    public FrequencySummary getWordFrequency(String word, String useCanonical, Integer startYear,
            Integer endYear) {
        return new MockFrequencySummary(word);
    }

    @Override
    public List<AudioFile> getAudio(String word, String useCanonical, Integer limit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<AudioFile> getAudio(String word, Map<String, Object> queryParams) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Definition> getDefinitions(String word, Integer limit, String partOfSpeech,
            String includeRelated, List<String> sourceDictionaries, String useCanonical,
            String includeTags) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Definition> getDefinitions(String word, Map<String, Object> queryParams) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getEtymologies(String word, String useCanonical) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getEtymologies(String word, Map<String, Object> queryParams) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ExampleSearchResults getExamples(String word, String includeDuplicates,
            String useCanonical, Integer skip, Integer limit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ExampleSearchResults getExamples(String word, Map<String, Object> queryParams) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Syllable> getHyphenation(String word, String useCanonical, String sourceDictionary,
            Integer limit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Syllable> getHyphenation(String word, Map<String, Object> queryParams) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Bigram> getPhrases(String word, Integer limit, Integer wlmi, String useCanonical) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Bigram> getPhrases(String word, Map<String, Object> queryParams) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Related> getRelatedWords(String word, String useCanonical, String relationshipTypes,
            Integer limitPerRelationshipType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Related> getRelatedWords(String word, Map<String, Object> queryParams) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long getScrabbleScore(String word) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<TextPron> getTextPronunciations(String word, String useCanonical,
            String sourceDictionary, String typeFormat, Integer limit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<TextPron> getTextPronunciations(String word, Map<String, Object> queryParams) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Example getTopExample(String word, String useCanonical) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Example getTopExample(String word, Map<String, Object> queryParams) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FrequencySummary getWordFrequency(String word, Map<String, Object> queryParams) {
        // TODO Auto-generated method stub
        return null;
    }

}
