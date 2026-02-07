package com.hp.contaSoft.spring.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hp.contaSoft.custom.CurrentUser;
import com.hp.contaSoft.hibernate.dao.repositories.UserRepository;
import com.hp.contaSoft.hibernate.entities.AppUser;

@RestController
@RequestMapping("/api/user")
public class UserManagementController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getUserProfile(Authentication authentication) {
        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        AppUser user = userRepository.findFirstByUsername(currentUser.getUsername());
        
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("username", user.getUsername());
        response.put("name", user.getName());
        response.put("phone", user.getPhone());
        response.put("familyId", user.getGroupCredentials().getGcId());
        response.put("role", user.getRole().getRole());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/family-users")
    public ResponseEntity<List<Map<String, Object>>> getFamilyUsers(Authentication authentication) {
        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        String familyId = currentUser.getFamilId();
        
        List<AppUser> allUsers = (List<AppUser>) userRepository.findAll();
        List<Map<String, Object>> familyUsers = allUsers.stream()
            .filter(user -> user.getGroupCredentials() != null && 
                          familyId.equals(user.getGroupCredentials().getGcId()))
            .map(user -> {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("username", user.getUsername());
                userMap.put("name", user.getName());
                userMap.put("phone", user.getPhone());
                userMap.put("role", user.getRole().getRole());
                return userMap;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(familyUsers);
    }
    
    @PutMapping("/update-profile")
    public ResponseEntity<Map<String, String>> updateProfile(
            @RequestBody Map<String, String> updates,
            Authentication authentication) {
        
        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        AppUser user = userRepository.findFirstByUsername(currentUser.getUsername());
        
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
        if (updates.containsKey("name")) {
            user.setName(updates.get("name"));
        }
        
        if (updates.containsKey("phone")) {
            user.setPhone(updates.get("phone"));
        }
        
        userRepository.save(user);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Perfil actualizado exitosamente");
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestBody Map<String, String> passwordData,
            Authentication authentication) {
        
        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        AppUser user = userRepository.findFirstByUsername(currentUser.getUsername());
        
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
        String currentPassword = passwordData.get("currentPassword");
        String newPassword = passwordData.get("newPassword");
        
        if (!bCryptPasswordEncoder.matches(currentPassword, user.getPassword())) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Contraseña actual incorrecta");
            return ResponseEntity.badRequest().body(response);
        }
        
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Contraseña cambiada exitosamente");
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout exitoso");
        return ResponseEntity.ok(response);
    }
}
