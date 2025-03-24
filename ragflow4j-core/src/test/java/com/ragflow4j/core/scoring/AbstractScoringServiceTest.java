package com.ragflow4j.core.scoring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class AbstractScoringServiceTest {

    private TestScoringService scoringService;

    @BeforeEach
    void setUp() {
        scoringService = new TestScoringService();
    }

    @Test
    void testScore() throws ExecutionException, InterruptedException {
        String query = "test query";
        List<String> documents = Arrays.asList("doc1", "doc2");

        CompletableFuture<List<ScoringResult>> future = scoringService.score(query, documents);
        List<ScoringResult> results = future.get();

        assertEquals(2, results.size());
        assertEquals("doc1", results.get(0).getDocument());
        assertEquals(1.0, results.get(0).getScore());
        assertEquals("doc2", results.get(1).getDocument());
        assertEquals(0.5, results.get(1).getScore());
    }

    @Test
    void testBatchScore() throws ExecutionException, InterruptedException {
        List<String> queries = Arrays.asList("query1", "query2");
        List<String> documents = Arrays.asList("doc1", "doc2");

        CompletableFuture<List<List<ScoringResult>>> future = scoringService.batchScore(queries, documents);
        List<List<ScoringResult>> results = future.get();

        assertEquals(2, results.size());
        assertEquals(2, results.get(0).size());
        assertEquals(2, results.get(1).size());
    }

    @Test
    void testCacheReuse() throws ExecutionException, InterruptedException {
        String query = "test query";
        List<String> documents = Arrays.asList("doc1", "doc2");

        CompletableFuture<List<ScoringResult>> future1 = scoringService.score(query, documents);
        CompletableFuture<List<ScoringResult>> future2 = scoringService.score(query, documents);

        assertSame(future1, future2, "应该返回相同的Future对象（使用缓存）");
    }

    @Test
    void testClearCache() throws ExecutionException, InterruptedException {
        String query = "test query";
        List<String> documents = Arrays.asList("doc1", "doc2");

        CompletableFuture<List<ScoringResult>> future1 = scoringService.score(query, documents);
        scoringService.clearCache();
        CompletableFuture<List<ScoringResult>> future2 = scoringService.score(query, documents);

        assertNotSame(future1, future2, "清除缓存后应该返回新的Future对象");
    }

    private static class TestScoringService extends AbstractScoringService {
        @Override
        protected String generateCacheKey(String query, List<String> documents) {
            return query + "_" + documents.hashCode();
        }

        @Override
        protected List<ScoringResult> computeScores(String query, List<String> documents) {
            return Arrays.asList(
                new ScoringResult(documents.get(0), 1.0),
                new ScoringResult(documents.get(1), 0.5)
            );
        }
    }
}