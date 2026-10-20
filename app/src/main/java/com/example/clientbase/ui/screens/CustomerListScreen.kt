package com.example.clientbase.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clientbase.data.model.Customer
import com.example.clientbase.ui.components.CustomerAvatar
import com.example.clientbase.ui.components.StatusBadge
import com.example.clientbase.ui.components.TagPill
import com.example.clientbase.ui.components.dialPhoneNumber
import com.example.clientbase.ui.components.formatRelativeDate
import com.example.clientbase.ui.components.sendEmail
import com.example.ui.theme.AccentTeal
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

@Composable
fun CustomerListScreen(
    customers: List<Customer>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    statusFilter: String,
    onStatusFilterChange: (String) -> Unit,
    tagFilter: String,
    onTagFilterChange: (String) -> Unit,
    allTags: List<String>,
    selectedCustomerId: Long?,
    onCustomerSelect: (Long) -> Unit,
    onCustomerOpenProfile: (Long) -> Unit,
    onAddCustomer: () -> Unit,
    onAddInteraction: (Customer) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedCustomer = customers.firstOrNull { it.id == selectedCustomerId } ?: customers.firstOrNull()

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .testTag("customer_list_screen")
    ) {
        val isDesktopHybrid = maxWidth >= 840.dp

        Row(modifier = Modifier.fillMaxSize()) {
            // Main Customer List / Table Area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // Header with Title and Add Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Customer Directory",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSlate900
                        )
                        Text(
                            text = "${customers.size} contacts found",
                            fontSize = 13.sp,
                            color = TextSlate500
                        )
                    }

                    Button(
                        onClick = onAddCustomer,
                        modifier = Modifier.testTag("customer_list_add_btn"),
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

                Spacer(modifier = Modifier.height(14.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("customer_search_input"),
                    placeholder = { Text("Search by name, company, email, or phone...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = TextSlate400,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search",
                                    tint = TextSlate400,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Indigo600,
                        unfocusedBorderColor = BorderSlate200,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Filter Chips Row
                val statuses = listOf("All", "Active", "Lead", "Opportunity", "Inactive")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Status:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSlate400
                    )

                    statuses.forEach { st ->
                        val selected = statusFilter.equals(st, ignoreCase = true)
                        FilterChip(
                            selected = selected,
                            onClick = { onStatusFilterChange(st) },
                            label = { Text(st, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Indigo50,
                                selectedLabelColor = Indigo700
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selected,
                                selectedBorderColor = Indigo600,
                                borderColor = BorderSlate200
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("filter_status_$st")
                        )
                    }

                    if (tagFilter != "All") {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = TealContainer,
                            modifier = Modifier.clickable { onTagFilterChange("All") }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Tag: #$tagFilter",
                                    color = TealText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Remove tag filter",
                                    tint = TealText,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Customer Content: Table on Desktop or Cards on Mobile
                if (customers.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = TextSlate400,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No matching customers found",
                                fontWeight = FontWeight.Bold,
                                color = TextSlate700,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Try clearing filters or add a new customer",
                                color = TextSlate500,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else if (isDesktopHybrid) {
                    // Desktop Table View
                    DesktopCustomerTable(
                        customers = customers,
                        selectedCustomerId = selectedCustomer?.id,
                        onRowClick = { cust -> onCustomerSelect(cust.id) },
                        onOpenProfile = { cust -> onCustomerOpenProfile(cust.id) },
                        onCall = { phone -> dialPhoneNumber(context, phone) },
                        onEmail = { email -> sendEmail(context, email) }
                    )
                } else {
                    // Mobile Card List
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(customers, key = { it.id }) { customer ->
                            MobileCustomerCard(
                                customer = customer,
                                onClick = { onCustomerOpenProfile(customer.id) },
                                onCall = { dialPhoneNumber(context, customer.phone) },
                                onEmail = { sendEmail(context, customer.email) }
                            )
                        }
                    }
                }
            }

            // Desktop Right Side Profile-Card Preview (Hybrid Pane)
            if (isDesktopHybrid && selectedCustomer != null) {
                Divider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp),
                    color = BorderSlate200
                )
                DesktopProfilePreviewCard(
                    customer = selectedCustomer,
                    onOpenFullProfile = { onCustomerOpenProfile(selectedCustomer.id) },
                    onAddInteraction = { onAddInteraction(selectedCustomer) },
                    onCall = { dialPhoneNumber(context, selectedCustomer.phone) },
                    onEmail = { sendEmail(context, selectedCustomer.email) },
                    modifier = Modifier
                        .width(360.dp)
                        .fillMaxHeight()
                )
            }
        }
    }
}

@Composable
fun DesktopCustomerTable(
    customers: List<Customer>,
    selectedCustomerId: Long?,
    onRowClick: (Customer) -> Unit,
    onOpenProfile: (Customer) -> Unit,
    onCall: (String) -> Unit,
    onEmail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxSize()
            .testTag("desktop_customer_table"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate200)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Table Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Indigo50.copy(alpha = 0.5f))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CUSTOMER",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSlate500,
                    modifier = Modifier.weight(2f)
                )
                Text(
                    text = "COMPANY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSlate500,
                    modifier = Modifier.weight(1.6f)
                )
                Text(
                    text = "TAGS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSlate500,
                    modifier = Modifier.weight(1.8f)
                )
                Text(
                    text = "LAST CONTACT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSlate500,
                    modifier = Modifier.weight(1.2f)
                )
                Text(
                    text = "STATUS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSlate500,
                    modifier = Modifier.weight(1.1f)
                )
                Text(
                    text = "ACTIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSlate500,
                    modifier = Modifier.width(90.dp)
                )
            }

            Divider(color = BorderSlate200)

            // Table Rows
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(customers, key = { it.id }) { customer ->
                    val isSelected = customer.id == selectedCustomerId
                    val rowBg = if (isSelected) Indigo50 else Color.Transparent

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(rowBg)
                            .clickable { onRowClick(customer) }
                            .padding(horizontal = 16.dp, vertical = 11.dp)
                            .testTag("table_row_${customer.id}"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Name & Avatar
                        Row(
                            modifier = Modifier.weight(2f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CustomerAvatar(name = customer.name, size = 32.dp)
                            Column {
                                Text(
                                    text = customer.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSlate900,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = customer.email,
                                    fontSize = 11.sp,
                                    color = TextSlate500,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Company
                        Text(
                            text = customer.company,
                            fontSize = 13.sp,
                            color = TextSlate700,
                            modifier = Modifier.weight(1.6f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Tags
                        Row(
                            modifier = Modifier.weight(1.8f),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            customer.getTagList().take(2).forEach { tag ->
                                TagPill(tag = tag)
                            }
                            if (customer.getTagList().size > 2) {
                                Text(
                                    text = "+${customer.getTagList().size - 2}",
                                    fontSize = 10.sp,
                                    color = TextSlate400,
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            }
                        }

                        // Last Contact
                        Text(
                            text = formatRelativeDate(customer.lastContactedAt),
                            fontSize = 12.sp,
                            color = TextSlate500,
                            modifier = Modifier.weight(1.2f)
                        )

                        // Status Badge
                        Box(modifier = Modifier.weight(1.1f)) {
                            StatusBadge(status = customer.status)
                        }

                        // Actions
                        Row(
                            modifier = Modifier.width(90.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            IconButton(
                                onClick = { onCall(customer.phone) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Call",
                                    tint = Indigo600,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            IconButton(
                                onClick = { onEmail(customer.email) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Email",
                                    tint = AccentTeal,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            IconButton(
                                onClick = { onOpenProfile(customer) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OpenInNew,
                                    contentDescription = "View Profile",
                                    tint = TextSlate700,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    Divider(color = BorderSlate200.copy(alpha = 0.6f))
                }
            }
        }
    }
}

@Composable
fun DesktopProfilePreviewCard(
    customer: Customer,
    onOpenFullProfile: () -> Unit,
    onAddInteraction: () -> Unit,
    onCall: () -> Unit,
    onEmail: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(18.dp)
            .testTag("desktop_profile_preview_card"),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CUSTOMER CARD",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSlate400,
                    letterSpacing = 1.sp
                )

                StatusBadge(status = customer.status)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Avatar & Name Card
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                CustomerAvatar(name = customer.name, size = 52.dp)
                Column {
                    Text(
                        text = customer.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSlate900
                    )
                    Text(
                        text = customer.company,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Indigo600
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Quick Call & Email Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onCall,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Indigo600,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Call", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = onEmail,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TealContainer,
                        contentColor = TealText
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Email, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Email", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            Divider(color = BorderSlate200)
            Spacer(modifier = Modifier.height(14.dp))

            // Contact Info Fields
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ContactField(label = "PHONE", value = customer.phone)
                ContactField(label = "EMAIL", value = customer.email)
                ContactField(label = "ADDRESS", value = customer.address)
                ContactField(label = "LAST CONTACTED", value = formatRelativeDate(customer.lastContactedAt))
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tags
            Text(
                text = "SEGMENTS & TAGS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextSlate400,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                customer.getTagList().forEach { tag ->
                    TagPill(tag = tag)
                }
            }

            if (customer.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "EXECUTIVE NOTES",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSlate400,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Indigo50.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = customer.notes,
                        fontSize = 12.sp,
                        color = TextSlate700,
                        modifier = Modifier.padding(10.dp),
                        lineHeight = 17.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action Buttons: + Add Interaction and View Full Profile
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onAddInteraction,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate200)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Interaction", fontSize = 12.sp)
                }

                Button(
                    onClick = onOpenFullProfile,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Indigo700,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Full Profile →", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ContactField(label: String, value: String) {
    if (value.isBlank()) return
    Column {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = TextSlate400,
            letterSpacing = 0.5.sp
        )
        Text(
            text = value,
            fontSize = 12.sp,
            color = TextSlate900,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun MobileCustomerCard(
    customer: Customer,
    onClick: () -> Unit,
    onCall: () -> Unit,
    onEmail: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("customer_card_${customer.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate200),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    CustomerAvatar(name = customer.name, size = 40.dp)
                    Column {
                        Text(
                            text = customer.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSlate900
                        )
                        Text(
                            text = customer.company,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Indigo600
                        )
                    }
                }
                StatusBadge(status = customer.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                customer.getTagList().take(3).forEach { tag ->
                    TagPill(tag = tag)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = BorderSlate200.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Contacted: ${formatRelativeDate(customer.lastContactedAt)}",
                    fontSize = 11.sp,
                    color = TextSlate500
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                        onClick = onCall,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Call, contentDescription = "Call", tint = Indigo600, modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = onEmail,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Email, contentDescription = "Email", tint = AccentTeal, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
