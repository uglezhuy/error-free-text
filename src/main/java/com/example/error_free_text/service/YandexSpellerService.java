package com.example.error_free_text.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class YandexSpellerService {

    private static final String YANDEX_URL = "https://speller.yandex.net/services/spellservice.json/checkTexts";

    private static final int MAX_TEXT_LENGTH = 10_000;

    private static final int IGNORE_DIGITS = 2;
    private static final int IGNORE_URLS = 4;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public YandexSpellerService(ObjectMapper objectMapper) {

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

        requestFactory.setConnectTimeout(Duration.ofSeconds(5));
        requestFactory.setReadTimeout(Duration.ofSeconds(10));

        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .build();

        this.objectMapper = objectMapper;
    }

    public String correctText(String text, String language) throws Exception {

        List<String> parts = splitText(text);

        int options = calculateOptions(text);

        List<List<YandexError>> allErrors = checkTexts(
                parts,
                language.toLowerCase(),
                options);

        if (allErrors.size() != parts.size()) {
            throw new RuntimeException(
                    "Yandex Speller returned an unexpected number of results");
        }

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < parts.size(); i++) {

            String correctedPart = applyCorrections(
                    parts.get(i),
                    allErrors.get(i));

            result.append(correctedPart);
        }

        return result.toString();
    }

    private List<List<YandexError>> checkTexts(
            List<String> parts,
            String language,
            int options) throws Exception {

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        for (String part : parts) {
            formData.add("text", part);
        }

        formData.add("lang", language);
        formData.add("options", String.valueOf(options));
        formData.add("format", "plain");

        String response = restClient.post()
                .uri(YANDEX_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .body(String.class);

        if (response == null || response.isBlank()) {
            throw new RuntimeException(
                    "Yandex Speller returned empty response");
        }

        return objectMapper.readValue(
                response,
                new TypeReference<List<List<YandexError>>>() {
                });
    }

    private int calculateOptions(String text) {

        int options = 0;

        if (Pattern.compile("\\d").matcher(text).find()) {
            options += IGNORE_DIGITS;
        }

        if (containsUrl(text)) {
            options += IGNORE_URLS;
        }

        return options;
    }

    private boolean containsUrl(String text) {

        return Pattern.compile(
                "(https?://|www\\.|[\\w.-]+\\.[a-zA-Z]{2,})").matcher(text).find();
    }

    private List<String> splitText(String text) {

        List<String> parts = new ArrayList<>();

        int start = 0;

        while (start < text.length()) {

            int end = Math.min(
                    start + MAX_TEXT_LENGTH,
                    text.length());

            if (end < text.length()) {

                int lastSpace = text.lastIndexOf(' ', end);

                if (lastSpace > start) {
                    end = lastSpace + 1;
                }
            }

            parts.add(text.substring(start, end));

            start = end;
        }

        return parts;
    }

    private String applyCorrections(
            String text,
            List<YandexError> errors) {

        if (errors == null || errors.isEmpty()) {
            return text;
        }

        errors.sort(
                Comparator.comparingInt(YandexError::getPos).reversed());

        StringBuilder result = new StringBuilder(text);

        for (YandexError error : errors) {

            if (error.getSuggestions() == null
                    || error.getSuggestions().isEmpty()) {
                continue;
            }

            int start = error.getPos();
            int end = start + error.getLen();

            if (start < 0 || end > result.length()) {
                continue;
            }

            String replacement = error.getSuggestions().get(0);

            result.replace(
                    start,
                    end,
                    replacement);
        }

        return result.toString();
    }

    public static class YandexError {

        private int code;
        private int pos;
        private int row;
        private int col;
        private int len;
        private String word;
        private List<String> s;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public int getPos() {
            return pos;
        }

        public void setPos(int pos) {
            this.pos = pos;
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public int getCol() {
            return col;
        }

        public void setCol(int col) {
            this.col = col;
        }

        public int getLen() {
            return len;
        }

        public void setLen(int len) {
            this.len = len;
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public List<String> getSuggestions() {
            return s;
        }

        public void setS(List<String> s) {
            this.s = s;
        }
    }
}