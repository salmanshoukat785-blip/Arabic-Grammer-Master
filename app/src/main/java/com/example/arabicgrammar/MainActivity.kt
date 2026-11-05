package com.example.arabicgrammar

import android.content.Context
import android.os.Bundle
import android.graphics.Typeface
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/* ============ رنگ (ہلکا آسمانی) ============ */
val Brand = Color(0xFF0284C7)
val BrandDark = Color(0xFF075985)
val BrandLight = Color(0xFFE0F2FE)
val Page = Color(0xFFF0F9FF)
val Border = Color(0xFFBAE6FD)
val Muted = Color(0xFF64748B)
val ExampleTint = Color(0xFFF0F9FF)
val Amber = Color(0xFFFEF3C7)
val AmberText = Color(0xFF92400E)
val Good = Color(0xFFDCFCE7)
val GoodText = Color(0xFF15803D)
val Bad = Color(0xFFFEE2E2)
val BadText = Color(0xFFB91C1C)

/* ============ زبان (اردو / English) ============ */
val LocalEn = compositionLocalOf { false }

@Composable
fun tr(ur: String, en: String): String = if (LocalEn.current) en else ur

private val mainLabels = mapOf(
    "irab" to "1. اعرابی حالات (رفع، نصب، جر)",
    "wusat" to "2. وسعت (معرفہ اور نکرہ)",
    "adad" to "3. عدد (واحد، تثنیہ، جمع)",
    "jins" to "4. جنس (مذکر، مؤنث و مؤنث سماعی)",
    "madi" to "5. علامات فعل ماضی معلوم (14 صیغے)",
    "mudari" to "6. علامات فعل مضارع معلوم (14 صیغے)",
    "damair" to "7. ضمائر (متصل و منفصل - 14 صیغے)",
    "ishara" to "8. اسم اشارہ (قریب، بعید، مکان)",
    "mausool" to "9. اسم موصول (خاص و عام)",
    "jar" to "10. حروفِ جار اور نحوی مرکبات"
)

private val mainLabelsEn = mapOf(
    "irab" to "1. Grammatical Cases (Raf', Nasb, Jarr)",
    "wusat" to "2. Definiteness (Ma'rifa & Nakira)",
    "adad" to "3. Number (Singular, Dual, Plural)",
    "jins" to "4. Gender (Masculine & Feminine)",
    "madi" to "5. Past Tense Signs (14 Forms)",
    "mudari" to "6. Present Tense Signs (14 Forms)",
    "damair" to "7. Pronouns (Attached & Detached)",
    "ishara" to "8. Demonstratives (Near, Far, Place)",
    "mausool" to "9. Relative Pronouns",
    "jar" to "10. Prepositions & Constructions"
)

fun topicLabel(key: String, en: Boolean, fallback: String): String =
    (if (en) mainLabelsEn[key] else mainLabels[key]) ?: fallback

/* ============ ڈیٹا کی صفائی (HTML ٹیگ ہٹانا) ============ */
fun cleanText(s: String): String =
    s.replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), "\n")
        .replace(Regex("<[^>]*>"), "")
        .replace("&nbsp;", " ")
        .lines().joinToString("\n") { it.trim() }
        .trim()

fun cleanCategory(c: Category): Category = c.copy(
    title = cleanText(c.title),
    badge = cleanText(c.badge),
    subCategories = c.subCategories.map { it.first to cleanText(it.second) },
    columns = c.columns.map { cleanText(it) },
    rule = cleanText(c.rule),
    note = cleanText(c.note),
    rows = c.rows.map { r -> Row(r.cells.map { cleanText(it) }) },
    examples = c.examples.map { Example(cleanText(it.arabic), cleanText(it.urdu), it.quran?.let { q -> cleanText(q) }) }
)

/* ============ محفوظ شدہ ڈیٹا ============ */
data class ScoreEntry(val type: String, val topic: String, val s: Int, val n: Int, val date: String)

private fun prefs(ctx: Context) = ctx.getSharedPreferences("agm", Context.MODE_PRIVATE)

fun loadName(ctx: Context): String = prefs(ctx).getString("name", "") ?: ""
fun saveName(ctx: Context, name: String) { prefs(ctx).edit().putString("name", name).apply() }
fun loadLangEn(ctx: Context): Boolean = prefs(ctx).getString("lang", "ur") == "en"
fun saveLang(ctx: Context, en: Boolean) { prefs(ctx).edit().putString("lang", if (en) "en" else "ur").apply() }

fun nowString(): String = SimpleDateFormat("dd/MM/yyyy  HH:mm", Locale.US).format(Date())

fun loadResults(ctx: Context): List<ScoreEntry> {
    return try {
        val arr = JSONArray(prefs(ctx).getString("results", "[]") ?: "[]")
        List(arr.length()) { i ->
            val o = arr.getJSONObject(i)
            ScoreEntry(o.optString("t"), o.optString("topic"), o.optInt("s"), o.optInt("n"), o.optString("d"))
        }
    } catch (e: Exception) {
        emptyList()
    }
}

fun addResult(ctx: Context, e: ScoreEntry) {
    try {
        val list = listOf(e) + loadResults(ctx)
        val arr = JSONArray()
        list.take(100).forEach {
            arr.put(JSONObject().put("t", it.type).put("topic", it.topic).put("s", it.s).put("n", it.n).put("d", it.date))
        }
        prefs(ctx).edit().putString("results", arr.toString()).apply()
    } catch (ex: Exception) {
    }
}

fun clearResults(ctx: Context) { prefs(ctx).edit().putString("results", "[]").apply() }

/* ============ ایکٹیویٹی ============ */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(primary = Brand)) {
                GrammarApp()
            }
        }
    }
}

@Composable
fun GrammarApp() {
    val context = LocalContext.current
    val categories = remember { loadDatabase(context).map { cleanCategory(it) } }
    var name by remember { mutableStateOf(loadName(context)) }
    var en by remember { mutableStateOf(loadLangEn(context)) }
    var route by remember { mutableStateOf(R_HOME) }
    // 0 = Splash، 1 = Onboarding، 2 = مرکزی ایپ / نام والی اسکرین
    var stage by remember { mutableStateOf(if (introDone) 2 else 0) }

    CompositionLocalProvider(
        LocalEn provides en,
        LocalLayoutDirection provides (if (en) LayoutDirection.Ltr else LayoutDirection.Rtl)
    ) {
        if (stage == 0) {
            SplashScreen(onDone = {
                if (name.isBlank()) {
                    stage = 1
                } else {
                    introDone = true
                    stage = 2
                }
            })
        } else if (stage == 1) {
            OnboardingScreen(onFinish = {
                introDone = true
                stage = 2
            })
        } else if (name.isBlank()) {
            WelcomeScreen(
                onToggleLang = { en = !en; saveLang(context, en) },
                onSave = { saveName(context, it); name = it }
            )
        } else {
            BackHandler(enabled = route != R_HOME) { route = R_HOME }

            val navSelected = when (route) {
                R_HOME -> 0
                R_LEARN, R_NAHW, R_QURAN -> 1
                R_QUIZ, R_SELF -> 2
                R_SCORE -> 3
                else -> 4
            }
            val navRoutes = listOf(R_HOME, R_LEARN, R_QUIZ, R_SCORE, R_PROFILE)

            Scaffold(
                containerColor = Page,
                topBar = {
                    if (route != R_HOME) {
                        Header(name, onToggleLang = { en = !en; saveLang(context, en) })
                    }
                },
                bottomBar = {
                    NavigationBar(containerColor = Color.White) {
                        val items = listOf(
                            "🏠" to tr("ہوم", "Home"),
                            "📖" to tr("اسباق", "Lessons"),
                            "✏️" to tr("پریکٹس", "Practice"),
                            "🏆" to tr("اسکور", "Score"),
                            "👤" to tr("پروفائل", "Profile")
                        )
                        items.forEachIndexed { i, (icon, label) ->
                            NavigationBarItem(
                                selected = navSelected == i,
                                onClick = { route = navRoutes[i] },
                                icon = { Text(icon, fontSize = 20.sp) },
                                label = { Text(label, fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedTextColor = Brand,
                                    indicatorColor = BrandLight
                                )
                            )
                        }
                    }
                }
            ) { padding ->
                Column(Modifier.padding(padding).fillMaxSize()) {
                    if (route >= R_LEARN && route <= R_QURAN) {
                        Segmented(
                            listOf(
                                tr("📖 گرامر", "📖 Grammar"),
                                tr("📘 نحو و صرف", "📘 Nahw & Sarf"),
                                tr("🕌 قرآن", "🕌 Quran")
                            ),
                            route - R_LEARN,
                            { route = R_LEARN + it },
                            Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                    if (route == R_QUIZ || route == R_SELF) {
                        Segmented(
                            listOf(tr("❓ کوئز", "❓ Quiz"), tr("📝 سیلف ٹیسٹ", "📝 Self Test")),
                            route - R_QUIZ,
                            { route = R_QUIZ + it },
                            Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                    Box(Modifier.weight(1f).fillMaxWidth()) {
                        when (route) {
                            R_HOME -> HomeScreen(
                                name = name,
                                categories = categories,
                                onOpen = { route = it },
                                onToggleLang = { en = !en; saveLang(context, en) }
                            )
                            R_LEARN -> LearnScreen(categories)
                            R_NAHW -> NahwSarfScreen()
                            R_QURAN -> QuranScreen()
                            R_QUIZ -> QuizScreen(categories)
                            R_SELF -> SelfTestScreen(categories)
                            R_SCORE -> ScoreScreen(name, onNameChange = { saveName(context, it); name = it })
                            else -> ProfileScreen(
                                name = name,
                                onNameChange = { saveName(context, it); name = it },
                                onToggleLang = { en = !en; saveLang(context, en) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LangButton(onClick: () -> Unit) {
    Surface(
        color = Color.White.copy(alpha = 0.25f),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            tr("English", "اردو"),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
        )
    }
}

@Composable
fun Header(name: String, onToggleLang: () -> Unit) {
    Box(
        Modifier.fillMaxWidth()
            .background(Brush.horizontalGradient(listOf(Color(0xFF0EA5E9), Color(0xFF38BDF8))))
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    tr("📚 عربی گرامر ماسٹر", "📚 Arabic Grammar Master"),
                    fontSize = 21.sp, fontWeight = FontWeight.Bold, color = Color.White
                )
                Text(
                    tr("السلام علیکم، $name", "Assalamu Alaikum, $name"),
                    fontSize = 14.sp, color = Color(0xFFE0F2FE)
                )
            }
            LangButton(onToggleLang)
        }
    }
}

@Composable
fun WelcomeScreen(onToggleLang: () -> Unit, onSave: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    Box(
        Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF0EA5E9), Color(0xFFBAE6FD)))),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(color = Brand, shape = RoundedCornerShape(20.dp), modifier = Modifier.clickable { onToggleLang() }) {
                    Text(
                        tr("English", "اردو"),
                        fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp)
                    )
                }
                QuranLogo(110.dp)
                Text(tr("عربی گرامر ماسٹر", "Arabic Grammar Master"),
                    fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Brand)
                Text(
                    tr("خوش آمدید! اپنا نام لکھیں تاکہ آپ کا پروفائل اور اسکور محفوظ ہو سکے۔",
                        "Welcome! Enter your name so your profile and scores can be saved."),
                    fontSize = 15.sp, lineHeight = 28.sp, color = Muted, textAlign = TextAlign.Center
                )
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    placeholder = { Text(tr("آپ کا نام", "Your name"), color = Muted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Brand, unfocusedBorderColor = Border)
                )
                BigButton(tr("شروع کریں", "Start"), onClick = {
                    if (text.isNotBlank()) onSave(text.trim())
                }, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

/* ============ مشترک اجزاء ============ */
@Composable
fun BigButton(text: String, onClick: () -> Unit, filled: Boolean = true, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (filled) Brand else Color.White,
        border = BorderStroke(1.5.dp, Brand),
        modifier = modifier.clickable { onClick() }
    ) {
        Box(Modifier.padding(vertical = 14.dp, horizontal = 12.dp), contentAlignment = Alignment.Center) {
            Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (filled) Color.White else Brand)
        }
    }
}

/* اردو (نستعلیق) فونٹ: assets میں NotoNastaliqUrdu-Regular.ttf ہو تو استعمال ہوگا، ورنہ عام فونٹ */
@Composable
fun urduFontFamily(): FontFamily? {
    val context = LocalContext.current
    return remember {
        try {
            FontFamily(Typeface.createFromAsset(context.assets, "NotoNastaliqUrdu-Regular.ttf"))
        } catch (e: Exception) {
            null
        }
    }
}

@Composable
fun DropdownField(
    label: String,
    selectedText: String,
    options: List<Pair<String, String>>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val en = LocalEn.current
    val urdu = urduFontFamily()
    // اردو میں: دائیں سے بائیں ترتیب (نمبر دائیں طرف) اور اردو (نستعلیق) فونٹ
    val textStyle = if (en) {
        TextStyle(textDirection = TextDirection.Ltr)
    } else {
        TextStyle(textDirection = TextDirection.Rtl, fontFamily = urdu, lineHeight = 30.sp)
    }
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Muted)
        Box(Modifier.fillMaxWidth()) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = BorderStroke(1.5.dp, Brand),
                modifier = Modifier.fillMaxWidth().clickable { expanded = true }
            ) {
                androidx.compose.foundation.layout.Row(
                    Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        selectedText,
                        modifier = Modifier.weight(1f),
                        fontWeight = if (en) FontWeight.Bold else FontWeight.Normal,
                        fontSize = if (en) 15.sp else 17.sp,
                        color = Color(0xFF0F172A),
                        style = textStyle
                    )
                    Text("▼", fontSize = 12.sp, color = Brand)
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.92f).background(Color.White)
            ) {
                options.forEach { (key, text) ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text,
                                fontSize = if (en) 15.sp else 17.sp,
                                style = textStyle,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        onClick = { onSelect(key); expanded = false }
                    )
                }
            }
        }
    }
}

/* ============ جدول ============ */
/** لفظ عربی ہے یا نہیں (اعراب یا عربی مخصوص حروف سے پہچان) */
fun isArabicToken(t: String): Boolean {
    for (ch in t) {
        val c = ch.code
        if (c in 0x064B..0x0652 || c == 0x0670 || c == 0x0671) return true
        if (ch == 'ك' || ch == 'ي' || ch == 'ة' || ch == 'ى') return true
    }
    return false
}

/** عربی الفاظ کے لیے الگ (نسخ) فونٹ اور بڑا سائز، باقی متن عام فونٹ میں */
@Composable
fun MixedText(
    text: String,
    modifier: Modifier = Modifier,
    size: Int = 14,
    arabicSize: Int = 19,
    color: Color = Color(0xFF0F172A),
    arabicColor: Color = Brand,
    bold: Boolean = false,
    align: TextAlign = TextAlign.Center,
    forceArabic: Boolean = false
) {
    val tokens = text.split(" ")
    val hasArabic = forceArabic || tokens.any { isArabicToken(it) }
    val annotated = buildAnnotatedString {
        tokens.forEachIndexed { i, tok ->
            if (forceArabic || isArabicToken(tok)) {
                withStyle(
                    SpanStyle(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = arabicSize.sp,
                        color = arabicColor
                    )
                ) { append(tok) }
            } else {
                withStyle(
                    SpanStyle(
                        fontSize = size.sp,
                        color = color,
                        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
                    )
                ) { append(tok) }
            }
            if (i < tokens.size - 1) append(" ")
        }
    }
    Text(
        text = annotated,
        textAlign = align,
        lineHeight = (if (hasArabic) arabicSize + 14 else size + 10).sp,
        modifier = modifier
    )
}

@Composable
fun CellContent(raw: String, col: Int) {
    val lines = raw.split("\n")
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        lines.forEachIndexed { i, line ->
            if (i == 0) {
                when {
                    col >= 3 -> MixedText(line, size = 14, arabicSize = 20, forceArabic = true)
                    col == 0 -> MixedText(line, size = 14, arabicSize = 18, color = BrandDark, arabicColor = BrandDark, bold = true)
                    else -> MixedText(line, size = 14, arabicSize = 20)
                }
            } else {
                Text(line, fontSize = 11.sp, color = Muted, lineHeight = 18.sp, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun GrammarTable(columns: List<String>, rows: List<Row>) {
    val scroll = rememberScrollState()
    val last = columns.size - 1
    val widths: List<Dp> = columns.indices.map { i ->
        when {
            i == 0 -> 150.dp
            i == last -> 170.dp
            else -> 135.dp
        }
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Border),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(Modifier.horizontalScroll(scroll)) {
            androidx.compose.foundation.layout.Row(Modifier.height(IntrinsicSize.Min)) {
                columns.forEachIndexed { i, c ->
                    Box(
                        Modifier.width(widths[i]).fillMaxHeight().background(Brand)
                            .border(0.5.dp, Color.White.copy(alpha = 0.4f)).padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(c, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp,
                            textAlign = TextAlign.Center)
                    }
                }
            }
            rows.forEachIndexed { idx, r ->
                val bg = if (idx % 2 == 0) Color.White else Page
                androidx.compose.foundation.layout.Row(Modifier.height(IntrinsicSize.Min)) {
                    columns.indices.forEach { i ->
                        val raw = r.cells.getOrElse(i) { "" }
                        val cellBg = when {
                            i == 0 -> BrandLight
                            i >= 3 -> ExampleTint
                            else -> bg
                        }
                        Box(
                            Modifier.width(widths[i]).fillMaxHeight().background(cellBg)
                                .border(0.5.dp, Border).padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CellContent(raw, i)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExamplesTable(examples: List<Example>) {
    val scroll = rememberScrollState()
    val heads = listOf(tr("مثال", "Example"), tr("مطلب", "Meaning"), tr("قرآنی شاہد", "Quranic Evidence"))
    val widths = listOf(170.dp, 170.dp, 210.dp)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Border),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(Modifier.horizontalScroll(scroll)) {
            androidx.compose.foundation.layout.Row(Modifier.height(IntrinsicSize.Min)) {
                heads.forEachIndexed { i, h ->
                    Box(
                        Modifier.width(widths[i]).fillMaxHeight().background(BrandDark)
                            .border(0.5.dp, Color.White.copy(alpha = 0.4f)).padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(h, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
            examples.forEachIndexed { idx, ex ->
                val bg = if (idx % 2 == 0) Color.White else Page
                androidx.compose.foundation.layout.Row(Modifier.height(IntrinsicSize.Min)) {
                    Box(Modifier.width(widths[0]).fillMaxHeight().background(BrandLight).border(0.5.dp, Border).padding(10.dp),
                        contentAlignment = Alignment.Center) {
                        Text(ex.arabic, fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif,
                            color = Brand, lineHeight = 32.sp, textAlign = TextAlign.Center)
                    }
                    Box(Modifier.width(widths[1]).fillMaxHeight().background(bg).border(0.5.dp, Border).padding(10.dp),
                        contentAlignment = Alignment.Center) {
                        Text(ex.urdu, fontSize = 14.sp, lineHeight = 24.sp, textAlign = TextAlign.Center)
                    }
                    Box(Modifier.width(widths[2]).fillMaxHeight().background(Amber).border(0.5.dp, Border).padding(10.dp),
                        contentAlignment = Alignment.Center) {
                        Text(ex.quran ?: "—", fontSize = 18.sp, fontFamily = FontFamily.Serif, lineHeight = 30.sp,
                            color = Color(0xFF1E293B), textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}

/* ============ سیکھیں ============ */
@Composable
fun LearnScreen(categories: List<Category>) {
    val en = LocalEn.current
    var selectedKey by remember { mutableStateOf(categories.first().key) }
    var subKey by remember { mutableStateOf("all") }
    var query by remember { mutableStateOf("") }

    val category = categories.first { it.key == selectedKey }
    val rows = if (query.isNotBlank()) searchRows(category, query) else filterRows(category, subKey)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            DropdownField(
                label = tr("بنیادی موضوع", "Main Topic"),
                selectedText = topicLabel(category.key, en, category.badge),
                options = categories.map { it.key to topicLabel(it.key, en, it.badge) },
                onSelect = { selectedKey = it; subKey = "all"; query = "" }
            )
        }
        item {
            DropdownField(
                label = tr("ذیلی زمرہ", "Sub Category"),
                selectedText = category.subCategories.firstOrNull { it.first == subKey }?.second
                    ?: category.subCategories.first().second,
                options = category.subCategories,
                onSelect = { subKey = it; query = "" }
            )
        }
        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                placeholder = { Text(tr("کوئی بھی علامت یا لفظ لکھیں...", "Search any sign or word..."), color = Muted) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Brand,
                    unfocusedBorderColor = Border
                )
            )
        }
        item { RuleCard(category, rows.size) }

        if (rows.isEmpty()) {
            item { Text(tr("کوئی مطابقت نہیں ملی۔", "No match found."), color = Muted) }
        } else {
            item {
                Text(
                    tr("📋 جدول (دائیں بائیں سرکا کر پورا دیکھیں ↔)", "📋 Table (swipe sideways to see all ↔)"),
                    fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Brand
                )
            }
            item { GrammarTable(category.columns, rows) }
        }

        if (category.examples.isNotEmpty()) {
            item {
                Text(
                    tr("📝 مثالیں", "📝 Examples"),
                    fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Brand,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
            item { ExamplesTable(category.examples) }
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
fun RuleCard(category: Category, count: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Border),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(category.title, fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Brand)
            Text(category.rule, fontSize = 15.sp, lineHeight = 28.sp, color = Color(0xFF1E293B))
            if (category.note.isNotBlank()) {
                Surface(color = Amber, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                    Text(
                        tr("نوٹ: ", "Note: ") + category.note,
                        fontSize = 13.sp, lineHeight = 24.sp, color = AmberText,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
            Text(tr("$count ریکارڈز موجود ہیں", "$count records"), fontSize = 12.sp, color = Muted)
        }
    }
}

/* ============ کوئز ============ */
data class Question(val prompt: String, val options: List<String>, val correct: Int)

fun scopeOptions(categories: List<Category>, en: Boolean): List<Pair<String, String>> =
    listOf("all" to (if (en) "All Topics" else "تمام موضوعات")) +
        categories.map { it.key to topicLabel(it.key, en, it.badge) }

fun scopeText(categories: List<Category>, key: String, en: Boolean): String =
    scopeOptions(categories, en).firstOrNull { it.first == key }?.second ?: (if (en) "All Topics" else "تمام موضوعات")

fun buildQuestions(categories: List<Category>, scopeKey: String, en: Boolean, count: Int = 10): List<Question> {
    val pool = if (scopeKey == "all") categories else categories.filter { it.key == scopeKey }
    val candidates = mutableListOf<Triple<Category, Row, Int>>()
    for (c in pool) for (r in c.rows) {
        candidates.add(Triple(c, r, 0))
        candidates.add(Triple(c, r, 1))
    }
    candidates.shuffle()

    val result = mutableListOf<Question>()
    val seen = mutableSetOf<String>()
    for ((c, r, type) in candidates) {
        if (result.size >= count) break
        val question: Question? = if (type == 0) {
            var exIdx = -1
            for (i in 1 until c.columns.size) if (r.cells.getOrElse(i) { "" }.isNotBlank()) exIdx = i
            if (exIdx < 2) null else {
                val value = r.cells[exIdx].replace("\n", " ")
                val clash = c.rows.any { it !== r && it.cells[exIdx] == r.cells[exIdx] && it.cells[0] != r.cells[0] }
                val wrong = c.rows.map { it.cells[0] }.filter { it != r.cells[0] }.distinct().shuffled().take(3)
                if (clash || wrong.isEmpty()) null else {
                    val opts = (wrong + r.cells[0]).shuffled()
                    val p = if (en)
                        "[${c.badge}]\n«$value»  (${c.columns[exIdx]})\n\nWhich «${c.columns[0]}» is correct?"
                    else
                        "[${c.badge}]\n«$value»  (${c.columns[exIdx]})\n\nصحیح «${c.columns[0]}» کون سا ہے؟"
                    Question(p, opts, opts.indexOf(r.cells[0]))
                }
            }
        } else {
            val ans = r.cells[1].replace("\n", " ")
            if (ans.isBlank() || ans.length > 80) null else {
                val wrong = c.rows.map { it.cells[1].replace("\n", " ") }
                    .filter { it != ans && it.isNotBlank() && it.length <= 80 }
                    .distinct().shuffled().take(3)
                if (wrong.isEmpty()) null else {
                    val opts = (wrong + ans).shuffled()
                    val p = if (en)
                        "[${c.badge}]\n«${r.cells[0]}»\n\nWhat is the correct «${c.columns[1]}» for it?"
                    else
                        "[${c.badge}]\n«${r.cells[0]}»\n\nاس کی صحیح «${c.columns[1]}» کون سی ہے؟"
                    Question(p, opts, opts.indexOf(ans))
                }
            }
        }
        if (question != null && seen.add(question.prompt)) result.add(question)
    }
    return result
}

@Composable
fun QuizScreen(categories: List<Category>) {
    val context = LocalContext.current
    val en = LocalEn.current
    var scope by remember { mutableStateOf("all") }
    var questions by remember { mutableStateOf<List<Question>>(emptyList()) }
    var started by remember { mutableStateOf(false) }
    var index by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var selected by remember { mutableStateOf<Int?>(null) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (!started) {
            Text(tr("❓ کوئز", "❓ Quiz"), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Brand)
            Text(
                tr("موضوع چنیں، 10 سوالات آئیں گے۔ ہر سوال کے 4 جواب میں سے ایک صحیح ہے۔",
                    "Choose a topic. You will get 10 questions, each with 4 options and one correct answer."),
                fontSize = 15.sp, lineHeight = 28.sp
            )
            DropdownField(
                label = tr("موضوع", "Topic"),
                selectedText = scopeText(categories, scope, en),
                options = scopeOptions(categories, en),
                onSelect = { scope = it }
            )
            BigButton(tr("کوئز شروع کریں", "Start Quiz"), onClick = {
                questions = buildQuestions(categories, scope, en)
                index = 0; score = 0; selected = null; started = true
            }, modifier = Modifier.fillMaxWidth())
        } else if (questions.isEmpty()) {
            Text(tr("اس موضوع میں کوئز کے لیے کافی ڈیٹا نہیں ہے۔", "Not enough data for a quiz on this topic."), fontSize = 16.sp)
            BigButton(tr("واپس", "Back"), onClick = { started = false }, modifier = Modifier.fillMaxWidth())
        } else if (index >= questions.size) {
            Text(tr("کوئز مکمل", "Quiz Complete"), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Brand)
            Surface(color = BrandLight, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Box(Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("$score / ${questions.size}", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = BrandDark)
                }
            }
            Text(
                when {
                    score == questions.size -> tr("ماشاء اللہ! سب جوابات صحیح ہیں۔", "MashaAllah! All answers are correct.")
                    score * 10 >= questions.size * 7 -> tr("بہت خوب! تھوڑی سی مزید مشق کافی ہے۔", "Great! A little more practice is enough.")
                    else -> tr("مزید مشق کریں، پہلے \"سیکھیں\" میں اس موضوع کو دیکھ لیں۔", "Practice more. Review this topic in \"Learn\" first.")
                },
                fontSize = 15.sp, lineHeight = 28.sp
            )
            BigButton(tr("دوبارہ کوئز", "Retry Quiz"), onClick = {
                questions = buildQuestions(categories, scope, en)
                index = 0; score = 0; selected = null
            }, modifier = Modifier.fillMaxWidth())
            BigButton(tr("موضوع بدلیں", "Change Topic"), onClick = { started = false }, filled = false, modifier = Modifier.fillMaxWidth())
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
                MixedText(q.prompt, size = 17, arabicSize = 23, bold = true, arabicColor = Brand,
                    align = TextAlign.Start, modifier = Modifier.fillMaxWidth().padding(16.dp))
            }
            q.options.forEachIndexed { i, opt ->
                val sel = selected
                val bg = when {
                    sel == null -> Color.White
                    i == q.correct -> Good
                    i == sel -> Bad
                    else -> Color.White
                }
                val borderColor = when {
                    sel == null -> Border
                    i == q.correct -> GoodText
                    i == sel -> BadText
                    else -> Border
                }
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = bg,
                    border = BorderStroke(1.5.dp, borderColor),
                    modifier = Modifier.fillMaxWidth().clickable(enabled = sel == null) {
                        selected = i
                        if (i == q.correct) score += 1
                    }
                ) {
                    MixedText(opt, size = 16, arabicSize = 21, align = TextAlign.Start, modifier = Modifier.padding(14.dp))
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
                            addResult(context, ScoreEntry("quiz", scopeText(categories, scope, en), score, questions.size, nowString()))
                        }
                        index += 1; selected = null
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/* ============ سیلف ٹیسٹ ============ */
data class CardItem(val cat: Category, val row: Row)

@Composable
fun SelfTestScreen(categories: List<Category>) {
    val context = LocalContext.current
    val en = LocalEn.current
    var scope by remember { mutableStateOf("all") }
    var deck by remember { mutableStateOf<List<CardItem>>(emptyList()) }
    var started by remember { mutableStateOf(false) }
    var index by remember { mutableStateOf(0) }
    var revealed by remember { mutableStateOf(false) }
    var known by remember { mutableStateOf(0) }
    var missed by remember { mutableStateOf<List<CardItem>>(emptyList()) }

    fun startWith(items: List<CardItem>) {
        deck = items; index = 0; revealed = false; known = 0; missed = emptyList(); started = true
    }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (!started) {
            Text(tr("📝 سیلف ٹیسٹ", "📝 Self Test"), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Brand)
            Text(
                tr("پہلے عنوان دیکھ کر خود یاد کریں، پھر \"جواب دکھائیں\" دبائیں اور ایمانداری سے بتائیں کہ آتا تھا یا نہیں۔",
                    "Look at the title and try to recall first. Then tap \"Show Answer\" and honestly mark whether you knew it."),
                fontSize = 15.sp, lineHeight = 28.sp
            )
            DropdownField(
                label = tr("موضوع", "Topic"),
                selectedText = scopeText(categories, scope, en),
                options = scopeOptions(categories, en),
                onSelect = { scope = it }
            )
            BigButton(tr("ٹیسٹ شروع کریں", "Start Test"), onClick = {
                val pool = if (scope == "all") categories else categories.filter { it.key == scope }
                val all = pool.flatMap { c -> c.rows.map { CardItem(c, it) } }.shuffled()
                startWith(if (scope == "all") all.take(15) else all)
            }, modifier = Modifier.fillMaxWidth())
        } else if (index >= deck.size) {
            Text(tr("ٹیسٹ مکمل", "Test Complete"), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Brand)
            Surface(color = BrandLight, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Box(Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                    Text(tr("آتا ہے: $known / ${deck.size}", "Known: $known / ${deck.size}"),
                        fontSize = 28.sp, fontWeight = FontWeight.Bold, color = BrandDark)
                }
            }
            if (missed.isNotEmpty()) {
                Text(tr("یاد کرنے والے:", "To revise:"), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BadText)
                missed.forEach { m ->
                    Surface(color = Bad, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                        Text("${m.cat.badge}: ${m.row.cells[0]}", fontSize = 14.sp, lineHeight = 26.sp,
                            modifier = Modifier.padding(10.dp))
                    }
                }
                BigButton(tr("صرف ان کو دوبارہ آزمائیں", "Retry only these"), onClick = { startWith(missed.shuffled()) },
                    modifier = Modifier.fillMaxWidth())
            } else {
                Text(tr("ماشاء اللہ! سب یاد تھا۔", "MashaAllah! You knew everything."),
                    fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Brand)
            }
            BigButton(tr("موضوع بدلیں", "Change Topic"), onClick = { started = false }, filled = false, modifier = Modifier.fillMaxWidth())
        } else {
            val item = deck[index]
            val cat = item.cat
            Text(
                tr("کارڈ ${index + 1} / ${deck.size}      آتا ہے: $known", "Card ${index + 1} / ${deck.size}      Known: $known"),
                fontSize = 13.sp, color = Muted
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Border),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.fillMaxWidth()) {
                    Box(Modifier.fillMaxWidth().background(BrandLight).padding(horizontal = 14.dp, vertical = 8.dp)) {
                        Text(cat.badge, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Brand)
                    }
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        MixedText(item.row.cells[0], size = 20, arabicSize = 25, bold = true, color = BrandDark,
                            arabicColor = Brand, align = TextAlign.Start)
                        if (revealed) {
                            for (i in 1 until cat.columns.size) {
                                val value = item.row.cells.getOrElse(i) { "" }
                                if (value.isBlank()) continue
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(cat.columns[i], fontSize = 12.sp, color = Muted)
                                    MixedText(
                                        value.replace("\n", "  "),
                                        size = 16,
                                        arabicSize = 22,
                                        forceArabic = i >= 3,
                                        align = TextAlign.Start
                                    )
                                }
                            }
                        } else {
                            Text(tr("پہلے خود یاد کریں، پھر جواب دکھائیں۔", "Try to recall first, then show the answer."),
                                fontSize = 14.sp, color = Muted)
                        }
                    }
                }
            }
            if (!revealed) {
                BigButton(tr("جواب دکھائیں", "Show Answer"), onClick = { revealed = true }, modifier = Modifier.fillMaxWidth())
            } else {
                BigButton(tr("✓ مجھے آتا ہے", "✓ I knew it"), onClick = {
                    val newKnown = known + 1
                    known = newKnown
                    if (index == deck.size - 1) {
                        addResult(context, ScoreEntry("self", scopeText(categories, scope, en), newKnown, deck.size, nowString()))
                    }
                    index += 1; revealed = false
                }, modifier = Modifier.fillMaxWidth())
                BigButton(tr("✗ یاد کرنا ہے", "✗ Need to revise"), onClick = {
                    missed = missed + item
                    if (index == deck.size - 1) {
                        addResult(context, ScoreEntry("self", scopeText(categories, scope, en), known, deck.size, nowString()))
                    }
                    index += 1; revealed = false
                }, filled = false, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

/* ============ اسکور بورڈ اور پروفائل ============ */
@Composable
fun StatBox(value: String, label: String, modifier: Modifier = Modifier) {
    Surface(color = BrandLight, shape = RoundedCornerShape(14.dp), modifier = modifier) {
        Column(Modifier.padding(vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = BrandDark)
            Text(label, fontSize = 11.sp, color = Brand)
        }
    }
}

@Composable
fun ScoreScreen(name: String, onNameChange: (String) -> Unit) {
    val context = LocalContext.current
    var results by remember { mutableStateOf(loadResults(context)) }
    var editing by remember { mutableStateOf(false) }
    var draft by remember { mutableStateOf(name) }

    val total = results.size
    val percents = results.map { if (it.n == 0) 0 else it.s * 100 / it.n }
    val avg = if (total == 0) 0 else percents.sum() / total
    val best = percents.maxOrNull() ?: 0

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Border),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    androidx.compose.foundation.layout.Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            Modifier.size(56.dp).clip(CircleShape).background(Brand),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(name.take(1), fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Column(Modifier.weight(1f)) {
                            Text(name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = BrandDark)
                            Text(tr("طالب علم", "Student"), fontSize = 12.sp, color = Muted)
                        }
                        Text(
                            if (editing) tr("منسوخ", "Cancel") else tr("نام بدلیں", "Edit name"),
                            fontSize = 13.sp, color = Brand, fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { editing = !editing; draft = name }.padding(6.dp)
                        )
                    }
                    if (editing) {
                        OutlinedTextField(
                            value = draft,
                            onValueChange = { draft = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Brand, unfocusedBorderColor = Border)
                        )
                        BigButton(tr("محفوظ کریں", "Save"), onClick = {
                            if (draft.isNotBlank()) { onNameChange(draft.trim()); editing = false }
                        }, modifier = Modifier.fillMaxWidth())
                    }
                    androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatBox("$total", tr("کل ٹیسٹ", "Tests"), Modifier.weight(1f))
                        StatBox("$avg%", tr("اوسط", "Average"), Modifier.weight(1f))
                        StatBox("$best%", tr("بہترین", "Best"), Modifier.weight(1f))
                    }
                }
            }
        }
        item { Text(tr("🏆 اسکور بورڈ", "🏆 Scoreboard"), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Brand) }
        if (results.isEmpty()) {
            item {
                Text(tr("ابھی کوئی نتیجہ نہیں۔ کوئز یا سیلف ٹیسٹ مکمل کریں۔",
                    "No results yet. Complete a quiz or self test."), color = Muted, lineHeight = 26.sp)
            }
        } else {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Border),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(Modifier.fillMaxWidth()) {
                        androidx.compose.foundation.layout.Row(Modifier.fillMaxWidth().background(Brand).padding(10.dp)) {
                            Text(tr("قسم", "Type"), Modifier.weight(1.1f), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(tr("موضوع", "Topic"), Modifier.weight(2f), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(tr("اسکور", "Score"), Modifier.weight(1f), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.Center)
                            Text(tr("تاریخ", "Date"), Modifier.weight(1.4f), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.Center)
                        }
                        results.forEachIndexed { idx, r ->
                            val pct = if (r.n == 0) 0 else r.s * 100 / r.n
                            val isQuiz = r.type == "quiz" || r.type == "کوئز"
                            androidx.compose.foundation.layout.Row(
                                Modifier.fillMaxWidth().background(if (idx % 2 == 0) Color.White else Page).padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    when {
                                        isQuiz -> tr("❓ کوئز", "❓ Quiz")
                                        r.type == "nahw" -> tr("📘 نحو", "📘 Nahw")
                                        r.type == "sarf" -> tr("🔧 صرف", "🔧 Sarf")
                                        r.type == "quran" -> tr("📖 قرآن", "📖 Quran")
                                        else -> tr("📝 ٹیسٹ", "📝 Test")
                                    },
                                    Modifier.weight(1.1f), fontSize = 12.sp, fontWeight = FontWeight.Bold
                                )
                                Text(r.topic, Modifier.weight(2f), fontSize = 11.sp, color = Muted, lineHeight = 18.sp)
                                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("${r.s}/${r.n}", fontSize = 14.sp, fontWeight = FontWeight.Bold,
                                        color = if (pct >= 70) GoodText else if (pct >= 40) AmberText else BadText)
                                    Text("$pct%", fontSize = 10.sp, color = Muted)
                                }
                                Text(r.date, Modifier.weight(1.4f), fontSize = 10.sp, color = Muted, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }
            item {
                BigButton(tr("اسکور بورڈ صاف کریں", "Clear Scoreboard"), onClick = {
                    clearResults(context); results = emptyList()
                }, filled = false, modifier = Modifier.fillMaxWidth())
            }
        }
        item { Spacer(Modifier.height(12.dp)) }
    }
}
