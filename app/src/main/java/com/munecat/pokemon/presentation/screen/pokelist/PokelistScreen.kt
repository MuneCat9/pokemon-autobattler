package com.munecat.pokemon.presentation.screen.pokelist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.munecat.pokemon.R
import com.munecat.pokemon.domain.model.Pokemon
import com.munecat.pokemon.presentation.screen.battle.getTypeSmallIcon
import com.munecat.pokemon.presentation.screen.components.PokemonInfoDialog
import com.munecat.pokemon.presentation.ui.theme.PokemonSolid
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokeListScreen(
    onBackClick: () -> Unit,
    viewModel: PokelistViewModel = hiltViewModel()
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
        modifier = Modifier
            .fillMaxSize()
    ) {
        AsyncImage(
            model = R.drawable.background_3,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Scaffold(
            modifier = Modifier
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                },
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    title = {
                        Image(
                            painter = painterResource(R.drawable.pokedex_logo),
                            contentDescription = "Pokédex",
                            modifier = Modifier
                                .padding(start = 68.dp, top = 4.dp)
                                .height(40.dp)
                        )
                    },
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
                val focusRequester = remember { FocusRequester() }
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 0.dp)
                        .focusRequester(focusRequester)
                        .focusable(false)
                        .clickable {
                            focusRequester.requestFocus()
                            keyboardController?.show()
                        },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            keyboardController?.hide()
                            focusRequester.freeFocus()
                        }
                    ),
                    placeholder = { Text("Find a pokemon") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },
                    trailingIcon = {
                        if (state.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.onTertiaryFixed,
                        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.onTertiaryFixed
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

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
                            items = filteredPokemon,
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
                                },
                                onItemClick = {
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                    selectedPokemon = pokemon
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
                    onPokemonClick = { pokemon ->
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        selectedPokemon = pokemon
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
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
        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showFilterSheet = false
                }
            ) {
                FilterContent(
                    selectedTypes = state.selectedTypes,
                    sortMode = state.sortMode,
                    isSortAscending = state.isSortAscending,
                    onTypeFilterChanged = { viewModel.onTypeFilterChanged(it) },
                    onSortModeChanged = { viewModel.onSortModeChanged(it) },
                    onToggleSortDirection = { viewModel.onToggleSortDirection() },
                    onClearFilters = {
                        viewModel.clearFilters()
                        showFilterSheet = false
                    }
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
            .padding(horizontal = 16.dp, vertical = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onItemClick() }
                .padding(horizontal = 12.dp, vertical = 0.dp),
            verticalAlignment = Alignment.CenterVertically,
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
                    .size(56.dp),
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

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                pokemon.types.forEach { type ->
                    Image(
                        painter = painterResource(getTypeSmallIcon(type)),
                        contentDescription = type,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Icon(
                imageVector = if (isInTeam) Icons.Default.Check else Icons.Default.Add,
                contentDescription = if (isInTeam) "In team" else "Add to team",
                tint = if (isInTeam) Color(0xFF4CAF50) else MaterialTheme.colorScheme.tertiary,
                modifier = Modifier
                    .size(24.dp)
                    .then(
                        if (isInTeam) Modifier
                        else Modifier.clickable { onAddClick() }
                    )
            )
        }
    }
}

@Composable
fun TeamSlots(
    modifier: Modifier = Modifier,
    team: List<Pokemon>,
    onRemoveClick: (Int) -> Unit,
    onPokemonClick: (Pokemon) -> Unit = {}
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        for (i in 0 until 3) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
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
                                .fillMaxSize(1f)
                                .clickable { onPokemonClick(team[i]) },
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
                            modifier = Modifier
                                .padding(top = 8.dp),
                            text = "?",
                            fontFamily = PokemonSolid,
                            fontSize = 32.sp,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterContent(
    selectedTypes: Set<String>,
    sortMode: SortMode,
    isSortAscending: Boolean,
    onTypeFilterChanged: (String) -> Unit,
    onSortModeChanged: (SortMode) -> Unit,
    onToggleSortDirection: () -> Unit,
    onClearFilters: () -> Unit
) {
    val types = listOf(
        "normal", "fighting", "flying", "poison", "ground", "rock",
        "bug", "ghost", "steel", "fire", "water", "grass",
        "electric", "psychic", "ice", "dragon", "dark", "fairy"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            "Sort by",
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onToggleSortDirection) {
                Icon(
                    imageVector = if (isSortAscending) Icons.Default.ArrowUpward
                    else Icons.Default.ArrowDownward,
                    contentDescription = "Toggle direction"
                )
            }

            FilterChip(
                selected = sortMode == SortMode.BY_NUMBER,
                onClick = { onSortModeChanged(SortMode.BY_NUMBER) },
                label = {
                    Text(
                        text = "1-151",
                        fontFamily = FontFamily.Default,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            FilterChip(
                selected = sortMode == SortMode.BY_NAME,
                onClick = { onSortModeChanged(SortMode.BY_NAME) },
                label = {
                    Text(
                        text = "A-Z",
                        fontFamily = FontFamily.Default,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Filter by type",
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Default,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column {
            types.chunked(6).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    row.forEach { type ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            IconButton(onClick = { onTypeFilterChanged(type) }) {
                                Image(
                                    painter = painterResource(getTypeSmallIcon(type)),
                                    contentDescription = type,
                                    modifier = Modifier.size(
                                        if (type in selectedTypes) 36.dp else 28.dp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onClearFilters) {
            Text(
                text = "Clear all filters",
                color = MaterialTheme.colorScheme.onBackground,
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold
            )
        }
    }
}