package com.movieappjc.presentation.screen.person

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.movieappjc.app.components.ToUI
import com.movieappjc.app.theme.fontCustomNormal
import com.movieappjc.presentation.viewmodel.person.PersonDetailViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PersonDetailScreen(viewModel: PersonDetailViewModel = hiltViewModel()) {

    val draggedDownAnchorTop = with(LocalDensity.current) { 250.dp.toPx() }
    val anchors = DraggableAnchors {
        AnchoredDraggableCardState.DRAGGED_DOWN at draggedDownAnchorTop
        AnchoredDraggableCardState.DRAGGED_UP at 0f
    }

    val density = LocalDensity.current
    val anchoredDraggableState = remember {
        AnchoredDraggableState(
            initialValue = AnchoredDraggableCardState.DRAGGED_DOWN,
            anchors = anchors,
            positionalThreshold = { distance: Float -> distance * 0.5f },
            velocityThreshold = { with(density) { 100.dp.toPx() } },
            animationSpec = tween(),
        )
    }

    val connection = getPersonNestedScrollConnection(anchoredDraggableState)

    val progress by remember(anchoredDraggableState) {
        derivedStateOf {
            val offset =
                if (anchoredDraggableState.offset.isNaN()) {
                    0f
                } else {
                    anchoredDraggableState.offset
                }
            (1 - (offset / draggedDownAnchorTop)).coerceIn(0f, 1f)
        }
    }

    val onBack = remember { { viewModel.onBack() } }
    val state by viewModel.person.collectAsStateWithLifecycle()
    state.ToUI(
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
            ) {
                ProfileHeader(
                    personEntity = it,
                    progress = { progress },
                    onBack = onBack
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 15.dp)
                        .anchoredDraggable(anchoredDraggableState, Orientation.Vertical)
                        .nestedScroll(connection),
                    text = it.getBiography(),
                    color = Color.LightGray,
                    style = MaterialTheme.typography.fontCustomNormal,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Start
                )
            }
        },
        onRetry = viewModel::getPersonDetail
    )
}