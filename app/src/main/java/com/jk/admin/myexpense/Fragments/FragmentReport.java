package com.jk.admin.myexpense.Fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
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
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jk.admin.myexpense.Classes.ExpenseDBHelper;
import com.jk.admin.myexpense.Modal.Expense;
import com.jk.admin.myexpense.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FragmentReport extends Fragment {

    TextView fromDateTv,toDateTv,totalExpTv;
    ImageView refreshBtn;
    RecyclerView reportRv;
    List<Expense> expenseList;
    ReportRvAdpter reportRvAdpter;
    ExpenseDBHelper expenseDBHelper;
    float  totalExp;

    Calendar calendar=Calendar.getInstance();
    SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report_layout,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final String todayDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        calendar.add(Calendar.DATE,-30);
        String beforedate=dateFormat.format(calendar.getTime());

        expenseDBHelper=new ExpenseDBHelper(getActivity());

        totalExp= (float) 0.0;

        fromDateTv=(TextView) view.findViewById(R.id.FromDateTv);
        toDateTv=(TextView) view.findViewById(R.id.ToDateTv);

        totalExpTv=(TextView) view.findViewById(R.id.TotalExpTv);
        refreshBtn=(ImageView) view.findViewById(R.id.RefreshBtn);
        reportRv=(RecyclerView) view.findViewById(R.id.ReportRv);
        expenseList=new ArrayList<>();

        expenseList=expenseDBHelper.GetReport(fromDateTv.getText().toString(),toDateTv.getText().toString());

        //Toast.makeText(getActivity(), ""+expenseList.get(0).getDate(), Toast.LENGTH_SHORT).show();

        for (int i=0;i<expenseList.size();i++)
        {
            totalExp= totalExp+Float.parseFloat(expenseList.get(i).getAmount());
        }
        totalExpTv.setText("Total Expense:- "+String.valueOf(totalExp)+" $");

        reportRvAdpter=new ReportRvAdpter(getActivity(),expenseList);
        reportRv.setAdapter(reportRvAdpter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        reportRv.setLayoutManager(linearLayoutManager);

        fromDateTv.setText(beforedate);
        toDateTv.setText(todayDate);

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                expenseList=expenseDBHelper.GetReport(fromDateTv.getText().toString(),toDateTv.getText().toString());

                //Toast.makeText(getActivity(), ""+expenseList.get(0).getDate(), Toast.LENGTH_SHORT).show();


                CalculateTotalExp();

                reportRvAdpter=new ReportRvAdpter(getActivity(),expenseList);
                reportRv.setAdapter(reportRvAdpter);
                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                reportRv.setLayoutManager(linearLayoutManager);
            }
        });

        final DatePickerDialog.OnDateSetListener fromdate = new DatePickerDialog.OnDateSetListener() {


            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //updateLabel();
                fromDateTv.setText(dateFormat.format(calendar.getTime()));
            }


        };

        final DatePickerDialog.OnDateSetListener todate = new DatePickerDialog.OnDateSetListener() {


            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //updateLabel();
                toDateTv.setText(dateFormat.format(calendar.getTime()));
            }


        };

        fromDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(),fromdate,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        toDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(),todate,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    void CalculateTotalExp() {
        totalExp=(float) 0.0;
        for(int i=0;i<expenseList.size();i++)
        {
            totalExp=totalExp+Float.parseFloat(expenseList.get(i).getAmount());
        }
        totalExpTv.setText("Total Expense:- "+String.valueOf(totalExp)+" $");
    }
    private void updateLabel() {

        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        fromDateTv.setText(sdf.format(calendar.getTime()));
        toDateTv.setText(sdf.format(calendar.getTime()));
    }

    class ReportRvViewHolder extends RecyclerView.ViewHolder
    {
        TextView reportRvDateTv,reportRvDescTv,reportRvAmountTv;
        ImageView reportRvFromIv,reportRvDeleteBtn;
        public ReportRvViewHolder(View itemView) {
            super(itemView);
            reportRvDateTv=(TextView) itemView.findViewById(R.id.ReportRvDateTv);
            reportRvFromIv=(ImageView) itemView.findViewById(R.id.ReportRvFromIv);
            reportRvDescTv=(TextView) itemView.findViewById(R.id.ReportRvDescTv);
            reportRvAmountTv=(TextView) itemView.findViewById(R.id.ReportRvAmountTv);
            reportRvDeleteBtn=(ImageView) itemView.findViewById(R.id.ReportRvDeleteBrn);
        }
    }

    class ReportRvAdpter extends RecyclerView.Adapter<ReportRvViewHolder>
    {
        Context context;
        List<Expense> expenseList;

        public ReportRvAdpter(Context context, List<Expense> expenseList) {
            this.context=context;
            this.expenseList=expenseList;
        }

        @NonNull
        @Override
        public ReportRvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view=LayoutInflater.from(context).inflate(R.layout.report_rv_raw,parent,false);
            ReportRvViewHolder holder=new ReportRvViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ReportRvViewHolder holder, final int position) {

            final Expense expense=expenseList.get(position);
            holder.reportRvDateTv.setText("Date:- "+expense.getDate());
            holder.reportRvDescTv.setText("Details:- "+expense.getDescription());
            holder.reportRvFromIv.setImageResource(expense.getFrom().equals("CA")?R.drawable.cash:R.drawable.ic_account_balance_black_24dp);
            holder.reportRvAmountTv.setText(expense.getAmount()+" $");

            holder.reportRvDeleteBtn.setOnClickListener(new View.OnClickListener() {
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
                reportRvAdpter.notifyDataSetChanged();
                CalculateTotalExp();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).create().show();

    }
}
