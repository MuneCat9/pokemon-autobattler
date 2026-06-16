package com.munecat.pokemon.presentation.screen.battle

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.munecat.pokemon.domain.model.battle.BattleLogEntry
import com.munecat.pokemon.domain.model.battle.BattlePokemon
import com.munecat.pokemon.domain.model.battle.LogType
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BattleScreen(
    onBackClick: () -> Unit,
    viewModel: BattleViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAllPokemon()
    }

    if (state.isTeamSelectionVisible) {
        TeamOrderSelection(
            teamOrder = state.teamOrder,
            opponentOrder = state.opponentOrder,
            allPokemon = state.allPokemon.associateBy { it.id },
            onSwapSlots = { index1, index2 -> viewModel.swapTeamSlots(index1, index2) },
            onReady = { viewModel.confirmSelection() }
        )
    } else {

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            modifier = Modifier.padding(start = 94.dp),
                            text = "Battle"
                        )
                    },
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
                        modifier = Modifier.fillMaxWidth(),
                        team = battleState.opponentTeam,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    BattleLog(
                        log = battleState.battleLog,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    PlayerCard(
                        pokemon = battleState.playerTeam[battleState.currentPlayerIndex],
                        modifier = Modifier.fillMaxWidth(),
                        team = battleState.playerTeam,
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
}

@Composable
fun OpponentCard(
    pokemon: BattlePokemon,
    modifier: Modifier = Modifier,
    team: List<BattlePokemon>,
) {
    var flash by remember { mutableStateOf(false) }
    LaunchedEffect(pokemon.currentHp) {
        flash = true
        delay(300)
        flash = false
    }

    Card(
        modifier = modifier
            .then(
                if (flash) Modifier.drawBehind {
                    drawRect(Color.Red.copy(alpha = 0.3f))
                } else Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(
                alpha = 0.3f
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {

            HpBar(
                currentHp = pokemon.currentHp,
                maxHp = pokemon.maxHp,
                name = pokemon.pokemon.name
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        pokemon.pokemon.types.forEach { type ->
                            Image(
                                painter = painterResource(getTypeIcon(type)),
                                contentDescription = type,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }

                    StatText("Attack: ${pokemon.pokemon.attack}")
                    StatText("Defense: ${pokemon.pokemon.defense}")
                    StatText("Speed: ${pokemon.pokemon.speed}")
                }

                AsyncImage(
                    model = pokemon.pokemon.battleFrontUrl,
                    contentDescription = pokemon.pokemon.name,
                    modifier = Modifier
                        .size(100.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.width(40.dp))

                TeamIndicators(
                    team = team
                )
            }
        }
    }
}


@Composable
fun PlayerCard(
    pokemon: BattlePokemon,
    modifier: Modifier = Modifier,
    team: List<BattlePokemon>,
) {
    var flash by remember { mutableStateOf(false) }
    LaunchedEffect(pokemon.currentHp) {
        flash = true
        delay(300)
        flash = false
    }

    Card(
        modifier = modifier
            .then(
                if (flash) Modifier.drawBehind {
                    drawRect(Color.Red.copy(alpha = 0.3f))
                } else Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                alpha = 0.3f
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {

            HpBar(
                currentHp = pokemon.currentHp,
                maxHp = pokemon.maxHp,
                name = pokemon.pokemon.name
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamIndicators(
                    team = team
                )

                Spacer(modifier = Modifier.width(40.dp))

                AsyncImage(
                    model = pokemon.pokemon.battleBackUrl,
                    contentDescription = pokemon.pokemon.name,
                    modifier = Modifier
                        .size(90.dp),
                    contentScale = ContentScale.Fit
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        pokemon.pokemon.types.forEach { type ->
                            Image(
                                painter = painterResource(getTypeIcon(type)),
                                contentDescription = type,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }

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
        hpFraction > 0.5f -> Color(0xFF4CAF50)
        hpFraction > 0.25f -> Color(0xFFFFC107)
        else -> Color(0xFFF44336)
    }

    Column {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = name,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Gray.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(hpFraction)
                    .height(24.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(barColor),
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "HP: $currentHp/$maxHp",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color.White,
                textAlign = TextAlign.Center
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
    log: List<BattleLogEntry>,
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
            items(log) { entry ->
                Text(
                    text = buildAnnotatedString(entry),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}

private fun buildAnnotatedString(entry: BattleLogEntry): AnnotatedString {
    return with(AnnotatedString.Builder()) {
        val message = entry.message

        val pokemonName = when {
            " dealt " in message -> message.substringBefore(" dealt ")
            " fainted" in message -> message.substringBefore(" fainted!")
            else -> ""
        }

        val damagePattern = Regex("""(\d+) damage""")
        val damageMatch = damagePattern.find(message)
        val damageValue = damageMatch?.groupValues?.get(1) ?: ""

        if (pokemonName.isNotEmpty() && damageValue.isNotEmpty()) {
            val beforeName = message.substringBefore(pokemonName)
            val afterName = message.substringAfter(pokemonName).substringBefore(damageValue)
            val afterDamage = message.substringAfter("$damageValue damage")

            append(beforeName)

            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(pokemonName)
            }

            append(afterName)

            withStyle(
                SpanStyle(
                    color = when (entry.type) {
                        LogType.HIGH_ROLL -> Color(0xFFFF4444)
                        LogType.LOW_ROLL -> Color(0xFFAAAAAA)
                        LogType.SUPER_EFFECTIVE -> Color(0xFF4CAF50)
                        LogType.NOT_EFFECTIVE -> Color(0xFFFF9800)
                        LogType.DODGED -> Color(0xFF42A5F5)
                        else -> Color.Unspecified
                    },
                    fontWeight = if (entry.type == LogType.HIGH_ROLL) FontWeight.Bold else FontWeight.Normal
                )
            ) {
                append(damageValue)
            }

            append(" damage")
            append(afterDamage)

        } else if (" dodged " in message) {
            val dodger = message.substringBefore(" dodged ")
            val afterDodged = message.substringAfter(" dodged ")

            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(dodger)
            }

            withStyle(
                SpanStyle(
                    color = Color(0xFF42A5F5),
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(" dodged ")
            }

            append(afterDodged)

        } else {
            withStyle(
                SpanStyle(
                    color = when (entry.type) {
                        LogType.FAINTED -> Color(0xFFF44336)
                        LogType.DODGED -> Color(0xFF42A5F5)
                        else -> Color.Unspecified
                    },
                    fontWeight = if (entry.type == LogType.FAINTED) FontWeight.Bold else FontWeight.Normal
                )
            ) {
                append(message)
            }
        }

        toAnnotatedString()
    }
}

@Composable
fun TeamIndicators(
    team: List<BattlePokemon>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        team.forEachIndexed { index, battlePokemon ->
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                    )
                    .background(
                        when {
                            battlePokemon.isFainted -> Color.Red.copy(alpha = 0.3f)
                            else -> Color.Transparent
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = battlePokemon.pokemon.imageUrl,
                    contentDescription = battlePokemon.pokemon.name,
                    modifier = Modifier
                        .size(40.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}