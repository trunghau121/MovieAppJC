package com.core_app.extension

import android.content.res.Resources
import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType

private var pdpRatio = 1f
private var pspRatio = 1f
val dpRatio
    get() = pdpRatio
val spRatio
    get() = pspRatio

@JvmInline
value class ADp(val value: Int) {
    companion object{
        fun initializeAdp(resources: Resources, designWidth: Int) {
            val sweetRatio = resources.displayMetrics.widthPixels.div(resources.displayMetrics.density)
            pdpRatio = designWidth.toFloat().div(sweetRatio)
            pspRatio = resources.configuration.fontScale.times(dpRatio)
        }
    }
}


@Stable
inline val Int.aDp: Dp
    get() = Dp(value = this.toFloat().div(dpRatio))

@Stable
inline val Double.aDp: Dp
    get() = Dp(value = this.toFloat().div(dpRatio))

@Stable
inline val Int.aSp: TextUnit
    get() = TextUnit(value = this.toFloat().div(spRatio), TextUnitType.Sp)

@Stable
inline val Double.aSp: TextUnit
    get() = TextUnit(value = this.toFloat().div(spRatio), TextUnitType.Sp)