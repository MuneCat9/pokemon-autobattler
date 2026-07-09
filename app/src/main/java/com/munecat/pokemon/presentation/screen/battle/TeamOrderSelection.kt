package com.munecat.pokemon.presentation.screen.battle

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    var showRules by remember { mutableStateOf(false) }

    var draggingIndex by remember { mutableIntStateOf(-1) }
    var targetIndex by remember { mutableIntStateOf(-1) }

    val density = LocalDensity.current.density

    Box(modifier = Modifier.fillMaxSize()) {
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
                        OpponentSlot(pokemon = pokemon, onClick = { selectedPokemon = pokemon })
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                teamOrder.forEachIndexed { index, id ->
                    val effectiveId = when {
                        draggingIndex != -1 && targetIndex != -1 && index == draggingIndex -> teamOrder[targetIndex]
                        draggingIndex != -1 && targetIndex != -1 && index == targetIndex -> teamOrder[draggingIndex]
                        else -> id
                    }
                    
                    val playerPokemon = allPokemon[effectiveId]
                    val opponentPokemon = allPokemon[opponentOrder.getOrNull(index)]

                    Box(
                        modifier = Modifier.size(60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (playerPokemon != null && opponentPokemon != null) {
                            EffectivenessArrow(playerPokemon, opponentPokemon)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

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
                        val targetOffset = if (index == targetIndex && draggingIndex != -1 && draggingIndex != targetIndex) {
                            val slotsBetween = targetIndex - draggingIndex
                            val slotWidth = 110.dp.value * density
                            -slotsBetween * slotWidth
                        } else {
                            0f
                        }
                        PlayerSlot(
                            pokemon = pokemon,
                            index = index,
                            isDragging = draggingIndex == index,
                            isTarget = targetIndex == index && draggingIndex != index,
                            animatedTargetOffset = targetOffset,
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
                                if (targetIdx != draggingIndex && targetIdx != index) {
                                    targetIndex = targetIdx
                                } else if (targetIdx == draggingIndex) {
                                    targetIndex = -1
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
                modifier = Modifier.width(200.dp).height(56.dp),
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

        FloatingActionButton(
            onClick = { showRules = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            containerColor = MaterialTheme.colorScheme.onTertiaryFixed
        ) {
            Icon(
                modifier = Modifier.size(40.dp),
                painter = painterResource(R.drawable.pokeball_placeholder),
                contentDescription = "Show rules",
                tint = Color.Unspecified
            )
        }
    }

    selectedPokemon?.let { pokemon ->
        PokemonInfoDialog(
            pokemon = pokemon,
            onDismiss = { selectedPokemon = null }
        )
    }

    if (showRules) {
        AlertDialog(
            onDismissRequest = { showRules = false },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(onClick = { showRules = false }) {
                        Text(
                            text = "Close",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Ketchum,
                            fontSize = 24.sp
                        )
                    }
                }
            },
            title = {
                Text(
                    text = "Type Effectiveness",
                    fontFamily = Ketchum,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                TypeEffectivenessTable()
            },
            shape = RoundedCornerShape(20)
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

    val (icon, color) = when {
        playerToOpponentMax > opponentToPlayerMax -> Icons.Default.ArrowUpward to Color(0xFF4CAF50)
        playerToOpponentMax < opponentToPlayerMax -> Icons.Default.ArrowDownward to Color(0xFFF44336)
        else -> Icons.Default.HorizontalRule to Color(0xFF9E9E9E)
    }

    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = color,
        modifier = Modifier.size(28.dp)
    )
}

@Composable
fun PlayerSlot(
    pokemon: Pokemon,
    index: Int,
    isDragging: Boolean,
    isTarget: Boolean,
    animatedTargetOffset: Float = 0f,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
    onDragOver: (Int) -> Unit = {},
    onClick: () -> Unit = {}
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var slotWidth by remember { mutableIntStateOf(0) }

    val smoothTargetOffset by animateFloatAsState(
        targetValue = animatedTargetOffset,
        animationSpec = spring(
            stiffness = Spring.StiffnessLow,
            visibilityThreshold = 0.5f
        ),
        label = "targetOffset"
    )

    val finalOffsetX = if (isDragging) offsetX else if (isTarget) smoothTargetOffset else 0f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .onGloballyPositioned { coordinates ->
                slotWidth = coordinates.size.width
            }
            .offset { IntOffset(finalOffsetX.roundToInt(), offsetY.roundToInt()) }
            .zIndex(if (isDragging) 1f else if (isTarget) 0.5f else 0f)
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
fun OpponentSlot(pokemon: Pokemon, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
        Text(
            pokemon.name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun TypeEffectivenessTable() {
    val allTypes = listOf(
        "normal", "fighting", "flying", "poison", "ground", "rock",
        "bug", "ghost", "steel", "fire", "water", "grass",
        "electric", "psychic", "ice", "dragon", "dark", "fairy"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 450.dp)
            .padding(horizontal = 0.dp)
    ) {
        // Заголовок
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                "Type",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(0.35f)
            )
            Text(
                "Strong vs",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF4CAF50),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1.325f)
                    .background(Color(0xFF4CAF50).copy(alpha = 0.15f))
            )
            Text(
                "Weak vs",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFFF44336),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1.325f)
                    .background(Color(0xFFF44336).copy(alpha = 0.15f))
            )
        }

        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(allTypes) { attackingType ->
                val attacking = PokemonType.fromString(attackingType)
                if (attacking != null) {
                    val strongAgainst = allTypes.filter { defendingType ->
                        val defending = PokemonType.fromString(defendingType)
                        defending != null && TypeEffectiveness.getMultiplier(attacking, listOf(defending)) > 1f
                    }

                    val weakAgainst = allTypes.filter { defendingType ->
                        val defending = PokemonType.fromString(defendingType)
                        defending != null && TypeEffectiveness.getMultiplier(attacking, listOf(defending)) < 1f
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(0.35f),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Image(
                                painter = painterResource(getTypeSmallIcon(attackingType)),
                                contentDescription = attackingType,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column(
                            modifier = Modifier
                                .weight(1.325f)
                                .background(Color(0xFF4CAF50).copy(alpha = 0.15f)),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                strongAgainst.forEach { type ->
                                    Image(
                                        painter = painterResource(getTypeSmallIcon(type)),
                                        contentDescription = type,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                if (strongAgainst.isEmpty()) {
                                    Text("-", fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        }

                        Column(
                            modifier = Modifier
                                .weight(1.325f)
                                .background(Color(0xFFF44336).copy(alpha = 0.15f)),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                weakAgainst.forEach { type ->
                                    Image(
                                        painter = painterResource(getTypeSmallIcon(type)),
                                        contentDescription = type,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                if (weakAgainst.isEmpty()) {
                                    Text("-", fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        }
                    }

                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}
