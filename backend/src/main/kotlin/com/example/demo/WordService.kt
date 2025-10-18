package com.example.demo

import jakarta.annotation.PostConstruct
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStreamReader

@Service
class WordService {

    private val baseWords: MutableList<String> = mutableListOf()

    @PostConstruct
    fun init() {
        val resource = ClassPathResource("wordlist.txt")
        BufferedReader(InputStreamReader(resource.inputStream)).useLines { lines ->
            lines.forEach { line ->
                if (line.isNotBlank()) {
                    baseWords.add(line.trim())
                }
            }
        }
    }

    fun getBaseWords(): List<String> {
        return baseWords
    }

    fun getWords(count: Int): List<String> {
        if (baseWords.size < count) {
            return baseWords.shuffled()
        }
        return baseWords.shuffled().take(count)
    }
}
