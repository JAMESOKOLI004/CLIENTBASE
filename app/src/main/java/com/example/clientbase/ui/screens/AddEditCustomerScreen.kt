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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clientbase.data.model.Customer
import com.example.ui.theme.AccentTeal
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditCustomerScreen(
    initialCustomer: Customer?,
    onSave: (Customer) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf(initialCustomer?.name ?: "") }
    var company by remember { mutableStateOf(initialCustomer?.company ?: "") }
    var phone by remember { mutableStateOf(initialCustomer?.phone ?: "") }
    var email by remember { mutableStateOf(initialCustomer?.email ?: "") }
    var address by remember { mutableStateOf(initialCustomer?.address ?: "") }
    var status by remember { mutableStateOf(initialCustomer?.status ?: "Active") }
    var notes by remember { mutableStateOf(initialCustomer?.notes ?: "") }

    var statusExpanded by remember { mutableStateOf(false) }
    val statusOptions = listOf("Active", "Lead", "Opportunity", "Inactive")

    val selectedTags = remember {
        mutableStateListOf<String>().apply {
            if (initialCustomer != null) {
                addAll(initialCustomer.getTagList())
            } else {
                addAll(listOf("Enterprise"))
            }
        }
    }

    val suggestedTags = listOf("Enterprise", "VIP", "SaaS", "Partner", "Referral", "High Value", "Healthcare", "Fintech")
    var customTagInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isEditing = initialCustomer != null

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundSlate)
            .testTag("add_edit_customer_screen"),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 760.dp)
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onCancel,
                        modifier = Modifier.testTag("customer_form_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextSlate700
                        )
                    }
                    Column {
                        Text(
                            text = if (isEditing) "Edit Customer Profile" else "Create New Customer",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSlate900
                        )
                        Text(
                            text = "Record customer contact information, tags, and account notes",
                            fontSize = 12.sp,
                            color = TextSlate500
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Main Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate200),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Full Name & Company (2 columns if desktop)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Full Name *",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSlate700
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = name,
                                onValueChange = {
                                    name = it
                                    errorMessage = null
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("customer_name_input"),
                                placeholder = { Text("e.g. Elena Vance") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Indigo600,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Indigo600,
                                    unfocusedBorderColor = BorderSlate200
                                )
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Company Name *",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSlate700
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = company,
                                onValueChange = {
                                    company = it
                                    errorMessage = null
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("customer_company_input"),
                                placeholder = { Text("e.g. Starlight Technologies") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Business,
                                        contentDescription = null,
                                        tint = Indigo600,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Indigo600,
                                    unfocusedBorderColor = BorderSlate200
                                )
                            )
                        }
                    }

                    // Phone & Email
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Phone Number",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSlate700
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("customer_phone_input"),
                                placeholder = { Text("+1 (415) 555-0199") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = null,
                                        tint = Indigo600,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Indigo600,
                                    unfocusedBorderColor = BorderSlate200
                                )
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Email Address",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSlate700
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("customer_email_input"),
                                placeholder = { Text("contact@company.com") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = null,
                                        tint = Indigo600,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Indigo600,
                                    unfocusedBorderColor = BorderSlate200
                                )
                            )
                        }
                    }

                    // Address
                    Column {
                        Text(
                            text = "Address",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSlate700
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("customer_address_input"),
                            placeholder = { Text("Street Address, City, State, Country") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Indigo600,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Indigo600,
                                unfocusedBorderColor = BorderSlate200
                            )
                        )
                    }

                    // Status Dropdown
                    Column {
                        Text(
                            text = "Pipeline Status",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSlate700
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        ExposedDropdownMenuBox(
                            expanded = statusExpanded,
                            onExpandedChange = { statusExpanded = !statusExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = status,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                                    .testTag("customer_status_dropdown"),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Indigo600,
                                    unfocusedBorderColor = BorderSlate200
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = statusExpanded,
                                onDismissRequest = { statusExpanded = false }
                            ) {
                                statusOptions.forEach { opt ->
                                    DropdownMenuItem(
                                        text = { Text(opt) },
                                        onClick = {
                                            status = opt
                                            statusExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Divider(color = BorderSlate200)

                    // Tags / Segments Section
                    Column {
                        Text(
                            text = "Segments & Tags",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSlate700
                        )
                        Text(
                            text = "Assign tags to organize into customer segments",
                            fontSize = 11.sp,
                            color = TextSlate500
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Selected tags pills
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            selectedTags.forEach { tag ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = TealContainer,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentTeal.copy(alpha = 0.3f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "#$tag",
                                            color = TealText,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Remove tag",
                                            tint = TealText,
                                            modifier = Modifier
                                                .size(14.dp)
                                                .clickable { selectedTags.remove(tag) }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Suggested Tag Chips
                        Text(
                            text = "Quick add suggestions:",
                            fontSize = 11.sp,
                            color = TextSlate400
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            suggestedTags.filter { !selectedTags.contains(it) }.forEach { suggestion ->
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Indigo50,
                                    modifier = Modifier.clickable { selectedTags.add(suggestion) }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            tint = Indigo600,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = suggestion,
                                            fontSize = 11.sp,
                                            color = Indigo700,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Custom Tag input
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = customTagInput,
                                onValueChange = { customTagInput = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("custom_tag_input"),
                                placeholder = { Text("Add custom tag...") },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Indigo600,
                                    unfocusedBorderColor = BorderSlate200
                                )
                            )
                            Button(
                                onClick = {
                                    val trimmed = customTagInput.trim()
                                    if (trimmed.isNotEmpty() && !selectedTags.contains(trimmed)) {
                                        selectedTags.add(trimmed)
                                        customTagInput = ""
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
                            ) {
                                Text("Add", fontSize = 12.sp)
                            }
                        }
                    }

                    Divider(color = BorderSlate200)

                    // Notes Field
                    Column {
                        Text(
                            text = "Customer Notes",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSlate700
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .testTag("customer_notes_input"),
                            placeholder = { Text("Account background, decision makers, budget notes, key milestones...") },
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Indigo600,
                                unfocusedBorderColor = BorderSlate200
                            )
                        )
                    }

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Save and Cancel Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onCancel,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate200)
                        ) {
                            Text("Cancel", color = TextSlate700)
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Button(
                            onClick = {
                                if (name.isBlank()) {
                                    errorMessage = "Please enter customer name"
                                    return@Button
                                }
                                if (company.isBlank()) {
                                    errorMessage = "Please enter company name"
                                    return@Button
                                }

                                val saved = (initialCustomer ?: Customer(
                                    name = name.trim(),
                                    company = company.trim(),
                                    email = email.trim(),
                                    phone = phone.trim(),
                                    address = address.trim(),
                                    tags = selectedTags.joinToString(", "),
                                    status = status,
                                    notes = notes.trim()
                                )).copy(
                                    name = name.trim(),
                                    company = company.trim(),
                                    email = email.trim(),
                                    phone = phone.trim(),
                                    address = address.trim(),
                                    tags = selectedTags.joinToString(", "),
                                    status = status,
                                    notes = notes.trim()
                                )

                                onSave(saved)
                            },
                            modifier = Modifier.testTag("save_customer_submit_btn"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Indigo600,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isEditing) "Save Changes" else "Create Customer",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
