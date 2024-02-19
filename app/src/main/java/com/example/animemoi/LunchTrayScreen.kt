package com.example.animemoi

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.animemoi.data.DataSource
import com.example.animemoi.ui.*

enum class LunchTrayScreen(@StringRes val title: Int) {
    Start(title = R.string.app_name), Entree(title = R.string.choose_entree), SideDish(title = R.string.choose_side_dish), Accompaniment(
        title = R.string.choose_accompaniment
    ),
    Checkout(title = R.string.order_checkout)
}

/**
 * Composable that displays the topBar and displays back button if back navigation is possible.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LunchTrayAppBar(
    @StringRes currentScreenTitle: Int, canNavigateBack: Boolean, navigateUp: () -> Unit, modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(title = { stringResource(currentScreenTitle) }, modifier = modifier, navigationIcon = {
        if (canNavigateBack) {
            IconButton(onClick = navigateUp) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_button)
                )
            }
        }
    })
}

@Composable
fun LunchTrayApp() {
    //Create NavController
    val navController = rememberNavController()
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = LunchTrayScreen.valueOf(
        backStackEntry?.destination?.route ?: LunchTrayScreen.Start.name
    )
    // Create ViewModel
    val viewModel: OrderViewModel = viewModel()

    Scaffold(topBar = {
        LunchTrayAppBar(currentScreenTitle = currentScreen.title,
            canNavigateBack = navController.previousBackStackEntry != null,
            navigateUp = { navController.navigateUp() })
    }) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = LunchTrayScreen.Start.name,
        ) {
            composable(route = LunchTrayScreen.Start.name) {
                StartOrderScreen(
                    onStartOrderButtonClicked = {
                        navController.navigate(LunchTrayScreen.Entree.name)
                    }, modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            composable(route = LunchTrayScreen.Entree.name) {
                EntreeMenuScreen(options = DataSource.entreeMenuItems, onCancelButtonClicked = {
                    viewModel.resetOrder()
                    navController.popBackStack(LunchTrayScreen.Start.name, inclusive = false)
                }, onNextButtonClicked = {
                    navController.navigate(LunchTrayScreen.SideDish.name)
                }, onSelectionChanged = { item ->
                    viewModel.updateEntree(item)
                }, modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                )
            }

            composable(route = LunchTrayScreen.SideDish.name) {
                SideDishMenuScreen(options = DataSource.sideDishMenuItems, onCancelButtonClicked = {
                    viewModel.resetOrder()
                    navController.popBackStack(LunchTrayScreen.Start.name, inclusive = false)
                }, onNextButtonClicked = {
                    navController.navigate(LunchTrayScreen.Accompaniment.name)
                }, onSelectionChanged = { item ->
                    viewModel.updateSideDish(item)
                }, modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                )
            }

            composable(route = LunchTrayScreen.Accompaniment.name) {
                AccompanimentMenuScreen(options = DataSource.accompanimentMenuItems, onCancelButtonClicked = {
                    viewModel.resetOrder()
                    navController.popBackStack(LunchTrayScreen.Start.name, inclusive = false)
                }, onNextButtonClicked = {
                    navController.navigate(LunchTrayScreen.Checkout.name)
                }, onSelectionChanged = { item ->
                    viewModel.updateAccompaniment(item)
                }, modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                )
            }

            composable(route = LunchTrayScreen.Checkout.name) {
                CheckoutScreen(orderUiState = uiState, onCancelButtonClicked = {
                    viewModel.resetOrder()
                    navController.popBackStack(LunchTrayScreen.Start.name, inclusive = false)
                }, onNextButtonClicked = {
                    viewModel.resetOrder()
                    navController.popBackStack(LunchTrayScreen.Start.name, inclusive = false)
                }, modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding(),
                        start = dimensionResource(R.dimen.padding_medium),
                        end = dimensionResource(R.dimen.padding_medium),
                    )
                )
            }
        }
    }
}