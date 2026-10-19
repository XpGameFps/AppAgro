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
fun ProdutividadeView(
    viewModel: AgroViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Soja (Vagens & Grãos)", "Milho (Espigas & Grãos)", "Amostragem em m²", "Colheita Direta")

    val formatter = remember { DecimalFormat("#,##0") }
    val decimalFormatter = remember { DecimalFormat("#,##0.0") }

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

        when (selectedTab) {
            0 -> item { SojaEstimativaTab(viewModel, formatter, decimalFormatter) }
            1 -> item { MilhoEstimativaTab(viewModel, formatter, decimalFormatter) }
            2 -> item { AmostragemM2Tab(viewModel, formatter, decimalFormatter) }
            3 -> item { ColheitaDiretaTab(viewModel, formatter, decimalFormatter) }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SojaEstimativaTab(
    viewModel: AgroViewModel,
    formatter: DecimalFormat,
    decimalFormatter: DecimalFormat
) {
    var plantasHaInput by remember { mutableStateOf("260000") }
    var vagensPorPlantaInput by remember { mutableStateOf("45") }
    var graosPorVagemInput by remember { mutableStateOf("2.2") }
    var pmsInput by remember { mutableStateOf("170") }
    var pesoSacaInput by remember { mutableStateOf("60") }

    val plantasHa = plantasHaInput.toDoubleOrNull() ?: 260000.0
    val vagensPlanta = vagensPorPlantaInput.toDoubleOrNull() ?: 45.0
    val graosVagem = graosPorVagemInput.toDoubleOrNull() ?: 2.2
    val pms = pmsInput.toDoubleOrNull() ?: 170.0
    val pesoSaca = pesoSacaInput.toDoubleOrNull() ?: 60.0

    // Produção (kg/ha) = plantas/ha × vagens/planta × grãos/vagem × PMS ÷ 1.000.000
    val kgPorHa = (plantasHa * vagensPlanta * graosVagem * pms) / 1000000.0
    val sacasPorHa = if (pesoSaca > 0) kgPorHa / pesoSaca else 0.0

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        AgroFormulaCard(
            formulaName = "Estimativa de Safra de Soja (Embrapa)",
            formulaMath = "Produção (kg/ha) = Plantas/ha × Vagens/planta × Grãos/vagem × PMS(g) ÷ 1.000.000\nSacas/ha = Produção (kg/ha) ÷ 60",
            description = "Amostre plantas de pontos representativos do interior do talhão, descartando bordaduras e manchas anômalas."
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AgroNumberField(
                value = plantasHaInput,
                onValueChange = { plantasHaInput = it },
                label = "Estande de Plantas",
                suffix = "pl/ha",
                helperText = "Ex: 260.000 pl/ha",
                modifier = Modifier.weight(1f)
            )
            AgroNumberField(
                value = vagensPorPlantaInput,
                onValueChange = { vagensPorPlantaInput = it },
                label = "Vagens por Planta",
                suffix = "vagens",
                helperText = "Média de vagens férteis",
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AgroNumberField(
                value = graosPorVagemInput,
                onValueChange = { graosPorVagemInput = it },
                label = "Grãos por Vagem",
                suffix = "grãos",
                helperText = "Média (ex: 2.1 a 2.4)",
                modifier = Modifier.weight(1f)
            )
            AgroNumberField(
                value = pmsInput,
                onValueChange = { pmsInput = it },
                label = "PMS Esperado",
                suffix = "g",
                helperText = "Peso de 1.000 grãos (ex: 160-180g)",
                modifier = Modifier.weight(1f)
            )
        }

        AgroResultCard(
            title = "Produtividade Estimada da Soja",
            mainValue = decimalFormatter.format(sacasPorHa),
            mainUnit = "sacas / ha",
            secondaryMetrics = listOf(
                "Produtividade em Massa" to "${formatter.format(kgPorHa)} kg/ha",
                "Grãos por Metro Quadrado" to "${formatter.format((plantasHa * vagensPlanta * graosVagem) / 10000.0)} grãos / m²",
                "Número de Grãos por Planta" to "${decimalFormatter.format(vagensPlanta * graosVagem)} grãos/planta",
                "Padrão Comercial" to "Sacas de ${pesoSaca.toInt()} kg a 14% de umidade"
            ),
            formulaExplanation = "${formatter.format(plantasHa)} × ${vagensPlanta} × ${graosVagem} × ${pms}g ÷ 1.000.000 = ${formatter.format(kgPorHa)} kg/ha",
            onSaveResult = {
                viewModel.saveCalculo(
                    tipo = "PRODUTIVIDADE",
                    titulo = "Estimativa de Safra Soja",
                    inputs = "${formatter.format(plantasHa)} pl/ha | ${vagensPlanta} vag/pl | ${graosVagem} gr/vag | PMS: ${pms}g",
                    resultado = "${decimalFormatter.format(sacasPorHa)} sc/ha (${formatter.format(kgPorHa)} kg/ha)"
                )
            }
        )
    }
}

@Composable
private fun MilhoEstimativaTab(
    viewModel: AgroViewModel,
    formatter: DecimalFormat,
    decimalFormatter: DecimalFormat
) {
    var plantasHaInput by remember { mutableStateOf("60000") }
    var espigasPorPlantaInput by remember { mutableStateOf("1.0") }
    var graosPorEspigaInput by remember { mutableStateOf("500") }
    var pmsInput by remember { mutableStateOf("340") }
    var pesoSacaInput by remember { mutableStateOf("60") }

    val plantasHa = plantasHaInput.toDoubleOrNull() ?: 60000.0
    val espigasPlanta = espigasPorPlantaInput.toDoubleOrNull() ?: 1.0
    val graosEspiga = graosPorEspigaInput.toDoubleOrNull() ?: 500.0
    val pms = pmsInput.toDoubleOrNull() ?: 340.0
    val pesoSaca = pesoSacaInput.toDoubleOrNull() ?: 60.0

    // Produção (kg/ha) = plantas/ha × espigas/planta × grãos/espiga × PMS ÷ 1.000.000
    val kgPorHa = (plantasHa * espigasPlanta * graosEspiga * pms) / 1000000.0
    val sacasPorHa = if (pesoSaca > 0) kgPorHa / pesoSaca else 0.0

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        AgroFormulaCard(
            formulaName = "Estimativa de Produtividade de Milho (Embrapa)",
            formulaMath = "Produção (kg/ha) = Plantas/ha × Espigas/planta × Grãos/espiga × PMS(g) ÷ 1.000.000\nSacas/ha = kg/ha ÷ 60",
            description = "Para calcular grãos por espiga: conte o número de fileiras de grãos e multiplique pelo número de grãos por fileira."
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AgroNumberField(
                value = plantasHaInput,
                onValueChange = { plantasHaInput = it },
                label = "Estande de Milho",
                suffix = "pl/ha",
                helperText = "Ex: 60.000 a 75.000",
                modifier = Modifier.weight(1f)
            )
            AgroNumberField(
                value = espigasPorPlantaInput,
                onValueChange = { espigasPorPlantaInput = it },
                label = "Espigas por Planta",
                suffix = "espigas",
                helperText = "Ex: 1.0 ou 1.1",
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AgroNumberField(
                value = graosPorEspigaInput,
                onValueChange = { graosPorEspigaInput = it },
                label = "Grãos por Espiga",
                suffix = "grãos",
                helperText = "Fileiras × grãos/fileira (ex: 500)",
                modifier = Modifier.weight(1f)
            )
            AgroNumberField(
                value = pmsInput,
                onValueChange = { pmsInput = it },
                label = "PMS (Mil Grãos)",
                suffix = "g",
                helperText = "Ex: 300 a 380 g",
                modifier = Modifier.weight(1f)
            )
        }

        AgroResultCard(
            title = "Produtividade Estimada do Milho",
            mainValue = decimalFormatter.format(sacasPorHa),
            mainUnit = "sacas / ha",
            secondaryMetrics = listOf(
                "Produtividade em Massa" to "${formatter.format(kgPorHa)} kg/ha",
                "Espigas por Hectare" to "${formatter.format(plantasHa * espigasPlanta)} espigas",
                "Total de Grãos por Espiga" to "${formatter.format(graosEspiga)} grãos",
                "Grãos por Metro Quadrado" to "${formatter.format((plantasHa * espigasPlanta * graosEspiga) / 10000.0)} grãos / m²"
            ),
            formulaExplanation = "${formatter.format(plantasHa)} × ${espigasPlanta} × ${graosEspiga} × ${pms}g ÷ 1.000.000 = ${formatter.format(kgPorHa)} kg/ha",
            onSaveResult = {
                viewModel.saveCalculo(
                    tipo = "PRODUTIVIDADE",
                    titulo = "Estimativa de Safra Milho",
                    inputs = "${formatter.format(plantasHa)} pl/ha | ${espigasPlanta} esp/pl | ${graosEspiga} gr/esp | PMS: ${pms}g",
                    resultado = "${decimalFormatter.format(sacasPorHa)} sc/ha (${formatter.format(kgPorHa)} kg/ha)"
                )
            }
        )
    }
}

@Composable
private fun AmostragemM2Tab(
    viewModel: AgroViewModel,
    formatter: DecimalFormat,
    decimalFormatter: DecimalFormat
) {
    var massaAmostraKgInput by remember { mutableStateOf("12.0") }
    var areaAmostradaM2Input by remember { mutableStateOf("20.0") }

    val massa = massaAmostraKgInput.toDoubleOrNull() ?: 12.0
    val area = areaAmostradaM2Input.toDoubleOrNull() ?: 20.0

    val kgPorHa = if (area > 0) (massa / area) * 10000.0 else 0.0
    val sacasPorHa = kgPorHa / 60.0

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        AgroFormulaCard(
            formulaName = "Estimativa por Amostragem Direta em m²",
            formulaMath = "Produtividade (kg/ha) = (Massa da amostra em kg ÷ Área amostrada em m²) × 10.000",
            description = "Colha manualmente as plantas de uma área conhecida (ex: 10 m² ou 20 m²), debulhe, pese e extrapole para o hectare."
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AgroNumberField(
                value = massaAmostraKgInput,
                onValueChange = { massaAmostraKgInput = it },
                label = "Massa Colhida (kg)",
                suffix = "kg",
                helperText = "Peso dos grãos colhidos",
                modifier = Modifier.weight(1f)
            )
            AgroNumberField(
                value = areaAmostradaM2Input,
                onValueChange = { areaAmostradaM2Input = it },
                label = "Área Amostrada (m²)",
                suffix = "m²",
                helperText = "Ex: 20 m²",
                modifier = Modifier.weight(1f)
            )
        }

        AgroResultCard(
            title = "Produtividade da Amostra",
            mainValue = decimalFormatter.format(sacasPorHa),
            mainUnit = "sacas / ha",
            secondaryMetrics = listOf(
                "Produtividade em Massa" to "${formatter.format(kgPorHa)} kg/ha",
                "Produtividade por Metro Quadrado" to "${decimalFormatter.format(if (area > 0) massa / area else 0.0)} kg/m²"
            ),
            formulaExplanation = "(${massa} kg ÷ ${area} m²) × 10.000 = ${formatter.format(kgPorHa)} kg/ha",
            onSaveResult = {
                viewModel.saveCalculo(
                    tipo = "PRODUTIVIDADE",
                    titulo = "Produtividade por Amostragem",
                    inputs = "${massa} kg colhidos em ${area} m²",
                    resultado = "${decimalFormatter.format(sacasPorHa)} sc/ha (${formatter.format(kgPorHa)} kg/ha)"
                )
            }
        )
    }
}

@Composable
private fun ColheitaDiretaTab(
    viewModel: AgroViewModel,
    formatter: DecimalFormat,
    decimalFormatter: DecimalFormat
) {
    var producaoTotalKgInput by remember { mutableStateOf("18000") }
    var areaColhidaHaInput by remember { mutableStateOf("4.0") }

    val producao = producaoTotalKgInput.toDoubleOrNull() ?: 18000.0
    val area = areaColhidaHaInput.toDoubleOrNull() ?: 4.0

    val kgPorHa = if (area > 0) producao / area else 0.0
    val sacasPorHa = kgPorHa / 60.0

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        AgroFormulaCard(
            formulaName = "Cálculo Direto por Produção e Área Colhida",
            formulaMath = "Produtividade (kg/ha) = Produção Colhida (kg) ÷ Área Colhida (ha)\nSacas/ha = kg/ha ÷ 60",
            description = "Cálculo real pós-colheita baseado no ticket de pesagem da balança."
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AgroNumberField(
                value = producaoTotalKgInput,
                onValueChange = { producaoTotalKgInput = it },
                label = "Produção Total Pesada",
                suffix = "kg",
                helperText = "Peso líquido do caminhão/moega",
                modifier = Modifier.weight(1f)
            )
            AgroNumberField(
                value = areaColhidaHaInput,
                onValueChange = { areaColhidaHaInput = it },
                label = "Área Efetiva Colhida",
                suffix = "ha",
                helperText = "Hectares colhidos",
                modifier = Modifier.weight(1f)
            )
        }

        AgroResultCard(
            title = "Resultado Real do Talhão",
            mainValue = decimalFormatter.format(sacasPorHa),
            mainUnit = "sacas / ha",
            secondaryMetrics = listOf(
                "Produtividade Média" to "${formatter.format(kgPorHa)} kg/ha",
                "Toneladas por Hectare" to "${decimalFormatter.format(kgPorHa / 1000.0)} t/ha",
                "Total em Sacas Fechadas" to "${formatter.format(producao / 60.0)} sacas de 60kg"
            ),
            formulaExplanation = "${formatter.format(producao)} kg ÷ ${area} ha = ${formatter.format(kgPorHa)} kg/ha",
            onSaveResult = {
                viewModel.saveCalculo(
                    tipo = "PRODUTIVIDADE",
                    titulo = "Colheita Real do Talhão",
                    inputs = "${formatter.format(producao)} kg em ${area} ha",
                    resultado = "${decimalFormatter.format(sacasPorHa)} sc/ha (${formatter.format(kgPorHa)} kg/ha)"
                )
            }
        )
    }
}
