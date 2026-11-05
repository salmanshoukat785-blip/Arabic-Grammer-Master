package com.example.arabicgrammar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* ============ اسکرینوں کے نمبر ============ */
const val R_HOME = 0
const val R_LEARN = 1
const val R_NAHW = 2
const val R_QURAN = 3
const val R_QUIZ = 4
const val R_SELF = 5
const val R_SCORE = 6
const val R_PROFILE = 7

/* ============ ہوم اسکرین ============ */
@Composable
fun HomeScreen(
    name: String,
    categories: List<Category>,
    onOpen: (Int) -> Unit,
    onToggleLang: () -> Unit
) {
    val context = LocalContext.current
    val en = LocalEn.current
    val results = remember { loadResults(context) }

    val tests = results.size
    var correct = 0
    var percentSum = 0
    for (r in results) {
        correct += r.s
        percentSum += if (r.n == 0) 0 else r.s * 100 / r.n
    }
    val avg = if (tests == 0) 0 else percentSum / tests

    // آج کا سبق: ہر دن ایک نیا باب
    val dayOfYear = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
    val today = if (categories.isEmpty()) null else categories[dayOfYear % categories.size]

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Box(Modifier.fillMaxWidth()) {
            HomeHeader(name, onToggleLang)

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 184.dp, start = 14.dp, end = 14.dp, bottom = 18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // ---- آج کا سبق + پیش رفت ----
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 6.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    androidx.compose.foundation.layout.Row(
                        Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // پیش رفت کا دائرہ
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(Modifier.size(78.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(
                                    progress = avg / 100f,
                                    modifier = Modifier.size(78.dp),
                                    color = Color(0xFF22C55E),
                                    trackColor = Color(0xFFE5E7EB),
                                    strokeWidth = 8.dp
                                )
                                Text("$avg%", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BrandDark)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(tr("تعلیمی پیش رفت", "Progress"), fontSize = 11.sp, color = Muted)
                        }

                        // آج کا سبق
                        Column(
                            Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Surface(shape = RoundedCornerShape(14.dp), color = Brand) {
                                Text(
                                    tr("▶  آج کا سبق", "▶  Today's lesson"),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                                )
                            }
                            Text(
                                if (today == null) "" else topicLabel(today.key, en, today.title),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandDark,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = BrandDark,
                                modifier = Modifier.clickable { onOpen(R_LEARN) }
                            ) {
                                Text(
                                    tr("سبق شروع کریں", "Start lesson"),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }

                        // کتاب
                        Box(
                            Modifier.size(78.dp).clip(RoundedCornerShape(20.dp)).background(BrandLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📖", fontSize = 42.sp)
                        }
                    }
                }

                // ---- اعداد و شمار ----
                androidx.compose.foundation.layout.Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard("🏆", "$correct", tr("کل اسکور", "Total score"), Color(0xFFFFF4D6), Modifier.weight(1f))
                    StatCard("📝", "$tests", tr("ٹیسٹ", "Tests"), Color(0xFFE3F6EA), Modifier.weight(1f))
                    StatCard("📊", "$avg%", tr("اوسط", "Average"), Color(0xFFEDE7FB), Modifier.weight(1f))
                }

                // ---- کورسز کا عنوان ----
                androidx.compose.foundation.layout.Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(shape = RoundedCornerShape(16.dp), color = BrandDark) {
                        Text(
                            tr("📚  ہمارے کورسز", "📚  Our Courses"),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = BrandLight,
                        modifier = Modifier.clickable { onOpen(R_LEARN) }
                    ) {
                        Text(
                            tr("تمام کورسز دیکھیں", "View all"),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandDark,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }

                // ---- کورسز (پہلی قطار) ----
                androidx.compose.foundation.layout.Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CourseTile(
                        "🕌", tr("قرآن فہمی", "Quran"),
                        tr("قرآن کی آیات کی تفہیم", "Understand the verses"),
                        tr("آیات", "Verses"),
                        Color(0xFF2F80ED), Color(0xFF0D47A1),
                        Modifier.weight(1f)
                    ) { onOpen(R_QURAN) }
                    CourseTile(
                        "📖", tr("عربی گرامر", "Arabic Grammar"),
                        tr("اعراب، عدد، جنس اور مزید", "Cases, number, gender"),
                        tr("${categories.size} ابواب", "${categories.size} chapters"),
                        Color(0xFF8B5CF6), Color(0xFF5B21B6),
                        Modifier.weight(1f)
                    ) { onOpen(R_LEARN) }
                    CourseTile(
                        "📘", tr("نحو و صرف", "Nahw & Sarf"),
                        tr("جملے اور الفاظ کے اصول", "Sentence and word rules"),
                        tr("اسباق", "Lessons"),
                        Color(0xFF22C55E), Color(0xFF15803D),
                        Modifier.weight(1f)
                    ) { onOpen(R_NAHW) }
                }

                // ---- کورسز (دوسری قطار) ----
                androidx.compose.foundation.layout.Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CourseTile(
                        "📝", tr("سیلف ٹیسٹ", "Self Test"),
                        tr("اپنی تیاری جانچیں", "Check your preparation"),
                        tr("شروع کریں", "Start"),
                        Color(0xFFFB923C), Color(0xFFEA580C),
                        Modifier.weight(1f)
                    ) { onOpen(R_SELF) }
                    CourseTile(
                        "❓", tr("کوئز", "Quiz"),
                        tr("اپنے علم کا امتحان لیں", "Test your knowledge"),
                        tr("شروع کریں", "Start"),
                        Color(0xFF14B8A6), Color(0xFF0F766E),
                        Modifier.weight(1f)
                    ) { onOpen(R_QUIZ) }
                    CourseTile(
                        "🏆", tr("میرا اسکور", "My Score"),
                        tr("اپنی کارکردگی دیکھیں", "See your progress"),
                        tr("تفصیلات", "Details"),
                        Color(0xFFFBBF24), Color(0xFFD97706),
                        Modifier.weight(1f)
                    ) { onOpen(R_SCORE) }
                }

                // ---- مشورہ ----
                Surface(shape = RoundedCornerShape(18.dp), color = BrandLight, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        tr("💡  روزانہ تھوڑا تھوڑا سیکھیں، ان شاء اللہ جلد ماہر بن جائیں گے۔",
                            "💡  Learn a little every day and you will improve quickly."),
                        fontSize = 13.sp,
                        color = BrandDark,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 14.dp)
                    )
                }
            }
        }
    }
}

/* ============ اوپر کی نیلی پٹی ============ */
@Composable
private fun HomeHeader(name: String, onToggleLang: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(226.dp)
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0A3D91), Color(0xFF1565C0), Color(0xFF3B8FE0))
                )
            )
    ) {
        // سجاوٹ
        Text(
            "🕌",
            fontSize = 84.sp,
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 10.dp, bottom = 54.dp)
        )
        Text(
            "✨",
            fontSize = 26.sp,
            modifier = Modifier.align(Alignment.TopEnd).padding(end = 22.dp, top = 70.dp)
        )

        // زبان کا بٹن
        Box(Modifier.align(Alignment.TopStart).padding(14.dp)) {
            LangButton(onToggleLang)
        }

        // عنوان
        Column(
            Modifier.align(Alignment.TopCenter).padding(top = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            QuranLogo(52.dp)
            Spacer(Modifier.height(4.dp))
            Text(
                "Arabic Learning",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                "Academy",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFC83D)
            )
        }

        // سلام
        Column(
            Modifier.align(Alignment.BottomStart).padding(start = 18.dp, bottom = 52.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                tr("السلام علیکم، $name!", "Assalamu Alaikum, $name!"),
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                tr("آئیں عربی سیکھنے کا سفر جاری رکھیں", "Let's continue your Arabic journey"),
                fontSize = 12.sp,
                color = Color(0xFFE0F2FE)
            )
        }
    }
}

@Composable
private fun StatCard(emoji: String, value: String, label: String, bg: Color, modifier: Modifier) {
    Surface(shape = RoundedCornerShape(18.dp), color = bg, modifier = modifier) {
        Column(
            Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 24.sp)
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = BrandDark)
            Text(label, fontSize = 11.sp, color = Muted, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun CourseTile(
    emoji: String,
    title: String,
    subtitle: String,
    footer: String,
    top: Color,
    bottom: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier
            .height(160.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.verticalGradient(listOf(top, bottom)))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(emoji, fontSize = 34.sp)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Text(
                subtitle,
                fontSize = 10.sp,
                color = Color(0xFFF1F5F9),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        Surface(shape = RoundedCornerShape(12.dp), color = Color.White, modifier = Modifier.fillMaxWidth()) {
            Text(
                footer,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = bottom,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
            )
        }
    }
}

/* ============ پروفائل ============ */
@Composable
fun ProfileScreen(name: String, onNameChange: (String) -> Unit, onToggleLang: () -> Unit) {
    var draft by remember { mutableStateOf(name) }
    var saved by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        QuranLogo(110.dp)
        Text(name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = BrandDark)
        Text(
            tr("آپ کا نام اسکور کے ساتھ محفوظ ہوتا ہے۔", "Your name is saved with your scores."),
            fontSize = 13.sp,
            color = Muted,
            textAlign = TextAlign.Center
        )
        OutlinedTextField(
            value = draft,
            onValueChange = { draft = it; saved = false },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            placeholder = { Text(tr("آپ کا نام", "Your name"), color = Muted) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Brand, unfocusedBorderColor = Border)
        )
        BigButton(
            tr("نام محفوظ کریں", "Save name"),
            onClick = {
                if (draft.isNotBlank()) {
                    onNameChange(draft.trim())
                    saved = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        if (saved) {
            Text(tr("✔ نام محفوظ ہو گیا", "✔ Name saved"), fontSize = 14.sp, color = GoodText)
        }
        BigButton(
            tr("English", "اردو"),
            onClick = onToggleLang,
            filled = false,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
