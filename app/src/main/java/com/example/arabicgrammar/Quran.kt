package com.example.arabicgrammar

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* ============ ڈیٹا ============ */
/** kind: 0 اسم، 1 فعل، 2 حرف */
data class QWord(val ar: String, val kind: Int, val i3rab: Bi)

data class QVerse(
    val id: String,
    val surah: Bi,
    val ref: String,
    val text: String,
    val meaning: Bi,
    val words: List<QWord>,
    val note: Bi? = null
)

fun kindLabel(k: Int, en: Boolean): String = when (k) {
    0 -> if (en) "Noun" else "اسم"
    1 -> if (en) "Verb" else "فعل"
    else -> if (en) "Particle" else "حرف"
}

/** (پس منظر، متن کا رنگ) */
fun kindColors(k: Int): Pair<Color, Color> = when (k) {
    0 -> BrandLight to BrandDark
    1 -> Good to GoodText
    else -> Amber to AmberText
}

private val sFatiha = Bi("سورۃ الفاتحہ", "Surah Al-Fatiha")
private val sIkhlas = Bi("سورۃ الاخلاص", "Surah Al-Ikhlas")
private val sKawthar = Bi("سورۃ الکوثر", "Surah Al-Kawthar")
private val sAsr = Bi("سورۃ العصر", "Surah Al-'Asr")
private val sNas = Bi("سورۃ الناس", "Surah An-Nas")
private val sFalaq = Bi("سورۃ الفلق", "Surah Al-Falaq")
private val sSharh = Bi("سورۃ الشرح", "Surah Ash-Sharh")

val quranVerses: List<QVerse> = listOf(
    QVerse(
        "f2", sFatiha, "1:2", "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ",
        Bi("تمام تعریفیں اللہ کے لیے ہیں جو سارے جہانوں کا پالنے والا ہے", "All praise is for Allah, Lord of all the worlds"),
        listOf(
            QWord("الْحَمْدُ", 0, Bi("مبتدا مرفوع؛ علامتِ رفع: ضمہ", "Subject (mubtada'), marfu'; sign: damma")),
            QWord("لِ", 2, Bi("حرفِ جر", "Preposition")),
            QWord("اللَّهِ", 0, Bi("لفظِ جلالہ، مجرور؛ علامتِ جر: کسرہ۔ جار مجرور مل کر خبر ہیں",
                "The name Allah, majrur; sign: kasra. Together with the preposition it forms the predicate (khabar)")),
            QWord("رَبِّ", 0, Bi("اللہ کی صفت (نعت)، مجرور؛ علامتِ جر: کسرہ؛ مضاف ہے",
                "Adjective of Allah (na't), majrur; sign: kasra; first term of an idafa")),
            QWord("الْعَالَمِينَ", 0, Bi("مضاف الیہ مجرور؛ علامتِ جر: یاء", "Second term of the idafa (mudaf ilayh), majrur; sign: ya"))
        )
    ),
    QVerse(
        "f4", sFatiha, "1:4", "مَالِكِ يَوْمِ الدِّينِ",
        Bi("وہ روزِ جزا کا مالک ہے", "Master of the Day of Judgment"),
        listOf(
            QWord("مَالِكِ", 0, Bi("اللہ کی صفت (نعت)، مجرور؛ علامتِ جر: کسرہ؛ مضاف ہے",
                "Adjective of Allah (na't), majrur; sign: kasra; first term of an idafa")),
            QWord("يَوْمِ", 0, Bi("مضاف الیہ مجرور؛ علامتِ جر: کسرہ؛ آگے والے لفظ کا مضاف بھی ہے",
                "Mudaf ilayh, majrur; sign: kasra; also the first term of the next idafa")),
            QWord("الدِّينِ", 0, Bi("مضاف الیہ مجرور؛ علامتِ جر: کسرہ", "Mudaf ilayh, majrur; sign: kasra"))
        )
    ),
    QVerse(
        "f5", sFatiha, "1:5", "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ",
        Bi("ہم تیری ہی عبادت کرتے ہیں اور تجھ ہی سے مدد مانگتے ہیں", "You alone we worship, and You alone we ask for help"),
        listOf(
            QWord("إِيَّاكَ", 0, Bi("ضمیرِ منفصل، مفعول بہ مقدم، محلِ نصب (مبنی)۔ آگے لانے سے مطلب بنتا ہے: صرف تیری",
                "Detached pronoun, object placed first (maf'ul bih muqaddam). Fronting gives the meaning: You alone")),
            QWord("نَعْبُدُ", 1, Bi("فعل مضارع مرفوع؛ علامتِ رفع: ضمہ؛ فاعل: ضمیر مستتر (نحن)",
                "Present verb, marfu'; sign: damma; doer: hidden pronoun (we)")),
            QWord("وَ", 2, Bi("حرفِ عطف", "Conjunction")),
            QWord("إِيَّاكَ", 0, Bi("ضمیرِ منفصل، مفعول بہ مقدم، محلِ نصب (مبنی)", "Detached pronoun, object placed first")),
            QWord("نَسْتَعِينُ", 1, Bi("فعل مضارع مرفوع؛ علامتِ رفع: ضمہ؛ فاعل: ضمیر مستتر (نحن)",
                "Present verb, marfu'; sign: damma; doer: hidden pronoun (we)"))
        )
    ),
    QVerse(
        "f6", sFatiha, "1:6", "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ",
        Bi("ہمیں سیدھے راستے کی ہدایت دے", "Guide us to the straight path"),
        listOf(
            QWord("اهْدِ", 1, Bi("فعلِ امر؛ فاعل: ضمیر مستتر (أنت)", "Command verb; doer: hidden pronoun (you)")),
            QWord("نَا", 0, Bi("ضمیرِ متصل، مفعول بہ، محلِ نصب (مبنی)", "Attached pronoun, object (us)")),
            QWord("الصِّرَاطَ", 0, Bi("مفعول بہ منصوب؛ علامتِ نصب: فتحہ", "Object, mansub; sign: fatha")),
            QWord("الْمُسْتَقِيمَ", 0, Bi("الصراط کی صفت (نعت) منصوب؛ علامتِ نصب: فتحہ", "Adjective of al-sirat (na't), mansub; sign: fatha"))
        )
    ),
    QVerse(
        "i1", sIkhlas, "112:1", "قُلْ هُوَ اللَّهُ أَحَدٌ",
        Bi("کہہ دیجیے: وہ اللہ ایک ہے", "Say: He is Allah, the One"),
        listOf(
            QWord("قُلْ", 1, Bi("فعلِ امر؛ فاعل: ضمیر مستتر (أنت)", "Command verb; doer: hidden pronoun (you)")),
            QWord("هُوَ", 0, Bi("ضمیرِ منفصل (ضمیرِ شان)، مبتدا، محلِ رفع (مبنی)", "Detached pronoun (pronoun of the matter), subject, in raf'")),
            QWord("اللَّهُ", 0, Bi("لفظِ جلالہ، مبتدا ثانی مرفوع؛ علامتِ رفع: ضمہ", "The name Allah, second subject, marfu'; sign: damma")),
            QWord("أَحَدٌ", 0, Bi("خبر مرفوع؛ علامتِ رفع: تنوینِ ضمہ", "Predicate (khabar), marfu'; sign: tanween damma"))
        ),
        Bi("ایک اور مشہور اعراب: هُوَ مبتدا، اللَّهُ خبر، أَحَدٌ خبر ثانی۔",
            "Another well-known analysis: هُوَ is the subject, اللَّهُ the predicate, and أَحَدٌ a second predicate.")
    ),
    QVerse(
        "i2", sIkhlas, "112:2", "اللَّهُ الصَّمَدُ",
        Bi("اللہ بے نیاز ہے (سب اس کے محتاج ہیں)", "Allah is the Eternal Refuge"),
        listOf(
            QWord("اللَّهُ", 0, Bi("لفظِ جلالہ، مبتدا مرفوع؛ علامتِ رفع: ضمہ", "The name Allah, subject, marfu'; sign: damma")),
            QWord("الصَّمَدُ", 0, Bi("خبر مرفوع؛ علامتِ رفع: ضمہ", "Predicate, marfu'; sign: damma"))
        )
    ),
    QVerse(
        "i3", sIkhlas, "112:3", "لَمْ يَلِدْ وَلَمْ يُولَدْ",
        Bi("نہ اس نے کسی کو جنا اور نہ وہ جنا گیا", "He neither begets nor is born"),
        listOf(
            QWord("لَمْ", 2, Bi("حرفِ نفی و جزم؛ مضارع کو ماضی منفی کے معنی دیتا ہے", "Negation and jussive particle; gives the present verb a past negative meaning")),
            QWord("يَلِدْ", 1, Bi("فعل مضارع مجزوم؛ علامتِ جزم: سکون؛ فاعل: ضمیر مستتر (هو)", "Present verb, majzum; sign: sukun; doer: hidden pronoun (he)")),
            QWord("وَ", 2, Bi("حرفِ عطف", "Conjunction")),
            QWord("لَمْ", 2, Bi("حرفِ نفی و جزم", "Negation and jussive particle")),
            QWord("يُولَدْ", 1, Bi("فعل مضارع مجہول مجزوم؛ علامتِ جزم: سکون؛ نائب فاعل: ضمیر مستتر (هو)", "Passive present verb, majzum; sign: sukun; deputy doer: hidden pronoun (he)"))
        )
    ),
    QVerse(
        "k1", sKawthar, "108:1", "إِنَّا أَعْطَيْنَاكَ الْكَوْثَرَ",
        Bi("بے شک ہم نے آپ کو کوثر عطا کی", "Indeed, We have given you al-Kawthar"),
        listOf(
            QWord("إِنَّ", 2, Bi("حرفِ مشبہ بالفعل؛ تاکید کے لیے، اسم کو نصب اور خبر کو رفع دیتا ہے", "Particle of emphasis; puts its subject in nasb and its predicate in raf'")),
            QWord("نَا", 0, Bi("ضمیرِ متصل، اسمِ إنَّ، محلِ نصب (مبنی)", "Attached pronoun, subject of inna, in nasb")),
            QWord("أَعْطَيْنَا", 1, Bi("فعلِ ماضی؛ فاعل: نَا (ضمیرِ متصل)۔ یہ جملہ إنَّ کی خبر ہے", "Past verb; doer: نَا (attached pronoun). This clause is the predicate of inna")),
            QWord("كَ", 0, Bi("ضمیرِ متصل، مفعول بہ اول، محلِ نصب (مبنی)", "Attached pronoun, first object")),
            QWord("الْكَوْثَرَ", 0, Bi("مفعول بہ ثانی منصوب؛ علامتِ نصب: فتحہ", "Second object, mansub; sign: fatha"))
        )
    ),
    QVerse(
        "k2", sKawthar, "108:2", "فَصَلِّ لِرَبِّكَ وَانْحَرْ",
        Bi("پس اپنے رب کے لیے نماز پڑھیے اور قربانی کیجیے", "So pray to your Lord and sacrifice"),
        listOf(
            QWord("فَ", 2, Bi("حرف (فاء)؛ سبب اور نتیجے کو جوڑتا ہے", "Particle (fa'); links reason and result")),
            QWord("صَلِّ", 1, Bi("فعلِ امر؛ فاعل: ضمیر مستتر (أنت)", "Command verb; doer: hidden pronoun (you)")),
            QWord("لِ", 2, Bi("حرفِ جر", "Preposition")),
            QWord("رَبِّ", 0, Bi("مجرور؛ علامتِ جر: کسرہ؛ مضاف ہے", "Majrur; sign: kasra; first term of an idafa")),
            QWord("كَ", 0, Bi("ضمیرِ متصل، مضاف الیہ، محلِ جر (مبنی)", "Attached pronoun, second term of the idafa")),
            QWord("وَ", 2, Bi("حرفِ عطف", "Conjunction")),
            QWord("انْحَرْ", 1, Bi("فعلِ امر؛ فاعل: ضمیر مستتر (أنت)", "Command verb; doer: hidden pronoun (you)"))
        )
    ),
    QVerse(
        "a1", sAsr, "103:1", "وَالْعَصْرِ",
        Bi("زمانے کی قسم", "By the time (al-'Asr)"),
        listOf(
            QWord("وَ", 2, Bi("حرفِ قسم اور حرفِ جر", "Particle of oath and preposition")),
            QWord("الْعَصْرِ", 0, Bi("مقسم بہ (جس کی قسم کھائی گئی)، مجرور؛ علامتِ جر: کسرہ", "The thing sworn by, majrur; sign: kasra"))
        )
    ),
    QVerse(
        "a2", sAsr, "103:2", "إِنَّ الْإِنْسَانَ لَفِي خُسْرٍ",
        Bi("بے شک انسان گھاٹے میں ہے", "Indeed, mankind is in loss"),
        listOf(
            QWord("إِنَّ", 2, Bi("حرفِ مشبہ بالفعل؛ تاکید کے لیے", "Particle of emphasis")),
            QWord("الْإِنْسَانَ", 0, Bi("اسمِ إنَّ منصوب؛ علامتِ نصب: فتحہ", "Subject of inna, mansub; sign: fatha")),
            QWord("لَ", 2, Bi("لامِ تاکید (حرف)", "Lam of emphasis (particle)")),
            QWord("فِي", 2, Bi("حرفِ جر", "Preposition")),
            QWord("خُسْرٍ", 0, Bi("مجرور؛ علامتِ جر: تنوینِ کسرہ۔ جار مجرور مل کر خبرِ إنَّ ہیں", "Majrur; sign: tanween kasra. With the preposition it forms the predicate of inna"))
        )
    ),
    QVerse(
        "s5", sSharh, "94:5", "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا",
        Bi("پس بے شک مشکل کے ساتھ آسانی ہے", "Indeed, with hardship comes ease"),
        listOf(
            QWord("فَ", 2, Bi("حرف (فاء)؛ پچھلی بات کی وجہ بتاتا ہے", "Particle (fa'); gives the reason for what came before")),
            QWord("إِنَّ", 2, Bi("حرفِ مشبہ بالفعل؛ تاکید کے لیے", "Particle of emphasis")),
            QWord("مَعَ", 0, Bi("ظرف منصوب؛ علامتِ نصب: فتحہ؛ مضاف ہے۔ یہ خبرِ إنَّ مقدم ہے", "Adverb of place/time, mansub; sign: fatha; first term of an idafa. It is the fronted predicate of inna")),
            QWord("الْعُسْرِ", 0, Bi("مضاف الیہ مجرور؛ علامتِ جر: کسرہ", "Mudaf ilayh, majrur; sign: kasra")),
            QWord("يُسْرًا", 0, Bi("اسمِ إنَّ مؤخر منصوب؛ علامتِ نصب: تنوینِ فتح", "Subject of inna placed last, mansub; sign: tanween fatha"))
        )
    ),
    QVerse(
        "n1", sNas, "114:1", "قُلْ أَعُوذُ بِرَبِّ النَّاسِ",
        Bi("کہہ دیجیے: میں لوگوں کے رب کی پناہ مانگتا ہوں", "Say: I seek refuge in the Lord of mankind"),
        listOf(
            QWord("قُلْ", 1, Bi("فعلِ امر؛ فاعل: ضمیر مستتر (أنت)", "Command verb; doer: hidden pronoun (you)")),
            QWord("أَعُوذُ", 1, Bi("فعل مضارع مرفوع؛ علامتِ رفع: ضمہ؛ فاعل: ضمیر مستتر (أنا)", "Present verb, marfu'; sign: damma; doer: hidden pronoun (I)")),
            QWord("بِ", 2, Bi("حرفِ جر", "Preposition")),
            QWord("رَبِّ", 0, Bi("مجرور؛ علامتِ جر: کسرہ؛ مضاف ہے", "Majrur; sign: kasra; first term of an idafa")),
            QWord("النَّاسِ", 0, Bi("مضاف الیہ مجرور؛ علامتِ جر: کسرہ", "Mudaf ilayh, majrur; sign: kasra"))
        )
    ),
    QVerse(
        "q1", sFalaq, "113:1", "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ",
        Bi("کہہ دیجیے: میں صبح کے رب کی پناہ مانگتا ہوں", "Say: I seek refuge in the Lord of the daybreak"),
        listOf(
            QWord("قُلْ", 1, Bi("فعلِ امر؛ فاعل: ضمیر مستتر (أنت)", "Command verb; doer: hidden pronoun (you)")),
            QWord("أَعُوذُ", 1, Bi("فعل مضارع مرفوع؛ علامتِ رفع: ضمہ؛ فاعل: ضمیر مستتر (أنا)", "Present verb, marfu'; sign: damma; doer: hidden pronoun (I)")),
            QWord("بِ", 2, Bi("حرفِ جر", "Preposition")),
            QWord("رَبِّ", 0, Bi("مجرور؛ علامتِ جر: کسرہ؛ مضاف ہے", "Majrur; sign: kasra; first term of an idafa")),
            QWord("الْفَلَقِ", 0, Bi("مضاف الیہ مجرور؛ علامتِ جر: کسرہ", "Mudaf ilayh, majrur; sign: kasra"))
        )
    )
)

/* ============ اسکرین ============ */
@Composable
fun QuranScreen() {
    var openId by remember { mutableStateOf<String?>(null) }
    val verse = quranVerses.firstOrNull { it.id == openId }
    BackHandler(enabled = verse != null) { openId = null }
    if (verse == null) {
        VerseList(onOpen = { openId = it })
    } else {
        VerseDetail(verse, onBack = { openId = null })
    }
}

@Composable
fun VerseList(onOpen: (String) -> Unit) {
    val en = LocalEn.current
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(tr("📖 قرآنی اعراب کا تجزیہ", "📖 Quranic I'rab Analysis"),
                    fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Brand)
                Text(
                    tr("آیت کھولیں، ہر لفظ پر ٹیپ کر کے اس کی قسم (اسم، فعل، حرف) اور اعراب دیکھیں، پھر مشق کریں۔",
                        "Open a verse, tap each word to see its type (noun, verb, particle) and i'rab, then practise."),
                    fontSize = 14.sp, color = Muted, lineHeight = 24.sp
                )
                Surface(color = Amber, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                    Text(
                        tr("نوٹ: یہ مشہور اور سادہ اعراب ہے۔ بعض آیات میں علماء کے ہاں ایک سے زیادہ درست اعراب ہوتے ہیں۔ پکی تصدیق کے لیے اپنے استاد سے ضرور مشورہ کریں۔",
                            "Note: these are the common, simplified analyses. Some verses have more than one valid analysis among scholars. Please confirm with your teacher."),
                        fontSize = 12.sp, color = AmberText, lineHeight = 20.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }
        items(quranVerses) { v ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onOpen(v.id) },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Border),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(v.surah.pick(en) + "  " + v.ref, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Muted)
                    MixedText(v.text, size = 14, arabicSize = 24, forceArabic = true, arabicColor = BrandDark)
                    Text(v.meaning.pick(en), fontSize = 12.sp, color = Muted, lineHeight = 20.sp, textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth())
                }
            }
        }
        item { Spacer(Modifier.height(12.dp)) }
    }
}

@Composable
fun VerseDetail(verse: QVerse, onBack: () -> Unit) {
    val en = LocalEn.current
    var picked by remember(verse.id) { mutableStateOf(0) }
    val w = verse.words[picked]
    val (kbg, kfg) = kindColors(w.kind)

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BigButton(tr("واپس: تمام آیات", "Back to all verses"), onClick = onBack, filled = false, modifier = Modifier.fillMaxWidth())

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Brand),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(verse.surah.pick(en) + "  " + verse.ref, fontSize = 13.sp, color = Color(0xFFE0F2FE))
                Text(verse.text, fontSize = 28.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif,
                    color = Color.White, lineHeight = 46.sp, textAlign = TextAlign.Center)
                Text(verse.meaning.pick(en), fontSize = 14.sp, color = Color(0xFFE0F2FE), lineHeight = 24.sp,
                    textAlign = TextAlign.Center)
            }
        }

        SectionTitle(tr("👆 لفظ پر ٹیپ کریں", "👆 Tap a word"))
        androidx.compose.foundation.layout.Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            verse.words.forEachIndexed { i, word ->
                val (bg, fg) = kindColors(word.kind)
                Surface(
                    color = if (i == picked) fg else bg,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, fg),
                    modifier = Modifier.clickable { picked = i }
                ) {
                    Text(word.ar, fontSize = 24.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif,
                        color = if (i == picked) Color.White else fg,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
                }
            }
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Border),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                androidx.compose.foundation.layout.Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(w.ar, fontSize = 30.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif, color = BrandDark)
                    Surface(color = kbg, shape = RoundedCornerShape(10.dp)) {
                        Text(kindLabel(w.kind, en), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = kfg,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp))
                    }
                }
                MixedText(w.i3rab.pick(en), size = 15, arabicSize = 20, align = TextAlign.Start)
            }
        }

        SectionTitle(tr("📋 مکمل جدول", "📋 Full Table"))
        VerseTable(verse)

        if (verse.note != null) {
            Surface(color = Amber, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                MixedText(verse.note.pick(en), size = 13, arabicSize = 17, color = AmberText, arabicColor = AmberText,
                    align = TextAlign.Start, modifier = Modifier.padding(12.dp))
            }
        }

        SectionTitle(tr("🧩 تجزیے کی مشق", "🧩 Analysis Exercise"))
        VerseExercise(verse)
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun VerseTable(verse: QVerse) {
    val en = LocalEn.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Border),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(Modifier.fillMaxWidth()) {
            androidx.compose.foundation.layout.Row(Modifier.fillMaxWidth().background(Brand).padding(10.dp)) {
                Text(tr("لفظ", "Word"), Modifier.weight(1f), color = Color.White, fontWeight = FontWeight.Bold,
                    fontSize = 12.sp, textAlign = TextAlign.Center)
                Text(tr("قسم", "Type"), Modifier.weight(0.8f), color = Color.White, fontWeight = FontWeight.Bold,
                    fontSize = 12.sp, textAlign = TextAlign.Center)
                Text(tr("اعراب", "I'rab"), Modifier.weight(2.2f), color = Color.White, fontWeight = FontWeight.Bold,
                    fontSize = 12.sp, textAlign = TextAlign.Center)
            }
            verse.words.forEachIndexed { i, word ->
                val (bg, fg) = kindColors(word.kind)
                androidx.compose.foundation.layout.Row(Modifier.height(IntrinsicSize.Min)) {
                    Box(
                        Modifier.weight(1f).fillMaxHeight().background(BrandLight).border(0.5.dp, Border).padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(word.ar, fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif,
                            color = BrandDark, textAlign = TextAlign.Center)
                    }
                    Box(
                        Modifier.weight(0.8f).fillMaxHeight().background(bg).border(0.5.dp, Border).padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(kindLabel(word.kind, en), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = fg,
                            textAlign = TextAlign.Center)
                    }
                    Box(
                        Modifier.weight(2.2f).fillMaxHeight()
                            .background(if (i % 2 == 0) Color.White else Page)
                            .border(0.5.dp, Border).padding(8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        MixedText(word.i3rab.pick(en), size = 12, arabicSize = 16, align = TextAlign.Start)
                    }
                }
            }
        }
    }
}

/* ============ مشق ============ */
data class VQ(val prompt: String, val options: List<String>, val correct: Int)

fun buildVerseQuestions(verse: QVerse, en: Boolean): List<VQ> {
    val result = mutableListOf<VQ>()
    for (w in verse.words) {
        val opts = listOf(kindLabel(0, en), kindLabel(1, en), kindLabel(2, en))
        val p = if (en) "«${w.ar}» — what is this word?" else "«${w.ar}» — یہ لفظ کیا ہے؟"
        result.add(VQ(p, opts, w.kind))
    }
    val pool = quranVerses.flatMap { v -> v.words.map { it.i3rab.pick(en) } }.distinct()
    val chosen = verse.words.shuffled().take(2)
    for (w in chosen) {
        val right = w.i3rab.pick(en)
        val wrong = pool.filter { it != right }.shuffled().take(2)
        val opts = (wrong + right).shuffled()
        val p = if (en) "«${w.ar}» — what is its i'rab?" else "«${w.ar}» — اس کا اعراب کیا ہے؟"
        result.add(VQ(p, opts, opts.indexOf(right)))
    }
    return result
}

@Composable
fun VerseExercise(verse: QVerse) {
    val context = LocalContext.current
    val en = LocalEn.current
    var round by remember(verse.id) { mutableStateOf(0) }
    var index by remember(verse.id) { mutableStateOf(0) }
    var score by remember(verse.id) { mutableStateOf(0) }
    var selected by remember(verse.id) { mutableStateOf<Int?>(null) }
    val questions = remember(verse.id, round, en) { buildVerseQuestions(verse, en) }
    val total = questions.size

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (index >= total) {
            Surface(color = BrandLight, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("$score / $total", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = BrandDark)
                    Text(
                        when {
                            score == total -> tr("ماشاء اللہ! آیت کا تجزیہ مکمل صحیح ہے۔", "MashaAllah! The whole analysis is correct.")
                            score * 10 >= total * 7 -> tr("بہت خوب! ایک بار جدول دوبارہ دیکھ لیں۔", "Great! Review the table once more.")
                            else -> tr("اوپر کا جدول دوبارہ پڑھ کر مشق دہرائیں۔", "Re-read the table above and try again.")
                        },
                        fontSize = 14.sp, color = Brand, textAlign = TextAlign.Center
                    )
                    Text(tr("نتیجہ اسکور بورڈ میں محفوظ ہو گیا۔", "Result saved to the scoreboard."), fontSize = 12.sp, color = Muted)
                }
            }
            BigButton(tr("مشق دوبارہ کریں", "Try Again"), onClick = {
                round += 1; index = 0; score = 0; selected = null
            }, modifier = Modifier.fillMaxWidth())
        } else {
            val q = questions[index]
            Text(
                tr("سوال ${index + 1} / $total      درست: $score", "Question ${index + 1} / $total      Correct: $score"),
                fontSize = 13.sp, color = Muted
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Border),
                shape = RoundedCornerShape(16.dp)
            ) {
                MixedText(q.prompt, size = 16, arabicSize = 24, bold = true, align = TextAlign.Start,
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
                    MixedText(opt, size = 14, arabicSize = 18, align = TextAlign.Start, modifier = Modifier.padding(14.dp))
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
                    if (index == total - 1) tr("نتیجہ دیکھیں", "See Result") else tr("اگلا سوال", "Next Question"),
                    onClick = {
                        if (index == total - 1) {
                            addResult(context, ScoreEntry("quran", verse.surah.pick(en) + " " + verse.ref, score, total, nowString()))
                        }
                        index += 1; selected = null
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
