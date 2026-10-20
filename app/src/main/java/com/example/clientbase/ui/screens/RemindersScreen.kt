package com.example.clientbase.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clientbase.data.model.Customer
import com.example.clientbase.data.model.Reminder
import com.example.clientbase.ui.components.formatExactDate
import com.example.clientbase.ui.components.formatRelativeDate
import com.example.ui.theme.AccentTeal
import com.example.ui.theme.AlertOverdue
import com.example.ui.theme.AlertOverdueBg
import com.example.ui.theme.AlertWarning
import com.example.ui.theme.AlertWarningBg
import com.example.ui.theme.BorderSlate200
import com.example.ui.theme.Indigo50
import com.example.ui.theme.Indigo600
import com.example.ui.theme.Indigo700
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealText
import com.example.ui.theme.TextSlate400
import com.example.ui.theme.TextSlate500
import com.example.ui.theme.TextSlate700
import com.example.ui.theme.TextSlate900

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    reminders: List<Reminder>,
    customers: List<Customer>,
    onToggleComplete: (Long, Boolean) -> Unit,
    onDeleteReminder: (Long) -> Unit,
    onAddReminder: (customerId: Long, customerName: String, title: String, dueMillis: Long, priority: String) -> Unit,
    onCustomerClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var filterTab by remember { mutableStateOf("Pending") } // "Pending", "All", "Completed"
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredReminders = remember(reminders, filterTab) {
        when (filterTab) {
            "Pending" -> reminders.filter { !it.isCompleted }
            "Completed" -> reminders.filter { it.isCompleted }
            else -> reminders
        }
    }

    val now = System.currentTimeMillis()
    val overdueCount = reminders.count { !it.isCompleted && it.dueMillis < now }

    // Dialog for adding a new follow-up
    if (showAddDialog) {
        var selectedCustomer by remember { mutableStateOf(customers.firstOrNull()) }
        var customerDropdownExpanded by remember { mutableStateOf(false) }
        var title by remember { mutableStateOf("") }
        var priority by remember { mutableStateOf("High") }
        var daysAhead by remember { mutableStateOf(1) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Follow-up Reminder", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Customer Selector
                    Text("Associated Customer *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSlate700)
                    ExposedDropdownMenuBox(
                        expanded = customerDropdownExpanded,
                        onExpandedChange = { customerDropdownExpanded = !customerDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedCustomer?.name ?: "Select customer",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = customerDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Indigo600,
                                unfocusedBorderColor = BorderSlate200
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = customerDropdownExpanded,
                            onDismissRequest = { customerDropdownExpanded = false }
                        ) {
                            customers.forEach { cust ->
                                DropdownMenuItem(
                                    text = { Text("${cust.name} (${cust.company})") },
                                    onClick = {
                                        selectedCustomer = cust
                                        customerDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Title
                    Text("Reminder Action *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSlate700)
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("e.g. Call to finalize contract terms") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Indigo600,
                            unfocusedBorderColor = BorderSlate200
                        )
                    )

                    // Due timeline
                    Text("Due Date", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSlate700)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(0 to "Today", 1 to "Tomorrow", 3 to "In 3 Days", 7 to "In 1 Week").forEach { (d, label) ->
                            val isCur = daysAhead == d
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isCur) Indigo600 else Indigo50,
                                modifier = Modifier.clickable { daysAhead = d }
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isCur) Color.White else Indigo600,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Priority
                    Text("Priority Level", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSlate700)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("High", "Medium", "Low").forEach { p ->
                            val isSel = priority == p
                            val (color, bg) = when (p) {
                                "High" -> AlertOverdue to AlertOverdueBg
                                "Medium" -> AlertWarning to AlertWarningBg
                                else -> AccentTeal to TealContainer
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSel) color else bg,
                                modifier = Modifier.clickable { priority = p }
                            ) {
                                Text(
                                    text = p,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else color,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cust = selectedCustomer
                        if (cust != null && title.isNotBlank()) {
                            val due = now + (daysAhead * 24 * 3600 * 1000L)
                            onAddReminder(cust.id, cust.name, title.trim(), due, priority)
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
                ) {
                    Text("Save Reminder")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = TextSlate700)
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("reminders_screen")
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Follow-up Reminders",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSlate900
                    )
                    Text(
                        text = "Prioritized task queues and client outreach milestones",
                        fontSize = 13.sp,
                        color = TextSlate500
                    )
                }

                Button(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.testTag("add_reminder_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Indigo600,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Follow-up", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Overdue status banner if overdue exists
        if (overdueCount > 0) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AlertOverdueBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AlertOverdue.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(AlertOverdue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "$overdueCount Action Items are Past Due",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = AlertOverdue
                            )
                            Text(
                                text = "Contact the associated accounts to keep negotiations on track",
                                fontSize = 11.sp,
                                color = TextSlate700
                            )
                        }
                    }
                }
            }
        }

        // Filter Tabs (Pending, All, Completed)
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("Pending", "All", "Completed").forEach { tab ->
                    val isSelected = filterTab == tab
                    FilterChip(
                        selected = isSelected,
                        onClick = { filterTab = tab },
                        label = {
                            val count = when (tab) {
                                "Pending" -> reminders.count { !it.isCompleted }
                                "Completed" -> reminders.count { it.isCompleted }
                                else -> reminders.size
                            }
                            Text("$tab ($count)", fontSize = 12.sp)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Indigo50,
                            selectedLabelColor = Indigo700
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            selectedBorderColor = Indigo600,
                            borderColor = BorderSlate200
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("filter_tab_$tab")
                    )
                }
            }
        }

        // Reminder Items List
        if (filteredReminders.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate200)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(36.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = AccentTeal,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (filterTab == "Pending") "All caught up! No pending follow-ups" else "No reminders in this view",
                            fontWeight = FontWeight.Bold,
                            color = TextSlate700,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Schedule proactive reminders to maintain strong client relationships",
                            fontSize = 12.sp,
                            color = TextSlate500
                        )
                    }
                }
            }
        } else {
            items(filteredReminders, key = { it.id }) { reminder ->
                ReminderItemCard(
                    reminder = reminder,
                    onToggleComplete = { onToggleComplete(reminder.id, !reminder.isCompleted) },
                    onDelete = { onDeleteReminder(reminder.id) },
                    onCustomerClick = { onCustomerClick(reminder.customerId) }
                )
            }
        }
    }
}

@Composable
fun ReminderItemCard(
    reminder: Reminder,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit,
    onCustomerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val now = System.currentTimeMillis()
    val isOverdue = !reminder.isCompleted && reminder.dueMillis < now
    val isToday = !reminder.isCompleted && !isOverdue && (reminder.dueMillis - now < 24 * 3600 * 1000L)

    val (priorityColor, priorityBg) = when (reminder.priority.lowercase()) {
        "high" -> AlertOverdue to AlertOverdueBg
        "medium" -> AlertWarning to AlertWarningBg
        else -> AccentTeal to TealContainer
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("reminder_item_${reminder.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOverdue) AlertOverdueBg.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isOverdue) AlertOverdue.copy(alpha = 0.4f) else BorderSlate200
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Checkbox to mark done
            IconButton(
                onClick = onToggleComplete,
                modifier = Modifier
                    .size(32.dp)
                    .testTag("reminder_toggle_${reminder.id}")
            ) {
                Icon(
                    imageVector = if (reminder.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = if (reminder.isCompleted) "Mark pending" else "Mark done",
                    tint = if (reminder.isCompleted) AccentTeal else if (isOverdue) AlertOverdue else Indigo600,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = reminder.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (reminder.isCompleted) TextSlate400 else TextSlate900,
                        textDecoration = if (reminder.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = priorityBg
                    ) {
                        Text(
                            text = reminder.priority,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = priorityColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.clickable { onCustomerClick() }
                    ) {
                        Text(
                            text = "Account:",
                            fontSize = 11.sp,
                            color = TextSlate400
                        )
                        Text(
                            text = reminder.customerName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Indigo600
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (isOverdue) {
                            Surface(shape = RoundedCornerShape(4.dp), color = AlertOverdue) {
                                Text(
                                    text = "Overdue",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        } else if (isToday) {
                            Surface(shape = RoundedCornerShape(4.dp), color = AlertWarning) {
                                Text(
                                    text = "Due Today",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }

                        Text(
                            text = formatRelativeDate(reminder.dueMillis),
                            fontSize = 11.sp,
                            color = if (isOverdue) AlertOverdue else TextSlate500,
                            fontWeight = if (isOverdue || isToday) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            // Delete action
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete reminder",
                    tint = TextSlate400,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
