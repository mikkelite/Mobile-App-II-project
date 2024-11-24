package com.example.project300352053.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Entity(tableName = "entries")
data class Entry(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "amount") val amount: Int,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "dateTime") val dateTime: String
)

@Dao
interface EntryDao {
    @Query("SELECT * FROM entries")
    suspend fun getAll(): List<Entry>

    @Insert
    fun insertAll(vararg entries: Entry)


}

@Database(entities = [Entry::class], version = 2,exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun EntryDao(): EntryDao

}


