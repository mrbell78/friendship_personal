
BEGIN;

--CREATE TABLE "medicine_consumption_detail_delete" (
--	"MED_CONSUMP_DTL_ID"	INT,
--	"MED_CONSUMP_ID"	NUM,
--	"MEDICINE_ID"	NUM,
--	"QTY"	NUM,
--	"PRICE"	NUM
--);
--
--CREATE TABLE "medicine_consumption_master_delete" (
--	"INTERVIEW_ID"	NUM,
--	"BENEF_CODE"	TEXT,
--	"MED_CONSUMP_ID"	INT,
--	"CONSUMP_DATE"	NUM,
--	"LOCATION_ID"	NUM,
--	"USER_ID"	TEXT,
--	"TOTAL_PRICE"	NUM,
--	"REMARKS"	TEXT,
--	"DATA_SENT"	TEXT
--);
--
--CREATE TABLE "patient_interview_detail_delete" (
--	"INTERVIEW_DTL_ID"	INT,
--	"INTERVIEW_ID"	INT,
--	"Q_ID"	INT,
--	"ANSWER"	TEXT,
--	"TRANS_REF"	NUM
--);
--
--CREATE TABLE "patient_interview_master_delete" (
--	"INTERVIEW_ID"	INT,
--	"FILE_PATH"	TEXT,
--	"PARENT_INTERVIEW_ID"	NUM,
--	"REF_CENTER_ID"	NUM,
--	"DURATION"	NUM,
--	"START_TIME"	NUM,
--	"DATA_SENT"	TEXT,
--	"REF_DATA_ID"	NUM,
--	"STATUS"	TEXT,
--	"NEXT_FOLLOWUP_DATE"	NUM,
--	"PRIORITY"	NUM,
--	"UPDATED_ON"	NUM,
--	"BENEF_CODE"	TEXT,
--	"USER_ID"	NUM,
--	"CREATE_DATE"	NUM,
--	"QUESTIONNAIRE_ID"	NUM,
--	"FILE_KEY"	TEXT,
--	"QUESTION_ANSWER_JSON"	TEXT,
--	"PRESC_ID"	NUM,
--	"ADVICE_FLAG"	NUM,
--	"TRANS_REF"	NUM,
--	"IS_FEEDBACK"	INT,
--	"BENEF_NAME"	TEXT,
--	"FCM_INTERVIEW_ID"	INT,
--	"BENEF_ADDRESS"	TEXT,
--	"AGE_IN_DAY"	INT,
--	"GENDER"	TEXT
--);
--
--CREATE TABLE "couple_registration" (
--	"COUPLE_ID"	INTEGER UNIQUE,
--	"BENEF_ID"	INTEGER,
--	"BENEF_CODE"	VARCHAR(30) NOT NULL,
--	"HUSBAND_BENEF_CODE"	VARCHAR(30) NOT NULL,
--	"WIFE_BENEF_CODE"	VARCHAR(30) NOT NULL,
--	"MARRIAGE_DATE"	TEXT NOT NULL,
--	"TT_INFO"	VARCHAR(200),
--	"LIVING_CHILDREN"	VARCHAR(30),
--	"AGE_FIRST_PREGNANCY"	VARCHAR(30),
--	"CREATE_DATE"	 INTEGER,
--	"REG_DATE"	INTEGER,
--	"STATE"	NUMERIC DEFAULT 1,
--	"TRANS_REF"	INTEGER,
--	"VERSION_NO"	INTEGER,
--	"COUPLE_CODE"	TEXT UNIQUE,
--	"UPDATE_DATA_SENT"	TEXT,
--	PRIMARY KEY("COUPLE_ID" AUTOINCREMENT),
--	UNIQUE("HUSBAND_BENEF_CODE","WIFE_BENEF_CODE")
--);




--ALTER TABLE medicine_consumption_master ADD COLUMN VERSION_NO INTEGER ;
--ALTER TABLE medicine_adjustment_master ADD COLUMN VERSION_NO INTEGER ;
--ALTER TABLE medicine_receive_master ADD COLUMN VERSION_NO INTEGER ;
--ALTER TABLE medicine_requisition_master ADD COLUMN VERSION_NO INTEGER ;
--ALTER TABLE medicine_consumption_master_delete ADD COLUMN VERSION_NO INTEGER ;
--ALTER TABLE patient_interview_master ADD COLUMN RECORD_DATE TEXT;
----
--CREATE TABLE "medicine_stock_history" (
--	"LOCATION_ID"	NUMERIC,
--	"USER_ID"	NUMERIC,
--	"MEDICINE_ID"	NUMERIC,
--	"CURRENT_STOCK_QTY"	NUMERIC,
--	"LAST_UPDATE_ON"	NUMERIC,
--	"LAST_SAVING_ON"	NUMERIC
--);

--DELETE FROM patient_interview_detail_delete;
--DELETE FROM patient_interview_master_delete;
--DELETE FROM medicine_consumption_master_delete;
--DELETE FROM medicine_consumption_detail_delete;

COMMIT;
