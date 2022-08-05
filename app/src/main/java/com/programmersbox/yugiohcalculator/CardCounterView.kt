package com.programmersbox.yugiohcalculator

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.programmersbox.yugiohcalculator.ui.theme.Emerald
import com.programmersbox.yugiohcalculator.ui.theme.YugiohCalculatorTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardCounterView(vm: CardCounterViewModel = viewModel()) {
    ChooseCardDialog(
        visible = vm.showCardPicker,
        onDismissRequest = { vm.showCardPicker = false },
        vm = vm
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Card Counters") }
            )
        }
    ) { p ->
        LazyColumn(
            contentPadding = p,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            item {
                OutlinedButton(
                    onClick = { vm.showCardPicker = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp)
                ) { Text("Add Card") }
            }
            items(vm.cardList, key = { it.id ?: it.name ?: it.hashCode() }) { card ->
                CounterItem(
                    cardInfo = card,
                    onDelete = { vm.cardList.remove(card) }
                )
            }
        }
    }
}

@OptIn(
    ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class
)
@Composable
fun CounterItem(
    cardInfo: CardInfo,
    onDelete: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var counter by remember { mutableStateOf(0) }

    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false),
            confirmButton = { TextButton(onClick = { showDialog = false }) { Text("Done") } },
            title = { Text(cardInfo.name.orEmpty()) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(onClick = { counter++ }) {
                        AsyncImage(
                            model = remember(counter) {
                                ImageRequest.Builder(context)
                                    .data(cardInfo.card_images?.randomOrNull()?.image_url.orEmpty())
                                    .lifecycle(lifecycle)
                                    .crossfade(true)
                                    .build()
                            },
                            contentScale = ContentScale.Crop,
                            contentDescription = cardInfo.name.orEmpty(),
                        )
                    }
                    ListItem(
                        overlineText = {
                            Text("Atk/Def: ${cardInfo.atk}/${cardInfo.def} | Lvl: ${cardInfo.level}")
                        },
                        headlineText = { },
                        supportingText = { Text(cardInfo.desc.orEmpty()) }
                    )
                }
            }
        )
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                model = remember {
                    ImageRequest.Builder(context)
                        .data(cardInfo.card_images?.randomOrNull()?.image_url.orEmpty())
                        .lifecycle(lifecycle)
                        .crossfade(true)
                        .build()
                },
                contentScale = ContentScale.Crop,
                contentDescription = cardInfo.name.orEmpty(),
                modifier = Modifier
                    .clickable { showDialog = true }
                    .align(Alignment.CenterVertically)
            )
            var counterCount by remember { mutableStateOf(0) }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = { counterCount-- },
                ) { Icon(Icons.Default.RemoveCircle, null) }
                Text(
                    counterCount.toString(),
                    textAlign = TextAlign.Center
                )
                IconButton(
                    onClick = { counterCount++ },
                ) { Icon(Icons.Default.AddCircle, null) }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.padding(horizontal = 2.dp)
            ) { Icon(Icons.Default.Delete, null) }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChooseCardDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    vm: CardCounterViewModel
) {
    if (visible) {
        LaunchedEffect(Unit) { vm.loadCards() }
        AlertDialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = onDismissRequest,
            confirmButton = { TextButton(onClick = onDismissRequest) { Text("Done") } },
            title = { Text("Choose Cards") },
            text = {
                Crossfade(targetState = vm.loadingState) { target ->
                    when (target) {
                        NetworkLoadingState.Loading -> {
                            CircularProgressIndicator()
                        }
                        NetworkLoadingState.Success -> {
                            var search by remember { mutableStateOf("") }

                            Scaffold(
                                bottomBar = {
                                    BottomAppBar {
                                        OutlinedTextField(
                                            value = search,
                                            onValueChange = { search = it },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            ) { p ->
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(3),
                                    verticalArrangement = Arrangement.spacedBy(2.dp),
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    contentPadding = p
                                ) {
                                    items(
                                        vm.cards.filter { it.name?.contains(search, true) == true }
                                    ) { card ->
                                        val isContained = vm.cardList.any { it == card }
                                        Surface(
                                            onClick = {
                                                if (isContained) vm.cardList.remove(card)
                                                else vm.cardList.add(card)
                                            },
                                            border = BorderStroke(
                                                animateDpAsState(targetValue = if (isContained) 4.dp else 0.dp).value,
                                                animateColorAsState(targetValue = if (isContained) Emerald else Color.Transparent).value
                                            )
                                        ) {
                                            AsyncImage(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data(card.card_images?.randomOrNull()?.image_url.orEmpty())
                                                    .lifecycle(LocalLifecycleOwner.current)
                                                    .crossfade(true)
                                                    .build(),
                                                contentScale = ContentScale.Crop,
                                                contentDescription = card.name.orEmpty(),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        NetworkLoadingState.Failure -> {
                            OutlinedButton(onClick = { vm.loadCards() }) {
                                Text("Something went wrong. Press here to try to load card list")
                            }
                        }
                    }
                }

            }
        )
    }
}

@Preview
@Composable
fun CardCounterPreview() {
    YugiohCalculatorTheme {
        CardCounterView()
    }
}

@Preview
@Composable
fun CounterItemPreview() {
    YugiohCalculatorTheme {
        CounterItem(
            cardInfo = CardInfo(
                id = null,
                name = "Dark Magician",
                type = null,
                desc = null,
                race = null,
                card_images = emptyList()
            ),
            onDelete = {},
        )
    }
}