package com.ericsson.hgd;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.domain.Filter;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.Version;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Inicial {

	
	    private static final String JIRA_URL = ApplicationProperties.INSTANCE.getJiraUrl();
	    private static final String JIRA_ADMIN_USERNAME = ApplicationProperties.INSTANCE.getJiraAdminUsername();
	    private static final String JIRA_ADMIN_PASSWORD = ApplicationProperties.INSTANCE.getJiraAdminPassword();
	    private static final String ENCODING = "UTF-8";
	    static int totRegistros=0;
	    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	    public static final Logger lg = Logger.getLogger(Inicial.class);
	public static void main(String[] args) throws Exception {
		try {
			
	    LocalDateTime fechaHoy = LocalDateTime.now();  
		lg.info("Creando Conexión a Jira...");
		JiraRestClient connJira = getclienteJira();
		

	
	
		ArrayList<Tickets> totalTickets = obtenerTickets(dtf.format(fechaHoy),connJira);
	
		
		lg.info("Total Tickets obtenidos ... "+totalTickets.size());
			
		
		iteraTickes(totalTickets);
		
		
	
			connJira.close();
		} catch (IOException e) {
			lg.error(e.getMessage(),e);
			 SendMsg smsge = new SendMsg();
				smsge.sendToTelegram(URLEncoder.encode("ERROR : "+e.getMessage(), ENCODING),"PERSONAL");
		}
		
		
		lg.info("Proceso Finalizado");
		
		
		
	}
	
	public static void iteraTickes( ArrayList<Tickets> datos) throws UnsupportedEncodingException{
	   // String retorno = "NOK";
	    StringBuilder msg = new StringBuilder();
		for (int i=0; i<datos.size(); i++) {
			 
		//	    retorno ="OK";
			    msg.append("\nAtender el Ticket: "+datos.get(i).getKey());   
			    msg.append("\nCreado el      : "+datos.get(i).getDateCreated());
			    msg.append("\nCreador por   : "+datos.get(i).getCreatedBy());
			    msg.append("\nStatus Actual  : "+datos.get(i).getStatus());
			    msg.append("\nDetectado en  : "+datos.get(i).getDetectionOn());
			    //msg.append("\nPara el Equipo : "+datos.get(i).getFixVersion());
			    msg.append("\nPara el Equipo : #TEAM# ");
			    msg.append("\n- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
			    
			   String[] teams = datos.get(i).getFixVersion().split("-");
			  
			  String msg1 = "";
			  if (teams.length == 1) {
				 
				 
				  msg1 = msg.toString().replaceFirst("#TEAM#", teams[0].toString());
				 // lg.info(msg1);
				 provideTeam(msg1);
				  
			  }else {
				
				 
				 // for (String eq : teams) {
					for (int j=0; j < teams.length;j++) {
					  msg1 = msg.toString().replace("#TEAM#", teams[j].toString() );
					  provideTeam(msg1);
					 
				    }
			  }
			  
			  
			  
			    
			   
			  /*ec		   
			 SendMsg smsg = new SendMsg();
			
		 try {
			
			if (datos.get(i).getFixVersion().contains("Asignacion")) {						
				retorno = 	smsg.sendToTelegram(URLEncoder.encode(msg.toString(), ENCODING),ApplicationProperties.INSTANCE.chatEnvioAsig());
			}else if (datos.get(i).getFixVersion().contains("Entrada")) {
				retorno = 	smsg.sendToTelegram(URLEncoder.encode(msg.toString(), ENCODING),ApplicationProperties.INSTANCE.chatEnvioEnt());		
			}else if (datos.get(i).getFixVersion().contains("Frontales")) {
				retorno = 	smsg.sendToTelegram(URLEncoder.encode(msg.toString(), ENCODING),ApplicationProperties.INSTANCE.chatEnvioFro());	
			}else if (datos.get(i).getFixVersion().contains("CDR")) {
				retorno = 	smsg.sendToTelegram(URLEncoder.encode(msg.toString(), ENCODING),ApplicationProperties.INSTANCE.chatEnvioCdR());	
			}else {
				retorno = 	smsg.sendToTelegram(URLEncoder.encode(msg.toString(), ENCODING),ApplicationProperties.INSTANCE.chatEnvio());
				
			}
			
			lg.info("Mensaje Enviado -> "+retorno);
			} catch (UnsupportedEncodingException e) {
				lg.error(e.getMessage(),e);
				 SendMsg smsge = new SendMsg();
				smsge.sendToTelegram(URLEncoder.encode("ERROR : "+e.getMessage(), ENCODING),ApplicationProperties.INSTANCE.chatErrores());
			}
		*/
		    lg.info(msg.toString());
			msg = new StringBuilder();
		}
	  
	    //return retorno;
	}
	
	public static String provideTeam(String pmsg) throws UnsupportedEncodingException {
		
		 SendMsg smsg = new SendMsg();
		 String retorno = "";
		 
		 try {
			 
			 if (pmsg.contains("Asignacion")) {						
					retorno = 	smsg.sendToTelegram(URLEncoder.encode(pmsg.toString(), ENCODING),ApplicationProperties.INSTANCE.chatEnvioAsig());
			}else if (pmsg.contains("Entrada")) {
				retorno = 	smsg.sendToTelegram(URLEncoder.encode(pmsg.toString(), ENCODING),ApplicationProperties.INSTANCE.chatEnvioEnt());		
			}else if (pmsg.contains("Frontales")) {
				retorno = 	smsg.sendToTelegram(URLEncoder.encode(pmsg.toString(), ENCODING),ApplicationProperties.INSTANCE.chatEnvioFro());	
			}else if (pmsg.contains("CDR")) {
				retorno = 	smsg.sendToTelegram(URLEncoder.encode(pmsg.toString(), ENCODING),ApplicationProperties.INSTANCE.chatEnvioCdR());	
			}else {
				retorno = 	smsg.sendToTelegram(URLEncoder.encode(pmsg.toString(), ENCODING),ApplicationProperties.INSTANCE.chatEnvio());
				
			}
			 
			 
		 }catch (UnsupportedEncodingException e) {
				lg.error(e.getMessage(),e);
				 SendMsg smsge = new SendMsg();
				retorno = smsge.sendToTelegram(URLEncoder.encode("ERROR : "+e.getMessage(), ENCODING),ApplicationProperties.INSTANCE.chatErrores());
			}
		 
		/*ec try {
		
		if (datos.get(i).getFixVersion().contains("Asignacion")) {						
			retorno = 	smsg.sendToTelegram(URLEncoder.encode(msg.toString(), ENCODING),ApplicationProperties.INSTANCE.chatEnvioAsig());
		}else if (datos.get(i).getFixVersion().contains("Entrada")) {
			retorno = 	smsg.sendToTelegram(URLEncoder.encode(msg.toString(), ENCODING),ApplicationProperties.INSTANCE.chatEnvioEnt());		
		}else if (datos.get(i).getFixVersion().contains("Frontales")) {
			retorno = 	smsg.sendToTelegram(URLEncoder.encode(msg.toString(), ENCODING),ApplicationProperties.INSTANCE.chatEnvioFro());	
		}else if (datos.get(i).getFixVersion().contains("CDR")) {
			retorno = 	smsg.sendToTelegram(URLEncoder.encode(msg.toString(), ENCODING),ApplicationProperties.INSTANCE.chatEnvioCdR());	
		}else {
			retorno = 	smsg.sendToTelegram(URLEncoder.encode(msg.toString(), ENCODING),ApplicationProperties.INSTANCE.chatEnvio());
			
		}
		
		lg.info("Mensaje Enviado -> "+retorno);
		} catch (UnsupportedEncodingException e) {
			lg.error(e.getMessage(),e);
			 SendMsg smsge = new SendMsg();
			smsge.sendToTelegram(URLEncoder.encode("ERROR : "+e.getMessage(), ENCODING),ApplicationProperties.INSTANCE.chatErrores());
		}
	*/
		return retorno;
	}
	public static JiraRestClient getclienteJira () throws URISyntaxException {
		JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
		 JiraRestClient client = null;
		 
			 URI uri = new URI(JIRA_URL);	  
			
			 client = factory.createWithBasicHttpAuthentication(uri, JIRA_ADMIN_USERNAME, JIRA_ADMIN_PASSWORD);
		
		return client;
		
	}
	
	public static String obtenerJQL(JiraRestClient pconnJira) throws Exception {
		String filterID =  ApplicationProperties.INSTANCE.getFilterID();
		
		Promise<Filter> fjql = pconnJira.getSearchClient().getFilter(Long.parseLong(filterID));
		String jql = fjql.get().getJql().toString();
		lg.info(jql);
		
		return jql;
	}

	public static ArrayList<Tickets> obtenerTickets(String fechaHoy, JiraRestClient pconnJira ) throws Exception {
	  ArrayList<Tickets> grupoTic = new ArrayList<Tickets>();
	  String jql = "";
	//  jql = ApplicationProperties.INSTANCE.getJQL();
	  
	//  jql = jql.replace("$fecha$", fechaHoy);
			
	//	String filterID =  ApplicationProperties.INSTANCE.getFilterID();
		
//		Promise<Filter> fjql = pconnJira.getSearchClient().getFilter(Long.parseLong(filterID));
	//	jql = obtenerJQL(pconnJira).get().getJql().toString();
		
		Promise<SearchResult> searchJqlPromise = pconnJira.getSearchClient().searchJql(obtenerJQL(pconnJira));
	  
	  for (Issue issue : searchJqlPromise.claim().getIssues()) {
			  Tickets tic = new Tickets();
			  String originador = "";
			  String detectionOn = "";
			  StringBuilder versiones  = new StringBuilder();
 
			
			  
			    tic.setDateCreated(issue.getCreationDate().toString("dd/MM/yyyy HH:mm"));
	            tic.setKey(issue.getKey());
	            tic.setStatus(issue.getStatus().getName());
	            
	            
	            try {
					JSONObject creador = new JSONObject(issue.getFieldByName("Creator").getValue().toString());
					originador  = creador.get("displayName").toString();
					
				} catch (JSONException e) {
					
					lg.error(e.getMessage(),e);
					originador = "";
				}
	            
	            
	            if (issue.getFieldByName("Detection On").getValue() != null) {
	            try {
					JSONObject dEtection = new JSONObject(issue.getFieldByName("Detection On").getValue().toString());
					detectionOn = dEtection.get("value").toString();
				} catch (JSONException e) {
					lg.error(e.getMessage(),e);
					detectionOn = "";
				}
	            }            	
	            	Iterable<Version> fv = issue.getFixVersions();
	            	
	            	for (Version ver:fv) {
	            		versiones.append(ver.getName() +"-");
	            		
	            	}
	            	        
	            tic.setCreatedBy(originador);
	            tic.setDetectionOn(detectionOn);
	            tic.setFixVersion(versiones.toString());
	        
	            
			  grupoTic.add(tic);
		  }
		return grupoTic;
	
	}
}
