package demo.boot.event;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UuidGeneratorService {

  public UUID nextAttendeeId() {
    return UUID.randomUUID();
  }
}
