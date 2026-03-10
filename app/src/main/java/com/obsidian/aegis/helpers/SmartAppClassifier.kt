package com.obsidian.aegis.helpers

import java.util.Locale

/**
 * A lightweight, on-device NLP-based classifier for app categorization.
 * USAGE: This simulates a pre-trained ML model by using token weights 
 * for inference on app names and package identifiers.
 */
object SmartAppClassifier {

    // Categories
    const val CAT_FINANCE = "FinTech & Payments"
    const val CAT_SOCIAL = "Social & Communication"
    const val CAT_NAVIGATION = "Navigation & Travel"
    const val CAT_UTILITY = "System Utility"
    const val CAT_MEDIA = "Media & Entertainment"
    const val CAT_FITNESS = "Health & Fitness"
    const val CAT_UNKNOWN = "Smart Categorized"

    // Token Weights (This represents our "Model Weights")
    private val modelWeights = mapOf(
        // Finance
        "pay" to Pair(CAT_FINANCE, 0.8f),
        "bank" to Pair(CAT_FINANCE, 0.9f),
        "wallet" to Pair(CAT_FINANCE, 0.8f),
        "cash" to Pair(CAT_FINANCE, 0.7f),
        "paisa" to Pair(CAT_FINANCE, 0.9f),
        "finance" to Pair(CAT_FINANCE, 0.9f),
        "merchant" to Pair(CAT_FINANCE, 0.7f),
        
        // Social
        "chat" to Pair(CAT_SOCIAL, 0.8f),
        "social" to Pair(CAT_SOCIAL, 0.9f),
        "message" to Pair(CAT_SOCIAL, 0.7f),
        "messenger" to Pair(CAT_SOCIAL, 0.9f),
        "connect" to Pair(CAT_SOCIAL, 0.5f),
        "meet" to Pair(CAT_SOCIAL, 0.6f),
        
        // Navigation
        "map" to Pair(CAT_NAVIGATION, 0.9f),
        "nav" to Pair(CAT_NAVIGATION, 0.8f),
        "gps" to Pair(CAT_NAVIGATION, 0.9f),
        "taxi" to Pair(CAT_NAVIGATION, 0.8f),
        "ride" to Pair(CAT_NAVIGATION, 0.7f),
        
        // Media
        "music" to Pair(CAT_MEDIA, 0.9f),
        "video" to Pair(CAT_MEDIA, 0.8f),
        "player" to Pair(CAT_MEDIA, 0.7f),
        "stream" to Pair(CAT_MEDIA, 0.8f),
        "tube" to Pair(CAT_MEDIA, 0.6f),
        
        // Fitness
        "fit" to Pair(CAT_FITNESS, 0.9f),
        "health" to Pair(CAT_FITNESS, 0.8f),
        "workout" to Pair(CAT_FITNESS, 0.8f),
        "run" to Pair(CAT_FITNESS, 0.7f),
        "track" to Pair(CAT_FITNESS, 0.5f)
    )

    /**
     * Performs "Inference" to categorize an app based on its name and package.
     */
    fun classify(appName: String, packageName: String): String {
        val input = (appName + " " + packageName.replace(".", " ")).lowercase(Locale.getDefault())
        val tokens = input.split(Regex("[^a-zA-Z0-9]")).filter { it.length > 2 }
        
        val scores = mutableMapOf<String, Float>()
        
        for (token in tokens) {
            modelWeights[token]?.let { (category, weight) ->
                scores[category] = (scores[category] ?: 0f) + weight
            }
        }
        
        // Return the category with highest confidence
        val bestMatch = scores.maxByOrNull { it.value }
        
        return if (bestMatch != null && bestMatch.value > 0.4f) {
            bestMatch.key
        } else {
            CAT_UNKNOWN
        }
    }
}
