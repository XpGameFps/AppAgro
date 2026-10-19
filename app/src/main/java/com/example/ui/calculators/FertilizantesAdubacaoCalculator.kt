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
fun FertilizantesAdubacaoView(
    viewModel: AgroViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Dose por Teor Simples", "Formulado N-P-K", "Calagem (Saturação V%)")

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
            0 -> item { DosePorTeorTab(viewModel, formatter, intFormatter) }
            1 -> item { FormuladoNpkTab(viewModel, formatter, intFormatter) }
            2 -> item { CalagemTab(viewModel, formatter, intFormatter) }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DosePorTeorTab(
    viewModel: AgroViewModel,
    formatter: DecimalFormat,
    intFormatter: DecimalFormat
) {
    var doseNutrienteKgInput by remember { mutableStateOf("80") }
    var teorNutrienteInput by remember { mutableStateOf("60") }
    var areaHaInput by remember { mutableStateOf("15") }

    val doseNutriente = doseNutrienteKgInput.toDoubleOrNull() ?: 80.0
    val teor = teorNutrienteInput.toDoubleOrNull() ?: 60.0
    val area = areaHaInput.toDoubleOrNull() ?: 15.0

    val teorDecimal = teor / 100.0
    val produtoComercialKgHa = if (teorDecimal > 0) doseNutriente / teorDecimal else 0.0
    val totalProdutoKg = produtoComercialKgHa * area
    val sacas50kg = if (totalProdutoKg > 0) Math.ceil(totalProdutoKg / 50.0).toInt() else 0

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        AgroFormulaCard(
            formulaName = "Cálculo de Produto Comercial a partir do Nutriente",
            formulaMath = "Produto Comercial (kg/ha) = Dose do Nutriente (kg/ha) ÷ Teor do Produto (decimal)",
            description = "Exemplos comuns: Ureia (45% N), MAP (11% N, 52% P2O5), Superfosfato Simples (18% P2O5), Cloreto de Potássio (60% K2O)."
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AgroNumberField(
                value = doseNutrienteKgInput,
                onValueChange = { doseNutrienteKgInput = it },
                label = "Dose do Nutriente Desejado",
                suffix = "kg/ha",
                helperText = "Ex: 80 kg/ha de K2O ou N",
                modifier = Modifier.weight(1f)
            )
            AgroNumberField(
                value = teorNutrienteInput,
                onValueChange = { teorNutrienteInput = it },
                label = "Teor da Fonte de Adubo",
                suffix = "%",
                helperText = "Ex: 60% (KCl) ou 45% (Ureia)",
                modifier = Modifier.weight(1f)
            )
        }

        AgroNumberField(
            value = areaHaInput,
            onValueChange = { areaHaInput = it },
            label = "Área da Aplicação",
            suffix = "ha",
            modifier = Modifier.fillMaxWidth()
        )

        AgroResultCard(
            title = "Produto Comercial a Aplicar",
            mainValue = formatter.format(produtoComercialKgHa),
            mainUnit = "kg / ha de adubo",
            secondaryMetrics = listOf(
                "Total para a Gleba (${formatter.format(area)} ha)" to "${intFormatter.format(totalProdutoKg)} kg (${formatter.format(totalProdutoKg / 1000.0)} toneladas)",
                "Equivalente em Sacas de 50 kg" to "${intFormatter.format(sacas50kg)} sacos de 50kg",
                "Teor Efetivo Considerado" to "${formatter.format(teor)}% de concentração"
            ),
            formulaExplanation = "${doseNutriente} kg/ha ÷ ${teorDecimal} = ${formatter.format(produtoComercialKgHa)} kg/ha",
            onSaveResult = {
                viewModel.saveCalculo(
                    tipo = "ADUBACAO",
                    titulo = "Dose de Fertilizante Comercial",
                    inputs = "Nutriente: ${doseNutriente} kg/ha | Teor: ${teor}% | Área: ${area}ha",
                    resultado = "${formatter.format(produtoComercialKgHa)} kg/ha | Total: ${intFormatter.format(totalProdutoKg)} kg"
                )
            }
        )
    }
}

@Composable
private fun FormuladoNpkTab(
    viewModel: AgroViewModel,
    formatter: DecimalFormat,
    intFormatter: DecimalFormat
) {
    var doseFormuladoInput by remember { mutableStateOf("250") }
    var teorNInput by remember { mutableStateOf("8") }
    var teorPInput by remember { mutableStateOf("28") }
    var teorKInput by remember { mutableStateOf("16") }
    var areaHaInput by remember { mutableStateOf("20") }

    val doseFormulado = doseFormuladoInput.toDoubleOrNull() ?: 250.0
    val tN = (teorNInput.toDoubleOrNull() ?: 8.0) / 100.0
    val tP = (teorPInput.toDoubleOrNull() ?: 28.0) / 100.0
    val tK = (teorKInput.toDoubleOrNull() ?: 16.0) / 100.0
    val area = areaHaInput.toDoubleOrNull() ?: 20.0

    val nFornecido = doseFormulado * tN
    val pFornecido = doseFormulado * tP
    val kFornecido = doseFormulado * tK

    val totalAduboKg = doseFormulado * area

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        AgroFormulaCard(
            formulaName = "Nutrientes Fornecidos por Formulado N-P-K",
            formulaMath = "Nutriente (kg/ha) = Adubo Aplicado (kg/ha) × Teor no Adubo (decimal)",
            description = "Exemplo do manual: 250 kg/ha de 08-28-16 fornece 20 kg de N, 70 kg de P2O5 e 40 kg de K2O por hectare."
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AgroNumberField(
                value = doseFormuladoInput,
                onValueChange = { doseFormuladoInput = it },
                label = "Dose do Formulado",
                suffix = "kg/ha",
                helperText = "Ex: 250 ou 300 kg/ha",
                modifier = Modifier.weight(1f)
            )
            AgroNumberField(
                value = areaHaInput,
                onValueChange = { areaHaInput = it },
                label = "Área Total",
                suffix = "ha",
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AgroNumberField(
                value = teorNInput,
                onValueChange = { teorNInput = it },
                label = "% N",
                suffix = "%",
                modifier = Modifier.weight(1f)
            )
            AgroNumberField(
                value = teorPInput,
                onValueChange = { teorPInput = it },
                label = "% P₂O₅",
                suffix = "%",
                modifier = Modifier.weight(1f)
            )
            AgroNumberField(
                value = teorKInput,
                onValueChange = { teorKInput = it },
                label = "% K₂O",
                suffix = "%",
                modifier = Modifier.weight(1f)
            )
        }

        AgroResultCard(
            title = "Aporte Nutricional NPK",
            mainValue = "${formatter.format(pFornecido)} P₂O₅",
            mainUnit = "kg/ha",
            secondaryMetrics = listOf(
                "Nitrogênio (N) Fornecido" to "${formatter.format(nFornecido)} kg/ha de N",
                "Fósforo (P₂O₅) Fornecido" to "${formatter.format(pFornecido)} kg/ha de P₂O₅",
                "Potássio (K₂O) Fornecido" to "${formatter.format(kFornecido)} kg/ha de K₂O",
                "Total de Adubo para o Talhão" to "${intFormatter.format(totalAduboKg)} kg (${formatter.format(totalAduboKg / 1000.0)} t)",
                "Proporção N:P:K" to "${teorNInput}-${teorPInput}-${teorKInput}"
            ),
            formulaExplanation = "N: ${doseFormulado}×${tN} | P: ${doseFormulado}×${tP} | K: ${doseFormulado}×${tK}",
            onSaveResult = {
                viewModel.saveCalculo(
                    tipo = "ADUBACAO",
                    titulo = "Aporte NPK ${teorNInput}-${teorPInput}-${teorKInput}",
                    inputs = "${doseFormulado} kg/ha de ${teorNInput}-${teorPInput}-${teorKInput} em ${area} ha",
                    resultado = "N: ${formatter.format(nFornecido)} | P: ${formatter.format(pFornecido)} | K: ${formatter.format(kFornecido)} kg/ha"
                )
            }
        )
    }
}

@Composable
private fun CalagemTab(
    viewModel: AgroViewModel,
    formatter: DecimalFormat,
    intFormatter: DecimalFormat
) {
    var v1AtualInput by remember { mutableStateOf("35") }
    var v2DesejadoInput by remember { mutableStateOf("70") }
    var ctcSoloInput by remember { mutableStateOf("9.5") }
    var prntInput by remember { mutableStateOf("85") }
    var areaHaInput by remember { mutableStateOf("30") }

    val v1 = v1AtualInput.toDoubleOrNull() ?: 35.0
    val v2 = v2DesejadoInput.toDoubleOrNull() ?: 70.0
    val ctc = ctcSoloInput.toDoubleOrNull() ?: 9.5
    val prnt = prntInput.toDoubleOrNull() ?: 85.0
    val area = areaHaInput.toDoubleOrNull() ?: 30.0

    // NC (t/ha) = (V2 - V1) × CTC ÷ PRNT
    val ncToneladasHa = if (prnt > 0) ((v2 - v1) * ctc) / prnt else 0.0
    val totalCalcarioToneladas = ncToneladasHa * area

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        AgroFormulaCard(
            formulaName = "Necessidade de Calagem (Método Saturação por Bases)",
            formulaMath = "NC (t/ha) = [(V₂ − V₁) × CTC] ÷ PRNT",
            description = "V₁ = Saturação atual por bases (análise de solo). V₂ = Saturação recomendada da cultura (soja/milho ~70%, café ~60%, cana ~60%). PRNT = Poder Relativo de Neutralização Total."
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AgroNumberField(
                value = v1AtualInput,
                onValueChange = { v1AtualInput = it },
                label = "V₁ Atual do Laudo",
                suffix = "%",
                helperText = "Saturação inicial (ex: 35%)",
                modifier = Modifier.weight(1f)
            )
            AgroNumberField(
                value = v2DesejadoInput,
                onValueChange = { v2DesejadoInput = it },
                label = "V₂ Desejado da Cultura",
                suffix = "%",
                helperText = "Ex: 70% para soja/milho",
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AgroNumberField(
                value = ctcSoloInput,
                onValueChange = { ctcSoloInput = it },
                label = "CTC a pH 7,0 (T)",
                suffix = "cmolc/dm³",
                helperText = "Capacidade de Troca Catiônica",
                modifier = Modifier.weight(1f)
            )
            AgroNumberField(
                value = prntInput,
                onValueChange = { prntInput = it },
                label = "PRNT do Calcário",
                suffix = "%",
                helperText = "Rótulo do calcário (ex: 80-90%)",
                modifier = Modifier.weight(1f)
            )
        }

        AgroNumberField(
            value = areaHaInput,
            onValueChange = { areaHaInput = it },
            label = "Área Total a Calcar",
            suffix = "ha",
            modifier = Modifier.fillMaxWidth()
        )

        AgroResultCard(
            title = "Necessidade de Calcário",
            mainValue = formatter.format(ncToneladasHa),
            mainUnit = "t / ha de calcário",
            secondaryMetrics = listOf(
                "Total para a Gleba (${formatter.format(area)} ha)" to "${formatter.format(totalCalcarioToneladas)} toneladas",
                "Elevação da Saturação (ΔV)" to "+${formatter.format(v2 - v1)}% de saturação por bases",
                "Recomendação de Aplicação" to if (ncToneladasHa > 4.0) "Parcelar em duas aplicações (metade antes da aração/subsolagem, metade pós)" else "Aplicação em dose única uniforme e incorporação"
            ),
            formulaExplanation = "[(${v2} - ${v1}) × ${ctc}] ÷ ${prnt} = ${formatter.format(ncToneladasHa)} t/ha",
            onSaveResult = {
                viewModel.saveCalculo(
                    tipo = "ADUBACAO",
                    titulo = "Calagem V2 - V1",
                    inputs = "V1: ${v1}% -> V2: ${v2}% | CTC: ${ctc} | PRNT: ${prnt}%",
                    resultado = "${formatter.format(ncToneladasHa)} t/ha | Total: ${formatter.format(totalCalcarioToneladas)} t"
                )
            }
        )
    }
}
