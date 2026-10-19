package com.example.ui.calculators

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
fun PopulacaoEspacamentoView(
    viewModel: AgroViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Linhas Simples", "Plantas / Metro", "Canteiros Duplos", "Quincôncio / Triangular")

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

        when (selectedTab) {
            0 -> {
                // Linhas Simples
                item {
                    LinhasSimplesContent(viewModel, formatter, decimalFormatter)
                }
            }
            1 -> {
                // Plantas por Metro Linear
                item {
                    PlantasPorMetroContent(viewModel, formatter, decimalFormatter)
                }
            }
            2 -> {
                // Canteiros Duplos
                item {
                    CanteirosDuplosContent(viewModel, formatter, decimalFormatter)
                }
            }
            3 -> {
                // Triangular / Quincôncio
                item {
                    QuinquoncioContent(viewModel, formatter, decimalFormatter)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun LinhasSimplesContent(
    viewModel: AgroViewModel,
    formatter: DecimalFormat,
    decimalFormatter: DecimalFormat
) {
    var espLinhas by remember { mutableStateOf("0.50") }
    var espPlantas by remember { mutableStateOf("0.33") }
    var areaHa by remember { mutableStateOf("1.0") }

    val eLinhas = espLinhas.toDoubleOrNull() ?: 0.50
    val ePlantas = espPlantas.toDoubleOrNull() ?: 0.33
    val area = areaHa.toDoubleOrNull() ?: 1.0

    val areaPorPlantaM2 = eLinhas * ePlantas
    val plantasPorHa = if (areaPorPlantaM2 > 0) 10000.0 / areaPorPlantaM2 else 0.0
    val plantasTotalArea = plantasPorHa * area
    val metrosLinhaPorHa = if (eLinhas > 0) 10000.0 / eLinhas else 0.0
    val metrosLinhaTotal = metrosLinhaPorHa * area
    val plantasPorMetro = if (ePlantas > 0) 1.0 / ePlantas else 0.0

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        AgroFormulaCard(
            formulaName = "Fórmula Oficial de Linhas Simples (Embrapa)",
            formulaMath = "Plantas/ha = 10.000 ÷ (Espaçamento Linhas × Espaçamento Plantas)",
            description = "Certifique-se de utilizar os valores em metros (ex: 50 cm = 0,50 m). O resultado indica a densidade teórica por hectare."
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AgroNumberField(
                value = espLinhas,
                onValueChange = { espLinhas = it },
                label = "Espaçamento Linhas",
                suffix = "m",
                helperText = "Ex: 0.50 m (50 cm)",
                modifier = Modifier.weight(1f),
                testTag = "input_esp_linhas"
            )
            AgroNumberField(
                value = espPlantas,
                onValueChange = { espPlantas = it },
                label = "Entre Plantas",
                suffix = "m",
                helperText = "Ex: 0.33 m (33 cm)",
                modifier = Modifier.weight(1f),
                testTag = "input_esp_plantas"
            )
        }

        AgroNumberField(
            value = areaHa,
            onValueChange = { areaHa = it },
            label = "Área Total da Gleba / Talhão",
            suffix = "ha",
            helperText = "Hectares da área a ser plantada",
            modifier = Modifier.fillMaxWidth(),
            testTag = "input_area_ha"
        )

        AgroResultCard(
            title = "População Estimada",
            mainValue = formatter.format(plantasPorHa),
            mainUnit = "plantas / ha",
            secondaryMetrics = listOf(
                "Total na Área (${decimalFormatter.format(area)} ha)" to "${formatter.format(plantasTotalArea)} plantas",
                "Plantas por Metro Linear" to "${decimalFormatter.format(plantasPorMetro)} pl/m",
                "Área por Planta" to "${decimalFormatter.format(areaPorPlantaM2)} m²",
                "Comprimento de Linhas por Hectare" to "${formatter.format(metrosLinhaPorHa)} m/ha",
                "Comprimento Total de Linhas" to "${formatter.format(metrosLinhaTotal)} metros"
            ),
            formulaExplanation = "10.000 / (${eLinhas} × ${ePlantas}) = ${formatter.format(plantasPorHa)} pl/ha",
            onSaveResult = {
                viewModel.saveCalculo(
                    tipo = "POPULACAO",
                    titulo = "População Linhas Simples",
                    inputs = "Linhas: ${eLinhas}m | Plantas: ${ePlantas}m | Área: ${area}ha",
                    resultado = "${formatter.format(plantasPorHa)} pl/ha | Total: ${formatter.format(plantasTotalArea)} pl"
                )
            }
        )
    }
}

@Composable
private fun PlantasPorMetroContent(
    viewModel: AgroViewModel,
    formatter: DecimalFormat,
    decimalFormatter: DecimalFormat
) {
    var plantasPorMetroInput by remember { mutableStateOf("12") }
    var espLinhasInput by remember { mutableStateOf("0.45") }
    var areaHaInput by remember { mutableStateOf("10.0") }

    val plMetro = plantasPorMetroInput.toDoubleOrNull() ?: 12.0
    val espLinhas = espLinhasInput.toDoubleOrNull() ?: 0.45
    val area = areaHaInput.toDoubleOrNull() ?: 10.0

    val metrosLinhaPorHa = if (espLinhas > 0) 10000.0 / espLinhas else 0.0
    val plantasPorHa = plMetro * metrosLinhaPorHa
    val totalPlantas = plantasPorHa * area
    val distanciaEntrePlantasCm = if (plMetro > 0) (100.0 / plMetro) else 0.0

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        AgroFormulaCard(
            formulaName = "Fórmula por Plantas por Metro Linear",
            formulaMath = "Metros de linha/ha = 10.000 ÷ Espaçamento Linhas\nPlantas/ha = Plantas/m × Metros de linha/ha",
            description = "Muito comum na recomendação de soja e milho para regulagem de sementes por metro de sulco."
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AgroNumberField(
                value = plantasPorMetroInput,
                onValueChange = { plantasPorMetroInput = it },
                label = "Plantas por Metro",
                suffix = "pl/m",
                helperText = "Ex: 12 plantas/metro",
                modifier = Modifier.weight(1f)
            )
            AgroNumberField(
                value = espLinhasInput,
                onValueChange = { espLinhasInput = it },
                label = "Entre Linhas",
                suffix = "m",
                helperText = "Ex: 0.45 m (45 cm)",
                modifier = Modifier.weight(1f)
            )
        }

        AgroNumberField(
            value = areaHaInput,
            onValueChange = { areaHaInput = it },
            label = "Área Total",
            suffix = "ha",
            helperText = "Tamanho do talhão em hectares",
            modifier = Modifier.fillMaxWidth()
        )

        AgroResultCard(
            title = "Densidade de Plantio",
            mainValue = formatter.format(plantasPorHa),
            mainUnit = "plantas / ha",
            secondaryMetrics = listOf(
                "Total para o Talhão (${decimalFormatter.format(area)} ha)" to "${formatter.format(totalPlantas)} plantas",
                "Distância Teórica entre Plantas" to "${decimalFormatter.format(distanciaEntrePlantasCm)} cm",
                "Metros Lineares por Hectare" to "${formatter.format(metrosLinhaPorHa)} m/ha"
            ),
            formulaExplanation = "${plMetro} pl/m × ${formatter.format(metrosLinhaPorHa)} m = ${formatter.format(plantasPorHa)} pl/ha",
            onSaveResult = {
                viewModel.saveCalculo(
                    tipo = "POPULACAO",
                    titulo = "População por Metro Linear",
                    inputs = "${plMetro} pl/m | Linhas: ${espLinhas}m | Área: ${area}ha",
                    resultado = "${formatter.format(plantasPorHa)} pl/ha | Total: ${formatter.format(totalPlantas)} pl"
                )
            }
        )
    }
}

@Composable
private fun CanteirosDuplosContent(
    viewModel: AgroViewModel,
    formatter: DecimalFormat,
    decimalFormatter: DecimalFormat
) {
    var linhasNoConjunto by remember { mutableStateOf("2") }
    var distCentrosCanteiro by remember { mutableStateOf("1.70") }
    var distEntrePlantas by remember { mutableStateOf("0.50") }
    var areaHa by remember { mutableStateOf("1.0") }

    val nLinhas = linhasNoConjunto.toDoubleOrNull() ?: 2.0
    val dCentros = distCentrosCanteiro.toDoubleOrNull() ?: 1.70
    val dPlantas = distEntrePlantas.toDoubleOrNull() ?: 0.50
    val area = areaHa.toDoubleOrNull() ?: 1.0

    val divisor = dCentros * dPlantas
    val plantasPorHa = if (divisor > 0) (nLinhas * 10000.0) / divisor else 0.0
    val totalPlantas = plantasPorHa * area

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        AgroFormulaCard(
            formulaName = "Fileiras Duplas e Canteiros",
            formulaMath = "Plantas/ha = (Nº Linhas × 10.000) ÷ (Dist. entre centros × Dist. entre plantas)",
            description = "Utilizado em hortaliças, cenoura, abacaxi, fumo, café em renque e fruticultura consorciada."
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AgroNumberField(
                value = linhasNoConjunto,
                onValueChange = { linhasNoConjunto = it },
                label = "Linhas no Canteiro",
                suffix = "linhas",
                isDecimal = false,
                helperText = "Geralmente 2 linhas",
                modifier = Modifier.weight(1f)
            )
            AgroNumberField(
                value = distCentrosCanteiro,
                onValueChange = { distCentrosCanteiro = it },
                label = "Dist. Centros Canteiro",
                suffix = "m",
                helperText = "Ex: 1.70 m centro a centro",
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AgroNumberField(
                value = distEntrePlantas,
                onValueChange = { distEntrePlantas = it },
                label = "Dist. entre Plantas",
                suffix = "m",
                helperText = "Ex: 0.50 m na linha",
                modifier = Modifier.weight(1f)
            )
            AgroNumberField(
                value = areaHa,
                onValueChange = { areaHa = it },
                label = "Área da Lavoura",
                suffix = "ha",
                modifier = Modifier.weight(1f)
            )
        }

        AgroResultCard(
            title = "População em Canteiros",
            mainValue = formatter.format(plantasPorHa),
            mainUnit = "plantas / ha",
            secondaryMetrics = listOf(
                "Total de Mudas / Plantas" to "${formatter.format(totalPlantas)} mudas",
                "Área Efetiva por Módulo" to "${decimalFormatter.format(divisor)} m²"
            ),
            formulaExplanation = "(${nLinhas} × 10.000) ÷ (${dCentros} × ${dPlantas}) = ${formatter.format(plantasPorHa)} pl/ha",
            onSaveResult = {
                viewModel.saveCalculo(
                    tipo = "POPULACAO",
                    titulo = "Canteiros / Fileiras Duplas",
                    inputs = "${nLinhas} linhas | Centros: ${dCentros}m | Plantas: ${dPlantas}m",
                    resultado = "${formatter.format(plantasPorHa)} pl/ha"
                )
            }
        )
    }
}

@Composable
private fun QuinquoncioContent(
    viewModel: AgroViewModel,
    formatter: DecimalFormat,
    decimalFormatter: DecimalFormat
) {
    var distPlantas by remember { mutableStateOf("5.0") }
    var areaHa by remember { mutableStateOf("1.0") }

    val d = distPlantas.toDoubleOrNull() ?: 5.0
    val area = areaHa.toDoubleOrNull() ?: 1.0

    val areaPorPlanta = 0.866 * d * d
    val plantasPorHa = if (areaPorPlanta > 0) 10000.0 / areaPorPlanta else 0.0
    val plantasTotal = plantasPorHa * area

    // Comparativo com arranjo quadrado tradicional (d x d)
    val plantasQuadrado = if (d > 0) 10000.0 / (d * d) else 0.0
    val ganhoDensidade = plantasPorHa - plantasQuadrado

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        AgroFormulaCard(
            formulaName = "Arranjo Triangular / Quincôncio",
            formulaMath = "Área por planta = 0,866 × d² (fator √3/2)\nPlantas/ha = 10.000 ÷ (0,866 × d²)",
            description = "Permite acomodar ~15,5% mais plantas que o espaçamento quadrado mantendo a exata mesma distância entre copas em pomares e silvicultura."
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AgroNumberField(
                value = distPlantas,
                onValueChange = { distPlantas = it },
                label = "Distância entre Plantas (d)",
                suffix = "m",
                helperText = "Ex: 5.0 metros",
                modifier = Modifier.weight(1f)
            )
            AgroNumberField(
                value = areaHa,
                onValueChange = { areaHa = it },
                label = "Área do Pomar",
                suffix = "ha",
                modifier = Modifier.weight(1f)
            )
        }

        AgroResultCard(
            title = "Densidade Triangular",
            mainValue = formatter.format(plantasPorHa),
            mainUnit = "plantas / ha",
            secondaryMetrics = listOf(
                "Total de Mudas na Área" to "${formatter.format(plantasTotal)} mudas",
                "Área Efetiva por Planta" to "${decimalFormatter.format(areaPorPlanta)} m²",
                "Comparativo com Quadrado ($d x $d m)" to "${formatter.format(plantasQuadrado)} pl/ha",
                "Ganho de Plantas no Quincôncio" to "+${formatter.format(ganhoDensidade)} pl/ha (+15,5%)"
            ),
            formulaExplanation = "10.000 ÷ (0,866 × ${d}²) = ${formatter.format(plantasPorHa)} pl/ha",
            onSaveResult = {
                viewModel.saveCalculo(
                    tipo = "POPULACAO",
                    titulo = "Plantio Quincôncio / Triangular",
                    inputs = "Distância d: ${d}m | Área: ${area}ha",
                    resultado = "${formatter.format(plantasPorHa)} pl/ha (+${formatter.format(ganhoDensidade)} pl vs quadrado)"
                )
            }
        )
    }
}
