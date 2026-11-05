package com.example.arabicgrammar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* ============ حرکات ============ */
const val FATHA = "\u064E"
const val DAMMA = "\u064F"
const val KASRA = "\u0650"
const val SUKUN = "\u0652"
const val SHADDA = "\u0651"

/* ============ افعال (صحیح سالم، ثلاثی مجرد) ============ */
data class SarfVerb(
    val f: String, val a: String, val l: String,
    val pastV: String, val presV: String,
    val masdar: String,
    val meaning: Bi
)

val sarfVerbs: List<SarfVerb> = listOf(
    SarfVerb("ك", "ت", "ب", FATHA, DAMMA, "كِتَابَةٌ", Bi("لکھنا", "to write")),
    SarfVerb("ض", "ر", "ب", FATHA, KASRA, "ضَرْبٌ", Bi("مارنا", "to hit")),
    SarfVerb("ف", "ت", "ح", FATHA, FATHA, "فَتْحٌ", Bi("کھولنا", "to open")),
    SarfVerb("س", "م", "ع", KASRA, FATHA, "سَمْعٌ", Bi("سننا", "to hear")),
    SarfVerb("ن", "ص", "ر", FATHA, DAMMA, "نَصْرٌ", Bi("مدد کرنا", "to help")),
    SarfVerb("ج", "ل", "س", FATHA, KASRA, "جُلُوسٌ", Bi("بیٹھنا", "to sit")),
    SarfVerb("ع", "ل", "م", KASRA, FATHA, "عِلْمٌ", Bi("جاننا", "to know")),
    SarfVerb("ذ", "ه", "ب", FATHA, FATHA, "ذَهَابٌ", Bi("جانا", "to go")),
    SarfVerb("خ", "ر", "ج", FATHA, DAMMA, "خُرُوجٌ", Bi("نکلنا", "to go out")),
    SarfVerb("ح", "م", "د", KASRA, FATHA, "حَمْدٌ", Bi("حمد کرنا", "to praise"))
)

val sarfPronouns = listOf(
    "هُوَ", "هُمَا", "هُمْ", "هِيَ", "هُمَا", "هُنَّ",
    "أَنْتَ", "أَنْتُمَا", "أَنْتُمْ", "أَنْتِ", "أَنْتُمَا", "أَنْتُنَّ",
    "أَنَا", "نَحْنُ"
)

val sarfNamesUr = listOf(
    "واحد مذکر غائب", "تثنیہ مذکر غائب", "جمع مذکر غائب",
    "واحد مؤنث غائب", "تثنیہ مؤنث غائب", "جمع مؤنث غائب",
    "واحد مذکر حاضر", "تثنیہ مذکر حاضر", "جمع مذکر حاضر",
    "واحد مؤنث حاضر", "تثنیہ مؤنث حاضر", "جمع مؤنث حاضر",
    "متکلم واحد", "متکلم جمع"
)

val sarfNamesEn = listOf(
    "3rd masc. singular", "3rd masc. dual", "3rd masc. plural",
    "3rd fem. singular", "3rd fem. dual", "3rd fem. plural",
    "2nd masc. singular", "2nd masc. dual", "2nd masc. plural",
    "2nd fem. singular", "2nd fem. dual", "2nd fem. plural",
    "1st singular", "1st plural"
)

/* ============ گردان بنانے کا طریقہ ============ */
fun pastForms(v: SarfVerb): List<String> {
    val b = v.f + FATHA + v.a + v.pastV + v.l
    return listOf(
        b + FATHA,
        b + FATHA + "ا",
        b + DAMMA + "وا",
        b + FATHA + "ت" + SUKUN,
        b + FATHA + "ت" + FATHA + "ا",
        b + SUKUN + "ن" + FATHA,
        b + SUKUN + "ت" + FATHA,
        b + SUKUN + "ت" + DAMMA + "م" + FATHA + "ا",
        b + SUKUN + "ت" + DAMMA + "م" + SUKUN,
        b + SUKUN + "ت" + KASRA,
        b + SUKUN + "ت" + DAMMA + "م" + FATHA + "ا",
        b + SUKUN + "ت" + DAMMA + "ن" + SHADDA + FATHA,
        b + SUKUN + "ت" + DAMMA,
        b + SUKUN + "ن" + FATHA + "ا"
    )
}

fun presentForms(v: SarfVerb): List<String> {
    fun stem(prefix: String) = prefix + v.f + SUKUN + v.a + v.presV + v.l
    val y = "ي" + FATHA
    val t = "ت" + FATHA
    val h = "أ" + FATHA
    val n = "ن" + FATHA
    val an = "ا" + "ن" + KASRA
    return listOf(
        stem(y) + DAMMA,
        stem(y) + FATHA + an,
        stem(y) + DAMMA + "و" + "ن" + FATHA,
        stem(t) + DAMMA,
        stem(t) + FATHA + an,
        stem(y) + SUKUN + "ن" + FATHA,
        stem(t) + DAMMA,
        stem(t) + FATHA + an,
        stem(t) + DAMMA + "و" + "ن" + FATHA,
        stem(t) + KASRA + "ي" + "ن" + FATHA,
        stem(t) + FATHA + an,
        stem(t) + SUKUN + "ن" + FATHA,
        stem(h) + DAMMA,
        stem(n) + DAMMA
    )
}

fun imperativeForms(v: SarfVerb): List<String> {
    val hv = if (v.presV == DAMMA) DAMMA else KASRA
    val b = "ا" + hv + v.f + SUKUN + v.a + v.presV + v.l
    return listOf(
        b + SUKUN,
        b + FATHA + "ا",
        b + DAMMA + "و" + "ا",
        b + KASRA + "ي",
        b + FATHA + "ا",
        b + SUKUN + "ن" + FATHA
    )
}

/** tense: 0 ماضی، 1 مضارع، 2 امر */
fun formsFor(v: SarfVerb, tense: Int): List<String> = when (tense) {
    0 -> pastForms(v)
    1 -> presentForms(v)
    else -> imperativeForms(v)
}

fun sighahIndex(tense: Int, i: Int): Int = if (tense == 2) i + 6 else i

fun babOf(v: SarfVerb): String {
    val f = v.f + FATHA + v.a
    return when {
        v.presV == DAMMA -> f + FATHA + v.l + FATHA + " " + "ي" + FATHA + v.f + SUKUN + v.a + DAMMA + v.l + DAMMA
        v.presV == KASRA -> f + FATHA + v.l + FATHA + " " + "ي" + FATHA + v.f + SUKUN + v.a + KASRA + v.l + DAMMA
        v.pastV == KASRA -> v.f + FATHA + v.a + KASRA + v.l + FATHA + " " + "ي" + FATHA + v.f + SUKUN + v.a + FATHA + v.l + DAMMA
        else -> v.f + FATHA + v.a + FATHA + v.l + FATHA + " " + "ي" + FATHA + v.f + SUKUN + v.a + FATHA + v.l + DAMMA
    }
}

fun verbLabel(v: SarfVerb, en: Boolean): String = pastForms(v)[0] + " — " + v.meaning.pick(en)

fun tenseName(tense: Int, en: Boolean): String = when (tense) {
    0 -> if (en) "Past" else "ماضی معلوم"
    1 -> if (en) "Present" else "مضارع معلوم"
    else -> if (en) "Imperative" else "امر حاضر"
}

/* ============ مشترک: ٹوگل بٹن ============ */
@Composable
fun Segmented(options: List<String>, selected: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    androidx.compose.foundation.layout.Row(
        modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(BrandLight).padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        options.forEachIndexed { i, o ->
            Box(
                Modifier.weight(1f).clip(RoundedCornerShape(11.dp))
                    .background(if (i == selected) Brand else Color.Transparent)
                    .clickable { onSelect(i) }.padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(o, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                    color = if (i == selected) Color.White else BrandDark)
            }
        }
    }
}

/* ============ نحو اور صرف ایک ٹیب میں ============ */
@Composable
fun NahwSarfScreen() {
    var mode by remember { mutableStateOf(0) }
    Column(Modifier.fillMaxSize()) {
        Segmented(
            listOf(tr("📘 نحو", "📘 Nahw"), tr("🔧 صرف", "🔧 Sarf"), tr("📖 قرآن", "📖 Quran")),
            mode, { mode = it },
            Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        )
        Box(Modifier.weight(1f).fillMaxWidth()) {
            when (mode) {
                0 -> LessonsScreen()
                1 -> SarfScreen()
                else -> QuranScreen()
            }
        }
    }
}

@Composable
fun SarfScreen() {
    var sub by remember { mutableStateOf(0) }
    Column(Modifier.fillMaxSize()) {
        Segmented(
            listOf(tr("📋 گردان", "📋 Conjugation"), tr("🎯 مشق", "🎯 Practice")),
            sub, { sub = it },
            Modifier.padding(horizontal = 14.dp, vertical = 2.dp)
        )
        Box(Modifier.weight(1f).fillMaxWidth()) {
            if (sub == 0) SarfTables() else SarfPractice()
        }
    }
}

/* ============ گردان کے جدول ============ */
@Composable
fun SarfTables() {
    val en = LocalEn.current
    var verbIdx by remember { mutableStateOf(0) }
    var tense by remember { mutableStateOf(0) }
    val verb = sarfVerbs[verbIdx]
    val forms = formsFor(verb, tense)

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Border),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(tr("🔧 صرف: فعل کی گردان", "🔧 Sarf: Verb Conjugation"),
                    fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Brand)
                MixedText(
                    tr("ہر فعل کی تین بنیادی شکلیں ہیں: ماضی (جو ہو چکا)، مضارع (جو ہو رہا ہے یا ہوگا) اور امر (حکم)۔ ہر شکل کے صیغے ضمیر کے مطابق بدلتے ہیں۔",
                        "Every verb has three basic forms: past (done), present (happening or will happen) and imperative (command). Each form changes according to the pronoun."),
                    size = 14, arabicSize = 18, align = TextAlign.Start
                )
                MixedText(
                    tr("ماضی: مادّے کے حروف پر فَعَلَ کا وزن، پھر آخر میں اضافہ: فَعَلُوا، فَعَلْتُ، فَعَلْنَا",
                        "Past: the root letters on the pattern فَعَلَ, then endings: فَعَلُوا، فَعَلْتُ، فَعَلْنَا"),
                    size = 14, arabicSize = 18, align = TextAlign.Start
                )
                MixedText(
                    tr("مضارع: شروع میں أ، ن، ي یا ت لگتا ہے: يَفْعَلُ، تَفْعَلُ، أَفْعَلُ، نَفْعَلُ",
                        "Present: a prefix أ، ن، ي or ت is added: يَفْعَلُ، تَفْعَلُ، أَفْعَلُ، نَفْعَلُ"),
                    size = 14, arabicSize = 18, align = TextAlign.Start
                )
                MixedText(
                    tr("امر: مضارع حاضر سے بنتا ہے۔ ت ہٹائیں، ہمزۂ وصل لگائیں اور آخر ساکن کریں: تَكْتُبُ ← اُكْتُبْ",
                        "Imperative: made from the 2nd person present. Drop the ت, add a hamzat al-wasl and make the end sukun: تَكْتُبُ ← اُكْتُبْ"),
                    size = 14, arabicSize = 18, align = TextAlign.Start
                )
                MixedText(
                    tr("درمیانی حرف کی حرکت (ماضی اور مضارع میں) فعل کا باب طے کرتی ہے۔",
                        "The vowel on the middle root letter (in past and present) decides the verb's bab (pattern)."),
                    size = 14, arabicSize = 18, align = TextAlign.Start
                )
            }
        }

        DropdownField(
            label = tr("فعل چنیں", "Choose a verb"),
            selectedText = verbLabel(verb, en),
            options = sarfVerbs.mapIndexed { i, v -> i.toString() to verbLabel(v, en) },
            onSelect = { verbIdx = it.toInt() }
        )

        Segmented(
            listOf(tenseName(0, en), tenseName(1, en), tenseName(2, en)),
            tense, { tense = it }
        )

        // بنیادی معلومات
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = BrandLight),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                InfoLine(tr("ماضی", "Past"), pastForms(verb)[0])
                InfoLine(tr("مضارع", "Present"), presentForms(verb)[0])
                InfoLine(tr("امر", "Imperative"), imperativeForms(verb)[0])
                InfoLine(tr("مصدر", "Verbal noun"), verb.masdar)
                InfoLine(tr("باب", "Bab (pattern)"), babOf(verb))
            }
        }

        Text(
            "📋 " + tenseName(tense, en) + "  (" + forms.size + ")",
            fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Brand
        )
        SarfTable(forms, tense)
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
fun InfoLine(label: String, arabic: String) {
    androidx.compose.foundation.layout.Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, Modifier.weight(1f), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandDark)
        MixedText(arabic, size = 14, arabicSize = 20, forceArabic = true, color = BrandDark,
            modifier = Modifier.weight(2f), align = TextAlign.Center)
    }
}

@Composable
fun SarfTable(forms: List<String>, tense: Int) {
    val en = LocalEn.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Border),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(Modifier.fillMaxWidth()) {
            androidx.compose.foundation.layout.Row(Modifier.fillMaxWidth().background(Brand).padding(10.dp)) {
                Text(tr("ضمیر", "Pronoun"), Modifier.weight(1f), color = Color.White,
                    fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.Center)
                Text(tr("صیغہ", "Form name"), Modifier.weight(1.3f), color = Color.White,
                    fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.Center)
                Text(tr("فعل", "Verb"), Modifier.weight(1.4f), color = Color.White,
                    fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.Center)
            }
            forms.forEachIndexed { i, form ->
                val si = sighahIndex(tense, i)
                val name = if (en) sarfNamesEn[si] else sarfNamesUr[si]
                androidx.compose.foundation.layout.Row(Modifier.height(IntrinsicSize.Min)) {
                    Box(
                        Modifier.weight(1f).fillMaxHeight().background(BrandLight)
                            .border(0.5.dp, Border).padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        MixedText(sarfPronouns[si], size = 13, arabicSize = 18, forceArabic = true, arabicColor = BrandDark)
                    }
                    Box(
                        Modifier.weight(1.3f).fillMaxHeight()
                            .background(if (i % 2 == 0) Color.White else Page)
                            .border(0.5.dp, Border).padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${i + 1}. $name", fontSize = 12.sp, lineHeight = 20.sp, textAlign = TextAlign.Center)
                    }
                    Box(
                        Modifier.weight(1.4f).fillMaxHeight().background(Color(0xFFF0F9FF))
                            .border(0.5.dp, Border).padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        MixedText(form, size = 14, arabicSize = 22, forceArabic = true)
                    }
                }
            }
        }
    }
}

/* ============ خودکار مشق ============ */
data class SarfQ(val prompt: String, val options: List<String>, val correct: Int)

fun buildSarfQuestions(en: Boolean, count: Int = 10): List<SarfQ> {
    val result = mutableListOf<SarfQ>()
    val seen = mutableSetOf<String>()
    var guard = 0
    while (result.size < count && guard < 200) {
        guard += 1
        val verb = sarfVerbs.random()
        val tense = (0..2).random()
        val forms = formsFor(verb, tense)
        val idx = forms.indices.random()
        val si = sighahIndex(tense, idx)
        val base = pastForms(verb)[0]
        val meaning = verb.meaning.pick(en)
        val unique = forms.count { it == forms[idx] } == 1
        val type = if (unique) (0..1).random() else 0

        val q: SarfQ? = if (type == 0) {
            val wrong = forms.filter { it != forms[idx] }.distinct().shuffled().take(3)
            if (wrong.size < 2) null else {
                val opts = (wrong + forms[idx]).shuffled()
                val nm = if (en) sarfNamesEn[si] else sarfNamesUr[si]
                val p = if (en)
                    "Verb «$base» ($meaning)\nWhich is the correct ${tenseName(tense, true)} form for «${sarfPronouns[si]}» ($nm)?"
                else
                    "فعل «$base» ($meaning)\n${tenseName(tense, false)} میں «${sarfPronouns[si]}» ($nm) کے لیے صحیح صیغہ کون سا ہے؟"
                SarfQ(p, opts, opts.indexOf(forms[idx]))
            }
        } else {
            val others = forms.indices.filter { forms[it] != forms[idx] }.shuffled().take(3)
            fun label(k: Int): String {
                val s = sighahIndex(tense, k)
                return (if (en) sarfNamesEn[s] else sarfNamesUr[s]) + " (" + sarfPronouns[s] + ")"
            }
            val correctLabel = label(idx)
            val opts = (others.map { label(it) } + correctLabel).shuffled()
            val p = if (en)
                "«${forms[idx]}» (verb «$base»)\nWhich ${tenseName(tense, true)} form is this?"
            else
                "«${forms[idx]}» (فعل «$base»)\nیہ ${tenseName(tense, false)} کا کون سا صیغہ ہے؟"
            SarfQ(p, opts, opts.indexOf(correctLabel))
        }
        if (q != null && seen.add(q.prompt)) result.add(q)
    }
    return result
}

@Composable
fun SarfPractice() {
    val context = LocalContext.current
    val en = LocalEn.current
    var questions by remember { mutableStateOf<List<SarfQ>>(emptyList()) }
    var started by remember { mutableStateOf(false) }
    var index by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var selected by remember { mutableStateOf<Int?>(null) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (!started) {
            Text(tr("🎯 صرف کی مشق", "🎯 Sarf Practice"), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Brand)
            Text(
                tr("10 سوالات خود بخود بنیں گے: ماضی، مضارع اور امر کے صیغے پہچانیں اور بنائیں۔ ہر بار نئے سوال آئیں گے۔",
                    "10 questions are generated automatically: recognise and form past, present and imperative forms. You get new questions every time."),
                fontSize = 15.sp, lineHeight = 28.sp
            )
            BigButton(tr("مشق شروع کریں", "Start Practice"), onClick = {
                questions = buildSarfQuestions(en)
                index = 0; score = 0; selected = null; started = true
            }, modifier = Modifier.fillMaxWidth())
        } else if (index >= questions.size) {
            Text(tr("مشق مکمل", "Practice Complete"), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Brand)
            Surface(color = BrandLight, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Box(Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("$score / ${questions.size}", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = BrandDark)
                }
            }
            Text(
                when {
                    score == questions.size -> tr("ماشاء اللہ! سب صیغے صحیح ہیں۔", "MashaAllah! All forms are correct.")
                    score * 10 >= questions.size * 7 -> tr("بہت خوب! تھوڑی سی مزید مشق کافی ہے۔", "Great! A little more practice is enough.")
                    else -> tr("گردان کے جدول دوبارہ دیکھ کر مشق دہرائیں۔", "Review the conjugation tables and try again.")
                },
                fontSize = 15.sp, lineHeight = 28.sp
            )
            BigButton(tr("نئی مشق", "New Practice"), onClick = {
                questions = buildSarfQuestions(en)
                index = 0; score = 0; selected = null
            }, modifier = Modifier.fillMaxWidth())
            BigButton(tr("واپس", "Back"), onClick = { started = false }, filled = false, modifier = Modifier.fillMaxWidth())
        } else {
            val q = questions[index]
            Text(
                tr("سوال ${index + 1} / ${questions.size}      درست: $score",
                    "Question ${index + 1} / ${questions.size}      Correct: $score"),
                fontSize = 13.sp, color = Muted
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Border),
                shape = RoundedCornerShape(16.dp)
            ) {
                MixedText(q.prompt, size = 16, arabicSize = 22, bold = true, align = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth().padding(16.dp))
            }
            q.options.forEachIndexed { i, opt ->
                val sel = selected
                val bg = when {
                    sel == null -> Color.White
                    i == q.correct -> Good
                    i == sel -> Bad
                    else -> Color.White
                }
                val bc = when {
                    sel == null -> Border
                    i == q.correct -> GoodText
                    i == sel -> BadText
                    else -> Border
                }
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = bg,
                    border = BorderStroke(1.5.dp, bc),
                    modifier = Modifier.fillMaxWidth().clickable(enabled = sel == null) {
                        selected = i
                        if (i == q.correct) score += 1
                    }
                ) {
                    MixedText(opt, size = 15, arabicSize = 22, align = TextAlign.Start,
                        modifier = Modifier.padding(14.dp))
                }
            }
            if (selected != null) {
                Text(
                    if (selected == q.correct) tr("✅ درست جواب", "✅ Correct")
                    else tr("❌ غلط، صحیح جواب سبز رنگ میں ہے", "❌ Wrong. The correct answer is in green."),
                    fontSize = 15.sp, fontWeight = FontWeight.Bold,
                    color = if (selected == q.correct) GoodText else BadText
                )
                BigButton(
                    if (index == questions.size - 1) tr("نتیجہ دیکھیں", "See Result") else tr("اگلا سوال", "Next Question"),
                    onClick = {
                        if (index == questions.size - 1) {
                            addResult(context, ScoreEntry("sarf", if (en) "Sarf Practice" else "صرف مشق", score, questions.size, nowString()))
                        }
                        index += 1; selected = null
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
