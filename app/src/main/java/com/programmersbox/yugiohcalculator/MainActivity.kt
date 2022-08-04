package com.programmersbox.yugiohcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.programmersbox.yugiohcalculator.ui.theme.Alizarin
import com.programmersbox.yugiohcalculator.ui.theme.Emerald
import com.programmersbox.yugiohcalculator.ui.theme.Sunflower
import com.programmersbox.yugiohcalculator.ui.theme.YugiohCalculatorTheme
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlin.random.nextInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YugiohCalculatorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) { YugiohView() }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YugiohView(vm: YugiohViewModel = viewModel()) {
    DiceDialog(
        visible = vm.showDiceDialog,
        onDismissRequest = { vm.showDiceDialog = false }
    )
    CoinFlipDialog(
        visible = vm.showCoinFlipDialog,
        onDismissRequest = { vm.showCoinFlipDialog = false }
    )
    ChangeLPDialog(
        visible = vm.showLPChangeDialog,
        onDismissRequest = { vm.showLPChangeDialog = false },
        vm = vm
    )
    ResetLPDialog(
        visible = vm.showResetLPDialog,
        onDismissRequest = { vm.showResetLPDialog = false },
        vm = vm
    )
    ResetLogsDialog(
        visible = vm.showResetLogDialog,
        onDismissRequest = { vm.showResetLogDialog = false },
        vm = vm
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Yu-Gi-Oh Calculator") }
            )
        }
    ) { p ->
        Column(
            modifier = Modifier.padding(p),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {

                OutlinedCard(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .weight(1f),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = SolidColor(animateColorAsState(targetValue = vm.playerOne.lpColor).value)
                    )
                ) {
                    ListItem(
                        overlineText = { Text("Player 1") },
                        headlineText = { Text(animateIntAsState(targetValue = vm.playerOne).value.toString()) }
                    )
                }

                OutlinedCard(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .weight(1f),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = SolidColor(animateColorAsState(targetValue = vm.playerTwo.lpColor).value)
                    )
                ) {
                    ListItem(
                        overlineText = { Text("Player 2") },
                        headlineText = { Text(animateIntAsState(targetValue = vm.playerTwo).value.toString()) }
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = { vm.showDiceDialog = true }
                ) { Text("Roll Dice") }
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = { vm.showCoinFlipDialog = true }
                ) { Text("Flip Coin") }
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = { vm.showLPChangeDialog = true }
                ) { Text("Change LP") }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    onClick = { vm.showResetLPDialog = true }
                ) { Text("Reset LP") }
                OutlinedButton(
                    onClick = { vm.showResetLogDialog = true }
                ) { Text("Reset Logs") }
            }
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                itemsIndexed(vm.logs) { index, it ->
                    ElevatedCard {
                        ListItem(
                            overlineText = { Text("${index + 1}.") },
                            headlineText = { Text(it) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ResetLPDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    vm: YugiohViewModel
) {
    if (visible) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text("Reset LP?") },
            properties = DialogProperties(usePlatformDefaultWidth = false),
            text = { Text("Are you sure you want to reset LP?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDismissRequest()
                        vm.resetLP()
                    }
                ) { Text("Yes") }
            },
            dismissButton = { TextButton(onClick = onDismissRequest) { Text("No") } }
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ResetLogsDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    vm: YugiohViewModel
) {
    if (visible) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text("Reset Logs?") },
            properties = DialogProperties(usePlatformDefaultWidth = false),
            text = { Text("Are you sure you want to reset logs?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDismissRequest()
                        vm.resetLogs()
                    }
                ) { Text("Yes") }
            },
            dismissButton = { TextButton(onClick = onDismissRequest) { Text("No") } }
        )
    }
}

class DiceRollerViewModel : ViewModel() {

    val diceList = mutableStateListOf<Dice>()

    fun addDice() {
        diceList.add(Dice(Random.nextInt(1..6), ""))
    }

    fun removeDice() {
        diceList.removeLastOrNull()
    }

}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChangeLPDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    vm: YugiohViewModel,
) {
    if (visible) {
        AlertDialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = onDismissRequest,
            confirmButton = { TextButton(onClick = onDismissRequest) { Text("Done") } },
            title = { Text("Change LP") },
            text = {
                var amount by remember { mutableStateOf("") }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {

                        OutlinedCard(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .weight(1f),
                            onClick = { vm.lpChangePlayerSelected = Players.PlayerOne },
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = animateColorAsState(if (vm.lpChangePlayerSelected == Players.PlayerOne) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface).value,
                                contentColor = animateColorAsState(if (vm.lpChangePlayerSelected == Players.PlayerOne) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary).value
                            ),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = SolidColor(animateColorAsState(vm.playerOne.lpColor).value)
                            )
                        ) {
                            ListItem(
                                overlineText = { Text("Player 1") },
                                headlineText = { Text(animateIntAsState(vm.playerOne).value.toString()) }
                            )
                        }

                        OutlinedCard(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .weight(1f),
                            onClick = { vm.lpChangePlayerSelected = Players.PlayerTwo },
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = animateColorAsState(if (vm.lpChangePlayerSelected == Players.PlayerTwo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface).value,
                                contentColor = animateColorAsState(if (vm.lpChangePlayerSelected == Players.PlayerTwo) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary).value
                            ),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = SolidColor(animateColorAsState(vm.playerTwo.lpColor).value)
                            )
                        ) {
                            ListItem(
                                overlineText = { Text("Player 2") },
                                headlineText = { Text(animateIntAsState(vm.playerTwo).value.toString()) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = amount,
                        onValueChange = {
                            amount = it
                                .replace(",", "")
                                .replace(" ", "")
                                .replace(".", "")
                                .replace("-", "")
                        },
                        singleLine = true,
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { amount.toIntOrNull()?.let(vm::changeLP) }
                        ),
                        leadingIcon = {
                            IconToggleButton(
                                checked = vm.lpAddOrSubtract == AddOrSubtract.Add,
                                onCheckedChange = {
                                    vm.lpAddOrSubtract =
                                        if (it) AddOrSubtract.Add else AddOrSubtract.Subtract
                                }
                            ) {
                                Crossfade(targetState = vm.lpAddOrSubtract) { target ->
                                    when (target) {
                                        AddOrSubtract.Add -> Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null
                                        )
                                        AddOrSubtract.Subtract -> Icon(
                                            imageVector = Icons.Default.Remove,
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        }
                    )

                    Button(
                        onClick = { amount.toIntOrNull()?.let(vm::changeLP) }
                    ) { Text("Change LP!") }
                }
            }
        )
    }
}

enum class AddOrSubtract { Add, Subtract }
enum class Players { PlayerOne, PlayerTwo }

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

class Coin(value: Boolean = Random.nextBoolean(), val location: String) {
    var value by mutableStateOf(value)

    suspend fun flip(flipCount: Int = 5) {
        repeat(flipCount) {
            delay(50L)
            value = Random.nextBoolean()
        }
    }
}

class CoinFlipViewModel : ViewModel() {

    val coinList = mutableStateListOf<Coin>()

    fun addCoin() {
        coinList.add(Coin(Random.nextBoolean(), ""))
    }

    fun removeCoin() {
        coinList.removeLastOrNull()
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

val Int.lpColor
    get() = when {
        this >= 6000 -> Emerald
        this in 3000..6000 -> Sunflower
        else -> Alizarin
    }

class YugiohViewModel : ViewModel() {

    var playerOne by mutableStateOf(8000)
    var playerTwo by mutableStateOf(8000)

    var showResetLPDialog by mutableStateOf(false)
    var showResetLogDialog by mutableStateOf(false)

    var showDiceDialog by mutableStateOf(false)
    var showCoinFlipDialog by mutableStateOf(false)
    var showLPChangeDialog by mutableStateOf(false)

    var lpChangePlayerSelected by mutableStateOf(Players.PlayerOne)
    var lpAddOrSubtract by mutableStateOf(AddOrSubtract.Subtract)

    val logs = mutableStateListOf("Player 1: $playerOne - Player 2: $playerTwo")

    fun resetLogs() {
        logs.clear()
        logs.add("Player 1: $playerOne - Player 2: $playerTwo")
    }

    fun resetLP() {
        playerOne = 8000
        playerTwo = 8000
        logs.add("Player 1: $playerOne - Player 2: $playerTwo")
    }

    private fun changePlayerOneLP(value: Int) {
        val type = if (value < 0) "-" else "+"
        logs.add("Player 1: $playerOne $type ${value.absoluteValue} = ${playerOne + value}")
        playerOne += value
    }

    private fun changePlayerTwoLP(value: Int) {
        val type = if (value < 0) "-" else "+"
        logs.add("Player 2: $playerTwo $type ${value.absoluteValue} = ${playerTwo + value}")
        playerTwo += value
    }

    fun changeLP(value: Int) {
        val action = when (lpChangePlayerSelected) {
            Players.PlayerOne -> ::changePlayerOneLP
            Players.PlayerTwo -> ::changePlayerTwoLP
        }

        when (lpAddOrSubtract) {
            AddOrSubtract.Add -> action(value)
            AddOrSubtract.Subtract -> action(-value)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    YugiohCalculatorTheme {
        YugiohView()
    }
}