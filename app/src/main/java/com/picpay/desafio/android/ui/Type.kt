package com.picpay.desafio.android.ui

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.picpay.desafio.android.R

val monaSansFont = FontFamily(
    Font(R.font.mona_sans_regular, FontWeight.Normal),
    Font(R.font.mona_sans_black, FontWeight.Black),
    Font(R.font.mona_sans_bold, FontWeight.Bold),
    Font(R.font.mona_sans_extrabold, FontWeight.ExtraBold),
    Font(R.font.mona_sans_light, FontWeight.Light),
    Font(R.font.mona_sans_medium, FontWeight.Medium),
    Font(R.font.mona_sans_semibold, FontWeight.SemiBold),
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)