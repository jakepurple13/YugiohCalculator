package com.programmersbox.yugiohcalculator

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.programmersbox.yugiohcalculator.ui.theme.YugiohCalculatorTheme
import kotlinx.coroutines.launch

class CardCounterViewModel : ViewModel() {

    var cards by mutableStateOf<List<CardInfo>>(emptyList())
    var loadingState by mutableStateOf(NetworkLoadingState.Loading)

    val cardList = mutableStateListOf<CardWithCounter>()

    var showCardPicker by mutableStateOf(false)

    init {
        loadCards()
    }

    fun loadCards() {
        viewModelScope.launch {
            if (cards.isNotEmpty()) {
                cards = Networking.loadCards { loadingState = it }
            }
        }
    }

}

data class CardWithCounter(val cardInfo: CardInfo) {
    var counter by mutableStateOf(0)
}

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
            items(vm.cardList) { card ->
                CounterItem(
                    cardInfo = card.cardInfo,
                    counterValue = card.counter,
                    onAdd = { card.counter++ },
                    onSubtract = { card.counter-- },
                    onDelete = { vm.cardList.removeIf { it.cardInfo == card.cardInfo } }
                )
            }
        }
    }
}

@Composable
fun CounterItem(
    cardInfo: CardInfo,
    counterValue: Int,
    onAdd: () -> Unit,
    onSubtract: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {
        Row {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(cardInfo.card_images?.randomOrNull()?.image_url.orEmpty())
                    .lifecycle(LocalLifecycleOwner.current)
                    .crossfade(true)
                    .build(),
                contentScale = ContentScale.Crop,
                contentDescription = cardInfo.name.orEmpty(),
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, top = 4.dp)
            ) {
                IconButton(
                    onClick = onSubtract,
                    modifier = Modifier.weight(1f)
                ) { Icon(Icons.Default.RemoveCircle, null) }
                Text(
                    counterValue.toString(),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                IconButton(
                    onClick = onAdd,
                    modifier = Modifier.weight(1f)
                ) { Icon(Icons.Default.AddCircle, null) }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.Top)
                    .padding(horizontal = 2.dp)
            ) { IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null) } }
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
                            LazyVerticalGrid(columns = GridCells.Fixed(3)) {
                                items(vm.cards) { card ->
                                    Surface(onClick = { vm.cardList.add(CardWithCounter(card)) }) {
                                        /*AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(card.card_images?.randomOrNull()?.image_url.orEmpty())
                                                .lifecycle(LocalLifecycleOwner.current)
                                                .crossfade(true)
                                                .build(),
                                            contentScale = ContentScale.Crop,
                                            contentDescription = card.name.orEmpty(),
                                        )*/
                                        Text(card.name.orEmpty())
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
                name = null,
                type = null,
                desc = null,
                race = null,
                card_images = emptyList()
            ),
            counterValue = 1,
            onAdd = {},
            onDelete = {},
            onSubtract = {}
        )
    }
}