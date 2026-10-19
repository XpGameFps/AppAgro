package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AgroDao {
    // Talhões
    @Query("SELECT * FROM talhoes ORDER BY id DESC")
    fun getAllTalhoes(): Flow<List<TalhaoEntity>>

    @Query("SELECT * FROM talhoes WHERE id = :id")
    suspend fun getTalhaoById(id: Long): TalhaoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTalhao(talhao: TalhaoEntity): Long

    @Update
    suspend fun updateTalhao(talhao: TalhaoEntity)

    @Query("DELETE FROM talhoes WHERE id = :id")
    suspend fun deleteTalhaoById(id: Long)

    // Vistorias
    @Query("SELECT * FROM vistorias ORDER BY id DESC")
    fun getAllVistorias(): Flow<List<VistoriaEntity>>

    @Query("SELECT * FROM vistorias WHERE talhaoId = :talhaoId ORDER BY id DESC")
    fun getVistoriasByTalhao(talhaoId: Long): Flow<List<VistoriaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVistoria(vistoria: VistoriaEntity): Long

    @Query("DELETE FROM vistorias WHERE id = :id")
    suspend fun deleteVistoriaById(id: Long)

    // Cálculos Salvos
    @Query("SELECT * FROM calculos_salvos ORDER BY criadoEm DESC")
    fun getAllCalculosSalvos(): Flow<List<CalculoSalvoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalculoSalvo(calculo: CalculoSalvoEntity): Long

    @Query("DELETE FROM calculos_salvos WHERE id = :id")
    suspend fun deleteCalculoSalvoById(id: Long)
}
