package com.jk.admin.myexpense.Classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jk.admin.myexpense.Modal.Balance;
import com.jk.admin.myexpense.Modal.Expense;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExpenseDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyExpense.db";
    public static final String TABLE_NAME_EXPENSE = "Expense";
    public static final String COLUMN_ID = "Id";
    public static final String COLUMN_DATE = "ExpenseDate";
    public static final String COLUMN_AMOUNT = "Amount";
    public static final String COLUMN_DESCRIPTION = "Description";
    public static final String COLUMN_FROM = "ExpenseFrom";

    public String TABLE_NAME_BALANCE="Balance";
//    public String COLOUMN_ID="Id";
//    public String COLOUMN_BALANCETYPE="BalanceType";
//    public String COLOUMN_BALANCE="BalanceAmount";

    public Context context;

    public ExpenseDBHelper(Context context) {

        super(context, DATABASE_NAME, null, 1);
        //this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("create table Expense(Id integer primary key autoincrement,ExpenseDate text,Amount float,Description text,ExpenseFrom text);");
        sqLiteDatabase.execSQL("create table Balance(Id integer primary key autoincrement,BalanceType text,BalanceAmount float);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean AddExpense(Expense expense)
    {
        SQLiteDatabase databaseread=getReadableDatabase();
        SQLiteDatabase database=getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(COLUMN_DATE,expense.getDate());
        contentValues.put(COLUMN_AMOUNT,expense.getAmount());
        contentValues.put(COLUMN_DESCRIPTION,expense.getDescription());
        contentValues.put(COLUMN_FROM,expense.getFrom());
        long id=database.insert(TABLE_NAME_EXPENSE,null,contentValues);
        if(id!=-1)
        {
            Cursor cursor=databaseread.rawQuery("select BalanceAmount from "+TABLE_NAME_BALANCE+" where BalanceType='"+expense.getFrom()+"';",null);
            cursor.moveToFirst();
            float bal=cursor.getFloat(0);
            float remBal=bal-Float.parseFloat(expense.getAmount());
//            String remBalS= ((String) remBal);

            database.execSQL("update "+TABLE_NAME_BALANCE+" set BalanceAmount="+remBal+" where BalanceType='"+expense.getFrom()+"';");

            return true;
        }
        else
        {
            return false;
        }
    }
    public boolean IsBalEntry()
    {
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery("SELECT count(*) FROM "+TABLE_NAME_BALANCE+";",null);
        cursor.moveToFirst();
        int count=cursor.getInt(0);
        if(count==0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    public List<Balance> GetBalance()
    {
        List<Balance> list=new ArrayList<>();

        SQLiteDatabase database=getReadableDatabase();
        Cursor cursor=database.rawQuery("select * from "+TABLE_NAME_BALANCE+";",null);

        if(cursor!=null)
        {
            cursor.moveToFirst();
            do
            {
                Balance balance=new Balance();
                balance.setBalanceType(cursor.getString(1));
                balance.setBalance(cursor.getString(2));
                list.add(balance);
            }while (cursor.moveToNext());
        }
        return list;
    }
    public boolean AddBalanceCash(String Amount)
    {
        SQLiteDatabase databaseread=getReadableDatabase();
        SQLiteDatabase databasewrite=getWritableDatabase();

        Cursor cursor=databaseread.rawQuery("select * from "+TABLE_NAME_BALANCE+" where BalanceType='CA'",null);
        cursor.moveToFirst();
        int count=cursor.getCount();
        if(count==0)
        {
            ContentValues contentValues=new ContentValues();
            contentValues.put("BalanceType","CA");
            contentValues.put("BalanceAmount",Amount);
            long id=databasewrite.insert(TABLE_NAME_BALANCE,null,contentValues);
            if(id!=-1)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            databasewrite.execSQL("update "+TABLE_NAME_BALANCE+" set BalanceAmount="+Amount+" where BalanceType='CA'");
            return true;
        }
    }

    public boolean AddBalanceBank(String Amount)
    {
        SQLiteDatabase databaseread=getReadableDatabase();
        SQLiteDatabase databasewrite=getWritableDatabase();

        Cursor cursor=databaseread.rawQuery("select * from "+TABLE_NAME_BALANCE+" where BalanceType='BA'",null);
        cursor.moveToFirst();
        int count=cursor.getCount();
        if(count==0)
        {
            ContentValues contentValues=new ContentValues();
            contentValues.put("BalanceType","BA");
            contentValues.put("BalanceAmount",Amount);
            long id=databasewrite.insert(TABLE_NAME_BALANCE,null,contentValues);
            if(id!=-1)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            databasewrite.execSQL("update "+TABLE_NAME_BALANCE+" set BalanceAmount="+Amount+" where BalanceType='BA'");
            return true;
        }
    }

    public List<Expense> GetReport(String fromDate,String toDate)
    {
//        Date fromDT,toDT;
//        fromDT=fromDate;

        List<Expense> expenses1=new ArrayList<>();
//        List<Expense> expenseListfinal=new ArrayList<>();
        SQLiteDatabase database=getReadableDatabase();
       // Cursor cursor=database.rawQuery("select * from "+TABLE_NAME_EXPENSE+" where ExpenseDate between '21/08/2018' and '05/09/2018';",null);
        Cursor cursor=database.rawQuery("SELECT * FROM "+ TABLE_NAME_EXPENSE +" ORDER BY Id DESC;", null);
//        Cursor cursor=database.rawQuery("select * from "+TABLE_NAME_EXPENSE+" where ExpenseDate between '"+fromDate+"' and '"+toDate+"';",null);
        if(cursor!=null&&cursor.moveToFirst())
        {

            do {
                Expense expense=new Expense();

                expense.setId(cursor.getString(0));
                expense.setDate(cursor.getString(1));
                expense.setAmount(cursor.getString(2));
                expense.setDescription(cursor.getString(3));
                expense.setFrom(cursor.getString(4));
                expenses1.add(expense);

            }while (cursor.moveToNext());
        }



        return expenses1;
    }

    public boolean DeleteExpense(String id,String from,String amount)
    {
        Float remBal= Float.valueOf(0);

        SQLiteDatabase databaseRead=getReadableDatabase();
        SQLiteDatabase databaseWrite=getWritableDatabase();
        databaseWrite.execSQL("delete from "+TABLE_NAME_EXPENSE+" where Id='"+id+"';" );

        Cursor cursor=databaseRead.rawQuery("select BalanceAmount from "+TABLE_NAME_BALANCE+" where BalanceType='"+from+"';",null);
        if(cursor!=null&&cursor.moveToFirst())
        {
            remBal=cursor.getFloat(0)+Float.parseFloat(amount);
        }
        databaseWrite.execSQL("update "+TABLE_NAME_BALANCE+" set BalanceAmount="+remBal+" where BalanceType='"+from+"';");

        return true;

    }

    public  List<Expense> GetTodayExpenses(String date)
    {
        List<Expense> list=new ArrayList<>();
        SQLiteDatabase databaseRead=getReadableDatabase();
        Cursor cursor=databaseRead.rawQuery("select * from "+TABLE_NAME_EXPENSE+" where ExpenseDate='"+date+"';",null);

        if(cursor!=null&&cursor.moveToFirst())
        {

            do{
                Expense expense=new Expense();
                expense.setId(cursor.getString(0));
                expense.setDate(cursor.getString(1));
                expense.setAmount(cursor.getString(2));
                expense.setDescription(cursor.getString(3));
                expense.setFrom(cursor.getString(4));

                list.add(expense);

            }while (cursor.moveToNext());
        }

        return list;
    }
}
