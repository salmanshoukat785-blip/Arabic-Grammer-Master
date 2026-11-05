package com.example.arabicgrammar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/* ============ Splash + Onboarding رنگ ============ */
private val IntroGreen = Color(0xFF0B6E4F)
private val IntroDark = Color(0xFF064E3B)
private val IntroGold = Color(0xFFD4A017)

/* ============ لوگو: القرآن ============ */
@Composable
fun QuranLogo(size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .shadow(12.dp, CircleShape)
            .clip(CircleShape)
            .background(Color.White)
            .border(size * 0.04f, IntroGold, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "القرآن",
            fontSize = (size.value * 0.28f).sp,
            fontWeight = FontWeight.Bold,
            color = IntroGreen,
            maxLines = 1
        )
    }
}

/* ============ Splash Screen ============ */
@Composable
fun SplashScreen(onDone: () -> Unit) {
    var started by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(1000),
        label = "splashAlpha"
    )
    LaunchedEffect(Unit) {
        started = true
        delay(2800)
        onDone()
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(IntroGreen, IntroDark))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier.alpha(alpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
        val pagerState = rememberPagerState(pageCount = { onboardPages.size })
        val scope = rememberCoroutineScope()
        val isLast = pagerState.currentPage == onboardPages.size - 1

        Column(Modifier.fillMaxSize().background(Color.White)) {
            // Skip (آخری صفحے پر چھپ جاتا ہے، جگہ برقرار رہتی ہے)
            Box(
                Modifier.fillMaxWidth().height(64.dp).padding(top = 12.dp, end = 8.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (!isLast) {
                    TextButton(onClick = onFinish) {
                        Text("Skip", fontSize = 16.sp, color = IntroGreen)
                    }
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardPageContent(onboardPages[page])
            }

            // نقطے
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(onboardPages.size) { i ->
                    val active = i == pagerState.currentPage
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

            Button(
                onClick = {
                    if (isLast) onFinish()
                    else scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                },
                colors = ButtonDefaults.buttonColors(containerColor = IntroGreen, contentColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 28.dp)
                    .height(52.dp)
            ) {
                Text(
                    if (isLast) "Get Started" else "Next",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
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
            Modifier.size(140.dp).clip(CircleShape).background(IntroGreen.copy(alpha = 0.1f)),
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
