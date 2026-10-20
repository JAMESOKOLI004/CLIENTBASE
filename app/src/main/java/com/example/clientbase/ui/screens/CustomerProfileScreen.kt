package com.example.clientbase.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clientbase.data.model.Customer
import com.example.clientbase.data.model.Interaction
import com.example.clientbase.data.model.Reminder
import com.example.clientbase.ui.components.CustomerAvatar
import com.example.clientbase.ui.components.StatusBadge
import com.example.clientbase.ui.components.TagPill
import com.example.clientbase.ui.components.dialPhoneNumber
import com.example.clientbase.ui.components.formatExactDate
import com.example.clientbase.ui.components.formatRelativeDate
import com.example.clientbase.ui.components.formatShortDate
import com.example.clientbase.ui.components.sendEmail
import com.example.ui.theme.AccentTeal
import com.example.ui.theme.AlertOverdue
import com.example.ui.theme.AlertOverdueBg
import com.example.ui.theme.AlertWarning
import com.example.ui.theme.AlertWarningBg
import com.example.ui.theme.BackgroundSlate
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomerProfileScreen(
    customer: Customer,
    interactions: List<Interaction>,
    reminders: List<Reminder>,
    onBack: () -> Unit,
    onEditCustomer: () -> Unit,
    onDeleteCustomer: () -> Unit,
    onAddInteraction: () -> Unit,
    onDeleteInteraction: (Long) -> Unit,
    onToggleReminder: (Long, Boolean) -> Unit,
    onAddReminder: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Customer Record", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete ${customer.name} and all associated interactions and reminders? This cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDeleteCustomer()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AlertOverdue)
                ) {
                    Text("Delete Record", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel", color = TextSlate700)
                }
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundSlate)
            .testTag("customer_profile_screen"),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 840.dp)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Navigation & Actions Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("profile_back_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextSlate700
                            )
                        }
                        Column {
                            Text(
                                text = customer.name,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSlate900
                            )
                            Text(
                                text = "${customer.company} • Account Overview",
                                fontSize = 12.sp,
                                color = TextSlate500
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = onEditCustomer,
                            modifier = Modifier.testTag("profile_edit_btn"),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate200)
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit", fontSize = 12.sp)
                        }

                        IconButton(
                            onClick = { showDeleteConfirm = true },
                            modifier = Modifier.testTag("profile_delete_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete customer",
                                tint = AlertOverdue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Header Profile Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate200),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                CustomerAvatar(name = customer.name, size = 56.dp)
                                Column {
                                    Text(
                                        text = customer.name,
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSlate900
                                    )
                                    Text(
                                        text = customer.company,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Indigo600
                                    )
                                    Text(
                                        text = "Client since ${formatShortDate(customer.createdAt)}",
                                        fontSize = 11.sp,
                                        color = TextSlate400
                                    )
                                }
                            }

                            StatusBadge(status = customer.status)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Tags in Accent Teal
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            customer.getTagList().forEach { tag ->
                                TagPill(tag = tag)
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))
                        Divider(color = BorderSlate200)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Quick Actions Bar (Call, Email, Add Follow-up)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { dialPhoneNumber(context, customer.phone) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("profile_call_btn"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Indigo600,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Call", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { sendEmail(context, customer.email) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("profile_email_btn"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = TealContainer,
                                    contentColor = TealText
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Email", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = onAddReminder,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate200)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Alarm,
                                    contentDescription = null,
                                    tint = AlertWarning,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Reminder", fontSize = 13.sp, color = TextSlate700)
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Detailed Contact Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                ProfileInfoItem(icon = Icons.Default.Phone, label = "PHONE", value = customer.phone.ifBlank { "Not specified" })
                                ProfileInfoItem(icon = Icons.Default.LocationOn, label = "ADDRESS", value = customer.address.ifBlank { "Not specified" })
                            }
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                ProfileInfoItem(icon = Icons.Default.Email, label = "EMAIL", value = customer.email.ifBlank { "Not specified" })
                                ProfileInfoItem(icon = Icons.Default.History, label = "LAST CONTACT", value = formatRelativeDate(customer.lastContactedAt))
                            }
                        }

                        // Notes section inside card if present
                        if (customer.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Indigo50.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "ACCOUNT NOTES",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Indigo700,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = customer.notes,
                                        fontSize = 12.sp,
                                        color = TextSlate700,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Customer Reminders / Follow-ups Section
            if (reminders.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate200)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Scheduled Follow-ups",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSlate900
                                )
                                Text(
                                    text = "${reminders.count { !it.isCompleted }} pending",
                                    fontSize = 12.sp,
                                    color = TextSlate500
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            reminders.forEach { rem ->
                                val isOverdue = !rem.isCompleted && rem.dueMillis < System.currentTimeMillis()
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    IconButton(
                                        onClick = { onToggleReminder(rem.id, !rem.isCompleted) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (rem.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                            contentDescription = "Toggle reminder",
                                            tint = if (rem.isCompleted) AccentTeal else if (isOverdue) AlertOverdue else TextSlate400,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = rem.title,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = if (rem.isCompleted) TextSlate400 else TextSlate900
                                        )
                                        Text(
                                            text = "Due ${formatRelativeDate(rem.dueMillis)}",
                                            fontSize = 11.sp,
                                            color = if (isOverdue) AlertOverdue else TextSlate500
                                        )
                                    }
                                    if (isOverdue) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = AlertOverdueBg
                                        ) {
                                            Text(
                                                text = "Overdue",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = AlertOverdue,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Divider(color = BorderSlate200.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }

            // Timeline Header & "+ Add Interaction" Button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Interaction Timeline",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSlate900
                        )
                        Text(
                            text = "Chronological record of calls, meetings, emails & notes",
                            fontSize = 12.sp,
                            color = TextSlate500
                        )
                    }

                    Button(
                        onClick = onAddInteraction,
                        modifier = Modifier.testTag("profile_add_interaction_btn"),
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
                        Text("Add Interaction", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Timeline Items
            if (interactions.isEmpty()) {
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
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = TextSlate400,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No interactions recorded yet",
                                fontWeight = FontWeight.Bold,
                                color = TextSlate700,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Record calls, client meetings, or key discussion points",
                                fontSize = 12.sp,
                                color = TextSlate500
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = onAddInteraction,
                                colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Log First Interaction", fontSize = 12.sp)
                            }
                        }
                    }
                }
            } else {
                items(interactions) { interaction ->
                    TimelineItemRow(
                        interaction = interaction,
                        onDelete = { onDeleteInteraction(interaction.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileInfoItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Indigo600, modifier = Modifier.size(16.dp))
        Column {
            Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSlate400)
            Text(text = value, fontSize = 12.sp, color = TextSlate900, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun TimelineItemRow(
    interaction: Interaction,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, iconColor, iconBg) = when (interaction.type.lowercase()) {
        "call" -> Triple(Icons.Default.Call, Indigo600, Indigo50)
        "meeting" -> Triple(Icons.Default.Event, AccentTeal, TealContainer)
        "email" -> Triple(Icons.Default.Email, Color(0xFF2563EB), Color(0xFFDBEAFE))
        else -> Triple(Icons.Default.NoteAlt, Color(0xFFD97706), Color(0xFFFEF3C7))
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag("timeline_item_${interaction.id}"),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Timeline Spine & Icon Node
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconBg)
                    .border(1.5.dp, iconColor.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = interaction.type,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(60.dp)
                    .background(BorderSlate200)
            )
        }

        // Timeline Content Card
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate200)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = iconBg
                        ) {
                            Text(
                                text = interaction.type,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = iconColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = formatExactDate(interaction.dateMillis),
                            fontSize = 11.sp,
                            color = TextSlate500
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete interaction",
                            tint = TextSlate400,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = interaction.description,
                    fontSize = 13.sp,
                    color = TextSlate700,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
