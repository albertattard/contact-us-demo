CREATE TABLE "events" (
  "id"          UUID PRIMARY KEY,
  "office"      VARCHAR(255) NOT NULL,
  "date"        DATE NOT NULL,
  "caption"     VARCHAR(64),
  "description" VARCHAR(255)
);

CREATE TABLE "events_attendees" (
  "id"               UUID PRIMARY KEY,
  "event"            UUID NOT NULL,
  "name"             VARCHAR(64),
  "food_preference"  VARCHAR(64)
);

INSERT INTO "events" ("id","office","date","caption","description") VALUES ('47705b9b-518b-4dc2-a517-3dbbcab13fe7','ThoughtWorks Cologne','2077-04-27','Spring Boot','Deep Dive into Spring Boot technologies');
