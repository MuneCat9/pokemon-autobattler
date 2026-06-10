package com.munecat.pokemon.presentation.screen.battle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.munecat.pokemon.domain.model.battle.BattlePokemon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BattleScreen(
    onBackClick: () -> Unit,
    viewModel: BattleViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAndStartBattle()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Battle") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            state.battleState?.let { battleState ->

                OpponentCard(
                    pokemon = battleState.opponentTeam[battleState.currentOpponentIndex],
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                BattleLog(
                    log = battleState.battleLog,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                PlayerCard(
                    pokemon = battleState.playerTeam[battleState.currentPlayerIndex],
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    state.battleState?.let { battleState ->
        if (battleState.isBattleOver) {
            AlertDialog(
                onDismissRequest = { },
                title = {
                    Text(
                        text = if (battleState.playerWon == true) "Victory!" else "Defeat!",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(if (battleState.playerWon == true) "You won the battle!" else "You lost the battle!")
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.confirmResult()
                        onBackClick()
                    }) {
                        Text("Accept")
                    }
                }
            )
        }
    }
}

@Composable
fun OpponentCard(
    pokemon: BattlePokemon,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            HpBar(
                currentHp = pokemon.currentHp,
                maxHp = pokemon.maxHp,
                name = pokemon.pokemon.name
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column {
                    StatText("Type: ${pokemon.pokemon.types.joinToString(", ")}")
                    StatText("Attack: ${pokemon.pokemon.attack}")
                    StatText("Defense: ${pokemon.pokemon.defense}")
                    StatText("Speed: ${pokemon.pokemon.speed}")
                }

                AsyncImage(
                    model = pokemon.pokemon.imageUrl,
                    contentDescription = pokemon.pokemon.name,
                    modifier = Modifier.size(100.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@Composable
fun PlayerCard(
    pokemon: BattlePokemon,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            HpBar(
                currentHp = pokemon.currentHp,
                maxHp = pokemon.maxHp,
                name = pokemon.pokemon.name
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                AsyncImage(
                    model = pokemon.pokemon.imageUrl,
                    contentDescription = pokemon.pokemon.name,
                    modifier = Modifier.size(100.dp),
                    contentScale = ContentScale.Fit
                )

                Column(horizontalAlignment = Alignment.End) {
                    StatText("Type: ${pokemon.pokemon.types.joinToString(", ")}")
                    StatText("Attack: ${pokemon.pokemon.attack}")
                    StatText("Defense: ${pokemon.pokemon.defense}")
                    StatText("Speed: ${pokemon.pokemon.speed}")
                }
            }
        }
    }
}

@Composable
fun HpBar(
    currentHp: Int,
    maxHp: Int,
    name: String
) {
    val hpFraction = if (maxHp > 0) currentHp.toFloat() / maxHp else 0f
    val barColor = when {
        hpFraction > 0.5f -> Color(0xFF4CAF50) // Зелёный
        hpFraction > 0.25f -> Color(0xFFFFC107) // Жёлтый
        else -> Color(0xFFF44336) // Красный
    }

    Column {
        Text(
            text = "$name - HP: $currentHp/$maxHp",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Gray.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(hpFraction)
                    .height(20.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(barColor)
            )
        }
    }
}

@Composable
fun StatText(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        modifier = Modifier.padding(vertical = 2.dp)
    )
}

@Composable
fun BattleLog(
    log: List<String>,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(log.size) {
        if (log.isNotEmpty()) {
            listState.animateScrollToItem(log.size - 1)
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.padding(8.dp)
        ) {
            items(log) { message ->
                Text(
                    text = message,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}