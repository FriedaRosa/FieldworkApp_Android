package com.example.fieldworkdatamask

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await

class MlKitOcrService(private val context: Context) {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun extractText(imageUri: Uri): OcrResult {
        return try {
            val image = InputImage.fromFilePath(context, imageUri)
            val result = recognizer.process(image).await()
            OcrResult(
                fullText = result.text,
                blocks = result.textBlocks.map { block ->
                    TextBlockData(
                        text = block.text,
                        boundingBox = block.boundingBox?.let { 
                            listOf(it.left, it.top, it.right, it.bottom)
                        } ?: emptyList()
                    )
                }
            )
        } catch (e: Exception) {
            OcrResult(fullText = "", error = e.message)
        }
    }
}

data class OcrResult(
    val fullText: String,
    val blocks: List<TextBlockData> = emptyList(),
    val error: String? = null
)

data class TextBlockData(
    val text: String,
    val boundingBox: List<Int>
)
