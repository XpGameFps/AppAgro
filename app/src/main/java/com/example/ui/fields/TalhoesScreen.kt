package com.example.ui.fields

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.CalculoSalvoEntity
import com.example.data.local.TalhaoEntity
import com.example.data.local.VistoriaEntity
import com.example.ui.components.AgroNumberField
import com.example.ui.components.AgroStatusBadge
import com.example.ui.theme.AgroEarth
import com.example.ui.theme.AgroGreenContainerLight
import com.example.ui.theme.AgroGreenDark
import com.example.ui.theme.AgroGreenPrimary
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusGreenBg
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusRedBg
import com.example.ui.theme.StatusYellow
import com.example.ui.theme.StatusYellowBg
import com.example.ui.viewmodel.AgroViewModel
import java.text.DecimalFormat

@Composable
fun TalhoesScreen(
    viewModel: AgroViewModel,
    modifier: Modifier = Modifier
) {
    val talhoes by viewModel.allTalhoes.collectAsStateWithLifecycle()
    val vistorias by viewModel.allVistorias.collectAsStateWithLifecycle()
    val calculosSalvos by viewModel.allCalculosSalvos.collectAsStateWithLifecycle()
    val selectedTalhao by viewModel.selectedTalhao.collectAsStateWithLifecycle()

    var showNewTalhaoDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Talhões, 1: Histórico de Vistorias, 2: Cálculos Salvos

    if (selectedTalhao != null) {
        TalhaoDetailView(
            talhao = selectedTalhao!!,
            allVistorias = vistorias.filter { it.talhaoId == selectedTalhao!!.id },
            onBack = { viewModel.selectTalhao(null) },
            onDelete = {
                viewModel.deleteTalhao(selectedTalhao!!.id)
            },
            onAddVistoria = { vistoria ->
                viewModel.saveVistoria(vistoria)
            },
            onDeleteVistoria = { vId ->
                viewModel.deleteVistoria(vId)
            }
        )
    } else {
        Scaffold(
            floatingActionButton = {
                if (selectedTab == 0) {
                    FloatingActionButton(
                        onClick = { showNewTalhaoDialog = true },
                        containerColor = AgroGreenPrimary,
                        contentColor = Color.White,
                        modifier = Modifier.testTag("fab_novo_talhao")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Novo Talhão", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    edgePadding = 16.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = AgroGreenPrimary
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "Meus Talhões (${talhoes.size})",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 0) AgroGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "Vistorias MIP (${vistorias.size})",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 1) AgroGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Text(
                                "Cálculos Salvos (${calculosSalvos.size})",
                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 2) AgroGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    when (selectedTab) {
                        0 -> TalhoesListContent(
                            talhoes = talhoes,
                            onSelectTalhao = { viewModel.selectTalhao(it) },
                            onNewTalhao = { showNewTalhaoDialog = true }
                        )
                        1 -> AllVistoriasListContent(
                            vistorias = vistorias,
                            onDeleteVistoria = { viewModel.deleteVistoria(it) }
                        )
                        2 -> SavedCalculationsContent(
                            calculos = calculosSalvos,
                            onDeleteCalculo = { viewModel.deleteCalculo(it) }
                        )
                    }
                }
            }
        }
    }

    if (showNewTalhaoDialog) {
        NewTalhaoDialog(
            onDismiss = { showNewTalhaoDialog = false },
            onConfirm = { newTalhao ->
                viewModel.saveTalhao(newTalhao)
                showNewTalhaoDialog = false
            }
        )
    }
}

@Composable
private fun TalhoesListContent(
    talhoes: List<TalhaoEntity>,
    onSelectTalhao: (TalhaoEntity) -> Unit,
    onNewTalhao: () -> Unit
) {
    val formatter = remember { DecimalFormat("#,##0.0") }

    if (talhoes.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Default.Agriculture,
                    contentDescription = null,
                    tint = AgroGreenPrimary,
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    text = "Nenhum talhão cadastrado",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Cadastre sua área para acompanhar vistorias e cálculos de estande.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = onNewTalhao,
                    colors = ButtonDefaults.buttonColors(containerColor = AgroGreenPrimary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Adicionar Primeiro Talhão")
                }
            }
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Talhões em Acompanhamento (Offline)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(talhoes) { talhao ->
                Card(
                    onClick = { onSelectTalhao(talhao) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AgroGreenContainerLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Place,
                                contentDescription = null,
                                tint = AgroGreenDark,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = talhao.nome,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.secondaryContainer)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = talhao.cultura,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                            if (talhao.fazenda.isNotEmpty()) {
                                Text(
                                    text = talhao.fazenda,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Text(
                                    text = "${formatter.format(talhao.areaHa)} ha",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AgroGreenPrimary
                                )
                                if (talhao.cultivar.isNotEmpty()) {
                                    Text(
                                        text = talhao.cultivar,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "Linhas: ${talhao.espacamentoLinhasM}m",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TalhaoDetailView(
    talhao: TalhaoEntity,
    allVistorias: List<VistoriaEntity>,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onAddVistoria: (VistoriaEntity) -> Unit,
    onDeleteVistoria: (Long) -> Unit
) {
    val formatter = remember { DecimalFormat("#,##0.0") }
    val intFormatter = remember { DecimalFormat("#,##0") }
    var showVistoriaDialog by remember { mutableStateOf(false) }

    // Comprimento total de linhas da gleba = (10.000 ÷ espaçamento) × área
    val metrosLinhaPorHa = if (talhao.espacamentoLinhasM > 0) 10000.0 / talhao.espacamentoLinhasM else 0.0
    val metrosLinhaTotal = metrosLinhaPorHa * talhao.areaHa

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(talhao.nome, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Excluir Talhão", tint = StatusRed)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showVistoriaDialog = true },
                containerColor = AgroGreenPrimary,
                contentColor = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Nova Vistoria MIP", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AgroGreenContainerLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "FICHA GERAL DO TALHÃO",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = AgroGreenDark
                            )
                            Text(
                                text = talhao.cultura,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = AgroGreenPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Área Total", style = MaterialTheme.typography.labelSmall, color = AgroGreenDark)
                                Text("${formatter.format(talhao.areaHa)} ha", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = AgroGreenDark)
                            }
                            Column {
                                Text("Espaçamento", style = MaterialTheme.typography.labelSmall, color = AgroGreenDark)
                                Text("${talhao.espacamentoLinhasM} m", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = AgroGreenDark)
                            }
                            Column {
                                Text("Plantio", style = MaterialTheme.typography.labelSmall, color = AgroGreenDark)
                                Text(talhao.dataPlantio.ifEmpty { "N/A" }, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = AgroGreenDark)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(10.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Cultivar / Híbrido:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(talhao.cultivar.ifEmpty { "Não informado" }, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Metros de linha por hectare:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${intFormatter.format(metrosLinhaPorHa)} m/ha", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Comprimento total de linhas:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${intFormatter.format(metrosLinhaTotal)} metros", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Vistorias e Monitoramentos MIP (${allVistorias.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (allVistorias.isEmpty()) {
                item {
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Visibility, contentDescription = null, tint = AgroGreenPrimary, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Nenhuma vistoria registrada neste talhão.", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Text("Toque em 'Nova Vistoria MIP' para registrar amostragens de pragas, pano de batida ou dano foliar.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                items(allVistorias) { vistoria ->
                    VistoriaItemCard(vistoria = vistoria, onDelete = { onDeleteVistoria(vistoria.id) })
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    if (showVistoriaDialog) {
        NewVistoriaDialog(
            talhao = talhao,
            onDismiss = { showVistoriaDialog = false },
            onConfirm = { vistoria ->
                onAddVistoria(vistoria)
                showVistoriaDialog = false
            }
        )
    }
}

@Composable
private fun VistoriaItemCard(
    vistoria: VistoriaEntity,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = AgroGreenPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = vistoria.data,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AgroStatusBadge(status = vistoria.statusNivelControle)
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = vistoria.pragaOuAlvo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Estádio: ${vistoria.estadio} • Amostragem: ${vistoria.metodoAmostragem}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Pontos", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${vistoria.pontosAvaliados}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Média / Ponto", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${vistoria.mediaPorPonto}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Dano Foliar", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${vistoria.porcentagemDano}%", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
            }

            if (vistoria.inimigosNaturais.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Inimigos Naturais: ${vistoria.inimigosNaturais}",
                    style = MaterialTheme.typography.labelSmall,
                    color = StatusGreen
                )
            }

            if (vistoria.decisaoTomada.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Decisão / Manejo: ${vistoria.decisaoTomada}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = AgroGreenDark
                )
            }
        }
    }
}

@Composable
private fun AllVistoriasListContent(
    vistorias: List<VistoriaEntity>,
    onDeleteVistoria: (Long) -> Unit
) {
    if (vistorias.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Nenhuma vistoria MIP registrada ainda.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Histórico Geral de Amostragens MIP", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            items(vistorias) { v ->
                VistoriaItemCard(vistoria = v, onDelete = { onDeleteVistoria(v.id) })
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SavedCalculationsContent(
    calculos: List<CalculoSalvoEntity>,
    onDeleteCalculo: (Long) -> Unit
) {
    if (calculos.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Nenhum cálculo salvo ainda. Ao executar uma calculadora, toque em 'Salvar no Histórico'.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Cálculos Salvos no Dispositivo (Offline)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            items(calculos) { calc ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = calc.titulo,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { onDeleteCalculo(calc.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.outline)
                            }
                        }
                        Text(
                            text = "Entradas: ${calc.resumoInputs}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(AgroGreenContainerLight)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = calc.resumoResultado,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = AgroGreenDark
                            )
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun NewTalhaoDialog(
    onDismiss: () -> Unit,
    onConfirm: (TalhaoEntity) -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var fazenda by remember { mutableStateOf("") }
    var areaHa by remember { mutableStateOf("") }
    var cultura by remember { mutableStateOf("Soja") }
    var cultivar by remember { mutableStateOf("") }
    var dataPlantio by remember { mutableStateOf("") }
    var espacamento by remember { mutableStateOf("0.50") }
    var populacaoAlvo by remember { mutableStateOf("260000") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novo Talhão de Lavoura", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome / Identificação do Talhão") },
                    placeholder = { Text("Ex: Talhão 02 - Pivô") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = fazenda,
                    onValueChange = { fazenda = it },
                    label = { Text("Propriedade / Fazenda") },
                    placeholder = { Text("Ex: Fazenda Alvorada") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AgroNumberField(
                        value = areaHa,
                        onValueChange = { areaHa = it },
                        label = "Área (ha)",
                        suffix = "ha",
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = cultura,
                        onValueChange = { cultura = it },
                        label = { Text("Cultura") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = cultivar,
                        onValueChange = { cultivar = it },
                        label = { Text("Cultivar / Híbrido") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    AgroNumberField(
                        value = espacamento,
                        onValueChange = { espacamento = it },
                        label = "Espaçamento",
                        suffix = "m",
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = dataPlantio,
                        onValueChange = { dataPlantio = it },
                        label = { Text("Data Plantio") },
                        placeholder = { Text("dd/mm/aaaa") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    AgroNumberField(
                        value = populacaoAlvo,
                        onValueChange = { populacaoAlvo = it },
                        label = "Alvo pl/ha",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nome.isNotEmpty()) {
                        onConfirm(
                            TalhaoEntity(
                                nome = nome,
                                fazenda = fazenda,
                                areaHa = areaHa.toDoubleOrNull() ?: 1.0,
                                cultura = cultura,
                                cultivar = cultivar,
                                dataPlantio = dataPlantio,
                                espacamentoLinhasM = espacamento.toDoubleOrNull() ?: 0.50,
                                populacaoAlvoHa = populacaoAlvo.toDoubleOrNull() ?: 0.0
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AgroGreenPrimary)
            ) {
                Text("Salvar Talhão")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun NewVistoriaDialog(
    talhao: TalhaoEntity,
    onDismiss: () -> Unit,
    onConfirm: (VistoriaEntity) -> Unit
) {
    var data by remember { mutableStateOf("19/10/2026") }
    var estadio by remember { mutableStateOf("R3") }
    var pragaAlvo by remember { mutableStateOf("Percevejo-marrom") }
    var metodo by remember { mutableStateOf("Pano de batida (1m)") }
    var pontos by remember { mutableStateOf("8") }
    var mediaPorPonto by remember { mutableStateOf("1.5") }
    var danoPercent by remember { mutableStateOf("5") }
    var inimigosNaturais by remember { mutableStateOf("Joaninhas presentes") }
    var statusNivel by remember { mutableStateOf("ATENCAO") }
    var decisao by remember { mutableStateOf("Monitorar em 4 dias") }

    // Auto calculate status recommendation based on crop standards
    val media = mediaPorPonto.toDoubleOrNull() ?: 0.0
    val dano = danoPercent.toDoubleOrNull() ?: 0.0

    val statusCalculado = when {
        talhao.cultura.contains("Soja", ignoreCase = true) && pragaAlvo.contains("Percevejo", ignoreCase = true) && media >= 2.0 -> "ACAO_NECESSARIA"
        talhao.cultura.contains("Soja", ignoreCase = true) && dano >= 15.0 -> "ACAO_NECESSARIA"
        talhao.cultura.contains("Milho", ignoreCase = true) && dano >= 10.0 -> "ACAO_NECESSARIA"
        media > 0.8 || dano >= 8.0 -> "ATENCAO"
        else -> "EQUILIBRIO"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nova Vistoria MIP - ${talhao.nome}", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = data,
                        onValueChange = { data = it },
                        label = { Text("Data") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = estadio,
                        onValueChange = { estadio = it },
                        label = { Text("Estádio") },
                        placeholder = { Text("ex: V4, R3") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = pragaAlvo,
                    onValueChange = { pragaAlvo = it },
                    label = { Text("Praga ou Inseto Observado") },
                    placeholder = { Text("ex: Percevejo-marrom, Lagarta") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = metodo,
                    onValueChange = { metodo = it },
                    label = { Text("Método de Amostragem") },
                    placeholder = { Text("Pano de batida, visual, armadilha") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AgroNumberField(
                        value = pontos,
                        onValueChange = { pontos = it },
                        label = "Pontos Amostrados",
                        isDecimal = false,
                        modifier = Modifier.weight(1f)
                    )
                    AgroNumberField(
                        value = mediaPorPonto,
                        onValueChange = { mediaPorPonto = it },
                        label = "Média / Ponto",
                        modifier = Modifier.weight(1f)
                    )
                    AgroNumberField(
                        value = danoPercent,
                        onValueChange = { danoPercent = it },
                        label = "Dano (%)",
                        suffix = "%",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Recomendação MIP
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when (statusCalculado) {
                                "ACAO_NECESSARIA" -> StatusRedBg
                                "ATENCAO" -> StatusYellowBg
                                else -> StatusGreenBg
                            }
                        )
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Status Sugerido: ${
                            when (statusCalculado) {
                                "ACAO_NECESSARIA" -> "Nível de Ação Ultrapassado (Controle Recomendado)"
                                "ATENCAO" -> "Atenção (Monitorar de perto)"
                                else -> "População em Equilíbrio"
                            }
                        }",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (statusCalculado) {
                            "ACAO_NECESSARIA" -> StatusRed
                            "ATENCAO" -> StatusYellow
                            else -> StatusGreen
                        }
                    )
                }

                OutlinedTextField(
                    value = inimigosNaturais,
                    onValueChange = { inimigosNaturais = it },
                    label = { Text("Inimigos Naturais Observados") },
                    placeholder = { Text("ex: Joaninhas, tesourinhas") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = decisao,
                    onValueChange = { decisao = it },
                    label = { Text("Decisão / Próxima Ação") },
                    placeholder = { Text("ex: Programar rotação de MoA") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        VistoriaEntity(
                            talhaoId = talhao.id,
                            talhaoNome = talhao.nome,
                            data = data,
                            estadio = estadio,
                            pragaOuAlvo = pragaAlvo,
                            metodoAmostragem = metodo,
                            pontosAvaliados = pontos.toIntOrNull() ?: 1,
                            mediaPorPonto = media,
                            porcentagemDano = dano,
                            inimigosNaturais = inimigosNaturais,
                            statusNivelControle = statusCalculado,
                            decisaoTomada = decisao
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = AgroGreenPrimary)
            ) {
                Text("Registrar Vistoria")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
