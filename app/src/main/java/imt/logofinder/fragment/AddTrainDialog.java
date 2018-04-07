package imt.logofinder.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import imt.logofinder.R;
import imt.logofinder.http.CustomRequest;
import imt.logofinder.http.HttpRequest;

/**
 * Created by 41000440 on 07/04/2018.
 */

public class AddTrainDialog extends DialogFragment {

    private String serverAddress = "51.254.205.180";
    private String pathToClasses = "/classes";

    private ArrayList<String> classList;
    private Spinner spinner;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View layout = inflater.inflate(R.layout.layout_dialog_add_train,null);

        CustomRequest request = new CustomRequest();
        try {
            String classes = request.execute(serverAddress, "8080", pathToClasses).get();
            Gson jsonner = new Gson();
            classList = new ArrayList<>();
            classList = jsonner.fromJson(classes, classList.getClass());

            spinner = layout.findViewById(R.id.dialog_spinner_classes);
            SpinnerAdapter adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, classList);
            spinner.setAdapter(adapter);
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
