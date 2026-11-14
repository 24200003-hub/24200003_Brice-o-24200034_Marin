package com.esan.diarioespacial.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = StarBlue,
    onPrimary = StarBlueDeep,
    primaryContainer = StarBlueContainer,
    onPrimaryContainer = StarBlueOnContainer,
    secondary = NebulaGrey,
    tertiary = MeteorOrange,
    onTertiary = MeteorOrangeDeep,
    background = NightBackground,
    onBackground = TextOnNight,
    surface = NightBackground,
    onSurface = TextOnNight,
    surfaceVariant = NightSurfaceHigh,
    onSurfaceVariant = TextOnNightMuted,
    // Los Card de Material 3 usan los "surfaceContainer" como fondo; se definen
    // para que las tarjetas resalten sobre el fondo azul noche.
    surfaceContainerLowest = NightBackground,
    surfaceContainerLow = NightSurface,
    surfaceContainer = NightSurface,
    surfaceContainerHigh = NightSurfaceHigh,
    surfaceContainerHighest = NightSurfaceHighest,
    outline = OutlineNight,
    outlineVariant = OutlineNight,
    error = DangerLight,
    errorContainer = DangerContainerDark,
    onErrorContainer = OnDangerContainerDark
)

private val LightColorScheme = lightColorScheme(
    primary = CosmicBlue,
    primaryContainer = CosmicBlueContainer,
    onPrimaryContainer = CosmicBlueOnContainer,
    secondary = SlateBlue,
    tertiary = MeteorBrown,
    background = DayBackground,
    onBackground = TextOnDay,
    surface = DayBackground,
    onSurface = TextOnDay,
    onSurfaceVariant = TextOnDayMuted,
    surfaceContainerHigh = DaySurfaceHigh,
    surfaceContainerHighest = DaySurfaceHighest
)

// Sin "dynamic color": en Android 12+ la app tomaría los colores del fondo de
// pantalla del usuario y perdería su identidad visual espacial.
@Composable
fun DiarioEspacialTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
