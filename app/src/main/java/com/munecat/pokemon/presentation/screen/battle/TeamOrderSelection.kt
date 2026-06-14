package com.munecat.pokemon.presentation.screen.battle


import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.munecat.pokemon.domain.model.Pokemon
import com.munecat.pokemon.presentation.screen.components.PokemonInfoDialog
import kotlin.math.roundToInt


@Composable
fun TeamOrderSelection(
    teamOrder: List<Int>,
    opponentOrder: List<Int>,
    allPokemon: Map<Int, Pokemon>,
    onSwapSlots: (Int, Int) -> Unit,
    onReady: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPokemon by remember { mutableStateOf<Pokemon?>(null) }

    var draggingIndex by remember { mutableStateOf(-1) }
    var targetIndex by remember { mutableStateOf(-1) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Choose your team order",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Opponent team:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            opponentOrder.forEach { id ->
                val pokemon = allPokemon[id]
                if (pokemon != null) {
                    OpponentSlot(
                        pokemon = pokemon,
                        onClick = {
                            selectedPokemon = pokemon
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Your team:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            teamOrder.forEachIndexed { index, id ->
                val pokemon = allPokemon[id]
                if (pokemon != null) {
                    PlayerSlot(
                        pokemon = pokemon,
                        index = index,
                        isDragging = draggingIndex == index,
                        isTarget = targetIndex == index && draggingIndex != index,
                        onDragStart = { draggingIndex = index },
                        onDragEnd = {
                            if (targetIndex != -1 && targetIndex != draggingIndex && draggingIndex != -1) {
                                onSwapSlots(draggingIndex, targetIndex)
                            }
                            draggingIndex = -1
                            targetIndex = -1
                        },
                        onDragCancel = {
                            draggingIndex = -1
                            targetIndex = -1
                        },
                        onDragOver = { targetIdx ->
                            if (targetIdx != index) {
                                targetIndex = targetIdx
                            }
                        },
                        onClick = { selectedPokemon = pokemon }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onReady,
            modifier = Modifier
                .width(200.dp)
                .height(56.dp)
        ) {
            Text("Ready!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
    selectedPokemon?.let { pokemon ->
        PokemonInfoDialog(
            pokemon = pokemon,
            onDismiss = { selectedPokemon = null }
        )
    }
}

@Composable
fun PlayerSlot(
    pokemon: Pokemon,
    index: Int,
    isDragging: Boolean,
    isTarget: Boolean,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
    onDragOver: (Int) -> Unit = {},
    onClick: () -> Unit = {}
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var slotWidth by remember { mutableStateOf(0) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .onGloballyPositioned { coordinates ->
                slotWidth = coordinates.size.width
            }
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .zIndex(if (isDragging) 1f else 0f)
            .pointerInput(index) {
                detectDragGestures(
                    onDragStart = {
                        onDragStart()
                        offsetX = 0f
                        offsetY = 0f
                    },
                    onDragEnd = {
                        onDragEnd()
                        offsetX = 0f
                        offsetY = 0f
                    },
                    onDragCancel = {
                        onDragCancel()
                        offsetX = 0f
                        offsetY = 0f
                    }
                ) { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                    if (slotWidth > 0) {
                        val slotOffset = (offsetX / slotWidth).roundToInt()
                        val targetIdx = (index + slotOffset).coerceIn(0, 2)
                        onDragOver(targetIdx)
                    }
                }
            }
    ) {

        Card(
            modifier = Modifier
                .size(100.dp)
                .padding(4.dp)
                .clickable { onClick() },
            colors = CardDefaults.cardColors(
                containerColor = when {
                    isDragging -> MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    isTarget -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)
                    else -> MaterialTheme.colorScheme.primaryContainer
                }
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = pokemon.cardImageUrl,
                    contentDescription = pokemon.name,
                    modifier = Modifier.fillMaxSize(0.8f)
                )
            }
        }

        Text(pokemon.name, fontSize = 12.sp, fontWeight = FontWeight.Medium)

    }
}

@Composable
fun OpponentSlot(
    pokemon: Pokemon,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .size(100.dp)
                .padding(4.dp)
                .clickable { onClick() },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = pokemon.cardImageUrl,
                    contentDescription = pokemon.name,
                    modifier = Modifier.fillMaxSize(0.8f)
                )
            }
        }
        Text(pokemon.name, fontSize = 10.sp)
    }
}