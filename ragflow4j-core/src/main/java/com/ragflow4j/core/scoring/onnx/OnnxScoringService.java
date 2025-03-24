package com.ragflow4j.core.scoring.onnx;

import ai.onnxruntime.OrtSession;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.ragflow4j.core.scoring.AbstractScoringService;
import com.ragflow4j.core.scoring.ScoringResult;

/**
 * 基于ONNX模型的评分服务实现
 */
public class OnnxScoringService extends AbstractScoringService {
    private static final int DEFAULT_MODEL_MAX_LENGTH = 510; // 512 - 2 (special tokens [CLS] and [SEP])

    private static final boolean DEFAULT_NORMALIZE = false;

    private final OnnxScoringBertCrossEncoder onnxBertBiEncoder;

    public OnnxScoringService(String pathToModel, String pathToTokenizer) {
        this.onnxBertBiEncoder = loadFromFileSystem(pathToModel, new OrtSession.SessionOptions(), pathToTokenizer, DEFAULT_MODEL_MAX_LENGTH, DEFAULT_NORMALIZE);
    }

    public OnnxScoringService(String pathToModel, OrtSession.SessionOptions options, String pathToTokenizer) {
        this.onnxBertBiEncoder = loadFromFileSystem(pathToModel, options, pathToTokenizer, DEFAULT_MODEL_MAX_LENGTH, DEFAULT_NORMALIZE);
    }

    public OnnxScoringService(String pathToModel, String pathToTokenizer, int modelMaxLength) {
        this.onnxBertBiEncoder = loadFromFileSystem(pathToModel, new OrtSession.SessionOptions(), pathToTokenizer, modelMaxLength, DEFAULT_NORMALIZE);
    }

    public OnnxScoringService(String pathToModel, OrtSession.SessionOptions options, String pathToTokenizer, int modelMaxLength, boolean normalize) {
        this.onnxBertBiEncoder = loadFromFileSystem(pathToModel, options, pathToTokenizer, modelMaxLength, normalize);
    }

    protected OnnxScoringBertCrossEncoder model() {
        return this.onnxBertBiEncoder;
    }

    static OnnxScoringBertCrossEncoder loadFromFileSystem(String pathToModel, OrtSession.SessionOptions options, String pathToTokenizer, int modelMaxLength, boolean normalize) {
        try {
            return new OnnxScoringBertCrossEncoder(pathToModel, options, pathToTokenizer, modelMaxLength, normalize);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String generateCacheKey(String query, List<String> documents) {
        return String.format("%s_%d", query.hashCode(), documents.hashCode());
    }

    @Override
    protected List<ScoringResult> computeScores(String query, List<String> documents) {
        try {
            OnnxScoringBertCrossEncoder.ScoringAndTokenCount scoresAndTokenCount = this.model().scoreAll(query, documents);
            List<Double> scores = scoresAndTokenCount.scores;
            return IntStream.range(0, documents.size())
                    .mapToObj(i -> new ScoringResult(
                            documents.get(i),
                            scores.get(i)
                    ))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("ONNX模型推理失败", e);
        }
    }

    @Override
    public void clearCache() {
        super.clearCache();
    }
}