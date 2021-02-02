package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;

import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {

    private DBHandler dbHandler;

    public PersistentTransactionDAO(Context context){
        dbHandler = new DBHandler(context);
    }

    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount){
        dbHandler.logTransaction(date, accountNo, expenseType, amount);
    }


    public List<Transaction> getAllTransactionLogs(){
        return dbHandler.getAllTransactionLogs();
    }


    public List<Transaction> getPaginatedTransactionLogs(int limit){
        return dbHandler.getPaginatedTransactionLogs(limit);
    }
}
