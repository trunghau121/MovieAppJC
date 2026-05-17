package com.movieappjc.presentation.screen.aa

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movieappjc.presentation.screen.drop_drag.pointer_input.DragShadow
import com.movieappjc.presentation.screen.drop_drag.pointer_input.dragDropSourceContainer
import com.movieappjc.presentation.screen.drop_drag.pointer_input.rememberListDragDropState
import kotlinx.coroutines.delay

// 1. Tạo Model dữ liệu mẫu mô phỏng dữ liệu từ API dạng dòng
data class ColumnItemModel(val id: String, val name: String, val email: String, val isLocked: Boolean)

@Composable
fun CleanColumnScreen() {
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // 2. State mô phỏng nhận kết quả từ API (Bắt đầu là danh sách rỗng)
    var apiResponseItems by remember { mutableStateOf<List<ColumnItemModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // 3. GIẢ LẬP GỌI API: Đợi 2 giây để tải dữ liệu từ Server về
    LaunchedEffect(Unit) {
        delay(2000) // Giả lập mạng chậm 2 giây
        apiResponseItems = (1..30).map {
            ColumnItemModel(
                id = "user_id_$it",
                name = "Nguyễn Văn Người Dùng $it",
                email = "user$it@gmail.com",
                isLocked = it < 5
            )
        }
        isLoading = false
    }

    // 4. Mảng tĩnh dành riêng cho bộ điều khiển Kéo thả (Bắt đầu bằng rỗng)
    val listData = remember { mutableStateListOf<ColumnItemModel>() }

    // 5. ĐỒNG BỘ DỮ LIỆU: API trả về lúc nào, đổ vào mảng kéo thả lúc đó
    LaunchedEffect(apiResponseItems) {
        if (apiResponseItems.isNotEmpty()) {
            listData.clear()
            listData.addAll(apiResponseItems)
        }
    }

    // 6. Khởi tạo State kéo thả List (Column) dùng chung
    val dragDropState = rememberListDragDropState(
        listData = listData,
        lazyListState = lazyListState,
        scope = scope,
        onListChanged = { updatedList ->
            // Khi người dùng thả tay đổi chỗ thành công, đồng bộ ngay với biến API
            apiResponseItems = updatedList
            println("Đã lưu thứ tự Column mới cục bộ thành công!")
        },
        // 1. Cho phép TẤT CẢ mọi item đều có quyền tự long press để kéo đi bình thường
        canDragItem = { true },

        // 2. Chỉ định luật khi bị lướt qua:
        canTargetAcceptSwap = { item ->
            // Nếu item đang nằm dưới ngón tay là ô bị khóa (!item.isLocked == false)
            // -> Từ chối hoán đổi, bắt ô đó phải đứng im cố định tại chỗ.
            !item.isLocked
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)) // Màu nền nền Slate nhẹ nhã nhặn
            .safeDrawingPadding()
            // Áp dụng Custom Modifier gom cử chỉ Long Press dùng chung của bạn
            .dragDropSourceContainer(dragDropState)
    ) {
        if (isLoading) {
            // Hiển thị vòng xoay Loading trong lúc chờ API giả lập
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color(0xFF10B981) // Màu xanh lục
            )
        } else {
            // Hiển thị LazyColumn sau khi đã có dữ liệu nạp vào listData
            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = dragDropState.draggedIndex == null,
                verticalArrangement = Arrangement.spacedBy(2.dp) // Khoảng cách giữa các dòng
            ) {
                itemsIndexed(
                    items = listData,
                    key = { _, item -> item.id } // Bắt buộc phải gắn ID độc nhất để hiệu ứng trượt hoạt động chính xác
                ) { index, item ->
                    val isDraggingThisItem = index == dragDropState.draggedIndex

                    ColumnItemRow(
                        item = item,
                        modifier = Modifier
                            // Hoạt họa hoán đổi vị trí mượt mà
                            .animateItem(
                                placementSpec = tween(
                                    durationMillis = 400,
                                    easing = FastOutSlowInEasing
                                )
                            )
                            // Ẩn dòng gốc đi (để lại khoảng trống) nếu dòng này đang được nhấc đi
                            .alpha(if (isDraggingThisItem) 0f else 1f)
                    )
                }
            }
        }

        // 7. Gọi Composable Vẽ bóng ma dùng chung bám theo ngón tay
        // Đối với Row của Column, ta cho chiều rộng chiếm khoảng 92% màn hình để giống ô gốc
        Box(
            modifier = Modifier.fillMaxSize() // Box con thứ 2 chuyên chứa bóng ma (không chứa padding)
        ) {
            DragShadow(dragDropState = dragDropState) { shadowItem ->
                // Ruột vẽ y hệt ô gốc
                ColumnItemRow(item = shadowItem)
            }
        }
    }
}

// ================= CÁC COMPONENT GIAO DIỆN CON (VẼ DÒNG TRONG COLUMN) =================

@Composable
fun ColumnItemRow(
    item: ColumnItemModel,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .height(76.dp) // Cố định chiều cao dòng 76.dp
    ) {
        ColumnRowContent(item = item)
    }
}

@Composable
fun ColumnRowContent(item: ColumnItemModel) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Vẽ Avatar hình tròn giả lập đầu dòng
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFFE2E8F0), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = item.name.firstOrNull()?.toString() ?: "",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Vẽ Khối text thông tin ở giữa
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.email,
                fontSize = 13.sp,
                color = Color(0xFF64748B)
            )
        }

        // Icon Hamburger Handle báo hiệu cho người dùng biết ô này có thể cầm kéo được
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Drag Handle",
            tint = Color(0xFFCBD5E1),
            modifier = Modifier.size(22.dp)
        )
    }
}