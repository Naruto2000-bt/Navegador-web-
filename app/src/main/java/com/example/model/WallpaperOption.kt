package com.example.model

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

sealed class WallpaperOption(
    val id: String,
    val name: String,
    val isImage: Boolean = false,
    val drawableRes: Int? = null,
    val previewGradient: Brush = Brush.verticalGradient(
        listOf(
            Color(0xFF18181B),
            Color(0xFF09090B),
            Color(0xFF000000)
        )
    )
) {
    data object Aurora : WallpaperOption(
        id = "aurora",
        name = "Aurora Boreal",
        previewGradient = Brush.verticalGradient(
            listOf(
                Color(0xFF0D253A),
                Color(0xFF0F4C5C),
                Color(0xFF052B36),
                Color(0xFF070B12)
            )
        )
    )

    data object ZenSunset : WallpaperOption(
        id = "zen_sunset",
        name = "Dunas de Atardecer",
        previewGradient = Brush.verticalGradient(
            listOf(
                Color(0xFF4A1942),
                Color(0xFF893168),
                Color(0xFF2E1C38),
                Color(0xFF0D0A14)
            )
        )
    )

    data object CosmicNight : WallpaperOption(
        id = "cosmic_night",
        name = "Noche Cósmica",
        previewGradient = Brush.verticalGradient(
            listOf(
                Color(0xFF1E1B4B),
                Color(0xFF312E81),
                Color(0xFF0F172A),
                Color(0xFF090D16)
            )
        )
    )

    data object CyberGlow : WallpaperOption(
        id = "cyber_glow",
        name = "Resplandor Neón",
        previewGradient = Brush.linearGradient(
            listOf(
                Color(0xFF0D1B2A),
                Color(0xFF1E3A8A),
                Color(0xFF064E3B),
                Color(0xFF0B0F19)
            )
        )
    )

    data object EmeraldForest : WallpaperOption(
        id = "emerald_forest",
        name = "Esmeralda Místico",
        previewGradient = Brush.verticalGradient(
            listOf(
                Color(0xFF064E3B),
                Color(0xFF065F46),
                Color(0xFF022C22),
                Color(0xFF04120D)
            )
        )
    )

    data object CrimsonNebula : WallpaperOption(
        id = "crimson_nebula",
        name = "Nebulosa Rubí",
        previewGradient = Brush.verticalGradient(
            listOf(
                Color(0xFF4C0519),
                Color(0xFF881337),
                Color(0xFF1C0A14),
                Color(0xFF0B0307)
            )
        )
    )

    data object MinimalDark : WallpaperOption(
        id = "minimal_dark",
        name = "Negro Minimalista",
        previewGradient = Brush.verticalGradient(
            listOf(
                Color(0xFF18181B),
                Color(0xFF09090B),
                Color(0xFF000000)
            )
        )
    )

    data object FrostedElegance : WallpaperOption(
        id = "frosted_elegance",
        name = "Lavanda Suave",
        previewGradient = Brush.verticalGradient(
            listOf(
                Color(0xFF2E1065),
                Color(0xFF3B0764),
                Color(0xFF1E1B4B),
                Color(0xFF0F0B1E)
            )
        )
    )

    companion object {
        val allOptions: List<WallpaperOption>
            get() = listOf(
                Aurora,
                ZenSunset,
                CosmicNight,
                CyberGlow,
                EmeraldForest,
                CrimsonNebula,
                FrostedElegance,
                MinimalDark
            )

        fun fromId(id: String): WallpaperOption {
            return allOptions.firstOrNull { it.id == id } ?: Aurora
        }
    }
}
