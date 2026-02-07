package com.hp.contaSoft.rest.api.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.hp.contaSoft.custom.CurrentUser;
import com.hp.contaSoft.hibernate.dao.repositories.TaxpayerRepository;
import com.hp.contaSoft.hibernate.entities.Address;
import com.hp.contaSoft.hibernate.entities.Taxpayer;

@RestController
@RequestMapping(value = "/api/ui/clientes", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")
public class ClientRestController {

    private static final Logger logger = LoggerFactory.getLogger(ClientRestController.class);

    @Autowired
    private TaxpayerRepository taxpayerRepository;
    
    public ClientRestController() {
        logger.info("=== ClientRestController INITIALIZED ===");
        System.out.println("=== ClientRestController INITIALIZED ===");
    }

    /**
     * Obtener todos los clientes (filtrado por familyId)
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllClients(Authentication authentication) {
        try {
            logger.info("=== getAllClients called ===");
            
            // Si no hay autenticación, devolver lista vacía
            if (authentication == null || authentication.getPrincipal() == null) {
                logger.warn("getAllClients - Intento de acceso sin autenticación");
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", new java.util.ArrayList<>());
                response.put("total", 0);
                return ResponseEntity.ok(response);
            }
            
            // Validar que el principal sea de tipo CurrentUser
            if (!(authentication.getPrincipal() instanceof CurrentUser)) {
                logger.warn("getAllClients - Principal no es CurrentUser: {}", 
                    authentication.getPrincipal().getClass().getName());
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", new java.util.ArrayList<>());
                response.put("total", 0);
                return ResponseEntity.ok(response);
            }
            
            CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
            String familyId = currentUser.getFamilId();
            
            logger.info("getAllClients - Usuario: {}, FamilyId: {}", currentUser.getUsername(), familyId);
            
            if (familyId == null) {
                logger.warn("getAllClients - familyId es null para usuario: {}", currentUser.getUsername());
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", new java.util.ArrayList<>());
                response.put("total", 0);
                return ResponseEntity.ok(response);
            }
            
            logger.info("Obteniendo lista de clientes para familyId: {}", familyId);
            List<Taxpayer> taxpayers = taxpayerRepository.findByFamilyId(familyId);
            logger.info("Encontrados {} clientes para familyId: {}", taxpayers.size(), familyId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", taxpayers);
            response.put("total", taxpayers.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al obtener clientes: ", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al obtener los clientes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Obtener un cliente por ID (validando familyId)
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getClientById(@PathVariable Long id, Authentication authentication) {
        try {
            if (authentication == null || authentication.getPrincipal() == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Usuario no autenticado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            
            CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
            String familyId = currentUser.getFamilId();
            
            logger.info("=== API REST: Obteniendo cliente con ID: {} para familyId: {} ===", id, familyId);
            
            Taxpayer taxpayer = taxpayerRepository.findById(id).orElse(null);
            
            if (taxpayer == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Cliente no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            // Validar que el cliente pertenece al familyId del usuario
            if (!familyId.equals(taxpayer.getFamilyId())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Acceso denegado");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", taxpayer);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al obtener cliente: ", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al obtener el cliente: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Crear un nuevo cliente (con familyId)
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createClient(@RequestBody ClientDTO clientDTO, Authentication authentication) {
        try {
            if (authentication == null || authentication.getPrincipal() == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Usuario no autenticado. Por favor inicie sesión.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            
            CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
            String familyId = currentUser.getFamilId();
            
            logger.info("Creando nuevo cliente: {} para familyId: {}", clientDTO.getRazonSocial(), familyId);
            
            // Validar que no exista un cliente con el mismo RUT en la misma familia
            Taxpayer existingTaxpayer = taxpayerRepository.findByRutAndFamilyId(clientDTO.getRutCliente(), familyId);
            if (existingTaxpayer != null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Ya existe un cliente con ese RUT en tu organización");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
            // Crear el nuevo cliente (Taxpayer)
            Taxpayer taxpayer = new Taxpayer();
            taxpayer.setName(clientDTO.getRazonSocial());
            taxpayer.setRut(clientDTO.getRutCliente());
            taxpayer.setFamilyId(familyId); // ASIGNAR FAMILY ID
            
            // Si hay datos del representante legal
            if (clientDTO.getNombreRepresentante() != null && clientDTO.getRutRepresentante() != null) {
                taxpayer.setLastname(clientDTO.getNombreRepresentante() + " - RUT: " + clientDTO.getRutRepresentante());
            }
            
            // Crear la dirección
            if (clientDTO.getDireccion() != null) {
                Address address = new Address();
                address.setName(clientDTO.getDireccion().getCalle());
                address.setNumber(clientDTO.getDireccion().getNumero());
                address.setProvince(clientDTO.getDireccion().getRegion());
                address.setComuna(clientDTO.getDireccion().getComuna());
                address.setTaxpayer(taxpayer);
                taxpayer.getAddress().add(address);
            }
            
            // Guardar el cliente
            Taxpayer savedTaxpayer = taxpayerRepository.save(taxpayer);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cliente creado exitosamente");
            response.put("data", savedTaxpayer);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Error al crear cliente: ", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al crear el cliente: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Actualizar un cliente existente (validando familyId)
     */
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateClient(@PathVariable Long id, @RequestBody ClientDTO clientDTO, Authentication authentication) {
        try {
            if (authentication == null || authentication.getPrincipal() == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Usuario no autenticado. Por favor inicie sesión.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            
            CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
            String familyId = currentUser.getFamilId();
            
            logger.info("Actualizando cliente con ID: {} para familyId: {}", id, familyId);
            
            Taxpayer taxpayer = taxpayerRepository.findById(id).orElse(null);
            if (taxpayer == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Cliente no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            // Validar que el cliente pertenece al familyId del usuario
            if (!familyId.equals(taxpayer.getFamilyId())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Acceso denegado");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }
            
            // Actualizar datos del cliente
            taxpayer.setName(clientDTO.getRazonSocial());
            taxpayer.setRut(clientDTO.getRutCliente());
            
            // Actualizar datos del representante legal
            if (clientDTO.getNombreRepresentante() != null && clientDTO.getRutRepresentante() != null) {
                taxpayer.setLastname(clientDTO.getNombreRepresentante() + " - RUT: " + clientDTO.getRutRepresentante());
            }
            
            // Actualizar dirección
            if (clientDTO.getDireccion() != null) {
                if (taxpayer.getAddress().isEmpty()) {
                    // Crear nueva dirección si no existe
                    Address address = new Address();
                    address.setName(clientDTO.getDireccion().getCalle());
                    address.setNumber(clientDTO.getDireccion().getNumero());
                    address.setProvince(clientDTO.getDireccion().getRegion());
                    address.setComuna(clientDTO.getDireccion().getComuna());
                    address.setTaxpayer(taxpayer);
                    taxpayer.getAddress().add(address);
                } else {
                    // Actualizar dirección existente
                    Address address = taxpayer.getAddress().get(0);
                    address.setName(clientDTO.getDireccion().getCalle());
                    address.setNumber(clientDTO.getDireccion().getNumero());
                    address.setProvince(clientDTO.getDireccion().getRegion());
                    address.setComuna(clientDTO.getDireccion().getComuna());
                }
            }
            
            // Guardar cambios
            Taxpayer updatedTaxpayer = taxpayerRepository.save(taxpayer);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cliente actualizado exitosamente");
            response.put("data", updatedTaxpayer);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al actualizar cliente: ", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al actualizar el cliente: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * DTO para recibir datos del cliente desde el frontend
     */
    public static class ClientDTO {
        private String razonSocial;
        private String rutCliente;
        private DireccionDTO direccion;
        private String rutRepresentante;
        private String nombreRepresentante;

        // Getters y Setters
        public String getRazonSocial() {
            return razonSocial;
        }

        public void setRazonSocial(String razonSocial) {
            this.razonSocial = razonSocial;
        }

        public String getRutCliente() {
            return rutCliente;
        }

        public void setRutCliente(String rutCliente) {
            this.rutCliente = rutCliente;
        }

        public DireccionDTO getDireccion() {
            return direccion;
        }

        public void setDireccion(DireccionDTO direccion) {
            this.direccion = direccion;
        }

        public String getRutRepresentante() {
            return rutRepresentante;
        }

        public void setRutRepresentante(String rutRepresentante) {
            this.rutRepresentante = rutRepresentante;
        }

        public String getNombreRepresentante() {
            return nombreRepresentante;
        }

        public void setNombreRepresentante(String nombreRepresentante) {
            this.nombreRepresentante = nombreRepresentante;
        }
    }

    /**
     * DTO para dirección
     */
    public static class DireccionDTO {
        private String region;
        private String comuna;
        private String calle;
        private String numero;

        // Getters y Setters
        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getComuna() {
            return comuna;
        }

        public void setComuna(String comuna) {
            this.comuna = comuna;
        }

        public String getCalle() {
            return calle;
        }

        public void setCalle(String calle) {
            this.calle = calle;
        }

        public String getNumero() {
            return numero;
        }

        public void setNumero(String numero) {
            this.numero = numero;
        }
    }
}
