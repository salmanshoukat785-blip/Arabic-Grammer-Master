package com.example.arabicgrammar

import android.content.Context
import org.json.JSONObject

data class Row(val cells: List<String>)
data class Example(val arabic: String, val urdu: String, val quran: String?)

data class Category(
    val key: String,
    val title: String,
    val badge: String,
    val subCategories: List<Pair<String, String>>,
    val columns: List<String>,
    val rule: String,
    val note: String,
    val rows: List<Row>,
    val examples: List<Example>
)

fun loadDatabase(context: Context): List<Category> {
    val text = context.assets.open("database.json").bufferedReader(Charsets.UTF_8).use { it.readText() }
    val root = JSONObject(text)
    val result = mutableListOf<Category>()
    val keys = root.keys()
    while (keys.hasNext()) {
        val key = keys.next()
        val o = root.getJSONObject(key)

        val subObj = o.getJSONObject("subCategories")
        val subs = mutableListOf<Pair<String, String>>()
        val sk = subObj.keys()
        while (sk.hasNext()) { val k = sk.next(); subs.add(k to subObj.getString(k)) }

        val colsArr = o.getJSONArray("columns")
        val cols = List(colsArr.length()) { colsArr.getString(it) }

        val rowsArr = o.getJSONArray("rows")
        val rows = List(rowsArr.length()) { i ->
            val r = rowsArr.getJSONObject(i)
            Row(listOf("c1", "c2", "c3", "c4", "c5").map { r.optString(it, "") })
        }

        val exArr = o.optJSONArray("examples")
        val examples = if (exArr == null) emptyList() else List(exArr.length()) { i ->
            val e = exArr.getJSONObject(i)
            Example(e.optString("arabic"), e.optString("urdu"), e.optString("quran").ifBlank { null })
        }

        result.add(
            Category(
                key, o.getString("title"), o.getString("badge"), subs, cols,
                o.optString("rule"), o.optString("note"), rows, examples
            )
        )
    }
    return result
}

fun filterRows(category: Category, subKey: String): List<Row> {
    if (subKey == "all") return category.rows
    return category.rows.filter { row ->
        val c1 = row.cells[0]
        val all = row.cells.joinToString(" ").lowercase()
        when (subKey) {
            "huroof17" -> (1..17).any { c1.contains("$it.") }
            "harakat" -> all.contains("حركات")
            "huroof" -> all.contains("حروف")
            "marifa" -> c1.contains("قسم") || (1..7).any { c1.contains("$it.") }
            "nakirah" -> c1.contains("نكرہ")
            "wahid" -> c1.contains("واحد")
            "tasniya" -> c1.contains("تثنيہ")
            "jama" -> c1.contains("جمع")
            "mudhakkar" -> c1.contains("مذكر")
            "haqiqi" -> c1.contains("حقيقي")
            "qiyasi" -> c1.contains("قياسي")
            "samai" -> c1.contains("سماعي")
            "ghaib" -> c1.contains("غائب")
            "hazir" -> c1.contains("حاضر")
            "mutakallim" -> c1.contains("متكلم")
            "mutasil" -> c1.contains("متصل")
            "munfasil" -> c1.contains("منفصل")
            "qareeb" -> c1.contains("قريب")
            "baeod" -> c1.contains("بعيد")
            "makan" -> c1.contains("مكان")
            "khas" -> c1.contains("خاص")
            "aam" -> c1.contains("عام")
            "murakkab" -> c1.contains("مركب") || c1.contains("جملہ")
            else -> true
        }
    }
}

fun searchRows(category: Category, query: String): List<Row> {
    val q = query.trim().lowercase()
    if (q.isEmpty()) return category.rows
    return category.rows.filter { r -> r.cells.any { it.lowercase().contains(q) } }
}
