package com.hp.contaSoft.utils;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import com.hp.contaSoft.constant.HPConstant;
import com.hp.contaSoft.hibernate.dao.repositories.AFPFactorsRepository;
import com.hp.contaSoft.hibernate.dao.repositories.AfpFactorNicknameRepository;
import com.hp.contaSoft.hibernate.entities.AFPFactors;
import com.hp.contaSoft.hibernate.entities.AfpFactorNickname;
import java.util.List;

public class PayBookUtils {

	
	@Autowired static AFPFactorsRepository afpFactorsrepository;
	@Autowired static AfpFactorNicknameRepository afpFactorNicknameRepository;


	public static double findPrevisionValue(String prevision) {

		try {
			System.out.println("Prevision:"+prevision);
			if (prevision == null) return 0d;

			String s = prevision.trim();
			if (s.length() > 4 && s.toUpperCase().startsWith("AFP ")) {
				s = s.substring(4).trim();
			}

			AFPFactors af = null;
			try {
				af = afpFactorsrepository.findByNameIgnoreCase(s);
			} catch (Exception e) {
				// ignore and try nickname lookup
			}

			if (af == null) {
				try {
					List<AfpFactorNickname> nicks = afpFactorNicknameRepository.findByNicknameIgnoreCase(s);
					if (nicks != null && !nicks.isEmpty()) {
						AfpFactorNickname nn = nicks.get(0);
						af = afpFactorsrepository.findById(nn.getAfpFactorId()).orElse(null);
					}
				} catch (Exception e) {
					// ignore
				}
			}

			if (af != null) return af.getPercentaje();

		}
		catch (Exception e) {
			e.printStackTrace();
		}

		try {
			TimeUnit.SECONDS.sleep(HPConstant.SLEEP_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
}
