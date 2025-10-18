package com.example.demo

import org.springframework.stereotype.Service

@Service
class WordService {

    private val words = listOf(
        "Apple", "Banana", "Carrot", "Dog", "Elephant", "Flower", "Guitar", "House", "Ice Cream", "Jungle",
        "Kite", "Lemon", "Mountain", "Notebook", "Ocean", "Piano", "Queen", "Rainbow", "Sun", "Tree",
        "Umbrella", "Violin", "Watermelon", "Xylophone", "Yacht", "Zebra", "Ant", "Bird", "Cat", "Duck"
    )

    // Backend test change
    fun getWords(count: Int): List<String> {
        return words.shuffled().take(count)
    }
}
