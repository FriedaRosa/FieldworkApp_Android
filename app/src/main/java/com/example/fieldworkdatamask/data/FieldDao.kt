package com.example.fieldworkdatamask.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FieldDao {
    @Query("SELECT * FROM field_entries ORDER BY date DESC, time DESC")
    fun getAllEntries(): Flow<List<FieldEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: FieldEntryEntity)

    @Delete
    suspend fun delete(entry: FieldEntryEntity)

    @Query("DELETE FROM field_entries")
    suspend fun deleteAll()
}
