package com.ragflow4j.core.embedding.onnx;

import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OnnxEmbeddingModelTest {

    private OnnxEmbeddingModel embeddingModel;
    private Path mockModelPath;
    private Path mockTokenizerPath;
    private InputStream mockTokenizerStream;

    @Test
    void testEmbed() throws OrtException {
        String modelPath = getClass().getClassLoader().getResource("e5-small-v2-q.onnx").getPath();
        String tokenizerPath = getClass().getClassLoader().getResource("e5-small-v2-q-tokenizer.json").getPath();
        PoolingMode poolingMode = PoolingMode.MEAN;
        OnnxEmbeddingModel embeddingModel = new OnnxEmbeddingModel(modelPath, tokenizerPath, poolingMode);
        float[] result = embeddingModel.embed("test text");
        assertEquals(384, result.length);
    }

    @Test
    void testEmbedBatch() throws OrtException {
        String modelPath = getClass().getClassLoader().getResource("e5-small-v2-q.onnx").getPath();
        String tokenizerPath = getClass().getClassLoader().getResource("e5-small-v2-q-tokenizer.json").getPath();
        PoolingMode poolingMode = PoolingMode.MEAN;
        OnnxEmbeddingModel embeddingModel = new OnnxEmbeddingModel(modelPath, tokenizerPath, poolingMode);
        List<String> texts = Arrays.asList("text1", "text2");
        
        List<float[]> results = embeddingModel.embedBatch(texts);
        assertEquals(2, results.size());
        assertEquals(384, results.get(0).length);
        assertEquals(384, results.get(1).length);
    }
}