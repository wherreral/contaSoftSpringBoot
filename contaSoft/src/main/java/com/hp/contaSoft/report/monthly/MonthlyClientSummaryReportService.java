package com.hp.contaSoft.report.monthly;

import com.hp.contaSoft.excel.entities.PayBookDetails;
import com.hp.contaSoft.hibernate.dao.repositories.PayBookDetailsRepository;
import com.hp.contaSoft.hibernate.dao.repositories.PayBookInstanceRepository;
import com.hp.contaSoft.hibernate.dao.repositories.TaxpayerRepository;
import com.hp.contaSoft.hibernate.entities.PayBookInstance;
import com.hp.contaSoft.hibernate.entities.Taxpayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@Service
public class MonthlyClientSummaryReportService {

    @Autowired
    private TaxpayerRepository taxpayerRepository;

    @Autowired
    private PayBookInstanceRepository payBookInstanceRepository;

    @Autowired
    private PayBookDetailsRepository payBookDetailsRepository;

    public MonthlyClientSummaryReportData buildReportData(MonthlyClientSummaryRequest request, String familyId, String generatedBy) {
        validateRequest(request);

        Optional<Taxpayer> taxpayerOpt = taxpayerRepository.findById(request.getClientId());
        if (!taxpayerOpt.isPresent()) {
            throw new IllegalArgumentException("Cliente no encontrado");
        }

        Taxpayer taxpayer = taxpayerOpt.get();
        if (familyId != null && taxpayer.getFamilyId() != null && !familyId.equals(taxpayer.getFamilyId())) {
            throw new IllegalArgumentException("Cliente no pertenece a su organización");
        }

        List<PayBookInstance> instances = payBookInstanceRepository.findAllByFamilyIdAndTaxpayerId(familyId, request.getClientId());
        if (instances == null || instances.isEmpty()) {
            throw new IllegalArgumentException("No existen liquidaciones para el cliente");
        }

        PayBookInstance targetInstance = selectTargetInstance(instances, request.getMonth(), request.getYear());
        if (targetInstance == null) {
            throw new IllegalArgumentException("No existen liquidaciones para el período solicitado");
        }

        List<PayBookDetails> details = payBookDetailsRepository.findAllByPayBookInstanceIdAndFamilyId(targetInstance.getId(), familyId);
        if (details == null || details.isEmpty()) {
            throw new IllegalArgumentException("La liquidación seleccionada no tiene detalles");
        }

        MonthlyClientSummaryReportData data = new MonthlyClientSummaryReportData();
        data.setClientName(safe(taxpayer.getName()));
        data.setClientRut(safe(taxpayer.getRut()));
        data.setPeriodLabel(buildPeriodLabel(request.getMonth(), request.getYear()));
        data.setGeneratedAt(new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("es-CL")).format(new Date()));
        data.setGeneratedBy(generatedBy != null ? generatedBy : "sistema");

        List<MonthlyClientSummaryRow> rows = new ArrayList<MonthlyClientSummaryRow>();
        for (PayBookDetails d : details) {
            MonthlyClientSummaryRow row = mapRow(d);
            rows.add(row);

            data.setTotalImponible(data.getTotalImponible() + row.getTotalImponible());
            data.setTotalNoImponible(data.getTotalNoImponible() + (d.getColacion() + d.getMovilizacion() + d.getTotalAsignacionFamiliar() + d.getDescuentoHerramientas()));
            data.setTotalHaber(data.getTotalHaber() + d.getTotalHaber());
            data.setTotalDctoPrevisional(data.getTotalDctoPrevisional() + row.getTotalDctoPrevisional());
            data.setTotalDctoPersonal(data.getTotalDctoPersonal() + row.getTotalDctoPersonal());
            data.setTotalDescuentos(data.getTotalDescuentos() + row.getTotalDescuentos());
            data.setTotalAlcanceLiquido(data.getTotalAlcanceLiquido() + row.getAlcanceLiquido());
        }

        data.setRows(rows);
        data.setWorkersCount(rows.size());
        return data;
    }

    private MonthlyClientSummaryRow mapRow(PayBookDetails d) {
        MonthlyClientSummaryRow row = new MonthlyClientSummaryRow();
        row.setRut(safe(d.getRut()));
        row.setCentroCosto(safe(d.getCentroCosto()));
        row.setDiasTrabajados(d.getDiasTrabajados());
        row.setSueldoMensual(d.getSueldoMensual());
        row.setGratificacion(d.getGratificacion());
        row.setBonoProduccion(d.getBonoProduccion());
        row.setTotalImponible(d.getTotalImponible());
        row.setValorPrevision(d.getValorPrevision());
        row.setValorSalud(d.getValorSalud());

        // Alineado a la definición actual del sistema: previsional = previsión + salud (sin AFC)
        double totalDctoPrevisional = d.getValorPrevision() + d.getValorSalud();
        row.setTotalDctoPrevisional(totalDctoPrevisional);

        double totalDctoPersonal =
                d.getValorIUT() +
                d.getPrestamos() +
                d.getSeguroOncologico() +
                parseDoubleSafe(d.getSeguroOAccidentes()) +
                d.getAnticipo() +
                d.getDescApvCtaAh() +
                d.getDescPtmoCcaaff() +
                d.getDescPtmoSolidario();
        row.setTotalDctoPersonal(totalDctoPersonal);

        double totalDescuentos = totalDctoPrevisional + totalDctoPersonal;
        row.setTotalDescuentos(totalDescuentos);

        double alcanceLiquido = d.getTotalHaber() - totalDctoPrevisional;
        row.setAlcanceLiquido(alcanceLiquido);
        return row;
    }

    private PayBookInstance selectTargetInstance(List<PayBookInstance> instances, String month, Integer year) {
        Integer requestMonthNumber = parseMonth(month);
        List<PayBookInstance> filtered = new ArrayList<PayBookInstance>();

        for (PayBookInstance instance : instances) {
            if (instance == null) continue;

            if (year != null) {
                Date created = instance.getCreated();
                if (created == null) continue;
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(created);
                int instanceYear = calendar.get(Calendar.YEAR);
                if (instanceYear != year.intValue()) continue;
            }

            if (requestMonthNumber != null) {
                Integer instanceMonthNumber = parseMonth(instance.getMonth());
                if (instanceMonthNumber == null || instanceMonthNumber.intValue() != requestMonthNumber.intValue()) continue;
            }

            filtered.add(instance);
        }

        if (filtered.isEmpty()) {
            return null;
        }

        Collections.sort(filtered, new Comparator<PayBookInstance>() {
            @Override
            public int compare(PayBookInstance a, PayBookInstance b) {
                int versionCmp = Integer.compare(b.getVersion(), a.getVersion());
                if (versionCmp != 0) return versionCmp;
                Date ad = a.getCreated();
                Date bd = b.getCreated();
                if (ad != null && bd != null) {
                    int dateCmp = bd.compareTo(ad);
                    if (dateCmp != 0) return dateCmp;
                }
                return Long.compare(b.getId(), a.getId());
            }
        });

        return filtered.get(0);
    }

    private void validateRequest(MonthlyClientSummaryRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request vacío");
        }
        if (request.getClientId() == null || request.getClientId() <= 0) {
            throw new IllegalArgumentException("clientId es obligatorio");
        }
        if (request.getMonth() == null || request.getMonth().trim().isEmpty()) {
            throw new IllegalArgumentException("month es obligatorio");
        }
        if (parseMonth(request.getMonth()) == null) {
            throw new IllegalArgumentException("month inválido. Use número (1-12) o nombre (ej: MARZO)");
        }
        if (request.getYear() != null && (request.getYear() < 2000 || request.getYear() > 2100)) {
            throw new IllegalArgumentException("year inválido");
        }
    }

    private Integer parseMonth(String value) {
        if (value == null) return null;
        String normalized = normalize(value);
        if (normalized.isEmpty()) return null;

        try {
            int m = Integer.parseInt(normalized);
            if (m >= 1 && m <= 12) return m;
        } catch (NumberFormatException ignored) {
        }

        Map<String, Integer> monthMap = new HashMap<String, Integer>();
        monthMap.put("ENERO", 1);
        monthMap.put("FEBRERO", 2);
        monthMap.put("MARZO", 3);
        monthMap.put("ABRIL", 4);
        monthMap.put("MAYO", 5);
        monthMap.put("JUNIO", 6);
        monthMap.put("JULIO", 7);
        monthMap.put("AGOSTO", 8);
        monthMap.put("SEPTIEMBRE", 9);
        monthMap.put("SETIEMBRE", 9);
        monthMap.put("OCTUBRE", 10);
        monthMap.put("NOVIEMBRE", 11);
        monthMap.put("DICIEMBRE", 12);

        Integer direct = monthMap.get(normalized);
        if (direct != null) return direct;

        for (Map.Entry<String, Integer> entry : monthMap.entrySet()) {
            if (normalized.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    private String buildPeriodLabel(String month, Integer year) {
        String normalizedMonth = month != null ? month.trim() : "";
        if (year != null) {
            return normalizedMonth + " " + year;
        }
        return normalizedMonth;
    }

    private String normalize(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return normalized.trim().toUpperCase(Locale.ROOT);
    }

    private double parseDoubleSafe(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value.trim().replace(",", "."));
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}

