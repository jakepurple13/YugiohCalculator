package com.programmersbox.yugiohcalculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlin.random.nextInt

class CoinFlipViewModel : ViewModel() {

    val coinList = mutableStateListOf<Coin>()

    fun addCoin() {
        coinList.add(Coin(Random.nextBoolean(), ""))
    }

    fun removeCoin() {
        coinList.removeLastOrNull()
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