package com.programmersbox.yugiohcalculator

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import kotlin.random.nextInt

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DiceDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    vm: DiceRollerViewModel = viewModel()
) {
    if (visible) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text("Roll Dice") },
            properties = DialogProperties(usePlatformDefaultWidth = false),
            text = {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { vm.removeDice() },
                            modifier = Modifier.weight(1f)
                        ) { Icon(Icons.Default.RemoveCircle, null) }
                        Text(
                            vm.diceList.size.toString(),
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        IconButton(
                            onClick = { vm.addDice() },
                            modifier = Modifier.weight(1f)
                        ) { Icon(Icons.Default.AddCircle, null) }
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(56.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        items(vm.diceList) { dice ->
                            var count by remember { mutableStateOf(0) }
                            LaunchedEffect(count) { dice.roll() }
                            Dice(dice) { count++ }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = onDismissRequest) { Text("Done") } }
        )
    }
}

class Dice(value: Int = Random.nextInt(1..6), val location: String) {
    var value by mutableStateOf(value)

    suspend fun roll(rollCount: Int = 5) {
        repeat(rollCount) {
            delay(50L)
            value = Random.nextInt(1..6)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dice(dice: Dice, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(7.dp),
        interactionSource = remember { MutableInteractionSource() },
        elevation = CardDefaults.cardElevation(3.dp),
        enabled = dice.value != 0,
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
                text = if (dice.value == 0) "" else dice.value.toString(),
                textAlign = TextAlign.Center
            )
        }
    }
}