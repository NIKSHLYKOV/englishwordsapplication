package ru.nikshlykov.utils

import java.util.Random

// Используется, т.к. String.toCharArray().shuffle(Random) показал худшую скорость при тестах
fun String.getShuffleCharacters(): ArrayList<Char> {
    val lettersCount = this.length
    val letters = ArrayList<Char>(lettersCount)
    for (i in 0 until lettersCount) {
        letters.add(this[i])
    }
    val shuffleLetters = ArrayList<Char>(lettersCount)
    while (letters.size != 0) {
        val random = Random()
        val removeLetterIndex = random.nextInt(letters.size)
        shuffleLetters.add(letters.removeAt(removeLetterIndex))
    }
    return shuffleLetters
}