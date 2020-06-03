package quickstart.repo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;



@Entity
@Table(name="agent_vm_track")
public class VirtualMeetingTrack {

	@Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer           Id;
	@Column(name = "virtual_meeting_id")
    private Integer           virtualMeetingId;
	@Column(name = "meeting_code")
	private String           meetingCode;
	@Column(name="meeting_duration")
	private Long             meetingDuration;
	
	public VirtualMeetingTrack(Integer id, Integer virtualMeetingId, String meetingCode, Long meetingDuration) {
		super();
		Id = id;
		this.virtualMeetingId = virtualMeetingId;
		this.meetingCode = meetingCode;
		this.meetingDuration = meetingDuration;
	}

	public Integer getId() {
		return Id;
	}

	public void setId(Integer id) {
		Id = id;
	}

	public Integer getVirtualMeetingId() {
		return virtualMeetingId;
	}

	public void setVirtualMeetingId(Integer virtualMeetingId) {
		this.virtualMeetingId = virtualMeetingId;
	}

	public String getMeetingCode() {
		return meetingCode;
	}

	public void setMeetingCode(String meetingCode) {
		this.meetingCode = meetingCode;
	}

	public Long getMeetingDuration() {
		return meetingDuration;
	}

	public void setMeetingDuration(Long meetingDuration) {
		this.meetingDuration = meetingDuration;
	}
	
	
}
