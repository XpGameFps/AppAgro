package com.example.ui.calculators

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AgroEarth
import com.example.ui.theme.AgroGold
import com.example.ui.theme.AgroGreenContainerLight
import com.example.ui.theme.AgroGreenDark
import com.example.ui.theme.AgroGreenPrimary
import com.example.ui.viewmodel.AgroViewModel

enum class CalculatorType(
    val title: String,
    val subtitle: String,
    val category: String,
    val icon: ImageVector,
    val tag: String
) {
    POPULACAO(
        "População & Espaçamento",
        "Linhas simples, plantas/metro, canteiros e quincôncio",
        "Plantio",
        Icons.Default.Eco,
        "calc_populacao"
    ),
    SEMEADURA(
        "Sementes & Estande Alvo",
        "Germinação, emergência, PMS, quilos/ha e regulagem",
        "Plantio",
        Icons.Default.Grain,
        "calc_semeadura"
    ),
    ESTANDE_REAL(
        "Conferência de Estande",
        "Aferição pós-emergência por metro ou área amostrada",
        "Plantio",
        Icons.Default.Straighten,
        "calc_estande_real"
    ),
    PRODUTIVIDADE(
        "Estimativa de Produtividade",
        "Soja (vagens/grãos), milho (espigas) e amostragem direta",
        "Colheita",
        Icons.Default.Agriculture,
        "calc_produtividade"
    ),
    UMIDADE_QUEBRA(
        "Correção de Umidade & Quebra",
        "Padronização comercial (ex: 14%), impurezas e peso líquido",
        "Colheita",
        Icons.Default.Opacity,
        "calc_umidade"
    ),
    PERDAS_COLHEITA(
        "Perdas na Colheita (Embrapa)",
        "Armação de 2m², perdas de plataforma e internas com tolerância",
        "Colheita",
        Icons.Default.Speed,
        "calc_perdas"
    ),
    ADUBACAO_NPK(
        "Fertilizantes & Nutrientes",
        "Dose de produto comercial por teor, NPK e complementação",
        "Nutrição",
        Icons.Default.Science,
        "calc_adubacao"
    ),
    IRRIGACAO_PULVERIZACAO(
        "Água, Irrigação & Calda",
        "Volume de lâmina d'água, chuva em m³ e calibração de pontas",
        "Manejo",
        Icons.Default.WaterDrop,
        "calc_irrigacao"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorsScreen(
    viewModel: AgroViewModel,
    modifier: Modifier = Modifier
) {
    var selectedCalculator by remember { mutableStateOf<CalculatorType?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Todos") }

    val categories = listOf("Todos", "Plantio", "Colheita", "Nutrição", "Manejo")

    if (selectedCalculator != null) {
        val calc = selectedCalculator!!
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(calc.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(calc.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { selectedCalculator = null }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                when (calc) {
                    CalculatorType.POPULACAO -> PopulacaoEspacamentoView(viewModel)
                    CalculatorType.SEMEADURA -> SemeaduraVCView(viewModel)
                    CalculatorType.ESTANDE_REAL -> EstandeRealView(viewModel)
                    CalculatorType.PRODUTIVIDADE -> ProdutividadeView(viewModel)
                    CalculatorType.UMIDADE_QUEBRA -> UmidadeQuebraView(viewModel)
                    CalculatorType.PERDAS_COLHEITA -> PerdaColheitaView(viewModel)
                    CalculatorType.ADUBACAO_NPK -> FertilizantesAdubacaoView(viewModel)
                    CalculatorType.IRRIGACAO_PULVERIZACAO -> IrrigacaoPulverizacaoView(viewModel)
                }
            }
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // Search field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar cálculo (ex: soja, sementes, perdas, NPK)...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AgroGreenPrimary) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AgroGreenPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                // Category Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { category ->
                        val isSelected = selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = category },
                            label = { Text(category, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AgroGreenPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            val filtered = CalculatorType.values().filter { item ->
                (selectedCategory == "Todos" || item.category == selectedCategory) &&
                (searchQuery.isEmpty() || item.title.contains(searchQuery, ignoreCase = true) || item.subtitle.contains(searchQuery, ignoreCase = true))
            }

            items(filtered) { calc ->
                CalculatorCard(
                    calculator = calc,
                    onClick = { selectedCalculator = calc }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun CalculatorCard(
    calculator: CalculatorType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag(calculator.tag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(AgroGreenContainerLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = calculator.icon,
                    contentDescription = null,
                    tint = AgroGreenDark,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = calculator.category.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgroGreenPrimary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = calculator.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = calculator.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
