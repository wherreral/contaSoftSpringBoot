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

	@Autowired
	private com.hp.contaSoft.service.ReferenceDataCache referenceDataCache;

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
				afpFactorsrepository.save(new AFPFactors("MODELO",10.58d));
			}

			// Siempre recargar IUT con valores actualizados (Febrero 2026 - SII)
			iUTRepository.deleteAll();
			System.out.println(">>> Cargando datos de IUT Febrero 2026...");
			// MENSUAL (fuente: https://www.sii.cl/valores_y_fechas/impuesto_2da_categoria/impuesto2026.htm)
			iUTRepository.save(new IUT("MENSUAL", 0d, 939748.50d, 0d, 0d, 0d));
			iUTRepository.save(new IUT("MENSUAL", 939748.51d, 2088330.00d, 0.04d, 37589.94d, 0d));
			iUTRepository.save(new IUT("MENSUAL", 2088330.01d, 3480550.00d, 0.08d, 121356.14d, 0d));
			iUTRepository.save(new IUT("MENSUAL", 3480550.01d, 4872770.00d, 0.135d, 313286.89d, 0d));
			iUTRepository.save(new IUT("MENSUAL", 4872770.01d, 6264990.00d, 0.23d, 776549.54d, 0d));
			iUTRepository.save(new IUT("MENSUAL", 6264990.01d, 8353320.00d, 0.304d, 1239879.08d, 0d));
			iUTRepository.save(new IUT("MENSUAL", 8353320.01d, 21579410.00d, 0.35d, 1624106.36d, 0d));
			iUTRepository.save(new IUT("MENSUAL", 21579410.01d, 999999999.99d, 0.4d, 2703076.86d, 0d));
			// QUINCENAL
			iUTRepository.save(new IUT("QUINCENAL", 0d, 469874.25d, 0d, 0d, 0d));
			iUTRepository.save(new IUT("QUINCENAL", 469874.26d, 1044165.00d, 0.04d, 18794.97d, 0d));
			iUTRepository.save(new IUT("QUINCENAL", 1044165.01d, 1740275.00d, 0.08d, 60678.07d, 0d));
			iUTRepository.save(new IUT("QUINCENAL", 1740275.01d, 2436385.00d, 0.135d, 156643.45d, 0d));
			iUTRepository.save(new IUT("QUINCENAL", 2436385.01d, 3132495.00d, 0.23d, 388274.77d, 0d));
			iUTRepository.save(new IUT("QUINCENAL", 3132495.01d, 4176660.00d, 0.304d, 619939.54d, 0d));
			iUTRepository.save(new IUT("QUINCENAL", 4176660.01d, 10789705.00d, 0.35d, 812053.18d, 0d));
			iUTRepository.save(new IUT("QUINCENAL", 10789705.01d, 999999999.99d, 0.4d, 1351538.43d, 0d));

			if (((java.util.Collection<?>) templateDetailsRepository.findAll()).size() < 19) {
				System.out.println(">>> Cargando datos iniciales de Template Definitions...");
				// Campos obligatorios
				templateDetailsRepository.save(new TemplateDefiniton("RUT","Rut del empleado",true, true));
				templateDetailsRepository.save(new TemplateDefiniton("CENTRO_COSTO","Alias de la sucursal",true, true));
				templateDetailsRepository.save(new TemplateDefiniton("SUELDO_BASE","Sueldo base del empleado",true, true));
				templateDetailsRepository.save(new TemplateDefiniton("DT","Dias trabajados por el empleados en la sucursal",true, true));
				templateDetailsRepository.save(new TemplateDefiniton("PREVISION","Nombre de la organizacion previsional afiliada al empleado",true, true));
				templateDetailsRepository.save(new TemplateDefiniton("SALUD","Nombre de la organizacion de salud afiliada al empleado",true, true));
				templateDetailsRepository.save(new TemplateDefiniton("SALUD_PORCENTAJE","% que el empleado aporta a la organizacion de salud",true, true));
				// Campos opcionales
				templateDetailsRepository.save(new TemplateDefiniton("BONO","Bono de produccion del empleado",true, false));
				templateDetailsRepository.save(new TemplateDefiniton("HORAS_EXTRA","Horas extra trabajadas",true, false));
				templateDetailsRepository.save(new TemplateDefiniton("ASIG_FAMILIAR","Numero de cargas familiares",true, false));
				templateDetailsRepository.save(new TemplateDefiniton("MOVILIZACION","Asignacion de movilizacion",true, false));
				templateDetailsRepository.save(new TemplateDefiniton("COLACION","Asignacion de colacion",true, false));
				templateDetailsRepository.save(new TemplateDefiniton("DESGASTE","Desgaste de herramientas",true, false));
				templateDetailsRepository.save(new TemplateDefiniton("AFC","Porcentaje de seguro de cesantia",true, false));
				templateDetailsRepository.save(new TemplateDefiniton("ALCANCE_LIQUIDO","Alcance liquido objetivo del empleado",true, false));
				templateDetailsRepository.save(new TemplateDefiniton("DESC_APV_CTA_AH","Descuento APV Cuenta de Ahorro",true, false));
				templateDetailsRepository.save(new TemplateDefiniton("DESC_PTMO_CCAAFF","Descuento Préstamo CC.AA.FF",true, false));
				templateDetailsRepository.save(new TemplateDefiniton("DESC_PTMO_SOLIDARIO","Descuento Préstamo Solidario",true, false));
				templateDetailsRepository.save(new TemplateDefiniton("REGIMEN","Tipo de contrato: INDEFINIDO o PLAZO_FIJO",true, false));
			}


			/**Initial load*/

			//Crear subsidiary
		

		//Create ADMIN USER only if doesn't exist (ensure ADMIN role)
		AppUser existingUser = userRepository.findFirstByUsername("willy");
		GroupCredentials gc;

		if (existingUser == null) {
			System.out.println(">>> Creando usuario 'willy' inicial...");
			AppUser user = new AppUser("willy","willy");

			//1. Create family credentials
			gc = new GroupCredentials("name","type",UUID.randomUUID().toString());
			groupCredentialsRepository.save(gc);

			//2.Set Role -> 1 means ROLE_ADMIN (see UserDetailsServiceImpl.getRoles)
			user.setRole(new Role(user,1));

			//3. Persist User
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			user.setGroupCredentials(gc);
			userRepository.save(user);
			System.out.println(">>> Usuario 'willy' creado exitosamente con familyId: " + gc.getGcId());
		} else {
			System.out.println(">>> Usuario 'willy' ya existe, comprobando rol. FamilyId: "
					+ existingUser.getGroupCredentials().getGcId());
			gc = existingUser.getGroupCredentials();
			// Ensure the existing user has admin role (role value 1)
			try {
				if (existingUser.getRole() == null) {
					existingUser.setRole(new Role(existingUser, 1));
					userRepository.save(existingUser);
					System.out.println(">>> Usuario 'willy' actualizado: se asignó rol ADMIN");
				} else if (existingUser.getRole().getRole() == null || existingUser.getRole().getRole().intValue() != 1) {
					existingUser.getRole().setRole(1);
					userRepository.save(existingUser);
					System.out.println(">>> Usuario 'willy' actualizado: rol cambiado a ADMIN");
				} else {
					System.out.println(">>> Usuario 'willy' ya tiene rol ADMIN");
				}
			} catch (Exception ex) {
				System.out.println(">>> Error comprobando/actualizando rol de 'willy': " + ex.getMessage());
			}
		}

		//Create AppUser only if doesn't exist
		existingUser = userRepository.findFirstByUsername("w");
		//gc;

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
				Subsidiary subsidiary = new Subsidiary("CHICAUMA 9.1");
				subsidiary.setFamilyId(familyId);
				Taxpayer tp3 = new Taxpayer("Copec SA", "15961705-3", add2, familyId, subsidiary);
				//Taxpayer tp3 = new Taxpayer("Copec SA","15961705-3", add2, familyId);
				taxpayerRepository.save(tp3);
				System.out.println(">>> Taxpayer 15961705-3 creado");
			} else {
				System.out.println(">>> Taxpayer 15961705-3 ya existe, omitiendo");
			}

			// Recargar cache con los datos seed recién insertados
			referenceDataCache.reload();

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
