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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.components.AgroFormulaCard
import com.example.ui.components.AgroNumberField
import com.example.ui.components.AgroResultCard
import com.example.ui.viewmodel.AgroViewModel
import java.text.DecimalFormat

@Composable
fun UmidadeQuebraView(
    viewModel: AgroViewModel,
    modifier: Modifier = Modifier
) {
    var massaMedidaInput by remember { mutableStateOf("1000") }
    var umidadeMedidaInput by remember { mutableStateOf("20.0") }
    var umidadePadraoInput by remember { mutableStateOf("14.0") }
    var impurezasPercentInput by remember { mutableStateOf("1.5") }

    val massaMedida = massaMedidaInput.toDoubleOrNull() ?: 1000.0
    val umidadeMedida = umidadeMedidaInput.toDoubleOrNull() ?: 20.0
    val umidadePadrao = umidadePadraoInput.toDoubleOrNull() ?: 14.0
    val impurezas = impurezasPercentInput.toDoubleOrNull() ?: 1.5

    val formatter = remember { DecimalFormat("#,##0.0") }
    val intFormatter = remember { DecimalFormat("#,##0") }

    // Fórmula: Massa Corrigida = Massa Medida × (100 - Umidade Medida) ÷ (100 - Umidade Padrão)
    val divisor = 100.0 - umidadePadrao
    val massaCorrigidaUmidade = if (divisor > 0) {
        massaMedida * (100.0 - umidadeMedida) / divisor
    } else 0.0

    // Desconto de impurezas sobre a massa corrigida
    val descontoImpurezasKg = (massaCorrigidaUmidade * (impurezas / 100.0))
    val massaLiquidaFinal = (massaCorrigidaUmidade - descontoImpurezasKg).coerceAtLeast(0.0)

    val quebraUmidadeKg = (massaMedida - massaCorrigidaUmidade).coerceAtLeast(0.0)
    val quebraTotalKg = (massaMedida - massaLiquidaFinal).coerceAtLeast(0.0)
    val percentualQuebraTotal = if (massaMedida > 0) (quebraTotalKg / massaMedida) * 100.0 else 0.0
    val sacasLiquidas = massaLiquidaFinal / 60.0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            AgroFormulaCard(
                formulaName = "Fórmula Geral de Correção de Umidade e Quebra",
                formulaMath = "Massa Corrigida = Massa Medida × (100 − Umidade Medida) ÷ (100 − Umidade Padrão)\nProdução Líquida = Massa Corrigida − Impurezas",
                description = "A umidade-padrão comercial é de 14% para soja e milho, 13% para feijão e trigo, e 12% para arroz."
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AgroNumberField(
                    value = massaMedidaInput,
                    onValueChange = { massaMedidaInput = it },
                    label = "Massa Medida Bruta",
                    suffix = "kg",
                    helperText = "Peso bruto na balança",
                    modifier = Modifier.weight(1f)
                )
                AgroNumberField(
                    value = umidadeMedidaInput,
                    onValueChange = { umidadeMedidaInput = it },
                    label = "Umidade Medida",
                    suffix = "%",
                    helperText = "Lida no determinador",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AgroNumberField(
                    value = umidadePadraoInput,
                    onValueChange = { umidadePadraoInput = it },
                    label = "Umidade Padrão",
                    suffix = "%",
                    helperText = "Contrato (ex: 14% soja/milho)",
                    modifier = Modifier.weight(1f)
                )
                AgroNumberField(
                    value = impurezasPercentInput,
                    onValueChange = { impurezasPercentInput = it },
                    label = "Impurezas / Matéria Estranha",
                    suffix = "%",
                    helperText = "Geralmente 1.0% a 2.0%",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            AgroResultCard(
                title = "Massa Comercial Líquida",
                mainValue = formatter.format(massaLiquidaFinal),
                mainUnit = "kg líquidos",
                secondaryMetrics = listOf(
                    "Sacas Comerciais (60kg)" to "${formatter.format(sacasLiquidas)} sacas",
                    "Massa Corrigida por Umidade" to "${formatter.format(massaCorrigidaUmidade)} kg",
                    "Desconto por Excesso de Água" to "-${formatter.format(quebraUmidadeKg)} kg",
                    "Desconto de Impurezas (${formatter.format(impurezas)}%)" to "-${formatter.format(descontoImpurezasKg)} kg",
                    "Quebra Total da Carga" to "-${formatter.format(quebraTotalKg)} kg (${formatter.format(percentualQuebraTotal)}% de quebra)"
                ),
                formulaExplanation = "${formatter.format(massaMedida)} × (100 - ${umidadeMedida}) ÷ (100 - ${umidadePadrao}) = ${formatter.format(massaCorrigidaUmidade)} kg",
                onSaveResult = {
                    viewModel.saveCalculo(
                        tipo = "UMIDADE",
                        titulo = "Correção de Umidade e Impureza",
                        inputs = "${formatter.format(massaMedida)} kg | Umidade: ${umidadeMedida}% -> ${umidadePadrao}% | Impurezas: ${impurezas}%",
                        resultado = "Líquido: ${formatter.format(massaLiquidaFinal)} kg (${formatter.format(sacasLiquidas)} sc) | Quebra: ${formatter.format(quebraTotalKg)} kg"
                    )
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
