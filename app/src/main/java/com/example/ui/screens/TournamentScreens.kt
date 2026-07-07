package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.MatchEntity
import com.example.data.Converters
import com.example.engine.*
import com.example.ui.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp

@Composable
fun TeamLogo(
    teamNameEn: String,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp
) {
    val flagUrl = remember(teamNameEn) { FlagStorage.getFlagUrl(teamNameEn) }
    
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .border(0.5.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = flagUrl,
            contentDescription = "$teamNameEn Logo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentMainScreen(viewModel: TournamentViewModel) {
    // Force Persian RTL layout direction
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val matches by viewModel.allMatches.collectAsState()
        val meta by viewModel.tournamentMeta.collectAsState()
        val liveSim by viewModel.liveSimState.collectAsState()

        var activeTab by remember { mutableStateOf(0) } // 0: Sim, 1: Groups, 2: Bracket, 3: Stats
        var selectedPlayedMatchForDetails by remember { mutableStateOf<MatchEntity?>(null) }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "جام",
                                tint = Gold,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "شبیه‌ساز جام‌جهانی ۲۰۲۶",
                                color = SoftWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = StadiumNight
                    )
                )
            },
            bottomBar = {
                if (meta != null && meta!!.currentStage != "NOT_STARTED") {
                    NavigationBar(
                        containerColor = PitchCard,
                        tonalElevation = 8.dp
                    ) {
                        NavigationBarItem(
                            selected = activeTab == 0,
                            onClick = { activeTab = 0 },
                            icon = { Icon(Icons.Default.SportsFootball, "شبیه‌ساز") },
                            label = { Text("شبیه‌ساز", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = StadiumNight,
                                selectedTextColor = Gold,
                                indicatorColor = Gold,
                                unselectedIconColor = GrayText,
                                unselectedTextColor = GrayText
                            )
                        )
                        NavigationBarItem(
                            selected = activeTab == 1,
                            onClick = { activeTab = 1 },
                            icon = { Icon(Icons.Default.TableChart, "گروه‌ها") },
                            label = { Text("گروه‌ها", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = StadiumNight,
                                selectedTextColor = Gold,
                                indicatorColor = Gold,
                                unselectedIconColor = GrayText,
                                unselectedTextColor = GrayText
                            )
                        )
                        NavigationBarItem(
                            selected = activeTab == 2,
                            onClick = { activeTab = 2 },
                            icon = { Icon(Icons.Default.AccountTree, "براکت") },
                            label = { Text("براکت", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = StadiumNight,
                                selectedTextColor = Gold,
                                indicatorColor = Gold,
                                unselectedIconColor = GrayText,
                                unselectedTextColor = GrayText
                            )
                        )
                        NavigationBarItem(
                            selected = activeTab == 3,
                            onClick = { activeTab = 3 },
                            icon = { Icon(Icons.Default.BarChart, "آمار") },
                            label = { Text("آمار", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = StadiumNight,
                                selectedTextColor = Gold,
                                indicatorColor = Gold,
                                unselectedIconColor = GrayText,
                                unselectedTextColor = GrayText
                            )
                        )
                    }
                }
            },
            containerColor = StadiumNight
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(PitchCard, StadiumNight),
                            radius = 1200f
                        )
                    )
            ) {
                if (meta == null || meta!!.currentStage == "NOT_STARTED") {
                    // Onboarding screen - Team selection
                    TeamSelectionScreen(onTeamSelected = { id ->
                        viewModel.startNewTournament(id)
                    })
                } else {
                    // Main Dashboard content based on tab
                    when (activeTab) {
                        0 -> SimulatorTab(
                            viewModel = viewModel,
                            matches = matches,
                            meta = meta!!,
                            onMatchClick = { match -> selectedPlayedMatchForDetails = match }
                        )
                        1 -> StandingsTab(viewModel = viewModel, matches = matches)
                        2 -> BracketTab(viewModel = viewModel, matches = matches)
                        3 -> StatsTab(viewModel = viewModel, matches = matches)
                    }
                }

                // Interactive Live Simulator overlay dialog
                liveSim?.let { state ->
                    InteractiveSimulatorDialog(
                        state = state,
                        onStop = { viewModel.stopInteractiveSimulation() }
                    )
                }

                // Match details dialog
                selectedPlayedMatchForDetails?.let { match ->
                    MatchDetailsDialog(
                        match = match,
                        viewModel = viewModel,
                        onDismiss = { selectedPlayedMatchForDetails = null }
                    )
                }
            }
        }
    }
}

// 1. TEAM SELECTION ONBOARDING SCREEN
@Composable
fun TeamSelectionScreen(onTeamSelected: (Int) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    val allTeams = remember { SimulationEngine.TEAMS.sortedBy { it.nameFa } }
    
    val filteredTeams = remember(searchQuery) {
        if (searchQuery.isEmpty()) allTeams
        else allTeams.filter { it.nameFa.contains(searchQuery) || it.nameEn.lowercase().contains(searchQuery.lowercase()) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "تیم محبوب خود را انتخاب کنید",
            color = Gold,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            "برای هدایت یک تیم در جام‌جهانی ۲۰۲۶ آمریکا، مکزیک و کانادا، کشور مورد نظر خود را انتخاب کنید.",
            color = GrayText,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("جستجوی تیم (مثلاً ایران)", color = GrayText) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = Gold) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = SoftWhite,
                unfocusedTextColor = SoftWhite,
                focusedBorderColor = Gold,
                unfocusedBorderColor = PitchCard,
                focusedContainerColor = PitchCard,
                unfocusedContainerColor = PitchCard
            ),
            shape = RoundedCornerShape(16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filteredTeams) { team ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clickable { onTeamSelected(team.id) },
                    colors = CardDefaults.cardColors(containerColor = PitchCard),
                    border = BorderStroke(1.dp, CardHighlight),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        TeamLogo(
                            teamNameEn = team.nameEn,
                            size = 40.dp,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Text(
                            text = team.nameFa,
                            color = SoftWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "قدرت: ${team.rating}",
                            color = Gold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

// 2. SIMULATOR DASHBOARD TAB
@Composable
fun SimulatorTab(
    viewModel: TournamentViewModel,
    matches: List<MatchEntity>,
    meta: com.example.data.TournamentMetaEntity,
    onMatchClick: (MatchEntity) -> Unit
) {
    val currentStage = meta.currentStage
    val myTeam = remember(meta.selectedTeamId) { viewModel.teamsMap[meta.selectedTeamId] }

    val stageMatches = remember(matches, currentStage) {
        matches.filter { match ->
            when (currentStage) {
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
        }.sortedWith(compareBy<MatchEntity> { it.stage }.thenBy { it.id })
    }

    val stageTitleFa = when (currentStage) {
        "GROUP_ROUND_1" -> "مرحله گروهی - دور اول"
        "GROUP_ROUND_2" -> "مرحله گروهی - دور دوم"
        "GROUP_ROUND_3" -> "مرحله گروهی - دور سوم"
        "R32" -> "مرحله حذفی - یک‌سی‌ودوم نهایی"
        "R16" -> "مرحله حذفی - یک‌هشتم نهایی"
        "QF" -> "مرحله حذفی - یک‌چهارم نهایی"
        "SF" -> "مرحله حذفی - نیمه نهایی"
        "FINAL" -> "فینال و رده‌بندی"
        "FINISHED" -> "تورنمنت پایان یافت!"
        else -> ""
    }

    val isStageCompleted = stageMatches.isNotEmpty() && stageMatches.all { it.isPlayed }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Dashboard Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PitchCard),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, CardHighlight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "مرحله فعلی:",
                                color = GrayText,
                                fontSize = 11.sp
                            )
                            Text(
                                text = stageTitleFa,
                                color = Gold,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        myTeam?.let {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(CardHighlight, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                TeamLogo(teamNameEn = it.nameEn, size = 20.dp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "تیم من: ${it.nameFa}",
                                    color = SoftWhite,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    
                    if (currentStage != "FINISHED") {
                        if (!isStageCompleted) {
                            Button(
                                onClick = { viewModel.quickSimAllRemainingInStage() },
                                colors = ButtonDefaults.buttonColors(containerColor = FieldGreen),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.FastForward, null, tint = SoftWhite)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("شبیه‌سازی تمام بازی‌های باقی‌مانده", color = SoftWhite, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = { viewModel.quickSimAllRemainingInStage() /* triggers state progression check */ },
                                colors = ButtonDefaults.buttonColors(containerColor = Gold),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.NavigateNext, null, tint = StadiumNight)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("رفتن به مرحله بعدی", color = StadiumNight, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Button(
                            onClick = { viewModel.resetTournament() },
                            colors = ButtonDefaults.buttonColors(containerColor = RedCardColor),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Refresh, null, tint = SoftWhite)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ریست و شروع مجدد تورنمنت", color = SoftWhite, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (currentStage == "FINISHED") {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.EmojiEvents,
                        null,
                        tint = Gold,
                        modifier = Modifier.size(90.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "قهرمان جام‌جهانی تعیین شد!",
                        color = SoftWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        "تمام مسابقات با موفقیت شبیه‌سازی شدند. می‌توانید جدول‌ها و آمار آقای گل را بررسی کنید.",
                        color = GrayText,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, start = 16.dp, end = 16.dp)
                    )
                }
            }
        } else {
            item {
                Text(
                    text = "بازی‌های این مرحله:",
                    color = SoftWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            items(stageMatches) { match ->
                MatchRowItem(
                    match = match,
                    viewModel = viewModel,
                    onMatchClick = onMatchClick,
                    onQuickSim = { viewModel.quickSimMatch(match) },
                    onSimMatch = { viewModel.startInteractiveSimulation(match) }
                )
            }
        }
    }
}

@Composable
fun MatchRowItem(
    match: MatchEntity,
    viewModel: TournamentViewModel,
    onMatchClick: (MatchEntity) -> Unit,
    onQuickSim: () -> Unit,
    onSimMatch: () -> Unit
) {
    val home = viewModel.teamsMap[match.homeTeamId]
    val away = viewModel.teamsMap[match.awayTeamId]

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = match.isPlayed) { onMatchClick(match) },
        colors = CardDefaults.cardColors(containerColor = PitchCard),
        border = BorderStroke(1.dp, if (match.isPlayed) CardHighlight else FieldGreen.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Stage/Group indicator
            Text(
                text = formatStageLabel(match.stage),
                color = GrayText,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home Team
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (home != null) {
                        TeamLogo(teamNameEn = home.nameEn, size = 24.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            home.nameFa,
                            color = SoftWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🏳️", fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("مشخص نشده", color = GrayText, fontSize = 12.sp)
                    }
                }

                // Scoreboard or Simulation Buttons
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .wrapContentHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    if (match.isPlayed) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${match.homeScore} - ${match.awayScore}",
                                color = Gold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            if (match.penalties) {
                                Text(
                                    text = "پنالتی: (${match.homePenalties}) - (${match.awayPenalties})",
                                    color = CyanAssist,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            } else if (match.extraTime) {
                                Text(
                                    text = "وقت اضافه",
                                    color = GrayText,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "VS",
                            color = GrayText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                // Away Team
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    if (away != null) {
                        Text(
                            away.nameFa,
                            color = SoftWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        TeamLogo(teamNameEn = away.nameEn, size = 24.dp)
                    } else {
                        Text("مشخص نشده", color = GrayText, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🏳️", fontSize = 14.sp)
                        }
                    }
                }
            }

            // Quick Sim & Match Sim action buttons if not played yet
            if (!match.isPlayed && home != null && away != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onQuickSim,
                        colors = ButtonDefaults.buttonColors(containerColor = CardHighlight),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.FlashOn, null, tint = Gold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("کوییک سیم", color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onSimMatch,
                        colors = ButtonDefaults.buttonColors(containerColor = FieldGreen),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 12.dp),
                        modifier = Modifier.weight(1.2f)
                    ) {
                        Icon(Icons.Default.PlayArrow, null, tint = SoftWhite, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("شبیه‌سازی کامل", color = SoftWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else if (match.isPlayed) {
                // Click to view events hint
                Text(
                    text = "برای مشاهده گزارش بازی ضربه بزنید 📝",
                    color = GrayText.copy(alpha = 0.6f),
                    fontSize = 9.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                )
            }
        }
    }
}

// 3. STANDINGS / GROUPS TAB
@Composable
fun StandingsTab(viewModel: TournamentViewModel, matches: List<MatchEntity>) {
    var viewThirdsByToggle by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (viewThirdsByToggle) "جدول رده‌بندی بهترین تیم‌های سوم" else "جداول رده‌بندی گروه‌ها",
                color = Gold,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Button(
                onClick = { viewThirdsByToggle = !viewThirdsByToggle },
                colors = ButtonDefaults.buttonColors(containerColor = CardHighlight),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (viewThirdsByToggle) "مشاهده گروه‌ها" else "مشاهده بهترین تیم‌های سوم",
                    color = Gold,
                    fontSize = 11.sp
                )
            }
        }

        if (viewThirdsByToggle) {
            val thirds = viewModel.getBestThirdPlacedTeams(matches)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        "طبق قوانین جام‌جهانی ۲۰۲۶ با حضور ۴۸ تیم، ۱۲ تیم برتر رتبه اول، ۱۲ تیم رتبه دوم و ۸ تیم برتر از بین رتبه‌های سوم گروه‌ها به مرحله یک‌سی‌ودوم صعود می‌کنند.",
                        color = GrayText,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = PitchCard),
                        border = BorderStroke(1.dp, CardHighlight),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Table Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("#", color = GrayText, fontSize = 11.sp, modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
                                Text("تیم", color = GrayText, fontSize = 11.sp, modifier = Modifier.weight(1f))
                                Text("گروه", color = GrayText, fontSize = 11.sp, modifier = Modifier.width(35.dp), textAlign = TextAlign.Center)
                                Text("بازی", color = GrayText, fontSize = 11.sp, modifier = Modifier.width(30.dp), textAlign = TextAlign.Center)
                                Text("تفاضل", color = GrayText, fontSize = 11.sp, modifier = Modifier.width(35.dp), textAlign = TextAlign.Center)
                                Text("امتیاز", color = GrayText, fontSize = 11.sp, modifier = Modifier.width(35.dp), textAlign = TextAlign.Center)
                            }

                            Divider(color = CardHighlight, thickness = 1.dp)

                            thirds.forEachIndexed { index, row ->
                                val isQualified = index < 8
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(if (isQualified) FieldGreen.copy(alpha = 0.08f) else Color.Transparent)
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = (index + 1).toString(),
                                        color = if (isQualified) Gold else GrayText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.width(20.dp),
                                        textAlign = TextAlign.Center
                                    )
                                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                        TeamLogo(teamNameEn = row.team.nameEn, size = 18.dp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(row.team.nameFa, color = SoftWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Text(row.team.groupName, color = SoftWhite, fontSize = 12.sp, modifier = Modifier.width(35.dp), textAlign = TextAlign.Center)
                                    Text(row.played.toString(), color = SoftWhite, fontSize = 12.sp, modifier = Modifier.width(30.dp), textAlign = TextAlign.Center)
                                    Text(
                                        text = (if (row.gd > 0) "+" else "") + row.gd,
                                        color = if (row.gd > 0) FieldGreen else if (row.gd < 0) RedCardColor else SoftWhite,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.width(35.dp),
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = row.points.toString(),
                                        color = if (isQualified) Gold else SoftWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        modifier = Modifier.width(35.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                                if (index < thirds.lastIndex) {
                                    Divider(color = CardHighlight.copy(alpha = 0.5f), thickness = 0.5.dp)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            val groups = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L")
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(groups) { g ->
                    val standings = viewModel.getGroupStandings(g, matches)
                    GroupCard(groupName = g, standings = standings)
                }
            }
        }
    }
}

@Composable
fun GroupCard(groupName: String, standings: List<TeamStanding>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PitchCard),
        border = BorderStroke(1.dp, CardHighlight),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Group Title
            Text(
                "گروه $groupName",
                color = Gold,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Columns Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("#", color = GrayText, fontSize = 10.sp, modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
                Text("تیم", color = GrayText, fontSize = 10.sp, modifier = Modifier.weight(1f))
                Text("بازی", color = GrayText, fontSize = 10.sp, modifier = Modifier.width(30.dp), textAlign = TextAlign.Center)
                Text("برد", color = GrayText, fontSize = 10.sp, modifier = Modifier.width(25.dp), textAlign = TextAlign.Center)
                Text("مساوی", color = GrayText, fontSize = 10.sp, modifier = Modifier.width(35.dp), textAlign = TextAlign.Center)
                Text("باخت", color = GrayText, fontSize = 10.sp, modifier = Modifier.width(25.dp), textAlign = TextAlign.Center)
                Text("تفاضل", color = GrayText, fontSize = 10.sp, modifier = Modifier.width(35.dp), textAlign = TextAlign.Center)
                Text("امتیاز", color = GrayText, fontSize = 10.sp, modifier = Modifier.width(35.dp), textAlign = TextAlign.Center)
            }

            Divider(color = CardHighlight, thickness = 1.dp)

            standings.forEachIndexed { index, row ->
                // Top 2 promote directly (gold/green glow), 3rd depends, 4th out
                val rankColor = when (index) {
                    0, 1 -> FieldGreen
                    2 -> Gold
                    else -> GrayText
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = (index + 1).toString(),
                        color = rankColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.width(20.dp),
                        textAlign = TextAlign.Center
                    )
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        TeamLogo(teamNameEn = row.team.nameEn, size = 18.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            row.team.nameFa,
                            color = SoftWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(row.played.toString(), color = SoftWhite, fontSize = 11.sp, modifier = Modifier.width(30.dp), textAlign = TextAlign.Center)
                    Text(row.won.toString(), color = SoftWhite, fontSize = 11.sp, modifier = Modifier.width(25.dp), textAlign = TextAlign.Center)
                    Text(row.drawn.toString(), color = SoftWhite, fontSize = 11.sp, modifier = Modifier.width(35.dp), textAlign = TextAlign.Center)
                    Text(row.lost.toString(), color = SoftWhite, fontSize = 11.sp, modifier = Modifier.width(25.dp), textAlign = TextAlign.Center)
                    Text(
                        text = (if (row.gd > 0) "+" else "") + row.gd,
                        color = if (row.gd > 0) FieldGreen else if (row.gd < 0) RedCardColor else SoftWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.width(35.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = row.points.toString(),
                        color = rankColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.width(35.dp),
                        textAlign = TextAlign.Center
                    )
                }

                if (index < 3) {
                    Divider(color = CardHighlight.copy(alpha = 0.3f), thickness = 0.5.dp)
                }
            }
        }
    }
}

// 4. VISUAL BRACKET TAB
@Composable
fun BracketTab(viewModel: TournamentViewModel, matches: List<MatchEntity>) {
    val stages = listOf(
        "R32" to "یک‌سی‌ودوم",
        "R16" to "یک‌هشتم نهایی",
        "QF" to "یک‌چهارم نهایی",
        "SF" to "نیمه نهایی",
        "FINAL" to "فینال"
    )
    var selectedStageFilter by remember { mutableStateOf("R32") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Tab row filter for stages
        ScrollableTabRow(
            selectedTabIndex = stages.indexOfFirst { it.first == selectedStageFilter }.coerceAtLeast(0),
            containerColor = StadiumNight,
            contentColor = Gold,
            edgePadding = 16.dp,
            divider = {}
        ) {
            stages.forEachIndexed { index, pair ->
                Tab(
                    selected = selectedStageFilter == pair.first,
                    onClick = { selectedStageFilter = pair.first },
                    text = { Text(pair.second, fontWeight = FontWeight.Bold) },
                    selectedContentColor = Gold,
                    unselectedContentColor = GrayText
                )
            }
        }

        val stageMatches = remember(matches, selectedStageFilter) {
            matches.filter { it.stage == selectedStageFilter || (selectedStageFilter == "FINAL" && it.stage == "THIRD_PLACE") }
                .sortedBy { it.id }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (stageMatches.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 80.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Lock, null, tint = GrayText, modifier = Modifier.size(50.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "مرحله حذفی هنوز شروع نشده است",
                            color = GrayText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                items(stageMatches) { match ->
                    val home = viewModel.teamsMap[match.homeTeamId]
                    val away = viewModel.teamsMap[match.awayTeamId]

                    Card(
                        colors = CardDefaults.cardColors(containerColor = PitchCard),
                        border = BorderStroke(1.dp, CardHighlight),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (match.stage == "THIRD_PLACE") "مسابقه رده‌بندی مقام سوم" else "بازی شماره ${match.matchIndex}",
                                color = Gold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )

                            // Team A row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (home != null) {
                                    TeamLogo(teamNameEn = home.nameEn, size = 20.dp)
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color.White.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("🏳️", fontSize = 11.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = home?.nameFa ?: "مشخص نشده (برنده دور قبل)",
                                    color = if (home != null) SoftWhite else GrayText,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                if (match.isPlayed) {
                                    Text(
                                        text = match.homeScore.toString() + (if (match.penalties) " (${match.homePenalties})" else ""),
                                        color = if (match.homeScore > match.awayScore || (match.penalties && match.homePenalties > match.awayPenalties)) Gold else GrayText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            Divider(color = CardHighlight, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 6.dp))

                            // Team B row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (away != null) {
                                    TeamLogo(teamNameEn = away.nameEn, size = 20.dp)
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color.White.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("🏳️", fontSize = 11.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = away?.nameFa ?: "مشخص نشده (برنده دور قبل)",
                                    color = if (away != null) SoftWhite else GrayText,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                if (match.isPlayed) {
                                    Text(
                                        text = match.awayScore.toString() + (if (match.penalties) " (${match.awayPenalties})" else ""),
                                        color = if (match.awayScore > match.homeScore || (match.penalties && match.awayPenalties > match.homePenalties)) Gold else GrayText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// 5. TOURNAMENT STATS TAB
@Composable
fun StatsTab(viewModel: TournamentViewModel, matches: List<MatchEntity>) {
    var activeStatSubTab by remember { mutableStateOf(0) } // 0: Scorers, 1: Assists, 2: Cards

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = activeStatSubTab,
            containerColor = StadiumNight,
            contentColor = Gold,
            divider = {}
        ) {
            Tab(
                selected = activeStatSubTab == 0,
                onClick = { activeStatSubTab = 0 },
                text = { Text("آقای گل ⚽", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = activeStatSubTab == 1,
                onClick = { activeStatSubTab = 1 },
                text = { Text("پاس گل 👟", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = activeStatSubTab == 2,
                onClick = { activeStatSubTab = 2 },
                text = { Text("جریمه‌ها 🟨🟥", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
        }

        val statsList = remember(matches, activeStatSubTab) {
            when (activeStatSubTab) {
                0 -> viewModel.getScorersLeaderboard(matches)
                1 -> viewModel.getAssistsLeaderboard(matches)
                2 -> viewModel.getCardsLeaderboard(matches)
                else -> emptyList()
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (statsList.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 80.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Analytics, null, tint = GrayText, modifier = Modifier.size(50.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "آمار مسابقات هنوز ثبت نشده است",
                            color = GrayText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                itemsIndexed(statsList) { index, row ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = PitchCard),
                        border = BorderStroke(1.dp, CardHighlight),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = (index + 1).toString(),
                                color = if (index == 0) Gold else SoftWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.width(28.dp),
                                textAlign = TextAlign.Center
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = row.playerNameFa,
                                    color = SoftWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    if (row.teamNameEn != null) {
                                        TeamLogo(teamNameEn = row.teamNameEn, size = 16.dp)
                                    } else {
                                        Text(row.teamFlag, fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = row.teamFa,
                                        color = GrayText,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.width(60.dp)
                            ) {
                                Text(
                                    text = row.count.toString(),
                                    color = Gold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = when (activeStatSubTab) {
                                        0 -> "گل"
                                        1 -> "پاس"
                                        2 -> "امتیاز"
                                        else -> ""
                                    },
                                    color = GrayText,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// 6. DETAILED MATCH EVENTS REPORT DIALOG
@Composable
fun MatchDetailsDialog(
    match: MatchEntity,
    viewModel: TournamentViewModel,
    onDismiss: () -> Unit
) {
    val home = viewModel.teamsMap[match.homeTeamId]
    val away = viewModel.teamsMap[match.awayTeamId]
    val converters = remember { Converters() }
    val events = remember(match.eventsJson) { converters.toEventsList(match.eventsJson) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .padding(20.dp),
                colors = CardDefaults.cardColors(containerColor = StadiumNight),
                border = BorderStroke(1.dp, Gold),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Title Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "گزارش نهایی بازی",
                            color = Gold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, null, tint = SoftWhite)
                        }
                    }

                    Divider(color = CardHighlight)

                    // Scoreboard Header
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PitchCard)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = formatStageLabel(match.stage),
                            color = Gold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            // Home
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (home != null) {
                                    TeamLogo(teamNameEn = home.nameEn, size = 28.dp)
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color.White.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("🏳️", fontSize = 16.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    home?.nameFa ?: "ناشناس",
                                    color = SoftWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "${match.homeScore} - ${match.awayScore}",
                                color = Gold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp
                            )
                            Spacer(modifier = Modifier.width(16.dp))

                            // Away
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    away?.nameFa ?: "ناشناس",
                                    color = SoftWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                if (away != null) {
                                    TeamLogo(teamNameEn = away.nameEn, size = 28.dp)
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color.White.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("🏳️", fontSize = 16.sp)
                                    }
                                }
                            }
                        }

                        if (match.penalties) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "برد در ضربات پنالتی: (${match.homePenalties}) - (${match.awayPenalties})",
                                color = CyanAssist,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        } else if (match.extraTime) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("نتیجه پس از ۱۲۰ دقیقه وقت اضافه", color = GrayText, fontSize = 11.sp)
                        }
                    }

                    Divider(color = CardHighlight)

                    // Event Timelines
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(events) { ev ->
                            val icon = when (ev.type) {
                                EventType.GOAL -> "⚽"
                                EventType.YELLOW_CARD -> "🟨"
                                EventType.RED_CARD -> "🟥"
                                EventType.KICKOFF -> "🏁"
                                EventType.HALF_TIME -> "⏱️"
                                EventType.NEAR_MISS -> "🎯"
                                EventType.FULL_TIME -> "🏆"
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (ev.type == EventType.GOAL) FieldGreen.copy(alpha = 0.08f)
                                        else Color.Transparent,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(CardHighlight, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(icon, fontSize = 16.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = ev.commentaryFa,
                                        color = if (ev.type == EventType.GOAL) Gold else SoftWhite,
                                        fontSize = 12.sp,
                                        lineHeight = 18.sp,
                                        fontWeight = if (ev.type == EventType.GOAL) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// 7. INTERACTIVE MATCH SIMULATOR FULL-SCREEN OVERLAY
@Composable
fun InteractiveSimulatorDialog(
    state: LiveSimState,
    onStop: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()

    // Automatically scroll to bottom as new events appear
    LaunchedEffect(state.visibleEvents.size) {
        if (state.visibleEvents.isNotEmpty()) {
            scrollState.animateScrollToItem(state.visibleEvents.lastIndex)
        }
    }

    Dialog(
        onDismissRequest = { /* Don't dismiss by tap outside to protect simulation */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(StadiumNight)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header Status
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "شبیه‌سازی زنده مسابقه",
                            color = Gold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        if (state.isFinished) {
                            IconButton(onClick = onStop) {
                                Icon(Icons.Default.Close, null, tint = SoftWhite)
                            }
                        }
                    }

                    // Immersive Stadium Clock/Live stats
                    Card(
                        colors = CardDefaults.cardColors(containerColor = PitchCard),
                        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Circular clock display
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .border(2.dp, Gold, CircleShape)
                                    .background(StadiumNight, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${state.currentMinute}'",
                                        color = Gold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp
                                    )
                                    Text(
                                        text = if (state.isFinished) "پایان" else "زنده",
                                        color = if (state.isFinished) RedCardColor else FieldGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Interactive Scoreboard
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                // Home Team
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    TeamLogo(teamNameEn = state.homeTeam.nameEn, size = 48.dp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = state.homeTeam.nameFa,
                                        color = SoftWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                // Interactive Score display
                                Text(
                                    text = "${state.homeScore} - ${state.awayScore}",
                                    color = Gold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 40.sp,
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                )

                                // Away Team
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    TeamLogo(teamNameEn = state.awayTeam.nameEn, size = 48.dp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = state.awayTeam.nameFa,
                                        color = SoftWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            if (state.isFinished && state.result.penalties) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "ضربات پنالتی: (${state.result.homePenalties}) - (${state.result.awayPenalties})",
                                    color = CyanAssist,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    // Live Event list
                    Text(
                        text = "گزارش زنده لحظه به لحظه:",
                        color = SoftWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
                    )

                    LazyColumn(
                        state = scrollState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.visibleEvents) { ev ->
                            val icon = when (ev.type) {
                                EventType.GOAL -> "⚽"
                                EventType.YELLOW_CARD -> "🟨"
                                EventType.RED_CARD -> "🟥"
                                EventType.KICKOFF -> "🏁"
                                EventType.HALF_TIME -> "⏱️"
                                EventType.NEAR_MISS -> "🎯"
                                EventType.FULL_TIME -> "🏆"
                            }

                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (ev.type == EventType.GOAL) FieldGreen.copy(alpha = 0.15f) else PitchCard
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    if (ev.type == EventType.GOAL) Gold else CardHighlight
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(icon, fontSize = 16.sp, modifier = Modifier.padding(end = 10.dp))
                                    Column {
                                        Text(
                                            text = ev.commentaryFa,
                                            color = if (ev.type == EventType.GOAL) Gold else SoftWhite,
                                            fontSize = 12.sp,
                                            fontWeight = if (ev.type == EventType.GOAL) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Lower actions (Continue button)
                    if (state.isFinished) {
                        Surface(
                            color = PitchCard,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = onStop,
                                colors = ButtonDefaults.buttonColors(containerColor = Gold),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text("ثبت نتیجه و بازگشت", color = StadiumNight, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Helpers
fun formatStageLabel(stage: String): String {
    if (stage.startsWith("GROUP_")) {
        val groupName = stage.substringAfter("GROUP_")
        return "مرحله گروهی - گروه $groupName"
    }
    return when (stage) {
        "R32" -> "مرحله یک‌سی‌ودوم نهایی"
        "R16" -> "مرحله یک‌هشتم نهایی"
        "QF" -> "یک‌چهارم نهایی"
        "SF" -> "نیمه نهایی"
        "THIRD_PLACE" -> "رده‌بندی رتبه سوم"
        "FINAL" -> "مسابقه بزرگ فینال"
        else -> stage
    }
}
