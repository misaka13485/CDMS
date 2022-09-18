DROP PROCEDURE CDMS.P_WRITE_LOG;

--
-- P_WRITE_LOG  (Procedure) 
--
CREATE OR REPLACE PROCEDURE CDMS.P_Write_Log (
       I_Proc_Id           In Varchar2,
       I_Log_Date          In Date,
       I_Log_Step          In Varchar2,
       I_Log_Msg           In Varchar2,
       I_Log_Amt1          In Number,       
       I_Log_Amt2          In Number 
   )
   As
      Pragma Autonomous_Transaction;
      L_Sqlerrm   Varchar ( 300 );
  Begin
      Begin
   
        Insert Into Cdms.T_Run_Log_Info (Proc_Id, Log_Date, Log_Step, Log_Msg, Log_Amt1, Log_Amt2) 
        Values ( I_Proc_Id, I_Log_Date,I_Log_Step,I_Log_Msg ,  I_Log_Amt1  , I_Log_Amt2 );
        
           
        Commit;
      Exception
         When Others Then
            Rollback;
            L_Sqlerrm := Substr ( Sqlerrm, 1, 200 );
            --Dbms_Output.Put_Line('写日志时发生错误:'||L_Sqlerrm);
            Rollback;
      End;

   End;
/


DROP PROCEDURE CDMS.P_WRITE_ORDER;

--
-- P_WRITE_ORDER  (Procedure) 
--
CREATE OR REPLACE PROCEDURE CDMS.P_Write_ORDER (
    I_OPERATE_TYPE IN     NUMBER,   --1增加 2修改 3删除
    I_OREDER_NO    IN     NUMBER,
    I_Cust_No      IN     NUMBER,
    I_Product_Id   IN     NUMBER,
    I_Buy_Amount   IN     NUMBER,
    I_STATUS       IN     NUMBER,
    O_RET_ID       OUT    NUMBER,
    O_RET_MSG      OUT    VARCHAR2)
   As
      Pragma Autonomous_Transaction;
      L_orader_no   NUMBER(20);
      L_Sqlerrm     VARCHAR2(300);
  Begin
      Begin
        IF I_OPERATE_TYPE = 1 THEN
            select Cdms.Se_Certificate_Of_Deposit_Order.Nextval into L_orader_no from dual;
            Insert Into Cdms.Certificate_Of_Deposit_Order ( Order_No, Cust_No, Amount, Order_Date, Certificates_Of_Deposit_Id , STATUS) 
            Values ( L_orader_no,I_Cust_No , I_Buy_Amount, Sysdate   ,I_Product_Id , I_STATUS);
        END IF;
        
        IF I_OPERATE_TYPE = 2 THEN
            Update Cdms.Certificate_Of_Deposit_Order set status = I_STATUS
            where order_no = I_OREDER_NO;
        END IF;
        
        IF I_OPERATE_TYPE = 3 THEN
            DELETE FROM Cdms.Certificate_Of_Deposit_Order WHERE ORDER_NO = I_OREDER_NO;
        END IF;
        
        Commit;
        
        O_RET_ID := L_orader_no;
        O_RET_MSG := '订单创建成功，订单号：'||to_char(L_orader_no);
        
      Exception
         When Others Then
            Rollback;
            L_Sqlerrm := Substr ( Sqlerrm, 1, 200 );
            --Dbms_Output.Put_Line('写日志时发生错误:'||L_Sqlerrm);
            O_RET_ID := -1;
            O_RET_MSG := '订单处理失败，错误信息：'||L_Sqlerrm||'操作码为'||to_char(I_OPERATE_TYPE);
            Rollback;
      End;

   End;
/


DROP PROCEDURE CDMS.PRO_BUY_PRODUCT;

--
-- PRO_BUY_PRODUCT  (Procedure) 
--
CREATE OR REPLACE PROCEDURE CDMS.Pro_Buy_Product
(
  I_Cust_No    In Varchar2,
  I_Product_Id In Varchar2,
  I_Buy_Amount In Number,
  O_Cwdm Out Number,
  O_Cwxx Out Varchar2
 )
  Is

  L_Product_Code     Varchar2(20);
  L_Product_Name     Varchar2(100);
  L_Currency         Varchar2(3);
  L_Cust_Status      Varchar2(12);
  L_PROD_Status      Varchar2(12);


  L_Balance          Number(32,2);
  L_Balance_Avl      Number(32,2);

  L_Total_Amount      Number(32,2);
  L_Residual_Amount   Number(32,2);
  L_Max_Amount_Per    Number(32,2);
  L_Min_Amount        Number(32,2);
  L_Start_Date        Date;
  L_End_Date          Date;

  L_Cust_Buy_Amount   Number(32,2);


  L_Sqlcode  Number;
  L_Sqlerrm  Varchar(4000);
  
  L_Lock_Retry_times       integer;
  L_Lock_Wait_Seconds      integer;  
  
  L_loop_cnt         integer;
    
  L_Exec_Init_Datetime     Date;           -- 存储过程执行的起始时间
  Nb_Step_Seconds Number;
  L_ORDER_NO number;
  L_RET_ID    NUMBER;
  L_RET_MSG VARCHAR2(300);
  
  
Begin


    L_Lock_Retry_times:=10000;
    L_Lock_Wait_Seconds:=3;
    
   /* 检查客户状态 */
    Begin
        Select Status
        Into L_Cust_Status
        From Cdms.Cust_Info
        Where Cust_No=I_Cust_No;
    Exception 
       When No_Data_Found Then
        O_Cwdm:=-1;
        O_Cwxx:='无效客户代码'||To_Char(I_Cust_No);
        Return;
       When Others Then
        O_Cwdm:=-1;
        O_Cwxx:='系统错误'||Sqlerrm;
        Return;
    End;    

    If L_Cust_Status<>'1' Then
        O_Cwdm:=-1;
        O_Cwxx:='客户状态异常：'||L_Cust_Status;
        Return;
    End If;


    L_loop_cnt:=0;
    L_Exec_Init_Datetime:=sysdate;
    
    Loop

       /* 检查产品相关信息 */
        Begin
            Select Certificates_Of_Deposit_Id,Certificates_Of_Deposit_Name,CURRENCY,Total_Amount,Residual_Amount,Max_Amount_Per,Min_Amount,Start_Date,End_Date,Status
            Into L_Product_Code,L_Product_Name,L_CURRENCY,L_Total_Amount,L_Residual_Amount,L_Max_Amount_Per,L_Min_Amount,L_Start_Date,L_End_Date,L_Prod_Status
            From Cdms.Certificate_Of_Deposit_Info
            Where Certificates_Of_Deposit_Id=I_Product_Id
            For Update Nowait;
            
            if L_loop_cnt>0 then
                P_Write_Log(userenv('sid'),sysdate,'产品','锁定产品表成功退出',Round((Sysdate - L_Exec_Init_Datetime) * 24 * 3600,0),L_Loop_Cnt);
            end if;

            exit;

        Exception 
           When No_Data_Found Then
            O_Cwdm:=-1;
            O_Cwxx:='无效产品代码'||I_Product_Id;
            Rollback;
            Return;
           When Others Then
              L_Sqlcode:=Sqlcode;
              L_Sqlerrm:=Sqlerrm;
              If L_Sqlcode = -54      Then          --如果锁定记录失败，被其他进程加锁后会返回 Ora-00054 错误 ）
                    L_Loop_Cnt:=L_Loop_Cnt+1;
                    Continue;        
              Else
                    O_Cwdm:=-1;
                    O_Cwxx:='锁定产品信息失败：'||Sqlerrm;
                    Rollback;
                    Return;
             End If;  
        End;    
       
       
       Nb_Step_Seconds := Round((Sysdate - L_Exec_Init_Datetime) * 24 * 3600,0);
       Exit When L_Loop_Cnt<L_Lock_Retry_Times or Nb_Step_Seconds>L_Lock_Wait_Seconds;
   
    End Loop;
   
   if L_Loop_Cnt>=L_Lock_Retry_Times then
        O_Cwdm:=-1;
        O_Cwxx:='锁定产品信息失败，被其他进程已锁定';
        P_Write_Log(userenv('sid'),sysdate,'产品','锁定产品表失败退出',Round((Sysdate - L_Exec_Init_Datetime) * 24 * 3600,0),L_Loop_Cnt);
        Return;
   end if;
   
   
    If L_PROD_Status<>'1' Then
        O_Cwdm:=-1;
        O_Cwxx:='产品['||L_Product_Code||' '||L_Product_Name||']状态异常：'||L_PROD_Status;
        Rollback;        
        Return;
    End If;
   
 
    If L_Start_Date>Sysdate Then
        O_Cwdm:=-1;
        O_Cwxx:='产品['||L_Product_Code||' '||L_Product_Name||']还未开始销售';
        Rollback;        
        Return;
    End If;

    If L_End_Date<Sysdate Then
        O_Cwdm:=-1;
        O_Cwxx:='产品['||L_Product_Code||' '||L_Product_Name||']已经停止销售';
        Rollback;        
        Return;
    End If;

    If L_Residual_Amount>L_Total_Amount Then
        O_Cwdm:=-1;
        O_Cwxx:='产品['||L_Product_Code||' '||L_Product_Name||']已售额度大于产品发行总额';
        Rollback;        
        Return;
    End If;

    If L_Residual_Amount<=0 Then
        O_Cwdm:=-1;
        O_Cwxx:='产品['||L_Product_Code||' '||L_Product_Name||']已可售金额已经等于0';
        Rollback;        
        Return;
    End If;

    If I_Buy_Amount<L_Min_Amount Then
        O_Cwdm:=-1;
        O_Cwxx:='产品['||L_Product_Code||' '||L_Product_Name||']单笔购买金额'||To_Char(I_Buy_Amount)||'小于该产品最小金额：'||To_Char(L_Min_Amount)||'元';
        Rollback;        
        Return;
    End If;


    If L_Residual_Amount<I_Buy_Amount Then
        O_Cwdm:=-1;
        O_Cwxx:='产品可售金额'||To_Char(L_Residual_Amount)||'已小于本次购买金额：'||To_Char(I_Buy_Amount)||'元';
        Rollback;        
        Return;
    End If;



   /* 检查客户已购买产品总金额 */
    Begin
        Select Sum(Amount) As Amount
        Into L_Cust_Buy_Amount
        From Cdms.Certificate_Of_Deposit_Order
        Where Cust_No=I_Cust_No AND Certificates_Of_Deposit_Id=I_Product_Id AND STATUS = 1 ;
    Exception 
       When Others Then
        L_Cust_Buy_Amount:=0;
    End;    

    If L_Cust_Buy_Amount+I_Buy_Amount>L_Max_Amount_Per Then
        O_Cwdm:=-1;
        O_Cwxx:='客户已购买总金额：'||To_Char(L_Cust_Buy_Amount)||'元，本次购买金额：'||To_Char(I_Buy_Amount)||'元，已经大于该产品最个人最大购买金额：'||To_Char(L_Max_Amount_Per)||'元';
        Rollback;        
        Return;
    End If;


    /* 以上购买准入检查完成.进行订单处理 */

    /* 第一步锁定客户余额及可用余额 */
   /* 检查客户币种及可用余额 */
           /* 写入订单表 */
    CDMS.P_Write_ORDER(1,0,I_Cust_No,I_Product_Id,I_Buy_Amount,0,L_RET_ID,L_RET_MSG);
    
    IF L_RET_ID > 0 THEN
        L_ORDER_NO := L_RET_ID;
    ELSE
        O_Cwdm:=-1;
        O_Cwxx:=L_RET_MSG;
        Rollback;       
        Return;
    End If;
    

           
       
    L_loop_cnt:=0;
    
    
    Loop
   
        Begin
            Select Balance,Balance_Avl
            Into L_Balance,L_Balance_Avl
            From Cdms.Cust_Balance
            Where Cust_No=I_Cust_No And Currency=L_Currency 
            For Update Nowait;

            if L_loop_cnt>0 then
                P_Write_Log(userenv('sid'),sysdate,'余额','锁定余额表成功退出',Round((Sysdate - L_Exec_Init_Datetime) * 24 * 3600,0),L_Loop_Cnt);
            end if;

            exit;  --锁定成功则退出循环

        Exception 
           When No_Data_Found Then
            O_Cwdm:=-1;
            O_Cwxx:='客户'||To_Char(I_Cust_No)||'['||L_Currency||']币种未开户';
            Rollback;
            Return;
           When Others Then
              L_Sqlcode:=Sqlcode;
              L_Sqlerrm:=Sqlerrm;
              If L_Sqlcode = -54      Then          --如果锁定记录失败，被其他进程加锁后会返回 Ora-00054 错误 ）
                    L_Loop_Cnt:=L_Loop_Cnt+1;
                    Continue;                
              Else
                    O_Cwdm:=-1;
                    O_Cwxx:='锁定客户余额失败：'||Sqlerrm;
                    Rollback;
                    Return;
             End If;  
        End;      
    
       Exit When L_Loop_Cnt<L_Lock_Retry_Times or Nb_Step_Seconds>L_Lock_Wait_Seconds;
   
   End Loop;
    
   if L_Loop_Cnt>=L_Lock_Retry_Times then
        O_Cwdm:=-1;
        O_Cwxx:='锁定客户余额失败，被其他进程已锁定';
        P_Write_Log(userenv('sid'),sysdate,'余额','锁定余额表失败退出',Round((Sysdate - L_Exec_Init_Datetime) * 24 * 3600,0),L_Loop_Cnt);
        Return;
   end if;
    

    If L_Balance_Avl<I_Buy_Amount Then
        O_Cwdm:=-1;
        O_Cwxx:='客户账户可用金额不足：'||To_Char(L_Balance_Avl);
        Rollback;        
        Return;
    End If;

   
    Begin



        /* 计减产品可售余额 */      
        Update Cdms.Certificate_Of_Deposit_Info Set Residual_Amount=Residual_Amount-I_Buy_Amount
        Where Certificates_Of_Deposit_Id=I_Product_Id;

        /* 计减客户余额及可用余额 */
        Update Cdms.Cust_Balance Set Balance_Avl=Balance_Avl-I_Buy_Amount,balance=balance-I_Buy_Amount
        Where Cust_No=I_Cust_No And Currency=L_Currency;
        
        /*更新当前订单状态为成功（1） */
     CDMS.P_Write_ORDER(2,L_ORDER_NO,I_Cust_No,I_Product_Id,I_Buy_Amount,1,L_RET_ID,L_RET_MSG);
    
    IF L_ORDER_NO = -1 THEN
        O_Cwdm:=-1;
        O_Cwxx:=L_RET_MSG;
        Rollback;       
        Return;
    End If;
    
        Commit;

        O_Cwdm:=100;
        O_Cwxx:='购买产品：'||L_Product_Name||' 金额：'||to_char(I_Buy_Amount)||' 成功';
    
    Exception 
       When Others Then
                O_Cwdm:=-1;
                O_Cwxx:='订单处理失败：'||Sqlerrm;
                Rollback;
    End;      




End;
/
