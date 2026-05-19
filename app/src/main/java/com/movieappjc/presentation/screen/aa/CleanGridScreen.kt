package com.movieappjc.presentation.screen.aa
//
//import androidx.compose.animation.core.FastOutSlowInEasing
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.safeDrawingPadding
//import androidx.compose.foundation.lazy.grid.GridCells
//import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
//import androidx.compose.foundation.lazy.grid.itemsIndexed
//import androidx.compose.foundation.lazy.grid.rememberLazyGridState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateListOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.alpha
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.movieappjc.presentation.screen.drop_drag.pointer_input.DragDropContext
//import com.movieappjc.presentation.screen.drop_drag.pointer_input.DragDropPolicy
//import com.movieappjc.presentation.screen.drop_drag.pointer_input.DragShadow
//import com.movieappjc.presentation.screen.drop_drag.pointer_input.dragDropSourceContainer
//import com.movieappjc.presentation.screen.drop_drag.pointer_input.rememberGridDragDropState
//import com.movieappjc.presentation.screen.drop_drag.pointer_input.rememberListDragDropState
//import kotlinx.coroutines.delay
//
//// 1. Tạo Model dữ liệu mẫu mô phỏng dữ liệu từ API
//data class GridItemModel(val id: String, val title: String)
//
//@Composable
//fun CleanGridScreen() {
//    val lazyGridState = rememberLazyGridState()
//    val scope = rememberCoroutineScope()
//
//    // 2. State mô phỏng nhận kết quả từ API (Bắt đầu là danh sách rỗng)
//    var apiResponseItems by remember { mutableStateOf<List<GridItemModel>>(emptyList()) }
//    var isLoading by remember { mutableStateOf(true) }
//
//    // 3. GIẢ LẬP GỌI API: Đợi 2 giây để tải dữ liệu từ Server về
//    LaunchedEffect(Unit) {
//        delay(2000) // Giả lập nghẽn mạng 2 giây
//        apiResponseItems = (1..45).map {
//            GridItemModel(id = "id_$it", title = "Ô số $it")
//        }
//        isLoading = false
//    }
//
//    // 4. Mảng tĩnh dành riêng cho bộ điều khiển Kéo thả (Bắt đầu bằng rỗng)
//    val listData = remember { mutableStateListOf<GridItemModel>() }
//
//    // 5. ĐỒNG BỘ DỮ LIỆU: Khi biến apiResponseItems nhận được cục data từ API, nạp ngay vào bộ kéo thả
//    LaunchedEffect(apiResponseItems) {
//        if (apiResponseItems.isNotEmpty()) {
//            listData.clear()
//            listData.addAll(apiResponseItems)
//        }
//    }
//
//    val dragDroPolicy = remember {
//        object : DragDropPolicy<GridItemModel> {
//            override fun isItemFixed(
//                item: GridItemModel,
//                context: DragDropContext
//            ): Boolean {
//                return false
//            }
//
//            override fun shouldRestrictSwapUntilDrop(
//                item: GridItemModel,
//                context: DragDropContext
//            ): Boolean {
//                return false
//            }
//        }
//    }
//
//    // 6. Khởi tạo State kéo thả List (Column) dùng chung
//    val dragDropState = rememberGridDragDropState(
//        listData = listData,
//        lazyGridState = lazyGridState,
//        scope = scope,
//        dragDropPolicy = dragDroPolicy,
//        getDragDropContext = { DragDropContext(isEditMode = true) },
//        onListChanged = { updatedList ->
//            println("Đã lưu thứ tự Column mới thành công!")
//        },
//        onDropEnd = { _, _ ->
//
//        }
//    )
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFF1F5F9)) // Màu nền xám nhạt hiện đại
//            .safeDrawingPadding()
//            // Tách cử chỉ dùng chung cực kỳ ngắn gọn
//            .dragDropSourceContainer(dragDropState)
//    ) {
//        if (isLoading) {
//            // Hiển thị vòng xoay Loading trong lúc chờ "API" chạy
//            CircularProgressIndicator(
//                modifier = Modifier.align(Alignment.Center),
//                color = Color(0xFF3B82F6)
//            )
//        } else {
//            // Hiển thị Grid danh sách sau khi đã load xong dữ liệu
//            LazyVerticalGrid(
//                columns = GridCells.Fixed(3), // Chia lưới thành 3 cột đều nhau
//                state = lazyGridState,
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(4.dp)
//            ) {
//                itemsIndexed(
//                    items = listData,
//                    key = { _, item -> item.id } // Bắt buộc phải gắn key theo ID độc nhất để animation không lỗi
//                ) { index, item ->
//                    val isDraggingThisItem = index == dragDropState.draggedIndex
//
//                    // Sử dụng component vẽ giao diện của ô Grid
//                    GridItemCard(
//                        item = item,
//                        modifier = Modifier
//                            // Hoạt họa di chuyển dạt ô mượt mà
//                            .animateItem(
//                                placementSpec = tween(
//                                    durationMillis = 400,
//                                    easing = FastOutSlowInEasing
//                                )
//                            )
//                            // Ẩn ô gốc đi (để lộ khoảng trống) nếu ô này đang được nhấc lên kéo đi
//                            .alpha(if (isDraggingThisItem) 0f else 1f)
//                    )
//                }
//            }
//        }
//
//        // 7. Gọi Composable Vẽ bóng ma dùng chung bám theo ngón tay
//        // Chiều rộng và chiều cao truyền vào đây nên trùng với kích thước của ô GridItemCard bên dưới
//        DragShadow(
//            dragDropState = dragDropState,
//            listData = listData
//        ) { shadowItem ->
//            // Định nghĩa ruột bên trong của bóng ma (Vẽ y hệt giao diện ô gốc)
//            GridItemCard(item = shadowItem)
//        }
//    }
//}
//
//// ================= CÁC COMPONENT GIAO DIỆN CON (VẼ CARD TRONG GRID) =================
//
//@Composable
//fun GridItemCard(
//    item: GridItemModel,
//    modifier: Modifier = Modifier
//) {
//    Card(
//        shape = RoundedCornerShape(12.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.White),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
//        modifier = modifier
//            .fillMaxWidth()
//            .padding(6.dp)
//            .height(100.dp) // Cố định chiều cao 100.dp
//    ) {
//        GridCardContent(item = item)
//    }
//}
//
//@Composable
//fun GridCardContent(item: GridItemModel) {
//    Box(
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        Text(
//            text = item.title,
//            fontSize = 14.sp,
//            fontWeight = FontWeight.SemiBold,
//            color = Color(0xFF334155)
//        )
//    }
//}