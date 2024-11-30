package com.example.project300352053.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.DeleteTable
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

@Entity(tableName = "entries")
data class Entry(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "amount") val amount: Int,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "dateTime") val dateTime: String
)
@Entity(tableName = "Accounts")
data class Account(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "Food") val Food: Int,
    @ColumnInfo(name = "Utility") val Utility: Int,
    @ColumnInfo(name = "Recreation") val Recreation: Int,
    @ColumnInfo(name = "Transportation") val Transportation: Int,
    @ColumnInfo(name = "Healthcare") val Healthcare: Int,
    @ColumnInfo(name = "Investments") val Investments: Int,
    @ColumnInfo(name = "Personal Care") val PersonalCare: Int,
    @ColumnInfo(name = "MIscellaneous") val MIscellaneous: Int

)



@Dao
interface EntryDao {
    @Query("SELECT * FROM entries")
    suspend fun getAll(): List<Entry>

    @Insert
    fun insertAll(vararg entries: Entry)
    @Query("DELETE FROM entries")
    suspend fun nuketable()



}
@Dao
interface AccountDao {
    @Insert
    suspend fun insert(account: Account)

    @Query("SELECT * FROM Accounts")
    suspend fun getAllAccounts():List<Account>
    @Update()
    fun update(account: Account)



}

@Database(entities = [Entry::class,Account::class], version = 3,exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun EntryDao(): EntryDao
    abstract fun AccountDao(): AccountDao
    companion object {
        val MIGRATION_1_2 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `Accounts` (`uid` INTEGER NOT NULL, `Food` INTEGER NOT NULL, " +
                            "`Utility` INTEGER NOT NULL, `Recreation` INTEGER NOT NULL, `Transportation` INTEGER NOT NULL," +
                            " `Healthcare` INTEGER NOT NULL, `Investments` INTEGER NOT NULL, `Personal Care` INTEGER NOT NULL, " +
                            "`MIscellaneous` INTEGER NOT NULL, PRIMARY KEY(`uid`))"
                )
            }
        }
    }

}


