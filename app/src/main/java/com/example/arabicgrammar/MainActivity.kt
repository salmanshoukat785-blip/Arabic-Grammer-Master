package com.example.arabicgrammar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Brand = Color(0xFF065F46)
private val BrandDark = Color(0xFF022C22)
private val BrandLight = Color(0xFFDCFCE7)
private val Amber = Color(0xFFFFFBEB)

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
    var selectedKey by remember { mutableStateOf(categories.first().key) }
    var subKey by remember { mutableStateOf("all") }
    var query by remember { mutableStateOf("") }

    val category = categories.first { it.key == selectedKey }
    val rows = if (query.isNotBlank()) searchRows(category, query) else filterRows(category, subKey)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("عربی گرامر ماسٹر", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandDark, titleContentColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF8FAFC)),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { c ->
                        FilterChip(
                            selected = c.key == selectedKey,
                            onClick = { selectedKey = c.key; subKey = "all"; query = "" },
                            label = { Text(c.badge) }
                        )
                    }
                }
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(category.subCategories) { (k, label) ->
                        AssistChip(
                            onClick = { subKey = k; query = "" },
                            label = { Text(label, fontSize = 12.sp) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (k == subKey && query.isBlank()) BrandLight else Color.White
                            )
                        )
                    }
                }
            }
            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("اس زمرے میں تلاش کریں...") },
                    singleLine = true
                )
            }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(Modifier.padding(14.dp)) {
                        Text(category.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Brand)
                        Spacer(Modifier.height(6.dp))
                        Text(category.rule, fontSize = 15.sp, lineHeight = 28.sp)
                        if (category.note.isNotBlank()) {
                            Spacer(Modifier.height(6.dp))
                            Text("نوٹ: ${category.note}", fontSize = 13.sp, color = Color(0xFFB45309), lineHeight = 24.sp)
                        }
                        Spacer(Modifier.height(6.dp))
                        Text("${rows.size} ریکارڈز موجود ہیں", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
            if (rows.isEmpty()) {
                item { Text("اس زمرے میں کوئی مطابقت نہیں ملی۔", color = Color.Gray) }
            }
            items(rows) { row -> RowCard(category.columns, row) }

            if (category.examples.isNotEmpty()) {
                item { Text("مثالیں", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Brand) }
                items(category.examples) { ex -> ExampleCard(ex) }
            }
        }
    }
}

@Composable
fun RowCard(columns: List<String>, row: Row) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            columns.forEachIndexed { i, col ->
                val value = row.cells.getOrElse(i) { "" }
                if (value.isNotBlank()) {
                    Row(verticalAlignment = Alignment.Top) {
                        Text("$col: ", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.widthIn(max = 110.dp))
                        Text(
                            value,
                            fontSize = if (i == 0) 16.sp else 18.sp,
                            fontWeight = if (i == 0 || i >= 3) FontWeight.Bold else FontWeight.Normal,
                            fontFamily = if (i == 1) FontFamily.Serif else FontFamily.Default,
                            color = if (i >= 3) Brand else Color.Unspecified
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExampleCard(ex: Example) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(ex.arabic, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Brand, fontFamily = FontFamily.Serif)
            Text(ex.urdu, fontSize = 13.sp, color = Color(0xFF047857))
            if (ex.quran != null) {
                Spacer(Modifier.height(6.dp))
                Text("قرآنی شاہد:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                Surface(color = Amber, shape = RoundedCornerShape(6.dp)) {
                    Text(ex.quran, fontSize = 17.sp, fontFamily = FontFamily.Serif, modifier = Modifier.padding(6.dp))
                }
            }
        }
    }
}
