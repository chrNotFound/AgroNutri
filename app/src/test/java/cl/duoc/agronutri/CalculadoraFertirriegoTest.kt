package cl.duoc.agronutri

import cl.duoc.agronutri.data.AnalisisEntity
import cl.duoc.agronutri.data.TipoMuestra
import cl.duoc.agronutri.domain.CalculadoraFertirriego
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculadoraFertirriegoTest {

    private fun analisis(
        tipo: String,
        ph: Double? = null,
        ce: Double? = null,
        n: Double? = null,
        k: Double? = null
    ): AnalisisEntity {
        return AnalisisEntity(
            planId = "plan1", tipo = tipo,
            ph = ph, ce = ce, n = n, p = null, k = k, ca = null, mg = null
        )
    }

    @Test
    fun sinAnalisis_usaDosisBase() {
        val programa = CalculadoraFertirriego.calcular("Palto", "Engorde", null, null)

        assertEquals(5, programa.lineas.size)
        assertFalse(programa.conAnalisis)
        assertEquals(60.0, programa.lineas[0].kgNutrienteHa, 0.01)
        assertEquals("SIN_DATO", programa.lineas[0].nivel)
        assertEquals(8, programa.aplicaciones)
    }

    @Test
    fun productoSeCalculaConLaRiquezaDelFertilizante() {
        val programa = CalculadoraFertirriego.calcular("Palto", "Engorde", null, null)
        val lineaN = programa.lineas[0]

        assertEquals("Urea", lineaN.fertilizante)
        assertEquals(130.43, lineaN.kgProductoTotal, 0.01)
        assertEquals(130.43 / 8, lineaN.kgProductoPorAplicacion, 0.01)
    }

    @Test
    fun potasioBajoSubeLaDosisUnVeintePorCiento() {
        val suelo = analisis(TipoMuestra.SUELO, k = 50.0)
        val programa = CalculadoraFertirriego.calcular("Palto", "Engorde", suelo, null)
        val lineaK = programa.lineas[2]

        assertEquals("BAJO", lineaK.nivel)
        assertEquals(100.8, lineaK.kgNutrienteHa, 0.01)
        assertTrue(programa.conAnalisis)
    }

    @Test
    fun nitrogenoAltoBajaLaDosisUnVeintePorCiento() {
        val suelo = analisis(TipoMuestra.SUELO, n = 80.0)
        val programa = CalculadoraFertirriego.calcular("Palto", "Engorde", suelo, null)

        assertEquals("ALTO", programa.lineas[0].nivel)
        assertEquals(48.0, programa.lineas[0].kgNutrienteHa, 0.01)
    }

    @Test
    fun elAnalisisDeAguaNoCambiaLasDosis() {
        val agua = analisis(TipoMuestra.AGUA, n = 5.0)
        val programa = CalculadoraFertirriego.calcular("Palto", "Engorde", null, agua)

        assertEquals(60.0, programa.lineas[0].kgNutrienteHa, 0.01)
        assertEquals("SIN_DATO", programa.lineas[0].nivel)
    }

    @Test
    fun phBajoGeneraAlerta() {
        val suelo = analisis(TipoMuestra.SUELO, ph = 4.8)
        val programa = CalculadoraFertirriego.calcular("Tomate", "Floración", suelo, null)

        assertEquals(1, programa.alertas.size)
        assertTrue(programa.alertas[0].contains("pH bajo"))
    }

    @Test
    fun ceAltaEnElAguaGeneraAlerta() {
        val agua = analisis(TipoMuestra.AGUA, ce = 3.0)
        val programa = CalculadoraFertirriego.calcular("Cerezo", "Maduración", null, agua)

        assertTrue(programa.alertas.any { it.contains("agua") })
    }
}
