package com.example.clientbase.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clientbase.data.model.Interaction
import com.example.clientbase.ui.components.CustomerAvatar
import com.example.clientbase.ui.components.StatCard
import com.example.clientbase.ui.components.formatRelativeDate
import com.example.clientbase.ui.viewmodel.DashboardStats
import com.example.ui.theme.AccentTeal
import com.example.ui.theme.AlertOverdue
import com.example.ui.theme.AlertOverdueBg
import com.example.ui.theme.AlertWarning
import com.example.ui.theme.AlertWarningBg
import com.example.ui.theme.BorderSlate200
import com.example.ui.theme.Indigo50
import com.example.ui.theme.Indigo600
import com.example.ui.theme.Indigo900
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealText
import com.example.ui.theme.TextSlate400
import com.example.ui.theme.TextSlate500
import com.example.ui.theme.TextSlate700
import com.example.ui.theme.TextSlate900

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    stats: DashboardStats,
    recentInteractions: List<Interaction>,
    onAddCustomer: () -> Unit,
    onNavigateCustomers: () -> Unit,
    onNavigateReminders: () -> Unit,
    onNavigateSegments: () -> Unit,
    onCustomerClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen")
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Executive Overview",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSlate900
                    )
                    Text(
                        text = "Pipeline performance, customer growth, and urgent follow-ups",
                        fontSize = 13.sp,
                        color = TextSlate500
                    )
                }

                Button(
                    onClick = onAddCustomer,
                    modifier = Modifier.testTag("dashboard_add_customer_btn"),
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
                    Text(
                        text = "Add Customer",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Urgent Alert banner if overdue follow-ups exist
        if (stats.overdueFollowUps > 0) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onNavigateReminders() }
                        .testTag("dashboard_overdue_alert"),
                    color = AlertOverdueBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AlertOverdue.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
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
                                    text = "${stats.overdueFollowUps} Overdue Follow-up Actions",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = AlertOverdue
                                )
                                Text(
                                    text = "Requires immediate outreach to prevent lead slippage",
                                    fontSize = 11.sp,
                                    color = TextSlate700
                                )
                            }
                        }

                        Text(
                            text = "Review Now →",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AlertOverdue
                        )
                    }
                }
            }
        }

        // Responsive KPI Stat Cards Grid
        item {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val isWide = maxWidth >= 600.dp
                if (isWide) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        StatCard(
                            title = "Total Customers",
                            value = "${stats.totalCustomers}",
                            trendText = "+14% vs last quarter",
                            icon = Icons.Default.Group,
                            iconColor = Indigo600,
                            iconBgColor = Indigo50,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateCustomers
                        )
                        StatCard(
                            title = "New This Month",
                            value = "${stats.newThisMonth}",
                            trendText = "4 joined this week",
                            icon = Icons.Default.PersonAdd,
                            iconColor = AccentTeal,
                            iconBgColor = TealContainer,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateCustomers
                        )
                        StatCard(
                            title = "Follow-ups Due",
                            value = "${stats.followUpsDue}",
                            trendText = if (stats.overdueFollowUps > 0) "${stats.overdueFollowUps} overdue" else "All up to date",
                            icon = Icons.Default.Alarm,
                            iconColor = if (stats.overdueFollowUps > 0) AlertOverdue else AlertWarning,
                            iconBgColor = if (stats.overdueFollowUps > 0) AlertOverdueBg else AlertWarningBg,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateReminders
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Total Customers",
                            value = "${stats.totalCustomers}",
                            trendText = "+14% vs last quarter",
                            icon = Icons.Default.Group,
                            iconColor = Indigo600,
                            iconBgColor = Indigo50,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onNavigateCustomers
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                title = "New This Month",
                                value = "${stats.newThisMonth}",
                                icon = Icons.Default.PersonAdd,
                                iconColor = AccentTeal,
                                iconBgColor = TealContainer,
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateCustomers
                            )
                            StatCard(
                                title = "Follow-ups Due",
                                value = "${stats.followUpsDue}",
                                icon = Icons.Default.Alarm,
                                iconColor = if (stats.overdueFollowUps > 0) AlertOverdue else AlertWarning,
                                iconBgColor = if (stats.overdueFollowUps > 0) AlertOverdueBg else AlertWarningBg,
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateReminders
                            )
                        }
                    }
                }
            }
        }

        // Quick Shortcuts bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateCustomers,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSlate700),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate200)
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Indigo600
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Directory", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = onNavigateSegments,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSlate700),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate200)
                ) {
                    Icon(
                        imageVector = Icons.Default.Label,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = AccentTeal
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Segments", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = onNavigateReminders,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSlate700),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate200)
                ) {
                    Icon(
                        imageVector = Icons.Default.Alarm,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = AlertWarning
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reminders", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Recent Activity Feed Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Activity Feed",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSlate900
                )
                Text(
                    text = "${recentInteractions.size} activities logged",
                    fontSize = 12.sp,
                    color = TextSlate500
                )
            }
        }

        // Recent Activity Feed list
        if (recentInteractions.isEmpty()) {
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
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notes,
                            contentDescription = null,
                            tint = TextSlate400,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No recent interactions logged yet",
                            fontWeight = FontWeight.SemiBold,
                            color = TextSlate700,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Log calls, meetings, or emails from any customer profile",
                            fontSize = 12.sp,
                            color = TextSlate500
                        )
                    }
                }
            }
        } else {
            items(recentInteractions) { item ->
                ActivityFeedItem(
                    interaction = item,
                    onClick = { onCustomerClick(item.customerId) }
                )
            }
        }
    }
}

@Composable
fun ActivityFeedItem(
    interaction: Interaction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, iconColor, iconBg) = when (interaction.type.lowercase()) {
        "call" -> Triple(Icons.Default.Call, Indigo600, Indigo50)
        "meeting" -> Triple(Icons.Default.Event, AccentTeal, TealContainer)
        "email" -> Triple(Icons.Default.Email, Color(0xFF2563EB), Color(0xFFDBEAFE))
        else -> Triple(Icons.Default.NoteAlt, Color(0xFFD97706), Color(0xFFFEF3C7))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("activity_item_${interaction.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate200),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = interaction.type,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = interaction.customerName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextSlate900
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = iconBg
                        ) {
                            Text(
                                text = interaction.type,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = iconColor,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = formatRelativeDate(interaction.dateMillis),
                        fontSize = 11.sp,
                        color = TextSlate500
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = interaction.description,
                    fontSize = 12.sp,
                    color = TextSlate700,
                    lineHeight = 17.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
