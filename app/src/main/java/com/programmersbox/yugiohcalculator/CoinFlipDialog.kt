package com.programmersbox.yugiohcalculator

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlin.random.Random

class Coin(value: Boolean = Random.nextBoolean(), val location: String) {
    var value by mutableStateOf(value)

    suspend fun flip(flipCount: Int = 5) {
        repeat(flipCount) {
            delay(50L)
            value = Random.nextBoolean()
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CoinFlipDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    vm: CoinFlipViewModel = viewModel()
) {
    if (visible) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text("Flip Coins") },
            properties = DialogProperties(usePlatformDefaultWidth = false),
            text = {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { vm.removeCoin() },
                            modifier = Modifier.weight(1f)
                        ) { Icon(Icons.Default.RemoveCircle, null) }
                        Text(
                            vm.coinList.size.toString(),
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        IconButton(
                            onClick = { vm.addCoin() },
                            modifier = Modifier.weight(1f)
                        ) { Icon(Icons.Default.AddCircle, null) }
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(56.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        items(vm.coinList) { coin ->
                            var count by remember { mutableStateOf(0) }
                            LaunchedEffect(count) { coin.flip() }
                            Coin(coin) { count++ }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = onDismissRequest) { Text("Done") } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Coin(coin: Coin, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = CircleShape,
        interactionSource = remember { MutableInteractionSource() },
        elevation = CardDefaults.cardElevation(3.dp),
        border = BorderStroke(1.dp, contentColorFor(MaterialTheme.colorScheme.surface)),
        modifier = Modifier
            .size(56.dp)
            .then(modifier),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = when (coin.value) {
                    true -> "Heads"
                    false -> "Tails"
                },
                textAlign = TextAlign.Center
            )
        }
    }
}