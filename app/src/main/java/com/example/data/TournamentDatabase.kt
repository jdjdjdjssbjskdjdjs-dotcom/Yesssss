package com.example.data

import androidx.room.*
import com.example.engine.MatchEvent
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey val id: Int,
    val homeTeamId: Int?,
    val awayTeamId: Int?,
    val homeScore: Int = 0,
    val awayScore: Int = 0,
    val isPlayed: Boolean = false,
    val stage: String, // "GROUP_A" .. "GROUP_L", "R32", "R16", "QF", "SF", "THIRD_PLACE", "FINAL"
    val matchIndex: Int, // Order of matches in bracket
    val extraTime: Boolean = false,
    val penalties: Boolean = false,
    val homePenalties: Int = 0,
    val awayPenalties: Int = 0,
    val eventsJson: String = "" // Serialized List<MatchEvent>
)

@Entity(tableName = "tournament_meta")
data class TournamentMetaEntity(
    @PrimaryKey val id: Int = 1,
    val currentStage: String = "NOT_STARTED", // "NOT_STARTED", "GROUP_ROUND_1", "GROUP_ROUND_2", "GROUP_ROUND_3", "R32", "R16", "QF", "SF", "FINAL", "FINISHED"
    val selectedTeamId: Int = -1
)

class Converters {
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val listType = Types.newParameterizedType(List::class.java, MatchEvent::class.java)
    private val adapter = moshi.adapter<List<MatchEvent>>(listType)

    @TypeConverter
    fun fromEventsList(value: List<MatchEvent>?): String {
        return if (value == null) "" else adapter.toJson(value)
    }

    @TypeConverter
    fun toEventsList(value: String?): List<MatchEvent> {
        if (value.isNullOrEmpty()) return emptyList()
        return try {
            adapter.fromJson(value) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}

@Dao
interface TournamentDao {
    @Query("SELECT * FROM matches ORDER BY id ASC")
    fun getAllMatchesFlow(): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches ORDER BY id ASC")
    suspend fun getAllMatches(): List<MatchEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatches(matches: List<MatchEntity>)

    @Update
    suspend fun updateMatch(match: MatchEntity)

    @Query("SELECT * FROM tournament_meta WHERE id = 1")
    fun getTournamentMetaFlow(): Flow<TournamentMetaEntity?>

    @Query("SELECT * FROM tournament_meta WHERE id = 1")
    suspend fun getTournamentMeta(): TournamentMetaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournamentMeta(meta: TournamentMetaEntity)

    @Query("DELETE FROM matches")
    suspend fun clearMatches()

    @Query("DELETE FROM tournament_meta")
    suspend fun clearMeta()
}

@Database(entities = [MatchEntity::class, TournamentMetaEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TournamentDatabase : RoomDatabase() {
    abstract fun tournamentDao(): TournamentDao

    companion object {
        @Volatile
        private var INSTANCE: TournamentDatabase? = null

        fun getDatabase(context: android.content.Context): TournamentDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TournamentDatabase::class.java,
                    "tournament_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
