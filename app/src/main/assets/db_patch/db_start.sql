BEGIN;
PRAGMA foreign_keys=off;

ALTER TABLE patient_interview_master ADD COLUMN RECORD_DATE TEXT;
ALTER TABLE patient_interview_master_delete ADD COLUMN RECORD_DATE TEXT;
DROP TABLE medicine_stock_history;
DROP TABLE maternal_service;

CREATE TABLE "medicine_stock_history" (
	"LOCATION_ID"	NUMERIC,
	"USER_ID"	NUMERIC,
	"MEDICINE_ID"	NUMERIC,
	"CURRENT_STOCK_QTY"	NUMERIC,
	"LAST_UPDATE_ON"	NUMERIC,
	"LAST_SAVING_ON"	NUMERIC
);


CREATE TABLE "maternal_service" (
	"MATERNAL_SERVICE_ID"	INTEGER DEFAULT (0),
	"MATERNAL_ID"	INTEGER NOT NULL,
	"BMI_VALUE"	NUMERIC,
	"BMI"	NUMERIC,
	"PULSE"	NUMERIC DEFAULT 'NULL',
	"PULSE_STATUS"	VARCHAR(50) DEFAULT 'NULL',
	"BLOOD_PRESSURE"	VARCHAR(10) DEFAULT 'NULL',
	"BLOOD_PRESSURE_TYPE"	VARCHAR(50) DEFAULT 'NULL',
	"TEMPERATURE"	NUMERIC DEFAULT 'NULL',
	"TEMPERATURE_TYPE"	VARCHAR(50) DEFAULT 'NULL',
	"WEIGHT"	NUMERIC DEFAULT 'NULL',
	"HEIGHT_IN_CM"	NUMERIC DEFAULT 'NULL',
	"WEEKLY_WEIGHT_GAIN"	NUMERIC DEFAULT 'NULL',
	"ANAEMIA"	VARCHAR(50) DEFAULT 'NULL',
	"JAUNDICE"	VARCHAR(50) DEFAULT 'NULL',
	"OEDEMA"	VARCHAR(50) DEFAULT 'NULL',
	"VOMITING"	VARCHAR(50) DEFAULT 'NULL',
	"SUGAR_OF_URINE"	VARCHAR(50) DEFAULT 'NULL',
	"PROTEIN_OF_URINE"	VARCHAR(20) DEFAULT 'NULL',
	"HEIGHT_OF_UTERUS"	VARCHAR(50) DEFAULT 'NULL',
	"FETAL_MOVEMENT"	VARCHAR(50) DEFAULT 'NULL',
	"FETAL_HEART_RATE"	VARCHAR(50),
	"FETAL_LIE"	VARCHAR(20) DEFAULT 'NULL',
	"FETAL_PRESENTATION"	VARCHAR(20) DEFAULT 'NULL',
	"BREAST_PROBLEM"	VARCHAR(150) DEFAULT 'NULL',
	"VITAMIN_A"	VARCHAR(3) DEFAULT 'NULL',
	"RISK_STATE"	VARCHAR(15) DEFAULT 'NULL',
	"RISK_PROP"	NUMERIC,
	"MATERENAL_STATUS"	VARCHAR(10) DEFAULT 'NULL',
	"MATERNAL_CARE_ID"	NUMERIC NOT NULL,
	"INTERVIEW_ID"	NUMERIC,
	"CREATE_DATE"	NUMERIC NOT NULL,
	"USER_ID"	NUMERIC,
	"BENEF_ID"	NUMERIC,
	"BENEF_CODE"	VARCHAR(30) NOT NULL,
	"LMP"	NUMERIC NOT NULL,
	"TRANS_REF"	NUMERIC
);

--
--DELETE FROM patient_interview_master;
--
--CREATE TABLE "medicine_stock_history" (
--	"LOCATION_ID"	NUMERIC,
--	"USER_ID"	NUMERIC,
--	"MEDICINE_ID"	NUMERIC,
--	"CURRENT_STOCK_QTY"	NUMERIC,
--	"LAST_UPDATE_ON"	NUMERIC,
--	"LAST_SAVING_ON"	NUMERIC
--);
--
--CREATE TABLE "satellite_session" (
--	"SAT_SESSION_ID" INTEGER PRIMARY KEY AUTOINCREMENT,
--	"SAT_SESSION_DATE"	TEXT,
--	"USER_ID"	NUMERIC,
--	"SAT_SESSION_LOCATION_ID"	NUMERIC,
--	"CREATE_DATE"	NUMERIC,
--	"UPDATE_DATE"	NUMERIC,
--	"DATA_SENT"	TEXT,
--	"VERSION_NO"	TEXT,
--	"STATE"	NUMERIC(2)
--);
--
--
--CREATE TABLE "satellite_session_chw" (
--	"SAT_SESSION_CHW_ID" INTEGER PRIMARY KEY AUTOINCREMENT,
--	"SAT_SESSION_ID"	NUMERIC,
--	"USER_ID"	NUMERIC,
--	"LOCATION_ID"	NUMERIC
--);



PRAGMA foreign_keys=on;
COMMIT;