package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    backgroundColor: Color = Color(0xCC151922),
    borderColor: Color = Color(0x2BFFFFFF),
    borderWidth: Dp = 1.dp,
    elevation: Dp = 12.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(elevation, shape, spotColor = Color(0x80000000))
            .clip(shape)
            .background(backgroundColor)
            .border(
                borderWidth,
                Brush.verticalGradient(
                    listOf(
                        borderColor.copy(alpha = 0.35f),
                        borderColor.copy(alpha = 0.08f)
                    )
                ),
                shape
            ),
        content = content
    )
}

@Composable
fun GlassCapsule(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xDF1C2230),
    borderColor: Color = Color(0x33FFFFFF),
    content: @Composable BoxScope.() -> Unit
) {
    GlassSurface(
        modifier = modifier,
        shape = RoundedCornerShape(32.dp),
        backgroundColor = backgroundColor,
        borderColor = borderColor,
        borderWidth = 1.dp,
        elevation = 8.dp,
        content = content
    )
}
