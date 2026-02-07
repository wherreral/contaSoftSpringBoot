package com.hp.contaSoft.rest.api.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.hp.contaSoft.custom.CurrentUser;
import com.hp.contaSoft.hibernate.dao.repositories.AFPFactorsRepository;
import com.hp.contaSoft.hibernate.dao.repositories.AfpFactorNicknameRepository;
import com.hp.contaSoft.hibernate.entities.AFPFactors;
import com.hp.contaSoft.hibernate.entities.AfpFactorNickname;

@RestController
@RequestMapping(value = "/api/ui/afp", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")
public class AFPRestController {

    private static final Logger logger = LoggerFactory.getLogger(AFPRestController.class);

    @Autowired
    private AFPFactorsRepository afpFactorsRepository;

    @Autowired
    private AfpFactorNicknameRepository afpFactorNicknameRepository;
    
    public AFPRestController() {
        logger.info("=== AFPRestController INITIALIZED ===");
    }

    /**
     * Helper: extrae familyId del Authentication
     */
    private String getFamilyId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CurrentUser)) {
            return null;
        }
        return ((CurrentUser) authentication.getPrincipal()).getFamilId();
    }

    /**
     * Obtener todas las AFP con nicknames del tenant actual
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllAFP(Authentication authentication) {
        try {
            logger.info("Obteniendo lista de todas las AFP");
            List<AFPFactors> afpList = (List<AFPFactors>) afpFactorsRepository.findAll();
            
            // Obtener nicknames del tenant si hay auth
            String familyId = getFamilyId(authentication);
            Map<Long, String> nicknameMap = new HashMap<>();
            if (familyId != null) {
                List<AfpFactorNickname> nicknames = afpFactorNicknameRepository.findByFamilyId(familyId);
                for (AfpFactorNickname n : nicknames) {
                    nicknameMap.put(n.getAfpFactorId(), n.getNickname());
                }
            }

            // Construir respuesta con nickname del tenant
            List<Map<String, Object>> dataList = new ArrayList<>();
            for (AFPFactors afp : afpList) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", afp.getId());
                item.put("name", afp.getName());
                item.put("percentaje", afp.getPercentaje());
                // Prioridad: nickname del tenant (se elimina el nickname global)
                String tenantNickname = nicknameMap.get(afp.getId());
                item.put("nickname", tenantNickname);
                item.put("tenantNickname", tenantNickname);
                dataList.add(item);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", dataList);
            response.put("total", dataList.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al obtener AFP: ", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al obtener las AFP: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Obtener una AFP por ID
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getAFPById(@PathVariable Long id) {
        try {
            logger.info("Obteniendo AFP con ID: {}", id);
            
            AFPFactors afp = afpFactorsRepository.findById(id).orElse(null);
            
            if (afp == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "AFP no encontrada");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", afp);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al obtener AFP: ", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al obtener la AFP: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Crear una nueva AFP
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createAFP(@RequestBody AFPDTO afpDTO) {
        try {
            logger.info("Creando nueva AFP: {}", afpDTO.getName());
            
            // Validar que no exista una AFP con el mismo nombre
            AFPFactors existingAFP = afpFactorsRepository.findByName(afpDTO.getName());
            if (existingAFP != null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Ya existe una AFP con ese nombre");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
            // Crear la nueva AFP
            AFPFactors afp = new AFPFactors();
            afp.setName(afpDTO.getName());
            afp.setPercentaje(afpDTO.getPercentaje());
            
            // Guardar la AFP
            AFPFactors savedAFP = afpFactorsRepository.save(afp);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "AFP creada exitosamente");
            response.put("data", savedAFP);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Error al crear AFP: ", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al crear la AFP: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Actualizar una AFP existente
     */
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateAFP(@PathVariable Long id, @RequestBody AFPDTO afpDTO) {
        try {
            logger.info("Actualizando AFP con ID: {}", id);
            
            AFPFactors afp = afpFactorsRepository.findById(id).orElse(null);
            if (afp == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "AFP no encontrada");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            // Validar que no exista otra AFP con el mismo nombre
            AFPFactors existingAFP = afpFactorsRepository.findByName(afpDTO.getName());
            if (existingAFP != null && existingAFP.getId() != id) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Ya existe otra AFP con ese nombre");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
            // Actualizar datos de la AFP
            afp.setName(afpDTO.getName());
            afp.setPercentaje(afpDTO.getPercentaje());
            
            // Guardar cambios
            AFPFactors updatedAFP = afpFactorsRepository.save(afp);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "AFP actualizada exitosamente");
            response.put("data", updatedAFP);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al actualizar AFP: ", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al actualizar la AFP: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Eliminar una AFP
     */
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteAFP(@PathVariable Long id) {
        try {
            logger.info("Eliminando AFP con ID: {}", id);
            
            AFPFactors afp = afpFactorsRepository.findById(id).orElse(null);
            if (afp == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "AFP no encontrada");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            // Eliminar la AFP
            afpFactorsRepository.delete(afp);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "AFP eliminada exitosamente");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al eliminar AFP: ", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al eliminar la AFP: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * DTO para recibir datos de AFP desde el frontend
     */
    public static class AFPDTO {
        private String name;
        private double percentaje;

        // Getters y Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getPercentaje() {
            return percentaje;
        }

        public void setPercentaje(double percentaje) {
            this.percentaje = percentaje;
        }
    }

    // ==================== NICKNAME POR TENANT ====================

    /**
     * Guardar o actualizar el nickname de una AFP para el tenant actual
     */
    @PutMapping(value = "/{afpId}/nickname", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveNickname(@PathVariable Long afpId,
                                          @RequestBody NicknameDTO nicknameDTO,
                                          Authentication authentication) {
        try {
            String familyId = getFamilyId(authentication);
            if (familyId == null) {
                Map<String, Object> err = new HashMap<>();
                err.put("success", false);
                err.put("message", "No autenticado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
            }

            // Validar que la AFP exista
            AFPFactors afp = afpFactorsRepository.findById(afpId).orElse(null);
            if (afp == null) {
                Map<String, Object> err = new HashMap<>();
                err.put("success", false);
                err.put("message", "AFP no encontrada");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
            }

            // Buscar nickname existente para este tenant + afp
            AfpFactorNickname existing = afpFactorNicknameRepository.findByAfpFactorIdAndFamilyId(afpId, familyId);

            if (nicknameDTO.getNickname() == null || nicknameDTO.getNickname().trim().isEmpty()) {
                // Si viene vacío, eliminar el nickname si existía
                if (existing != null) {
                    afpFactorNicknameRepository.delete(existing);
                }
            } else {
                if (existing != null) {
                    existing.setNickname(nicknameDTO.getNickname().trim());
                    afpFactorNicknameRepository.save(existing);
                } else {
                    AfpFactorNickname nn = new AfpFactorNickname(afpId, familyId, nicknameDTO.getNickname().trim());
                    afpFactorNicknameRepository.save(nn);
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Nickname actualizado exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al guardar nickname: ", e);
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "Error al guardar nickname: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    /**
     * Obtener todos los nicknames del tenant actual
     */
    @GetMapping(value = "/nicknames", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getNicknames(Authentication authentication) {
        try {
            String familyId = getFamilyId(authentication);
            if (familyId == null) {
                Map<String, Object> err = new HashMap<>();
                err.put("success", false);
                err.put("message", "No autenticado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
            }

            List<AfpFactorNickname> nicknames = afpFactorNicknameRepository.findByFamilyId(familyId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", nicknames);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al obtener nicknames: ", e);
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "Error al obtener nicknames: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    /**
     * DTO para recibir nickname
     */
    public static class NicknameDTO {
        private String nickname;

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
    }
}
