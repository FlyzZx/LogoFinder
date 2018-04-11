package imt.logofinder.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import imt.logofinder.R;
import imt.logofinder.activity.ResultActivity;
import imt.logofinder.http.CustomRequest;
import imt.logofinder.http.HttpRequest;

/**
 * Created by 41000440 on 07/04/2018.
 */

public class AddTrainDialog extends DialogFragment {
    private ArrayList<String> classList;
    private Spinner spinner;
    private Button btnValid;
    private EditText edtClasse;

    OnTrainAddedListener listener = null;

    public interface OnTrainAddedListener {
        void onTrainAdded(String classe);
    }

    public void setOnTraiAddedListener(OnTrainAddedListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View layout = inflater.inflate(R.layout.layout_dialog_add_train,null);

        CustomRequest request = new CustomRequest();
        try {
            String classes = request.execute(ResultActivity.serverAddress, "8080", ResultActivity.pathToClasses).get();
            Gson jsonner = new Gson();
            classList = new ArrayList<>();
            classList = jsonner.fromJson(classes, classList.getClass());

            spinner = layout.findViewById(R.id.dialog_spinner_classes);
            SpinnerAdapter adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, classList);
            spinner.setAdapter(adapter);

            edtClasse = layout.findViewById(R.id.dialog_edt_classe);

            btnValid = layout.findViewById(R.id.dialog_btn_validClasse);
            btnValid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String newClass;
                    if(!edtClasse.getText().toString().equals("")) {
                        newClass = edtClasse.getText().toString();
                    } else {
                        newClass = (String) spinner.getSelectedItem();
                    }
                    newClass = newClass.substring(0, 1).toUpperCase() + newClass.substring(1).toLowerCase();
                    listener.onTrainAdded(newClass);
                }
            });

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        builder.setView(layout);

        final AlertDialog alertDialog = builder.create();

        return alertDialog;
    }
}
