package com.example.demo

import jakarta.annotation.PostConstruct
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStreamReader

@Service
class WordService {

    private val baseWords: MutableList<String> = mutableListOf()
    private val fallbackWords = listOf(
        "Apple", "Banana", "Carrot", "Dog", "Elephant", "Flower", "Guitar", "House", "Ice Cream", "Jungle",
        "Kite", "Lemon", "Mountain", "Notebook", "Ocean", "Piano", "Queen", "Rainbow", "Sun", "Tree",
        "Umbrella", "Violin", "Watermelon", "Xylophone", "Yacht", "Zebra", "Ant", "Bird", "Cat", "Duck"
    )

    @PostConstruct
    fun init() {
        try {
            val resource = ClassPathResource("wordlist.txt")
            BufferedReader(InputStreamReader(resource.inputStream)).useLines { lines ->
                lines.forEach { line ->
                    if (line.isNotBlank()) {
                        baseWords.add(line.trim())
                    }
                }
            }
        } catch (e: Exception) {
            println("wordlist.txt not found or unreadable, using fallback words.")
            // If the file can't be read, the baseWords list will be empty,
            // and the logic in GameService will rely on the fallback.
        }
    }

    fun getBaseWords(): List<String> {
        return baseWords
    }

    fun getFallbackWords(): List<String> {
        return fallbackWords
    }
}