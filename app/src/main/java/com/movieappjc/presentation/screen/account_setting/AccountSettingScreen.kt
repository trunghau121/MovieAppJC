package com.movieappjc.presentation.screen.account_setting

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.movieappjc.presentation.screen.account_setting.componemt.AccountEmptyDottedBox
import com.movieappjc.presentation.screen.account_setting.componemt.AccountRowCard
import com.movieappjc.presentation.screen.account_setting.componemt.AccountSectionHeader
import com.movieappjc.presentation.screen.account_setting.componemt.AccountSettingHeader
import com.movieappjc.presentation.screen.account_setting.componemt.PlaceHolderRow
import com.movieappjc.presentation.screen.account_setting.data.AccountItem
import com.movieappjc.presentation.screen.account_setting.data.AccountItemEmpty
import com.movieappjc.presentation.screen.account_setting.data.Header
import com.movieappjc.presentation.screen.account_setting.data.PlaceHolder
import com.movieappjc.presentation.screen.account_setting.data.Title
import com.movieappjc.presentation.screen.account_setting.reorder.AccountDragDropPolicy
import com.movieappjc.presentation.screen.drop_drag.pointer_input.DragDropContext
import com.movieappjc.presentation.screen.drop_drag.pointer_input.DragShadow
import com.movieappjc.presentation.screen.drop_drag.pointer_input.dragDropItemModifier
import com.movieappjc.presentation.screen.drop_drag.pointer_input.dragDropSourceContainer
import com.movieappjc.presentation.screen.drop_drag.pointer_input.rememberListDragDropState
import com.movieappjc.presentation.screen.drop_drag.pointer_input.swap


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettingScreen(viewModel: AccountSettingViewModel = hiltViewModel()) {
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val dragDroPolicy = remember { AccountDragDropPolicy() }

    val accounts = remember { mutableStateListOf<AccountItem>() }


    LaunchedEffect(Unit) {
        accounts.addAll(
            listOf(
                Header(),
                Title("Default Account"),
                AccountItemEmpty(isAccountDefault = true),
                Title("Accounts on Home"),
                AccountItemEmpty(),
                AccountItemEmpty(),
                PlaceHolder(),
                Title("Accounts"),
                AccountItem(name = "Account name 1", number = "700-001-11111"),
                AccountItem(name = "Account name 2", number = "700-001-22222"),
                AccountItem(name = "Account name 3", number = "700-001-33333"),
                AccountItem(name = "Account name 4", number = "700-001-44444"),
                AccountItem(name = "Account name 5", number = "700-001-55555"),
                AccountItem(name = "Account name 6", number = "700-001-66666"),
                AccountItem(name = "Account name 7", number = "700-001-77777"),
                AccountItem(name = "Account name 8", number = "700-001-88888"),
                AccountItem(name = "Account name 9", number = "700-001-99999"),
                AccountItem(name = "Account name 10", number = "700-001-10101")
            )
        )
        viewModel.loadDataAccounts()
    }

    val dragDropState = rememberListDragDropState(
        lazyListState = lazyListState,
        scope = scope,
        dragDropPolicy = dragDroPolicy,
        getDragDropContext = { DragDropContext(isEditMode = true) },
        getItemAt = accounts::getOrNull,
        performSwap = accounts::swap,
        onDropEnd = { from, to ->
            val temp = accounts[from]
            accounts[from] = accounts[to]
            accounts[to] = temp
        }
    )
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
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = false
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .dragDropSourceContainer(dragDropState)
        ) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize(),
                userScrollEnabled = dragDropState.draggedIndex == null && !dragDropState.isReturningAnimation,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                itemsIndexed(
                    accounts,
                    key = { _, item -> item.id }
                ) { index, item ->
                    when (item) {
                        is Header -> {
                            AccountSettingHeader()
                        }

                        is Title -> {
                            AccountSectionHeader(item.text)
                        }

                        is PlaceHolder -> {
                            PlaceHolderRow()
                        }

                        is AccountItemEmpty -> {
                            AccountEmptyDottedBox(modifier = Modifier.height(80.dp))
                        }

                        else -> {
                            AccountRowCard(
                                modifier = Modifier
                                    .animateItem(
                                        placementSpec = tween(
                                            durationMillis = 150,
                                            easing = FastOutSlowInEasing
                                        )
                                    )
                                    .dragDropItemModifier(index, item, dragDropState),
                                item = item
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                DragShadow(
                    dragDropState = dragDropState,
                    getItemAt = { index -> accounts[index] }
                ) { item ->
                    AccountRowCard(modifier = Modifier, item = item)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAccountSetting() {
    AccountSettingScreen()
}