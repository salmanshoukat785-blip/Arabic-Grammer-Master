package com.example.arabicgrammar

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* ============ Splash + Onboarding رنگ ============ */
private val IntroGreen = Color(0xFF0B6E4F)
private val IntroDark = Color(0xFF064E3B)
private val IntroGold = Color(0xFFD4A017)

/** ایپ چلنے کے دوران Splash / Onboarding صرف ایک بار دکھانے کے لیے */
var introDone = false

/* ============ لوگو: القرآن ============ */
@Composable
fun QuranLogo(logoSize: Dp) {
    Box(
        modifier = Modifier
            .size(logoSize)
            .clip(CircleShape)
            .background(Color.White)
            .border(logoSize * 0.04f, IntroGold, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "القرآن",
            fontSize = (logoSize.value * 0.28f).sp,
            fontWeight = FontWeight.Bold,
            color = IntroGreen,
            maxLines = 1
        )
    }
}

/* ============ Splash Screen ============ */
@Composable
fun SplashScreen(onDone: () -> Unit) {
    DisposableEffect(Unit) {
        val handler = Handler(Looper.getMainLooper())
        val task = Runnable { onDone() }
        handler.postDelayed(task, 2500L)
        onDispose { handler.removeCallbacks(task) }
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(IntroGreen, IntroDark))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            QuranLogo(160.dp)
            Spacer(Modifier.height(24.dp))
            Text(
                "Arabic Grammar",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(Modifier.height(6.dp))
            Text("قواعد اللغة العربية", fontSize = 24.sp, color = IntroGold)
        }
    }
}

/* ============ Onboarding (3 صفحے) ============ */
private data class OnboardPage(
    val emoji: String,
    val arabic: String,
    val titleEn: String,
    val titleUr: String,
    val bodyEn: String,
    val bodyUr: String
)

private val onboardPages = listOf(
    OnboardPage(
        "👋", "السلام عليكم",
        "Assalamu Alaikum! Welcome", "السلام علیکم! خوش آمدید",
        "Welcome to Arabic Grammar. Learn the Arabic language step by step.",
        "عربی گرامر میں خوش آمدید۔ عربی زبان قدم بہ قدم سیکھیں۔"
    ),
    OnboardPage(
        "📖", "تعلم القواعد",
        "Learn Grammar Easily", "گرامر آسانی سے سیکھیں",
        "Simple lessons on nouns, verbs, particles and sentence structure.",
        "اسم، فعل، حرف اور جملے کی ساخت پر آسان اسباق۔"
    ),
    OnboardPage(
        "🏆", "ابدأ الآن",
        "Practice & Improve", "مشق کریں اور بہتر بنیں",
        "Practice every day and track your progress. Let's begin!",
        "روزانہ مشق کریں اور اپنی ترقی دیکھیں۔ چلیں شروع کریں!"
    )
)

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    // Skip کا بٹن ہمیشہ دائیں طرف رہے، چاہے ایپ اردو (RTL) میں ہو
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        var page by remember { mutableStateOf(0) }
        val lastIndex = onboardPages.size - 1
        val isLast = page == lastIndex

        Column(Modifier.fillMaxSize().background(Color.White)) {
            // Skip (آخری صفحے پر چھپ جاتا ہے، جگہ برقرار رہتی ہے)
            Box(
                Modifier.fillMaxWidth().height(60.dp).padding(end = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (!isLast) {
                    Text(
                        "Skip",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = IntroGreen,
                        modifier = Modifier
                            .clickable { onFinish() }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    )
                }
            }

            // صفحے کا مواد (دائیں بائیں سوائپ بھی کر سکتے ہیں)
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        var total = 0f
                        detectHorizontalDragGestures(
                            onDragStart = { total = 0f },
                            onDragEnd = {
                                if (total < -80f && page < lastIndex) {
                                    page += 1
                                } else if (total > 80f && page > 0) {
                                    page -= 1
                                }
                            },
                            onDragCancel = { },
                            onHorizontalDrag = { _, amount -> total += amount }
                        )
                    }
            ) {
                OnboardPageContent(onboardPages[page])
            }

            // نقطے
            androidx.compose.foundation.layout.Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                for (i in 0..lastIndex) {
                    val active = i == page
                    Box(
                        Modifier
                            .padding(horizontal = 4.dp)
                            .width(if (active) 24.dp else 8.dp)
                            .height(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (active) IntroGreen else Color(0xFFD1D5DB))
                    )
                }
            }

            // Next / Get Started
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = IntroGreen,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 28.dp)
                    .clickable {
                        if (isLast) {
                            onFinish()
                        } else {
                            page += 1
                        }
                    }
            ) {
                Box(Modifier.padding(vertical = 15.dp), contentAlignment = Alignment.Center) {
                    Text(
                        if (isLast) "Get Started" else "Next",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardPageContent(p: OnboardPage) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            Modifier.size(140.dp).clip(CircleShape).background(Color(0xFFE6F2EE)),
            contentAlignment = Alignment.Center
        ) {
            Text(p.emoji, fontSize = 64.sp)
        }
        Spacer(Modifier.height(24.dp))
        Text(
            p.arabic,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = IntroGold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Text(
            p.titleEn,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = IntroDark,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            p.titleUr,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = IntroDark,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Text(
            p.bodyEn,
            fontSize = 15.sp,
            color = Color(0xFF4B5563),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            p.bodyUr,
            fontSize = 16.sp,
            lineHeight = 26.sp,
            color = Color(0xFF4B5563),
            textAlign = TextAlign.Center
        )
    }
}
