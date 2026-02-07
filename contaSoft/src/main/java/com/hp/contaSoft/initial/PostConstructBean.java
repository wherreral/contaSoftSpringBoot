package com.hp.contaSoft.initial;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.hp.contaSoft.constant.Meses;
import com.hp.contaSoft.hibernate.dao.repositories.AFPFactorsRepository;
import com.hp.contaSoft.hibernate.dao.repositories.GroupCredentialsRepository;
import com.hp.contaSoft.hibernate.dao.repositories.HealthFactorsRepository;
import com.hp.contaSoft.hibernate.dao.repositories.IUTRepository;
import com.hp.contaSoft.hibernate.dao.repositories.TaxpayerRepository;
import com.hp.contaSoft.hibernate.dao.repositories.TemplateDetailsRepository;
import com.hp.contaSoft.hibernate.dao.repositories.UserRepository;
import com.hp.contaSoft.hibernate.entities.AFPFactors;
import com.hp.contaSoft.hibernate.entities.Address;
import com.hp.contaSoft.hibernate.entities.AppUser;
import com.hp.contaSoft.hibernate.entities.GroupCredentials;
import com.hp.contaSoft.hibernate.entities.HealthFactors;
import com.hp.contaSoft.hibernate.entities.Taxpayer;
import com.hp.contaSoft.hibernate.entities.Template;
import com.hp.contaSoft.hibernate.entities.TemplateDefiniton;
import com.hp.contaSoft.hibernate.entities.IUT;
import com.hp.contaSoft.hibernate.entities.Role;
import com.hp.contaSoft.hibernate.entities.Subsidiary;

@Component
public class PostConstructBean implements ApplicationListener<ContextRefreshedEvent>{

	/*public static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("A");
	public static final EntityManager manager = emf.createEntityManager();
*/
	
	public static final EntityManagerFactory emf = null;
	public static final EntityManager manager = null;
	
	@Autowired HealthFactorsRepository healthFactorsRepository;
	@Autowired AFPFactorsRepository afpFactorsrepository;
	@Autowired IUTRepository iUTRepository;
	@Autowired GroupCredentialsRepository groupCredentialsRepository;
	
	@Autowired TaxpayerRepository taxpayerRepository;
	@Autowired TemplateDetailsRepository templateDetailsRepository;
	
	@Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired UserRepository userRepository;
	
	@Autowired(required = false)
	private AutoTestRunner autoTestRunner;
	
	//Map with templates
	public static final Map<String,Map<String,String>> taxpayerTemplates;
	public static Map<String,String> uniqueTemplates = new HashMap<>();
	
    
    
	static {
		//Initialize Template Map
		uniqueTemplates.put("RENTA",
				"RUT;CENTRO_COSTO;SUELDO_BASE;DT;PREVISION;SALUD;SALUD_PORCENTAJE;BONO;HORAS_EXTRA;ASIG_FAMILIAR;MOVILIZACION;COLACION;DESGASTE;ALCANCE_LIQUIDO");
		taxpayerTemplates = new HashMap<String,Map<String,String>>();
		taxpayerTemplates.put("15961703-3", uniqueTemplates);
		taxpayerTemplates.put("15961705-3", uniqueTemplates);
		
		
	}
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent arg0) {
		
		System.out.println("Start H2 DataBase ");
		
		try {
			
			// Only insert seed data if tables are empty (prevent duplicates on multiple ContextRefreshedEvent)
			if (((java.util.Collection<?>) healthFactorsRepository.findAll()).size() == 0) {
				System.out.println(">>> Cargando datos iniciales de Health Factors...");
				healthFactorsRepository.save(new HealthFactors("FONASA", 7d));
				healthFactorsRepository.save(new HealthFactors("Banmédica", 7.0));
				healthFactorsRepository.save(new HealthFactors("Colmena", 7.0));
				healthFactorsRepository.save(new HealthFactors("Consalud", 7.0));
				healthFactorsRepository.save(new HealthFactors("Nueva Masvida", 7.0));
				healthFactorsRepository.save(new HealthFactors("Vida Tres", 7.0));
				healthFactorsRepository.save(new HealthFactors("Cruz Blanca", 7.0));
			}
			
			if (((java.util.Collection<?>) afpFactorsrepository.findAll()).size() == 0) {
				System.out.println(">>> Cargando datos iniciales de AFP Factors...");
				afpFactorsrepository.save(new AFPFactors("CAPITAL",11.44d));
				afpFactorsrepository.save(new AFPFactors("CUPRUM",11.48d));
				afpFactorsrepository.save(new AFPFactors("HABITAT",11.27d));
				afpFactorsrepository.save(new AFPFactors("PLANVITAL",10.41d));
				afpFactorsrepository.save(new AFPFactors("PROVIDA",11.45d));
				afpFactorsrepository.save(new AFPFactors("MODELO",10.77d));
			}
	
			if (((java.util.Collection<?>) iUTRepository.findAll()).size() == 0) {
				System.out.println(">>> Cargando datos iniciales de IUT...");
				iUTRepository.save(new IUT("MENSUAL", 0d, 644341.50d, 0d,0d,0d));
				iUTRepository.save(new IUT("MENSUAL", 644341.51d, 1431870.00d, 0.04d, 25773.66d, 2.20d));
				iUTRepository.save(new IUT("MENSUAL", 1431870.01d, 2386450.00d, 0.08d,  83048.46d, 4.52d));
				iUTRepository.save(new IUT("MENSUAL", 2386450.01d, 3341030.00d, 0.135d,  214303.21d, 7.09d));
				iUTRepository.save(new IUT("MENSUAL", 3341030.01d, 6341030.00d, 0.135d,  214303.21d, 7.09d));
			}
			
			if (((java.util.Collection<?>) templateDetailsRepository.findAll()).size() == 0) {
				System.out.println(">>> Cargando datos iniciales de Template Definitions...");
				templateDetailsRepository.save(new TemplateDefiniton("RUT","Rut del empleado",true, true));
				templateDetailsRepository.save(new TemplateDefiniton("CENTRO_COSTO","Alias de la sucursal",true, true));
				templateDetailsRepository.save(new TemplateDefiniton("SUELDO_BASE","Sueldo base del empleado",true, true));
				templateDetailsRepository.save(new TemplateDefiniton("DT","Dias trabajados por el empleados en la sucursal",true, true));
				templateDetailsRepository.save(new TemplateDefiniton("PREVISION","Nombre de la organizacion previsional afiliada al empleado",true, true));
				templateDetailsRepository.save(new TemplateDefiniton("SALUD","Nombre de la organizacion de salud afiliada al empleado",true, true));
				templateDetailsRepository.save(new TemplateDefiniton("SALUD_PORCENTAJE","% que el empleado aporta a la organizacion de salud",true, true));
			}
			
			
		/**Initial load*/
		
		//Create AppUser only if doesn't exist
		AppUser existingUser = userRepository.findFirstByUsername("w");
		GroupCredentials gc;
		
		if (existingUser == null) {
			System.out.println(">>> Creando usuario 'w' inicial...");
			AppUser user = new AppUser("w","w");
			
			//1. Create family credentials
			gc = new GroupCredentials("name","type",UUID.randomUUID().toString());
			groupCredentialsRepository.save(gc);
			
			//2.Set Role
			user.setRole(new Role(user,1));
			
			//3. Persist User
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			user.setGroupCredentials(gc);
			userRepository.save(user);
			System.out.println(">>> Usuario 'w' creado exitosamente con familyId: " + gc.getGcId());
		} else {
			System.out.println(">>> Usuario 'w' ya existe, omitiendo creación. FamilyId: "
					+ existingUser.getGroupCredentials().getGcId());
			gc = existingUser.getGroupCredentials();
		}
		
		//Create AppUser only if doesn't exist
		existingUser = userRepository.findFirstByUsername("z");
		//GroupCredentials gc;
		
		if (existingUser == null) {
			System.out.println(">>> Creando usuario 'z' inicial...");
			AppUser user = new AppUser("z","z");
			
			//1. Create family credentials
			gc = new GroupCredentials("name","type",UUID.randomUUID().toString());
			groupCredentialsRepository.save(gc);
			
			//2.Set Role
			user.setRole(new Role(user,1));
			
			//3. Persist User
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			user.setGroupCredentials(gc);
			userRepository.save(user);
			System.out.println(">>> Usuario 'z' creado exitosamente con familyId: " + gc.getGcId());
		} else {
			System.out.println(">>> Usuario 'z' ya existe, omitiendo creación. FamilyId: " + existingUser.getGroupCredentials().getGcId());
			gc = existingUser.getGroupCredentials();
		}
			//New Address
			Address address = new Address("Tu Casa","4");
			Address add = new Address("calle1", "2");
			Address add2 = new Address("Mi Casa","3");
			
			//NewTaxpayer - only create if not exists (rut + familyId as key)
			String familyId = gc.getGcId();
			
			if (taxpayerRepository.findByRutAndFamilyId("15961703-3", familyId) == null) {
				Taxpayer tp = new Taxpayer("Williams SA","15961703-3", address, new Subsidiary("SANTA OLGA"));
				tp.setFamilyId(familyId);
				taxpayerRepository.save(tp);
				System.out.println(">>> Taxpayer 15961703-3 creado");
			} else {
				System.out.println(">>> Taxpayer 15961703-3 ya existe, omitiendo");
			}
			
			if (taxpayerRepository.findByRutAndFamilyId("15961704-3", familyId) == null) {
				Taxpayer tp2 = new Taxpayer("Marco SA","15961704-3", add, familyId);
				taxpayerRepository.save(tp2);
				System.out.println(">>> Taxpayer 15961704-3 creado");
			} else {
				System.out.println(">>> Taxpayer 15961704-3 ya existe, omitiendo");
			}
			
			if (taxpayerRepository.findByRutAndFamilyId("15961705-3", familyId) == null) {
				Taxpayer tp3 = new Taxpayer("Copec SA","15961705-3", add2, familyId);
				taxpayerRepository.save(tp3);
				System.out.println(">>> Taxpayer 15961705-3 creado");
			} else {
				System.out.println(">>> Taxpayer 15961705-3 ya existe, omitiendo");
			}
			
			// Ejecutar AutoTest si está habilitado
			if (autoTestRunner != null) {
				System.out.println("\n>>> Ejecutando AutoTest después de PostConstructBean...\n");
				try {
					autoTestRunner.runTest();
				} catch (Exception e) {
					System.out.println("Error ejecutando AutoTest: " + e.getMessage());
					e.printStackTrace();
				}
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	
		
		
		//emf = Persistence.createEntityManagerFactory("A");
		//manager = emf.createEntityManager();
		
	}

	
	
	
}
