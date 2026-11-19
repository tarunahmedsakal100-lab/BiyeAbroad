package com.example.ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BurgundyDeep
import com.example.ui.theme.BurgundyPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.theme.PureWhite
import kotlinx.coroutines.launch

@Composable
fun ScrollUpDownFloatingControls(
    scrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = BurgundyDeep.copy(alpha = 0.94f),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldAccent),
        shadowElevation = 8.dp,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
        ) {
            // Scroll Up
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        val current = scrollState.value
                        val target = (current - 700).coerceAtLeast(0)
                        scrollState.animateScrollTo(target)
                    }
                },
                modifier = Modifier.size(42.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Scroll Up",
                    tint = GoldLight,
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = "Scroll",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = PureWhite
            )

            // Scroll Down
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        val current = scrollState.value
                        val target = (current + 700).coerceAtMost(scrollState.maxValue)
                        scrollState.animateScrollTo(target)
                    }
                },
                modifier = Modifier.size(42.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Scroll Down",
                    tint = GoldLight,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun ScrollTouchBar(
    scrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    androidx.compose.foundation.layout.Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            onClick = {
                coroutineScope.launch {
                    val target = (scrollState.value - 600).coerceAtLeast(0)
                    scrollState.animateScrollTo(target)
                }
            },
            shape = RoundedCornerShape(16.dp),
            color = BurgundyPrimary.copy(alpha = 0.08f),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent),
            modifier = Modifier.weight(1f)
        ) {
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, tint = BurgundyPrimary, modifier = Modifier.size(18.dp))
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(2.dp))
                Text("⬆ Scroll Up", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BurgundyDeep)
            }
        }

        Surface(
            onClick = {
                coroutineScope.launch {
                    val target = (scrollState.value + 600).coerceAtMost(scrollState.maxValue)
                    scrollState.animateScrollTo(target)
                }
            },
            shape = RoundedCornerShape(16.dp),
            color = BurgundyPrimary.copy(alpha = 0.08f),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent),
            modifier = Modifier.weight(1f)
        ) {
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = BurgundyPrimary, modifier = Modifier.size(18.dp))
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(2.dp))
                Text("⬇ Scroll Down", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BurgundyDeep)
            }
        }
    }
}

@Composable
fun LazyScrollUpDownFloatingControls(
    lazyListState: LazyListState,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = BurgundyDeep.copy(alpha = 0.94f),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldAccent),
        shadowElevation = 8.dp,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
        ) {
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        val current = lazyListState.firstVisibleItemIndex
                        val target = (current - 2).coerceAtLeast(0)
                        lazyListState.animateScrollToItem(target)
                    }
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Scroll Up",
                    tint = GoldLight,
                    modifier = Modifier.size(26.dp)
                )
            }

            Text(
                text = "Scroll",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = PureWhite
            )

            IconButton(
                onClick = {
                    coroutineScope.launch {
                        val current = lazyListState.firstVisibleItemIndex
                        val target = current + 2
                        lazyListState.animateScrollToItem(target)
                    }
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Scroll Down",
                    tint = GoldLight,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}
