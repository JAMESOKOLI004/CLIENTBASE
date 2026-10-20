package com.example.clientbase.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.clientbase.data.model.Interaction
import kotlinx.coroutines.flow.Flow

@Dao
interface InteractionDao {
    @Query("SELECT * FROM interactions ORDER BY dateMillis DESC")
    fun getAllInteractions(): Flow<List<Interaction>>

    @Query("SELECT * FROM interactions WHERE customerId = :customerId ORDER BY dateMillis DESC")
    fun getInteractionsForCustomer(customerId: Long): Flow<List<Interaction>>

    @Query("SELECT * FROM interactions ORDER BY dateMillis DESC LIMIT :limit")
    fun getRecentInteractions(limit: Int = 10): Flow<List<Interaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInteraction(interaction: Interaction): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInteractions(interactions: List<Interaction>): List<Long>

    @Delete
    suspend fun deleteInteraction(interaction: Interaction)

    @Query("DELETE FROM interactions WHERE id = :id")
    suspend fun deleteInteractionById(id: Long)

    @Query("DELETE FROM interactions WHERE customerId = :customerId")
    suspend fun deleteInteractionsForCustomer(customerId: Long)
}
