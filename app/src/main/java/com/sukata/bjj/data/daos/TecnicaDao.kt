package com.sukata.bjj.data.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sukata.bjj.data.entities.Dificultad
import com.sukata.bjj.data.entities.TecnicaEntity

@Dao
interface TecnicaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg tecnicas: TecnicaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTecnica(tecnica: TecnicaEntity)

    @Update
    suspend fun updateTecnica(tecnica: TecnicaEntity)

    @Delete
    suspend fun deleteTecnica(tecnica: TecnicaEntity)

    @Query("SELECT * FROM Tecnicas")
    fun getAllTecnicas(): LiveData<List<TecnicaEntity>>

    @Query("SELECT * FROM tecnicas")
    suspend fun fetchAllTecnicas(): List<TecnicaEntity>

    @Query("SELECT COUNT(*) FROM Tecnicas")
    fun count(): Int

    @Query("SELECT * FROM Tecnicas WHERE dificultad = :dificultad")
    suspend fun getTecnicasFiltradas(dificultad: String): List<TecnicaEntity>

    // Método para filtrar técnicas por múltiples criterios
    @Query("""
    SELECT * FROM tecnicas
    WHERE (:posicionInicial IS NULL OR posicionInicial = :posicionInicial)
    AND (:subtipoPosicionInicial IS NULL OR subtipoPosicionInicial = :subtipoPosicionInicial)
    AND (:tipoTecnica IS NULL OR tipoTecnica = :tipoTecnica)
    AND (:dificultad IS NULL OR dificultad = :dificultad)
""")
    fun getTecnicasFiltradas(
        posicionInicial: String?,
        subtipoPosicionInicial: String?, // Nuevo campo
        tipoTecnica: String?,
        dificultad: Dificultad?
    ): LiveData<List<TecnicaEntity>>

    // Método para filtrar técnicas por múltiples criterios incluyendo búsqueda por texto

    @Query("""
        SELECT * FROM Tecnicas
        WHERE 
            (:posicionInicial IS NULL OR posicionInicial = :posicionInicial)
            AND (:subtipoPosicionInicial IS NULL OR subtipoPosicionInicial = :subtipoPosicionInicial)
            AND (:tipoTecnica IS NULL OR tipoTecnica = :tipoTecnica)
            AND (:dificultad IS NULL OR dificultad = :dificultad)
            AND (:textoBusqueda IS NULL OR 
                nombre LIKE '%' || :textoBusqueda || '%' OR 
                descripcion LIKE '%' || :textoBusqueda || '%' OR 
                contexto LIKE '%' || :textoBusqueda || '%' OR 
                objetivoPrincipal LIKE '%' || :textoBusqueda || '%' OR 
                recursosAdicionales LIKE '%' || :textoBusqueda || '%')
    """)
    fun getTecnicasFiltradasConFiltros(
        posicionInicial: String?,
        subtipoPosicionInicial: String?,
        tipoTecnica: String?,
        dificultad: Dificultad?,
        textoBusqueda: String?
    ): LiveData<List<TecnicaEntity>>

}
