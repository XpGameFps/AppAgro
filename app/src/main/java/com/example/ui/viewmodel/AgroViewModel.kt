package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AgroDatabase
import com.example.data.local.CalculoSalvoEntity
import com.example.data.local.TalhaoEntity
import com.example.data.local.VistoriaEntity
import com.example.data.repository.AgroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AgroViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AgroRepository

    val allTalhoes: StateFlow<List<TalhaoEntity>>
    val allVistorias: StateFlow<List<VistoriaEntity>>
    val allCalculosSalvos: StateFlow<List<CalculoSalvoEntity>>

    private val _selectedTalhao = MutableStateFlow<TalhaoEntity?>(null)
    val selectedTalhao: StateFlow<TalhaoEntity?> = _selectedTalhao.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    init {
        val database = AgroDatabase.getDatabase(application)
        repository = AgroRepository(database.agroDao())

        allTalhoes = repository.allTalhoes.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allVistorias = repository.allVistorias.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allCalculosSalvos = repository.allCalculosSalvos.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Seed initial data if empty so the user has immediate examples
        viewModelScope.launch {
            repository.allTalhoes.collect { list ->
                if (list.isEmpty()) {
                    seedInitialData()
                }
            }
        }
    }

    private suspend fun seedInitialData() {
        val talhaoId = repository.insertTalhao(
            TalhaoEntity(
                nome = "Talhão 04 - Sede",
                fazenda = "Fazenda Primavera",
                areaHa = 35.0,
                cultura = "Soja",
                cultivar = "TMG 7062 IPRO",
                dataPlantio = "15/10/2026",
                espacamentoLinhasM = 0.45,
                populacaoAlvoHa = 260000.0,
                notas = "Palhada uniforme de braquiária ruziziensis. Solo argiloso corrigido."
            )
        )

        repository.insertVistoria(
            VistoriaEntity(
                talhaoId = talhaoId,
                talhaoNome = "Talhão 04 - Sede",
                data = "18/10/2026",
                estadio = "R3 (Início de formação de vagens)",
                pragaOuAlvo = "Percevejo-marrom (Euschistus heros)",
                metodoAmostragem = "Pano de batida (1 m linear)",
                pontosAvaliados = 8,
                mediaPorPonto = 1.25,
                porcentagemDano = 4.0,
                inimigosNaturais = "Presença de joaninhas e percevejos predadores (Podisus)",
                statusNivelControle = "ATENCAO",
                decisaoTomada = "Abaixo do nível de controle (alvo 2,0/pano). Reavaliar em 4 dias.",
                climaSolo = "Temperatura 29°C, solo úmido com boa retenção.",
                notas = "Bordaduras do lado da reserva com maior incidência."
            )
        )

        repository.insertCalculoSalvo(
            CalculoSalvoEntity(
                tipoCalculo = "SEMEADURA",
                titulo = "Semeadura Soja - Talhão 04",
                resumoInputs = "Alvo: 260.000 pl/ha | Germ: 90% | Emerg: 85% | PMS: 165g",
                resumoResultado = "Sementes/ha: 339.869 | Massa/ha: 56,1 kg/ha | Total (35 ha): 1.963 kg",
                talhaoNome = "Talhão 04 - Sede"
            )
        )
    }

    fun selectTalhao(talhao: TalhaoEntity?) {
        _selectedTalhao.value = talhao
    }

    fun saveTalhao(talhao: TalhaoEntity) {
        viewModelScope.launch {
            if (talhao.id == 0L) {
                repository.insertTalhao(talhao)
                _userMessage.value = "Talhão '${talhao.nome}' cadastrado com sucesso!"
            } else {
                repository.updateTalhao(talhao)
                _userMessage.value = "Talhão '${talhao.nome}' atualizado!"
            }
        }
    }

    fun deleteTalhao(id: Long) {
        viewModelScope.launch {
            repository.deleteTalhao(id)
            if (_selectedTalhao.value?.id == id) {
                _selectedTalhao.value = null
            }
            _userMessage.value = "Talhão removido."
        }
    }

    fun saveVistoria(vistoria: VistoriaEntity) {
        viewModelScope.launch {
            repository.insertVistoria(vistoria)
            _userMessage.value = "Vistoria MIP registrada com sucesso!"
        }
    }

    fun deleteVistoria(id: Long) {
        viewModelScope.launch {
            repository.deleteVistoria(id)
            _userMessage.value = "Registro de vistoria excluído."
        }
    }

    fun saveCalculo(
        tipo: String,
        titulo: String,
        inputs: String,
        resultado: String,
        talhaoNome: String = ""
    ) {
        viewModelScope.launch {
            repository.insertCalculoSalvo(
                CalculoSalvoEntity(
                    tipoCalculo = tipo,
                    titulo = titulo,
                    resumoInputs = inputs,
                    resumoResultado = resultado,
                    talhaoNome = talhaoNome
                )
            )
            _userMessage.value = "Cálculo salvo no histórico offline!"
        }
    }

    fun deleteCalculo(id: Long) {
        viewModelScope.launch {
            repository.deleteCalculoSalvo(id)
            _userMessage.value = "Cálculo removido do histórico."
        }
    }

    fun clearMessage() {
        _userMessage.value = null
    }
}
