package com.mensis.app.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.mensis.app.data.ThemeMode

/** Cycle-phase + semantic colors not covered by the Material color roles. */
data class PhaseColors(
    val period: Color,
    val fertile: Color,
    val ovulation: Color,
    val follicular: Color,
    val luteal: Color,
    val onPhase: Color
)

val LocalPhaseColors = staticCompositionLocalOf {
    PhaseColors(PeriodLight, FertileLight, OvulationLight, FollicularLight, LutealLight, LightInk)
}

private val LightScheme = lightColorScheme(
    primary = Blush,
    onPrimary = Color.White,
    primaryContainer = PeriodLight,
    onPrimaryContainer = Color(0xFF4C0519),
    secondary = Coral,
    onSecondary = Color.White,
    background = LightBackground,
    onBackground = LightInk,
    surface = LightSurface,
    onSurface = LightInk,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightInkSoft,
    outline = LightOutlineStrong,
    outlineVariant = LightOutline
)

private val DarkScheme = darkColorScheme(
    primary = BlushDark,
    onPrimary = Color(0xFF4C0519),
    primaryContainer = PeriodDark,
    onPrimaryContainer = Color(0xFFFFE4E6),
    secondary = Coral,
    onSecondary = Color(0xFF4C0519),
    background = DarkBackground,
    onBackground = DarkInk,
    surface = DarkSurface,
    onSurface = DarkInk,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkInkSoft,
    outline = DarkOutlineStrong,
    outlineVariant = DarkOutline
)

@Composable
fun MensisTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val dark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val phaseColors = if (dark) {
        PhaseColors(PeriodDark, FertileDark, OvulationDark, FollicularDark, LutealDark, DarkInk)
    } else {
        PhaseColors(PeriodLight, FertileLight, OvulationLight, FollicularLight, LutealLight, LightInk)
    }
    // Adatta le icone di status bar e nav bar al tema EFFETTIVO dell'app (non a quello di
    // sistema): in modalità chiara icone scure, così la barra delle notifiche resta visibile.
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = view.context.findActivity()?.window ?: return@SideEffect
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !dark
            controller.isAppearanceLightNavigationBars = !dark
        }
    }

    CompositionLocalProvider(LocalPhaseColors provides phaseColors) {
        MaterialTheme(
            colorScheme = if (dark) DarkScheme else LightScheme,
            typography = MensisTypography,
            content = content
        )
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
