package com.hp.contaSoft.excel.entities;



import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hp.contaSoft.hibernate.dao.service.FileUtilsService;
import com.hp.contaSoft.constant.Regimen;
import com.hp.contaSoft.hibernate.entities.Base;
import com.hp.contaSoft.hibernate.entities.PayBookInstance;
import com.hp.contaSoft.hibernate.entities.Taxpayer;
import com.hp.contaSoft.utils.PayBookUtils;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@Getter
@Setter
@ToString
@Entity
@Table(name="PayBookDetails")
public class PayBookDetails {

	
	public PayBookDetails() {
		
	}
	
	@PrePersist
	public void valueColumns() {
		
		
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column
	private String familyId;
	
	/**
	 * Required Fields
	 */
	@Column
	@CsvBindByName(column = "RUT")
	private String rut;
	
	@Column
	@CsvBindByName(column = "CENTRO_COSTO")
	private String centroCosto;
	
	@Column
	@CsvBindByName(column = "SUELDO_BASE")
	private double sueldoBase;
	
	@Column
	@CsvBindByName(column = "DT")
	private int diasTrabajados;
	
	@Column
	@CsvBindByName(column = "PREVISION")
	private String prevision;
	
	@Column
	@CsvBindByName(column = "SALUD")
	private String salud;
	
	@Column
	@CsvBindByName(column = "SALUD_PORCENTAJE")
	private double saludPorcentaje;
	
	/**
	 * Non Required Fields
	 */
	@Column
	@CsvBindByName(column = "BONO")
	private double bonoProduccion;
	
	@Column
	private double aguinaldo;
	
	@Column
	@CsvBindByName(column = "HORAS_EXTRA")
	private double horasExtra;
	
	@Column
	@CsvBindByName(column = "ASIG_FAMILIAR")
	private int asignacionFamiliar;
	
	@Column
	@CsvBindByName(column = "MOVILIZACION")
	private double movilizacion;
	
	@Column
	@CsvBindByName(column = "COLACION")
	private double colacion;
	
	@Column
	@CsvBindByName(column = "DESGASTE")
	private double descuentoHerramientas;
	
	@Column
	@CsvBindByName(column = "AFC")
	private double afc;

	@Column
	@CsvBindByName(column = "ALCANCE_LIQUIDO")
	private Double alcanceLiquido;

	@Column
	private int apv;
	
	@Column
	private double prestamos;
	
	@Column
	private double seguroOncologico;
	
	@Column
	private String seguroOAccidentes;
	
	@Column
	private double anticipo;

	@Column
	@CsvBindByName(column = "DESC_APV_CTA_AH")
	private double descApvCtaAh;

	@Column
	@CsvBindByName(column = "DESC_PTMO_CCAAFF")
	private double descPtmoCcaaff;

	@Column
	@CsvBindByName(column = "DESC_PTMO_SOLIDARIO")
	private double descPtmoSolidario;

	@Column
	@CsvBindByName(column = "REGIMEN")
	private String regimen;

	/**
	 * Returns the effective Regimen. Defaults to INDEFINIDO if not set.
	 */
	public Regimen getRegimenEffective() {
		if (this.regimen == null || this.regimen.trim().isEmpty()) {
			return Regimen.INDEFINIDO;
		}
		try {
			return Regimen.valueOf(this.regimen.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			System.err.println("ADVERTENCIA: Régimen desconocido '" + this.regimen + "' para RUT " + this.rut + ". Usando INDEFINIDO.");
			return Regimen.INDEFINIDO;
		}
	}

	/**
	 * Campos Calculados
	 */
	
	@Column
	private double sueldoMensual;
	
	@Column
	private double gratificacion;
	
	@Column
	private double valorHora;
	
	@Column
	private double totalImponible;
	
	@Column
	private double totalHaber;
	
	@Column
	private double porcentajePrevision;
	
	//@Formula("totalImponible * porcentajePrevision")
	@Column
	private double valorPrevision;
	
	public double getPorcentajePrevision2() {
	    return this.porcentajePrevision;
	}
	
	@Column
	private double valorSalud;
	
	@Column
	private double valorAFC;
	
	@Column
	private double totalAsignacionFamiliar;
	
	@Column
	private double valorIUT;
	
	@Column
	private double valorSeguroOAccidentes;
	
	@Column
	private double rentaLiquidaImponible;
	
	@Column
	private double totalHoraExtra;

	@Column
	private double afcEmpleador;

	@Column
	private double sisEmpleador;
	
	@JsonBackReference
	@ManyToOne(cascade={CascadeType.ALL})
	@JoinColumn(name="payBookInstance_id")
	private PayBookInstance payBookInstance;

	/**
	 * Calculators
	 */
	
	public void calculateAsignacionFamiliar(Double amount) {
		//que renta se utiliza en la asignacion familiar?
		this.totalAsignacionFamiliar = this.asignacionFamiliar * amount;
	}
	
	
	public void calculateSueldoMensual() {
		this.sueldoMensual = ( this.sueldoBase / 30 ) * this.diasTrabajados;
	}
	
	public void calculateGratificacion() {
		// Gratificación = (25% del sueldo base / 12 meses) * proporción de días trabajados
		this.gratificacion = ((this.sueldoBase * 4.75) / 12) * (this.diasTrabajados / 30.0); 
	}

	public void calculateValorHora() {
		this.valorHora = (( this.sueldoBase / 30) * 7) / (45 * 1.5); 
	}
	
	public void calculateTotalImponible() {
		this.totalImponible = this.sueldoMensual +  this.gratificacion + 
				this.bonoProduccion + this.aguinaldo + this.totalHoraExtra; 
	}
	
	public void calculateTotalHaber() {
		this.totalHaber = this.totalImponible + this.colacion + this.movilizacion + this.totalAsignacionFamiliar + this.descuentoHerramientas; 
	}
	
	public void calculatePrevision() {
		//this.valorPrevision = this.totalImponible * PayBookUtils.findPrevisionValue(this.prevision);
		//this.valorPrevision = this.totalImponible * fileUtilsService.findPrevisionValue(this.prevision);
		//this.valorPrevision = this.getPorcentajePrevision();
		//this.valorPrevision = getPorcentajePrevision2();
		this.valorPrevision = (this.totalImponible * this.getPorcentajePrevision()) / 100;
	
	}

	public void calculateSalud() {
		this.valorSalud = (this.totalImponible * this.saludPorcentaje)/ 100; 
	}
	
	public void calculateAfc() {
		// AFC only applies to INDEFINIDO contracts
		if (getRegimenEffective() == Regimen.PLAZO_FIJO) {
			this.valorAFC = 0.0;
			return;
		}
		this.valorAFC = (this.afc * this.totalImponible) / 100.0;
	}

	/**
	 * Calculated transient value (not persisted):
	 * TOTAL DCTO. PREVISIONAL = AFP (valorPrevision) + AFC (valorAFC) + Salud (valorSalud)
	 * Jackson will serialize this getter as `totalDctoPrevisional` in the JSON response.
	 */
	public double getTotalDctoPrevisional() {
		// AFC excluded from 'descuentos previsionales' as requested.
		double vp = this.getValorPrevision();
		double vs = this.getValorSalud();
		return vp + vs;
	}
	
	public void calculateRentaLiquidaImponible() {
		this.rentaLiquidaImponible = this.totalImponible -
				(this.getValorSalud() + this.getValorPrevision() + this.getValorAFC()); 
	}

	public void calculateSeguroAccidentes() {
		this.valorSeguroOAccidentes = 1;
	}

	/**
	 * Calcula AFC empleador: 2.4% para INDEFINIDO, 3.0% para PLAZO_FIJO
	 */
	public void calculateAfcEmpleador() {
		if (getRegimenEffective() == Regimen.PLAZO_FIJO) {
			this.afcEmpleador = this.totalImponible * 0.03;
		} else {
			this.afcEmpleador = this.totalImponible * 0.024;
		}
	}

	/**
	 * Calcula SIS (Seguro de Invalidez y Sobrevivencia) — cargo del empleador
	 * @param sisTasa tasa SIS de la AFP (ej: 1.49)
	 */
	public void calculateSis(double sisTasa) {
		this.sisEmpleador = this.totalImponible * sisTasa / 100.0;
	}

	/**
	 * Total costo empleador (transient)
	 */
	public double getTotalCostoEmpleador() {
		return this.afcEmpleador + this.sisEmpleador;
	}
	
	/**
	 * Calculo de IUT (Impuesto Unico Trabajadores)
	 * Formula SII: IUT = (rentaLiquidaImponible * factor) - cantidadARebajar
	 */

	public void calculateValorUIT(double factor, double rebaja) {
		this.valorIUT = Math.max(0, (this.rentaLiquidaImponible * factor) - rebaja);
	}
	
	public void calculateTotalHoraExtra() {
		this.totalHoraExtra = (this.valorHora * this.horasExtra);
	}

	/**
	 * Calcula el BONO_PRODUCCION usando el método algebraico directo.
	 *
	 * Fórmula derivada:
	 * BONO = [AL_TARGET - NO_IMP - BASE_IMP_SIN_BONO * (1 - TOTAL_DESC_%)] / (1 - TOTAL_DESC_%)
	 *
	 * donde:
	 * - AL_TARGET = alcanceLiquido del CSV (valor conocido)
	 * - NO_IMP = movilizacion + colacion + descuentoHerramientas
	 * - BASE_IMP_SIN_BONO = sueldoMensual + gratificacion + aguinaldo + totalHoraExtra
	 * - TOTAL_DESC_% = (porcentajePrevision + saludPorcentaje + afc) / 100
	 *
	 * @param alcanceLiquidoTarget El alcance líquido objetivo del CSV
	 * @return El bono calculado (>= 0)
	 */
	public double calculateBonoByAlgebraicMethod(double alcanceLiquidoTarget) {
		// Componentes NO imponibles
		double noImponible = this.movilizacion + this.colacion + this.descuentoHerramientas + this.totalAsignacionFamiliar;

		// Base imponible SIN bono (ya calculados antes)
		double baseImponibleSinBono = this.sueldoMensual + this.gratificacion +
									   this.aguinaldo + this.totalHoraExtra;

		// Porcentaje TOTAL de descuentos previsionales (AFP + Salud + AFC si INDEFINIDO)
		double afcEfectivo = (getRegimenEffective() == Regimen.PLAZO_FIJO) ? 0.0 : this.afc;
		double totalDescuentoPorcentaje = (this.porcentajePrevision + this.saludPorcentaje + afcEfectivo) / 100.0;

		// Fórmula algebraica directa
		// ALCANCE_LIQUIDO = TOTAL_HABER - DCTO_PREVISIONALES
		// TOTAL_HABER = NO_IMP + TOTAL_IMPONIBLE
		// TOTAL_IMPONIBLE = BASE_IMP_SIN_BONO + BONO
		// DCTO_PREVISIONALES = TOTAL_IMPONIBLE * TOTAL_DESC_%
		//
		// Sustituyendo y resolviendo para BONO:
		// AL = NO_IMP + BASE_IMP_SIN_BONO + BONO - (BASE_IMP_SIN_BONO + BONO) * TOTAL_DESC_%
		// AL = NO_IMP + (BASE_IMP_SIN_BONO + BONO) * (1 - TOTAL_DESC_%)
		// AL - NO_IMP = (BASE_IMP_SIN_BONO + BONO) * (1 - TOTAL_DESC_%)
		// (AL - NO_IMP) / (1 - TOTAL_DESC_%) = BASE_IMP_SIN_BONO + BONO
		// BONO = (AL - NO_IMP) / (1 - TOTAL_DESC_%) - BASE_IMP_SIN_BONO

		double numerador = alcanceLiquidoTarget - noImponible;
		double denominador = 1.0 - totalDescuentoPorcentaje;

		// Validación: denominador no puede ser cero o negativo
		if (denominador <= 0.0) {
			System.err.println("ERROR: Porcentaje de descuentos >= 100% para RUT " + this.rut);
			return 0.0;
		}

		double bonoCalculado = (numerador / denominador) - baseImponibleSinBono;

		// Validación: bono no puede ser negativo (requisito del usuario)
		if (bonoCalculado < 0.0) {
			System.out.println("ADVERTENCIA: Bono calculado negativo (" + bonoCalculado +
							 ") para RUT " + this.rut + ". Ajustando a 0.");
			return 0.0;
		}

		return bonoCalculado;
	}

	/**
	 * Calcula el BONO_PRODUCCION usando el método Newton-Raphson.
	 * Este método es iterativo y más robusto en casos extremos.
	 *
	 * Definimos: f(BONO) = ALCANCE_LIQUIDO_CALCULADO - ALCANCE_LIQUIDO_TARGET
	 * Buscamos BONO tal que f(BONO) = 0
	 *
	 * @param alcanceLiquidoTarget El alcance líquido objetivo del CSV
	 * @return El bono calculado (>= 0)
	 */
	public double calculateBonoByNewtonRaphson(double alcanceLiquidoTarget) {
		// Componentes que NO dependen del bono
		double noImponible = this.movilizacion + this.colacion + this.descuentoHerramientas + this.totalAsignacionFamiliar;
		double baseImponibleSinBono = this.sueldoMensual + this.gratificacion +
									   this.aguinaldo + this.totalHoraExtra;
		double afcEfectivo = (getRegimenEffective() == Regimen.PLAZO_FIJO) ? 0.0 : this.afc;
		double totalDescuentoPorcentaje = (this.porcentajePrevision + this.saludPorcentaje + afcEfectivo) / 100.0;

		// Validación
		if (totalDescuentoPorcentaje >= 1.0) {
			System.err.println("ERROR: Porcentaje de descuentos >= 100% para RUT " + this.rut);
			return 0.0;
		}

		// Configuración Newton-Raphson
		final int MAX_ITERATIONS = 50;
		final double TOLERANCE = 0.01; // Tolerancia de 1 centavo

		// Valor inicial: usar método algebraico como semilla
		double bonoActual = calculateBonoByAlgebraicMethod(alcanceLiquidoTarget);

		// Si el método algebraico ya dio 0, retornar directamente
		if (bonoActual <= 0.0) {
			return 0.0;
		}

		// Iteración Newton-Raphson
		for (int i = 0; i < MAX_ITERATIONS; i++) {
			// Calcular ALCANCE_LIQUIDO con el bono actual
			// ALCANCE_LIQUIDO = TOTAL_HABER - DCTO_PREVISIONALES
			double totalImponible = baseImponibleSinBono + bonoActual;
			double totalHaber = noImponible + totalImponible;
			double dctoPrevisionales = totalImponible * totalDescuentoPorcentaje;
			double alcanceLiquidoCalculado = totalHaber - dctoPrevisionales;

			// f(BONO) = alcanceLiquidoCalculado - alcanceLiquidoTarget
			double f = alcanceLiquidoCalculado - alcanceLiquidoTarget;

			// Verificar convergencia
			if (Math.abs(f) < TOLERANCE) {
				// Validar que no sea negativo
				if (bonoActual < 0.0) {
					System.out.println("ADVERTENCIA: Newton-Raphson convergió a bono negativo para RUT " +
									 this.rut + ". Ajustando a 0.");
					return 0.0;
				}
				System.out.println("Newton-Raphson convergió en " + (i + 1) + " iteraciones para RUT " + this.rut);
				return bonoActual;
			}

			// f'(BONO) = derivada respecto a BONO
			// d/dBONO[ALCANCE_LIQUIDO] = d/dBONO[NO_IMP + BASE + BONO - (BASE + BONO) * DESC%]
			//                          = 1 - DESC%
			double fPrima = 1.0 - totalDescuentoPorcentaje;

			if (Math.abs(fPrima) < 1e-10) {
				System.err.println("ERROR: Derivada muy pequeña en Newton-Raphson para RUT " + this.rut);
				break;
			}

			// Actualización Newton-Raphson: BONO_nuevo = BONO_actual - f(BONO) / f'(BONO)
			double bonoNuevo = bonoActual - (f / fPrima);

			// Evitar bonos negativos durante la iteración
			if (bonoNuevo < 0.0) {
				bonoNuevo = 0.0;
			}

			bonoActual = bonoNuevo;
		}

		// Si no convergió, usar el último valor (con validación)
		System.out.println("ADVERTENCIA: Newton-Raphson no convergió completamente para RUT " +
						 this.rut + ". Usando último valor.");
		return Math.max(0.0, bonoActual);
	}

	/**
	 * Calcula el BONO_PRODUCCION basándose en el ALCANCE_LIQUIDO del CSV.
	 * Usa el método configurado en SystemParameter "BONO_CALCULATION_METHOD".
	 *
	 * @param alcanceLiquidoTarget El alcance líquido del CSV
	 * @param calculationMethod El método a usar: "ALGEBRAIC" o "NEWTON_RAPHSON"
	 * @return El bono calculado
	 */
	public double calculateBonoFromAlcanceLiquido(double alcanceLiquidoTarget, String calculationMethod) {
		if (calculationMethod == null || calculationMethod.isEmpty()) {
			calculationMethod = "ALGEBRAIC"; // Default
		}

		calculationMethod = calculationMethod.trim().toUpperCase();

		System.out.println("Calculando BONO para RUT " + this.rut +
						 " usando método: " + calculationMethod +
						 " con ALCANCE_LIQUIDO target: " + alcanceLiquidoTarget);

		double bonoCalculado;

		if ("NEWTON_RAPHSON".equals(calculationMethod)) {
			bonoCalculado = calculateBonoByNewtonRaphson(alcanceLiquidoTarget);
		} else {
			// Default a algebraico (más rápido)
			bonoCalculado = calculateBonoByAlgebraicMethod(alcanceLiquidoTarget);
		}

		System.out.println("BONO calculado para RUT " + this.rut + ": " + bonoCalculado);

		return bonoCalculado;
	}

	public double getUIT(double rentaLiquidaImponible) {
		return 0.0;
	}
	
	
	
	
	private double findSaludValue(String prevision) {
		// TODO Auto-generated method stub
		return 0.1;
	}
	
	private double findAsignacionFamiliar(double sueldo, int cantidad) {
		// TODO Auto-generated method stub
		return 0.1;
	}


	public PayBookDetails(String rut, String centroCosto, double sueldoBase, int diasTrabajados, String prevision,
			String salud) {
		super();
		this.rut = rut;
		this.centroCosto = centroCosto;
		this.sueldoBase = sueldoBase;
		this.diasTrabajados = diasTrabajados;
		this.prevision = prevision;
		this.salud = salud;
	}
}
