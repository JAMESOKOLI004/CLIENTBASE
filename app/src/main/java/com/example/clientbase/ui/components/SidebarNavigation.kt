package com.example.clientbase.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clientbase.data.UserSession
import com.example.clientbase.ui.viewmodel.CrmScreen
import com.example.ui.theme.AccentTeal
import com.example.ui.theme.AlertOverdue
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

sealed class NavItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val screen: CrmScreen,
    val testTag: String
) {
    object Dashboard : NavItem("Dashboard", Icons.Filled.Dashboard, Icons.Outlined.Dashboard, CrmScreen.Dashboard, "nav_dashboard")
    object Customers : NavItem("Customers", Icons.Filled.Group, Icons.Outlined.Group, CrmScreen.CustomerList, "nav_customers")
    object Segments : NavItem("Segments", Icons.Filled.Label, Icons.Outlined.Label, CrmScreen.Segments, "nav_segments")
    object Reminders : NavItem("Reminders", Icons.Filled.Alarm, Icons.Outlined.Alarm, CrmScreen.Reminders, "nav_reminders")
}

val navItems = listOf(
    NavItem.Dashboard,
    NavItem.Customers,
    NavItem.Segments,
    NavItem.Reminders
)

@Composable
fun DesktopSidebar(
    currentScreen: CrmScreen,
    onNavigate: (CrmScreen) -> Unit,
    onAddCustomer: () -> Unit,
    customerCount: Int,
    reminderDueCount: Int,
    segmentCount: Int,
    session: UserSession,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .width(260.dp)
            .fillMaxHeight()
            .testTag("desktop_sidebar"),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate200)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            // Brand Logo & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Indigo600),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CorporateFare,
                        contentDescription = "ClientBase Logo",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = "ClientBase",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSlate900,
                        letterSpacing = (-0.3).sp
                    )
                    Text(
                        text = "CRM & Pipelines",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = AccentTeal
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Workspace pill
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = Indigo50
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(AccentTeal)
                    )
                    Column {
                        Text(
                            text = session.workspace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Indigo900,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = session.domain,
                            fontSize = 10.sp,
                            color = TextSlate500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Add Customer Button
            Button(
                onClick = onAddCustomer,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sidebar_add_customer_btn"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Indigo600,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Add Customer",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "WORKSPACE MENU",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextSlate400,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )

            // Nav links
            navItems.forEach { item ->
                val isSelected = when (item) {
                    is NavItem.Dashboard -> currentScreen is CrmScreen.Dashboard
                    is NavItem.Customers -> currentScreen is CrmScreen.CustomerList || currentScreen is CrmScreen.CustomerProfile
                    is NavItem.Segments -> currentScreen is CrmScreen.Segments
                    is NavItem.Reminders -> currentScreen is CrmScreen.Reminders
                }

                val badgeText = when (item) {
                    is NavItem.Customers -> customerCount.toString()
                    is NavItem.Reminders -> if (reminderDueCount > 0) reminderDueCount.toString() else null
                    is NavItem.Segments -> segmentCount.toString()
                    else -> null
                }

                val isAlert = item is NavItem.Reminders && reminderDueCount > 0

                val bg = if (isSelected) Indigo50 else Color.Transparent
                val contentColor = if (isSelected) Indigo600 else TextSlate700
                val icon = if (isSelected) item.selectedIcon else item.unselectedIcon

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onNavigate(item.screen) }
                        .testTag(item.testTag),
                    shape = RoundedCornerShape(8.dp),
                    color = bg
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = item.title,
                                tint = contentColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = item.title,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = contentColor
                            )
                        }

                        if (badgeText != null) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isAlert) AlertOverdue else if (isSelected) Indigo600 else TealContainer
                            ) {
                                Text(
                                    text = badgeText,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isAlert || isSelected) Color.White else TealText,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Divider(color = BorderSlate200)
            Spacer(modifier = Modifier.height(12.dp))

            // User Profile Card & Logout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    CustomerAvatar(name = session.userName, size = 34.dp)
                    Column {
                        Text(
                            text = session.userName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSlate900,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = session.userRole,
                            fontSize = 10.sp,
                            color = TextSlate500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                IconButton(
                    onClick = onLogout,
                    modifier = Modifier.size(32.dp).testTag("sidebar_logout_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Sign Out",
                        tint = TextSlate500,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobileTopBar(
    title: String,
    onAddCustomer: () -> Unit,
    onLogout: () -> Unit,
    userSession: UserSession,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        modifier = modifier.testTag("mobile_top_bar"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Indigo600),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CorporateFare,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Column {
                    Text(
                        text = "ClientBase",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSlate900
                    )
                    Text(
                        text = title,
                        fontSize = 11.sp,
                        color = AccentTeal,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = onAddCustomer,
                modifier = Modifier.testTag("mobile_top_add_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Customer",
                    tint = Indigo600
                )
            }
            Box {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.testTag("mobile_menu_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu",
                        tint = TextSlate700
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    text = userSession.userName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = userSession.userRole,
                                    fontSize = 11.sp,
                                    color = TextSlate500
                                )
                            }
                        },
                        onClick = {}
                    )
                    Divider()
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Logout,
                                    contentDescription = null,
                                    tint = AlertOverdue,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text("Sign Out", color = AlertOverdue)
                            }
                        },
                        onClick = {
                            menuExpanded = false
                            onLogout()
                        }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun MobileBottomNav(
    currentScreen: CrmScreen,
    onNavigate: (CrmScreen) -> Unit,
    customerCount: Int,
    reminderDueCount: Int,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.testTag("mobile_bottom_nav"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        navItems.forEach { item ->
            val isSelected = when (item) {
                is NavItem.Dashboard -> currentScreen is CrmScreen.Dashboard
                is NavItem.Customers -> currentScreen is CrmScreen.CustomerList || currentScreen is CrmScreen.CustomerProfile
                is NavItem.Segments -> currentScreen is CrmScreen.Segments
                is NavItem.Reminders -> currentScreen is CrmScreen.Reminders
            }

            val badgeText = when (item) {
                is NavItem.Reminders -> if (reminderDueCount > 0) reminderDueCount.toString() else null
                else -> null
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.screen) },
                icon = {
                    if (badgeText != null) {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = AlertOverdue) {
                                    Text(badgeText, color = Color.White)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        }
                    } else {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title
                        )
                    }
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Indigo600,
                    selectedTextColor = Indigo600,
                    indicatorColor = Indigo50,
                    unselectedIconColor = TextSlate500,
                    unselectedTextColor = TextSlate500
                ),
                modifier = Modifier.testTag("bottom_nav_${item.title.lowercase()}")
            )
        }
    }
}
