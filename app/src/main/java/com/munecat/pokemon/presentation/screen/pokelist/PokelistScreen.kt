package com.munecat.pokemon.presentation.screen.pokelist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.munecat.pokemon.R
import com.munecat.pokemon.domain.model.Pokemon
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokeListScreen(
    onBackClick: () -> Unit,
    viewModel: PokelistViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            modifier = Modifier.padding(start = 78.dp),
                            text = "Pokédex"
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        // TO DO !!!!!!!!!!!!!!
                        IconButton(
                            onClick = { }) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filter"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (state.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        items(
                            items = state.allPokemon,
                            key = { it.id }
                        ) { pokemon ->
                            PokemonListItem(
                                pokemon = pokemon,
                                isInTeam = state.team.any { it.id == pokemon.id },
                                onAddClick = {
                                    viewModel.processCommand(
                                        PokelistCommand.AddToTeam(
                                            pokemon
                                        )
                                    )
                                }
                            )
                        }
                    }
                }

                TeamSlots(
                    team = state.team,
                    onRemoveClick = { pokemonId ->
                        viewModel.processCommand(PokelistCommand.RemoveFromTeam(pokemonId = pokemonId))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }

        state.error?.let { error ->
            var isVisible by remember { mutableStateOf(true) }

            LaunchedEffect(state.errorTimestamp) {
                isVisible = true
                delay(500L)
                isVisible = false
                delay(1200L)
                viewModel.clearError()
            }
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(1200))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .padding(32.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PokemonListItem(
    pokemon: Pokemon,
    isInTeam: Boolean,
    onAddClick: () -> Unit,
    onItemClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onItemClick() }                            // TO DO !!!!
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#${pokemon.id}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.width(40.dp)
            )

            AsyncImage(
                model = pokemon.imageUrl,
                contentDescription = pokemon.name,
                modifier = Modifier
                    .size(40.dp),
                contentScale = ContentScale.Fit,
                placeholder = painterResource(R.drawable.pokeball_placeholder),
                error = painterResource(R.drawable.pokeball_error)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = pokemon.name,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )

            if (!isInTeam) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add to team",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        onAddClick()
                    }
                )
            }
        }
    }
}

@Composable
fun TeamSlots(
    modifier: Modifier = Modifier,
    team: List<Pokemon>,
    onRemoveClick: (Int) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        for (i in 0 until 3) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (i < team.size) {
                        AsyncImage(
                            model = team[i].imageUrl,
                            contentDescription = team[i].name,
                            modifier = Modifier
                                .fillMaxSize(0.8f),
                            contentScale = ContentScale.Fit
                        )
                        IconButton(
                            onClick = {
                                onRemoveClick(team[i].id)
                            },
                            modifier = Modifier
                                .padding(4.dp)
                                .align(Alignment.TopEnd)
                                .size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove from team",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                    } else {
                        Text(
                            text = "?",
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
    }
}