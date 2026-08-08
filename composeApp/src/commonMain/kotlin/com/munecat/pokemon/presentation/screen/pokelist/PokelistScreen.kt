package com.munecat.pokemon.presentation.screen.pokelist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.munecat.pokemon.domain.model.Pokemon
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokeListScreen(
    onBackClick: () -> Unit,
    viewModel: PokelistViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedPokemon by remember { mutableStateOf<Pokemon?>(null) }
    
    val filteredPokemon = remember(
        state.allPokemon,
        state.searchQuery,
        state.selectedTypes,
        state.sortMode,
        state.isSortAscending
    ) {
        var result = state.allPokemon

        if (state.searchQuery.isNotBlank()) {
            result = result.filter {
                it.name.contains(state.searchQuery, ignoreCase = true)
            }
        }

        if (state.selectedTypes.isNotEmpty()) {
            result = result.filter { pokemon ->
                pokemon.types.any { it in state.selectedTypes }
            }
        }

        when (state.sortMode) {
            SortMode.BY_NUMBER -> {
                if (state.isSortAscending) result.sortedBy { it.id }
                else result.sortedByDescending { it.id }
            }

            SortMode.BY_NAME -> {
                if (state.isSortAscending) result.sortedBy { it.name.lowercase() }
                else result.sortedByDescending { it.name.lowercase() }
            }
        }
    }
    
    var showFilterSheet by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Временно убираем фоновую картинку до настройки ресурсов KMP
        Scaffold(
            modifier = Modifier
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                },
            containerColor = Color.White.copy(alpha = 0.9f),
            topBar = {
                TopAppBar(
                    title = { Text("Pokédex") },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            showFilterSheet = true
                        }) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filter")
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
                val focusRequester = remember { FocusRequester() }
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .focusRequester(focusRequester),
                    placeholder = { Text("Find a pokemon") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(items = filteredPokemon, key = { it.id }) { pokemon ->
                            PokemonListItem(
                                pokemon = pokemon,
                                isInTeam = state.team.any { it.id == pokemon.id },
                                onAddClick = {
                                    viewModel.processCommand(PokelistCommand.AddToTeam(pokemon))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PokemonListItem(
    pokemon: Pokemon,
    isInTeam: Boolean,
    onAddClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "#${pokemon.id}", fontWeight = FontWeight.Bold)
            
            Spacer(modifier = Modifier.width(12.dp))
            
            AsyncImage(
                model = pokemon.imageUrl,
                contentDescription = pokemon.name,
                modifier = Modifier.size(50.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(text = pokemon.name, modifier = Modifier.weight(1f))

            IconButton(onClick = onAddClick) {
                Icon(
                    imageVector = if (isInTeam) Icons.Default.Check else Icons.Default.Add,
                    contentDescription = null,
                    tint = if (isInTeam) Color.Green else Color.Gray
                )
            }
        }
    }
}
