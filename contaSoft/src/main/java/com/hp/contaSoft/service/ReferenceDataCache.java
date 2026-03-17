package com.hp.contaSoft.service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hp.contaSoft.hibernate.dao.repositories.AFPFactorsRepository;
import com.hp.contaSoft.hibernate.dao.repositories.AfpFactorNicknameRepository;
import com.hp.contaSoft.hibernate.dao.repositories.IUTRepository;
import com.hp.contaSoft.hibernate.dao.repositories._AFamiliarRepository;
import com.hp.contaSoft.hibernate.dao.repositories.CotizacionEmpleadorLey21735Repository;
import com.hp.contaSoft.hibernate.entities.AFPFactors;
import com.hp.contaSoft.hibernate.entities.AfpFactorNickname;
import com.hp.contaSoft.hibernate.entities.CotizacionEmpleadorLey21735;
import com.hp.contaSoft.hibernate.entities.IUT;
import com.hp.contaSoft.hibernate.entities._AFamiliar;

@Service
public class ReferenceDataCache {

	@Autowired private AFPFactorsRepository afpRepo;
	@Autowired private AfpFactorNicknameRepository nickRepo;
	@Autowired private IUTRepository iutRepo;
	@Autowired private _AFamiliarRepository afRepo;
	@Autowired private CotizacionEmpleadorLey21735Repository cotLey21735Repo;

	private Map<String, AFPFactors> afpByName = new HashMap<>();
	private Map<String, Long> afpByNickname = new HashMap<>();
	private Map<Long, AFPFactors> afpById = new HashMap<>();
	private List<IUT> iutTramos = new ArrayList<>();
	private List<_AFamiliar> afTramos = new ArrayList<>();
	private List<CotizacionEmpleadorLey21735> cotLey21735Tramos = new ArrayList<>();

	@PostConstruct
	public void init() {
		reload();
	}

	public void reload() {
		afpByName.clear();
		afpById.clear();
		afpByNickname.clear();

		for (AFPFactors af : afpRepo.findAll()) {
			afpByName.put(stripAccents(af.getName().trim().toUpperCase()), af);
			afpById.put(af.getId(), af);
		}
		for (AfpFactorNickname nick : nickRepo.findAll()) {
			afpByNickname.put(stripAccents(nick.getNickname().trim().toUpperCase()), nick.getAfpFactorId());
		}

		iutTramos = new ArrayList<>();
		iutRepo.findAll().forEach(iutTramos::add);

		// Si no existen tramos de asignación familiar en la BD, insertarlos (idempotente)
		if (afRepo.count() == 0) {
			afRepo.save(new _AFamiliar("A", 1.0, 631976.0, 22007.0));
			afRepo.save(new _AFamiliar("B", 631977.0, 923067.0, 13505.0));
			afRepo.save(new _AFamiliar("C", 923068.0, 1439668.0, 4267.0));
			afRepo.save(new _AFamiliar("D", 1439669.0, Double.MAX_VALUE, 0.0));
		}

		afTramos = new ArrayList<>();
		afRepo.findAll().forEach(afTramos::add);

		cotLey21735Tramos = new ArrayList<>(cotLey21735Repo.findAll());

		System.out.println("ReferenceDataCache loaded: "
			+ afpByName.size() + " AFPs, "
			+ afpByNickname.size() + " nicknames, "
			+ iutTramos.size() + " IUT tramos, "
			+ afTramos.size() + " AF tramos, "
			+ cotLey21735Tramos.size() + " tramos Ley 21.735");
	}

	public AFPFactors findAfp(String prevision) {
		if (prevision == null) return null;
		String s = prevision.trim();
		if (s.length() > 4 && s.toUpperCase().startsWith("AFP ")) {
			s = s.substring(4).trim();
		}
		String key = stripAccents(s.toUpperCase());

		AFPFactors af = afpByName.get(key);
		if (af != null) return af;

		Long afpId = afpByNickname.get(key);
		if (afpId != null) return afpById.get(afpId);

		System.out.println("WARN: AFP no encontrada para '" + prevision + "' (key='" + key + "'). "
			+ "Nombres disponibles: " + afpByName.keySet()
			+ ", Nicknames disponibles: " + afpByNickname.keySet());
		return null;
	}

	public IUT findIutTramo(double rentaLiquidaImponible) {
		for (IUT tramo : iutTramos) {
			if (rentaLiquidaImponible > tramo.getDesde()
				&& rentaLiquidaImponible <= tramo.getHasta()) {
				return tramo;
			}
		}
		return null;
	}

	public Double findAsigFamiliarAmount(double imponible) {
		for (_AFamiliar tramo : afTramos) {
			if (imponible > tramo.getDesde() && imponible < tramo.getHasta()) {
				return tramo.getAmount();
			}
		}
		return null;
	}

	/**
	 * Busca el tramo de cotización empleador Ley 21.735 vigente
	 * según el mes y año del período de remuneración.
	 * @param month nombre del mes en español (ej: "MARZO", "AGOSTO")
	 * @param year año como String (ej: "2026")
	 */
	public CotizacionEmpleadorLey21735 findCotizacionEmpleador(String month, String year) {
		if (month == null || year == null) return null;
		try {
			int yearInt = Integer.parseInt(year.trim());
			int monthInt = mesANumero(month.trim().toUpperCase());
			if (monthInt == 0) return null;

			java.util.Calendar cal = java.util.Calendar.getInstance();
			cal.set(yearInt, monthInt - 1, 15, 0, 0, 0); // día 15 del mes
			java.util.Date fecha = cal.getTime();

			for (CotizacionEmpleadorLey21735 tramo : cotLey21735Tramos) {
				if (!fecha.before(tramo.getFechaDesde()) && !fecha.after(tramo.getFechaHasta())) {
					return tramo;
				}
			}
		} catch (Exception e) {
			System.out.println("WARN: Error buscando tramo Ley 21.735: " + e.getMessage());
		}
		return null;
	}

	private String stripAccents(String s) {
		if (s == null) return null;
		return Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
	}

	private int mesANumero(String mes) {
		switch (mes) {
			case "ENERO": return 1;
			case "FEBRERO": return 2;
			case "MARZO": return 3;
			case "ABRIL": return 4;
			case "MAYO": return 5;
			case "JUNIO": return 6;
			case "JULIO": return 7;
			case "AGOSTO": return 8;
			case "SEPTIEMBRE": return 9;
			case "OCTUBRE": return 10;
			case "NOVIEMBRE": return 11;
			case "DICIEMBRE": return 12;
			default: return 0;
		}
	}
}
