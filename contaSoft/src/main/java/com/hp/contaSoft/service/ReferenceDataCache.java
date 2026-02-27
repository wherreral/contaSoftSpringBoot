package com.hp.contaSoft.service;

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
import com.hp.contaSoft.hibernate.entities.AFPFactors;
import com.hp.contaSoft.hibernate.entities.AfpFactorNickname;
import com.hp.contaSoft.hibernate.entities.IUT;
import com.hp.contaSoft.hibernate.entities._AFamiliar;

@Service
public class ReferenceDataCache {

	@Autowired private AFPFactorsRepository afpRepo;
	@Autowired private AfpFactorNicknameRepository nickRepo;
	@Autowired private IUTRepository iutRepo;
	@Autowired private _AFamiliarRepository afRepo;

	private Map<String, AFPFactors> afpByName = new HashMap<>();
	private Map<String, Long> afpByNickname = new HashMap<>();
	private Map<Long, AFPFactors> afpById = new HashMap<>();
	private List<IUT> iutTramos = new ArrayList<>();
	private List<_AFamiliar> afTramos = new ArrayList<>();

	@PostConstruct
	public void init() {
		reload();
	}

	public void reload() {
		afpByName.clear();
		afpById.clear();
		afpByNickname.clear();

		for (AFPFactors af : afpRepo.findAll()) {
			afpByName.put(af.getName().trim().toUpperCase(), af);
			afpById.put(af.getId(), af);
		}
		for (AfpFactorNickname nick : nickRepo.findAll()) {
			afpByNickname.put(nick.getNickname().trim().toUpperCase(), nick.getAfpFactorId());
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

		System.out.println("ReferenceDataCache loaded: "
			+ afpByName.size() + " AFPs, "
			+ afpByNickname.size() + " nicknames, "
			+ iutTramos.size() + " IUT tramos, "
			+ afTramos.size() + " AF tramos");
	}

	public AFPFactors findAfp(String prevision) {
		if (prevision == null) return null;
		String s = prevision.trim();
		if (s.length() > 4 && s.toUpperCase().startsWith("AFP ")) {
			s = s.substring(4).trim();
		}
		String key = s.toUpperCase();

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
}
