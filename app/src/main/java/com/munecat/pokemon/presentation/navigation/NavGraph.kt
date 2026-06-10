package com.munecat.pokemon.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.munecat.pokemon.presentation.screen.battle.BattleScreen
import com.munecat.pokemon.presentation.screen.main.MainScreen
import com.munecat.pokemon.presentation.screen.pokelist.PokeListScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) {
            MainScreen(
                onNavigateToPokelist = {
                    navController.navigate(route = Screen.Pokelist.route)
                },
                onNavigateToBattle = {
                    navController.navigate(route = Screen.Battle.route)
                }
            )
        }
        composable(Screen.Pokelist.route) {
            PokeListScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.Battle.route) {
            BattleScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

sealed class Screen (val route: String) {
    data object Main : Screen("main")
    data object Pokelist : Screen("poke_list")
    data object Battle : Screen("battle")
}