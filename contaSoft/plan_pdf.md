@'
## Plan: PDF desde plantilla Excel

TL;DR: Generar el PDF del paybook usando una plantilla Excel (.xlsx) situada en el classpath. El flujo lee la plantilla con Apache POI, rellena los valores desde `PayBookInstance` y `PayBookDetails`, y renderiza a PDF (streaming) usando PDFBox o un HTML→PDF renderer. Mantener fallback a Jasper (`PayCheckReport.jrxml`) si falta la plantilla Excel.

### Steps
1. Añadir dependencias: actualizar `pom.xml` para incluir `org.apache.poi:poi-ooxml` y `org.apache.pdfbox:pdfbox` (o `openhtmltopdf` si se elige HTML→PDF).
2. Añadir plantilla: colocar `PayCheckTemplate.xlsx` en `src/main/resources/`.
3. Implementar endpoint: crear/actualizar `src/main/java/com/hp/contaSoft/spring/controller/ReportController.java` (GET `/api/reports/paybook/{id}/pdf`) que:
   - Recupera `PayBookInstance` y `PayBookDetails` desde repositorios existentes.
   - Detecta plantilla Excel en classpath; si existe, usa POI para leerla y rellenarla; si no, usa `PayCheckReport.jrxml` con Jasper.
   - Si se usa Excel: opcionalmente generar HTML desde la hoja (mejor estilo) y renderizar a PDF con `openhtmltopdf`; alternativa simple: pintar tabla con PDFBox.
   - Devolver `StreamingResponseBody` con `Content-Type: application/pdf` y `Content-Disposition: attachment; filename="paybook-{id}.pdf"`.
4. Errores y logging: devolver 404 si no se encuentra `PayBookInstance`; 500 para errores internos; loguear stacktrace.
5. Pruebas: ejecutar `mvnw spring-boot:run` y solicitar `GET /api/reports/paybook/{id}/pdf`.

### Further Considerations
1. Seguridad: ajustar `SecurityConfig` si el endpoint debe requerir autenticación.
2. Formato: para alta fidelidad en estilos Excel, prefiero la ruta Excel→HTML→PDF con `openhtmltopdf`.
3. Performance: usar streaming y cachear PDFs generados si se reusan frecuentemente.
'@ | Out-File -FilePath 'e:\ContaHerreraParra\contaSoftSpringBoot\contaSoft\plan_pdf.md' -Encoding utf8