package com.example.ui.converter

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AgroFormulaCard
import com.example.ui.components.AgroNumberField
import com.example.ui.components.AgroResultCard
import com.example.ui.theme.AgroGreenContainerLight
import com.example.ui.theme.AgroGreenDark
import com.example.ui.theme.AgroGreenPrimary
import java.text.DecimalFormat

@Composable
fun QuickConverterScreen(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Área & Alqueires", "Produtividade (kg ⇄ sc)", "Chuva & Vazão", "Formulário Oficial")

    val formatter = remember { DecimalFormat("#,##0.00") }
    val intFormatter = remember { DecimalFormat("#,##0") }

    Column(modifier = modifier.fillMaxSize()) {
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            when (selectedTab) {
                0 -> AreaConverterContent(formatter, intFormatter)
                1 -> YieldConverterContent(formatter, intFormatter)
                2 -> RainConverterContent(formatter, intFormatter)
                3 -> CheatSheetContent()
            }
        }
    }
}

@Composable
private fun AreaConverterContent(
    formatter: DecimalFormat,
    intFormatter: DecimalFormat
) {
    var haInput by remember { mutableStateOf("1.0") }
    val ha = haInput.toDoubleOrNull() ?: 1.0

    val m2 = ha * 10000.0
    val km2 = ha / 100.0
    val alqueirePaulista = ha / 2.42 // 24.200 m²
    val alqueireMineiroGoiano = ha / 4.84 // 48.400 m²
    val alqueireNorte = ha / 2.72 // 27.200 m²
    val alqueireBaiano = ha / 9.68 // 96.800 m²
    val tarefas = m2 / 4356.0

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            AgroFormulaCard(
                formulaName = "Tabela de Medidas de Área do Agro Brasileiro",
                formulaMath = "1 ha = 10.000 m² | 1 km² = 100 ha\nAlqueire Paulista = 2,42 ha | Mineiro/Goiano = 4,84 ha",
                description = "Evite confusão jurídica e comercial convertendo com precisão as medidas regionais."
            )
        }

        item {
            AgroNumberField(
                value = haInput,
                onValueChange = { haInput = it },
                label = "Área em Hectares (ha)",
                suffix = "ha",
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "EQUIVALÊNCIAS EXATAS",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgroGreenPrimary
                    )

                    ConversionRow("Metros Quadrados (m²)", "${intFormatter.format(m2)} m²")
                    ConversionRow("Quilômetros Quadrados (km²)", "${formatter.format(km2)} km²")
                    ConversionRow("Alqueire Paulista (2,42 ha)", "${formatter.format(alqueirePaulista)} alq")
                    ConversionRow("Alqueire Mineiro / Goiano (4,84 ha)", "${formatter.format(alqueireMineiroGoiano)} alq")
                    ConversionRow("Alqueire do Norte (2,72 ha)", "${formatter.format(alqueireNorte)} alq")
                    ConversionRow("Alqueire Baiano (9,68 ha)", "${formatter.format(alqueireBaiano)} alq")
                    ConversionRow("Tarefas Nordestinas (~4.356 m²)", "${formatter.format(tarefas)} tarefas")
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun YieldConverterContent(
    formatter: DecimalFormat,
    intFormatter: DecimalFormat
) {
    var sacasInput by remember { mutableStateOf("60.0") }
    var kgHaInput by remember { mutableStateOf("3600.0") }
    var pesoSacaInput by remember { mutableStateOf("60.0") }

    val pesoSaca = pesoSacaInput.toDoubleOrNull() ?: 60.0

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            AgroFormulaCard(
                formulaName = "Conversão Direta de Produtividade (Embrapa)",
                formulaMath = "Sacas/ha = kg/ha ÷ Peso da Saca (60 kg)\nkg/ha = Sacas/ha × Peso da Saca (60 kg)",
                description = "No comércio brasileiro de grãos (soja, milho, arroz, feijão), a saca padrão corresponde a 60 kg."
            )
        }

        item {
            AgroNumberField(
                value = pesoSacaInput,
                onValueChange = { pesoSacaInput = it },
                label = "Peso Padrão da Saca",
                suffix = "kg",
                helperText = "Padrão brasileiro: 60 kg",
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("De Sacas/ha para kg/ha e t/ha", fontWeight = FontWeight.Bold, color = AgroGreenPrimary)
                    AgroNumberField(
                        value = sacasInput,
                        onValueChange = { sacasInput = it },
                        label = "Produtividade em Sacas",
                        suffix = "sc/ha"
                    )
                    val sVal = sacasInput.toDoubleOrNull() ?: 0.0
                    val kgVal = sVal * pesoSaca
                    val tVal = kgVal / 1000.0
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(AgroGreenContainerLight)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Equivale a:", fontWeight = FontWeight.SemiBold, color = AgroGreenDark)
                        Text("${intFormatter.format(kgVal)} kg/ha (${formatter.format(tVal)} t/ha)", fontWeight = FontWeight.Bold, color = AgroGreenDark)
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("De kg/ha para Sacas/ha", fontWeight = FontWeight.Bold, color = AgroGreenPrimary)
                    AgroNumberField(
                        value = kgHaInput,
                        onValueChange = { kgHaInput = it },
                        label = "Produtividade em Massa",
                        suffix = "kg/ha"
                    )
                    val kVal = kgHaInput.toDoubleOrNull() ?: 0.0
                    val scVal = if (pesoSaca > 0) kVal / pesoSaca else 0.0
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(AgroGreenContainerLight)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Equivale a:", fontWeight = FontWeight.SemiBold, color = AgroGreenDark)
                        Text("${formatter.format(scVal)} sacas/ha", fontWeight = FontWeight.Bold, color = AgroGreenDark)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun RainConverterContent(
    formatter: DecimalFormat,
    intFormatter: DecimalFormat
) {
    var chuvaMmInput by remember { mutableStateOf("25.0") }
    var areaHaInput by remember { mutableStateOf("10.0") }

    val chuva = chuvaMmInput.toDoubleOrNull() ?: 25.0
    val area = areaHaInput.toDoubleOrNull() ?: 10.0

    val litrosM2 = chuva
    val litrosHa = chuva * 10000.0
    val m3Ha = chuva * 10.0
    val m3Total = m3Ha * area
    val litrosTotal = litrosHa * area

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            AgroFormulaCard(
                formulaName = "Equivalência Chuva & Volume Hídrico",
                formulaMath = "1 mm de chuva = 1 Litro / m²\n1 mm em 1 ha = 10.000 Litros = 10 m³ de água",
                description = "Ajuda a dimensionar reservatórios de irrigação e avaliar a água disponível no solo após precipitação pluviométrica."
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AgroNumberField(
                    value = chuvaMmInput,
                    onValueChange = { chuvaMmInput = it },
                    label = "Chuva Registrada",
                    suffix = "mm",
                    helperText = "Leitura do pluviômetro",
                    modifier = Modifier.weight(1f)
                )
                AgroNumberField(
                    value = areaHaInput,
                    onValueChange = { areaHaInput = it },
                    label = "Área da Fazenda",
                    suffix = "ha",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("VOLUME DE CHUVA CALCULADO", fontWeight = FontWeight.Bold, color = AgroGreenPrimary)
                    ConversionRow("Volume por Metro Quadrado", "${formatter.format(litrosM2)} Litros / m²")
                    ConversionRow("Volume por Hectare", "${intFormatter.format(litrosHa)} L/ha (${intFormatter.format(m3Ha)} m³/ha)")
                    ConversionRow("Volume Total (${formatter.format(area)} ha)", "${intFormatter.format(m3Total)} m³ de água")
                    ConversionRow("Total em Litros", "${intFormatter.format(litrosTotal)} Litros")
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CheatSheetContent() {
    val formulas = listOf(
        Pair("População Linhas Simples", "Plantas/ha = 10.000 ÷ (Espaçamento Linhas × Espaçamento Plantas)"),
        Pair("População por Metro Linear", "Plantas/ha = Plantas/m × (10.000 ÷ Espaçamento Linhas)"),
        Pair("Comprimento de Linhas", "Metros de linha/ha = 10.000 ÷ Espaçamento Linhas (m)"),
        Pair("Canteiros / Fileiras Duplas", "Plantas/ha = (Nº Linhas × 10.000) ÷ (Dist. Centros × Dist. Plantas)"),
        Pair("Arranjo Quincôncio / Triangular", "Plantas/ha = 10.000 ÷ (0,866 × d²)"),
        Pair("Necessidade de Sementes", "Sementes/ha = População Alvo ÷ (Germinação dec. × Emergência dec.)"),
        Pair("Massa de Sementes (kg/ha)", "Massa/ha = Sementes/ha × PMS(g) ÷ 1.000.000"),
        Pair("Valor Cultural (VC%)", "VC (%) = (Pureza % × Germinação %) ÷ 100"),
        Pair("Conferência de Estande Real", "Plantas/ha = (Média plantas na linha ÷ Metros) × 10.000 ÷ Espaçamento"),
        Pair("Estimativa Milho (kg/ha)", "Produção = Plantas/ha × Espigas/pl × Grãos/espiga × PMS(g) ÷ 1.000.000"),
        Pair("Estimativa Soja (kg/ha)", "Produção = Plantas/ha × Vagens/pl × Grãos/vagem × PMS(g) ÷ 1.000.000"),
        Pair("Correção de Umidade Comercial", "Massa Corrigida = Massa Medida × (100 − Umid. Medida) ÷ (100 − Umid. Padrão)"),
        Pair("Perda na Colheita (Embrapa)", "Perda (kg/ha) = Massa coletada na armação 2m² (kg) ÷ 2 × 10.000"),
        Pair("Dose Fertilizante Comercial", "Produto (kg/ha) = Dose do Nutriente (kg/ha) ÷ (Teor % ÷ 100)"),
        Pair("Volume de Calda / Irrigação", "Volume (m³) = Lâmina (mm) × Área (ha) × 10"),
        Pair("Taxa de Calda Pulverizador", "Taxa (L/ha) = (60.000 × Vazão L/min) ÷ (Velocidade km/h × Bicos cm)")
    )

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text("Formulário Completo de Campo (Offline)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Consulte rapidamente a base matemática oficial utilizada no manual da Embrapa e MAPA.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        items(formulas) { (name, math) ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = AgroGreenPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(8.dp)
                    ) {
                        Text(text = math, style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ConversionRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}
