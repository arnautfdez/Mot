import java.io.File
import java.text.Normalizer

// Helper function to remove accents and convert to uppercase
fun String.cleanup(): String {
    val normalized = Normalizer.normalize(this, Normalizer.Form.NFD)
    // Regex to remove diacritical marks (accents)
    val accentPattern = "[\\p{InCombiningDiacriticalMarks}]".toRegex()
    return accentPattern.replace(normalized, "").uppercase()
}

fun main() {
    // IMPORTANT: Change this to the actual path of your catala.dic file
    val inputFile = File("/Users/arnau.torrents/Downloads/catala.dic")

    // IMPORTANT: Change this to where you want to save the final word list
    val outputFile = File("/Users/arnau.torrents/Downloads/wordle_words.txt")

    if (!inputFile.exists()) {
        println("Error: Input file not found at ${inputFile.absolutePath}")
        return
    }

    // Read all lines, process them, and store in a Set to avoid duplicates
    val fiveLetterWords = inputFile.readLines()
        .asSequence() // Use a sequence for better performance with large files
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        // Handle the "/FLAG" format common in .dic files
        .map { it.substringBefore('/') }
        .filter { it.length == 5 }
        // Make sure the word contains only letters (handles words with hyphens, etc.)
        .filter { it.all { char -> char.isLetter() } }
        .map { it.cleanup() } // Convert to uppercase and remove accents
        .toSortedSet() // Sort alphabetically and remove duplicates

    // Write the result to the output file, one word per line
    outputFile.writeText(fiveLetterWords.joinToString("\n"))

    println("Processing finished!")
    println("${fiveLetterWords.size} unique 5-letter words found.")
    println("Clean word list saved to: ${outputFile.absolutePath}")
}