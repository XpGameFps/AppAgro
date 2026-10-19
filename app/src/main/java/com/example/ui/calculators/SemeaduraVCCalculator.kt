package com.example.ui.calculators

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.AgroFormulaCard
import com.example.ui.components.AgroNumberField
import com.example.ui.components.AgroResultCard
import com.example.ui.theme.AgroGreenPrimary
import com.example.ui.viewmodel.AgroViewModel
import java.text.DecimalFormat

@Composable
fun SemeaduraVCView(
    viewModel: AgroViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Grãos (Germinação & Emergência)", "Forrageiras (Valor Cultural VC%)")

    val formatter = remember { DecimalFormat("#,##0") }
    val decimalFormatter = remember { DecimalFormat("#,##0.00") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 0.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = AgroGreenPrimary
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) AgroGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }
        }

        if (selectedTab == 0) {
            item {
                GraosSemeaduraTab(viewModel, formatter, decimalFormatter)
            }
        } else {
            item {
                ForrageirasVCTab(viewModel, formatter, decimalFormatter)
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun GraosSemeaduraTab(
    viewModel: AgroViewModel,
    formatter: DecimalFormat,
    decimalFormatter: DecimalFormat
) {
    var popAlvoInput by remember { mutableStateOf("260000") }
    var germinacaoInput by remember { mutableStateOf("90") }
    var emergenciaInput by remember { mutableStateOf("85") }
    var pmsInput by remember { mutableStateOf("165") }
    var espacamentoInput by remember { mutableStateOf("0.45") }
    var areaHaInput by remember { mutableStateOf("25") }

    val popAlvo = popAlvoInput.toDoubleOrNull() ?: 260000.0
    val germPercent = germinacaoInput.toDoubleOrNull() ?: 90.0
    val emergPercent = emergenciaInput.toDoubleOrNull() ?: 85.0
    val pms = pmsInput.toDoubleOrNull() ?: 165.0
    val espLinhas = espacamentoInput.toDoubleOrNull() ?: 0.45
    val areaHa = areaHaInput.toDoubleOrNull() ?: 25.0

    val germDecimal = germPercent / 100.0
    val emergDecimal = emergPercent / 100.0
    val fatorSobrevivencia = germDecimal * emergDecimal

    val sementesPorHa = if (fatorSobrevivencia > 0) popAlvo / fatorSobrevivencia else 0.0
    val sementesTotal = sementesPorHa * areaHa

    // Massa em kg/ha = (Sementes/ha / 1000) * PMS(g) / 1000 = Sementes * PMS / 1.000.000
    val kgPorHa = (sementesPorHa * pms) / 1000000.0
    val kgTotal = kgPorHa * areaHa

    // Sementes por metro linear
    val metrosLinhaPorHa = if (espLinhas > 0) 10000.0 / espLinhas else 0.0
    val sementesPorMetro = if (metrosLinhaPorHa > 0) sementesPorHa / metrosLinhaPorHa else 0.0

    val sacas40kg = if (kgTotal > 0) Math.ceil(kgTotal / 40.0).toInt() else 0
    val sacas60kg = if (kgTotal > 0) Math.ceil(kgTotal / 60.0).toInt() else 0

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        AgroFormulaCard(
            formulaName = "Cálculo de Sementes e Estande Alvo (Embrapa)",
            formulaMath = "Sementes/ha = População Desejada ÷ (Germinação decimal × Emergência decimal)\nMassa (kg/ha) = Sementes/ha × PMS(g) ÷ 1.000.000",
            description = "Compensa perdas de campo por profundidade, crosta, patógenos e vigor das sementes para atingir o estande final."
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AgroNumberField(
                value = popAlvoInput,
                onValueChange = { popAlvoInput = it },
                label = "População Alvo Final",
                suffix = "pl/ha",
                helperText = "Ex: 60.000 milho ou 260.000 soja",
                modifier = Modifier.weight(1f)
            )
            AgroNumberField(
                value = pmsInput,
                onValueChange = { pmsInput = it },
                label = "PMS (Peso Mil Sementes)",
                suffix = "g",
                helperText = "Peso de 1.000 sementes no saco",
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AgroNumberField(
                value = germinacaoInput,
                onValueChange = { germinacaoInput = it },
                label = "Germinação do Laudo",
                suffix = "%",
                helperText = "Mínimo legal ou laudo (ex: 90%)",
                modifier = Modifier.weight(1f)
            )
            AgroNumberField(
                value = emergenciaInput,
                onValueChange = { emergenciaInput = it },
                label = "Emergência de Campo",
                suffix = "%",
                helperText = "Histórico de vigor do solo (ex: 85%)",
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AgroNumberField(
                value = espacamentoInput,
                onValueChange = { espacamentoInput = it },
                label = "Espaçamento Linhas",
                suffix = "m",
                helperText = "Ex: 0.45 ou 0.50 m",
                modifier = Modifier.weight(1f)
            )
            AgroNumberField(
                value = areaHaInput,
                onValueChange = { areaHaInput = it },
                label = "Área da Lavoura",
                suffix = "ha",
                helperText = "Hectares totais",
                modifier = Modifier.weight(1f)
            )
        }

        AgroResultCard(
            title = "Necessidade de Sementes",
            mainValue = formatter.format(sementesPorHa),
            mainUnit = "sementes / ha",
            secondaryMetrics = listOf(
                "Massa de Sementes por Hectare" to "${decimalFormatter.format(kgPorHa)} kg/ha",
                "Massa Total para a Área (${decimalFormatter.format(areaHa)} ha)" to "${formatter.format(kgTotal)} kg (${sacas40kg} sacas de 40kg)",
                "Regulagem da Semeadora (Sementes/m)" to "${decimalFormatter.format(sementesPorMetro)} sementes/metro linear",
                "Total de Sementes a Comprar" to "${formatter.format(sementesTotal)} sementes",
                "Sobrevivência Estimada (G × E)" to "${decimalFormatter.format(fatorSobrevivencia * 100)}%"
            ),
            formulaExplanation = "${formatter.format(popAlvo)} ÷ (${germDecimal} × ${emergDecimal}) = ${formatter.format(sementesPorHa)} sementes/ha",
            onSaveResult = {
                viewModel.saveCalculo(
                    tipo = "SEMEADURA",
                    titulo = "Planejamento de Semeadura",
                    inputs = "Alvo: ${popAlvo} pl/ha | G: ${germPercent}% | E: ${emergPercent}% | PMS: ${pms}g",
                    resultado = "${formatter.format(sementesPorHa)} sem/ha | ${decimalFormatter.format(kgPorHa)} kg/ha | Total: ${formatter.format(kgTotal)} kg"
                )
            }
        )
    }
}

@Composable
private fun ForrageirasVCTab(
    viewModel: AgroViewModel,
    formatter: DecimalFormat,
    decimalFormatter: DecimalFormat
) {
    var purezaInput by remember { mutableStateOf("98") }
    var germinacaoInput by remember { mutableStateOf("80") }
    var spvRecomendadaInput by remember { mutableStateOf("15") }
    var areaHaInput by remember { mutableStateOf("10") }

    val pureza = purezaInput.toDoubleOrNull() ?: 98.0
    val germ = germinacaoInput.toDoubleOrNull() ?: 80.0
    val spv = spvRecomendadaInput.toDoubleOrNull() ?: 15.0
    val area = areaHaInput.toDoubleOrNull() ?: 10.0

    val valorCultural = (pureza * germ) / 100.0
    val vcDecimal = valorCultural / 100.0

    val sementesComerciaisKgHa = if (vcDecimal > 0) spv / vcDecimal else 0.0
    val kgTotal = sementesComerciaisKgHa * area

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        AgroFormulaCard(
            formulaName = "Valor Cultural (VC%) e Sementes Forrageiras",
            formulaMath = "VC (%) = (Pureza % × Germinação %) ÷ 100\nSementes Comerciais (kg/ha) = SPV recomendada (kg/ha) ÷ (VC ÷ 100)",
            description = "Sementes de pastagem (Braquiária, Panicum) são comercializadas por ponto de Valor Cultural."
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AgroNumberField(
                value = purezaInput,
                onValueChange = { purezaInput = it },
                label = "Pureza Física (P)",
                suffix = "%",
                helperText = "Ex: 95% a 98%",
                modifier = Modifier.weight(1f)
            )
            AgroNumberField(
                value = germinacaoInput,
                onValueChange = { germinacaoInput = it },
                label = "Germinação (G)",
                suffix = "%",
                helperText = "Ex: 75% a 85%",
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AgroNumberField(
                value = spvRecomendadaInput,
                onValueChange = { spvRecomendadaInput = it },
                label = "Recomendação SPV",
                suffix = "kg/ha",
                helperText = "Sementes Puras Viáveis/ha",
                modifier = Modifier.weight(1f)
            )
            AgroNumberField(
                value = areaHaInput,
                onValueChange = { areaHaInput = it },
                label = "Área da Pastagem",
                suffix = "ha",
                modifier = Modifier.weight(1f)
            )
        }

        AgroResultCard(
            title = "Valor Cultural (VC%) Calculado",
            mainValue = "${decimalFormatter.format(valorCultural)}%",
            mainUnit = "VC",
            secondaryMetrics = listOf(
                "Semente Comercial a Semear" to "${decimalFormatter.format(sementesComerciaisKgHa)} kg/ha",
                "Total para o Piquete (${decimalFormatter.format(area)} ha)" to "${formatter.format(kgTotal)} kg",
                "Pontos de VC por Hectare" to "${formatter.format(spv * 100)} pontos"
            ),
            formulaExplanation = "(${pureza}% × ${germ}%) ÷ 100 = ${decimalFormatter.format(valorCultural)}% VC",
            onSaveResult = {
                viewModel.saveCalculo(
                    tipo = "SEMEADURA",
                    titulo = "Forrageiras Valor Cultural VC%",
                    inputs = "Pureza: ${pureza}% | Germinação: ${germ}% | SPV: ${spv} kg/ha",
                    resultado = "VC: ${decimalFormatter.format(valorCultural)}% | Semear: ${decimalFormatter.format(sementesComerciaisKgHa)} kg/ha"
                )
            }
        )
    }
}
