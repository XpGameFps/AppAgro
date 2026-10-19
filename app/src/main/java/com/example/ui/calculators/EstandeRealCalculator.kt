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
fun EstandeRealView(
    viewModel: AgroViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Por Comprimento de Linha", "Por Área Amostrada (m²)")

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
                EstandePorLinhaTab(viewModel, formatter, decimalFormatter)
            }
        } else {
            item {
                EstandePorAreaTab(viewModel, formatter, decimalFormatter)
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun EstandePorLinhaTab(
    viewModel: AgroViewModel,
    formatter: DecimalFormat,
    decimalFormatter: DecimalFormat
) {
    var plantasContadasInput by remember { mutableStateOf("26") }
    var metrosAvaliadosInput by remember { mutableStateOf("10.0") }
    var espacamentoLinhasInput by remember { mutableStateOf("0.50") }
    var estandePlanejadoInput by remember { mutableStateOf("60000") }

    val plantasContadas = plantasContadasInput.toDoubleOrNull() ?: 26.0
    val metrosAvaliados = metrosAvaliadosInput.toDoubleOrNull() ?: 10.0
    val espLinhas = espacamentoLinhasInput.toDoubleOrNull() ?: 0.50
    val estandePlanejado = estandePlanejadoInput.toDoubleOrNull() ?: 60000.0

    val plantasPorMetro = if (metrosAvaliados > 0) plantasContadas / metrosAvaliados else 0.0
    val plantasPorHa = if (espLinhas > 0) (plantasPorMetro * 10000.0) / espLinhas else 0.0
    val eficienciaEstande = if (estandePlanejado > 0) (plantasPorHa / estandePlanejado) * 100.0 else 0.0
    val perdasOuGanho = plantasPorHa - estandePlanejado

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        AgroFormulaCard(
            formulaName = "Conferência de Estande por Linha (Embrapa)",
            formulaMath = "Plantas/ha = (Média plantas ÷ Metros da amostra) × 10.000 ÷ Espaçamento Linhas",
            description = "Faça a contagem após a emergência em pelo menos 5 a 10 pontos do talhão para obter uma média confiável."
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AgroNumberField(
                value = plantasContadasInput,
                onValueChange = { plantasContadasInput = it },
                label = "Plantas Contadas",
                suffix = "pl",
                helperText = "Média de plantas no ponto",
                modifier = Modifier.weight(1f)
            )
            AgroNumberField(
                value = metrosAvaliadosInput,
                onValueChange = { metrosAvaliadosInput = it },
                label = "Comprimento Medido",
                suffix = "m",
                helperText = "Ex: 5m, 10m de linha",
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AgroNumberField(
                value = espacamentoLinhasInput,
                onValueChange = { espacamentoLinhasInput = it },
                label = "Espaçamento Linhas",
                suffix = "m",
                helperText = "Ex: 0.50 m (50 cm)",
                modifier = Modifier.weight(1f)
            )
            AgroNumberField(
                value = estandePlanejadoInput,
                onValueChange = { estandePlanejadoInput = it },
                label = "Estande Planejado",
                suffix = "pl/ha",
                helperText = "Meta inicial pretendida",
                modifier = Modifier.weight(1f)
            )
        }

        AgroResultCard(
            title = "Estande Real Verificado",
            mainValue = formatter.format(plantasPorHa),
            mainUnit = "plantas / ha",
            secondaryMetrics = listOf(
                "Média de Plantas por Metro" to "${decimalFormatter.format(plantasPorMetro)} pl/m",
                "Eficiência do Estande (% da Meta)" to "${decimalFormatter.format(eficienciaEstande)}%",
                "Diferença vs Alvo Planejado" to "${if (perdasOuGanho >= 0) "+" else ""}${formatter.format(perdasOuGanho)} pl/ha",
                "Status da Emergência" to if (eficienciaEstande >= 90) "Excelente distribuição" else if (eficienciaEstande >= 75) "Atenção a falhas pontuais" else "Alto índice de falhas/mortalidade"
            ),
            formulaExplanation = "(${plantasContadas} ÷ ${metrosAvaliados}m) × 10.000 ÷ ${espLinhas}m = ${formatter.format(plantasPorHa)} pl/ha",
            onSaveResult = {
                viewModel.saveCalculo(
                    tipo = "ESTANDE_REAL",
                    titulo = "Estande Real por Linha",
                    inputs = "${plantasContadas} pl em ${metrosAvaliados}m | Linha: ${espLinhas}m",
                    resultado = "${formatter.format(plantasPorHa)} pl/ha (${decimalFormatter.format(eficienciaEstande)}% do alvo)"
                )
            }
        )
    }
}

@Composable
private fun EstandePorAreaTab(
    viewModel: AgroViewModel,
    formatter: DecimalFormat,
    decimalFormatter: DecimalFormat
) {
    var plantasAmostraInput by remember { mutableStateOf("150") }
    var areaAmostradaM2Input by remember { mutableStateOf("20.0") }
    var estandePlanejadoInput by remember { mutableStateOf("70000") }

    val plantasAmostra = plantasAmostraInput.toDoubleOrNull() ?: 150.0
    val areaAmostrada = areaAmostradaM2Input.toDoubleOrNull() ?: 20.0
    val estandeAlvo = estandePlanejadoInput.toDoubleOrNull() ?: 70000.0

    val plantasPorHa = if (areaAmostrada > 0) (plantasAmostra / areaAmostrada) * 10000.0 else 0.0
    val densidadeM2 = if (areaAmostrada > 0) plantasAmostra / areaAmostrada else 0.0
    val percentualMeta = if (estandeAlvo > 0) (plantasPorHa / estandeAlvo) * 100.0 else 0.0

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        AgroFormulaCard(
            formulaName = "Conferência por Área Amostrada (m²)",
            formulaMath = "Plantas/ha = (Plantas contadas ÷ Área amostrada em m²) × 10.000",
            description = "Útil para canteiros de hortaliças, pastagens e lavouras semeadas a lanço ou em linha estreita."
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AgroNumberField(
                value = plantasAmostraInput,
                onValueChange = { plantasAmostraInput = it },
                label = "Plantas na Amostra",
                suffix = "pl",
                helperText = "Total de plantas contadas",
                modifier = Modifier.weight(1f)
            )
            AgroNumberField(
                value = areaAmostradaM2Input,
                onValueChange = { areaAmostradaM2Input = it },
                label = "Área Amostrada",
                suffix = "m²",
                helperText = "Ex: 5 m², 20 m²",
                modifier = Modifier.weight(1f)
            )
        }

        AgroNumberField(
            value = estandePlanejadoInput,
            onValueChange = { estandePlanejadoInput = it },
            label = "Estande Alvo Pretendido",
            suffix = "pl/ha",
            modifier = Modifier.fillMaxWidth()
        )

        AgroResultCard(
            title = "Estande por Metro Quadrado",
            mainValue = formatter.format(plantasPorHa),
            mainUnit = "plantas / ha",
            secondaryMetrics = listOf(
                "Densidade por Metro Quadrado" to "${decimalFormatter.format(densidadeM2)} plantas / m²",
                "Cumprimento da Meta" to "${decimalFormatter.format(percentualMeta)}%",
                "Espaço Médio por Planta" to "${decimalFormatter.format(if (densidadeM2 > 0) 1.0 / densidadeM2 else 0.0)} m²"
            ),
            formulaExplanation = "(${plantasAmostra} pl ÷ ${areaAmostrada} m²) × 10.000 = ${formatter.format(plantasPorHa)} pl/ha",
            onSaveResult = {
                viewModel.saveCalculo(
                    tipo = "ESTANDE_REAL",
                    titulo = "Estande Real por Área Amostrada",
                    inputs = "${plantasAmostra} pl em ${areaAmostrada} m²",
                    resultado = "${formatter.format(plantasPorHa)} pl/ha (${decimalFormatter.format(densidadeM2)} pl/m²)"
                )
            }
        )
    }
}
