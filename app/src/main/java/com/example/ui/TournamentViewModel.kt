package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.engine.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class TeamStanding(
    val team: Team,
    val played: Int = 0,
    val won: Int = 0,
    val drawn: Int = 0,
    val lost: Int = 0,
    val gf: Int = 0, // goals for
    val ga: Int = 0, // goals against
    val gd: Int = 0, // goal difference
    val points: Int = 0
)

data class PlayerStatRow(
    val playerNameEn: String,
    val playerNameFa: String,
    val teamFa: String,
    val teamFlag: String,
    val count: Int,
    val teamNameEn: String? = null
)

class TournamentViewModel(application: Application) : AndroidViewModel(application) {

    private val database = TournamentDatabase.getDatabase(application)
    private val dao = database.tournamentDao()

    // Exposed Flows
    val allMatches: StateFlow<List<MatchEntity>> = dao.getAllMatchesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tournamentMeta: StateFlow<TournamentMetaEntity?> = dao.getTournamentMetaFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // UI state for Live Simulation
    private val _liveSimState = MutableStateFlow<LiveSimState?>(null)
    val liveSimState: StateFlow<LiveSimState?> = _liveSimState.asStateFlow()

    private var liveSimJob: Job? = null

    // Helper map of teams for quick access
    val teamsMap = SimulationEngine.TEAMS.associateBy { it.id }

    // Start a fresh tournament
    fun startNewTournament(userTeamId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.clearMatches()
            dao.clearMeta()

            val matches = mutableListOf<MatchEntity>()
            var matchId = 1

            // 1. Generate Group Stage Matches (Round Robin)
            val groups = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L")
            for (g in groups) {
                val groupTeams = SimulationEngine.TEAMS.filter { it.groupName == g }
                if (groupTeams.size == 4) {
                    val t1 = groupTeams[0]
                    val t2 = groupTeams[1]
                    val t3 = groupTeams[2]
                    val t4 = groupTeams[3]

                    // Round 1
                    matches.add(MatchEntity(matchId++, t1.id, t2.id, stage = "GROUP_$g", matchIndex = 1))
                    matches.add(MatchEntity(matchId++, t3.id, t4.id, stage = "GROUP_$g", matchIndex = 2))

                    // Round 2
                    matches.add(MatchEntity(matchId++, t1.id, t3.id, stage = "GROUP_$g", matchIndex = 3))
                    matches.add(MatchEntity(matchId++, t2.id, t4.id, stage = "GROUP_$g", matchIndex = 4))

                    // Round 3
                    matches.add(MatchEntity(matchId++, t1.id, t4.id, stage = "GROUP_$g", matchIndex = 5))
                    matches.add(MatchEntity(matchId++, t2.id, t3.id, stage = "GROUP_$g", matchIndex = 6))
                }
            }

            // 2. Generate Knockout Stage templates (Ids 73 to 104)
            // Round of 32: matches 73 to 88 (Index 1 to 16)
            for (i in 1..16) {
                matches.add(MatchEntity(matchId++, null, null, stage = "R32", matchIndex = i))
            }
            // Round of 16: matches 89 to 96 (Index 1 to 8)
            for (i in 1..8) {
                matches.add(MatchEntity(matchId++, null, null, stage = "R16", matchIndex = i))
            }
            // Quarterfinals: matches 97 to 100 (Index 1 to 4)
            for (i in 1..4) {
                matches.add(MatchEntity(matchId++, null, null, stage = "QF", matchIndex = i))
            }
            // Semifinals: matches 101 to 102 (Index 1 to 2)
            for (i in 1..2) {
                matches.add(MatchEntity(matchId++, null, null, stage = "SF", matchIndex = i))
            }
            // Third Place: match 103 (Index 1)
            matches.add(MatchEntity(matchId++, null, null, stage = "THIRD_PLACE", matchIndex = 1))
            // Final: match 104 (Index 1)
            matches.add(MatchEntity(matchId++, null, null, stage = "FINAL", matchIndex = 1))

            dao.insertMatches(matches)
            dao.insertTournamentMeta(TournamentMetaEntity(1, "GROUP_ROUND_1", userTeamId))
        }
    }

    // Reset whole tournament
    fun resetTournament() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.clearMatches()
            dao.clearMeta()
        }
    }

    // Get standings for a specific group
    fun getGroupStandings(groupName: String, matches: List<MatchEntity>): List<TeamStanding> {
        val groupTeams = SimulationEngine.TEAMS.filter { it.groupName == groupName }
        val standings = groupTeams.associateWith { TeamStanding(it) }.toMutableMap()

        val groupMatches = matches.filter { it.stage == "GROUP_$groupName" && it.isPlayed }
        
        groupMatches.forEach { match ->
            val home = teamsMap[match.homeTeamId]
            val away = teamsMap[match.awayTeamId]
            if (home != null && away != null) {
                val homeStand = standings[home] ?: TeamStanding(home)
                val awayStand = standings[away] ?: TeamStanding(away)

                val hs = match.homeScore
                val ascore = match.awayScore

                val (hw, hd, hl) = when {
                    hs > ascore -> Triple(1, 0, 0)
                    hs < ascore -> Triple(0, 0, 1)
                    else -> Triple(0, 1, 0)
                }

                val (aw, ad, al) = when {
                    ascore > hs -> Triple(1, 0, 0)
                    ascore < hs -> Triple(0, 0, 1)
                    else -> Triple(0, 1, 0)
                }

                standings[home] = homeStand.copy(
                    played = homeStand.played + 1,
                    won = homeStand.won + hw,
                    drawn = homeStand.drawn + hd,
                    lost = homeStand.lost + hl,
                    gf = homeStand.gf + hs,
                    ga = homeStand.ga + ascore,
                    gd = homeStand.gd + (hs - ascore),
                    points = homeStand.points + (hw * 3 + hd)
                )

                standings[away] = awayStand.copy(
                    played = awayStand.played + 1,
                    won = awayStand.won + aw,
                    drawn = awayStand.drawn + ad,
                    lost = awayStand.lost + al,
                    gf = awayStand.gf + ascore,
                    ga = awayStand.ga + hs,
                    gd = awayStand.gd + (ascore - hs),
                    points = awayStand.points + (aw * 3 + ad)
                )
            }
        }

        return standings.values.sortedWith(
            compareByDescending<TeamStanding> { it.points }
                .thenByDescending { it.gd }
                .thenByDescending { it.gf }
                .thenBy { it.team.id }
        )
    }

    // Get 8 Best Third Placed Teams
    fun getBestThirdPlacedTeams(matches: List<MatchEntity>): List<TeamStanding> {
        val groups = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L")
        val thirds = mutableListOf<TeamStanding>()

        for (g in groups) {
            val standings = getGroupStandings(g, matches)
            if (standings.size >= 3) {
                thirds.add(standings[2]) // index 2 is 3rd place
            }
        }

        return thirds.sortedWith(
            compareByDescending<TeamStanding> { it.points }
                .thenByDescending { it.gd }
                .thenByDescending { it.gf }
                .thenByDescending { it.team.rating }
        ).take(8)
    }

    // Quick sim single match
    fun quickSimMatch(match: MatchEntity) {
        if (match.isPlayed || match.homeTeamId == null || match.awayTeamId == null) return
        viewModelScope.launch(Dispatchers.IO) {
            val home = teamsMap[match.homeTeamId] ?: return@launch
            val away = teamsMap[match.awayTeamId] ?: return@launch
            val isKnockout = match.stage !in listOf("GROUP_A", "GROUP_B", "GROUP_C", "GROUP_D", "GROUP_E", "GROUP_F", "GROUP_G", "GROUP_H", "GROUP_I", "GROUP_J", "GROUP_K", "GROUP_L")
            
            val result = SimulationEngine.simulateMatch(home, away, isKnockout)
            
            val converters = Converters()
            val eventsJson = converters.fromEventsList(result.events)

            val updated = match.copy(
                homeScore = result.homeGoals,
                awayScore = result.awayGoals,
                isPlayed = true,
                extraTime = result.extraTime,
                penalties = result.penalties,
                homePenalties = result.homePenalties,
                awayPenalties = result.awayPenalties,
                eventsJson = eventsJson
            )
            dao.updateMatch(updated)
            checkAndAdvanceTournamentState()
        }
    }

    // Quick sim current round / all pending games in current stage
    fun quickSimAllRemainingInStage() {
        viewModelScope.launch(Dispatchers.IO) {
            val meta = dao.getTournamentMeta() ?: return@launch
            val matches = dao.getAllMatches()
            val pending = matches.filter { !it.isPlayed && isMatchInCurrentStage(it, meta.currentStage) }

            pending.forEach { match ->
                val home = teamsMap[match.homeTeamId] ?: return@forEach
                val away = teamsMap[match.awayTeamId] ?: return@forEach
                val isKnockout = match.stage !in listOf("GROUP_A", "GROUP_B", "GROUP_C", "GROUP_D", "GROUP_E", "GROUP_F", "GROUP_G", "GROUP_H", "GROUP_I", "GROUP_J", "GROUP_K", "GROUP_L")

                val result = SimulationEngine.simulateMatch(home, away, isKnockout)
                val converters = Converters()
                val eventsJson = converters.fromEventsList(result.events)

                val updated = match.copy(
                    homeScore = result.homeGoals,
                    awayScore = result.awayGoals,
                    isPlayed = true,
                    extraTime = result.extraTime,
                    penalties = result.penalties,
                    homePenalties = result.homePenalties,
                    awayPenalties = result.awayPenalties,
                    eventsJson = eventsJson
                )
                dao.updateMatch(updated)
            }
            checkAndAdvanceTournamentState()
        }
    }

    private fun isMatchInCurrentStage(match: MatchEntity, currentStage: String): Boolean {
        return when (currentStage) {
            "GROUP_ROUND_1" -> match.stage.startsWith("GROUP_") && (match.matchIndex == 1 || match.matchIndex == 2)
            "GROUP_ROUND_2" -> match.stage.startsWith("GROUP_") && (match.matchIndex == 3 || match.matchIndex == 4)
            "GROUP_ROUND_3" -> match.stage.startsWith("GROUP_") && (match.matchIndex == 5 || match.matchIndex == 6)
            "R32" -> match.stage == "R32"
            "R16" -> match.stage == "R16"
            "QF" -> match.stage == "QF"
            "SF" -> match.stage == "SF"
            "FINAL" -> match.stage == "FINAL" || match.stage == "THIRD_PLACE"
            else -> false
        }
    }

    // Interactive simulation
    fun startInteractiveSimulation(match: MatchEntity, speedMs: Long = 100L) {
        if (match.isPlayed || match.homeTeamId == null || match.awayTeamId == null) return
        liveSimJob?.cancel()
        
        liveSimJob = viewModelScope.launch(Dispatchers.IO) {
            val home = teamsMap[match.homeTeamId] ?: return@launch
            val away = teamsMap[match.awayTeamId] ?: return@launch
            val isKnockout = match.stage !in listOf("GROUP_A", "GROUP_B", "GROUP_C", "GROUP_D", "GROUP_E", "GROUP_F", "GROUP_G", "GROUP_H", "GROUP_I", "GROUP_J", "GROUP_K", "GROUP_L")

            val result = SimulationEngine.simulateMatch(home, away, isKnockout)
            val fullEventsList = result.events
            val maxMinutes = fullEventsList.lastOrNull()?.minute ?: 90

            _liveSimState.value = LiveSimState(
                matchId = match.id,
                homeTeam = home,
                awayTeam = away,
                currentMinute = 0,
                homeScore = 0,
                awayScore = 0,
                visibleEvents = emptyList(),
                isFinished = false,
                result = result
            )

            // Loop minutes
            for (minute in 0..maxMinutes) {
                val newEvents = fullEventsList.filter { it.minute == minute }
                
                withContext(Dispatchers.Main) {
                    val current = _liveSimState.value ?: return@withContext
                    val updatedVisible = current.visibleEvents + newEvents
                    
                    // Increment scores if goals happened
                    val homeGoalsSoFar = updatedVisible.count { it.type == EventType.GOAL && it.teamId == 0 }
                    val awayGoalsSoFar = updatedVisible.count { it.type == EventType.GOAL && it.teamId == 1 }

                    _liveSimState.value = current.copy(
                        currentMinute = minute,
                        homeScore = homeGoalsSoFar,
                        awayScore = awayGoalsSoFar,
                        visibleEvents = updatedVisible
                    )
                }
                delay(speedMs)
            }

            // Finish match
            withContext(Dispatchers.Main) {
                val current = _liveSimState.value ?: return@withContext
                _liveSimState.value = current.copy(
                    isFinished = true,
                    homeScore = result.homeGoals,
                    awayScore = result.awayGoals
                )
            }

            // Save result to Database
            val converters = Converters()
            val eventsJson = converters.fromEventsList(result.events)

            val updated = match.copy(
                homeScore = result.homeGoals,
                awayScore = result.awayGoals,
                isPlayed = true,
                extraTime = result.extraTime,
                penalties = result.penalties,
                homePenalties = result.homePenalties,
                awayPenalties = result.awayPenalties,
                eventsJson = eventsJson
            )
            dao.updateMatch(updated)
            checkAndAdvanceTournamentState()
        }
    }

    fun stopInteractiveSimulation() {
        liveSimJob?.cancel()
        _liveSimState.value = null
    }

    // Core state progression check
    private suspend fun checkAndAdvanceTournamentState() {
        val meta = dao.getTournamentMeta() ?: return
        val matches = dao.getAllMatches()

        when (meta.currentStage) {
            "GROUP_ROUND_1" -> {
                // Check if all round 1 group matches (index 1 & 2) are completed
                val r1Matches = matches.filter { it.stage.startsWith("GROUP_") && (it.matchIndex == 1 || it.matchIndex == 2) }
                if (r1Matches.all { it.isPlayed }) {
                    dao.insertTournamentMeta(meta.copy(currentStage = "GROUP_ROUND_2"))
                }
            }
            "GROUP_ROUND_2" -> {
                val r2Matches = matches.filter { it.stage.startsWith("GROUP_") && (it.matchIndex == 3 || it.matchIndex == 4) }
                if (r2Matches.all { it.isPlayed }) {
                    dao.insertTournamentMeta(meta.copy(currentStage = "GROUP_ROUND_3"))
                }
            }
            "GROUP_ROUND_3" -> {
                val r3Matches = matches.filter { it.stage.startsWith("GROUP_") && (it.matchIndex == 5 || it.matchIndex == 6) }
                if (r3Matches.all { it.isPlayed }) {
                    // Populate Round of 32!
                    populateRoundOf32(matches)
                }
            }
            "R32" -> {
                val r32Matches = matches.filter { it.stage == "R32" }
                if (r32Matches.all { it.isPlayed }) {
                    populateRoundOf16(matches)
                }
            }
            "R16" -> {
                val r16Matches = matches.filter { it.stage == "R16" }
                if (r16Matches.all { it.isPlayed }) {
                    populateQuarterfinals(matches)
                }
            }
            "QF" -> {
                val qfMatches = matches.filter { it.stage == "QF" }
                if (qfMatches.all { it.isPlayed }) {
                    populateSemifinals(matches)
                }
            }
            "SF" -> {
                val sfMatches = matches.filter { it.stage == "SF" }
                if (sfMatches.all { it.isPlayed }) {
                    populateFinalAndThirdPlace(matches)
                }
            }
            "FINAL" -> {
                val finalMatches = matches.filter { it.stage == "FINAL" || it.stage == "THIRD_PLACE" }
                if (finalMatches.all { it.isPlayed }) {
                    dao.insertTournamentMeta(meta.copy(currentStage = "FINISHED"))
                }
            }
        }
    }

    private suspend fun populateRoundOf32(matches: List<MatchEntity>) {
        val groups = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L")
        val winners = mutableMapOf<String, Team>()
        val runnersUp = mutableMapOf<String, Team>()

        for (g in groups) {
            val standings = getGroupStandings(g, matches)
            winners[g] = standings[0].team
            runnersUp[g] = standings[1].team
        }

        val bestThirds = getBestThirdPlacedTeams(matches).map { it.team }

        // Setup Round of 32 matchups.
        // We have 16 matchups. To keep it deterministic and robust:
        // Match 1: 1A vs 3rd Best 1 (index 0 in best thirds list)
        // Match 2: 2A vs 2B
        // Match 3: 1B vs 3rd Best 2
        // Match 4: 1C vs 3rd Best 3
        // Match 5: 2C vs 2D
        // Match 6: 1D vs 3rd Best 4
        // Match 7: 1E vs 3rd Best 5
        // Match 8: 2E vs 2F
        // Match 9: 1F vs 3rd Best 6
        // Match 10: 1G vs 3rd Best 7
        // Match 11: 2G vs 2H
        // Match 12: 1H vs 3rd Best 8
        // Match 13: 1I vs 2J
        // Match 14: 2I vs 1J
        // Match 15: 1K vs 2L
        // Match 16: 2K vs 1L

        val updatedMatches = matches.map { match ->
            if (match.stage == "R32") {
                val homeId: Int
                val awayId: Int
                when (match.matchIndex) {
                    1 -> { homeId = winners["A"]!!.id; awayId = bestThirds.getOrNull(0)?.id ?: winners["L"]!!.id }
                    2 -> { homeId = runnersUp["A"]!!.id; awayId = runnersUp["B"]!!.id }
                    3 -> { homeId = winners["B"]!!.id; awayId = bestThirds.getOrNull(1)?.id ?: winners["K"]!!.id }
                    4 -> { homeId = winners["C"]!!.id; awayId = bestThirds.getOrNull(2)?.id ?: winners["J"]!!.id }
                    5 -> { homeId = runnersUp["C"]!!.id; awayId = runnersUp["D"]!!.id }
                    6 -> { homeId = winners["D"]!!.id; awayId = bestThirds.getOrNull(3)?.id ?: winners["I"]!!.id }
                    7 -> { homeId = winners["E"]!!.id; awayId = bestThirds.getOrNull(4)?.id ?: winners["H"]!!.id }
                    8 -> { homeId = runnersUp["E"]!!.id; awayId = runnersUp["F"]!!.id }
                    9 -> { homeId = winners["F"]!!.id; awayId = bestThirds.getOrNull(5)?.id ?: winners["G"]!!.id }
                    10 -> { homeId = winners["G"]!!.id; awayId = bestThirds.getOrNull(6)?.id ?: winners["F"]!!.id }
                    11 -> { homeId = runnersUp["G"]!!.id; awayId = runnersUp["H"]!!.id }
                    12 -> { homeId = winners["H"]!!.id; awayId = bestThirds.getOrNull(7)?.id ?: winners["E"]!!.id }
                    13 -> { homeId = winners["I"]!!.id; awayId = runnersUp["J"]!!.id }
                    14 -> { homeId = runnersUp["I"]!!.id; awayId = winners["J"]!!.id }
                    15 -> { homeId = winners["K"]!!.id; awayId = runnersUp["L"]!!.id }
                    16 -> { homeId = runnersUp["K"]!!.id; awayId = winners["L"]!!.id }
                    else -> return@map match
                }
                match.copy(homeTeamId = homeId, awayTeamId = awayId)
            } else match
        }

        dao.insertMatches(updatedMatches)
        val meta = dao.getTournamentMeta() ?: return
        dao.insertTournamentMeta(meta.copy(currentStage = "R32"))
    }

    private suspend fun populateRoundOf16(matches: List<MatchEntity>) {
        val r32 = matches.filter { it.stage == "R32" }.associateBy { it.matchIndex }
        
        fun getWinnerId(index: Int): Int {
            val m = r32[index]!!
            return if (m.homeScore > m.awayScore) m.homeTeamId!! else if (m.awayScore > m.homeScore) m.awayTeamId!! else {
                if (m.homePenalties > m.awayPenalties) m.homeTeamId!! else m.awayTeamId!!
            }
        }

        val updatedMatches = matches.map { match ->
            if (match.stage == "R16") {
                val homeId: Int
                val awayId: Int
                when (match.matchIndex) {
                    1 -> { homeId = getWinnerId(1); awayId = getWinnerId(2) }
                    2 -> { homeId = getWinnerId(3); awayId = getWinnerId(4) }
                    3 -> { homeId = getWinnerId(5); awayId = getWinnerId(6) }
                    4 -> { homeId = getWinnerId(7); awayId = getWinnerId(8) }
                    5 -> { homeId = getWinnerId(9); awayId = getWinnerId(10) }
                    6 -> { homeId = getWinnerId(11); awayId = getWinnerId(12) }
                    7 -> { homeId = getWinnerId(13); awayId = getWinnerId(14) }
                    8 -> { homeId = getWinnerId(15); awayId = getWinnerId(16) }
                    else -> return@map match
                }
                match.copy(homeTeamId = homeId, awayTeamId = awayId)
            } else match
        }

        dao.insertMatches(updatedMatches)
        val meta = dao.getTournamentMeta() ?: return
        dao.insertTournamentMeta(meta.copy(currentStage = "R16"))
    }

    private suspend fun populateQuarterfinals(matches: List<MatchEntity>) {
        val r16 = matches.filter { it.stage == "R16" }.associateBy { it.matchIndex }

        fun getWinnerId(index: Int): Int {
            val m = r16[index]!!
            return if (m.homeScore > m.awayScore) m.homeTeamId!! else if (m.awayScore > m.homeScore) m.awayTeamId!! else {
                if (m.homePenalties > m.awayPenalties) m.homeTeamId!! else m.awayTeamId!!
            }
        }

        val updatedMatches = matches.map { match ->
            if (match.stage == "QF") {
                val homeId: Int
                val awayId: Int
                when (match.matchIndex) {
                    1 -> { homeId = getWinnerId(1); awayId = getWinnerId(2) }
                    2 -> { homeId = getWinnerId(3); awayId = getWinnerId(4) }
                    3 -> { homeId = getWinnerId(5); awayId = getWinnerId(6) }
                    4 -> { homeId = getWinnerId(7); awayId = getWinnerId(8) }
                    else -> return@map match
                }
                match.copy(homeTeamId = homeId, awayTeamId = awayId)
            } else match
        }

        dao.insertMatches(updatedMatches)
        val meta = dao.getTournamentMeta() ?: return
        dao.insertTournamentMeta(meta.copy(currentStage = "QF"))
    }

    private suspend fun populateSemifinals(matches: List<MatchEntity>) {
        val qf = matches.filter { it.stage == "QF" }.associateBy { it.matchIndex }

        fun getWinnerId(index: Int): Int {
            val m = qf[index]!!
            return if (m.homeScore > m.awayScore) m.homeTeamId!! else if (m.awayScore > m.homeScore) m.awayTeamId!! else {
                if (m.homePenalties > m.awayPenalties) m.homeTeamId!! else m.awayTeamId!!
            }
        }

        val updatedMatches = matches.map { match ->
            if (match.stage == "SF") {
                val homeId: Int
                val awayId: Int
                when (match.matchIndex) {
                    1 -> { homeId = getWinnerId(1); awayId = getWinnerId(2) }
                    2 -> { homeId = getWinnerId(3); awayId = getWinnerId(4) }
                    else -> return@map match
                }
                match.copy(homeTeamId = homeId, awayTeamId = awayId)
            } else match
        }

        dao.insertMatches(updatedMatches)
        val meta = dao.getTournamentMeta() ?: return
        dao.insertTournamentMeta(meta.copy(currentStage = "SF"))
    }

    private suspend fun populateFinalAndThirdPlace(matches: List<MatchEntity>) {
        val sf = matches.filter { it.stage == "SF" }.associateBy { it.matchIndex }

        fun getWinnerId(index: Int): Int {
            val m = sf[index]!!
            return if (m.homeScore > m.awayScore) m.homeTeamId!! else if (m.awayScore > m.homeScore) m.awayTeamId!! else {
                if (m.homePenalties > m.awayPenalties) m.homeTeamId!! else m.awayTeamId!!
            }
        }

        fun getLoserId(index: Int): Int {
            val m = sf[index]!!
            val w = getWinnerId(index)
            return if (m.homeTeamId == w) m.awayTeamId!! else m.homeTeamId!!
        }

        val updatedMatches = matches.map { match ->
            when (match.stage) {
                "THIRD_PLACE" -> {
                    match.copy(homeTeamId = getLoserId(1), awayTeamId = getLoserId(2))
                }
                "FINAL" -> {
                    match.copy(homeTeamId = getWinnerId(1), awayTeamId = getWinnerId(2))
                }
                else -> match
            }
        }

        dao.insertMatches(updatedMatches)
        val meta = dao.getTournamentMeta() ?: return
        dao.insertTournamentMeta(meta.copy(currentStage = "FINAL"))
    }

    // Stats Compilation (Golden Boot & Playmakers)
    fun getScorersLeaderboard(matches: List<MatchEntity>): List<PlayerStatRow> {
        val scorers = mutableMapOf<String, Int>()
        val playerToTeam = mutableMapOf<String, Team>() // map player key to team

        val converters = Converters()

        matches.filter { it.isPlayed }.forEach { match ->
            val home = teamsMap[match.homeTeamId]
            val away = teamsMap[match.awayTeamId]
            
            val events = converters.toEventsList(match.eventsJson)
            events.filter { it.type == EventType.GOAL }.forEach { ev ->
                val playerKey = ev.playerEn
                scorers[playerKey] = (scorers[playerKey] ?: 0) + 1
                
                if (!playerToTeam.containsKey(playerKey)) {
                    val matchingTeam = if (ev.teamId == 0) home else away
                    if (matchingTeam != null) {
                        playerToTeam[playerKey] = matchingTeam
                    }
                }
            }
        }

        return scorers.entries.map { entry ->
            val playerKey = entry.key
            val team = playerToTeam[playerKey]
            val playerFa = team?.players?.firstOrNull { it.nameEn == playerKey }?.nameFa ?: playerKey
            
            PlayerStatRow(
                playerNameEn = playerKey,
                playerNameFa = playerFa,
                teamFa = team?.nameFa ?: "ناشناس",
                teamFlag = team?.flag ?: "🏳️",
                count = entry.value,
                teamNameEn = team?.nameEn
            )
        }.sortedByDescending { it.count }
    }

    fun getAssistsLeaderboard(matches: List<MatchEntity>): List<PlayerStatRow> {
        val assists = mutableMapOf<String, Int>()
        val playerToTeam = mutableMapOf<String, Team>()

        val converters = Converters()

        matches.filter { it.isPlayed }.forEach { match ->
            val home = teamsMap[match.homeTeamId]
            val away = teamsMap[match.awayTeamId]

            val events = converters.toEventsList(match.eventsJson)
            events.filter { it.type == EventType.GOAL && it.assistEn != null }.forEach { ev ->
                val playerKey = ev.assistEn!!
                assists[playerKey] = (assists[playerKey] ?: 0) + 1

                if (!playerToTeam.containsKey(playerKey)) {
                    val matchingTeam = if (ev.teamId == 0) home else away
                    if (matchingTeam != null) {
                        playerToTeam[playerKey] = matchingTeam
                    }
                }
            }
        }

        return assists.entries.map { entry ->
            val playerKey = entry.key
            val team = playerToTeam[playerKey]
            val playerFa = team?.players?.firstOrNull { it.nameEn == playerKey }?.nameFa ?: playerKey

            PlayerStatRow(
                playerNameEn = playerKey,
                playerNameFa = playerFa,
                teamFa = team?.nameFa ?: "ناشناس",
                teamFlag = team?.flag ?: "🏳️",
                count = entry.value,
                teamNameEn = team?.nameEn
            )
        }.sortedByDescending { it.count }
    }

    fun getCardsLeaderboard(matches: List<MatchEntity>): List<PlayerStatRow> {
        val cards = mutableMapOf<String, Int>()
        val playerToTeam = mutableMapOf<String, Team>()

        val converters = Converters()

        matches.filter { it.isPlayed }.forEach { match ->
            val home = teamsMap[match.homeTeamId]
            val away = teamsMap[match.awayTeamId]

            val events = converters.toEventsList(match.eventsJson)
            events.filter { it.type == EventType.YELLOW_CARD || it.type == EventType.RED_CARD }.forEach { ev ->
                val playerKey = ev.playerEn
                val score = if (ev.type == EventType.RED_CARD) 2 else 1
                cards[playerKey] = (cards[playerKey] ?: 0) + score

                if (!playerToTeam.containsKey(playerKey)) {
                    val matchingTeam = if (ev.teamId == 0) home else away
                    if (matchingTeam != null) {
                        playerToTeam[playerKey] = matchingTeam
                    }
                }
            }
        }

        return cards.entries.map { entry ->
            val playerKey = entry.key
            val team = playerToTeam[playerKey]
            val playerFa = team?.players?.firstOrNull { it.nameEn == playerKey }?.nameFa ?: playerKey

            PlayerStatRow(
                playerNameEn = playerKey,
                playerNameFa = playerFa,
                teamFa = team?.nameFa ?: "ناشناس",
                teamFlag = team?.flag ?: "🏳️",
                count = entry.value,
                teamNameEn = team?.nameEn
            )
        }.sortedByDescending { it.count }
    }
}

data class LiveSimState(
    val matchId: Int,
    val homeTeam: Team,
    val awayTeam: Team,
    val currentMinute: Int,
    val homeScore: Int,
    val awayScore: Int,
    val visibleEvents: List<MatchEvent>,
    val isFinished: Boolean,
    val result: MatchSimulationResult
)
