package com.jk.admin.myexpense.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jk.admin.myexpense.Classes.ExpenseDBHelper;
import com.jk.admin.myexpense.Modal.Expense;
import com.jk.admin.myexpense.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FragmentTodayExpense extends Fragment {

    TextView totalExpTodayRptTv;
    RecyclerView todayRptRv;
    List<Expense> expenseList;
    TodayRptAdapter todayRptAdapter;
    ExpenseDBHelper expenseDBHelper;
    float TotalExp;
    //SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_today_expense_layout,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        totalExpTodayRptTv =(TextView) view.findViewById(R.id.TodayRptTotalExpTv);
        todayRptRv =(RecyclerView) view.findViewById(R.id.TodayRptRv);

        expenseDBHelper=new ExpenseDBHelper(getActivity());

        String date= new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault()).format(new Date());
        expenseList = expenseDBHelper.GetTodayExpenses(date);

        if(expenseList!=null)
        {
            todayRptAdapter=new TodayRptAdapter(getActivity(),expenseList);
            todayRptRv.setAdapter(todayRptAdapter);
            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            todayRptRv.setLayoutManager(linearLayoutManager);

            CalculateTotalExp();


        }
        else
        {
            Toast.makeText(getActivity(), "No Expense For Today.", Toast.LENGTH_LONG).show();
        }




    }

    private void CalculateTotalExp() {
        TotalExp=(float) 0.0;
        for(int i=0;i<expenseList.size();i++)
        {
            TotalExp=TotalExp+Float.parseFloat(expenseList.get(i).getAmount());
        }
        totalExpTodayRptTv.setText("Total Expense:- "+String.valueOf(TotalExp)+" $");
    }

    public class TodayRptViewHolder extends RecyclerView.ViewHolder
    {
        TextView todayReportRvDateTv,todayReportRvDescTv,todayReportRvAmountTv;
        ImageView todayReportRvFromIv,todayReportRvDeleteBtn;

        public TodayRptViewHolder(View itemView) {
            super(itemView);

            todayReportRvDateTv=(TextView) itemView.findViewById(R.id.TodayReportRvDateTv);
            todayReportRvFromIv=(ImageView) itemView.findViewById(R.id.TodayReportRvFromIv);
            todayReportRvDescTv=(TextView) itemView.findViewById(R.id.TodayReportRvDescTv);
            todayReportRvAmountTv=(TextView) itemView.findViewById(R.id.TodayReportRvAmountTv);
            todayReportRvDeleteBtn=(ImageView) itemView.findViewById(R.id.TodayReportRvDeleteBrn);
        }
    }

    class TodayRptAdapter extends RecyclerView.Adapter<TodayRptViewHolder>
    {
        List<Expense> expenseList;
        Context context;

        public TodayRptAdapter(Context context, List<Expense> expenseList) {
            this.context=context;
            this.expenseList=expenseList;
        }

        @NonNull
        @Override
        public TodayRptViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=getActivity().getLayoutInflater().inflate(R.layout.today_report_rv_raw,parent,false);

            TodayRptViewHolder holder=new TodayRptViewHolder(view);


            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull TodayRptViewHolder holder,final int position) {


            final Expense expense=expenseList.get(position);
            holder.todayReportRvDateTv.setText("Date:- "+expense.getDate());
            holder.todayReportRvDescTv.setText("Details:- "+expense.getDescription());
            holder.todayReportRvFromIv.setImageResource(expense.getFrom().equals("CA")?R.drawable.cash:R.drawable.ic_account_balance_black_24dp);
            holder.todayReportRvAmountTv.setText(expense.getAmount()+" $");

            holder.todayReportRvDeleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OpenDialog(expense.getId(),expense.getFrom(),expense.getAmount(),position);
                }
            });

        }

        @Override
        public int getItemCount() {
            return expenseList.size();
        }
    }

    private void OpenDialog(final String id, final String from, final String amount, final int position) {

        //Toast.makeText(getActivity(), ""+from, Toast.LENGTH_SHORT).show();

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete ?");
        builder.setMessage("Are you sure you want to delete this entry ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                expenseDBHelper.DeleteExpense(id,from,amount);

                expenseList.remove(position);
                todayRptAdapter.notifyDataSetChanged();
                CalculateTotalExp();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).create().show();

    }

}
