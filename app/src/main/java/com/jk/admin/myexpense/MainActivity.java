package com.jk.admin.myexpense;

import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jk.admin.myexpense.Classes.ExpenseDBHelper;
import com.jk.admin.myexpense.Fragments.FragmentAddExpense;
import com.jk.admin.myexpense.Fragments.FragmentReport;
import com.jk.admin.myexpense.Fragments.FragmentTodayExpense;
import com.jk.admin.myexpense.Modal.Balance;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tablayout;
    List<String> list;
    TextView cashBalTv,bankBalTv,totalBalTv;
    EditText cashBalEt,bankBalEt;
    ImageView editBtn,saveBtn,closeBtn;
    ExpenseDBHelper expenseDBHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager=(ViewPager) findViewById(R.id.ViewPager);
        tablayout=(TabLayout) findViewById(R.id.TabLayout);
        expenseDBHelper=new ExpenseDBHelper(this);
        list=new ArrayList<>();
        list.add(0,"Add Expense");
        list.add(1,"Report");
        list.add(2,"Today Expenses");

        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(),list));
        tablayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.BalMenu)
        {
            View view= getLayoutInflater().inflate(R.layout.balance_dialog,null,false);

            cashBalTv=(TextView) view.findViewById(R.id.CashBalTv);
            bankBalTv=(TextView) view.findViewById(R.id.BankBalTv);
            cashBalEt=(EditText) view.findViewById(R.id.CashBalEt);
            bankBalEt=(EditText)view.findViewById(R.id.BankBalEt);
            totalBalTv=(TextView) view.findViewById(R.id.TotalBalTv);
            editBtn=(ImageView) view.findViewById(R.id.EditBtn);
            saveBtn=(ImageView) view.findViewById(R.id.SaveBtn);

            closeBtn=(ImageView) view.findViewById(R.id.CloseBtn);

            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setView(view);
            builder.setCancelable(false);
            final AlertDialog dialog=builder.create();
            dialog.show();

            if(!expenseDBHelper.IsBalEntry())
            {
                cashBalTv.setVisibility(View.VISIBLE);
                cashBalTv.setText("0");
                bankBalTv.setVisibility(View.VISIBLE);
                bankBalTv.setText("0");
                editBtn.setVisibility(View.VISIBLE);
            }
            else
            {
                cashBalTv.setVisibility(View.VISIBLE);
                bankBalTv.setVisibility(View.VISIBLE);
                ShowBalance(cashBalTv,bankBalTv,totalBalTv);
            }

            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String cashBal=cashBalTv.getText().toString();
                    String bankBal=bankBalTv.getText().toString();
                    cashBalTv.setVisibility(View.GONE);
                    bankBalTv.setVisibility(View.GONE);
                    cashBalEt.setVisibility(View.VISIBLE);
                    bankBalEt.setVisibility(View.VISIBLE);
                    cashBalEt.setText(cashBal);
                    bankBalEt.setText(bankBal);
                    saveBtn.setVisibility(View.VISIBLE);
                }
            });

            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(cashBalEt.getText().toString().equals("")||cashBalEt.getText().toString().equals("0"))
                    {
                        cashBalEt.setError("Enter Amount.");
                    }
                    else if(bankBalEt.getText().toString().equals("")||bankBalEt.getText().toString().equals("0"))
                    {
                        bankBalEt.setError("Enter Amount");
                    }
                    else
                    {
                        if(expenseDBHelper.AddBalanceBank(bankBalEt.getText().toString())&&expenseDBHelper.AddBalanceCash(cashBalEt.getText().toString()))
                        {
                            cashBalEt.setVisibility(View.GONE);
                            bankBalEt.setVisibility(View.GONE);
                            cashBalTv.setVisibility(View.VISIBLE);
                            bankBalTv.setVisibility(View.VISIBLE);
                            saveBtn.setVisibility(View.GONE);

                            ShowBalance(cashBalTv,bankBalTv,totalBalTv);
                        }
                    }
                }
            });

            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    private void ShowBalance(TextView cashBalTv, TextView bankBalTv,TextView totalBalTv) {
//
//        TextView cashBalTv2,bankBalTv2;
//        cashBalTv2=this.cashBalTv;
//        bankBalTv2=this.bankBalTv;
        Float total;
        List<Balance> list=new ArrayList<>();
        list=expenseDBHelper.GetBalance();
        for(int i=0;i<list.size();i++)
        {
            Balance balance=new Balance();
            balance=list.get(i);

            //Toast.makeText(this, ""+balance.getBalanceType(), Toast.LENGTH_SHORT).show();
            if(balance.getBalanceType().equals("CA"))
            {
                cashBalTv.setText(balance.getBalance());
            }
            else
            {
                bankBalTv.setText(balance.getBalance());
            }
        }
        total=Float.parseFloat(cashBalTv.getText().toString())+Float.parseFloat(bankBalTv.getText().toString());
        totalBalTv.setText("Total Balance:-  "+total.toString()+"$");

    }

    class PagerAdapter extends FragmentPagerAdapter
    {

        List<String> list;
        public PagerAdapter(FragmentManager supportFragmentManager, List<String> list) {
            super(supportFragmentManager);
            this.list=list;
        }

        @Override
        public Fragment getItem(int position) {

            if(position==0)
            {
                return new FragmentAddExpense();
            }
            else if(position==1)
            {
                return new FragmentReport();
            }
            else if(position==2)
            {
                return new FragmentTodayExpense();
            }

            return null;

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return list.get(position);
        }
    }

}
