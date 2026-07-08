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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.munecat.pokemon.R
import com.munecat.pokemon.domain.model.Pokemon
import com.munecat.pokemon.domain.model.battle.PokemonType
import com.munecat.pokemon.domain.model.battle.TypeEffectiveness
import com.munecat.pokemon.presentation.screen.components.PokemonInfoDialog
import com.munecat.pokemon.presentation.ui.theme.Ketchum
import java.util.Locale
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

    var draggingIndex by remember { mutableIntStateOf(-1) }
    var targetIndex by remember { mutableIntStateOf(-1) }

    AsyncImage(
        model = R.drawable.background_2,
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Choose your team order",
            fontFamily = Ketchum,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Opponent team:",
            fontFamily = Ketchum,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(4.dp))

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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            teamOrder.forEachIndexed { index, id ->
                val playerPokemon = allPokemon[id]
                val opponentPokemon = allPokemon[opponentOrder.getOrNull(index)]
                
                Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center) {
                    if (playerPokemon != null && opponentPokemon != null) {
                        EffectivenessArrow(playerPokemon, opponentPokemon)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your team:",
            fontFamily = Ketchum,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(4.dp))

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
                .height(56.dp),
            shape = RoundedCornerShape(20),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onPrimaryFixed,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        ) {
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = "Ready!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
            )
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
fun EffectivenessArrow(player: Pokemon, opponent: Pokemon) {
    val playerTypes = player.types.mapNotNull { PokemonType.fromString(it) }
    val opponentTypes = opponent.types.mapNotNull { PokemonType.fromString(it) }

    val playerToOpponentMax = playerTypes.maxOfOrNull { pType ->
        TypeEffectiveness.getMultiplier(pType, opponentTypes)
    } ?: 1f

    val opponentToPlayerMax = opponentTypes.maxOfOrNull { oType ->
        TypeEffectiveness.getMultiplier(oType, playerTypes)
    } ?: 1f

    val (icon, color, displayMultiplier) = when {
        playerToOpponentMax > opponentToPlayerMax ->
            Triple(Icons.Default.ArrowUpward, Color(0xFF4CAF50), playerToOpponentMax)
        playerToOpponentMax < opponentToPlayerMax ->
            Triple(Icons.Default.ArrowDownward, Color(0xFFF44336), opponentToPlayerMax)
        else ->
            Triple(Icons.Default.HorizontalRule, Color(0xFF9E9E9E), 1f)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(28.dp)
        )
        if (displayMultiplier != 1f) {
            Text(
                text = "×${String.format(Locale.ROOT, "%.1f", displayMultiplier)}",
                fontSize = 11.sp,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
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
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var slotWidth by remember { mutableIntStateOf(0) }

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
                .clip(RoundedCornerShape(8.dp))
                .clickable { onClick() },
            colors = CardDefaults.cardColors(
                containerColor = when {
                    isDragging -> MaterialTheme.colorScheme.tertiary
                    isTarget -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)
                    else -> MaterialTheme.colorScheme.onTertiaryFixed
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
                Icon(
                    imageVector = Icons.Default.DragIndicator,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
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
                .clip(RoundedCornerShape(8.dp))
                .clickable { onClick() },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimaryFixedVariant)
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