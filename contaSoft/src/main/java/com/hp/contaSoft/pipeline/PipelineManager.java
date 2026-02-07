package com.hp.contaSoft.pipeline;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.contaSoft.constant.HPConstant;
import com.hp.contaSoft.pipeline.Error.PipelineMessage;
import com.hp.contaSoft.pipeline.chain.processor.ValidateFileNameProcessor;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Component
public class PipelineManager {

	private String pipelineName;
	private String chainName;
	private boolean status;
	@Autowired
	private ValidateFileNameProcessor validateFileNameProcessor;
	@Autowired
    private ApplicationContext appContext;
	
	public PipelineManager() {
		
	}
	
	private List<PipelineManagerEntity> loadFile() throws InterruptedException  {
		
		System.out.println("Method:loadFile");
		TimeUnit.SECONDS.sleep(HPConstant.SLEEP_TIME);
		
		ObjectMapper objectMapper = new ObjectMapper();
		PipelineManagerHandlerEntity example;
		
		try {
			
//			example = objectMapper.readValue(new File("C:\\Users\\williams\\Desktop\\pipeline_boot.json"), PipelineManagerHandlerEntity.class);
			//File file = new ClassPathResource("pipeline_boot.json").getFile();
			//example = objectMapper.readValue( file, PipelineManagerHandlerEntity.class);
			
			example = objectMapper.readValue( new ClassPathResource("pipeline_boot.json").getFile(), PipelineManagerHandlerEntity.class);
			
			List<PipelineManagerEntity> pipelineManagerList =example.getPipelinemanager();
			return pipelineManagerList;
			
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			
		}
		
		return null;
		
		
		/*
		for(PipelineManagerEntity pc : pipelineList) {
			System.out.println("pc:"+pc.pipelinechain.size());
		}
		*/
		
		
	}
	
	@Transactional
	public PipelineMessage execute(PipelineMessage pm) throws InterruptedException {
		
		System.out.println("Method:execute");
		TimeUnit.SECONDS.sleep(HPConstant.SLEEP_TIME);
		
		List<PipelineManagerEntity> pmel = loadFile();
		PipelineManagerEntity pme = existPipeline(pmel);
		
		
		
		if(pme != null) {
			pm = startChain(pme, pm); 
		}else {
			pm.setValid(false);
			pm.setErrorMessageOutput("Error Procesando Archivo");
		}
			
		
		return pm;
		
		
		
	}
	
	
	
	private PipelineManagerEntity existPipeline(List<PipelineManagerEntity> pipelineList) throws InterruptedException {
		
		System.out.println("Method:existPipeline");
		TimeUnit.SECONDS.sleep(HPConstant.SLEEP_TIME);
		
		System.out.println("existPipeline PL");
		pipelineList.forEach(x-> System.out.println(x));
		
		for(PipelineManagerEntity pme : pipelineList)
		{
			System.out.println(pme.getName());
			if(pme.getName().equals("UploadPayrollFile"))
				return pme;
		}
		return null;
	}
	
	private PipelineMessage startChain(PipelineManagerEntity pme, PipelineMessage pm) throws InterruptedException {
		
		
		
		System.out.println("Method:startChain");
		System.out.println("pme="+pme);
		List<PipelineChainEntity> pcl = pme.getPipelinechain();
		System.out.println("pcl ="+pcl);
		TimeUnit.SECONDS.sleep(HPConstant.SLEEP_TIME);
		Class class1;
		boolean flag = true;
		int i = 0;
		String classNameLink = "";
		
		//for(PipelineChainEntity pipelineChain : pcl)
		while(flag)
		{
			//First case should just execute
			if (i == 0) 
			{
				try {
		
					System.out.println("Method:startChain- i=0");
					System.out.println(pcl.get(i).getName());
					TimeUnit.SECONDS.sleep(HPConstant.SLEEP_TIME);
					
					class1 = Class.forName(pcl.get(i).getName());
					
					Processor processor = (Processor) appContext.getBean(class1);			
					//Processor processor = (Processor) class1.newInstance();
					
					System.out.println("processor="+processor);
					pm = processor.run(pm);
					//pm = validateFileNameProcessor.run(pm);
					
					if(!pm.isValid())
					{
						pm.setValid(false);
						pm.setErrorMessageOutput("Error Procesando Pipeline");
						return pm;
						//this.setStatus(false);
						//break;
					}
						
					if(pcl.get(i).getLink() != null) {
						classNameLink = pcl.get(i).getLink();
						i++;
					}else
						break;
					
					
					
				} catch (ClassNotFoundException e) {
					System.out.println("aqui1");
					this.status = false;
					e.printStackTrace();
					flag=false;
				}
			}
			else 
			{ //second iteration should check existence of the link
				
				System.out.println("Method:startChain- i!=0");
				if(i < pcl.size())
					System.out.println(pcl.get(i).getName());
				
				TimeUnit.SECONDS.sleep(1);
				
				if(containsLink(pcl, classNameLink)) 
				{
					System.out.println("Encontro Link");
					
					try 
					{
						class1 = Class.forName(pcl.get(i).getName());
						//Processor processor;
						//processor = (Processor) class1.newInstance();
						Processor processor = (Processor) appContext.getBean(class1);
						pm = processor.run(pm);
						
						if(!pm.isValid())
						{
							pm.setValid(false);
							pm.setErrorMessageOutput("Error Procesando Pipeline-second iteration");
							return pm;
							
						}
							
						if(pcl.get(i).getLink() != null) {
							classNameLink = pcl.get(i).getLink();
							i++;
						}else
							return pm;
					}catch(Exception e) {
						e.printStackTrace();
					}
					
					
				}
				else
					flag = false;
				
			}
			
			
			
			
		}
		
		System.out.println("Method:FIN startChain- ");
		TimeUnit.SECONDS.sleep(1);
		
		return pm;
	}
	
	public boolean getStatus() {
		return this.status;
	}
	
	public boolean containsLink(final List<PipelineChainEntity> list, final String name){
	    
		System.out.println("LinkName:"+name);
		if(!name.equals(""))
			return list.stream().filter(o -> o.getLink().equals(name)).findFirst().isPresent();
		else
			return false;
	}
	
}
