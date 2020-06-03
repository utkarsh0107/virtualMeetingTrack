package quickstart.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendeeRepo extends JpaRepository<Attendee, Integer>{

}
