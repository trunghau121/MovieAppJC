package com.movieappjc.presentation.screen.account_setting

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movieappjc.presentation.screen.menu.reorderable.ReorderableItem
import com.movieappjc.presentation.screen.menu.reorderable.rememberReorderableLazyListState
import java.util.UUID

// Định nghĩa dữ liệu mẫu cho Account
open class AccountItem(
    val id: Int = UUID.randomUUID().hashCode(),
    val name: String = "",
    val number: String = "",
    val isEmpty: Boolean = false
)

class Header : AccountItem()
class Title(val text: String) : AccountItem()
class PlaceHolder : AccountItem()
class AccountItemEmpty : AccountItem(isEmpty = true)



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettingScreen() {
    // Dữ liệu mẫu ban đầu dựa trên ảnh của bạn
    var listData by remember {
        mutableStateOf(
            listOf(
                Header(),
                Title("Default Account"),
                AccountItemEmpty(),
                Title("Accounts on Home"),
                AccountItemEmpty(),
                AccountItemEmpty(),
                PlaceHolder(),
                Title("Accounts"),
                AccountItem(name = "Account name 1", number = "700-001-123457"),
                AccountItem(name = "Account name 2", number = "700-001-123458"),
                AccountItem(name = "Account name 3", number = "700-001-123459"),
                AccountItem(name = "Account name 4", number = "700-001-123459"),
                AccountItem(name = "Account name 5", number = "700-001-123459"),
                AccountItem(name = "Account name 6", number = "700-001-123454")
            )
        )
    }

    val haptic = LocalHapticFeedback.current

    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->

        val fromAccountIndex = listData.indexOfFirst { it.id == from.key }
        val toAccountIndex = listData.indexOfFirst { it.id == to.key }
        val isFromAccountHome = fromAccountIndex == 4 || fromAccountIndex == 5
        val isToAccountHome = toAccountIndex == 4 || toAccountIndex == 5
        val isFromDefaultAccount = fromAccountIndex == 2
        val isToDefaultAccount = toAccountIndex == 2

        val isFromAccount = !isFromAccountHome && !isFromDefaultAccount
        val isToAccount = !isToAccountHome && !isToDefaultAccount


        Log.d("AAAAAAA", isFromAccountHome.toString())
        Log.d("AAAAAAA", isFromAccountHome.toString())

        if (fromAccountIndex != -1 && toAccountIndex != -1) {
            val item = listData[fromAccountIndex]
            val toItem = listData[toAccountIndex]
            if (isFromAccount && isToAccountHome) {
                if (toItem is AccountItemEmpty) {
                    listData = listData.toMutableList().apply {
                        removeAt(fromAccountIndex)
                    }
                    listData = listData.toMutableList().apply {
                        this[toAccountIndex] = item
                    }
                } else if (item is AccountItemEmpty) {
                    listData = listData.toMutableList().apply {
                        this[fromAccountIndex] = AccountItemEmpty()
                    }
                    listData = listData.toMutableList().apply {
                        this[toAccountIndex] = item
                    }
                } else if (listData[4] is AccountItemEmpty){
                    listData = listData.toMutableList().apply {
                        this[4] = toItem
                    }
                    listData = listData.toMutableList().apply {
                        removeAt(fromAccountIndex)
                    }

                    listData = listData.toMutableList().apply {
                        this[toAccountIndex] = item
                    }
                } else {
                    listData = listData.toMutableList().apply {
                        this[fromAccountIndex] = toItem
                    }
                    listData = listData.toMutableList().apply {
                        this[toAccountIndex] = item
                    }
                }
            } else if (!isFromAccount && isToAccountHome && !isFromAccountHome) {
                if (toItem is AccountItemEmpty) {
                    listData = listData.toMutableList().apply {
                        removeAt(fromAccountIndex)
                    }
                    listData = listData.toMutableList().apply {
                        this[toAccountIndex] = item
                    }
                } else {
                    listData = listData.toMutableList().apply {
                        this[fromAccountIndex] = AccountItemEmpty()
                    }
                    listData = listData.toMutableList().apply {
                        this[toAccountIndex] = item
                    }
                }
            } else if (isFromAccountHome && isToAccount) {
                listData = listData.toMutableList().apply {
                    this[fromAccountIndex] = AccountItemEmpty()
                }

                listData = listData.toMutableList().apply {
                    add(toAccountIndex, item)
                }

            } else if (isFromAccount && isToAccountHome) {
                listData = listData.toMutableList().apply {
                    this[fromAccountIndex] = AccountItemEmpty()
                }

                listData = listData.toMutableList().apply {
                    add(toAccountIndex, item)
                }

            } else {
                listData = listData.toMutableList().apply {
                    add(toAccountIndex, removeAt(fromAccountIndex))
                }
            }
        }

        haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Account setting",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* Handle Back */ }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            // Phần Nút bấm ở dưới cùng
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { /* Handle Reset */ },
                    modifier = Modifier
                        .weight(0.3f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE8F0FE),
                        contentColor = Color(0xFF1A73E8)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Reset", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { /* Handle Save */ },
                    modifier = Modifier
                        .weight(0.7f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE0E2E7),
                        contentColor = Color(0xFF9AA0A6)
                    ), // Trạng thái disable tạm thời như ảnh
                    shape = RoundedCornerShape(12.dp),
                    enabled = false // Đổi thành true khi có thay đổi dữ liệu
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(
                listData,
                key = { _, item -> item.id }
            ) { index, item ->
                when (item) {
                    is Header -> {
                        Column {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Set accounts for Home screen",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 36.sp,
                                color = Color(0xFF111827)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Accounts will be displayed on the Home Screen in the order you select. You can change the account order by selecting the Handle button on the right side of account",
                                fontSize = 14.sp,
                                color = Color(0xFF6B7280),
                                lineHeight = 20.sp
                            )
                        }
                    }

                    is Title -> {
                        SectionHeader(item.text)
                    }

                    is PlaceHolder -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                thickness = DividerDefaults.Thickness,
                                color = Color(0xFFE5E7EB)
                            )
                            Text(
                                text = "Accounts shown on home end here",
                                modifier = Modifier.padding(horizontal = 8.dp),
                                color = Color(0xFF9CA3AF),
                                fontSize = 12.sp
                            )
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                thickness = DividerDefaults.Thickness,
                                color = Color(0xFFE5E7EB)
                            )
                        }
                    }
                    is AccountItemEmpty -> {
                        ReorderableItem(reorderableLazyListState, key = item.id) {
                            EmptyDottedBox(modifier = Modifier.height(80.dp))
                        }
                    }
                    else -> {
                        ReorderableItem(
                            reorderableLazyListState,
                            key = item.id,
                        ) { isDragging ->
                            AccountRowCard(
                                account = item,
                                index = index,
                                haptic = haptic,
                                list = listData,
                                updateList = {
                                    listData = it
                                }
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1F2937),
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

// Vùng vẽ khung nét đứt (Dotted Box) đại diện cho các slot trống
@Composable
fun EmptyDottedBox(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color(0xFFE5E7EB),
                shape = RoundedCornerShape(12.dp) // Trong compose thực tế nếu muốn nét đứt chuẩn (dashed), bạn dùng Canvas để vẽ PathEffect.
            )
            .background(Color.Transparent)
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewAccountSetting() {
    AccountSettingScreen()
}