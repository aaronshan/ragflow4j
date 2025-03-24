package com.ragflow4j.core.scoring.onnx;

import ai.onnxruntime.OrtSession;
import com.ragflow4j.core.scoring.ScoringResult;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Percentage.withPercentage;

class OnnxScoringServiceTest {

    @Test
    void testSuccessfulScoring() throws ExecutionException, InterruptedException {
        String modelPath = getClass().getClassLoader().getResource("ms-marco-MiniLM-L-6-v2.onnx").getPath();
        String tokenizerPath = getClass().getClassLoader().getResource("ms-marco-MiniLM-L-6-v2-tokenizer.json").getPath();
        OnnxScoringService scoringService = new OnnxScoringService(modelPath, new OrtSession.SessionOptions(),  tokenizerPath, 512, false);

        List<String> documents = Arrays.asList(
            "Berlin has a population of 3,520,031 registered inhabitants in an area of 891.82 square kilometers.", 
            "New York City is famous for the Metropolitan Museum of Art."
        );

        List<ScoringResult> results = scoringService.score("How many people live in Berlin?", documents).get();

        assertEquals(2, results.size());
        assertEquals(documents.get(0), results.get(0).getDocument());
        assertThat(results.get(0).getScore()).isCloseTo(8.663132667541504, withPercentage(0.1));
        assertEquals(documents.get(1), results.get(1).getDocument());
        assertThat(results.get(1).getScore()).isCloseTo(-11.245542526245117, withPercentage(0.1));

    }
}