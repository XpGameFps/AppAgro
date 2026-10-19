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
fun IrrigacaoPulverizacaoView(
    viewModel: AgroViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Lâmina & Volume de Água", "Eficiência da Irrigação", "Calibração de Pulverizador")

    val formatter = remember { DecimalFormat("#,##0.0") }
    val intFormatter = remember { DecimalFormat("#,##0") }

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
            0 -> item { VolumeLaminaTab(viewModel, formatter, intFormatter) }
            1 -> item { EficienciaIrrigacaoTab(viewModel, formatter, intFormatter) }
            2 -> item { CalibracaoPulverizadorTab(viewModel, formatter, intFormatter) }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun VolumeLaminaTab(
    viewModel: AgroViewModel,
    formatter: DecimalFormat,
    intFormatter: DecimalFormat
) {
    var laminaMmInput by remember { mutableStateOf("12.0") }
    var areaHaInput by remember { mutableStateOf("5.0") }

    val laminaMm = laminaMmInput.toDoubleOrNull() ?: 12.0
    val areaHa = areaHaInput.toDoubleOrNull() ?: 5.0

    // Volume (m³) = lâmina (mm) × área (ha) × 10
    val volumeM3 = laminaMm * areaHa * 10.0
    val volumeLitros = volumeM3 * 1000.0

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        AgroFormulaCard(
            formulaName = "Volume de Água e Equivalência de Chuva",
            formulaMath = "Volume (m³) = Lâmina (mm) × Área (ha) × 10\n1 mm de chuva em 1 ha = 10.000 Litros = 10 m³",
            description = "1 mm sobre 1 metro quadrado equivale a exatamente 1 litro de água. Em 1 hectare (10.000 m²), são 10.000 litros."
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AgroNumberField(
                value = laminaMmInput,
                onValueChange = { laminaMmInput = it },
                label = "Lâmina de Água / Chuva",
                suffix = "mm",
                helperText = "Ex: 12 mm aplicados ou chovidos",
                modifier = Modifier.weight(1f)
            )
            AgroNumberField(
                value = areaHaInput,
                onValueChange = { areaHaInput = it },
                label = "Área da Gleba",
                suffix = "ha",
                helperText = "Ex: 5 hectares",
                modifier = Modifier.weight(1f)
            )
        }

        AgroResultCard(
            title = "Volume Total de Água",
            mainValue = intFormatter.format(volumeM3),
            mainUnit = "m³ de água",
            secondaryMetrics = listOf(
                "Volume em Litros" to "${intFormatter.format(volumeLitros)} Litros",
                "Volume por Hectare" to "${intFormatter.format(laminaMm * 10.0)} m³/ha (${intFormatter.format(laminaMm * 10000.0)} L/ha)",
                "Equivalência de Chuva" to "Mesmo aporte de uma precipitação uniforme de ${formatter.format(laminaMm)} mm"
            ),
            formulaExplanation = "${laminaMm} mm × ${areaHa} ha × 10 = ${intFormatter.format(volumeM3)} m³",
            onSaveResult = {
                viewModel.saveCalculo(
                    tipo = "IRRIGACAO",
                    titulo = "Volume de Água e Lâmina",
                    inputs = "Lâmina: ${laminaMm} mm | Área: ${areaHa} ha",
                    resultado = "${intFormatter.format(volumeM3)} m³ (${intFormatter.format(volumeLitros)} L)"
                )
            }
        )
    }
}

@Composable
private fun EficienciaIrrigacaoTab(
    viewModel: AgroViewModel,
    formatter: DecimalFormat,
    intFormatter: DecimalFormat
) {
    var laminaLiquidaInput by remember { mutableStateOf("20.0") }
    var eficienciaPercentInput by remember { mutableStateOf("75") }
    var areaHaInput by remember { mutableStateOf("25") }

    val laminaLiquida = laminaLiquidaInput.toDoubleOrNull() ?: 20.0
    val eficiencia = eficienciaPercentInput.toDoubleOrNull() ?: 75.0
    val area = areaHaInput.toDoubleOrNull() ?: 25.0

    val efDecimal = eficiencia / 100.0
    val laminaBrutaMm = if (efDecimal > 0) laminaLiquida / efDecimal else 0.0
    val volumeBrutoM3 = laminaBrutaMm * area * 10.0
    val perdaEvaporacaoM3 = (laminaBrutaMm - laminaLiquida) * area * 10.0

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        AgroFormulaCard(
            formulaName = "Lâmina Bruta com Eficiência do Sistema",
            formulaMath = "Lâmina Bruta (mm) = Lâmina Necessária (mm) ÷ Eficiência (decimal)",
            description = "Compensa perdas por evaporação, vento e desuniformidade. Eficiência média: Pivô Central (80-88%), Aspersão convencional (70-78%), Gotejamento (90-95%)."
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AgroNumberField(
                value = laminaLiquidaInput,
                onValueChange = { laminaLiquidaInput = it },
                label = "Lâmina Líquida Requerida",
                suffix = "mm",
                helperText = "Demanda hídrica da cultura",
                modifier = Modifier.weight(1f)
            )
            AgroNumberField(
                value = eficienciaPercentInput,
                onValueChange = { eficienciaPercentInput = it },
                label = "Eficiência do Sistema",
                suffix = "%",
                helperText = "Pivô ~85%, Aspersão ~75%",
                modifier = Modifier.weight(1f)
            )
        }

        AgroNumberField(
            value = areaHaInput,
            onValueChange = { areaHaInput = it },
            label = "Área Irrigada",
            suffix = "ha",
            modifier = Modifier.fillMaxWidth()
        )

        AgroResultCard(
            title = "Lâmina Bruta a Aplicar",
            mainValue = formatter.format(laminaBrutaMm),
            mainUnit = "mm brutos",
            secondaryMetrics = listOf(
                "Volume Bruto Total Bombeado" to "${intFormatter.format(volumeBrutoM3)} m³",
                "Volume Efetivamente Aproveitado" to "${intFormatter.format(laminaLiquida * area * 10.0)} m³",
                "Perda Estimada por Deriva/Evaporação" to "${intFormatter.format(perdaEvaporacaoM3)} m³ (${formatter.format(100 - eficiencia)}%)"
            ),
            formulaExplanation = "${laminaLiquida} mm ÷ ${efDecimal} = ${formatter.format(laminaBrutaMm)} mm brutos",
            onSaveResult = {
                viewModel.saveCalculo(
                    tipo = "IRRIGACAO",
                    titulo = "Eficiência de Irrigação",
                    inputs = "Líquida: ${laminaLiquida} mm | Eficiência: ${eficiencia}% | Área: ${area} ha",
                    resultado = "Bruta: ${formatter.format(laminaBrutaMm)} mm | Volume: ${intFormatter.format(volumeBrutoM3)} m³"
                )
            }
        )
    }
}

@Composable
private fun CalibracaoPulverizadorTab(
    viewModel: AgroViewModel,
    formatter: DecimalFormat,
    intFormatter: DecimalFormat
) {
    var vazaoPontaLMinInput by remember { mutableStateOf("0.85") }
    var velocidadeKmHInput by remember { mutableStateOf("16.0") }
    var espacamentoBicosCmInput by remember { mutableStateOf("50") }
    var volumeTanqueLitrosInput by remember { mutableStateOf("2000") }

    val vazaoPonta = vazaoPontaLMinInput.toDoubleOrNull() ?: 0.85
    val velocidade = velocidadeKmHInput.toDoubleOrNull() ?: 16.0
    val espacamentoCm = espacamentoBicosCmInput.toDoubleOrNull() ?: 50.0
    val capacidadeTanque = volumeTanqueLitrosInput.toDoubleOrNull() ?: 2000.0

    // Taxa de aplicação (L/ha) = (60.000 × Q) ÷ (V × W)
    val divisor = velocidade * espacamentoCm
    val taxaAplicacaoLHa = if (divisor > 0) (60000.0 * vazaoPonta) / divisor else 0.0
    val hectaresPorTanque = if (taxaAplicacaoLHa > 0) capacidadeTanque / taxaAplicacaoLHa else 0.0

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        AgroFormulaCard(
            formulaName = "Calibração e Taxa de Aplicação do Pulverizador",
            formulaMath = "Taxa de Calda (L/ha) = (60.000 × Q) ÷ (V × W)",
            description = "Q = Vazão média por ponta em L/min (coleta com jarra graduada). V = Velocidade do trator em km/h. W = Espaçamento entre bicos na barra em centímetros (ex: 50 cm)."
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AgroNumberField(
                value = vazaoPontaLMinInput,
                onValueChange = { vazaoPontaLMinInput = it },
                label = "Vazão por Ponta (Q)",
                suffix = "L/min",
                helperText = "Medida com copo aferidor",
                modifier = Modifier.weight(1f)
            )
            AgroNumberField(
                value = velocidadeKmHInput,
                onValueChange = { velocidadeKmHInput = it },
                label = "Velocidade Real (V)",
                suffix = "km/h",
                helperText = "Velocidade na passada",
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AgroNumberField(
                value = espacamentoBicosCmInput,
                onValueChange = { espacamentoBicosCmInput = it },
                label = "Espaçamento Bicos (W)",
                suffix = "cm",
                helperText = "Geralmente 50 cm na barra",
                modifier = Modifier.weight(1f)
            )
            AgroNumberField(
                value = volumeTanqueLitrosInput,
                onValueChange = { volumeTanqueLitrosInput = it },
                label = "Capacidade do Tanque",
                suffix = "L",
                helperText = "Ex: 2.000 ou 3.000 L",
                modifier = Modifier.weight(1f)
            )
        }

        AgroResultCard(
            title = "Taxa de Calda da Pulverização",
            mainValue = formatter.format(taxaAplicacaoLHa),
            mainUnit = "L / ha",
            secondaryMetrics = listOf(
                "Autonomia por Tanque (${intFormatter.format(capacidadeTanque)} L)" to "${formatter.format(hectaresPorTanque)} hectares por abastecimento",
                "Vazão de Calda por Minuto" to "${formatter.format(vazaoPonta)} L/min por ponta",
                "Consumo de Calda para 100 ha" to "${intFormatter.format(taxaAplicacaoLHa * 100.0)} Litros de calda"
            ),
            formulaExplanation = "(60.000 × ${vazaoPonta}) ÷ (${velocidade} × ${espacamentoCm}) = ${formatter.format(taxaAplicacaoLHa)} L/ha",
            onSaveResult = {
                viewModel.saveCalculo(
                    tipo = "IRRIGACAO",
                    titulo = "Calibração de Pulverizador",
                    inputs = "Q: ${vazaoPonta} L/min | V: ${velocidade} km/h | W: ${espacamentoCm} cm",
                    resultado = "${formatter.format(taxaAplicacaoLHa)} L/ha | ${formatter.format(hectaresPorTanque)} ha/tanque"
                )
            }
        )
    }
}
