package com.movieappjc.presentation.screen.drop_drag

import android.content.ClipData
import android.content.ClipDescription
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import kotlinx.coroutines.launch

/**
 * Modifier áp dụng cho Grid chính (vùng nhận Drop và xử lý AutoScroll)
 */
@OptIn(ExperimentalFoundationApi::class)
fun <T> Modifier.dragDropTargetContainer(
    state: DragAndDropState<T>,
    scroller: DragDropScroller,
    moveItem: (Int, Int) -> Unit
): Modifier = composed {
    var containerLeftOnScreenPx by remember { mutableFloatStateOf(0f) }
    var containerTopOnScreenPx by remember { mutableFloatStateOf(0f) }
    var containerBottomOnScreenPx by remember { mutableFloatStateOf(0f) }

    val composeView = LocalView.current
    val viewLocationInWindow = remember { IntArray(2) }
    val haptic = LocalHapticFeedback.current

    // ĐÃ THÊM: Khởi tạo CoroutineScope hợp lệ bên trong composed block
    val coroutineScope = rememberCoroutineScope()

    this.onGloballyPositioned { layoutCoordinates ->
        val bounds = layoutCoordinates.boundsInWindow()
        containerLeftOnScreenPx = bounds.left
        containerTopOnScreenPx = bounds.top
        containerBottomOnScreenPx = bounds.bottom
    }.dragAndDropTarget(
        shouldStartDragAndDrop = { event ->
            event.mimeTypes().contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
        },
        target = remember(scroller, state, coroutineScope) {
            object : DragAndDropTarget {
                override fun onStarted(event: DragAndDropEvent) {
                    val androidEvent = event.toAndroidDragEvent()
                    val dragItemState = androidEvent.localState as? DraggedItemState
                    state.draggedSourceIndex = dragItemState?.sourceIndex
                    haptic.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                }

                override fun onMoved(event: DragAndDropEvent) {
                    val androidEvent = event.toAndroidDragEvent()
                    composeView.getLocationOnScreen(viewLocationInWindow)
                    val pointerAbsoluteY = viewLocationInWindow[1] + androidEvent.y

                    scroller.handleDragPosition(
                        pointerAbsoluteY = pointerAbsoluteY,
                        containerTopPx = containerTopOnScreenPx,
                        containerBottomPx = containerBottomOnScreenPx
                    )
                }

                override fun onDrop(event: DragAndDropEvent): Boolean {
                    // 1. Dừng auto scroll ngay lập tức
                    scroller.stop()
                    haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)

                    val androidEvent = event.toAndroidDragEvent()
                    val dragItemState = androidEvent.localState as? DraggedItemState

                    if (dragItemState != null) {
                        val sourceIndex = dragItemState.sourceIndex
                        composeView.getLocationOnScreen(viewLocationInWindow)
                        val pointerAbsoluteX = viewLocationInWindow[0] + androidEvent.x
                        val pointerAbsoluteY = viewLocationInWindow[1] + androidEvent.y

                        val targetIndex = scroller.findTargetIndex(
                            dropTargetX = pointerAbsoluteX,
                            dropTargetY = pointerAbsoluteY,
                            containerLeftPx = containerLeftOnScreenPx,
                            containerTopPx = containerTopOnScreenPx
                        )

                        if (targetIndex != null && sourceIndex != targetIndex) {
                            // 2. Reset trạng thái kéo về null TRƯỚC để Item lấy lại Alpha = 1f
                            state.draggedSourceIndex = null

                            // 3. Thực hiện Swap vị trí dữ liệu
                            moveItem(sourceIndex, targetIndex)

                            // 4. FIX TRIỆT ĐỂ LỖI INDEX 0:
                            // Nếu đích đến là 0, gọi scroller ép buộc đứng im ngay tại Frame này
                            if (targetIndex == 0) {
                                coroutineScope.launch {
                                    scroller.requestKeepPosition()
                                }
                            }
                        } else {
                            state.draggedSourceIndex = null
                        }
                    } else {
                        state.draggedSourceIndex = null
                    }
                    return true
                }

                override fun onEnded(event: DragAndDropEvent) {
                    scroller.stop()
                    state.draggedSourceIndex = null
                }

                override fun onExited(event: DragAndDropEvent) {
                    scroller.stop()
                    state.draggedSourceIndex = null
                }
            }
        }
    )
}

/**
 * Modifier áp dụng cho từng Item trong Grid (vùng kích hoạt Drag)
 */
@OptIn(ExperimentalFoundationApi::class)
fun Modifier.dragSourceItem(
    index: Int,
    state: DragAndDropState<*>,
    graphicsLayer: GraphicsLayer
): Modifier = this
    .alpha(if (state.draggedSourceIndex == index) 0f else 1f)
    .drawWithContent {
        if (state.draggedSourceIndex != index) {
            graphicsLayer.record {
                this@drawWithContent.drawContent()
            }
        }
        drawLayer(graphicsLayer)
    }
    .dragAndDropSource(
        drawDragDecoration = {
            drawLayer(graphicsLayer)
        },
        transferData = {
            val clipData = ClipData.newPlainText("text_data", index.toString())
            DragAndDropTransferData(
                clipData = clipData,
                localState = DraggedItemState(sourceIndex = index),
                flags = 0
            )
        }
    )
