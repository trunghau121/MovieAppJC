package com.movieappjc.presentation.screen.menu

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.movieappjc.presentation.screen.drop_drag.pointer_input.DragShadow
import com.movieappjc.presentation.screen.drop_drag.pointer_input.dragDropItemModifier
import com.movieappjc.presentation.screen.drop_drag.pointer_input.dragDropSourceContainer
import com.movieappjc.presentation.screen.drop_drag.pointer_input.rememberGridDragDropState
import com.movieappjc.presentation.screen.drop_drag.pointer_input.swap
import com.movieappjc.presentation.screen.menu.compoment.DashedSlotMenu
import com.movieappjc.presentation.screen.menu.compoment.MyMenuItemCard
import com.movieappjc.presentation.screen.menu.compoment.MyMenuPlaceHolder
import com.movieappjc.presentation.screen.menu.reorder.MenuDragDropPolicy
import com.movieappjc.presentation.screen.menu.utils.shaking
import kotlinx.coroutines.delay

@Composable
fun MyMenuScreen(viewModel: MyMenuViewModel = hiltViewModel()) {
    var isInitialShaking by remember { mutableStateOf(true) }
    val lazyGridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    val dragDroPolicy = remember { MenuDragDropPolicy() }
    val myMenuList = remember { viewModel.myMenuList }
    val ignoreIndices = remember { viewModel.ignoreIndices }

    LaunchedEffect(Unit) {
        viewModel.loadMyMenuList()
        delay(1000)
        isInitialShaking = false
    }

    val dragDropState = rememberGridDragDropState(
        lazyGridState = lazyGridState,
        scope = scope,
        dragDropPolicy = dragDroPolicy,
        ignoreIndices = ignoreIndices,
        getItemAt = { myMenuList.getOrNull(it) },
        performSwap = viewModel::swapMenuItem,
        onDropEnd = { from, to ->
            if (from < 0 || to < 0) {
                return@rememberGridDragDropState
            }
            val itemTo = myMenuList[to]
            val itemFrom = myMenuList[from]

            when (itemTo) {
                is EmptyMenuItem -> {
                    myMenuList.apply {
                        set(to, itemFrom.copy(isHome = itemTo.isHome))
                        if (viewModel.isFromMenuHome(from))
                            set(from, EmptyMenuItem())
                        else {
                            myMenuList.removeAt(from)
                        }
                    }
                }
                else -> {
                    myMenuList.swap(from, to)
                }
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .safeDrawingPadding()
    ) {
        Text(
            "Set up My Menu",
            fontSize = 24.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .dragDropSourceContainer(dragDropState)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                state = lazyGridState,
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = dragDropState.draggedIndex == null,
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            ) {
                itemsIndexed(
                    myMenuList,
                    key = { index, item -> item.id },
                    span = { index, item ->
                        val span = if (item is MyMenuPlaceHolder) maxLineSpan else 1
                        GridItemSpan(span)
                    }
                ) { index, item ->
                    when (item) {
                        is EmptyMenuItem -> {
                            DashedSlotMenu()
                        }

                        is MyMenuPlaceHolder -> {
                            MyMenuPlaceHolder(item)
                        }

                        else -> {
                            MyMenuItemCard(
                                item = item,
                                modifier = Modifier
                                    .shaking(enabled = isInitialShaking && !item.isFixed)
                                    .animateItem(
                                        placementSpec = tween(
                                            durationMillis = 150,
                                            easing = FastOutSlowInEasing
                                        )
                                    )
                                    .dragDropItemModifier(index, item, dragDropState),
                                onRemoveOrAdd = {
                                    viewModel.onRemoveOrAdd(index)
                                }
                            )
                        }
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                DragShadow(
                    dragDropState = dragDropState,
                    getItemAt = { index -> myMenuList[index] }
                ) { item ->
                    MyMenuItemCard(modifier = Modifier, item = item) {}
                }
            }
        }

        // Bottom Buttons
        Row(
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {},
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8F0FE))
            ) {
                Text("Reset", color = Color(0xFF1A56A0))
            }
            Button(
                onClick = {},
                modifier = Modifier.weight(2f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0056D2))
            ) {
                Text("Save")
            }
        }
    }
}
