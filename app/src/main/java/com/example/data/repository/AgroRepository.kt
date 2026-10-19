package com.example.data.repository

import com.example.data.local.AgroDao
import com.example.data.local.CalculoSalvoEntity
import com.example.data.local.TalhaoEntity
import com.example.data.local.VistoriaEntity
import kotlinx.coroutines.flow.Flow

class AgroRepository(private val agroDao: AgroDao) {
    val allTalhoes: Flow<List<TalhaoEntity>> = agroDao.getAllTalhoes()
    val allVistorias: Flow<List<VistoriaEntity>> = agroDao.getAllVistorias()
    val allCalculosSalvos: Flow<List<CalculoSalvoEntity>> = agroDao.getAllCalculosSalvos()

    suspend fun getTalhaoById(id: Long): TalhaoEntity? = agroDao.getTalhaoById(id)

    fun getVistoriasByTalhao(talhaoId: Long): Flow<List<VistoriaEntity>> =
        agroDao.getVistoriasByTalhao(talhaoId)

    suspend fun insertTalhao(talhao: TalhaoEntity): Long = agroDao.insertTalhao(talhao)

    suspend fun updateTalhao(talhao: TalhaoEntity) = agroDao.updateTalhao(talhao)

    suspend fun deleteTalhao(id: Long) = agroDao.deleteTalhaoById(id)

    suspend fun insertVistoria(vistoria: VistoriaEntity): Long = agroDao.insertVistoria(vistoria)

    suspend fun deleteVistoria(id: Long) = agroDao.deleteVistoriaById(id)

    suspend fun insertCalculoSalvo(calculo: CalculoSalvoEntity): Long =
        agroDao.insertCalculoSalvo(calculo)

    suspend fun deleteCalculoSalvo(id: Long) = agroDao.deleteCalculoSalvoById(id)
}
