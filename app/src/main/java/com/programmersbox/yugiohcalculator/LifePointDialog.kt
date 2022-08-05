package com.programmersbox.yugiohcalculator

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.programmersbox.yugiohcalculator.ui.theme.Alizarin
import com.programmersbox.yugiohcalculator.ui.theme.Emerald
import com.programmersbox.yugiohcalculator.ui.theme.Sunflower


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
                            onDone = {
                                amount.toIntOrNull()?.let(vm::changeLP).also { amount = "" }
                            }
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
                        onClick = { amount.toIntOrNull()?.let(vm::changeLP)?.also { amount = "" } }
                    ) { Text("Change LP!") }
                }
            }
        )
    }
}

enum class AddOrSubtract { Add, Subtract }
enum class Players { PlayerOne, PlayerTwo }

val Int.lpColor
    get() = when {
        this >= 6000 -> Emerald
        this in 3000..6000 -> Sunflower
        else -> Alizarin
    }