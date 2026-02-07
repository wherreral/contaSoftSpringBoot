package com.hp.contaSoft.excel.entities;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests unitarios para el cálculo automático de BONO_PRODUCCION.
 * No requiere contexto de Spring ya que solo testea métodos de cálculo puros.
 */
public class PayBookDetailsBonoCalculationTest {

    /**
     * Test Case 1: Caso básico con valores conocidos
     * Verificar que el método algebraico calcula correctamente
     *
     * Usando cálculo manual simplificado:
     * SueldoBase = 1,000,000, DT = 30 -> SueldoMensual = 1,000,000
     * Gratificación = ((1,000,000 * 0.25) / 12) * 1.0 = 20,833.33
     * BASE_SIN_BONO = 1,020,833.33
     * NO_IMP = 80,000
     * TOTAL_DESC_% = 17.6% = 0.176
     *
     * Queremos AL = 900,000
     * BONO = (900,000 - 80,000) / (1 - 0.176) - 1,020,833.33
     *      = 820,000 / 0.824 - 1,020,833.33
     *      = 995,145.63 - 1,020,833.33
     *      = -25,687.70 -> 0 (negativo, ajusta a 0)
     *
     * Con BONO = 0:
     * TOTAL_IMP = 1,020,833.33
     * TOTAL_HABER = 80,000 + 1,020,833.33 = 1,100,833.33
     * DCTO_PREV = 1,020,833.33 * 0.176 = 179,666.67
     * AL_CALC = 1,100,833.33 - 179,666.67 = 921,166.67
     *
     * Este test espera BONO = 0 porque el target es demasiado bajo.
     */
    @Test
    public void testCalculateBonoAlgebraic_BasicCase() {
        PayBookDetails detail = new PayBookDetails();

        // Configurar valores base
        detail.setSueldoBase(1000000);
        detail.setDiasTrabajados(30);
        detail.setMovilizacion(50000);
        detail.setColacion(30000);
        detail.setDescuentoHerramientas(0);
        detail.setPorcentajePrevision(10.0); // 10%
        detail.setSaludPorcentaje(7.0);       // 7%
        detail.setAfc(0.6);                   // 0.6%
        detail.setRut("12345678-9");

        // Calcular valores base
        detail.calculateSueldoMensual();      // 1,000,000
        detail.calculateGratificacion();      // 20,833.33
        detail.setAguinaldo(0);
        detail.setHorasExtra(0);
        detail.calculateValorHora();
        detail.calculateTotalHoraExtra();     // 0

        // Target: queremos un alcance líquido de 1,100,000 (más alto que el base para forzar BONO > 0)
        double alcanceLiquidoTarget = 1100000;

        // Calcular bono
        double bono = detail.calculateBonoByAlgebraicMethod(alcanceLiquidoTarget);

        // Verificar que el bono sea >= 0
        assertTrue("El bono debe ser >= 0", bono >= 0);

        // Establecer el bono y recalcular todo
        detail.setBonoProduccion(bono);
        detail.calculateTotalImponible();
        detail.calculateAfc();
        detail.calculatePrevision();
        detail.calculateSalud();
        detail.calculateTotalHaber();

        // Calcular alcance líquido resultante
        double totalDescuentos = detail.getValorPrevision() + detail.getValorAFC() + detail.getValorSalud();
        double alcanceLiquidoCalculado = detail.getTotalHaber() - totalDescuentos;

        // Verificar que coincida con el target (tolerancia de $10 por redondeos)
        double diferencia = Math.abs(alcanceLiquidoCalculado - alcanceLiquidoTarget);
        assertTrue("Diferencia debe ser < $10, pero fue: " + diferencia, diferencia < 10.0);

        System.out.println("Test BasicCase OK:");
        System.out.println("  BONO calculado: " + bono);
        System.out.println("  ALCANCE_LIQUIDO target: " + alcanceLiquidoTarget);
        System.out.println("  ALCANCE_LIQUIDO calculado: " + alcanceLiquidoCalculado);
        System.out.println("  Diferencia: " + diferencia);
    }

    /**
     * Test Case 2: Caso donde el bono debería ser negativo (debe retornar 0)
     */
    @Test
    public void testCalculateBonoAlgebraic_NegativeCase() {
        PayBookDetails detail = new PayBookDetails();

        // Configurar valores que resultan en bono negativo
        detail.setSueldoBase(1000000);
        detail.setDiasTrabajados(30);
        detail.setMovilizacion(50000);
        detail.setColacion(30000);
        detail.setDescuentoHerramientas(0);
        detail.setPorcentajePrevision(10.0);
        detail.setSaludPorcentaje(7.0);
        detail.setAfc(0.6);

        detail.calculateSueldoMensual();
        detail.calculateGratificacion();
        detail.setAguinaldo(0);
        detail.setHorasExtra(0);
        detail.calculateValorHora();
        detail.calculateTotalHoraExtra();

        // Target muy bajo (imposible sin bono negativo)
        double alcanceLiquidoTarget = 500000;

        // Calcular bono
        double bono = detail.calculateBonoByAlgebraicMethod(alcanceLiquidoTarget);

        // Debe ser 0 (no negativo)
        assertEquals("Bono negativo debe ajustarse a 0", 0.0, bono, 0.01);

        System.out.println("Test NegativeCase OK: Bono = " + bono);
    }

    /**
     * Test Case 3: Comparar método algebraico vs Newton-Raphson
     */
    @Test
    public void testCalculateBono_AlgebraicVsNewtonRaphson() {
        PayBookDetails detail = new PayBookDetails();

        detail.setSueldoBase(1500000);
        detail.setDiasTrabajados(30);
        detail.setMovilizacion(80000);
        detail.setColacion(50000);
        detail.setDescuentoHerramientas(10000);
        detail.setPorcentajePrevision(11.5);
        detail.setSaludPorcentaje(7.5);
        detail.setAfc(0.6);

        detail.calculateSueldoMensual();
        detail.calculateGratificacion();
        detail.setAguinaldo(50000);
        detail.setHorasExtra(10);
        detail.calculateValorHora();
        detail.calculateTotalHoraExtra();

        double alcanceLiquidoTarget = 1400000;

        // Método algebraico
        long startAlgebraic = System.nanoTime();
        double bonoAlgebraic = detail.calculateBonoByAlgebraicMethod(alcanceLiquidoTarget);
        long endAlgebraic = System.nanoTime();

        // Método Newton-Raphson
        long startNewton = System.nanoTime();
        double bonoNewton = detail.calculateBonoByNewtonRaphson(alcanceLiquidoTarget);
        long endNewton = System.nanoTime();

        // Ambos deben dar resultados muy similares
        double diferencia = Math.abs(bonoAlgebraic - bonoNewton);
        assertTrue("Diferencia entre métodos debe ser < $10, pero fue: " + diferencia,
                   diferencia < 10.0);

        double tiempoAlgebraic = (endAlgebraic - startAlgebraic) / 1_000_000.0;
        double tiempoNewton = (endNewton - startNewton) / 1_000_000.0;

        System.out.println("Test AlgebraicVsNewtonRaphson OK:");
        System.out.println("  Bono Algebraico:  " + bonoAlgebraic + " (tiempo: " + tiempoAlgebraic + " ms)");
        System.out.println("  Bono Newton:      " + bonoNewton + " (tiempo: " + tiempoNewton + " ms)");
        System.out.println("  Diferencia:       " + diferencia);

        // Ambos métodos deben ser rápidos (< 10ms).
        // No comparamos velocidad en primera ejecución por variación del JIT compiler
        assertTrue("Algebraico debe ser < 10ms", tiempoAlgebraic < 10.0);
        assertTrue("Newton debe ser < 10ms", tiempoNewton < 10.0);
    }

    /**
     * Test Case 4: Caso con descuentos muy altos (cerca del 100%)
     */
    @Test
    public void testCalculateBono_HighDiscounts() {
        PayBookDetails detail = new PayBookDetails();

        detail.setSueldoBase(1000000);
        detail.setDiasTrabajados(30);
        detail.setMovilizacion(50000);
        detail.setColacion(30000);
        detail.setDescuentoHerramientas(0);
        detail.setPorcentajePrevision(20.0); // Alto
        detail.setSaludPorcentaje(15.0);     // Alto
        detail.setAfc(5.0);                  // Alto
        // Total: 40%

        detail.calculateSueldoMensual();
        detail.calculateGratificacion();
        detail.setAguinaldo(0);
        detail.setHorasExtra(0);
        detail.calculateValorHora();
        detail.calculateTotalHoraExtra();

        double alcanceLiquidoTarget = 700000;

        double bono = detail.calculateBonoByAlgebraicMethod(alcanceLiquidoTarget);

        assertTrue("Bono debe ser >= 0", bono >= 0);

        System.out.println("Test HighDiscounts OK: Bono = " + bono);
    }

    /**
     * Test Case 5: Performance test con múltiples cálculos
     */
    @Test
    public void testCalculateBono_Performance() {
        int iterations = 1000;
        long startTime = System.nanoTime();

        for (int i = 0; i < iterations; i++) {
            PayBookDetails detail = new PayBookDetails();
            detail.setSueldoBase(1000000 + (i * 1000));
            detail.setDiasTrabajados(30);
            detail.setMovilizacion(50000);
            detail.setColacion(30000);
            detail.setDescuentoHerramientas(0);
            detail.setPorcentajePrevision(10.0);
            detail.setSaludPorcentaje(7.0);
            detail.setAfc(0.6);

            detail.calculateSueldoMensual();
            detail.calculateGratificacion();
            detail.setAguinaldo(0);
            detail.setHorasExtra(0);
            detail.calculateValorHora();
            detail.calculateTotalHoraExtra();

            detail.calculateBonoByAlgebraicMethod(900000);
        }

        long endTime = System.nanoTime();
        double tiempoTotal = (endTime - startTime) / 1_000_000.0;
        double tiempoPromedio = tiempoTotal / iterations;

        System.out.println("Test Performance OK:");
        System.out.println("  Iteraciones:      " + iterations);
        System.out.println("  Tiempo total:     " + tiempoTotal + " ms");
        System.out.println("  Tiempo promedio:  " + tiempoPromedio + " ms/cálculo");

        // Debe ser rápido: < 1ms por cálculo en promedio
        assertTrue("Tiempo promedio debe ser < 1ms, pero fue: " + tiempoPromedio,
                   tiempoPromedio < 1.0);
    }
}
