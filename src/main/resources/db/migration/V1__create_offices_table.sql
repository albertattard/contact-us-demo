CREATE TABLE "offices" (
  "name"    VARCHAR(64) PRIMARY KEY,
  "address" VARCHAR(255) NOT NULL,
  "country" VARCHAR(64) NOT NULL,
  "phone"   VARCHAR(64),
  "email"   VARCHAR(64),
  "webpage" VARCHAR(128)
);
