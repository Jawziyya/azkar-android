package io.jawziyya.azkar.data.helper

import androidx.annotation.DrawableRes
import io.jawziyya.azkar.R
import java.util.Calendar
import kotlin.math.abs

enum class MoonPhase(@DrawableRes val drawableRes: Int) {
    NewMoon(drawableRes = R.drawable.ic_moonphase_new_moon),
    WaxingCrescent(drawableRes = R.drawable.ic_moonphase_waxing_crescent),
    FirstQuarter(drawableRes = R.drawable.ic_moonphase_first_quarter),
    WaxingGibbous(drawableRes = R.drawable.ic_moonphase_waxing_gibbous),
    FullMoon(drawableRes = R.drawable.ic_moonphase_full_moon),
    WaningGibbous(drawableRes = R.drawable.ic_moonphase_waning_gibbous),
    LastQuarter(drawableRes = R.drawable.ic_moonphase_last_quarter),
    WaningCrescent(drawableRes = R.drawable.ic_moonphase_waning_crescent),
}

object MoonPhaseCalculator {

    /**
     * Returns the moon phase (rough approximation).
     */
    operator fun invoke(calendar: Calendar): MoonPhase {
        val fraction = getMoonPhaseFraction(calendar)

        return when {
            fraction < 0.03 || fraction > 0.97 -> MoonPhase.NewMoon
            fraction < 0.22 -> MoonPhase.WaxingCrescent
            fraction < 0.28 -> MoonPhase.FirstQuarter
            fraction < 0.47 -> MoonPhase.WaxingGibbous
            fraction < 0.53 -> MoonPhase.FullMoon
            fraction < 0.72 -> MoonPhase.WaningGibbous
            fraction < 0.78 -> MoonPhase.LastQuarter
            else -> MoonPhase.WaningCrescent
        }
    }

    /**
     * Returns the moon phase as a value between 0.0 and 1.0
     * 0.0 = New Moon
     * 0.25 = First Quarter
     * 0.5 = Full Moon
     * 0.75 = Last Quarter
     */
    private fun getMoonPhaseFraction(calendar: Calendar = Calendar.getInstance()): Double {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Conway’s algorithm approximation
        var r = year % 100
        r %= 19
        if (r > 9) r -= 19
        r = ((r * 11) % 30) + month + day
        if (month < 3) r += 2

        val adjustment = if (year < 2000) -4.0 else -8.3
        val phase = (r.toDouble() + adjustment) % 30.0

        return abs(phase) / 30.0
    }
}