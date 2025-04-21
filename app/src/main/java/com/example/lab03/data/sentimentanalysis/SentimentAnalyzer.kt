package com.example.text_classification.data.sentimentanalysis

import android.content.Context
import com.google.mediapipe.tasks.text.textclassifier.TextClassifier
import com.google.mediapipe.tasks.text.textclassifier.TextClassifierResult
import com.google.mediapipe.tasks.core.BaseOptions


class SentimentAnalyzer(context: Context) {
    private val currentModel = "sentiment_analysis.tflite"
    private val options = TextClassifier.TextClassifierOptions.builder()
        .setBaseOptions(
            BaseOptions.builder()
                .setModelAssetPath(currentModel)
                .build()
        )
        .build()
    private val textClassifier: TextClassifier = TextClassifier.createFromOptions(context, options)
    fun analyze(text: String): String {
        val results: TextClassifierResult = textClassifier.classify(text)
        val categories = results.classificationResult().classifications().firstOrNull()?.categories()
        return if (!categories.isNullOrEmpty()) {
            "${categories[0].categoryName()} ${categories[0].displayName()}"
        } else {
            "Unknown"
        }
    }
}