package demo.boot.event;

import java.util.UUID;

public class UuidGeneratorService {
  public UUID nextAttendeeId() {
    return UUID.randomUUID();
  }
}
