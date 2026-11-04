package com.example.arabicgrammar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Brand = Color(0xFF047857)
private val BrandDark = Color(0xFF022C22)
private val BrandLight = Color(0xFFD1FAE5)
private val Page = Color(0xFFF1F5F9)
private val Amber = Color(0xFFFEF3C7)
private val AmberText = Color(0xFF92400E)
private val Border = Color(0xFFE2E8F0)
private val Muted = Color(0xFF64748B)
private val Good = Color(0xFFDCFCE7)
private val Bad = Color(0xFFFEE2E2)
private val BadText = Color(0xFFB91C1C)

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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(primary = Brand)) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    GrammarApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrammarApp() {
    val context = LocalContext.current
    val categories = remember { loadDatabase(context) }
    var tab by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = Page,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("عربی گرامر ماسٹر", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = BrandDark,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                val items = listOf("📖" to "سیکھیں", "❓" to "کوئز", "📝" to "سیلف ٹیسٹ")
                items.forEachIndexed { i, (icon, label) ->
                    NavigationBarItem(
                        selected = tab == i,
                        onClick = { tab = i },
                        icon = { Text(icon, fontSize = 20.sp) },
                        label = { Text(label, fontSize = 12.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedTextColor = Brand,
                            indicatorColor = BrandLight
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (tab) {
                0 -> LearnScreen(categories)
                1 -> QuizScreen(categories)
                else -> SelfTestScreen(categories)
            }
        }
    }
}

/* ---------------- ڈراپ ڈاؤن ---------------- */

@Composable
fun DropdownField(
    label: String,
    selectedText: String,
    options: List<Pair<String, String>>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
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
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A)
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
                        text = { Text(text, fontSize = 15.sp) },
                        onClick = { onSelect(key); expanded = false }
                    )
                }
            }
        }
    }
}

/* ---------------- سیکھیں ---------------- */

@Composable
fun LearnScreen(categories: List<Category>) {
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
                label = "بنیادی موضوع (Main Topic)",
                selectedText = mainLabels[category.key] ?: category.badge,
                options = categories.map { it.key to (mainLabels[it.key] ?: it.badge) },
                onSelect = { selectedKey = it; subKey = "all"; query = "" }
            )
        }
        item {
            DropdownField(
                label = "ذیلی زمرہ (Sub Category)",
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
                placeholder = { Text("کوئی بھی علامت یا لفظ لکھیں...", color = Muted) },
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
            item { Text("اس زمرے میں کوئی مطابقت نہیں ملی۔", color = Muted) }
        }
        items(rows) { row -> RowCard(category.columns, row) }

        if (category.examples.isNotEmpty()) {
            item {
                Text(
                    "مثالیں",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Brand,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
            items(category.examples) { ex -> ExampleCard(ex) }
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
                        "نوٹ: ${category.note}",
                        fontSize = 13.sp,
                        lineHeight = 24.sp,
                        color = AmberText,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
            Text("$count ریکارڈز موجود ہیں", fontSize = 12.sp, color = Muted)
        }
    }
}

@Composable
fun RowCard(columns: List<String>, row: Row) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Border),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.fillMaxWidth()) {
            Box(Modifier.fillMaxWidth().background(BrandLight).padding(horizontal = 14.dp, vertical = 10.dp)) {
                Text(
                    row.cells[0],
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandDark,
                    lineHeight = 26.sp
                )
            }
            Column(
                Modifier.fillMaxWidth().padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (i in 1 until columns.size) {
                    val value = row.cells.getOrElse(i) { "" }
                    if (value.isBlank()) continue
                    val isExample = i >= 3
                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(columns[i], fontSize = 12.sp, color = Muted)
                        if (isExample) {
                            Surface(
                                color = Color(0xFFF0FDF4),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    value,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif,
                                    color = Brand,
                                    lineHeight = 36.sp,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        } else {
                            Text(
                                value,
                                fontSize = if (i == 1) 17.sp else 16.sp,
                                lineHeight = 28.sp,
                                color = Color(0xFF0F172A)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExampleCard(ex: Example) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Border),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                ex.arabic,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Brand,
                fontFamily = FontFamily.Serif,
                lineHeight = 38.sp
            )
            Text(ex.urdu, fontSize = 14.sp, color = Color(0xFF334155), lineHeight = 26.sp)
            if (ex.quran != null) {
                Surface(color = Amber, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("قرآنی شاہد", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AmberText)
                        Text(
                            ex.quran,
                            fontSize = 19.sp,
                            fontFamily = FontFamily.Serif,
                            lineHeight = 32.sp,
                            color = Color(0xFF1E293B)
                        )
                    }
                }
            }
        }
    }
}

/* ---------------- مشترک بٹن اور دائرہ کار ---------------- */

@Composable
fun BigButton(text: String, onClick: () -> Unit, filled: Boolean = true, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (filled) Brand else Color.White,
        border = BorderStroke(1.5.dp, Brand),
        modifier = modifier.clickable { onClick() }
    ) {
        Box(Modifier.padding(vertical = 14.dp, horizontal = 12.dp), contentAlignment = Alignment.Center) {
            Text(
                text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (filled) Color.White else Brand
            )
        }
    }
}

fun scopeOptions(categories: List<Category>): List<Pair<String, String>> =
    listOf("all" to "تمام موضوعات") + categories.map { it.key to (mainLabels[it.key] ?: it.badge) }

fun scopeText(categories: List<Category>, key: String): String =
    scopeOptions(categories).firstOrNull { it.first == key }?.second ?: "تمام موضوعات"

/* ---------------- کوئز ---------------- */

data class Question(val prompt: String, val options: List<String>, val correct: Int)

fun buildQuestions(categories: List<Category>, scopeKey: String, count: Int = 10): List<Question> {
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
                val value = r.cells[exIdx]
                val clash = c.rows.any { it !== r && it.cells[exIdx] == value && it.cells[0] != r.cells[0] }
                val wrong = c.rows.map { it.cells[0] }.filter { it != r.cells[0] }.distinct().shuffled().take(3)
                if (clash || wrong.isEmpty()) null else {
                    val opts = (wrong + r.cells[0]).shuffled()
                    Question(
                        "[${c.badge}]\n«$value»  (${c.columns[exIdx]})\n\nصحیح «${c.columns[0]}» کون سا ہے؟",
                        opts, opts.indexOf(r.cells[0])
                    )
                }
            }
        } else {
            val ans = r.cells[1]
            if (ans.isBlank() || ans.length > 80) null else {
                val wrong = c.rows.map { it.cells[1] }
                    .filter { it != ans && it.isNotBlank() && it.length <= 80 }
                    .distinct().shuffled().take(3)
                if (wrong.isEmpty()) null else {
                    val opts = (wrong + ans).shuffled()
                    Question(
                        "[${c.badge}]\n«${r.cells[0]}»\n\nاس کی صحیح «${c.columns[1]}» کون سی ہے؟",
                        opts, opts.indexOf(ans)
                    )
                }
            }
        }
        if (question != null && seen.add(question.prompt)) result.add(question)
    }
    return result
}

@Composable
fun QuizScreen(categories: List<Category>) {
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
            Text("کوئز", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Brand)
            Text("موضوع چنیں، 10 سوالات آئیں گے۔ ہر سوال کے 4 جواب میں سے ایک صحیح ہے۔", fontSize = 15.sp, lineHeight = 28.sp)
            DropdownField(
                label = "موضوع",
                selectedText = scopeText(categories, scope),
                options = scopeOptions(categories),
                onSelect = { scope = it }
            )
            BigButton("کوئز شروع کریں", onClick = {
                questions = buildQuestions(categories, scope)
                index = 0; score = 0; selected = null; started = true
            }, modifier = Modifier.fillMaxWidth())
        } else if (questions.isEmpty()) {
            Text("اس موضوع میں کوئز کے لیے کافی ڈیٹا نہیں ہے۔", fontSize = 16.sp)
            BigButton("واپس", onClick = { started = false }, modifier = Modifier.fillMaxWidth())
        } else if (index >= questions.size) {
            Text("کوئز مکمل", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Brand)
            Surface(color = BrandLight, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Box(Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("$score / ${questions.size}", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = BrandDark)
                }
            }
            Text(
                when {
                    score == questions.size -> "ماشاء اللہ! سب جوابات صحیح ہیں۔"
                    score * 10 >= questions.size * 7 -> "بہت خوب! تھوڑی سی مزید مشق کافی ہے۔"
                    else -> "مزید مشق کریں، پہلے \"سیکھیں\" میں اس موضوع کو دیکھ لیں۔"
                },
                fontSize = 15.sp, lineHeight = 28.sp
            )
            BigButton("دوبارہ کوئز", onClick = {
                questions = buildQuestions(categories, scope)
                index = 0; score = 0; selected = null
            }, modifier = Modifier.fillMaxWidth())
            BigButton("موضوع بدلیں", onClick = { started = false }, filled = false, modifier = Modifier.fillMaxWidth())
        } else {
            val q = questions[index]
            Text("سوال ${index + 1} / ${questions.size}      درست: $score", fontSize = 13.sp, color = Muted)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Border),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(q.prompt, fontSize = 18.sp, lineHeight = 32.sp, fontWeight = FontWeight.Bold,
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
                val borderColor = when {
                    sel == null -> Border
                    i == q.correct -> Brand
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
                    Text(opt, fontSize = 16.sp, lineHeight = 28.sp, modifier = Modifier.padding(14.dp))
                }
            }
            if (selected != null) {
                Text(
                    if (selected == q.correct) "✅ درست جواب" else "❌ غلط، صحیح جواب سبز رنگ میں ہے",
                    fontSize = 15.sp, fontWeight = FontWeight.Bold,
                    color = if (selected == q.correct) Brand else BadText
                )
                BigButton(
                    if (index == questions.size - 1) "نتیجہ دیکھیں" else "اگلا سوال",
                    onClick = { index += 1; selected = null },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/* ---------------- سیلف ٹیسٹ ---------------- */

data class CardItem(val cat: Category, val row: Row)

@Composable
fun SelfTestScreen(categories: List<Category>) {
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
            Text("سیلف ٹیسٹ", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Brand)
            Text(
                "پہلے عنوان دیکھ کر خود یاد کریں، پھر \"جواب دکھائیں\" دبائیں اور ایمانداری سے بتائیں کہ آتا تھا یا نہیں۔",
                fontSize = 15.sp, lineHeight = 28.sp
            )
            DropdownField(
                label = "موضوع",
                selectedText = scopeText(categories, scope),
                options = scopeOptions(categories),
                onSelect = { scope = it }
            )
            BigButton("ٹیسٹ شروع کریں", onClick = {
                val pool = if (scope == "all") categories else categories.filter { it.key == scope }
                val all = pool.flatMap { c -> c.rows.map { CardItem(c, it) } }.shuffled()
                startWith(if (scope == "all") all.take(15) else all)
            }, modifier = Modifier.fillMaxWidth())
        } else if (index >= deck.size) {
            Text("ٹیسٹ مکمل", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Brand)
            Surface(color = BrandLight, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Box(Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("آتا ہے: $known / ${deck.size}", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = BrandDark)
                }
            }
            if (missed.isNotEmpty()) {
                Text("یاد کرنے والے:", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BadText)
                missed.forEach { m ->
                    Surface(color = Bad, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                        Text("${m.cat.badge}: ${m.row.cells[0]}", fontSize = 14.sp, lineHeight = 26.sp,
                            modifier = Modifier.padding(10.dp))
                    }
                }
                BigButton("صرف ان کو دوبارہ آزمائیں", onClick = { startWith(missed.shuffled()) },
                    modifier = Modifier.fillMaxWidth())
            } else {
                Text("ماشاء اللہ! سب یاد تھا۔", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Brand)
            }
            BigButton("موضوع بدلیں", onClick = { started = false }, filled = false, modifier = Modifier.fillMaxWidth())
        } else {
            val item = deck[index]
            val cat = item.cat
            Text("کارڈ ${index + 1} / ${deck.size}      آتا ہے: $known", fontSize = 13.sp, color = Muted)
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
                        Text(item.row.cells[0], fontSize = 22.sp, fontWeight = FontWeight.Bold,
                            lineHeight = 36.sp, color = BrandDark)
                        if (revealed) {
                            for (i in 1 until cat.columns.size) {
                                val value = item.row.cells.getOrElse(i) { "" }
                                if (value.isBlank()) continue
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(cat.columns[i], fontSize = 12.sp, color = Muted)
                                    Text(
                                        value,
                                        fontSize = if (i >= 3) 22.sp else 16.sp,
                                        fontWeight = if (i >= 3) FontWeight.Bold else FontWeight.Normal,
                                        fontFamily = if (i >= 3) FontFamily.Serif else FontFamily.Default,
                                        color = if (i >= 3) Brand else Color(0xFF0F172A),
                                        lineHeight = if (i >= 3) 36.sp else 28.sp
                                    )
                                }
                            }
                        } else {
                            Text("پہلے خود یاد کریں، پھر جواب دکھائیں۔", fontSize = 14.sp, color = Muted)
                        }
                    }
                }
            }
            if (!revealed) {
                BigButton("جواب دکھائیں", onClick = { revealed = true }, modifier = Modifier.fillMaxWidth())
            } else {
                BigButton("✓ مجھے آتا ہے", onClick = {
                    known += 1; index += 1; revealed = false
                }, modifier = Modifier.fillMaxWidth())
                BigButton("✗ یاد کرنا ہے", onClick = {
                    missed = missed + item; index += 1; revealed = false
                }, filled = false, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
