package com.example.clientbase.data.repository

import com.example.clientbase.data.dao.CustomerDao
import com.example.clientbase.data.dao.InteractionDao
import com.example.clientbase.data.dao.ReminderDao
import com.example.clientbase.data.model.Customer
import com.example.clientbase.data.model.Interaction
import com.example.clientbase.data.model.Reminder
import kotlinx.coroutines.flow.Flow

class CrmRepository(
    private val customerDao: CustomerDao,
    private val interactionDao: InteractionDao,
    private val reminderDao: ReminderDao
) {
    val allCustomers: Flow<List<Customer>> = customerDao.getAllCustomers()
    val allInteractions: Flow<List<Interaction>> = interactionDao.getAllInteractions()
    val recentInteractions: Flow<List<Interaction>> = interactionDao.getRecentInteractions(15)
    val allReminders: Flow<List<Reminder>> = reminderDao.getAllReminders()
    val pendingReminders: Flow<List<Reminder>> = reminderDao.getPendingReminders()

    fun getCustomerById(id: Long): Flow<Customer?> = customerDao.getCustomerById(id)

    suspend fun getCustomerByIdDirect(id: Long): Customer? = customerDao.getCustomerByIdDirect(id)

    fun getInteractionsForCustomer(customerId: Long): Flow<List<Interaction>> =
        interactionDao.getInteractionsForCustomer(customerId)

    fun getRemindersForCustomer(customerId: Long): Flow<List<Reminder>> =
        reminderDao.getRemindersForCustomer(customerId)

    suspend fun saveCustomer(customer: Customer): Long {
        return if (customer.id == 0L) {
            customerDao.insertCustomer(customer)
        } else {
            customerDao.updateCustomer(customer)
            customer.id
        }
    }

    suspend fun deleteCustomer(id: Long) {
        interactionDao.deleteInteractionsForCustomer(id)
        reminderDao.deleteRemindersForCustomer(id)
        customerDao.deleteCustomerById(id)
    }

    suspend fun addInteraction(interaction: Interaction) {
        interactionDao.insertInteraction(interaction)
        customerDao.updateLastContacted(interaction.customerId, interaction.dateMillis)
    }

    suspend fun deleteInteraction(id: Long) {
        interactionDao.deleteInteractionById(id)
    }

    suspend fun saveReminder(reminder: Reminder): Long {
        return if (reminder.id == 0L) {
            reminderDao.insertReminder(reminder)
        } else {
            reminderDao.updateReminder(reminder)
            reminder.id
        }
    }

    suspend fun setReminderCompleted(id: Long, completed: Boolean) {
        reminderDao.setCompleted(id, completed)
    }

    suspend fun deleteReminder(id: Long) {
        reminderDao.deleteReminderById(id)
    }
}
