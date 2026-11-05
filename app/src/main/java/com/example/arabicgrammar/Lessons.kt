package com.example.arabicgrammar

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* ============ ڈیٹا ماڈل (اردو + English) ============ */
data class Bi(val ur: String, val en: String)

fun Bi.pick(useEn: Boolean): String = if (useEn) en else ur

data class LessonExample(val arabic: String, val parse: Bi)
data class LessonStep(val q: Bi, val options: List<Bi>, val correct: Int, val why: Bi)
data class Lesson(
    val id: String,
    val icon: String,
    val title: Bi,
    val intro: Bi,
    val points: List<Bi>,
    val examples: List<LessonExample>,
    val sentence: String,
    val sentenceMeaning: Bi,
    val steps: List<LessonStep>
)

private val oNVP = listOf(Bi("اسم", "Noun"), Bi("فعل", "Verb"), Bi("حرف", "Particle"))
private val oRoles1 = listOf(Bi("مبتدا", "Subject (mubtada')"), Bi("خبر", "Predicate (khabar)"), Bi("فاعل", "Doer (fa'il)"))
private val oCases = listOf(Bi("رفع", "Raf' (nominative)"), Bi("نصب", "Nasb (accusative)"), Bi("جر", "Jarr (genitive)"))

/* ============ اسباق ============ */
val nahwLessons: List<Lesson> = listOf(

    Lesson(
        id = "parts", icon = "🔤",
        title = Bi("کلمہ کی اقسام", "Parts of Speech"),
        intro = Bi(
            "عربی کا ہر لفظ تین میں سے کوئی ایک ہوتا ہے: اسم، فعل یا حرف۔ جملے کو سمجھنے کی پہلی سیڑھی یہی پہچان ہے۔",
            "Every Arabic word is one of three types: noun (ism), verb (fi'l) or particle (harf). Recognising these is the first step to understanding any sentence."
        ),
        points = listOf(
            Bi("اسم: کسی شخص، چیز یا جگہ کا نام۔ اس میں زمانہ نہیں ہوتا۔ جیسے: كِتَابٌ، مُحَمَّدٌ، مَدْرَسَةٌ",
                "Noun (ism): names a person, thing or place and has no tense. e.g. كِتَابٌ، مُحَمَّدٌ، مَدْرَسَةٌ"),
            Bi("فعل: ایسا کام جس میں زمانہ ہو (ماضی، مضارع، امر)۔ جیسے: كَتَبَ، يَكْتُبُ، اكْتُبْ",
                "Verb (fi'l): an action with tense (past, present, command). e.g. كَتَبَ، يَكْتُبُ، اكْتُبْ"),
            Bi("حرف: اکیلا مکمل معنی نہیں دیتا، دوسرے لفظ سے مل کر معنی دیتا ہے۔ جیسے: فِي، مِنْ، عَلَى، لَمْ",
                "Particle (harf): gives meaning only together with another word. e.g. فِي، مِنْ، عَلَى، لَمْ"),
            Bi("اسم کی پہچان: اس پر ال، تنوین یا حرفِ جر آ سکتا ہے۔",
                "Signs of a noun: it can take ال, tanween, or a preposition before it."),
            Bi("فعل کی پہچان: اس پر قَدْ، سَـ، سَوْفَ یا تائے تانیث ساکنہ (كَتَبَتْ) آ سکتی ہے۔",
                "Signs of a verb: it can take قَدْ, سَـ, سَوْفَ or the silent feminine ت (كَتَبَتْ).")
        ),
        examples = listOf(
            LessonExample("ذَهَبَ الطَّالِبُ إِلَى الْمَدْرَسَةِ",
                Bi("ذَهَبَ: فعل (ماضی)\nالطَّالِبُ: اسم\nإِلَى: حرف (جر)\nالْمَدْرَسَةِ: اسم\nمعنی: طالب علم مدرسے گیا",
                    "ذَهَبَ: verb (past)\nالطَّالِبُ: noun\nإِلَى: particle (preposition)\nالْمَدْرَسَةِ: noun\nMeaning: The student went to school")),
            LessonExample("مُحَمَّدٌ فِي الْبَيْتِ",
                Bi("مُحَمَّدٌ: اسم\nفِي: حرف (جر)\nالْبَيْتِ: اسم\nمعنی: محمد گھر میں ہے",
                    "مُحَمَّدٌ: noun\nفِي: particle (preposition)\nالْبَيْتِ: noun\nMeaning: Muhammad is in the house")),
            LessonExample("سَيَكْتُبُ الدَّرْسَ",
                Bi("سَـ: حرف (مستقبل)\nيَكْتُبُ: فعل (مضارع)\nالدَّرْسَ: اسم\nمعنی: وہ سبق لکھے گا",
                    "سَـ: particle (future)\nيَكْتُبُ: verb (present)\nالدَّرْسَ: noun\nMeaning: He will write the lesson"))
        ),
        sentence = "ذَهَبَ الطَّالِبُ إِلَى الْمَدْرَسَةِ",
        sentenceMeaning = Bi("طالب علم مدرسے گیا", "The student went to school"),
        steps = listOf(
            LessonStep(Bi("«ذَهَبَ» کیا ہے؟", "What is «ذَهَبَ»?"), oNVP, 1,
                Bi("یہ کام (جانا) بتاتا ہے اور اس میں زمانہ (ماضی) ہے، اس لیے فعل ہے۔",
                    "It tells an action (going) with a tense (past), so it is a verb.")),
            LessonStep(Bi("«الطَّالِبُ» کیا ہے؟", "What is «الطَّالِبُ»?"), oNVP, 0,
                Bi("یہ شخص کا نام ہے اور اس پر ال داخل ہے، اس لیے اسم ہے۔",
                    "It names a person and carries ال, so it is a noun.")),
            LessonStep(Bi("«إِلَى» کیا ہے؟", "What is «إِلَى»?"), oNVP, 2,
                Bi("یہ اکیلا معنی نہیں دیتا اور اسم کو جر دیتا ہے، یعنی حرفِ جر ہے۔",
                    "It gives no full meaning alone and puts the noun in jarr, so it is a preposition (particle).")),
            LessonStep(Bi("«الْمَدْرَسَةِ» کیا ہے؟", "What is «الْمَدْرَسَةِ»?"), oNVP, 0,
                Bi("اس پر ال ہے اور حرفِ جر کے بعد آیا ہے، یہ دونوں اسم کی نشانیاں ہیں۔",
                    "It has ال and comes after a preposition. Both are signs of a noun."))
        )
    ),

    Lesson(
        id = "nominal", icon = "🧱",
        title = Bi("جملۂ اسمیہ (مبتدا اور خبر)", "Nominal Sentence (Mubtada' & Khabar)"),
        intro = Bi(
            "جو جملہ اسم سے شروع ہو وہ جملۂ اسمیہ ہے۔ اس کے دو حصے ہیں: مبتدا (جس کے بارے میں بات کی جائے) اور خبر (جو بات کہی جائے)۔ دونوں مرفوع ہوتے ہیں۔",
            "A sentence that begins with a noun is a nominal sentence. It has two parts: the subject (mubtada') is what we talk about, and the predicate (khabar) is what is said about it. Both are in the nominative (marfu')."
        ),
        points = listOf(
            Bi("مبتدا عام طور پر معرفہ ہوتا ہے اور خبر عام طور پر نکرہ۔",
                "The mubtada' is usually definite and the khabar is usually indefinite."),
            Bi("دونوں پر رفع کی علامت آتی ہے: ضمہ یا تنوینِ ضمہ (ـُ / ـٌ)۔",
                "Both take the sign of raf': damma or tanween damma (ـُ / ـٌ)."),
            Bi("عربی میں «ہے» لکھا نہیں جاتا: الْبَابُ مَفْتُوحٌ کا مطلب ہے دروازہ کھلا ہے۔",
                "Arabic has no written word for «is»: الْبَابُ مَفْتُوحٌ means The door is open."),
            Bi("خبر مبتدا کے مطابق ہوتی ہے: مذکر کے ساتھ مذکر، مؤنث کے ساتھ مؤنث۔",
                "The khabar agrees with the mubtada': masculine with masculine, feminine with feminine.")
        ),
        examples = listOf(
            LessonExample("الْكِتَابُ جَدِيدٌ",
                Bi("الْكِتَابُ: مبتدا (مرفوع)\nجَدِيدٌ: خبر (مرفوع)\nمعنی: کتاب نئی ہے",
                    "الْكِتَابُ: mubtada' (marfu')\nجَدِيدٌ: khabar (marfu')\nMeaning: The book is new")),
            LessonExample("الطَّالِبَةُ مُجْتَهِدَةٌ",
                Bi("الطَّالِبَةُ: مبتدا (مرفوع)\nمُجْتَهِدَةٌ: خبر (مرفوع، مؤنث)\nمعنی: طالبہ محنتی ہے",
                    "الطَّالِبَةُ: mubtada' (marfu')\nمُجْتَهِدَةٌ: khabar (marfu', feminine)\nMeaning: The female student is hardworking")),
            LessonExample("مُحَمَّدٌ مُعَلِّمٌ",
                Bi("مُحَمَّدٌ: مبتدا (مرفوع)\nمُعَلِّمٌ: خبر (مرفوع)\nمعنی: محمد استاد ہے",
                    "مُحَمَّدٌ: mubtada' (marfu')\nمُعَلِّمٌ: khabar (marfu')\nMeaning: Muhammad is a teacher"))
        ),
        sentence = "الْبَابُ مَفْتُوحٌ",
        sentenceMeaning = Bi("دروازہ کھلا ہے", "The door is open"),
        steps = listOf(
            LessonStep(Bi("«الْبَابُ» کا کردار کیا ہے؟", "What is the role of «الْبَابُ»?"), oRoles1, 0,
                Bi("جملہ اسم سے شروع ہوا اور اسی کے بارے میں بات کی جا رہی ہے، اس لیے مبتدا ہے۔",
                    "The sentence starts with this noun and it is what we speak about, so it is the mubtada'.")),
            LessonStep(Bi("«مَفْتُوحٌ» کا کردار کیا ہے؟", "What is the role of «مَفْتُوحٌ»?"), oRoles1, 1,
                Bi("یہ مبتدا کے بارے میں خبر دیتا ہے (کھلا ہے)، اس لیے خبر ہے۔",
                    "It gives information about the mubtada' (it is open), so it is the khabar.")),
            LessonStep(Bi("ان دونوں کی حالت کیا ہے؟", "What case are both words in?"), oCases, 0,
                Bi("مبتدا اور خبر ہمیشہ مرفوع ہوتے ہیں۔ یہاں علامت ضمہ اور تنوینِ ضمہ ہے۔",
                    "The mubtada' and khabar are always marfu'. Here the signs are damma and tanween damma."))
        )
    ),

    Lesson(
        id = "verbal", icon = "⚡",
        title = Bi("جملۂ فعلیہ (فعل، فاعل، مفعول)", "Verbal Sentence (Verb, Doer, Object)"),
        intro = Bi(
            "جو جملہ فعل سے شروع ہو وہ جملۂ فعلیہ ہے۔ فعل کے بعد فاعل (کام کرنے والا) آتا ہے جو مرفوع ہوتا ہے، پھر مفعول بہ (جس پر کام ہوا) آتا ہے جو منصوب ہوتا ہے۔",
            "A sentence that begins with a verb is a verbal sentence. After the verb comes the doer (fa'il) in raf', then the object (maf'ul bih) in nasb."
        ),
        points = listOf(
            Bi("ترتیب: فعل + فاعل + مفعول بہ۔", "Order: verb + doer + object."),
            Bi("فاعل مرفوع (ضمہ) ہوتا ہے اور مفعول بہ منصوب (فتحہ)۔",
                "The doer is marfu' (damma) and the object is mansub (fatha)."),
            Bi("فعل فاعل سے پہلے آئے تو جنس میں فاعل کے مطابق ہوتا ہے (ذَهَبَتِ الطَّالِبَةُ) مگر تعداد میں عموماً واحد رہتا ہے (ذَهَبَ الطُّلَّابُ)۔",
                "When the verb comes first it agrees with the doer in gender (ذَهَبَتِ الطَّالِبَةُ) but usually stays singular in number (ذَهَبَ الطُّلَّابُ).")
        ),
        examples = listOf(
            LessonExample("كَتَبَ الطَّالِبُ الدَّرْسَ",
                Bi("كَتَبَ: فعل ماضی\nالطَّالِبُ: فاعل (مرفوع)\nالدَّرْسَ: مفعول بہ (منصوب)\nمعنی: طالب علم نے سبق لکھا",
                    "كَتَبَ: past verb\nالطَّالِبُ: doer (marfu')\nالدَّرْسَ: object (mansub)\nMeaning: The student wrote the lesson")),
            LessonExample("يَقْرَأُ الْوَلَدُ الْقُرْآنَ",
                Bi("يَقْرَأُ: فعل مضارع\nالْوَلَدُ: فاعل (مرفوع)\nالْقُرْآنَ: مفعول بہ (منصوب)\nمعنی: لڑکا قرآن پڑھتا ہے",
                    "يَقْرَأُ: present verb\nالْوَلَدُ: doer (marfu')\nالْقُرْآنَ: object (mansub)\nMeaning: The boy reads the Quran")),
            LessonExample("فَتَحَ عَلِيٌّ الْبَابَ",
                Bi("فَتَحَ: فعل ماضی\nعَلِيٌّ: فاعل (مرفوع)\nالْبَابَ: مفعول بہ (منصوب)\nمعنی: علی نے دروازہ کھولا",
                    "فَتَحَ: past verb\nعَلِيٌّ: doer (marfu')\nالْبَابَ: object (mansub)\nMeaning: Ali opened the door"))
        ),
        sentence = "كَتَبَ الطَّالِبُ الدَّرْسَ",
        sentenceMeaning = Bi("طالب علم نے سبق لکھا", "The student wrote the lesson"),
        steps = listOf(
            LessonStep(Bi("«الطَّالِبُ» کا کردار کیا ہے؟", "What is the role of «الطَّالِبُ»?"),
                listOf(Bi("فاعل", "Doer (fa'il)"), Bi("مفعول بہ", "Object (maf'ul bih)"), Bi("مبتدا", "Subject (mubtada')")), 0,
                Bi("لکھنے کا کام اسی نے کیا، اس لیے فاعل ہے۔", "He is the one who did the writing, so he is the doer.")),
            LessonStep(Bi("«الدَّرْسَ» کا کردار کیا ہے؟", "What is the role of «الدَّرْسَ»?"),
                listOf(Bi("فاعل", "Doer (fa'il)"), Bi("مفعول بہ", "Object (maf'ul bih)"), Bi("خبر", "Predicate (khabar)")), 1,
                Bi("لکھنے کا کام اس پر واقع ہوا، اس لیے مفعول بہ ہے۔", "The action fell on it, so it is the object.")),
            LessonStep(Bi("«الدَّرْسَ» کی حالت کیا ہے؟", "What case is «الدَّرْسَ» in?"), oCases, 1,
                Bi("مفعول بہ منصوب ہوتا ہے، علامت فتحہ (ـَ) ہے۔", "The object is mansub, with the sign of fatha (ـَ).")),
            LessonStep(Bi("«الطَّالِبُ» کی حالت کیا ہے؟", "What case is «الطَّالِبُ» in?"), oCases, 0,
                Bi("فاعل مرفوع ہوتا ہے، علامت ضمہ (ـُ) ہے۔", "The doer is marfu', with the sign of damma (ـُ)."))
        )
    ),

    Lesson(
        id = "cases", icon = "🎯",
        title = Bi("اعرابی حالتیں: رفع، نصب، جر", "Cases: Raf', Nasb, Jarr"),
        intro = Bi(
            "اسم کی تین حالتیں ہوتی ہیں: رفع (فاعل، مبتدا، خبر)، نصب (مفعول بہ) اور جر (حرفِ جر یا مضاف الیہ کے بعد)۔ حالت لفظ کے آخری حرف کی حرکت یا کسی حرف سے پہچانی جاتی ہے۔",
            "A noun has three cases: raf' (doer, mubtada', khabar), nasb (object) and jarr (after a preposition or as possessor). The case shows in the last letter's vowel or in a letter."
        ),
        points = listOf(
            Bi("رفع: ضمہ ـُ یا تنوین ـٌ", "Raf': damma ـُ or tanween ـٌ"),
            Bi("نصب: فتحہ ـَ یا تنوین ـً", "Nasb: fatha ـَ or tanween ـً"),
            Bi("جر: کسرہ ـِ یا تنوین ـٍ", "Jarr: kasra ـِ or tanween ـٍ"),
            Bi("تثنیہ: رفع میں ـَانِ، نصب اور جر میں ـَيْنِ", "Dual: ـَانِ in raf', ـَيْنِ in nasb and jarr"),
            Bi("جمع مذکر سالم: رفع میں ـُونَ، نصب اور جر میں ـِينَ", "Sound masculine plural: ـُونَ in raf', ـِينَ in nasb and jarr")
        ),
        examples = listOf(
            LessonExample("جَاءَ الْمُسْلِمُونَ",
                Bi("جَاءَ: فعل ماضی\nالْمُسْلِمُونَ: فاعل، مرفوع (علامت: واؤ)\nمعنی: مسلمان آئے",
                    "جَاءَ: past verb\nالْمُسْلِمُونَ: doer, marfu' (sign: waw)\nMeaning: The Muslims came")),
            LessonExample("رَأَيْتُ الْمُسْلِمِينَ",
                Bi("رَأَيْتُ: فعل اور فاعل (تُ)\nالْمُسْلِمِينَ: مفعول بہ، منصوب (علامت: یاء)\nمعنی: میں نے مسلمانوں کو دیکھا",
                    "رَأَيْتُ: verb and doer (تُ)\nالْمُسْلِمِينَ: object, mansub (sign: ya)\nMeaning: I saw the Muslims")),
            LessonExample("مَرَرْتُ بِالْمُسْلِمِينَ",
                Bi("مَرَرْتُ: فعل اور فاعل (تُ)\nبِ: حرف جر\nالْمُسْلِمِينَ: مجرور (علامت: یاء)\nمعنی: میں مسلمانوں کے پاس سے گزرا",
                    "مَرَرْتُ: verb and doer (تُ)\nبِ: preposition\nالْمُسْلِمِينَ: majrur (sign: ya)\nMeaning: I passed by the Muslims"))
        ),
        sentence = "رَأَيْتُ الْمُسْلِمِينَ",
        sentenceMeaning = Bi("میں نے مسلمانوں کو دیکھا", "I saw the Muslims"),
        steps = listOf(
            LessonStep(Bi("«الْمُسْلِمِينَ» کا کردار کیا ہے؟", "What is the role of «الْمُسْلِمِينَ»?"),
                listOf(Bi("مفعول بہ", "Object (maf'ul bih)"), Bi("فاعل", "Doer (fa'il)"), Bi("مبتدا", "Subject (mubtada')")), 0,
                Bi("دیکھنے کا کام ان پر واقع ہوا، اس لیے مفعول بہ ہیں۔", "The seeing fell on them, so they are the object.")),
            LessonStep(Bi("«الْمُسْلِمِينَ» کی حالت کیا ہے؟", "What case is «الْمُسْلِمِينَ» in?"), oCases, 1,
                Bi("مفعول بہ منصوب ہوتا ہے۔", "The object is mansub.")),
            LessonStep(Bi("اس حالت کی علامت کیا ہے؟", "What is the sign of this case?"),
                listOf(Bi("واؤ", "Waw"), Bi("یاء", "Ya"), Bi("الف", "Alif")), 1,
                Bi("جمع مذکر سالم میں نصب اور جر کی علامت یاء (ـِينَ) ہے۔", "In the sound masculine plural, nasb and jarr take ya (ـِينَ).")),
            LessonStep(Bi("«جَاءَ الْمُسْلِمُونَ» میں «الْمُسْلِمُونَ» کی حالت؟", "In «جَاءَ الْمُسْلِمُونَ», what case is «الْمُسْلِمُونَ»?"), oCases, 0,
                Bi("یہ فاعل ہے، اس لیے مرفوع ہے اور علامت واؤ (ـُونَ) ہے۔", "It is the doer, so it is marfu' with waw (ـُونَ)."))
        )
    ),

    Lesson(
        id = "definite", icon = "🔖",
        title = Bi("معرفہ اور نکرہ", "Definite and Indefinite"),
        intro = Bi(
            "نکرہ غیر متعین چیز ہے (ایک کتاب) اور معرفہ متعین چیز (وہ مخصوص کتاب)۔ نکرہ کی سب سے بڑی پہچان تنوین ہے، اور ال لگنے سے تنوین ختم ہو کر لفظ معرفہ بن جاتا ہے۔",
            "Indefinite (nakira) means something non-specific (a book) and definite (ma'rifa) means something specific (the book). Tanween is the sign of indefiniteness; adding ال removes it and makes the word definite."
        ),
        points = listOf(
            Bi("نکرہ: تنوین والا لفظ، جیسے كِتَابٌ، رَجُلٌ", "Indefinite: a word with tanween, e.g. كِتَابٌ، رَجُلٌ"),
            Bi("ال لگے تو تنوین ختم: كِتَابٌ ← الْكِتَابُ", "Adding ال drops the tanween: كِتَابٌ ← الْكِتَابُ"),
            Bi("معرفہ کی اقسام: ضمیر، علم (نام)، اسم اشارہ، اسم موصول، ال والا اسم، معرفہ کی طرف مضاف، منادی",
                "Kinds of definite: pronoun, proper name, demonstrative, relative pronoun, noun with ال, noun annexed to a definite, vocative"),
            Bi("نام (علم) پر تنوین ہو تب بھی وہ معرفہ ہے، جیسے مُحَمَّدٌ",
                "A proper name is definite even when it has tanween, e.g. مُحَمَّدٌ")
        ),
        examples = listOf(
            LessonExample("كِتَابٌ ← الْكِتَابُ",
                Bi("كِتَابٌ: نکرہ (تنوین)\nالْكِتَابُ: معرفہ (ال)\nمعنی: ایک کتاب ← وہ مخصوص کتاب",
                    "كِتَابٌ: indefinite (tanween)\nالْكِتَابُ: definite (ال)\nMeaning: a book ← the book")),
            LessonExample("رَجُلٌ ← الرَّجُلُ",
                Bi("رَجُلٌ: نکرہ\nالرَّجُلُ: معرفہ\nمعنی: ایک آدمی ← وہ آدمی",
                    "رَجُلٌ: indefinite\nالرَّجُلُ: definite\nMeaning: a man ← the man")),
            LessonExample("بَيْتُ مُحَمَّدٍ",
                Bi("بَيْتُ: مضاف (معرفہ، کیونکہ مضاف الیہ معرفہ ہے)\nمُحَمَّدٍ: مضاف الیہ (نام، معرفہ)\nمعنی: محمد کا گھر",
                    "بَيْتُ: first term (definite, since the second is definite)\nمُحَمَّدٍ: second term (a name, definite)\nMeaning: Muhammad's house"))
        ),
        sentence = "قَلَمٌ — الْقَلَمُ — مُحَمَّدٌ — هَذَا",
        sentenceMeaning = Bi("نکرہ اور معرفہ پہچانیں", "Spot the indefinite and definite"),
        steps = listOf(
            LessonStep(Bi("«قَلَمٌ» کیا ہے؟", "What is «قَلَمٌ»?"),
                listOf(Bi("نکرہ", "Indefinite"), Bi("معرفہ", "Definite")), 0,
                Bi("اس پر تنوین ہے اور ال نہیں، اس لیے نکرہ ہے۔", "It has tanween and no ال, so it is indefinite.")),
            LessonStep(Bi("«الْقَلَمُ» کیا ہے؟", "What is «الْقَلَمُ»?"),
                listOf(Bi("نکرہ", "Indefinite"), Bi("معرفہ", "Definite")), 1,
                Bi("ال لگنے سے تنوین ختم ہو گئی اور لفظ معرفہ بن گیا۔", "ال removed the tanween and made it definite.")),
            LessonStep(Bi("«مُحَمَّدٌ» (تنوین کے ساتھ) کیا ہے؟", "What is «مُحَمَّدٌ» (with tanween)?"),
                listOf(Bi("نکرہ", "Indefinite"), Bi("معرفہ", "Definite")), 1,
                Bi("یہ نام (علم) ہے، اور نام ہمیشہ معرفہ ہوتا ہے چاہے تنوین ہو۔", "It is a proper name, and names are always definite even with tanween.")),
            LessonStep(Bi("«هَذَا» کیا ہے؟", "What is «هَذَا»?"),
                listOf(Bi("نکرہ", "Indefinite"), Bi("معرفہ", "Definite")), 1,
                Bi("اسم اشارہ معرفہ کی قسموں میں سے ہے۔", "A demonstrative is one of the kinds of definite words."))
        )
    ),

    Lesson(
        id = "pronouns", icon = "👤",
        title = Bi("ضمیریں", "Pronouns"),
        intro = Bi(
            "ضمیر اسم کی جگہ آتی ہے۔ ضمیرِ منفصل الگ لفظ ہوتی ہے (هُوَ، أَنَا) اور ضمیرِ متصل کسی لفظ سے جڑی ہوتی ہے (ـهُ، ـكَ، ـنَا)۔",
            "A pronoun replaces a noun. A detached pronoun is a separate word (هُوَ، أَنَا) and an attached pronoun joins another word (ـهُ، ـكَ، ـنَا)."
        ),
        points = listOf(
            Bi("منفصل (الگ): هُوَ (وہ)، هِيَ (وہ مؤنث)، أَنْتَ (تو)، أَنْتِ (تو مؤنث)، أَنَا (میں)، نَحْنُ (ہم)",
                "Detached: هُوَ (he), هِيَ (she), أَنْتَ (you m.), أَنْتِ (you f.), أَنَا (I), نَحْنُ (we)"),
            Bi("متصل (جڑی): ـهُ (اس کا)، ـهَا (اس مؤنث کا)، ـكَ (تیرا)، ـكِ (تیری)، ـي (میرا)، ـنَا (ہمارا)",
                "Attached: ـهُ (his), ـهَا (her), ـكَ (your m.), ـكِ (your f.), ـي (my), ـنَا (our)"),
            Bi("اسم کے ساتھ جڑے تو مضاف الیہ ہوتی ہے: كِتَابُهُ = اس کی کتاب",
                "Attached to a noun it is the possessor (mudaf ilayh): كِتَابُهُ = his book"),
            Bi("فعل کے ساتھ جڑے تو مفعول بہ ہوتی ہے: رَأَيْتُهُ = میں نے اسے دیکھا",
                "Attached to a verb it is the object: رَأَيْتُهُ = I saw him")
        ),
        examples = listOf(
            LessonExample("هُوَ مُعَلِّمٌ",
                Bi("هُوَ: ضمیر منفصل، مبتدا\nمُعَلِّمٌ: خبر\nمعنی: وہ استاد ہے",
                    "هُوَ: detached pronoun, mubtada'\nمُعَلِّمٌ: khabar\nMeaning: He is a teacher")),
            LessonExample("كِتَابُهُ جَدِيدٌ",
                Bi("كِتَابُ: مبتدا، مضاف\nهُ: ضمیر متصل، مضاف الیہ\nجَدِيدٌ: خبر\nمعنی: اس کی کتاب نئی ہے",
                    "كِتَابُ: mubtada', first term\nهُ: attached pronoun, possessor\nجَدِيدٌ: khabar\nMeaning: His book is new")),
            LessonExample("رَبُّنَا اللهُ",
                Bi("رَبُّ: مبتدا، مضاف\nنَا: ضمیر متصل، مضاف الیہ\nاللهُ: خبر\nمعنی: ہمارا رب اللہ ہے",
                    "رَبُّ: mubtada', first term\nنَا: attached pronoun, possessor\nاللهُ: khabar\nMeaning: Our Lord is Allah"))
        ),
        sentence = "كِتَابُهُ جَدِيدٌ",
        sentenceMeaning = Bi("اس کی کتاب نئی ہے", "His book is new"),
        steps = listOf(
            LessonStep(Bi("«ـهُ» کس قسم کی ضمیر ہے؟", "What kind of pronoun is «ـهُ»?"),
                listOf(Bi("متصل (جڑی ہوئی)", "Attached"), Bi("منفصل (الگ)", "Detached")), 0,
                Bi("یہ لفظ كِتَابُ سے جڑی ہوئی ہے، اس لیے متصل ہے۔", "It is joined to كِتَابُ, so it is attached.")),
            LessonStep(Bi("«ـهُ» کا مطلب کیا ہے؟", "What does «ـهُ» mean here?"),
                listOf(Bi("اس کا", "his"), Bi("میرا", "my"), Bi("تیرا", "your")), 0,
                Bi("ـهُ مذکر غائب کی ضمیر ہے، یعنی اس (مرد) کا۔", "ـهُ is the third person masculine pronoun, meaning his.")),
            LessonStep(Bi("«ـهُ» کا کردار کیا ہے؟", "What is the role of «ـهُ»?"),
                listOf(Bi("مضاف الیہ", "Possessor (mudaf ilayh)"), Bi("فاعل", "Doer (fa'il)"), Bi("مفعول بہ", "Object (maf'ul bih)")), 0,
                Bi("اسم کے ساتھ جڑ کر ملکیت بتاتی ہے، اس لیے مضاف الیہ ہے۔", "Joined to a noun it shows possession, so it is the possessor.")),
            LessonStep(Bi("«هُوَ» کس قسم کی ضمیر ہے؟", "What kind of pronoun is «هُوَ»?"),
                listOf(Bi("متصل (جڑی ہوئی)", "Attached"), Bi("منفصل (الگ)", "Detached")), 1,
                Bi("هُوَ الگ لفظ ہے، اس لیے منفصل ہے۔", "هُوَ is a separate word, so it is detached."))
        )
    ),

    Lesson(
        id = "prepositions", icon = "➡️",
        title = Bi("حروفِ جر", "Prepositions"),
        intro = Bi(
            "حرفِ جر اسم سے پہلے آ کر اسے جر (زیر) دیتا ہے۔ حرفِ جر اور مجرور اسم مل کر «جار مجرور» کہلاتے ہیں۔",
            "A preposition comes before a noun and puts it in jarr (kasra). The preposition and its noun together are called jar wa majrur."
        ),
        points = listOf(
            Bi("مِنْ (سے)، إِلَى (تک / کی طرف)، فِي (میں)، عَلَى (پر)، عَنْ (سے / کے بارے میں)، بِـ (کے ساتھ / میں)، لِـ (کے لیے)، كَـ (جیسے)",
                "مِنْ (from), إِلَى (to), فِي (in), عَلَى (on), عَنْ (about / away from), بِـ (with / by), لِـ (for), كَـ (like)"),
            Bi("حرفِ جر کے بعد اسم ہمیشہ مجرور ہوتا ہے: کسرہ یا تنوینِ کسرہ۔",
                "The noun after a preposition is always majrur: kasra or tanween kasra."),
            Bi("بِـ، لِـ اور كَـ اسم سے جڑ کر لکھے جاتے ہیں: لِلَّهِ، بِالْقَلَمِ",
                "بِـ، لِـ and كَـ are written joined to the noun: لِلَّهِ، بِالْقَلَمِ")
        ),
        examples = listOf(
            LessonExample("الْكِتَابُ عَلَى الطَّاوِلَةِ",
                Bi("الْكِتَابُ: مبتدا\nعَلَى: حرف جر\nالطَّاوِلَةِ: اسم مجرور\nمعنی: کتاب میز پر ہے",
                    "الْكِتَابُ: mubtada'\nعَلَى: preposition\nالطَّاوِلَةِ: majrur noun\nMeaning: The book is on the table")),
            LessonExample("ذَهَبْتُ إِلَى الْمَسْجِدِ",
                Bi("ذَهَبْتُ: فعل اور فاعل (تُ)\nإِلَى: حرف جر\nالْمَسْجِدِ: اسم مجرور\nمعنی: میں مسجد گیا",
                    "ذَهَبْتُ: verb and doer (تُ)\nإِلَى: preposition\nالْمَسْجِدِ: majrur noun\nMeaning: I went to the mosque")),
            LessonExample("الْحَمْدُ لِلَّهِ",
                Bi("الْحَمْدُ: مبتدا\nلِ: حرف جر\nاللَّهِ: اسم مجرور\nمعنی: تمام تعریفیں اللہ کے لیے ہیں",
                    "الْحَمْدُ: mubtada'\nلِ: preposition\nاللَّهِ: majrur noun\nMeaning: All praise is for Allah"))
        ),
        sentence = "ذَهَبْتُ إِلَى الْمَسْجِدِ",
        sentenceMeaning = Bi("میں مسجد گیا", "I went to the mosque"),
        steps = listOf(
            LessonStep(Bi("«إِلَى» کیا ہے؟", "What is «إِلَى»?"),
                listOf(Bi("حرفِ جر", "Preposition"), Bi("فعل", "Verb"), Bi("فاعل", "Doer")), 0,
                Bi("یہ اسم سے پہلے آ کر اسے جر دیتا ہے۔", "It comes before a noun and puts it in jarr.")),
            LessonStep(Bi("«إِلَى» کا مطلب کیا ہے؟", "What does «إِلَى» mean?"),
                listOf(Bi("سے", "from"), Bi("کی طرف / تک", "to / towards"), Bi("پر", "on")), 1,
                Bi("إِلَى کا مطلب ہے کی طرف یا تک۔", "إِلَى means to or towards.")),
            LessonStep(Bi("«الْمَسْجِدِ» کی حالت کیا ہے؟", "What case is «الْمَسْجِدِ» in?"), oCases, 2,
                Bi("حرفِ جر کے بعد اسم مجرور ہوتا ہے۔", "The noun after a preposition is majrur.")),
            LessonStep(Bi("جر کی علامت کیا ہے؟", "What is the sign of jarr here?"),
                listOf(Bi("ضمہ", "Damma"), Bi("فتحہ", "Fatha"), Bi("کسرہ", "Kasra")), 2,
                Bi("جر کی علامت کسرہ (ـِ) ہے۔", "The sign of jarr is kasra (ـِ)."))
        )
    ),

    Lesson(
        id = "idafa", icon = "🔗",
        title = Bi("اضافت (مضاف اور مضاف الیہ)", "Possession (Idafa)"),
        intro = Bi(
            "جب دو اسم مل کر «کا / کی» کا مطلب دیں تو اسے اضافت کہتے ہیں۔ پہلا اسم مضاف اور دوسرا مضاف الیہ ہوتا ہے۔ جیسے: كِتَابُ الطَّالِبِ = طالب علم کی کتاب۔",
            "When two nouns combine to mean «of», it is called idafa. The first noun is the mudaf and the second is the mudaf ilayh. e.g. كِتَابُ الطَّالِبِ = the student's book."
        ),
        points = listOf(
            Bi("مضاف پر ال اور تنوین نہیں آتی۔", "The mudaf never takes ال or tanween."),
            Bi("مضاف الیہ ہمیشہ مجرور (کسرہ) ہوتا ہے۔", "The mudaf ilayh is always majrur (kasra)."),
            Bi("مضاف کی حالت جملے میں اس کے کردار کے مطابق بدلتی ہے۔", "The case of the mudaf depends on its role in the sentence."),
            Bi("اردو کے برعکس پہلے وہ چیز آتی ہے جس کی بات ہو، پھر مالک: كِتَابُ الطَّالِبِ (کتاب، پھر طالب علم)۔",
                "The possessed thing comes first, then the owner: كِتَابُ الطَّالِبِ (book, then student).")
        ),
        examples = listOf(
            LessonExample("كِتَابُ الطَّالِبِ",
                Bi("كِتَابُ: مضاف (بغیر ال اور تنوین)\nالطَّالِبِ: مضاف الیہ (مجرور)\nمعنی: طالب علم کی کتاب",
                    "كِتَابُ: mudaf (no ال, no tanween)\nالطَّالِبِ: mudaf ilayh (majrur)\nMeaning: The student's book")),
            LessonExample("بَابُ الْبَيْتِ",
                Bi("بَابُ: مضاف\nالْبَيْتِ: مضاف الیہ (مجرور)\nمعنی: گھر کا دروازہ",
                    "بَابُ: mudaf\nالْبَيْتِ: mudaf ilayh (majrur)\nMeaning: The door of the house")),
            LessonExample("مَسْجِدُ الْمَدِينَةِ",
                Bi("مَسْجِدُ: مضاف\nالْمَدِينَةِ: مضاف الیہ (مجرور)\nمعنی: شہر کی مسجد",
                    "مَسْجِدُ: mudaf\nالْمَدِينَةِ: mudaf ilayh (majrur)\nMeaning: The mosque of the city"))
        ),
        sentence = "كِتَابُ الطَّالِبِ",
        sentenceMeaning = Bi("طالب علم کی کتاب", "The student's book"),
        steps = listOf(
            LessonStep(Bi("«كِتَابُ» کیا ہے؟", "What is «كِتَابُ»?"),
                listOf(Bi("مضاف", "Mudaf"), Bi("مضاف الیہ", "Mudaf ilayh"), Bi("فاعل", "Doer")), 0,
                Bi("یہ پہلا اسم ہے جس کی نسبت آگے والے اسم کی طرف ہے۔", "It is the first noun, attributed to the noun after it.")),
            LessonStep(Bi("«الطَّالِبِ» کیا ہے؟", "What is «الطَّالِبِ»?"),
                listOf(Bi("مضاف", "Mudaf"), Bi("مضاف الیہ", "Mudaf ilayh"), Bi("مفعول بہ", "Object")), 1,
                Bi("یہ مالک ہے جس کی طرف نسبت کی گئی، اس لیے مضاف الیہ ہے۔", "It is the owner the first noun is attributed to, so it is the mudaf ilayh.")),
            LessonStep(Bi("«الطَّالِبِ» کی حالت کیا ہے؟", "What case is «الطَّالِبِ» in?"), oCases, 2,
                Bi("مضاف الیہ ہمیشہ مجرور ہوتا ہے، علامت کسرہ ہے۔", "The mudaf ilayh is always majrur, with kasra.")),
            LessonStep(Bi("کیا «كِتَابُ» پر ال لگ سکتا ہے؟", "Can «كِتَابُ» take ال here?"),
                listOf(Bi("ہاں", "Yes"), Bi("نہیں", "No")), 1,
                Bi("مضاف پر ال اور تنوین نہیں آتی۔", "The mudaf takes neither ال nor tanween."))
        )
    ),

    Lesson(
        id = "demonstrative", icon = "👉",
        title = Bi("اسمِ اشارہ", "Demonstratives"),
        intro = Bi(
            "اسمِ اشارہ کسی چیز کی طرف اشارہ کرتا ہے۔ قریب کے لیے هَذَا / هَذِهِ آتے ہیں اور دور کے لیے ذَلِكَ / تِلْكَ۔",
            "A demonstrative points to something. Near: هَذَا / هَذِهِ. Far: ذَلِكَ / تِلْكَ."
        ),
        points = listOf(
            Bi("قریب: هَذَا (یہ، مذکر)، هَذِهِ (یہ، مؤنث)، هَؤُلَاءِ (یہ سب)",
                "Near: هَذَا (this, m.), هَذِهِ (this, f.), هَؤُلَاءِ (these)"),
            Bi("بعید: ذَلِكَ (وہ، مذکر)، تِلْكَ (وہ، مؤنث)، أُولَئِكَ (وہ سب)",
                "Far: ذَلِكَ (that, m.), تِلْكَ (that, f.), أُولَئِكَ (those)"),
            Bi("اشارہ کے بعد ال والا اسم ہو تو «یہ + اسم» بنتا ہے: هَذَا الْكِتَابُ = یہ کتاب",
                "With an ال noun after it you get «this + noun»: هَذَا الْكِتَابُ = this book"),
            Bi("اشارہ کے بعد بغیر ال والا اسم ہو تو مکمل جملہ بنتا ہے: هَذَا كِتَابٌ = یہ ایک کتاب ہے",
                "With a noun without ال you get a full sentence: هَذَا كِتَابٌ = this is a book")
        ),
        examples = listOf(
            LessonExample("هَذَا الْكِتَابُ جَدِيدٌ",
                Bi("هَذَا: اسم اشارہ (مبتدا)\nالْكِتَابُ: اشارہ کی وضاحت (بدل)\nجَدِيدٌ: خبر\nمعنی: یہ کتاب نئی ہے",
                    "هَذَا: demonstrative (mubtada')\nالْكِتَابُ: explains the demonstrative (badal)\nجَدِيدٌ: khabar\nMeaning: This book is new")),
            LessonExample("هَذِهِ مَدْرَسَةٌ",
                Bi("هَذِهِ: اسم اشارہ (مبتدا)\nمَدْرَسَةٌ: خبر\nمعنی: یہ ایک مدرسہ ہے",
                    "هَذِهِ: demonstrative (mubtada')\nمَدْرَسَةٌ: khabar\nMeaning: This is a school")),
            LessonExample("تِلْكَ الطَّالِبَةُ مُجْتَهِدَةٌ",
                Bi("تِلْكَ: اسم اشارہ (مبتدا)\nالطَّالِبَةُ: اشارہ کی وضاحت (بدل)\nمُجْتَهِدَةٌ: خبر\nمعنی: وہ طالبہ محنتی ہے",
                    "تِلْكَ: demonstrative (mubtada')\nالطَّالِبَةُ: explains the demonstrative (badal)\nمُجْتَهِدَةٌ: khabar\nMeaning: That female student is hardworking"))
        ),
        sentence = "هَذِهِ مَدْرَسَةٌ",
        sentenceMeaning = Bi("یہ ایک مدرسہ ہے", "This is a school"),
        steps = listOf(
            LessonStep(Bi("«هَذِهِ» کس کے لیے آتا ہے؟", "What is «هَذِهِ» used for?"),
                listOf(Bi("قریب مذکر", "near, masculine"), Bi("قریب مؤنث", "near, feminine"), Bi("بعید مذکر", "far, masculine")), 1,
                Bi("هَذِهِ قریب کی مؤنث چیز کے لیے ہے، اور مَدْرَسَةٌ مؤنث ہے۔", "هَذِهِ is for a near feminine thing, and مَدْرَسَةٌ is feminine.")),
            LessonStep(Bi("«وہ کتاب» کے لیے کون سا درست ہے؟", "Which is correct for «that book»?"),
                listOf(Bi("هَذَا الْكِتَابُ", "هَذَا الْكِتَابُ"), Bi("ذَلِكَ الْكِتَابُ", "ذَلِكَ الْكِتَابُ"), Bi("تِلْكَ الْكِتَابُ", "تِلْكَ الْكِتَابُ")), 1,
                Bi("كِتَابٌ مذکر ہے اور وہ دور کی چیز ہے، اس لیے ذَلِكَ آئے گا۔", "كِتَابٌ is masculine and far, so ذَلِكَ is used.")),
            LessonStep(Bi("«هَذَا كِتَابٌ» کا مطلب کیا ہے؟", "What does «هَذَا كِتَابٌ» mean?"),
                listOf(Bi("یہ ایک کتاب ہے", "This is a book"), Bi("یہ کتاب (خاص)", "This book (specific)"), Bi("وہ کتاب", "That book")), 0,
                Bi("كِتَابٌ بغیر ال ہے، اس لیے یہ مکمل جملہ ہے۔", "كِتَابٌ has no ال, so this is a full sentence."))
        )
    )
)

/* ============ اسکرینیں ============ */
@Composable
fun LessonsScreen() {
    var openId by remember { mutableStateOf<String?>(null) }
    val lesson = nahwLessons.firstOrNull { it.id == openId }
    BackHandler(enabled = lesson != null) { openId = null }
    if (lesson == null) {
        LessonList(onOpen = { openId = it })
    } else {
        LessonDetail(lesson, onBack = { openId = null })
    }
}

@Composable
fun LessonList(onOpen: (String) -> Unit) {
    val en = LocalEn.current
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(tr("📘 انٹرایکٹو نحو", "📘 Interactive Nahw"), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Brand)
                Text(
                    tr("ہر سبق میں آسان وضاحت، مثالیں، اعراب اور مرحلہ وار مشق ہے۔",
                        "Each lesson has a simple explanation, examples, i'rab and a step-by-step exercise."),
                    fontSize = 14.sp, color = Muted, lineHeight = 24.sp
                )
            }
        }
        items(nahwLessons) { l ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onOpen(l.id) },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Border),
                shape = RoundedCornerShape(16.dp)
            ) {
                androidx.compose.foundation.layout.Row(
                    Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        Modifier.size(48.dp).clip(CircleShape).background(BrandLight),
                        contentAlignment = Alignment.Center
                    ) { Text(l.icon, fontSize = 22.sp) }
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(l.title.pick(en), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BrandDark)
                        Text(l.intro.pick(en), fontSize = 12.sp, color = Muted, lineHeight = 20.sp,
                            maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                    Text(tr("کھولیں", "Open"), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Brand)
                }
            }
        }
        item { Spacer(Modifier.height(12.dp)) }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(text, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Brand, modifier = Modifier.padding(top = 4.dp))
}

@Composable
fun LessonDetail(lesson: Lesson, onBack: () -> Unit) {
    val en = LocalEn.current
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BigButton(tr("واپس: تمام اسباق", "Back to all lessons"), onClick = onBack, filled = false, modifier = Modifier.fillMaxWidth())

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Brand),
            shape = RoundedCornerShape(18.dp)
        ) {
            androidx.compose.foundation.layout.Row(
                Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(lesson.icon, fontSize = 32.sp)
                Text(lesson.title.pick(en), fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Color.White, lineHeight = 28.sp)
            }
        }

        SectionTitle(tr("📖 وضاحت", "📖 Explanation"))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Border),
            shape = RoundedCornerShape(16.dp)
        ) {
            MixedText(lesson.intro.pick(en), size = 15, arabicSize = 19, align = TextAlign.Start,
                modifier = Modifier.fillMaxWidth().padding(16.dp))
        }

        SectionTitle(tr("⭐ اہم نکات", "⭐ Key Points"))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Border),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                lesson.points.forEachIndexed { i, p ->
                    androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            Modifier.size(24.dp).clip(CircleShape).background(BrandLight),
                            contentAlignment = Alignment.Center
                        ) { Text("${i + 1}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Brand) }
                        MixedText(p.pick(en), size = 14, arabicSize = 18, align = TextAlign.Start,
                            modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        SectionTitle(tr("📝 مثالیں اور اعراب", "📝 Examples and I'rab"))
        ExampleParseTable(lesson.examples)

        SectionTitle(tr("🧩 مرحلہ وار مشق", "🧩 Step-by-Step Exercise"))
        StepExercise(lesson)

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun ExampleParseTable(examples: List<LessonExample>) {
    val en = LocalEn.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Border),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(Modifier.fillMaxWidth()) {
            androidx.compose.foundation.layout.Row(Modifier.fillMaxWidth().background(Brand).padding(10.dp)) {
                Text(tr("عربی جملہ", "Arabic"), Modifier.weight(1f), color = Color.White,
                    fontWeight = FontWeight.Bold, fontSize = 13.sp, textAlign = TextAlign.Center)
                Text(tr("اعراب اور ترجمہ", "I'rab and meaning"), Modifier.weight(1.6f), color = Color.White,
                    fontWeight = FontWeight.Bold, fontSize = 13.sp, textAlign = TextAlign.Center)
            }
            examples.forEachIndexed { idx, ex ->
                androidx.compose.foundation.layout.Row(Modifier.height(IntrinsicSize.Min)) {
                    Box(
                        Modifier.weight(1f).fillMaxHeight().background(BrandLight)
                            .border(0.5.dp, Border).padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        MixedText(ex.arabic, size = 14, arabicSize = 20, forceArabic = true)
                    }
                    Column(
                        Modifier.weight(1.6f).fillMaxHeight()
                            .background(if (idx % 2 == 0) Color.White else Page)
                            .border(0.5.dp, Border).padding(10.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        ex.parse.pick(en).split("\n").forEach { line ->
                            val isMeaning = line.startsWith("معنی") || line.startsWith("Meaning")
                            MixedText(
                                line, size = 13, arabicSize = 17, align = TextAlign.Start,
                                color = if (isMeaning) AmberText else Color(0xFF0F172A),
                                bold = isMeaning
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StepExercise(lesson: Lesson) {
    val context = LocalContext.current
    val en = LocalEn.current
    var round by remember(lesson.id) { mutableStateOf(0) }
    var index by remember(lesson.id) { mutableStateOf(0) }
    var score by remember(lesson.id) { mutableStateOf(0) }
    var selected by remember(lesson.id) { mutableStateOf<Int?>(null) }
    val orders = remember(lesson.id, round) { lesson.steps.map { st -> st.options.indices.shuffled() } }
    val total = lesson.steps.size

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(color = BrandLight, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)) {
                MixedText(lesson.sentence, size = 15, arabicSize = 24, forceArabic = true, arabicColor = BrandDark)
                Text(lesson.sentenceMeaning.pick(en), fontSize = 13.sp, color = Muted)
            }
        }

        if (index >= total) {
            Surface(color = BrandLight, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("$score / $total", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = BrandDark)
                    Text(
                        when {
                            score == total -> tr("ماشاء اللہ! سبق مکمل ہوا۔", "MashaAllah! Lesson mastered.")
                            score * 10 >= total * 7 -> tr("بہت خوب! ایک بار وضاحت دوبارہ دیکھ لیں۔", "Great! Review the explanation once more.")
                            else -> tr("اوپر کی وضاحت دوبارہ پڑھ کر مشق دہرائیں۔", "Re-read the explanation above and try again.")
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
            val step = lesson.steps[index]
            val perm = orders[index]
            Text(
                tr("مرحلہ ${index + 1} / $total      درست: $score", "Step ${index + 1} / $total      Correct: $score"),
                fontSize = 13.sp, color = Muted
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Border),
                shape = RoundedCornerShape(16.dp)
            ) {
                MixedText(step.q.pick(en), size = 17, arabicSize = 22, bold = true, align = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth().padding(16.dp))
            }
            perm.forEachIndexed { shown, orig ->
                val sel = selected
                val isCorrect = orig == step.correct
                val bg = when {
                    sel == null -> Color.White
                    isCorrect -> Good
                    shown == sel -> Bad
                    else -> Color.White
                }
                val bc = when {
                    sel == null -> Border
                    isCorrect -> GoodText
                    shown == sel -> BadText
                    else -> Border
                }
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = bg,
                    border = BorderStroke(1.5.dp, bc),
                    modifier = Modifier.fillMaxWidth().clickable(enabled = sel == null) {
                        selected = shown
                        if (isCorrect) score += 1
                    }
                ) {
                    MixedText(step.options[orig].pick(en), size = 16, arabicSize = 21, align = TextAlign.Start,
                        modifier = Modifier.padding(14.dp))
                }
            }
            val sel = selected
            if (sel != null) {
                val ok = perm[sel] == step.correct
                Surface(color = if (ok) Good else Amber, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            if (ok) tr("✅ درست جواب", "✅ Correct") else tr("❌ غلط جواب", "❌ Not quite"),
                            fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (ok) GoodText else AmberText
                        )
                        MixedText(step.why.pick(en), size = 14, arabicSize = 18, align = TextAlign.Start)
                    }
                }
                BigButton(
                    if (index == total - 1) tr("نتیجہ دیکھیں", "See Result") else tr("اگلا مرحلہ", "Next Step"),
                    onClick = {
                        if (index == total - 1) {
                            addResult(context, ScoreEntry("nahw", lesson.title.pick(en), score, total, nowString()))
                        }
                        index += 1; selected = null
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
