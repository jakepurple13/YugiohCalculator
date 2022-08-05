package com.programmersbox.yugiohcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.programmersbox.yugiohcalculator.ui.theme.YugiohCalculatorTheme
import kotlinx.coroutines.launch

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

    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    BackHandler(drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    ModalNavigationDrawer(
        drawerContent = { CardCounterView() },
        drawerState = drawerState
    ) {
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

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    item {
                        OutlinedButton(
                            onClick = { vm.showDiceDialog = true }
                        ) { Text("Roll Dice") }
                    }
                    item {
                        OutlinedButton(
                            onClick = { vm.showCoinFlipDialog = true }
                        ) { Text("Flip Coin") }
                    }
                    item {
                        OutlinedButton(
                            onClick = { vm.showLPChangeDialog = true }
                        ) { Text("Change LP") }
                    }
                    item {
                        OutlinedButton(
                            onClick = { scope.launch { drawerState.open() } }
                        ) { Text("Card Counter") }
                    }
                    item {
                        OutlinedButton(
                            onClick = { vm.showResetLPDialog = true }
                        ) { Text("Reset LP") }
                    }
                    item {
                        OutlinedButton(
                            onClick = { vm.showResetLogDialog = true }
                        ) { Text("Reset Logs") }
                    }
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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    YugiohCalculatorTheme {
        YugiohView()
    }
}