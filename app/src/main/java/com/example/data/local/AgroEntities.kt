package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "talhoes")
data class TalhaoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val fazenda: String = "",
    val areaHa: Double,
    val cultura: String,
    val cultivar: String = "",
    val dataPlantio: String = "",
    val espacamentoLinhasM: Double = 0.50,
    val populacaoAlvoHa: Double = 0.0,
    val notas: String = "",
    val criadoEm: Long = System.currentTimeMillis()
)

@Entity(tableName = "vistorias")
data class VistoriaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val talhaoId: Long,
    val talhaoNome: String,
    val data: String,
    val estadio: String,
    val pragaOuAlvo: String,
    val metodoAmostragem: String,
    val pontosAvaliados: Int,
    val mediaPorPonto: Double,
    val porcentagemDano: Double,
    val inimigosNaturais: String = "",
    val statusNivelControle: String, // "EQUILIBRIO", "ATENCAO", "ACAO_NECESSARIA"
    val decisaoTomada: String = "",
    val climaSolo: String = "",
    val notas: String = "",
    val criadoEm: Long = System.currentTimeMillis()
)

@Entity(tableName = "calculos_salvos")
data class CalculoSalvoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tipoCalculo: String,
    val titulo: String,
    val resumoInputs: String,
    val resumoResultado: String,
    val talhaoNome: String = "",
    val criadoEm: Long = System.currentTimeMillis()
)
