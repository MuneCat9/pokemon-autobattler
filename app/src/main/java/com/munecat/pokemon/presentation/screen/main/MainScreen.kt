package com.munecat.pokemon.presentation.screen.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.munecat.pokemon.R
import com.munecat.pokemon.domain.model.Pokemon
import com.munecat.pokemon.presentation.ui.theme.Ketchum
import com.munecat.pokemon.presentation.ui.theme.PokemonSolid

@Composable
fun MainScreen(
    onNavigateToPokelist: () -> Unit,
    onNavigateToBattle: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showRules by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        AsyncImage(
            model = R.drawable.background_1,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.pokemon_logo2),
                contentDescription = stringResource(R.string.pok_mon),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, bottom = 16.dp)
                    .height(120.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = stringResource(R.string.your_team),
                fontFamily = Ketchum,
                fontSize = 28.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            TeamSlots(
                team = state.team,
                onSlotClick = onNavigateToPokelist,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = stringResource(R.string.battle_simulation),
                fontFamily = Ketchum,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onNavigateToBattle() },
                enabled = state.isStartEnabled,
                shape = RoundedCornerShape(20),
                modifier = Modifier
                    .width(200.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onPrimaryFixed,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            ) {
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = stringResource(R.string.start),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp,
                    color = if (state.isStartEnabled)
                        MaterialTheme.colorScheme.background
                    else
                        MaterialTheme.colorScheme.background,
                )
            }
        }

        FloatingActionButton(
            onClick = { showRules = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .navigationBarsPadding(),
            containerColor = MaterialTheme.colorScheme.onTertiaryFixed
        ) {
            Icon(
                modifier = Modifier.size(40.dp),
                painter = painterResource(R.drawable.pokeball_placeholder),
                contentDescription = stringResource(R.string.manage_team),
                tint = Color.Unspecified
            )
        }
    }

    if (showRules) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showRules = false },
            confirmButton = {
                Button(onClick = { showRules = false }) {
                    Text(
                        modifier = Modifier.padding(top = 4.dp),
                        text = stringResource(R.string.got_it),
                        letterSpacing = 4.sp,

                    )
                }
            },
            title = {
                Text(
                    text = stringResource(R.string.battle_rules),
                    fontFamily = Ketchum,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    letterSpacing = 2.sp
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.welcome_text),
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                )
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun TeamSlots(
    team: List<Pokemon>,
    onSlotClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        for (i in 0 until 3) {
            Card(
                onClick = onSlotClick,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.onTertiaryFixed
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (i < team.size) {
                        AsyncImage(
                            model = team[i].cardImageUrl,
                            contentDescription = team[i].name,
                            modifier = Modifier
                                .fillMaxSize(0.8f),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Text(
                            modifier = Modifier
                                .padding(top = 8.dp),
                            text = "?",
                            fontFamily = PokemonSolid,
                            fontSize = 36.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}