package com.example.kit_market.domain.util

//Здесь реализован алгоритм для расстояния Левинштейна
//Он вычисляет расстояние между двумя строками
//Это нужно для того, чтобы при поиске продуктов пользователь мог ошибиться, но все равно найти нужный ему продукт
fun levenshteinDistance(a: String, b: String): Int {
    val m = a.length
    val n = b.length

    // dp[i][j] — расстояние между a[0..i-1] и b[0..j-1]
    val dp = Array(m + 1) { IntArray(n + 1) }

    for (i in 0..m) dp[i][0] = i
    for (j in 0..n) dp[0][j] = j

    for (i in 1..m) {
        for (j in 1..n) {
            val cost = if (a[i - 1].lowercaseChar() == b[j - 1].lowercaseChar()) 0 else 1
            dp[i][j] = minOf(
                dp[i - 1][j] + 1,      // удаление
                dp[i][j - 1] + 1,      // вставка
                dp[i - 1][j - 1] + cost // замена
            )
        }
    }
    //возвращается нжний правый элемент массива - искомое расстояние
    return dp[m][n]
}


 //Проверяет, является ли [query] нечётким совпадением для [text].
 //Возвращает расстояние (чем меньше, тем лучше), или null если совпадения нет.

fun fuzzyMatchScore(query: String, text: String): Int? {
    val q = query.lowercase().trim()
    val t = text.lowercase().trim()

    if (q.isEmpty()) return null

    // Точное вхождение подстроки — лучший результат
    if (t.contains(q)) return 0

    val words = t.split(" ", "-", ",").filter { it.isNotBlank() }
    val queryWords = q.split(" ").filter { it.isNotBlank() }
    val maxAllowedDistance = maxOf(q.length / 3, 1)

    // Если запрос из нескольких слов — каждое слово запроса
    // должно нечётко совпасть хотя бы с одним словом из названия
    if (queryWords.size > 1) {
        var totalScore = 0
        for (qWord in queryWords) {
            val wordMaxDist = maxOf(qWord.length / 3, 1)
            val bestForWord = words.minOfOrNull { levenshteinDistance(qWord, it) }
            if (bestForWord == null || bestForWord > wordMaxDist) return null
            totalScore += bestForWord
        }
        return totalScore
    }

    // Однословный запрос — сравниваем с каждым словом названия
    var bestDistance = Int.MAX_VALUE

    for (word in words) {
        // Точное совпадение со словом
        val distance = levenshteinDistance(q, word)
        if (distance < bestDistance) {
            bestDistance = distance
        }
        // Для длинных слов — проверяем подстроки длиной ~query
        // (например, запрос "молок" в слове "ультрапастеризованное" не нужен,
        //  но "парм" в "parmalat" — полезно)
        if (word.length > q.length + 2) {
            for (i in 0..word.length - q.length) {
                val sub = word.substring(i, i + q.length)
                val subDist = levenshteinDistance(q, sub)
                if (subDist < bestDistance) {
                    bestDistance = subDist
                }
            }
        }
    }

    return if (bestDistance <= maxAllowedDistance) bestDistance else null
}
