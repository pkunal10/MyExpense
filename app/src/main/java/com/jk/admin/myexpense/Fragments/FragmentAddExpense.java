package com.jk.admin.myexpense.Fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jk.admin.myexpense.Classes.ExpenseDBHelper;
import com.jk.admin.myexpense.Modal.Expense;
import com.jk.admin.myexpense.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FragmentAddExpense extends Fragment {

    TextView dateTv;
    EditText amountEt,descriptionEt;
    RadioGroup sourceRg;
    Button addBtn;
    Calendar myCalendar=Calendar.getInstance();
    ExpenseDBHelper expenseDBHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_expense_layout,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final String date_n = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        dateTv=(TextView) view.findViewById(R.id.DateTv);
        amountEt=(EditText) view.findViewById(R.id.AmountEt);
        descriptionEt=(EditText)view.findViewById(R.id.DescriptionEt);
        sourceRg=(RadioGroup) view.findViewById(R.id.FromRg);
        addBtn=(Button)view.findViewById(R.id.AddBtn);
        expenseDBHelper=new ExpenseDBHelper(getActivity());

        dateTv.setText(date_n);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {


            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }


        };

        dateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(),date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(!expenseDBHelper.IsBalEntry())
                {
                    Toast.makeText(getActivity(), "Please Enter Current Balance First.", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    if(amountEt.getText().toString().equals("")||amountEt.getText().toString().equals("0"))
                    {
                        amountEt.setError("Enter Amount.");
                    }
                    else if(descriptionEt.getText().toString().equals(""))
                    {
                        descriptionEt.setError("Enter Description.");
                    }
                    else {


                        Expense expense = new Expense();
                        expense.setDate((String) dateTv.getText());
                        expense.setAmount(amountEt.getText().toString());
                        expense.setDescription(descriptionEt.getText().toString());
                        expense.setFrom(sourceRg.getCheckedRadioButtonId() == R.id.CashRb ? "CA" : "BA");
                        if (expenseDBHelper.AddExpense(expense)) {
                            amountEt.setText("");
                            descriptionEt.setText("");
                            Toast.makeText(getActivity(), "Data Added.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Data Not Added.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }
        });
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        try {
//            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//            View view = getActivity().getCurrentFocus();
//            if (view != null) {
//                inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//            }
//
//        } catch (Exception e) {
//        }
//
//        }

        private void updateLabel() {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateTv.setText(sdf.format(myCalendar.getTime()));
    }
}
