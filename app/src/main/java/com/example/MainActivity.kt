package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clientbase.ui.components.DesktopSidebar
import com.example.clientbase.ui.components.MobileBottomNav
import com.example.clientbase.ui.components.MobileTopBar
import com.example.clientbase.ui.screens.AddEditCustomerScreen
import com.example.clientbase.ui.screens.AddInteractionDialog
import com.example.clientbase.ui.screens.CustomerListScreen
import com.example.clientbase.ui.screens.CustomerProfileScreen
import com.example.clientbase.ui.screens.DashboardScreen
import com.example.clientbase.ui.screens.LoginScreen
import com.example.clientbase.ui.screens.RemindersScreen
import com.example.clientbase.ui.screens.SegmentsScreen
import com.example.clientbase.ui.viewmodel.CrmScreen
import com.example.clientbase.ui.viewmodel.CrmViewModel
import com.example.ui.theme.BackgroundSlate
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ClientBaseApp()
            }
        }
    }
}

@Composable
fun ClientBaseApp(viewModel: CrmViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val userSession by viewModel.userSession.collectAsStateWithLifecycle()
    val allCustomers by viewModel.allCustomers.collectAsStateWithLifecycle()
    val filteredCustomers by viewModel.filteredCustomers.collectAsStateWithLifecycle()
    val dashboardStats by viewModel.dashboardStats.collectAsStateWithLifecycle()
    val recentInteractions by viewModel.recentInteractions.collectAsStateWithLifecycle()
    val segments by viewModel.segments.collectAsStateWithLifecycle()
    val allReminders by viewModel.allReminders.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val statusFilter by viewModel.statusFilter.collectAsStateWithLifecycle()
    val tagFilter by viewModel.tagFilter.collectAsStateWithLifecycle()
    val selectedCustomerId by viewModel.selectedCustomerId.collectAsStateWithLifecycle()
    val interactionDialogCustomer by viewModel.interactionDialogCustomer.collectAsStateWithLifecycle()

    // Handle system back navigation
    BackHandler(enabled = currentScreen !is CrmScreen.Dashboard && currentScreen !is CrmScreen.Login) {
        if (!viewModel.navigateBack()) {
            viewModel.navigateTo(CrmScreen.Dashboard)
        }
    }

    // Modal Add Interaction Dialog
    interactionDialogCustomer?.let { targetCustomer ->
        AddInteractionDialog(
            customer = targetCustomer,
            onDismiss = { viewModel.closeAddInteractionDialog() },
            onSave = { type, dateMillis, desc, reminderTitle, reminderDue ->
                viewModel.addInteraction(type, dateMillis, desc, reminderTitle, reminderDue)
            }
        )
    }

    if (!userSession.isLoggedIn || currentScreen is CrmScreen.Login) {
        LoginScreen(
            onLoginSuccess = { email, workspace, remember ->
                viewModel.login(email, workspace, remember)
            }
        )
        return
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(BackgroundSlate)) {
        val isDesktop = maxWidth >= 720.dp

        if (isDesktop) {
            // Desktop-first responsive layout with persistent sidebar
            Row(modifier = Modifier.fillMaxSize()) {
                DesktopSidebar(
                    currentScreen = currentScreen,
                    onNavigate = { screen -> viewModel.navigateTo(screen) },
                    onAddCustomer = { viewModel.navigateTo(CrmScreen.AddEditCustomer()) },
                    customerCount = allCustomers.size,
                    reminderDueCount = dashboardStats.followUpsDue,
                    segmentCount = segments.size,
                    session = userSession,
                    onLogout = { viewModel.logout() }
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    ScreenContent(
                        currentScreen = currentScreen,
                        viewModel = viewModel,
                        allCustomers = allCustomers,
                        filteredCustomers = filteredCustomers,
                        dashboardStats = dashboardStats,
                        recentInteractions = recentInteractions,
                        segments = segments,
                        allReminders = allReminders,
                        searchQuery = searchQuery,
                        statusFilter = statusFilter,
                        tagFilter = tagFilter,
                        selectedCustomerId = selectedCustomerId
                    )
                }
            }
        } else {
            // Mobile layout with TopBar and BottomNavigationBar
            val showBottomNav = currentScreen is CrmScreen.Dashboard ||
                currentScreen is CrmScreen.CustomerList ||
                currentScreen is CrmScreen.Segments ||
                currentScreen is CrmScreen.Reminders

            Scaffold(
                topBar = {
                    val title = when (currentScreen) {
                        is CrmScreen.Dashboard -> "Overview"
                        is CrmScreen.CustomerList -> "Directory"
                        is CrmScreen.CustomerProfile -> "Profile"
                        is CrmScreen.AddEditCustomer -> "Customer Details"
                        is CrmScreen.Segments -> "Segments"
                        is CrmScreen.Reminders -> "Reminders"
                        else -> "ClientBase"
                    }
                    MobileTopBar(
                        title = title,
                        onAddCustomer = { viewModel.navigateTo(CrmScreen.AddEditCustomer()) },
                        onLogout = { viewModel.logout() },
                        userSession = userSession
                    )
                },
                bottomBar = {
                    if (showBottomNav) {
                        MobileBottomNav(
                            currentScreen = currentScreen,
                            onNavigate = { screen -> viewModel.navigateTo(screen) },
                            customerCount = allCustomers.size,
                            reminderDueCount = dashboardStats.followUpsDue
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    ScreenContent(
                        currentScreen = currentScreen,
                        viewModel = viewModel,
                        allCustomers = allCustomers,
                        filteredCustomers = filteredCustomers,
                        dashboardStats = dashboardStats,
                        recentInteractions = recentInteractions,
                        segments = segments,
                        allReminders = allReminders,
                        searchQuery = searchQuery,
                        statusFilter = statusFilter,
                        tagFilter = tagFilter,
                        selectedCustomerId = selectedCustomerId
                    )
                }
            }
        }
    }
}

@Composable
fun ScreenContent(
    currentScreen: CrmScreen,
    viewModel: CrmViewModel,
    allCustomers: List<com.example.clientbase.data.model.Customer>,
    filteredCustomers: List<com.example.clientbase.data.model.Customer>,
    dashboardStats: com.example.clientbase.ui.viewmodel.DashboardStats,
    recentInteractions: List<com.example.clientbase.data.model.Interaction>,
    segments: List<com.example.clientbase.ui.viewmodel.SegmentCount>,
    allReminders: List<com.example.clientbase.data.model.Reminder>,
    searchQuery: String,
    statusFilter: String,
    tagFilter: String,
    selectedCustomerId: Long?
) {
    when (currentScreen) {
        is CrmScreen.Login -> {
            LoginScreen(
                onLoginSuccess = { email, workspace, remember ->
                    viewModel.login(email, workspace, remember)
                }
            )
        }

        is CrmScreen.Dashboard -> {
            DashboardScreen(
                stats = dashboardStats,
                recentInteractions = recentInteractions,
                onAddCustomer = { viewModel.navigateTo(CrmScreen.AddEditCustomer()) },
                onNavigateCustomers = { viewModel.navigateTo(CrmScreen.CustomerList) },
                onNavigateReminders = { viewModel.navigateTo(CrmScreen.Reminders) },
                onNavigateSegments = { viewModel.navigateTo(CrmScreen.Segments) },
                onCustomerClick = { customerId ->
                    viewModel.navigateTo(CrmScreen.CustomerProfile(customerId))
                }
            )
        }

        is CrmScreen.CustomerList -> {
            CustomerListScreen(
                customers = filteredCustomers,
                searchQuery = searchQuery,
                onSearchQueryChange = { viewModel.setSearchQuery(it) },
                statusFilter = statusFilter,
                onStatusFilterChange = { viewModel.setStatusFilter(it) },
                tagFilter = tagFilter,
                onTagFilterChange = { viewModel.setTagFilter(it) },
                allTags = segments.map { it.name },
                selectedCustomerId = selectedCustomerId,
                onCustomerSelect = { viewModel.selectCustomer(it) },
                onCustomerOpenProfile = { customerId ->
                    viewModel.navigateTo(CrmScreen.CustomerProfile(customerId))
                },
                onAddCustomer = { viewModel.navigateTo(CrmScreen.AddEditCustomer()) },
                onAddInteraction = { customer ->
                    viewModel.openAddInteractionDialog(customer)
                }
            )
        }

        is CrmScreen.CustomerProfile -> {
            val customer = allCustomers.firstOrNull { it.id == currentScreen.customerId }
            if (customer != null) {
                val customerInteractions by viewModel.getInteractionsForCustomer(customer.id)
                    .collectAsStateWithLifecycle(initialValue = emptyList())
                val customerReminders by viewModel.getRemindersForCustomer(customer.id)
                    .collectAsStateWithLifecycle(initialValue = emptyList())

                CustomerProfileScreen(
                    customer = customer,
                    interactions = customerInteractions,
                    reminders = customerReminders,
                    onBack = {
                        if (!viewModel.navigateBack()) {
                            viewModel.navigateTo(CrmScreen.CustomerList)
                        }
                    },
                    onEditCustomer = {
                        viewModel.navigateTo(CrmScreen.AddEditCustomer(customer.id))
                    },
                    onDeleteCustomer = {
                        viewModel.deleteCustomer(customer.id)
                    },
                    onAddInteraction = {
                        viewModel.openAddInteractionDialog(customer)
                    },
                    onDeleteInteraction = { interactionId ->
                        viewModel.deleteInteraction(interactionId)
                    },
                    onToggleReminder = { reminderId, completed ->
                        viewModel.toggleReminderCompleted(reminderId, completed)
                    },
                    onAddReminder = {
                        viewModel.openAddReminderDialog(customer)
                    }
                )
            } else {
                viewModel.navigateTo(CrmScreen.CustomerList)
            }
        }

        is CrmScreen.AddEditCustomer -> {
            val initialCustomer = currentScreen.customerId?.let { id ->
                allCustomers.firstOrNull { it.id == id }
            }
            AddEditCustomerScreen(
                initialCustomer = initialCustomer,
                onSave = { saved ->
                    viewModel.saveCustomer(saved) { newId ->
                        viewModel.navigateTo(CrmScreen.CustomerProfile(newId))
                    }
                },
                onCancel = {
                    if (!viewModel.navigateBack()) {
                        viewModel.navigateTo(CrmScreen.CustomerList)
                    }
                }
            )
        }

        is CrmScreen.Segments -> {
            SegmentsScreen(
                segments = segments,
                onSelectSegment = { segmentName ->
                    viewModel.filterBySegmentAndNavigate(segmentName)
                }
            )
        }

        is CrmScreen.Reminders -> {
            RemindersScreen(
                reminders = allReminders,
                customers = allCustomers,
                onToggleComplete = { id, comp -> viewModel.toggleReminderCompleted(id, comp) },
                onDeleteReminder = { id -> viewModel.deleteReminder(id) },
                onAddReminder = { custId, custName, title, due, priority ->
                    viewModel.addReminder(custId, custName, title, due, priority)
                },
                onCustomerClick = { custId ->
                    viewModel.navigateTo(CrmScreen.CustomerProfile(custId))
                }
            )
        }
    }
}
