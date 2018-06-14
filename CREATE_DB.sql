CREATE TABLE "project" (
    "id"  SERIAL  NOT NULL,
    "name" varchar   NOT NULL,
    "description" varchar   NULL,
    CONSTRAINT "pk_project" PRIMARY KEY (
        "id"
     )
);

CREATE TABLE "room" (
    "id"  SERIAL  NOT NULL,
    "projectid" int   NOT NULL,
    "name" varchar   NOT NULL,
    "description" varchar   NULL,
    "floorplan" varchar   NULL,
    "floorspace" decimal   NULL,
    CONSTRAINT "pk_room" PRIMARY KEY (
        "id"
     )
);

CREATE TABLE "measurement" (
    "id"  SERIAL  NOT NULL,
    "roomid" int   NOT NULL,
    "name" varchar   NOT NULL,
    "description" varchar   NULL,
    "startdate" date   NULL,
    "enddate" date   NULL,
    "creator" varchar   NULL,
    "measurementstate" varchar  DEFAULT 'ready' NOT NULL,
    "xoffset" decimal   NULL,
    "yoffset" decimal   NULL,
    "scalefactor" decimal   NULL,
    "createddate" timestamp   NOT NULL,
    CONSTRAINT "pk_measurement" PRIMARY KEY (
        "id"
     )
);

CREATE TABLE "reading" (
    "id"  SERIAL  NOT NULL,
    "measurementid" int   NOT NULL,
    "luxvalue" int   NOT NULL,
    "timestamp" timestamp   NOT NULL,
    "xposition" decimal   NOT NULL,
    "yposition" decimal   NOT NULL,
    "zposition" decimal   NOT NULL,
    CONSTRAINT "pk_reading" PRIMARY KEY (
        "id"
     )
);

CREATE TABLE "anchorposition" (
    "id"  SERIAL  NOT NULL,
    "anchorid" int   NOT NULL,
    "measurementid" int   NOT NULL,
    "xposition" decimal   NOT NULL,
    "yposition" decimal   NOT NULL,
    "zposition" decimal   NOT NULL,
    CONSTRAINT "pk_anchorposition" PRIMARY KEY (
        "id"
     )
);

CREATE TABLE "anchor" (
    "id"  SERIAL  NOT NULL,
    "networkid" varchar   NOT NULL,
    CONSTRAINT "pk_anchor" PRIMARY KEY (
        "id"
     )
);

ALTER TABLE "room" ADD CONSTRAINT "fk_room_projectid" FOREIGN KEY("projectid")
REFERENCES "project" ("id");

ALTER TABLE "measurement" ADD CONSTRAINT "fk_measurement_roomid" FOREIGN KEY("roomid")
REFERENCES "room" ("id");

ALTER TABLE "reading" ADD CONSTRAINT "fk_reading_measurementid" FOREIGN KEY("measurementid")
REFERENCES "measurement" ("id");

ALTER TABLE "anchorposition" ADD CONSTRAINT "fk_anchorposition_anchorid" FOREIGN KEY("anchorid")
REFERENCES "anchor" ("id");

ALTER TABLE "anchorposition" ADD CONSTRAINT "fk_anchorposition_measurementid" FOREIGN KEY("measurementid")
REFERENCES "measurement" ("id");

