DECLARE 
  I_CNT INTEGER;
  V_SQL VARCHAR2(32767);
  
BEGIN 

  I_CNT:=1;
  V_SQL:='TRUNCATE TABLE CUST_INFO DROP STORAGE';
  EXECUTE IMMEDIATE V_SQL;

  V_SQL:='TRUNCATE TABLE CUST_BALANCE DROP STORAGE';
  EXECUTE IMMEDIATE V_SQL;

  V_SQL:='ALTER TABLE CDMS.CUST_INFO DROP PRIMARY KEY CASCADE';
  EXECUTE IMMEDIATE V_SQL;

  V_SQL:='ALTER TABLE CDMS.CUST_BALANCE DROP PRIMARY KEY CASCADE';
  EXECUTE IMMEDIATE V_SQL;
  
  LOOP 
  
  INSERT INTO CUST_INFO    (CUST_NO, CUST_NAME, CUST_PASSWORD, CUST_SEX, PHONE_NUMBER, STATUS) VALUES   (TO_CHAR(I_CNT), '????'||TO_CHAR(I_CNT), '123', '??', '120',  '1');
  INSERT INTO CUST_BALANCE (CUST_NO, CURRENCY, BALANCE, BALANCE_AVL) VALUES (TO_CHAR(I_CNT), 'RMB', 10000000000, 10000000000);
  
  IF MOD(I_CNT,10000)=0 THEN
  	COMMIT; 
	END IF;
  
  I_CNT:=I_CNT+1;
  EXIT WHEN I_CNT>=10000*100;
  
  END LOOP;
  

  V_SQL:='ALTER TABLE CDMS.CUST_INFO ADD (CONSTRAINT PK_CUST_INFO PRIMARY KEY (CUST_NO))';
  EXECUTE IMMEDIATE V_SQL;

  V_SQL:='ALTER TABLE CDMS.CUST_BALANCE ADD (CONSTRAINT PK_CUST_BALANCE PRIMARY KEY (CUST_NO, CURRENCY))';
  EXECUTE IMMEDIATE V_SQL;

END;
/