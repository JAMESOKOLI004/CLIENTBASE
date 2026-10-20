package com.example.clientbase.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.clientbase.data.model.Customer
import com.example.ui.theme.AccentTeal
import com.example.ui.theme.BorderSlate200
import com.example.ui.theme.Indigo50
import com.example.ui.theme.Indigo600
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealText
import com.example.ui.theme.TextSlate500
import com.example.ui.theme.TextSlate700
import com.example.ui.theme.TextSlate900
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class InteractionTypeOption(
    val type: String,
    val icon: ImageVector,
    val activeColor: Color,
    val activeBg: Color
)

@Composable
fun AddInteractionDialog(
    customer: Customer,
    onDismiss: () -> Unit,
    onSave: (type: String, dateMillis: Long, description: String, reminderTitle: String?, reminderDueMillis: Long?) -> Unit
) {
    val types = listOf(
        InteractionTypeOption("Call", Icons.Default.Call, Indigo600, Indigo50),
        InteractionTypeOption("Meeting", Icons.Default.Event, AccentTeal, TealContainer),
        InteractionTypeOption("Email", Icons.Default.Email, Color(0xFF2563EB), Color(0xFFDBEAFE)),
        InteractionTypeOption("Note", Icons.Default.NoteAlt, Color(0xFFD97706), Color(0xFFFEF3C7))
    )

    var selectedType by remember { mutableStateOf("Call") }
    var description by remember { mutableStateOf("") }
    var interactionDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }

    var createFollowUp by remember { mutableStateOf(false) }
    var followUpTitle by remember { mutableStateOf("Follow up regarding call discussion") }
    var followUpDaysAhead by remember { mutableStateOf(3) }

    val formattedDate = remember(interactionDateMillis) {
        val sdf = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault())
        sdf.format(Date(interactionDateMillis))
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("add_interaction_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate200),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Dialog Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Log Interaction",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSlate900
                        )
                        Text(
                            text = "For ${customer.name} • ${customer.company}",
                            fontSize = 12.sp,
                            color = TextSlate500
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSlate500
                        )
                    }
                }

                Divider(color = BorderSlate200)

                // Type Selector (Call, Meeting, Email, Note)
                Text(
                    text = "Interaction Type",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSlate700
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    types.forEach { opt ->
                        val isSelected = selectedType == opt.type
                        val bg = if (isSelected) opt.activeBg else Color(0xFFF8FAFC)
                        val border = if (isSelected) opt.activeColor else BorderSlate200
                        val textColor = if (isSelected) opt.activeColor else TextSlate700

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    selectedType = opt.type
                                    followUpTitle = when (opt.type) {
                                        "Call" -> "Follow up regarding phone conversation"
                                        "Meeting" -> "Send meeting recap and next steps"
                                        "Email" -> "Check on email response"
                                        else -> "Follow up on customer notes"
                                    }
                                }
                                .testTag("interaction_type_${opt.type.lowercase()}"),
                            shape = RoundedCornerShape(10.dp),
                            color = bg,
                            border = androidx.compose.foundation.BorderStroke(if (isSelected) 1.5.dp else 1.dp, border)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = opt.icon,
                                    contentDescription = opt.type,
                                    tint = textColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = opt.type,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = textColor
                                )
                            }
                        }
                    }
                }

                // Date stamp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Timestamp",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSlate700
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Indigo50
                    ) {
                        Text(
                            text = formattedDate,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Indigo600,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Description Field
                Column {
                    Text(
                        text = "Notes & Summary",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSlate700
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("interaction_description_input"),
                        placeholder = { Text("Summarize discussion points, agreements, customer questions...") },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Indigo600,
                            unfocusedBorderColor = BorderSlate200
                        )
                    )
                }

                // Optional Follow-up Reminder Checkbox
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF8FAFC),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate200),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { createFollowUp = !createFollowUp }
                        ) {
                            Checkbox(
                                checked = createFollowUp,
                                onCheckedChange = { createFollowUp = it },
                                colors = CheckboxDefaults.colors(checkedColor = Indigo600)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Schedule Follow-up Reminder",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSlate900
                            )
                        }

                        if (createFollowUp) {
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = followUpTitle,
                                onValueChange = { followUpTitle = it },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Indigo600,
                                    unfocusedBorderColor = BorderSlate200
                                )
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Due in:", fontSize = 11.sp, color = TextSlate500)
                                listOf(1 to "Tomorrow", 3 to "3 days", 7 to "1 week").forEach { (d, label) ->
                                    val isCur = followUpDaysAhead == d
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isCur) Indigo600 else Indigo50,
                                        modifier = Modifier.clickable { followUpDaysAhead = d }
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = if (isCur) Color.White else Indigo600,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Action buttons: Cancel & Save
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate200)
                    ) {
                        Text("Cancel", color = TextSlate700, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (description.isNotBlank()) {
                                val reminderDue = if (createFollowUp) {
                                    System.currentTimeMillis() + (followUpDaysAhead * 24 * 3600 * 1000L)
                                } else null
                                val reminderText = if (createFollowUp) followUpTitle.trim() else null

                                onSave(selectedType, interactionDateMillis, description.trim(), reminderText, reminderDue)
                            }
                        },
                        modifier = Modifier.testTag("save_interaction_btn"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Indigo600,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Save Interaction", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
