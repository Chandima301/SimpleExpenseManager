package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.ExpenseManager;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.AccountAlreadyExistsException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InsufficientBalanceException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.ui.MainActivity;

public class PersistentAccountDAO implements AccountDAO {
    private DBHandler dbHandler;

    public PersistentAccountDAO(Context context){
        dbHandler = new DBHandler(context);
    }

    @Override
    public List<String> getAccountNumbersList() {
        return dbHandler.getAccountNumbersList();
    }

    @Override
    public List<Account> getAccountsList() {
        return dbHandler.getAccountsList();
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        return dbHandler.getAccount(accountNo);
    }

    @Override
    public void addAccount(Account account)throws AccountAlreadyExistsException {
        dbHandler.addAccount(account);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        dbHandler.removeAccount(accountNo);
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException, InsufficientBalanceException {
        dbHandler.updateBalance(accountNo,expenseType,amount);
    }
}
