package cl.duoc.agronutri.domain

import cl.duoc.agronutri.data.AnalisisEntity

data class LineaPrograma(
    val nutriente: String,
    val fertilizante: String,
    val nivel: String,
    val observacion: String,
    val kgNutrienteHa: Double,
    val kgProductoTotal: Double,
    val kgProductoPorAplicacion: Double
)

data class ProgramaFertirriego(
    val lineas: List<LineaPrograma>,
    val aplicaciones: Int,
    val frecuencia: String,
    val alertas: List<String>,
    val conAnalisis: Boolean
) {
    fun detalleComoTexto(): String {
        var texto = ""
        for (linea in lineas) {
            if (texto.isNotEmpty()) {
                texto += "\n"
            }
            texto += "${linea.nutriente} · ${linea.fertilizante}: " +
                "${"%.1f".format(linea.kgProductoTotal)} kg/ha en total " +
                "(${"%.1f".format(linea.kgProductoPorAplicacion)} kg/ha por aplicación)"
        }
        return texto
    }

    fun comoTexto(cultivo: String, etapa: String, predio: String, cuartel: String): String {
        var texto = "AgroNutri · Programa de fertirriego\n"
        texto += "Cultivo: $cultivo ($etapa)\n"
        texto += "Predio: $predio · $cuartel\n"
        texto += "Frecuencia: $frecuencia\n\n"
        texto += detalleComoTexto()
        if (alertas.isNotEmpty()) {
            texto += "\n\nAlertas:"
            for (alerta in alertas) {
                texto += "\n- $alerta"
            }
        }
        texto += "\n\nValores referenciales (versión beta), pendientes de validación del asesor."
        return texto
    }
}

object CalculadoraFertirriego {

    fun calcular(
        cultivo: String,
        etapa: String,
        suelo: AnalisisEntity?,
        agua: AnalisisEntity?
    ): ProgramaFertirriego {
        val requerimiento = Catalogo.requerimiento(cultivo, etapa)
        val semanas = Catalogo.semanas(etapa)

        val lineas = mutableListOf<LineaPrograma>()
        lineas.add(crearLinea("N", requerimiento.n, suelo?.n, 20.0, 50.0, semanas))
        lineas.add(crearLinea("P", requerimiento.p, suelo?.p, 10.0, 30.0, semanas))
        lineas.add(crearLinea("K", requerimiento.k, suelo?.k, 100.0, 250.0, semanas))
        lineas.add(crearLinea("Ca", requerimiento.ca, suelo?.ca, 800.0, 2000.0, semanas))
        lineas.add(crearLinea("Mg", requerimiento.mg, suelo?.mg, 100.0, 400.0, semanas))

        val alertas = mutableListOf<String>()
        val phSuelo = suelo?.ph
        if (phSuelo != null) {
            if (phSuelo < 5.5) {
                alertas.add("pH bajo del suelo ($phSuelo): revisar acidez antes de fertilizar.")
            }
            if (phSuelo > 7.5) {
                alertas.add("pH alto del suelo ($phSuelo): puede bloquear la absorción de nutrientes.")
            }
        }
        val ceSuelo = suelo?.ce
        if (ceSuelo != null && ceSuelo > 2.0) {
            alertas.add("Conductividad eléctrica alta en el suelo ($ceSuelo dS/m): riesgo de salinidad.")
        }
        val ceAgua = agua?.ce
        if (ceAgua != null && ceAgua > 2.0) {
            alertas.add("Conductividad eléctrica alta en el agua de riego ($ceAgua dS/m): riesgo de salinidad.")
        }

        return ProgramaFertirriego(
            lineas = lineas,
            aplicaciones = semanas,
            frecuencia = "1 aplicación por semana durante $semanas semanas",
            alertas = alertas,
            conAnalisis = suelo != null
        )
    }

    private fun crearLinea(
        nutriente: String,
        dosisBase: Double,
        valorSuelo: Double?,
        bajo: Double,
        alto: Double,
        semanas: Int
    ): LineaPrograma {
        var factor = 1.0
        var nivel = "ADECUADO"
        var observacion = "Nivel adecuado"

        if (valorSuelo == null) {
            nivel = "SIN_DATO"
            observacion = "Sin dato en el análisis de suelo: dosis base"
        } else if (valorSuelo < bajo) {
            factor = 1.2
            nivel = "BAJO"
            observacion = "Nivel bajo: +20%"
        } else if (valorSuelo > alto) {
            factor = 0.8
            nivel = "ALTO"
            observacion = "Nivel alto: -20%"
        }

        val kgNutriente = dosisBase * factor
        val fertilizante = Catalogo.fertilizanteDe(nutriente)
        val kgProducto = kgNutriente / fertilizante.riqueza

        return LineaPrograma(
            nutriente = nutriente,
            fertilizante = fertilizante.nombre,
            nivel = nivel,
            observacion = observacion,
            kgNutrienteHa = kgNutriente,
            kgProductoTotal = kgProducto,
            kgProductoPorAplicacion = kgProducto / semanas
        )
    }
}
