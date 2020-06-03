package quickstart.repo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="meeting_attendee")
public class Attendee {
	
	
	@Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer           Id;
	
	@Column(name = "display_name")
    private String           name;
	
	@Column(name = "device_type")
	private String           deviceType;
	
	@Column(name ="email")
	private String           email;

	@Column(name="meeting_code")
	private String           meetingCode;
	
	public Attendee() {
		
	}
	
	public Attendee(String name, String deviceType,String email,String meetingCode) {
		super();
		this.name = name;
		this.deviceType = deviceType;
		this.email=email;
		this.meetingCode=meetingCode;
	}

	public String getMeetingCode() {
		return meetingCode;
	}

	public void setMeetingCode(String meetingCode) {
		this.meetingCode = meetingCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
	
}
