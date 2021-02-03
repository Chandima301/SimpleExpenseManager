package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.AccountAlreadyExistsException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InsufficientBalanceException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class DBHandler extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    //Database Version
    private static final int VERSION = 1;

    //Database Name
    private static final String DB_NAME = "180029V";

    //Table Names
    private static final String TABLE_ACCOUNT = "account";
    private static final String TABLE_TRANSACTION = "transactions";

    //Common column names
    private static final String KEY_ACCOUNT_NO = "accountNo";

    //ACCOUNT table - column names
    private static final String KEY_BANK_NAME = "bankName";
    private static final String KEY_ACCOUNT_HOLDER_NAME = "accountHolderName";
    private static final String KEY_BALANCE = "balance";

    //TRANSACTION table - column names
    private static final String KEY_TRANSACTION_ID = "transactionId";
    private static final String KEY_EXPENSE_TYPE = "expenseType";
    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_DATE = "date";

    //ACCOUNT table create statement
    private static final String CREATE_TABLE_ACCOUNT = "CREATE TABLE "
            + TABLE_ACCOUNT + "("
            + KEY_ACCOUNT_NO + " TEXT PRIMARY KEY,"
            + KEY_BANK_NAME + " TEXT,"
            + KEY_ACCOUNT_HOLDER_NAME + " TEXT,"
            + KEY_BALANCE + " REAL" + ")";

    //TRANSACTION table create statement
    private static final String CREATE_TABLE_TRANSACTION = "CREATE TABLE "
            + TABLE_TRANSACTION + "("
            + KEY_TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_ACCOUNT_NO + " TEXT,"
            + KEY_EXPENSE_TYPE + " TEXT,"
            + KEY_AMOUNT + " REAL,"
            + KEY_DATE + " TEXT" + ")";


    public DBHandler(@Nullable Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_ACCOUNT);
        sqLiteDatabase.execSQL(CREATE_TABLE_TRANSACTION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // on upgrade drop older tables
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION);

        // create new tables
        onCreate(sqLiteDatabase);
    }


    public List<String> getAccountNumbersList() {
        List<String> accountNumbers = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + KEY_ACCOUNT_NO + " FROM " + TABLE_ACCOUNT;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                // adding to accountNumbers list
                accountNumbers.add(c.getString(c.getColumnIndex(KEY_ACCOUNT_NO)));
            } while (c.moveToNext());
        }

        return accountNumbers;
    }

    public List<Account> getAccountsList() {
        List<Account> accounts = new ArrayList<Account>();

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_ACCOUNT;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);


        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Account account = new Account(
                        c.getString(c.getColumnIndex(KEY_ACCOUNT_NO)),
                        c.getString(c.getColumnIndex(KEY_BANK_NAME)),
                        c.getString(c.getColumnIndex(KEY_ACCOUNT_HOLDER_NAME)),
                        c.getDouble(c.getColumnIndex(KEY_BALANCE))
                );
                // adding to accountNumbers list
                accounts.add(account);
            } while (c.moveToNext());
        }
        return accounts;
    }

    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_ACCOUNT + " WHERE "
                + KEY_ACCOUNT_NO + " = ?";
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, new String[]{accountNo});

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            Account account = new Account(
                    c.getString(c.getColumnIndex(KEY_ACCOUNT_NO)),
                    c.getString(c.getColumnIndex(KEY_BANK_NAME)),
                    c.getString(c.getColumnIndex(KEY_ACCOUNT_HOLDER_NAME)),
                    c.getDouble(c.getColumnIndex(KEY_BALANCE))
            );

            return account;

        }

        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);

    }

    public void addAccount(Account account) throws AccountAlreadyExistsException {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ACCOUNT_NO, account.getAccountNo());
        values.put(KEY_BANK_NAME, account.getBankName());
        values.put(KEY_ACCOUNT_HOLDER_NAME, account.getAccountHolderName());
        values.put(KEY_BALANCE, account.getBalance());
        Account acc = null;
        try {
            acc = getAccount(account.getAccountNo());
        } catch (InvalidAccountException e) {
            // insert row
            db.insert(TABLE_ACCOUNT, null, values);
        }
        String msg = "Account number " + account.getAccountNo() + " already exists";
        if (acc != null) throw new AccountAlreadyExistsException(msg);

    }

    public void removeAccount(String accountNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACCOUNT, KEY_ACCOUNT_NO + " = ?",
                new String[]{accountNo});
    }

    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException, InsufficientBalanceException {

        Account acc = getAccount(accountNo);
        Double balance = acc.getBalance();
        SQLiteDatabase dbWrite = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        switch (expenseType) {
            case EXPENSE:
                String msg = "Your Account balance " + balance + " is not sufficient";
                if (balance - amount < 0) throw new InsufficientBalanceException(msg);
                values.put(KEY_BALANCE, balance - amount);
                break;
            case INCOME:
                values.put(KEY_BALANCE, balance + amount);
                break;
        }
        // updating row
        dbWrite.update(TABLE_ACCOUNT, values, KEY_ACCOUNT_NO + " = ?",
                new String[]{accountNo});

    }

    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String strDate = dateFormat.format(date);

        ContentValues values = new ContentValues();
        values.put(KEY_ACCOUNT_NO, accountNo);
        values.put(KEY_EXPENSE_TYPE, expenseType.toString());
        values.put(KEY_AMOUNT, amount);
        values.put(KEY_DATE, strDate);

        // insert row
        db.insert(TABLE_TRANSACTION, null, values);

    }

    public List<Transaction> getAllTransactionLogs() {
        List<Transaction> transactions = new ArrayList<Transaction>();

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_TRANSACTION;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");


        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Date date = null;
                try {
                    date = dateFormat.parse(c.getString(c.getColumnIndex(KEY_DATE)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String stringExpenseType = c.getString(c.getColumnIndex(KEY_EXPENSE_TYPE));
                ExpenseType expenseType = null;
                switch (stringExpenseType) {
                    case "EXPENSE":
                        expenseType = ExpenseType.EXPENSE;
                        break;
                    case "INCOME":
                        expenseType = ExpenseType.INCOME;
                        break;
                }
                Transaction transaction = new Transaction(
                        date,
                        c.getString(c.getColumnIndex(KEY_ACCOUNT_NO)),
                        expenseType,
                        c.getDouble(c.getColumnIndex(KEY_AMOUNT))
                );
                // adding to accountNumbers list
                transactions.add(transaction);
            } while (c.moveToNext());
        }
        return transactions;
    }

    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> transactions = getAllTransactionLogs();

        int size = transactions.size();
        if (size <= limit) {
            return transactions;
        }
        // return the last <code>limit</code> number of transaction logs
        return transactions.subList(size - limit, size);
    }

}
