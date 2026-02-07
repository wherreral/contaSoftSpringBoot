package com.hp.contaSoft.spring.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hp.contaSoft.hibernate.dao.repositories.SubsidiaryRepository;
import com.hp.contaSoft.hibernate.dao.repositories.TaxpayerRepository;
import com.hp.contaSoft.hibernate.entities.Subsidiary;
import com.hp.contaSoft.hibernate.entities.Taxpayer;

@Controller
@RequestMapping("/sucursales")
public class SubsidiaryController {

	@Autowired
	private SubsidiaryRepository subsidiaryRepository;
	
	@Autowired
	private TaxpayerRepository taxpayerRepository;
	
	@GetMapping
	public String showSubsidiaries(Model model, org.springframework.security.core.Authentication authentication) {
		// Obtener familyId del usuario autenticado
		String familyId = null;
		if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof com.hp.contaSoft.custom.CurrentUser) {
			com.hp.contaSoft.custom.CurrentUser currentUser = (com.hp.contaSoft.custom.CurrentUser) authentication.getPrincipal();
			familyId = currentUser.getFamilId();
		}
		
		List<Subsidiary> subsidiaries;
		if (familyId != null) {
			subsidiaries = subsidiaryRepository.findAllByFamilyId(familyId);
		} else {
			subsidiaries = (List<Subsidiary>) subsidiaryRepository.findAll();
		}
		
		List<Taxpayer> taxpayers = (List<Taxpayer>) taxpayerRepository.findAll();
		
		model.addAttribute("subsidiaries", subsidiaries);
		model.addAttribute("taxpayers", taxpayers);
		
		return "sucursales";
	}
	
	@PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> createSubsidiary(
			@RequestParam String name,
			@RequestParam(required = false) String nickname,
			@RequestParam(required = false) String address,
			@RequestParam Long taxpayerId,
			org.springframework.security.core.Authentication authentication) {
		
		Map<String, Object> response = new HashMap<>();
		
		try {
			// Obtener familyId del usuario autenticado
			String familyId = null;
			if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof com.hp.contaSoft.custom.CurrentUser) {
				com.hp.contaSoft.custom.CurrentUser currentUser = (com.hp.contaSoft.custom.CurrentUser) authentication.getPrincipal();
				familyId = currentUser.getFamilId();
				System.out.println("FamilyId obtenido para sucursal: " + familyId);
			} else {
				response.put("success", false);
				response.put("message", "Usuario no autenticado");
				return ResponseEntity.ok(response);
			}
			
			Taxpayer taxpayer = taxpayerRepository.findById(taxpayerId).orElse(null);
			
		if (taxpayer == null) {
			response.put("success", false);
			response.put("message", "Cliente no encontrado");
			return ResponseEntity.ok(response);
		}
		
		Subsidiary subsidiary = new Subsidiary();
		subsidiary.setName(name);
		subsidiary.setNickname(nickname);
		subsidiary.setAddress(address);
		subsidiary.setTaxpayer(taxpayer);
		subsidiary.setFamilyId(familyId);
		
		subsidiaryRepository.save(subsidiary);
		
		response.put("success", true);
		response.put("message", "Sucursal creada exitosamente");
		response.put("subsidiaryId", subsidiary.getSubsidiaryId());
		
	} catch (Exception e) {
		response.put("success", false);
		response.put("message", "Error al crear sucursal: " + e.getMessage());
		e.printStackTrace();
	}
	
	return ResponseEntity.ok(response);
}	@PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateSubsidiary(
			@RequestParam Long id,
			@RequestParam String name,
			@RequestParam(required = false) String nickname,
			@RequestParam(required = false) String address,
			@RequestParam Long taxpayerId,
			org.springframework.security.core.Authentication authentication) {
		
		Map<String, Object> response = new HashMap<>();
		
		try {
			// Obtener familyId del usuario autenticado
			String familyId = null;
			if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof com.hp.contaSoft.custom.CurrentUser) {
				com.hp.contaSoft.custom.CurrentUser currentUser = (com.hp.contaSoft.custom.CurrentUser) authentication.getPrincipal();
				familyId = currentUser.getFamilId();
			} else {
				response.put("success", false);
				response.put("message", "Usuario no autenticado");
				return ResponseEntity.ok(response);
			}
			
			Subsidiary subsidiary = subsidiaryRepository.findById(id).orElse(null);
			
		if (subsidiary == null) {
			response.put("success", false);
			response.put("message", "Sucursal no encontrada");
			return ResponseEntity.ok(response);
		}
		
		// Verificar que la sucursal pertenezca al mismo familyId
		if (!familyId.equals(subsidiary.getFamilyId())) {
			response.put("success", false);
			response.put("message", "No autorizado para modificar esta sucursal");
			return ResponseEntity.ok(response);
		}
		
		Taxpayer taxpayer = taxpayerRepository.findById(taxpayerId).orElse(null);
		
		if (taxpayer == null) {
			response.put("success", false);
			response.put("message", "Cliente no encontrado");
			return ResponseEntity.ok(response);
		}			subsidiary.setName(name);
			subsidiary.setNickname(nickname);
			subsidiary.setAddress(address);
			subsidiary.setTaxpayer(taxpayer);
			
		subsidiaryRepository.save(subsidiary);
		
		response.put("success", true);
		response.put("message", "Sucursal actualizada exitosamente");
		
	} catch (Exception e) {
		response.put("success", false);
		response.put("message", "Error al actualizar sucursal: " + e.getMessage());
		e.printStackTrace();
	}
	
	return ResponseEntity.ok(response);
}	@PostMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteSubsidiary(@RequestParam Long id, org.springframework.security.core.Authentication authentication) {
		
		Map<String, Object> response = new HashMap<>();
		
		try {
			// Obtener familyId del usuario autenticado
			String familyId = null;
			if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof com.hp.contaSoft.custom.CurrentUser) {
				com.hp.contaSoft.custom.CurrentUser currentUser = (com.hp.contaSoft.custom.CurrentUser) authentication.getPrincipal();
				familyId = currentUser.getFamilId();
			} else {
				response.put("success", false);
				response.put("message", "Usuario no autenticado");
				return ResponseEntity.ok(response);
			}
			
			Subsidiary subsidiary = subsidiaryRepository.findById(id).orElse(null);
			if (subsidiary == null) {
				response.put("success", false);
				response.put("message", "Sucursal no encontrada");
				return ResponseEntity.ok(response);
			}
			
			// Verificar que la sucursal pertenezca al mismo familyId
			if (!familyId.equals(subsidiary.getFamilyId())) {
				response.put("success", false);
				response.put("message", "No autorizado para eliminar esta sucursal");
				return ResponseEntity.ok(response);
			}
			
			subsidiaryRepository.deleteById(id);
			
			response.put("success", true);
			response.put("message", "Sucursal eliminada exitosamente");
			
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "Error al eliminar sucursal: " + e.getMessage());
			e.printStackTrace();
		}
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/byTaxpayer")
	@ResponseBody
	public List<Subsidiary> getSubsidiariesByTaxpayer(@RequestParam Long taxpayerId) {
		return subsidiaryRepository.findByTaxpayerId(taxpayerId);
	}
}
