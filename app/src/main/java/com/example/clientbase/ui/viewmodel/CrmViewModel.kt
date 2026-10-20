package com.example.clientbase.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.clientbase.data.AppDatabase
import com.example.clientbase.data.SessionManager
import com.example.clientbase.data.UserSession
import com.example.clientbase.data.model.Customer
import com.example.clientbase.data.model.Interaction
import com.example.clientbase.data.model.Reminder
import com.example.clientbase.data.repository.CrmRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

sealed interface CrmScreen {
    object Login : CrmScreen
    object Dashboard : CrmScreen
    object CustomerList : CrmScreen
    data class CustomerProfile(val customerId: Long) : CrmScreen
    data class AddEditCustomer(val customerId: Long? = null) : CrmScreen
    object Segments : CrmScreen
    object Reminders : CrmScreen
}

data class SegmentCount(
    val name: String,
    val count: Int,
    val companies: List<String>
)

data class DashboardStats(
    val totalCustomers: Int = 0,
    val newThisMonth: Int = 0,
    val followUpsDue: Int = 0,
    val overdueFollowUps: Int = 0,
    val activeOpportunities: Int = 0
)

class CrmViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(application)
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = CrmRepository(
        database.customerDao(),
        database.interactionDao(),
        database.reminderDao()
    )

    val userSession: StateFlow<UserSession> = sessionManager.sessionState

    private val _currentScreen = MutableStateFlow<CrmScreen>(
        if (sessionManager.sessionState.value.isLoggedIn) CrmScreen.Dashboard else CrmScreen.Login
    )
    val currentScreen: StateFlow<CrmScreen> = _currentScreen.asStateFlow()

    // Navigation back stack for smooth BackHandler behavior
    private val backStack = mutableListOf<CrmScreen>()

    // Filter and search state for customers
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _statusFilter = MutableStateFlow("All")
    val statusFilter: StateFlow<String> = _statusFilter.asStateFlow()

    private val _tagFilter = MutableStateFlow("All")
    val tagFilter: StateFlow<String> = _tagFilter.asStateFlow()

    // Selected customer for desktop side preview
    private val _selectedCustomerId = MutableStateFlow<Long?>(null)
    val selectedCustomerId: StateFlow<Long?> = _selectedCustomerId.asStateFlow()

    // Modal dialogs
    private val _interactionDialogCustomer = MutableStateFlow<Customer?>(null)
    val interactionDialogCustomer: StateFlow<Customer?> = _interactionDialogCustomer.asStateFlow()

    private val _reminderDialogCustomer = MutableStateFlow<Customer?>(null)
    val reminderDialogCustomer: StateFlow<Customer?> = _reminderDialogCustomer.asStateFlow()

    // Data streams
    val allCustomers: StateFlow<List<Customer>> = repository.allCustomers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentInteractions: StateFlow<List<Interaction>> = repository.recentInteractions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReminders: StateFlow<List<Reminder>> = repository.allReminders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered customers flow
    val filteredCustomers: StateFlow<List<Customer>> = combine(
        allCustomers,
        _searchQuery,
        _statusFilter,
        _tagFilter
    ) { customers, query, status, tag ->
        customers.filter { customer ->
            val matchesQuery = query.isBlank() ||
                customer.name.contains(query, ignoreCase = true) ||
                customer.company.contains(query, ignoreCase = true) ||
                customer.email.contains(query, ignoreCase = true) ||
                customer.phone.contains(query, ignoreCase = true)

            val matchesStatus = status == "All" || customer.status.equals(status, ignoreCase = true)
            val matchesTag = tag == "All" || customer.getTagList().any { it.equals(tag, ignoreCase = true) }

            matchesQuery && matchesStatus && matchesTag
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dynamic segments list with counts
    val segments: StateFlow<List<SegmentCount>> = allCustomers.combine(_searchQuery) { customers, _ ->
        val map = mutableMapOf<String, MutableList<String>>()
        customers.forEach { customer ->
            customer.getTagList().forEach { tag ->
                val list = map.getOrPut(tag) { mutableListOf() }
                if (!list.contains(customer.company)) {
                    list.add(customer.company)
                }
            }
        }
        map.map { (tag, companies) ->
            SegmentCount(
                name = tag,
                count = customers.count { it.getTagList().any { t -> t.equals(tag, ignoreCase = true) } },
                companies = companies.take(3)
            )
        }.sortedByDescending { it.count }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dashboard KPI Stats
    val dashboardStats: StateFlow<DashboardStats> = combine(
        allCustomers,
        allReminders
    ) { customers, reminders ->
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val monthStart = calendar.timeInMillis

        val newThisMonth = customers.count { it.createdAt >= monthStart }
        val activeOpportunities = customers.count { it.status.equals("Opportunity", ignoreCase = true) || it.status.equals("Lead", ignoreCase = true) }

        // Due reminders: not completed, due up to end of today or overdue
        val endOfTodayCal = Calendar.getInstance()
        endOfTodayCal.set(Calendar.HOUR_OF_DAY, 23)
        endOfTodayCal.set(Calendar.MINUTE, 59)
        endOfTodayCal.set(Calendar.SECOND, 59)
        val endOfToday = endOfTodayCal.timeInMillis

        val dueReminders = reminders.count { !it.isCompleted && it.dueMillis <= endOfToday }
        val overdueReminders = reminders.count { !it.isCompleted && it.dueMillis < now }

        DashboardStats(
            totalCustomers = customers.size,
            newThisMonth = newThisMonth,
            followUpsDue = dueReminders,
            overdueFollowUps = overdueReminders,
            activeOpportunities = activeOpportunities
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardStats())

    init {
        // Automatically preselect first customer for desktop layout when customers load
        viewModelScope.launch {
            allCustomers.collect { list ->
                if (_selectedCustomerId.value == null && list.isNotEmpty()) {
                    _selectedCustomerId.value = list.first().id
                }
            }
        }
    }

    fun navigateTo(screen: CrmScreen) {
        if (_currentScreen.value != screen) {
            backStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (backStack.isNotEmpty()) {
            _currentScreen.value = backStack.removeAt(backStack.size - 1)
            return true
        }
        return false
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setStatusFilter(status: String) {
        _statusFilter.value = status
    }

    fun setTagFilter(tag: String) {
        _tagFilter.value = tag
    }

    fun selectCustomer(id: Long?) {
        _selectedCustomerId.value = id
    }

    fun filterBySegmentAndNavigate(segmentName: String) {
        _tagFilter.value = segmentName
        _statusFilter.value = "All"
        _searchQuery.value = ""
        navigateTo(CrmScreen.CustomerList)
    }

    fun openAddInteractionDialog(customer: Customer) {
        _interactionDialogCustomer.value = customer
    }

    fun closeAddInteractionDialog() {
        _interactionDialogCustomer.value = null
    }

    fun openAddReminderDialog(customer: Customer?) {
        _reminderDialogCustomer.value = customer
    }

    fun closeAddReminderDialog() {
        _reminderDialogCustomer.value = null
    }

    fun saveCustomer(customer: Customer, onSaved: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = repository.saveCustomer(customer)
            _selectedCustomerId.value = id
            onSaved(id)
        }
    }

    fun deleteCustomer(id: Long) {
        viewModelScope.launch {
            repository.deleteCustomer(id)
            if (_selectedCustomerId.value == id) {
                _selectedCustomerId.value = allCustomers.value.firstOrNull { it.id != id }?.id
            }
            if (_currentScreen.value is CrmScreen.CustomerProfile) {
                navigateTo(CrmScreen.CustomerList)
            }
        }
    }

    fun addInteraction(type: String, dateMillis: Long, description: String, scheduleReminderTitle: String? = null, reminderDueMillis: Long? = null) {
        val target = _interactionDialogCustomer.value ?: return
        viewModelScope.launch {
            repository.addInteraction(
                Interaction(
                    customerId = target.id,
                    customerName = target.name,
                    type = type,
                    dateMillis = dateMillis,
                    description = description
                )
            )

            if (!scheduleReminderTitle.isNullOrBlank() && reminderDueMillis != null) {
                repository.saveReminder(
                    Reminder(
                        customerId = target.id,
                        customerName = target.name,
                        title = scheduleReminderTitle,
                        dueMillis = reminderDueMillis,
                        isCompleted = false,
                        priority = "High"
                    )
                )
            }
            closeAddInteractionDialog()
        }
    }

    fun deleteInteraction(id: Long) {
        viewModelScope.launch {
            repository.deleteInteraction(id)
        }
    }

    fun addReminder(customerId: Long, customerName: String, title: String, dueMillis: Long, priority: String) {
        viewModelScope.launch {
            repository.saveReminder(
                Reminder(
                    customerId = customerId,
                    customerName = customerName,
                    title = title,
                    dueMillis = dueMillis,
                    isCompleted = false,
                    priority = priority
                )
            )
            closeAddReminderDialog()
        }
    }

    fun toggleReminderCompleted(id: Long, completed: Boolean) {
        viewModelScope.launch {
            repository.setReminderCompleted(id, completed)
        }
    }

    fun deleteReminder(id: Long) {
        viewModelScope.launch {
            repository.deleteReminder(id)
        }
    }

    fun getInteractionsForCustomer(customerId: Long) = repository.getInteractionsForCustomer(customerId)

    fun getRemindersForCustomer(customerId: Long) = repository.getRemindersForCustomer(customerId)

    fun login(email: String, workspace: String, remember: Boolean) {
        sessionManager.login(email, workspace, remember)
        backStack.clear()
        _currentScreen.value = CrmScreen.Dashboard
    }

    fun logout() {
        sessionManager.logout()
        backStack.clear()
        _currentScreen.value = CrmScreen.Login
    }
}
