package com.hp.contaSoft.pipeline.Error;

import java.io.InputStream;

import org.springframework.stereotype.Component;

import com.hp.contaSoft.hibernate.entities.PayBookInstance;
import com.hp.contaSoft.hibernate.entities.Taxpayer;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Component
public class PipelineMessage {

	private boolean isValid = true;
	private InputStream isInput; 
	private InputStream isInput2;
	private Taxpayer taxpayerInput;
	private PayBookInstance payBookInstance;
	private String fileNameInput;
	private String errorMessageOutput;
	private String fileClientRutOutput;
	private String fileMonthOutput;
	private String fileYearOutput;
	private String familyId;
	private String fileExtension;


	public PipelineMessage() {
		
	}
}
