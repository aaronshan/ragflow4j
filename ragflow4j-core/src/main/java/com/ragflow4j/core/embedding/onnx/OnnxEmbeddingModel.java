package com.ragflow4j.core.embedding.onnx;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executor;

import static com.ragflow4j.core.utils.ValidationUtils.ensureNotNull;


/**
 * An embedding model that runs within your Java application's process
 * using <a href="https://onnxruntime.ai/">ONNX runtime</a>.
 * <br>
 * Many models (e.g., from <a href="https://huggingface.co/">HuggingFace</a>) can be used,
 * as long as they are in the ONNX format.
 * <br>
 * Information on how to convert models into ONNX format can be found
 * <a href="https://huggingface.co/docs/optimum/exporters/onnx/usage_guides/export_a_model">here</a>.
 * <br>
 * Many models already converted to ONNX format are available <a href="https://huggingface.co/Xenova">here</a>.
 */
public class OnnxEmbeddingModel extends AbstractInProcessEmbeddingModel {
    @Override
    public String getModelName() {
        return "onnx-embedding-model";
    }

    @Override
    public int getDimension() {
        return embed("test").length;
    }

    private final OnnxBertBiEncoder onnxBertBiEncoder;

    /**
     * @param pathToModel     The path to the modelPath file (e.g., "/path/to/model.onnx")
     * @param pathToTokenizer The path to the tokenizer file (e.g., "/path/to/tokenizer.json")
     * @param poolingMode     The pooling model to use. Can be found in the ".../1_Pooling/config.json" file on HuggingFace.
     *                        Here is an <a href="https://huggingface.co/sentence-transformers/all-MiniLM-L6-v2/blob/main/1_Pooling/config.json">example</a>.
     *                        {@code "pooling_mode_mean_tokens": true} means that {@link PoolingMode#MEAN} should be used.
     */
    public OnnxEmbeddingModel(Path pathToModel, Path pathToTokenizer, PoolingMode poolingMode) {
        super(null);
        this.onnxBertBiEncoder = loadFromFileSystem(pathToModel, pathToTokenizer, poolingMode);
    }

    /**
     * @param pathToModel     The path to the modelPath file (e.g., "/path/to/model.onnx")
     * @param pathToTokenizer The path to the tokenizer file (e.g., "/path/to/tokenizer.json")
     * @param poolingMode     The pooling model to use. Can be found in the ".../1_Pooling/config.json" file on HuggingFace.
     *                        Here is an <a href="https://huggingface.co/sentence-transformers/all-MiniLM-L6-v2/blob/main/1_Pooling/config.json">example</a>.
     *                        {@code "pooling_mode_mean_tokens": true} means that {@link PoolingMode#MEAN} should be used.
     * @param executor        The executor to use to parallelize the embedding process.
     */
    public OnnxEmbeddingModel(Path pathToModel, Path pathToTokenizer, PoolingMode poolingMode, Executor executor) {
        super(ensureNotNull(executor, "executor"));
        this.onnxBertBiEncoder = loadFromFileSystem(pathToModel, pathToTokenizer, poolingMode);
    }

    /**
     * @param pathToModel     The path to the model file (e.g., "/home/me/model.onnx")
     * @param pathToTokenizer The path to the tokenizer file (e.g., "/path/to/tokenizer.json")
     * @param poolingMode     The pooling model to use. Can be found in the ".../1_Pooling/config.json" file on HuggingFace.
     *                        Here is an <a href="https://huggingface.co/sentence-transformers/all-MiniLM-L6-v2/blob/main/1_Pooling/config.json">example</a>.
     *                        {@code "pooling_mode_mean_tokens": true} means that {@link PoolingMode#MEAN} should be used.
     */
    public OnnxEmbeddingModel(String pathToModel, String pathToTokenizer, PoolingMode poolingMode) {
        this(Paths.get(pathToModel), Paths.get(pathToTokenizer), poolingMode);
    }

    /**
     * @param pathToModel     The path to the model file (e.g., "/home/me/model.onnx")
     * @param pathToTokenizer The path to the tokenizer file (e.g., "/path/to/tokenizer.json")
     * @param poolingMode     The pooling model to use. Can be found in the ".../1_Pooling/config.json" file on HuggingFace.
     *                        Here is an <a href="https://huggingface.co/sentence-transformers/all-MiniLM-L6-v2/blob/main/1_Pooling/config.json">example</a>.
     *                        {@code "pooling_mode_mean_tokens": true} means that {@link PoolingMode#MEAN} should be used.
     * @param executor        The executor to use to parallelize the embedding process.
     */
    public OnnxEmbeddingModel(String pathToModel, String pathToTokenizer, PoolingMode poolingMode, Executor executor) {
        this(Paths.get(pathToModel), Paths.get(pathToTokenizer), poolingMode, executor);
    }

    /**
     * @param pathToModel The path to the modelPath file (e.g., "/path/to/model.onnx")
     * @deprecated Use {@link OnnxEmbeddingModel#OnnxEmbeddingModel(Path, Path, PoolingMode)} or
     * {@link OnnxEmbeddingModel#OnnxEmbeddingModel(String, String, PoolingMode)} instead.
     */
    @Deprecated
    public OnnxEmbeddingModel(Path pathToModel) {
        super(null);
        this.onnxBertBiEncoder = loadFromFileSystem(
                pathToModel,
                OnnxEmbeddingModel.class.getResourceAsStream("/bert-tokenizer.json"),
                PoolingMode.MEAN
        );
    }

    /**
     * @param pathToModel The path to the modelPath file (e.g., "/path/to/model.onnx")
     * @deprecated Use {@link OnnxEmbeddingModel#OnnxEmbeddingModel(Path, Path, PoolingMode)} or
     * {@link OnnxEmbeddingModel#OnnxEmbeddingModel(String, String, PoolingMode)} instead.
     */
    @Deprecated
    public OnnxEmbeddingModel(String pathToModel) {
        this(Paths.get(pathToModel));
    }

    @Override
    protected OnnxBertBiEncoder model() {
        return onnxBertBiEncoder;
    }
}
