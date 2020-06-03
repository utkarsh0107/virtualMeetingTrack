package quickstart.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.admin.reports.Reports;
import com.google.api.services.admin.reports.model.Activities;
import com.google.api.services.admin.reports.model.Activity;
import com.google.api.services.admin.reports.model.Activity.Events.Parameters;

import quickstart.AdminSDKReportsQuickstart;
import quickstart.repo.Attendee;
import quickstart.repo.AttendeeRepo;
import quickstart.repo.VirtualMeetingTrackRepo;

@Service
public class VirtualMeetingService {

	@Autowired
	private VirtualMeetingTrackRepo vmrepo;
	
	@Autowired
	private AttendeeRepo  attendeerepo;
	
	public void save(Attendee attendee) {
        attendeerepo.save(attendee);
    }
	
	AdminSDKReportsQuickstart admin;
	
	public void getCallEnded() throws IOException, GeneralSecurityException {
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Reports service = new Reports.Builder(HTTP_TRANSPORT, admin.JSON_FACTORY, admin.getCredentials(HTTP_TRANSPORT))
                .setApplicationName(admin.APPLICATION_NAME)
                .build();

        System.out.println("1");
        String userKey = "all";
        String applicationName = "meet";
        String eventName = "call_ended";
        String startTime = new SimpleDateFormat("yyyy-05-26'T'00:00:00+05:30")
                .format(new Date());
        String endTime= new SimpleDateFormat("yyyy-05-27'T'23:59:59+05:30")
                .format(new Date());
//        System.out.println(startTime);
//        System.out.println(endTime);
        Activities result = service.activities().list(userKey, applicationName).setEventName(eventName).setCustomerId("C00xtu9oz").setEndTime(endTime).setStartTime(startTime).execute();
        List<Activity> activities = result.getItems();
        if (activities == null || activities.size() == 0) {
            System.out.println("No meetings found.");
        } else {
            System.out.println("Meeting Details:");
            List<LocalTime> meetingStartTime=new ArrayList<LocalTime>();
        	List<LocalTime> meetingEndTime =new ArrayList<LocalTime>();
        	String clientMeetingCode = null, rmMeetingCode = null,otherMeetingCode=null;
        	String MeetingCode=null;
        	String DisplayName="";
        	String rmEmail="";
        	String clientEmail="";
        	Long duration = (long) 0;
        	HashMap<String,Long> meetingCodeToDurationMapping= new HashMap<>();
        	HashMap<String,List<Attendee>> meetingCodeToAttendeeMapping=new HashMap<String,List<Attendee>>();
        	
            for (Activity activity : activities) {
            	
            	Instant instant = Instant.parse( activity.getId().getTime().toStringRfc3339());
            	ZonedDateTime startTimeOfEntity = instant.atZone(ZoneId.of( "Asia/Kolkata" ));
            	List<Parameters> parameters=activity.getEvents().get(0).getParameters();
            	Map<String, String> stringParametersMap = parameters.stream().collect(HashMap::new, (m,v)->m.put(v.getName(), v.getValue()), HashMap::putAll);
            	Map<String, Long> intParametersMap = parameters.stream().collect(HashMap::new, (m,v)->m.put(v.getName(), v.getIntValue()), HashMap::putAll);
            	Map<String, Boolean> boolParametersMap = parameters.stream().collect(HashMap::new, (m,v)->m.put(v.getName(), v.getBoolValue()), HashMap::putAll);            	
            	System.out.println(boolParametersMap);
                System.out.println(stringParametersMap);
                System.out.println(intParametersMap);
                String meetingCode=stringParametersMap.get("meeting_code");
//              System.out.println(meetingCode);
                Boolean isExternal=boolParametersMap.get("is_external");
                Long meetingDuration=intParametersMap.get("duration_seconds");
                String displayName=stringParametersMap.get("display_name");
                String deviceType=stringParametersMap.get("device_type");
                if(!isExternal) {
                	if(!(activity.getActor().getEmail()).equals("utkarsh@fastfox.app")) {
                		if(!rmEmail.equals(activity.getActor().getEmail()) || !rmMeetingCode.equals(meetingCode)){
                		rmEmail=activity.getActor().getEmail();
                		rmMeetingCode=meetingCode;
                		Attendee attendees=new Attendee(displayName,deviceType,rmEmail,meetingCode);
                		save(attendees);
                			if(meetingCodeToAttendeeMapping.containsKey(meetingCode)){
                				List<Attendee> attendee1=meetingCodeToAttendeeMapping.get(meetingCode);
                				attendee1.add(attendees);
                				meetingCodeToAttendeeMapping.put(meetingCode,attendee1);
                			
                			}
                			else {
                				List<Attendee> attendee=new ArrayList<Attendee>();
                				attendee.add(attendees);
                				meetingCodeToAttendeeMapping.put(meetingCode, attendee);
                		    
                			}
                		}
                	}
                }
                else {
                	if(!stringParametersMap.containsKey("identifier_type")) {
                		if(!DisplayName.equals(displayName) || !otherMeetingCode.equals(meetingCode)) {
                			
                			DisplayName=displayName;
                			otherMeetingCode=meetingCode;
                			System.out.println(displayName+" "+ deviceType+" "+ meetingCode);
                			Attendee attendees=new Attendee(displayName,deviceType,"null",meetingCode);
                			save(attendees);
                			if(meetingCodeToAttendeeMapping.containsKey(meetingCode)){
                    			List<Attendee> attendee1=meetingCodeToAttendeeMapping.get(meetingCode);
                    			attendee1.add(attendees);
                    			meetingCodeToAttendeeMapping.put(meetingCode,attendee1);
                    			
                    		}
                    		else {
                    			List<Attendee> attendee=new ArrayList<Attendee>();
                    			attendee.add(attendees);
                    			meetingCodeToAttendeeMapping.put(meetingCode, attendee);
                    			
                    		}
                			
                		}
                	}
                	else {
                		if(!clientEmail.equals(stringParametersMap.get("identifier")) || !clientMeetingCode.equals(meetingCode)) {
                			clientEmail=stringParametersMap.get("identifier");
                			clientMeetingCode=meetingCode;
                			Attendee attendees=new Attendee(displayName,deviceType,clientEmail,meetingCode);
                			save(attendees);
                			if(meetingCodeToAttendeeMapping.containsKey(meetingCode)){
                    			List<Attendee> attendee1=meetingCodeToAttendeeMapping.get(meetingCode);
                    			attendee1.add(attendees);
                    			meetingCodeToAttendeeMapping.put(meetingCode,attendee1);
                    			
                    		}
                    		else {
                    			List<Attendee> attendee=new ArrayList<Attendee>();
                    			attendee.add(attendees);
                    			meetingCodeToAttendeeMapping.put(meetingCode, attendee);
                    			
                    		}
                		}
                	}
                }
                ZonedDateTime endTimeOfEntity= startTimeOfEntity.plusSeconds(meetingDuration);
                LocalTime localStartTime=startTimeOfEntity.toLocalTime();
                LocalTime localEndTime=endTimeOfEntity.toLocalTime();
                if(!meetingCode.equals(MeetingCode)) {
                	if(!meetingStartTime.isEmpty()) {
                		//System.out.println(meetingStartTime.toString());
                		//System.out.println(meetingEndTime.toString());
                		duration=duration(meetingStartTime,meetingEndTime);
                		meetingCodeToDurationMapping.put(MeetingCode, duration);
                		meetingStartTime.clear();
                		meetingEndTime.clear();
                	}
            		MeetingCode=meetingCode;
                }
                meetingStartTime.add(localStartTime);
        		meetingEndTime.add(localEndTime);
            }
            //System.out.println(meetingStartTime.toString());
            //System.out.println(meetingEndTime.toString());
            duration=duration(meetingStartTime,meetingEndTime);
    		meetingCodeToDurationMapping.put(MeetingCode, duration);
            
            Set<String> setofMeetingCodes = meetingCodeToAttendeeMapping.keySet();
            Iterator<String> itr = setofMeetingCodes.iterator();
            while(itr.hasNext())
            {
                String key = (String) itr.next();
                System.out.println(key);
                List<Attendee> value = (ArrayList<Attendee>) meetingCodeToAttendeeMapping.get(key);
                for(Attendee attendee:value) {
                	System.out.print(attendee.getDeviceType()+" ");
                	System.out.print(attendee.getEmail()+" ");
                	System.out.print(attendee.getName()+" ");
                }
                System.out.println();
            }
            System.out.println();
            System.out.println(meetingCodeToDurationMapping);
        }
    }
    public long duration(List<LocalTime> meetingStartTime,List<LocalTime>meetingEndTime) {
    	//System.out.println("1");
    	Collections.sort(meetingStartTime);
    	Collections.sort(meetingEndTime);
    	//System.out.println(meetingStartTime.toString());
		//System.out.println(meetingEndTime.toString());
    	int overlap=1,i=1,j=0;
    	long duration=0;
    	LocalTime startTime = null,endTime = null;
    	int size=meetingStartTime.size();
    	while(i<size && j<size) {
    		if(meetingStartTime.get(i).compareTo(meetingEndTime.get(j))<0 && overlap==0) {
    			overlap++;
    			i++;
    		}
    		else if(meetingStartTime.get(i).compareTo(meetingEndTime.get(j))>0 && overlap==1) {
    			overlap--;
    			j++;
    		}
    		else if(meetingStartTime.get(i).compareTo(meetingEndTime.get(j))<0 && overlap==1) {
    			overlap--;
    			startTime=meetingStartTime.get(i);
    			endTime=meetingEndTime.get(j);
    			duration=duration + startTime.until(endTime, ChronoUnit.SECONDS);
    			i++;
    		}
    		else if(meetingStartTime.get(i).compareTo(meetingEndTime.get(j))==0 && overlap==1) {
    			overlap--;
    			j++;
    			i++;
    		}
    	}
    	return duration;
	}
}
