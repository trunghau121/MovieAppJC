package com.movieappjc.presentation.screen.person

import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity

@Composable
fun getPersonNestedScrollConnection(
    anchoredDraggableState: AnchoredDraggableState<AnchoredDraggableCardState>
): NestedScrollConnection {
    return remember {
        object : NestedScrollConnection {

            override fun onPreScroll( // Desides if use the sroll for parent (Swipe) or pass it to the childern
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val delta = available.y
                return if (delta < 0) {
                    anchoredDraggableState.dispatchRawDelta(delta).toOffset()
                } else {
                    Offset.Zero
                }
            }

            override fun onPostScroll( // If there is any leftover sroll from childern, let's try to use it on parent swipe
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val delta = available.y
                return anchoredDraggableState.dispatchRawDelta(delta).toOffset()
            }

            override suspend fun onPostFling( // Lets's try to use fling on parent and pass all leftover to childern
                consumed: Velocity,
                available: Velocity
            ): Velocity {
                anchoredDraggableState.settle(animationSpec = spring())
                return super.onPostFling(consumed, available)
            }

            private fun Float.toOffset() = Offset(0f, this)
        }
    }
}