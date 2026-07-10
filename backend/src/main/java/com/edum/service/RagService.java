package com.edum.service;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RagService {

    private static final int CHUNK_SIZE = 300;
    private final List<Chunk> chunks = new ArrayList<>();

    @PostConstruct
    public void loadKnowledge() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:knowledge/*.md");

            for (Resource resource : resources) {
                String filename = resource.getFilename();
                if (filename == null) continue;

                String text;
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                    text = reader.lines().collect(Collectors.joining("\n"));
                }

                // split into chunks by ## headings or CHUNK_SIZE lines
                String[] sections = text.split("(?=^## )");
                for (String section : sections) {
                    section = section.trim();
                    if (section.isEmpty()) continue;

                    if (section.split("\n").length > CHUNK_SIZE) {
                        String[] lines = section.split("\n");
                        StringBuilder buf = new StringBuilder();
                        for (String line : lines) {
                            buf.append(line).append("\n");
                            if (buf.toString().split("\n").length >= CHUNK_SIZE) {
                                chunks.add(new Chunk(buf.toString(), filename));
                                buf = new StringBuilder();
                            }
                        }
                        if (!buf.isEmpty()) {
                            chunks.add(new Chunk(buf.toString(), filename));
                        }
                    } else {
                        chunks.add(new Chunk(section, filename));
                    }
                }
            }
            System.out.println("[RAG] Loaded " + chunks.size() + " knowledge chunks from " + resources.length + " files");
        } catch (Exception e) {
            System.err.println("[RAG] Failed to load knowledge: " + e.getMessage());
        }
    }

    public String retrieveContext(String query, int maxChunks) {
        if (chunks.isEmpty() || query == null || query.isBlank()) {
            return "";
        }

        String q = query.toLowerCase();
        String[] queryWords = q.split("\\s+");

        // score each chunk by keyword overlap + heading match
        List<ScoredChunk> scored = new ArrayList<>();
        for (Chunk chunk : chunks) {
            double score = 0;
            String chunkLower = chunk.text.toLowerCase();

            // keyword matching
            for (String word : queryWords) {
                if (word.length() < 2) continue;
                if (chunkLower.contains(word)) {
                    score += 1.0;
                }
            }

            // heading match bonus
            String firstLine = chunk.text.split("\n")[0].toLowerCase();
            for (String word : queryWords) {
                if (word.length() < 2) continue;
                if (firstLine.contains(word)) {
                    score += 3.0;
                }
            }

            if (score > 0) {
                scored.add(new ScoredChunk(chunk, score));
            }
        }

        // sort by score desc
        scored.sort((a, b) -> Double.compare(b.score, a.score));

        // take top chunks
        StringBuilder context = new StringBuilder();
        int count = 0;
        for (ScoredChunk sc : scored) {
            if (count >= maxChunks) break;
            context.append("---\n").append(sc.chunk.text).append("\n");
            count++;
        }

        return context.toString();
    }

    public String buildPrompt(String userMessage) {
        String context = retrieveContext(userMessage, 5);
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an AI assistant for the EDUM teaching management system.\n");
        prompt.append("Answer questions based on the following knowledge base context.\n");
        prompt.append("If the answer cannot be found in the context, say so.\n");
        prompt.append("Keep answers concise and technical.\n\n");

        if (!context.isEmpty()) {
            prompt.append("=== Knowledge Context ===\n");
            prompt.append(context);
            prompt.append("=== End of Context ===\n\n");
        }

        prompt.append("User question: ").append(userMessage);
        return prompt.toString();
    }

    private static class Chunk {
        final String text;
        final String source;

        Chunk(String text, String source) {
            this.text = text;
            this.source = source;
        }
    }

    private static class ScoredChunk {
        final Chunk chunk;
        final double score;

        ScoredChunk(Chunk chunk, double score) {
            this.chunk = chunk;
            this.score = score;
        }
    }
}
