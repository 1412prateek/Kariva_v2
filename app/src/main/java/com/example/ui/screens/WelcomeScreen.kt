package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.KarivaButton
import com.example.ui.components.KarivaEmblem
import com.example.ui.theme.KarivaTerracotta
import com.example.ui.theme.KarivaTerracottaLight

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onSignIn: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("welcome_screen")
    ) {
        // Hero Background Image (Handcrafted crochet & woolen creations)
        Image(
            painter = painterResource(id = R.drawable.crochet_hero_artisan),
            contentDescription = "Kariva Handcrafted Kurus",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Warm cozy vignette gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.20f),
                            Color.Black.copy(alpha = 0.35f),
                            Color.Black.copy(alpha = 0.85f),
                            Color.Black.copy(alpha = 0.98f)
                        ),
                        startY = 100f
                    )
                )
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            KarivaEmblem(size = 46)

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Kariva",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp
                ),
                color = Color.White
            )

            Text(
                text = "HANDCRAFTED KURUS & WOOL ATELIER",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 2.5.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = KarivaTerracottaLight
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Every stitch tells a story\nWarmth & craft made with love 🧶",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Normal,
                    lineHeight = 24.sp
                ),
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Carousel indicator line
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height(4.dp)
                        .background(KarivaTerracotta, RoundedCornerShape(2.dp))
                )
                Box(
                    modifier = Modifier
                        .width(10.dp)
                        .height(4.dp)
                        .background(Color.White.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
                )
                Box(
                    modifier = Modifier
                        .width(10.dp)
                        .height(4.dp)
                        .background(Color.White.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            KarivaButton(
                text = "Explore Creations",
                onClick = onGetStarted,
                isTerracotta = true,
                testTag = "welcome_get_started_btn"
            )

            Spacer(modifier = Modifier.height(14.dp))

            KarivaButton(
                text = "Sign In",
                onClick = onSignIn,
                isSecondary = true,
                modifier = Modifier.background(
                    Color.White.copy(alpha = 0.15f),
                    RoundedCornerShape(28.dp)
                ),
                testTag = "welcome_sign_in_btn"
            )

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
