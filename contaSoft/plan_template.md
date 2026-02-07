# Plan: Gestión de Templates para CSV de Liquidaciones

## Resumen Ejecutivo

Crear una interfaz completa de gestión de templates que permita a cada Taxpayer (empresa cliente) definir y administrar múltiples configuraciones de mapeo de cabeceras CSV para importar liquidaciones de sueldo. El sistema permitirá:

1. **Entender el significado de cada campo**: Cada cabecera CSV (RUT, CENTRO_COSTO, SUELDO_BASE, etc.) tendrá una descripción clara y ejemplos.
2. **Crear múltiples templates**: Cada Taxpayer puede crear N templates con nombres personalizados (ej: "Nómina Mensual", "Honorarios", "Turnos").
3. **Un template activo**: Solo un template puede estar activo a la vez para el proceso de importación.
4. **Campos obligatorios vs opcionales**: El sistema distinguirá entre campos requeridos (RUT, CENTRO_COSTO, SUELDO_BASE, DT, PREVISION, SALUD, SALUD_PORCENTAJE) y opcionales (BONO, HORAS_EXTRA, etc.).

---

## Arquitectura Actual

### Entidades Existentes

```
Taxpayer (1) ←──→ (N) Template
    ↓                    ↓
    | 1:N                | (value = CSV mapping JSON)
    ↓                    ↓
PayBookInstance      TemplateDefiniton (catálogo de campos)
    ↓ 1:N
PayBookDetails (datos del CSV parseado)
```

**Problema Actual**: 
- `Template` es OneToOne con `Taxpayer` (solo 1 template por empresa).
- Templates están hardcodeados en `PostConstructBean.taxpayerTemplates`.
- No existe interfaz de usuario para gestión.

**Solución Propuesta**:
- Cambiar relación a `ManyToOne` (N templates por Taxpayer).
- Agregar flag `isActive` para identificar el template activo.
- Crear CRUD completo con interfaz web.

---

## Implementación Completada

✅ 1. Modificado modelo de datos (Template.java y Taxpayer.java)
✅ 2. Creado TemplateRepository con métodos de consulta
✅ 3. Creado TemplateService con lógica de negocio
✅ 4. Creado TemplateController REST API
✅ 5. Creado templates.jsp con interfaz completa
✅ 6. Implementado JavaScript para gestión de templates
✅ 7. Actualizado proceso de importación

## Estimación

**Tiempo real de implementación**: Completado en una sesión
