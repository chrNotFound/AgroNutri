package cl.duoc.agronutri.domain

data class Requerimiento(val n: Double, val p: Double, val k: Double, val ca: Double, val mg: Double)

data class Fertilizante(
    val nombre: String,
    val nutriente: String,
    val composicion: String,
    val riqueza: Double
)

object Catalogo {

    val cultivos = listOf("Palto", "Cítricos", "Tomate", "Uva de mesa", "Cerezo")
    val etapas = listOf("Floración", "Engorde", "Maduración")

    val fertilizantes = listOf(
        Fertilizante("Urea", "N", "46% N", 0.46),
        Fertilizante("Fosfato monoamónico", "P", "12-61-0 (N-P2O5-K2O)", 0.27),
        Fertilizante("Nitrato de potasio", "K", "13-0-46 (N-P2O5-K2O)", 0.38),
        Fertilizante("Nitrato de calcio", "Ca", "15,5% N y 19% Ca", 0.19),
        Fertilizante("Sulfato de magnesio", "Mg", "9,8% Mg", 0.098)
    )

    private val base = mapOf(
        "Palto" to Requerimiento(50.0, 10.0, 70.0, 20.0, 8.0),
        "Cítricos" to Requerimiento(60.0, 12.0, 80.0, 22.0, 9.0),
        "Tomate" to Requerimiento(60.0, 15.0, 90.0, 30.0, 10.0),
        "Uva de mesa" to Requerimiento(45.0, 10.0, 75.0, 18.0, 7.0),
        "Cerezo" to Requerimiento(40.0, 8.0, 60.0, 20.0, 6.0)
    )
    private val factorEtapa = mapOf("Floración" to 0.8, "Engorde" to 1.2, "Maduración" to 0.7)
    private val semanasEtapa = mapOf("Floración" to 4, "Engorde" to 8, "Maduración" to 4)

    fun requerimientoBase(cultivo: String): Requerimiento {
        return base[cultivo] ?: Requerimiento(45.0, 10.0, 70.0, 20.0, 8.0)
    }

    fun requerimiento(cultivo: String, etapa: String): Requerimiento {
        val b = requerimientoBase(cultivo)
        val f = factorEtapa[etapa] ?: 1.0
        return Requerimiento(b.n * f, b.p * f, b.k * f, b.ca * f, b.mg * f)
    }

    fun semanas(etapa: String): Int {
        return semanasEtapa[etapa] ?: 4
    }

    fun fertilizanteDe(nutriente: String): Fertilizante {
        return fertilizantes.first { it.nutriente == nutriente }
    }
}
