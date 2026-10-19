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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.AgroFormulaCard
import com.example.ui.components.AgroNumberField
import com.example.ui.components.AgroResultCard
import com.example.ui.theme.AgroGreenPrimary
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusGreenBg
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusRedBg
import com.example.ui.viewmodel.AgroViewModel
import java.text.DecimalFormat

@Composable
fun PerdaColheitaView(
    viewModel: AgroViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Pesagem da Armação (kg)", "Contagem de Grãos (Soja)", "Plataforma vs Mecanismos")

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
            0 -> item { PerdaPesagemArmacaoTab(viewModel, formatter, intFormatter) }
            1 -> item { PerdaContagemGraosTab(viewModel, formatter, intFormatter) }
            2 -> item { PerdaPlataformaInternaTab(viewModel, formatter, intFormatter) }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PerdaPesagemArmacaoTab(
    viewModel: AgroViewModel,
    formatter: DecimalFormat,
    intFormatter: DecimalFormat
) {
    var massaColetadaGramaInput by remember { mutableStateOf("18.0") }
    var areaArmacaoM2Input by remember { mutableStateOf("2.0") }
    var larguraPlataformaInput by remember { mutableStateOf("9.1") }

    val massaGramas = massaColetadaGramaInput.toDoubleOrNull() ?: 18.0
    val massaKg = massaGramas / 1000.0
    val areaM2 = areaArmacaoM2Input.toDoubleOrNull() ?: 2.0
    val larguraPlat = larguraPlataformaInput.toDoubleOrNull() ?: 9.1

    // Comprimento da armação para ter 2 m² = 2 ÷ Largura da plataforma
    val comprimentoArmacaoM = if (larguraPlat > 0) 2.0 / larguraPlat else 0.0

    // Perda (kg/ha) = M (kg) ÷ A (m²) × 10.000
    val perdaKgHa = if (areaM2 > 0) (massaKg / areaM2) * 10000.0 else 0.0
    val perdaSacasHa = perdaKgHa / 60.0

    val toleranciaEmbrapaKg = 60.0 // 1 saca/ha
    val dentroDaTolerancia = perdaKgHa <= toleranciaEmbrapaKg

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        AgroFormulaCard(
            formulaName = "Método da Armação de 2 m² (Embrapa)",
            formulaMath = "Comprimento da armação = 2 ÷ Largura da plataforma (m)\nPerda (kg/ha) = (Massa em kg ÷ 2 m²) × 10.000\nNível de Tolerância Embrapa = 60 kg/ha (1 sc/ha)",
            description = "Realize a medição após a colhedora passar em pelo menos 5 pontos do talhão para detectar descalibração de corte ou trilha."
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AgroNumberField(
                value = massaColetadaGramaInput,
                onValueChange = { massaColetadaGramaInput = it },
                label = "Massa Coletada",
                suffix = "g",
                helperText = "Peso dos grãos colhidos na armação",
                modifier = Modifier.weight(1f)
            )
            AgroNumberField(
                value = areaArmacaoM2Input,
                onValueChange = { areaArmacaoM2Input = it },
                label = "Área da Armação",
                suffix = "m²",
                helperText = "Padrão Embrapa: 2,0 m²",
                modifier = Modifier.weight(1f)
            )
        }

        AgroNumberField(
            value = larguraPlataformaInput,
            onValueChange = { larguraPlataformaInput = it },
            label = "Largura da Plataforma da Colhedora",
            suffix = "m",
            helperText = "Ex: 9.1 m (30 pés), 10.6 m (35 pés)",
            modifier = Modifier.fillMaxWidth()
        )

        // Banner de Status da Tolerância
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (dentroDaTolerancia) StatusGreenBg else StatusRedBg
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (dentroDaTolerancia) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (dentroDaTolerancia) StatusGreen else StatusRed,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (dentroDaTolerancia) "DENTRO DA TOLERÂNCIA OFICIAL" else "ATENÇÃO: PERDA ACIMA DA TOLERÂNCIA",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (dentroDaTolerancia) StatusGreen else StatusRed
                    )
                    Text(
                        text = if (dentroDaTolerancia)
                            "Perda abaixo do limite aceitável de 60 kg/ha (1 sc/ha)."
                        else
                            "Perda excessiva de ${formatter.format(perdaKgHa - toleranciaEmbrapaKg)} kg/ha além do tolerável! Ajuste rotação do rotor/cilindro, abertura de côncavo, vento das peneiras ou velocidade de avanço.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        AgroResultCard(
            title = "Perda Estimada na Colheita",
            mainValue = formatter.format(perdaKgHa),
            mainUnit = "kg / ha",
            secondaryMetrics = listOf(
                "Perda em Sacas" to "${formatter.format(perdaSacasHa)} sc/ha (60kg)",
                "Dimensão da Armação para ${larguraPlat}m" to "${larguraPlat} m de largura × ${formatter.format(comprimentoArmacaoM * 100)} cm de comprimento",
                "Tolerância Máxima Embrapa" to "60,0 kg/ha (1,0 sc/ha)",
                "Prejuízo Financeiro Estimado" to "Equivalente a ${formatter.format(perdaSacasHa * 100)} sacas a cada 100 ha colhidos"
            ),
            formulaExplanation = "(${massaGramas}g ÷ 1.000) ÷ ${areaM2}m² × 10.000 = ${formatter.format(perdaKgHa)} kg/ha",
            onSaveResult = {
                viewModel.saveCalculo(
                    tipo = "PERDAS",
                    titulo = "Perda de Colheita - Armação 2m²",
                    inputs = "${massaGramas}g em ${areaM2}m² | Plataforma: ${larguraPlat}m",
                    resultado = "${formatter.format(perdaKgHa)} kg/ha (${formatter.format(perdaSacasHa)} sc/ha) - ${if (dentroDaTolerancia) "Dentro da tolerância" else "Excesso de perda"}"
                )
            }
        )
    }
}

@Composable
private fun PerdaContagemGraosTab(
    viewModel: AgroViewModel,
    formatter: DecimalFormat,
    intFormatter: DecimalFormat
) {
    var graosContadosInput by remember { mutableStateOf("90") }
    var areaContagemM2Input by remember { mutableStateOf("2.0") }
    var pesoMilGraosInput by remember { mutableStateOf("165") }

    val graosContados = graosContadosInput.toDoubleOrNull() ?: 90.0
    val area = areaContagemM2Input.toDoubleOrNull() ?: 2.0
    val pms = pesoMilGraosInput.toDoubleOrNull() ?: 165.0

    // Regra prática da soja: em 2 m², cerca de 60 a 66 grãos equivalem a 1 saca/ha (60 kg)
    // Cálculo preciso: Graos/ha = (graos / area) * 10.000. Perda kg = Graos/ha * PMS / 1.000.000
    val graosPorHa = if (area > 0) (graosContados / area) * 10000.0 else 0.0
    val perdaKgHa = (graosPorHa * pms) / 1000000.0
    val perdaSacasHa = perdaKgHa / 60.0
    val regraPraticaSacas = if (area == 2.0) graosContados / 60.0 else (graosContados / area) / 30.0

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        AgroFormulaCard(
            formulaName = "Contagem Rápida de Grãos de Soja no Chão",
            formulaMath = "Em 2 m²: ~60 a 66 grãos no solo = 1 saca/ha perdida (60 kg)\nFórmula exata: Perda (kg/ha) = (Grãos ÷ Área m²) × 10.000 × PMS ÷ 1.000.000",
            description = "Método prático de campo sem necessidade de balança analítica. Conte os grãos caídos dentro da armação."
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AgroNumberField(
                value = graosContadosInput,
                onValueChange = { graosContadosInput = it },
                label = "Grãos de Soja Contados",
                suffix = "grãos",
                isDecimal = false,
                helperText = "Total de grãos no chão",
                modifier = Modifier.weight(1f)
            )
            AgroNumberField(
                value = areaContagemM2Input,
                onValueChange = { areaContagemM2Input = it },
                label = "Área Delimitada",
                suffix = "m²",
                helperText = "Ex: 2 m²",
                modifier = Modifier.weight(1f)
            )
        }

        AgroNumberField(
            value = pesoMilGraosInput,
            onValueChange = { pesoMilGraosInput = it },
            label = "PMS do Grão (Peso Mil Sementes)",
            suffix = "g",
            helperText = "Geralmente 160g a 180g",
            modifier = Modifier.fillMaxWidth()
        )

        AgroResultCard(
            title = "Perda por Contagem",
            mainValue = formatter.format(perdaSacasHa),
            mainUnit = "sacas / ha",
            secondaryMetrics = listOf(
                "Perda em Quilos" to "${formatter.format(perdaKgHa)} kg/ha",
                "Grãos por Metro Quadrado" to "${intFormatter.format(if (area > 0) graosContados / area else 0.0)} grãos / m²",
                "Pela Regra Prática de Bolso" to "~${formatter.format(regraPraticaSacas)} sc/ha (60 grãos em 2m² ≈ 1 sc)",
                "Status" to if (perdaKgHa <= 60.0) "Dentro da tolerância (≤ 1 sc/ha)" else "Perda elevada (> 1 sc/ha)"
            ),
            formulaExplanation = "${intFormatter.format(graosContados)} grãos em ${area}m² = ${formatter.format(perdaKgHa)} kg/ha",
            onSaveResult = {
                viewModel.saveCalculo(
                    tipo = "PERDAS",
                    titulo = "Perda de Soja por Contagem",
                    inputs = "${intFormatter.format(graosContados)} grãos em ${area}m²",
                    resultado = "${formatter.format(perdaSacasHa)} sc/ha (${formatter.format(perdaKgHa)} kg/ha)"
                )
            }
        )
    }
}

@Composable
private fun PerdaPlataformaInternaTab(
    viewModel: AgroViewModel,
    formatter: DecimalFormat,
    intFormatter: DecimalFormat
) {
    var perdaTotalInput by remember { mutableStateOf("90.0") }
    var perdaPlataformaInput by remember { mutableStateOf("40.0") }

    val perdaTotal = perdaTotalInput.toDoubleOrNull() ?: 90.0
    val perdaPlat = perdaPlataformaInput.toDoubleOrNull() ?: 40.0

    // Perda interna = Perda total - Perda plataforma
    val perdaInterna = (perdaTotal - perdaPlat).coerceAtLeast(0.0)

    val percentualPlataforma = if (perdaTotal > 0) (perdaPlat / perdaTotal) * 100.0 else 0.0
    val percentualInterna = if (perdaTotal > 0) (perdaInterna / perdaTotal) * 100.0 else 0.0

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        AgroFormulaCard(
            formulaName = "Diagnóstico: Plataforma vs Mecanismos Internos",
            formulaMath = "Perda nos Mecanismos Internos = Perda Total − Perda da Plataforma",
            description = "Para medir a plataforma: pare e recue a máquina, medindo a armação na área ceifada pela barra antes da entrada no alimentador."
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AgroNumberField(
                value = perdaTotalInput,
                onValueChange = { perdaTotalInput = it },
                label = "Perda Total (Atrás da Máquina)",
                suffix = "kg/ha",
                helperText = "Coletada após a passagem",
                modifier = Modifier.weight(1f)
            )
            AgroNumberField(
                value = perdaPlataformaInput,
                onValueChange = { perdaPlataformaInput = it },
                label = "Perda na Plataforma",
                suffix = "kg/ha",
                helperText = "Coletada na área ceifada",
                modifier = Modifier.weight(1f)
            )
        }

        AgroResultCard(
            title = "Perda dos Mecanismos Internos",
            mainValue = formatter.format(perdaInterna),
            mainUnit = "kg / ha",
            secondaryMetrics = listOf(
                "Perda da Plataforma (Corte/Molinete)" to "${formatter.format(perdaPlat)} kg/ha (${formatter.format(percentualPlataforma)}% do total)",
                "Perda Interna (Trilha/Separação/Limpeza)" to "${formatter.format(perdaInterna)} kg/ha (${formatter.format(percentualInterna)}% do total)",
                "Recomendação Plataforma" to if (perdaPlat > 35) "Ajustar altura de corte, navalhas, dedos retráteis ou velocidade do molinete" else "Corte regulado adequadamente",
                "Recomendação Mecanismos" to if (perdaInterna > 35) "Ajustar rotação do cilindro/rotor, côncavo ou vazão de ar das peneiras" else "Trilha e separação adequadas"
            ),
            formulaExplanation = "${perdaTotal} - ${perdaPlat} = ${formatter.format(perdaInterna)} kg/ha nos mecanismos internos",
            onSaveResult = {
                viewModel.saveCalculo(
                    tipo = "PERDAS",
                    titulo = "Diagnóstico Plataforma vs Trilha",
                    inputs = "Total: ${perdaTotal} kg/ha | Plat: ${perdaPlat} kg/ha",
                    resultado = "Interna: ${formatter.format(perdaInterna)} kg/ha (${formatter.format(percentualInterna)}%)"
                )
            }
        )
    }
}
