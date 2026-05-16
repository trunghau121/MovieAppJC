package com.movieappjc.presentation.screen.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.RemoveCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movieappjc.R
import com.movieappjc.presentation.screen.menu.reorderable.ReorderableItem
import com.movieappjc.presentation.screen.menu.reorderable.rememberReorderableLazyGridState
import com.movieappjc.presentation.screen.menu.utils.addItemToTop
import com.movieappjc.presentation.screen.menu.utils.handleItemReorder
import com.movieappjc.presentation.screen.menu.utils.removeItemFromTop
import com.movieappjc.presentation.screen.menu.utils.reorderableItemModifier
import com.movieappjc.presentation.screen.menu.utils.shaking
import kotlinx.coroutines.delay

@Composable
fun MyMenuScreen() {
    var isInitialShaking by remember { mutableStateOf(true) }
//    var isItemDragging by remember { mutableStateOf<MenuItem?>(null) }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(Unit) {
        delay(1000)
        isInitialShaking = false
    }

    val listData = remember {
        mutableStateOf(
            listOf(
                MenuItem(title = "Transfer", icon = R.drawable.icon_play, isFixed = true),
                MenuItem(title = "Payments", icon = R.drawable.icon_play, isFixed = true),
                Empty,
                Empty,
                Empty,
                Empty,
                Empty,
                Empty,
                MyMenuHeader("Out of home"),
                MenuItem(index = 9, title = "Inquiry", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 10,title = "Product Center", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 11,title = "Cards", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 12,title = "QRIS", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 13,title = "Personal Set", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 14,title = "Security", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 15,title = "Manage M-OTP", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 16,title = "Favorite Acc", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 17,title = "Inbox", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 18,title = "Help Center", icon = R.drawable.icon_play, isHome = false),

                MenuItem(index = 19, title = "Transfer FX", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 20,title = "Flazz", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 21,title = "Setting", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 22,title = "Account", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 23,title = "E-slip", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 24,title = "Contact", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 25,title = "Information", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 26,title = "Profile", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 27,title = "Chat", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 28,title = "About", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 29,title = "Information", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 30,title = "Profile", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 31,title = "Chat", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 32,title = "About", icon = R.drawable.icon_play, isHome = false)

            )
        )
    }

    val lazyGridState = rememberLazyGridState()

    val reorderableState = rememberReorderableLazyGridState(lazyGridState) { from, to ->
        listData.value = listData.value.handleItemReorder(from.index, to.index)
        haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
    }

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

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            state = lazyGridState,
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        ) {
            itemsIndexed(
                listData.value,
                key = { index, item -> item.id },
                span = { index, item ->
                    val span = if (item is MyMenuHeader) {
                        maxLineSpan
                    } else {
                        1
                    }
                    GridItemSpan(span)
                }
            ) { index, item ->
                if (item.isEmpty) {
                    ReorderableItem(reorderableState, key = item.id) {
                        DashedSlotUI()
                    }
                } else if (item is MyMenuHeader) {
                    Box(
                        modifier = Modifier.padding(vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0xFFEEEEEE))
                                .padding(vertical = 20.dp)
                        )
                        Text(
                            item.title,
                            color = Color.LightGray,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .background(Color.White)
                                .padding(vertical = 8.dp, horizontal = 7.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    ReorderableItem(
                        reorderableState,
                        key = item.id,
                        animateItemModifier = Modifier.animateItem(),
                        enabled = !item.isFixed
//                                &&
//                                (isItemDragging == null ||
//                                        isItemDragging?.isHome == true ||
//                                        isItemDragging?.id == item.id)
                    ) { isDragging ->
//                        if (isDragging) {
//                            isItemDragging = item
//                        }
                        MenuCardUI(
                            item = item,
                            isDragging = isDragging,
                            isShakingActive = isInitialShaking,
                            // Chỉ cho phép kéo nếu không phải Fixed
                            modifier = if (!item.isFixed && item.isCanDrag) {
                                reorderableItemModifier(listData, item, index, haptic)
                            } else Modifier,
                            onRemoveOrAdd = {
                                val currentItem = listData.value[index]
                                if (!currentItem.isHome) {
                                    listData.value = listData.value.addItemToTop(index)
                                } else {
                                    listData.value = listData.value.removeItemFromTop(index)
                                }
                            }
                        )
                    }
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

@Composable
fun MenuCardUI(
    item: MenuItem,
    isDragging: Boolean,
    isShakingActive: Boolean,
    modifier: Modifier = Modifier,
    onRemoveOrAdd: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Surface(
        onClick = {},
        modifier = modifier
            .aspectRatio(1f)
            .shaking(enabled = isShakingActive && !item.isFixed && !isDragging)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(15.dp),
                clip = false,
                ambientColor = DefaultShadowColor.copy(alpha = 0.4f),
                spotColor = DefaultShadowColor.copy(alpha = 0.4f)
            ),
        shape = RoundedCornerShape(15.dp),
        color = Color.White,
        interactionSource = interactionSource
    ) {
        Box(Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = 10.dp,
                        start = 10.dp,
                        end = 10.dp,
                        bottom = 32.dp
                    )
            ) {
                Text(
                    item.title,
                    fontSize = 13.sp,
                    lineHeight = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )

                if (!item.isFixed) {
                    IconButton(
                        onClick = onRemoveOrAdd,
                        modifier = Modifier
                            .size(22.dp)
                    ) {
                        Icon(
                            imageVector = if (item.isHome) Icons.Rounded.RemoveCircle else Icons.Rounded.AddCircle,
                            contentDescription = null,
                            tint = if (item.isHome) Color.Gray else Color(0xFF0056D2),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Image(
                painter = painterResource(item.icon),
                contentDescription = null,
                modifier = Modifier
                    .size(46.dp)
                    .padding(end = 10.dp, bottom = 10.dp)
                    .align(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
fun DashedSlotUI() {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(12.dp))
            .background(Color(0xFFFAFAFA), RoundedCornerShape(12.dp))
    )
}