package com.movieappjc.presentation.screen.account_setting

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movieappjc.presentation.screen.menu.reorderable.ReorderableCollectionItemScope

@Composable
fun ReorderableCollectionItemScope.AccountRowCard(
    index: Int,
    account: AccountItem,
    haptic: HapticFeedback,
    list: List<AccountItem>,
    updateList: (List<AccountItem>) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .semantics {
                customActions = listOf(
                    CustomAccessibilityAction(
                        label = "Move Up",
                        action = {
                            if (index > 0) {
                                updateList(list.toMutableList().apply {
                                    add(index - 1, removeAt(index))
                                })
                                true
                            } else {
                                false
                            }
                        }
                    ),
                    CustomAccessibilityAction(
                        label = "Move Down",
                        action = {
                            if (index < list.size - 1) {
                                updateList(list.toMutableList().apply {
                                    add(index + 1, removeAt(index))
                                })
                                true
                            } else {
                                false
                            }
                        }
                    ),
                )
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F6)),
        interactionSource = interactionSource,
        onClick = {}
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = account.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF111827)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = account.number,
                    fontSize = 14.sp,
                    color = Color(0xFF9CA3AF)
                )
            }
            IconButton(onClick = { /* Hành động kéo thả */ }) {
                Icon(
                    imageVector = Icons.Default.Menu, // Thay bằng icon Menu hamburger biểu thị Handle reorder
                    contentDescription = "Reorder handle",
                    tint = Color(0xFF6B7280),
                    modifier = Modifier
                        .longPressDraggableHandle(
                            onDragStarted = {
                                haptic.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                            },
                            onDragStopped = {
                                haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
                            }
                        )
                        .clearAndSetSemantics { },
                )
            }
        }
    }
}