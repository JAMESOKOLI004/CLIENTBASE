package com.example.clientbase.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.clientbase.data.dao.CustomerDao
import com.example.clientbase.data.dao.InteractionDao
import com.example.clientbase.data.dao.ReminderDao
import com.example.clientbase.data.model.Customer
import com.example.clientbase.data.model.Interaction
import com.example.clientbase.data.model.Reminder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Customer::class, Interaction::class, Reminder::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun interactionDao(): InteractionDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "clientbase_db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: AppDatabase) {
            val customerDao = database.customerDao()
            val interactionDao = database.interactionDao()
            val reminderDao = database.reminderDao()

            val now = System.currentTimeMillis()
            val hour = 3600 * 1000L
            val day = 24 * hour

            val customers = listOf(
                Customer(
                    name = "Elena Vance",
                    company = "Starlight Technologies",
                    email = "elena.vance@starlighttech.io",
                    phone = "+1 (415) 890-2134",
                    address = "450 Mission St, San Francisco, CA",
                    tags = "Enterprise, SaaS, High Value",
                    status = "Active",
                    notes = "Evaluating multi-seat enterprise expansion for Q4. Primary technical decision maker.",
                    createdAt = now - (18 * day),
                    lastContactedAt = now - (2 * hour)
                ),
                Customer(
                    name = "Marcus Thorne",
                    company = "Quantum Dynamics",
                    email = "m.thorne@quantumdynamics.com",
                    phone = "+1 (212) 555-7891",
                    address = "1251 Ave of the Americas, New York, NY",
                    tags = "Enterprise, VIP",
                    status = "Active",
                    notes = "Signed 3-year master services agreement. Needs quarterly executive ROI reviews.",
                    createdAt = now - (45 * day),
                    lastContactedAt = now - (1 * day)
                ),
                Customer(
                    name = "Sophia Reynolds",
                    company = "Vanguard Health Partners",
                    email = "sreynolds@vanguardhealth.org",
                    phone = "+1 (312) 420-9988",
                    address = "300 N Michigan Ave, Chicago, IL",
                    tags = "Healthcare, Compliance, VIP",
                    status = "Opportunity",
                    notes = "Requires HIPAA compliance addendum before final sign-off.",
                    createdAt = now - (7 * day),
                    lastContactedAt = now - (3 * day)
                ),
                Customer(
                    name = "David Chen",
                    company = "Nexus Financial Systems",
                    email = "david@nexusfin.com",
                    phone = "+1 (650) 334-1122",
                    address = "2100 University Ave, Palo Alto, CA",
                    tags = "Fintech, SaaS, Early Adopter",
                    status = "Active",
                    notes = "Fast-moving Series B fintech. Expanding sales team from 10 to 45.",
                    createdAt = now - (12 * day),
                    lastContactedAt = now - (5 * hour)
                ),
                Customer(
                    name = "Amara Okafor",
                    company = "Acro Logistics Corp",
                    email = "amara.o@acrologistics.com",
                    phone = "+1 (404) 789-4321",
                    address = "800 Peachtree St NE, Atlanta, GA",
                    tags = "Enterprise, Partner",
                    status = "Lead",
                    notes = "Met at Supply Chain Summit. Interested in automated workflow dispatch.",
                    createdAt = now - (4 * day),
                    lastContactedAt = now - (4 * day)
                ),
                Customer(
                    name = "Liam Gallagher",
                    company = "Apex Cloud Infrastructure",
                    email = "liam.g@apexcloud.io",
                    phone = "+1 (206) 880-9911",
                    address = "500 Pike St, Seattle, WA",
                    tags = "SaaS, Referral, High Value",
                    status = "Active",
                    notes = "Referred by Marcus Thorne. Platform integration went live last week.",
                    createdAt = now - (25 * day),
                    lastContactedAt = now - (1 * day)
                ),
                Customer(
                    name = "Chloe Martin",
                    company = "Solstice Media Group",
                    email = "chloe@solsticemedia.com",
                    phone = "+1 (305) 670-3344",
                    address = "1111 Brickell Ave, Miami, FL",
                    tags = "Media, Lead, Referral",
                    status = "Lead",
                    notes = "Inbound inquiry via website. Scheduled introductory demo for marketing automation.",
                    createdAt = now - (2 * day),
                    lastContactedAt = now - (2 * day)
                ),
                Customer(
                    name = "Nathaniel Brooks",
                    company = "Beacon Harbor Capital",
                    email = "nbrooks@beaconharbor.com",
                    phone = "+1 (617) 990-5544",
                    address = "100 Federal St, Boston, MA",
                    tags = "Finance, VIP, Enterprise",
                    status = "Active",
                    notes = "Evaluating portfolio-wide deployment across 6 subsidiaries.",
                    createdAt = now - (35 * day),
                    lastContactedAt = now - (6 * day)
                )
            )

            val insertedIds = customerDao.insertCustomers(customers)
            if (insertedIds.size >= 8) {
                val cElena = insertedIds[0]
                val cMarcus = insertedIds[1]
                val cSophia = insertedIds[2]
                val cDavid = insertedIds[3]
                val cAmara = insertedIds[4]
                val cLiam = insertedIds[5]
                val cChloe = insertedIds[6]
                val cNathaniel = insertedIds[7]

                val interactions = listOf(
                    Interaction(
                        customerId = cElena,
                        customerName = "Elena Vance",
                        type = "Call",
                        dateMillis = now - (2 * hour),
                        description = "30-min strategy review on SSO integration and role-based permissions. Sent updated security whitepaper."
                    ),
                    Interaction(
                        customerId = cDavid,
                        customerName = "David Chen",
                        type = "Meeting",
                        dateMillis = now - (5 * hour),
                        description = "Executive product roadmap alignment. David confirmed onboarding 15 new sales engineers next month."
                    ),
                    Interaction(
                        customerId = cMarcus,
                        customerName = "Marcus Thorne",
                        type = "Email",
                        dateMillis = now - (1 * day),
                        description = "Sent signed SLA confirmation and scheduled Q4 quarterly business review."
                    ),
                    Interaction(
                        customerId = cLiam,
                        customerName = "Liam Gallagher",
                        type = "Call",
                        dateMillis = now - (1 * day),
                        description = "Quick check-in call post rollout. Feedback on API speed was overwhelmingly positive."
                    ),
                    Interaction(
                        customerId = cChloe,
                        customerName = "Chloe Martin",
                        type = "Meeting",
                        dateMillis = now - (2 * day),
                        description = "Initial product demo highlighting automated lead scoring and segment filtering."
                    ),
                    Interaction(
                        customerId = cSophia,
                        customerName = "Sophia Reynolds",
                        type = "Note",
                        dateMillis = now - (3 * day),
                        description = "Legal team forwarded the standard BAA agreement. Needs internal VP sign-off before Friday."
                    ),
                    Interaction(
                        customerId = cAmara,
                        customerName = "Amara Okafor",
                        type = "Email",
                        dateMillis = now - (4 * day),
                        description = "Followed up with enterprise pricing tier breakdown and case studies from peer logistics firms."
                    ),
                    Interaction(
                        customerId = cNathaniel,
                        customerName = "Nathaniel Brooks",
                        type = "Call",
                        dateMillis = now - (6 * day),
                        description = "Discussed portfolio pricing model for 6 operating units. Scheduled deck presentation."
                    )
                )
                interactionDao.insertInteractions(interactions)

                val reminders = listOf(
                    Reminder(
                        customerId = cSophia,
                        customerName = "Sophia Reynolds",
                        title = "Follow up on BAA agreement and HIPAA sign-off",
                        dueMillis = now - (1 * day), // Overdue
                        isCompleted = false,
                        priority = "High"
                    ),
                    Reminder(
                        customerId = cElena,
                        customerName = "Elena Vance",
                        title = "Deliver enterprise quote with 50-seat tiered discount",
                        dueMillis = now + (3 * hour), // Today
                        isCompleted = false,
                        priority = "High"
                    ),
                    Reminder(
                        customerId = cChloe,
                        customerName = "Chloe Martin",
                        title = "Send trial sandbox invite and video walkthrough",
                        dueMillis = now + (8 * hour), // Today
                        isCompleted = false,
                        priority = "Medium"
                    ),
                    Reminder(
                        customerId = cAmara,
                        customerName = "Amara Okafor",
                        title = "Schedule deep-dive architecture demo with IT director",
                        dueMillis = now + (2 * day),
                        isCompleted = false,
                        priority = "Medium"
                    ),
                    Reminder(
                        customerId = cMarcus,
                        customerName = "Marcus Thorne",
                        title = "Prepare Q4 executive scorecard presentation",
                        dueMillis = now + (5 * day),
                        isCompleted = false,
                        priority = "Low"
                    ),
                    Reminder(
                        customerId = cDavid,
                        customerName = "David Chen",
                        title = "Verify API rate limit upgrade completed in staging",
                        dueMillis = now - (3 * day),
                        isCompleted = true,
                        priority = "Medium"
                    )
                )
                reminderDao.insertReminders(reminders)
            }
        }
    }
}
