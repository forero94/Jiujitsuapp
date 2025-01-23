package com.sukata.bjj.ui.tecnicas

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.sukata.bjj.data.db.AppDatabase
import com.sukata.bjj.data.entities.Dificultad
import com.sukata.bjj.data.entities.FiltroSemanalEntity
import com.sukata.bjj.data.entities.TecnicaEntity
import kotlinx.coroutines.launch

class TecnicasViewModel(application: Application) : AndroidViewModel(application) {

    private val tecnicaDao = AppDatabase.getDatabase(application).tecnicaDao()

    // LiveData para los filtros
    private val _filtros = MutableLiveData<Filtros>()
    val filtros: LiveData<Filtros> get() = _filtros

    // LiveData para opciones de Spinners
    private val _tiposTecnica = MutableLiveData<List<String>>().apply { value = emptyList() }
    val tiposTecnica: LiveData<List<String>> get() = _tiposTecnica

    private val _subtiposPosicionInicial = MutableLiveData<List<String>>().apply { value = emptyList() }
    val subtiposPosicionInicial: LiveData<List<String>> get() = _subtiposPosicionInicial

    private val _dificultades = MutableLiveData<List<Dificultad>>().apply { value = emptyList() }
    val dificultades: LiveData<List<Dificultad>> get() = _dificultades

    private val _posicionesIniciales = MutableLiveData<List<String>>().apply { value = emptyList() }
    val posicionesIniciales: LiveData<List<String>> get() = _posicionesIniciales
    private val filtroSemanalDao = AppDatabase.getDatabase(application).filtroSemanalDao()

    suspend fun obtenerFiltroParaSemana(mes: String, semana: String): FiltroSemanalEntity? {
        Log.d("FiltroSemanalDao", "Intentando obtener filtro para mes=$mes, semana=$semana")
        val filtro = filtroSemanalDao.obtenerFiltroPorSemana(mes, semana)
        Log.d("FiltroSemanalDao", "Resultado del filtro: $filtro")
        return filtro
    }


    suspend fun guardarFiltroSemanal(filtro: FiltroSemanalEntity) {
        filtroSemanalDao.insertFiltro(filtro)
    }
    // Técnicas filtradas
    val tecnicasFiltradas: LiveData<List<TecnicaEntity>> = _filtros.switchMap { filtros ->
        Log.d("ViewModel", "Aplicando filtros en switchMap: $filtros")
        tecnicaDao.getTecnicasFiltradasConFiltros(
            posicionInicial = filtros.posicionInicial,
            subtipoPosicionInicial = filtros.subtipoPosicionInicial,
            tipoTecnica = filtros.tipoTecnica,
            dificultad = filtros.dificultad,
            textoBusqueda = filtros.textoBusqueda
        )
    }


    init {
        // Inicializa los filtros
        _filtros.value = Filtros()

        // Cargar opciones iniciales
        cargarOpcionesSpinners()
    }

    // Data class para agrupar filtros
    data class Filtros(
        val posicionInicial: String? = null,
        val subtipoPosicionInicial: String? = null,
        val tipoTecnica: String? = null,
        val dificultad: Dificultad? = null,
        val contexto: String? = null,
        val objetivoPrincipal: String? = null,
        val textoBusqueda: String? = null // Nuevo campo para búsqueda por texto
    )


    // Método para cargar opciones en los Spinners
    private fun cargarOpcionesSpinners() {
        try {
            Log.d("TecnicasViewModel", "Cargando opciones para Spinners...")
            _tiposTecnica.value = listOf("Pasaje", "Inversion", "Finalizacion", "Progresion", "Escape", "Derribos", "Barridos", "Controles", "Re-Guard", "Defensa de Sumisiones", "Contraataques", "Respuestas")

            _dificultades.value = Dificultad.values().toList()
            Log.d("TecnicasViewModel", "Dificultades cargadas: ${_dificultades.value}")

            _posicionesIniciales.value = listOf(
                "De pie (Standing)",
                "Guardia cerrada (Closed Guard)",
                "Guardia abierta (Open Guard)",
                "Half Guard (Media Guardia)",
                "Butterfly Guard (Guardia Mariposa)",
                "X-Guard",
                "Side Control (Control Lateral)",
                "North-South",
                "Montada (Mount)",
                "Back Control (Control de espalda)",
                "Reverse Half Guard (Media guardia invertida)",
                "Turtle (Tortuga)",
                "Quarter Guard"
            )
            Log.d("TecnicasViewModel", "Posiciones Iniciales: ${_posicionesIniciales.value}")
        } catch (e: Exception) {
            Log.e("TecnicasViewModel", "Error cargando opciones para Spinners", e)
        }
    }


    fun cargarSubtiposPosicion(posicionInicial: String) {
        _subtiposPosicionInicial.value = when (posicionInicial) {
            "De pie (Standing)" -> listOf(
                "Neutral (sin grips)",
                "Agarres de judo (gi)",
                "Agarres de wrestling (no-gi)",
                "Takedowns o clinch"
            )

            "Guardia cerrada (Closed Guard)" -> listOf(
                "Alta",
                "Baja",
                "Neutral"
            )

            "Guardia abierta (Open Guard)" -> listOf(
                "Spider Guard (Araña)",
                "Lasso Guard (Lazo)",
                "De la Riva",
                "Reverse De la Riva",
                "Collar and Sleeve",
                "Sit-up Guard (Guardia de sentado)"
            )

            "Half Guard (Media Guardia)" -> listOf(
                "Tradicional",
                "Deep Half",
                "Z-Guard",
                "Knee Shield",
                "50/50 Guard"
            )

            "Butterfly Guard (Guardia Mariposa)" -> listOf(
                "Butterfly Hook doble",
                "Single Butterfly Hook (Half Butterfly)"
            )

            "X-Guard" -> listOf(
                "X-Guard básica",
                "Single-Leg X (Ashi Garami)",

            )

            "Side Control (Control Lateral)" -> listOf(
                "Estándar (Cross Side)",
                "Kesa Gatame (Scarf Hold)",
                "Twister Side Control"
            )

            "Knee on Belly" -> listOf(
                "Knee on Belly clásico",
                "Knee on Belly switching (cambiar de lado)"
            )

            "North-South" -> listOf(
                "Control de cadera y torso",
                "North-South con aislación de brazo (Kimura)"
            )

            "Montada (Mount)" -> listOf(
                "Montada alta (High Mount)",
                "Montada baja (Low Mount)",
                "S-Mount"
            )

            "Back Control (Control de espalda)" -> listOf(
                "Con ganchos (Hooks)",
                "Body Triangle",
                "Seatbelt grip"
            )

            "Turtle (Tortuga)" -> listOf(
                "Tortuga compacta (defensiva)",
                "Tortuga en transición (scramble)",
                "Standing Turtle (cuando el rival está de pie)"
            )

            "Reverse Half Guard (Media guardia invertida)" -> listOf(
                "Versión estándar (pierna controlada invertida)"
            )

            "Quarter Guard" -> listOf(
                "Atrapar solo el pie del oponente"
            )
            else -> emptyList() // Si no hay subtipos
        }
    }
    suspend fun obtenerTodasTecnicas(): List<TecnicaEntity> {
        return tecnicaDao.fetchAllTecnicas()
    }
    suspend fun obtenerTecnicasFiltradas(filtro: FiltroSemanalEntity?): List<TecnicaEntity> {
        val todasLasTecnicas = obtenerTodasTecnicas()
        val posicion = filtro?.posicionInicial ?: "Todos"
        val tipo = filtro?.tipoTecnica ?: "Todos"
        return todasLasTecnicas.filter { tecnica ->
            (posicion == "Todos" || tecnica.posicionInicial == posicion) &&
                    (tipo == "Todos" || tecnica.tipoTecnica == tipo)
        }
    }
    suspend fun obtenerTecnicasCombinadas(filtroSemanal: FiltroSemanalEntity?, dificultad: String?): List<TecnicaEntity> {
        val todasLasTecnicas = obtenerTodasTecnicas()
        val posicion = filtroSemanal?.posicionInicial ?: "Todos"
        val tipo = filtroSemanal?.tipoTecnica ?: "Todos"

        return todasLasTecnicas.filter { tecnica ->
            (posicion == "Todos" || tecnica.posicionInicial == posicion) &&
                    (tipo == "Todos" || tecnica.tipoTecnica == tipo) &&
                    (dificultad == null || tecnica.dificultad?.name == dificultad)
        }
    }

    suspend fun obtenerTecnicasFiltradasPorDificultad(dificultad: String): List<TecnicaEntity> {
        return tecnicaDao.getTecnicasFiltradas(dificultad)
    }



    // Métodos para actualizar y limpiar filtros
    // En TecnicasViewModel.kt

    fun setFiltros(
        posicionInicial: String? = _filtros.value?.posicionInicial,
        subtipoPosicionInicial: String? = _filtros.value?.subtipoPosicionInicial,
        tipoTecnica: String? = _filtros.value?.tipoTecnica,
        dificultad: Dificultad? = _filtros.value?.dificultad,
        contexto: String? = _filtros.value?.contexto,
        objetivoPrincipal: String? = _filtros.value?.objetivoPrincipal,
        textoBusqueda: String? = _filtros.value?.textoBusqueda
    ) {
        _filtros.value = Filtros(
            posicionInicial = posicionInicial,
            subtipoPosicionInicial = subtipoPosicionInicial,
            tipoTecnica = tipoTecnica,
            dificultad = dificultad,
            contexto = contexto,
            objetivoPrincipal = objetivoPrincipal,
            textoBusqueda = textoBusqueda
        )
        Log.d("ViewModel", "Filtros aplicados: ${_filtros.value}")
    }


    fun setTextoBusqueda(texto: String?) {
        setFiltros(textoBusqueda = texto)
    }


    fun limpiarFiltros() {
        _filtros.value = Filtros()
    }

    fun insert(tecnica: TecnicaEntity) = viewModelScope.launch {
        tecnicaDao.insertTecnica(tecnica)
    }


}
