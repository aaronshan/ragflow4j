package com.ragflow4j.core.embedding.onnx;

import com.ragflow4j.core.embedding.DocumentEmbedding;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static com.ragflow4j.core.utils.Utils.getOrDefault;
import static com.ragflow4j.core.utils.ValidationUtils.ensureNotEmpty;
import static java.nio.file.Files.newInputStream;
import static java.util.Collections.singletonList;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;

public abstract class AbstractInProcessEmbeddingModel implements DocumentEmbedding {

    private final Executor executor;

    protected AbstractInProcessEmbeddingModel(Executor executor) {
        this.executor = getOrDefault(executor, this::createDefaultExecutor);
    }

    private Executor createDefaultExecutor() {
        int threadPoolSize = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                threadPoolSize, threadPoolSize,
                1, SECONDS,
                new LinkedBlockingQueue<>()
        );
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        return threadPoolExecutor;
    }

    protected static OnnxBertBiEncoder loadFromJar(String modelFileName, String tokenizerFileName, PoolingMode poolingMode) {
        InputStream model = Thread.currentThread().getContextClassLoader().getResourceAsStream(modelFileName);
        InputStream tokenizer = Thread.currentThread().getContextClassLoader().getResourceAsStream(tokenizerFileName);
        return new OnnxBertBiEncoder(model, tokenizer, poolingMode);
    }

    protected static OnnxBertBiEncoder loadFromFileSystem(Path pathToModel, Path pathToTokenizer, PoolingMode poolingMode) {
        try {
            return new OnnxBertBiEncoder(newInputStream(pathToModel), newInputStream(pathToTokenizer), poolingMode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static OnnxBertBiEncoder loadFromFileSystem(Path pathToModel, InputStream tokenizer, PoolingMode poolingMode) {
        try {
            return new OnnxBertBiEncoder(newInputStream(pathToModel), tokenizer, poolingMode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract OnnxBertBiEncoder model();

    @Override
    public float[] embed(String text) {
        OnnxBertBiEncoder.EmbeddingAndTokenCount embeddingAndTokenCount = model().embed(text);
        return embeddingAndTokenCount.embedding;
    }

    @Override
    public CompletableFuture<float[]> embedAsync(String text) {
        return supplyAsync(() -> embed(text), executor);
    }


    @Override
    public List<float[]> embedBatch(List<String> texts) {
        ensureNotEmpty(texts, "texts");
        if (texts.size() == 1) {
            return embedInTheSameThread(texts.get(0));
        } else {
            return parallelizeEmbedding(texts);
        }
    }

    @Override
    public CompletableFuture<List<float[]>> embedBatchAsync(List<String> texts) {
        return CompletableFuture.supplyAsync(() -> embedBatch(texts), executor);
    }

    private List<float[]> embedInTheSameThread(String text) {
        OnnxBertBiEncoder.EmbeddingAndTokenCount embeddingAndTokenCount = model().embed(text);
        return singletonList(embeddingAndTokenCount.embedding);
    }

    private List<float[]> parallelizeEmbedding(List<String> texts) {
        List<CompletableFuture<OnnxBertBiEncoder.EmbeddingAndTokenCount>> futures = texts.stream()
                .map(text -> supplyAsync(() -> model().embed(text), executor))
                .collect(toList());

        int inputTokenCount = 0;
        List<float[]> embeddings = new ArrayList<>();

        for (CompletableFuture<OnnxBertBiEncoder.EmbeddingAndTokenCount> future : futures) {
            try {
                OnnxBertBiEncoder.EmbeddingAndTokenCount embeddingAndTokenCount = future.get();
                embeddings.add(embeddingAndTokenCount.embedding);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        return embeddings;
    }
}
